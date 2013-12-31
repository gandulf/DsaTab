package com.dsatab.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.NotesEditActivity;
import com.dsatab.data.Connection;
import com.dsatab.data.Event;
import com.dsatab.data.Hero;
import com.dsatab.data.NotesItem;
import com.dsatab.data.adapter.NotesAdapter;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.haarman.listviewanimations.itemmanipulation.AnimateAdapter;
import com.haarman.listviewanimations.itemmanipulation.OnAnimateCallback;
import com.haarman.listviewanimations.view.DynamicListView;

public class NotesFragment extends BaseListFragment implements OnItemClickListener, OnMultiChoiceClickListener,
		OnAnimateCallback {

	public static final int ACTION_EDIT = 1;

	private MediaRecorder mediaRecorder;
	private MediaPlayer mediaPlayer;

	private File recordingsDir;

	private DynamicListView listView;

	private NotesAdapter notesAdapter;
	private AnimateAdapter<NotesItem> animateNotesAdapter;

	private Set<EventCategory> categoriesSelected;
	private EventCategory[] categories;

	private Object selectedObject = null;

	private final class NoteActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyNotesChanged = false;

			notesAdapter.setNotifyOnChange(false);

			SparseBooleanArray checkedPositions = listView.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = notesAdapter.getItem(checkedPositions.keyAt(i));
						if (obj instanceof Event) {
							Event event = (Event) obj;
							if (item.getItemId() == R.id.option_delete) {
								if (event.isDeletable()) {
									animateNotesAdapter.animateDismiss(checkedPositions.keyAt(i));
									notifyNotesChanged = true;
								}
							} else if (item.getItemId() == R.id.option_edit) {
								editEvent(event);
								mode.finish();
								break;
							}
						} else if (obj instanceof Connection) {
							Connection connection = (Connection) obj;
							if (item.getItemId() == R.id.option_delete) {
								animateNotesAdapter.animateDismiss(checkedPositions.keyAt(i));
								notifyNotesChanged = true;
							} else if (item.getItemId() == R.id.option_edit) {
								editConnection(connection);
								mode.finish();
								break;
							}
						}
					}
				}
				if (notifyNotesChanged) {
					notesAdapter.notifyDataSetChanged();
				}

			}
			notesAdapter.setNotifyOnChange(true);
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.note_list_popupmenu, menu);
			mode.setTitle("Notizen");
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			listView.clearChoices();
			notesAdapter.notifyDataSetChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode
		 * (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			SparseBooleanArray checkedPositions = listView.getCheckedItemPositions();

			com.actionbarsherlock.view.MenuItem view = menu.findItem(R.id.option_delete);
			int selected = 0;
			boolean allDeletable = true;
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = notesAdapter.getItem(checkedPositions.keyAt(i));
						selected++;
						if (obj instanceof Event) {
							Event event = (Event) obj;
							allDeletable &= event.isDeletable();
						}
					}
				}
			}

			if (allDeletable != view.isEnabled()) {
				view.setEnabled(allDeletable);
				return true;
			}

			mode.setSubtitle(selected + " ausgewählt");

			notesAdapter.notifyDataSetChanged();

			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mCallback = new NoteActionMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com. actionbarsherlock.view.Menu,
	 * com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.note_list_menu, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com. actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == R.id.option_note_add) {
			editEvent(null, null);
			return true;
		} else if (item.getItemId() == R.id.option_note_record) {
			recordEvent();
			return true;
		} else if (item.getItemId() == R.id.option_note_filter) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			String[] categoryNames = new String[categories.length];
			boolean[] categoriesSet = new boolean[categories.length];

			for (int i = 0; i < categories.length; i++) {
				categoryNames[i] = categories[i].name();
				if (categoriesSelected.contains(categories[i]))
					categoriesSet[i] = true;
			}

			builder.setMultiChoiceItems(categoryNames, categoriesSet, this);
			builder.setTitle("Filtern");
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					notesAdapter.filter(null, new ArrayList<EventCategory>(categoriesSelected));
				}
			});
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return configureContainerView(inflater.inflate(R.layout.sheet_notes, container, false));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		recordingsDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_RECORDINGS);

		listView = (DynamicListView) findViewById(android.R.id.list);
		listView.setOnItemLongClickListener(this);
		listView.setOnItemClickListener(this);
		listView.setOnItemCheckedListener(this);
		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

		categories = EventCategory.values();
		categoriesSelected = new HashSet<EventCategory>(Arrays.asList(categories));
		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {
		List<NotesItem> notes = new ArrayList<NotesItem>();
		notes.addAll(getHero().getEvents());
		notes.addAll(getHero().getConnections());

		notesAdapter = new NotesAdapter(getActivity(), notes);
		animateNotesAdapter = new AnimateAdapter<NotesItem>(notesAdapter, this);
		animateNotesAdapter.setAbsListView(listView);
		listView.setAdapter(animateNotesAdapter);
	}

	@Override
	public void onShow(AbsListView listView, int[] reverseSortedPositions) {

	}

	@Override
	public void onDismiss(AbsListView list, int[] positions) {
		for (int pos : positions) {
			NotesItem item = notesAdapter.getItem(pos);
			notesAdapter.remove(item);
			if (item instanceof Event)
				getHero().removeEvent((Event) item);
			else if (item instanceof Connection)
				getHero().removeConnection((Connection) item);
		}
	}

	/**
	 * 
	 */
	private void initMediaPlayer() {
		// init player
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	private void initMediaRecorder() {
		// init recorder
		mediaRecorder = new MediaRecorder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View,
	 * int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if (mMode == null) {
			Object obj = listView.getItemAtPosition(position);
			if (obj instanceof Event) {
				Event event = (Event) obj;

				if (event.getAudioPath() != null) {
					try {
						if (mediaPlayer == null)
							initMediaPlayer();

						mediaPlayer.setDataSource(event.getAudioPath());
						mediaPlayer.prepare();
						mediaPlayer.start();
						mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								mp.stop();
								mp.reset();
							}
						});
					} catch (IllegalArgumentException e) {
						Debug.error(e);
					} catch (IllegalStateException e) {
						Debug.error(e);
					} catch (IOException e) {
						Debug.error(e);
					}

				}
			}
			listView.setItemChecked(position, false);
		} else {
			super.onItemClick(parent, view, position, id);
		}
	}

	private void recordEvent() {
		try {
			final File currentAudio = new File(recordingsDir, "last.3gp");

			if (mediaRecorder == null)
				initMediaRecorder();

			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mediaRecorder.setOutputFile(currentAudio.getAbsolutePath());
			mediaRecorder.prepare();
			mediaRecorder.start(); // Recording is now started

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.recording);
			builder.setMessage(R.string.recording_message);
			builder.setPositiveButton(R.string.label_save, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (mediaRecorder != null) {
						try {
							mediaRecorder.stop();
						} catch (IllegalStateException e) {
							Debug.warning("Couldn't stop mediaRecorder something went wrong.", e);
						} finally {
							mediaRecorder.reset();
						}
					}

					File nowAudio = new File(recordingsDir, System.currentTimeMillis() + ".3gp");
					currentAudio.renameTo(nowAudio);

					editEvent(null, nowAudio.getAbsolutePath());
				}
			});

			builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (mediaRecorder != null) {
						try {
							mediaRecorder.stop();
						} catch (IllegalStateException e) {
							Debug.warning("Couldn't stop mediaRecorder something went wrong.", e);
						} finally {
							mediaRecorder.reset();
						}
					}
					currentAudio.delete();
				}
			});

			builder.show();
		} catch (IllegalStateException e) {
			Debug.error(e);
		} catch (IOException e) {
			Debug.error(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.DialogInterface.OnMultiChoiceClickListener#onClick(android .content.DialogInterface, int,
	 * boolean)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		if (isChecked)
			categoriesSelected.add(categories[which]);
		else
			categoriesSelected.remove(categories[which]);
	}

	private void editEvent(final Event event) {
		editEvent(event, event.getAudioPath());
	}

	private void editEvent(final Event event, final String audioPath) {

		selectedObject = event;

		Intent intent = new Intent(getActivity(), NotesEditActivity.class);
		if (event != null) {
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_TEXT, event.getComment());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_NAME, event.getName());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_CATEGORY, event.getCategory());
		}
		if (audioPath != null) {
			intent.putExtra(NotesEditFragment.INTENT_NAME_AUDIO_PATH, audioPath);
		}
		getActivity().startActivityForResult(intent, ACTION_EDIT);

	}

	private void editConnection(final Connection event) {
		selectedObject = event;
		Intent intent = new Intent(getActivity(), NotesEditActivity.class);
		if (event != null) {
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_TEXT, event.getDescription());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_NAME, event.getName());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_SOZIALSTATUS, event.getSozialStatus());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_CATEGORY, event.getCategory());
		}
		getActivity().startActivityForResult(intent, ACTION_EDIT);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ACTION_EDIT && resultCode == Activity.RESULT_OK) {

			String comment = data.getStringExtra(NotesEditFragment.INTENT_NAME_EVENT_TEXT);
			String name = data.getStringExtra(NotesEditFragment.INTENT_NAME_EVENT_NAME);
			String sozialstatus = data.getStringExtra(NotesEditFragment.INTENT_NAME_EVENT_SOZIALSTATUS);
			String audioPath = data.getStringExtra(NotesEditFragment.INTENT_NAME_AUDIO_PATH);
			EventCategory category = (EventCategory) data
					.getSerializableExtra(NotesEditFragment.INTENT_NAME_EVENT_CATEGORY);

			if (category == EventCategory.Bekanntschaft) {
				if (selectedObject instanceof Event) {
					getHero().removeEvent((Event) selectedObject);
					notesAdapter.remove((Event) selectedObject);
					selectedObject = null;
					animateNotesAdapter.notifyDataSetChanged();
				}

				if (selectedObject instanceof Connection) {
					Connection selectedEvent = (Connection) selectedObject;
					selectedEvent.setDescription(comment.trim());
					selectedEvent.setName(name);
					selectedEvent.setSozialStatus(sozialstatus);
					animateNotesAdapter.notifyDataSetChanged();
				} else if (selectedObject == null) {
					Connection connection = new Connection();
					connection.setName(name);
					connection.setDescription(comment);
					connection.setSozialStatus(sozialstatus);
					getHero().addConnection(connection);
					animateNotesAdapter.animateShow(notesAdapter.getCount());
					notesAdapter.add(connection);
				}

				// notesAdapter.sort(NotesItem.COMPARATOR);
				// notesAdapter.refilter();

			} else {
				if (selectedObject instanceof Connection) {
					getHero().removeConnection((Connection) selectedObject);
					notesAdapter.remove((Connection) selectedObject);
					selectedObject = null;
				}

				if (selectedObject instanceof Event) {
					Event selectedEvent = (Event) selectedObject;
					selectedEvent.setName(name);
					selectedEvent.setComment(comment);
					selectedEvent.setAudioPath(audioPath);
					selectedEvent.setCategory(category);

					animateNotesAdapter.notifyDataSetChanged();
				} else if (selectedObject == null) {
					Event selectedEvent = new Event();
					selectedEvent.setName(name);
					selectedEvent.setCategory(category);
					selectedEvent.setComment(comment);
					selectedEvent.setAudioPath(audioPath);
					getHero().addEvent(selectedEvent);

					animateNotesAdapter.animateShow(notesAdapter.getCount());
					notesAdapter.add(selectedEvent);
				}

				notesAdapter.refilter();
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onEventChanged(Event e) {
		Util.notifyDatasetChanged(listView);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {

		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}

		super.onPause();
	}
}
