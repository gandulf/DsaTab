package com.dsatab.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.commonsware.cwac.merge.MergeAdapter;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.NotesEditActivity;
import com.dsatab.data.Connection;
import com.dsatab.data.Event;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.ConnectionAdapter;
import com.dsatab.data.adapter.EventAdapter;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.util.Debug;

public class NotesFragment extends BaseListFragment implements OnItemClickListener, OnMultiChoiceClickListener {

	public static final int ACTION_EDIT = 1;

	private MediaRecorder mediaRecorder;
	private MediaPlayer mediaPlayer;

	private File recordingsDir;

	private ListView listView;

	private MergeAdapter mergeAdapter;
	private EventAdapter notesListAdapter;
	private ConnectionAdapter connectionsAdapter;

	private Set<EventCategory> categoriesSelected;
	private EventCategory[] categories;

	private Object selectedObject = null;

	private final class NoteActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyNotesChanged = false;
			boolean notifyConnectionsChanged = false;

			notesListAdapter.setNotifyOnChange(false);
			connectionsAdapter.setNotifyOnChange(false);

			SparseBooleanArray checkedPositions = listView.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = mergeAdapter.getItem(checkedPositions.keyAt(i));
						if (obj instanceof Event) {
							Event event = (Event) obj;
							if (item.getItemId() == R.id.option_delete) {
								if (event.isDeletable()) {
									getHero().removeEvent(event);
									notesListAdapter.remove(event);
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
								getHero().removeConnection(connection);
								connectionsAdapter.remove(connection);
								notifyConnectionsChanged = true;
							} else if (item.getItemId() == R.id.option_edit) {
								editConnection(connection);
								mode.finish();
								break;
							}
						}
					}
				}
				if (notifyNotesChanged) {
					notesListAdapter.notifyDataSetChanged();
				}
				if (notifyConnectionsChanged) {
					connectionsAdapter.notifyDataSetChanged();
				}
			}
			notesListAdapter.setNotifyOnChange(true);
			connectionsAdapter.setNotifyOnChange(true);
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
			mergeAdapter.notifyDataSetChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
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
						Object obj = mergeAdapter.getItem(checkedPositions.keyAt(i));
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
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com. actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
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
					notesListAdapter.filter(null, new ArrayList<EventCategory>(categoriesSelected));
					connectionsAdapter.filter(null, new ArrayList<EventCategory>(categoriesSelected));
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
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
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

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemLongClickListener(this);
		listView.setOnItemClickListener(this);
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
		notesListAdapter = new EventAdapter(getActivity(), getHero().getEvents());

		connectionsAdapter = new ConnectionAdapter(getActivity(), getHero().getConnections());

		mergeAdapter = new MergeAdapter();
		mergeAdapter.addAdapter(notesListAdapter);
		mergeAdapter.addAdapter(connectionsAdapter);

		listView.setAdapter(mergeAdapter);
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
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View, int, long)
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
						mediaRecorder.stop();
						mediaRecorder.reset();
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
						mediaRecorder.stop();
						mediaRecorder.reset();
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
	 * @see android.content.DialogInterface.OnMultiChoiceClickListener#onClick(android .content.DialogInterface, int, boolean)
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
					notesListAdapter.remove((Event) selectedObject);
					selectedObject = null;
					notesListAdapter.notifyDataSetChanged();
				}

				if (selectedObject instanceof Connection) {
					Connection selectedEvent = (Connection) selectedObject;
					selectedEvent.setDescription(comment.trim());
					selectedEvent.setName(name);
					selectedEvent.setSozialStatus(sozialstatus);
				} else if (selectedObject == null) {
					Connection connection = new Connection();
					connection.setName(name);
					connection.setDescription(comment);
					connection.setSozialStatus(sozialstatus);
					getHero().addConnection(connection);
					connectionsAdapter.add(connection);
				}

				connectionsAdapter.sort(Connection.NAME_COMPARATOR);
				connectionsAdapter.refilter();

			} else {
				if (selectedObject instanceof Connection) {
					getHero().removeConnection((Connection) selectedObject);
					connectionsAdapter.remove((Connection) selectedObject);
					selectedObject = null;
				}

				if (selectedObject instanceof Event) {
					Event selectedEvent = (Event) selectedObject;
					selectedEvent.setName(name);
					selectedEvent.setComment(comment);
					selectedEvent.setAudioPath(audioPath);
					selectedEvent.setCategory(category);
				} else if (selectedObject == null) {
					Event selectedEvent = new Event();
					selectedEvent.setName(name);
					selectedEvent.setCategory(category);
					selectedEvent.setComment(comment);
					selectedEvent.setAudioPath(audioPath);
					getHero().addEvent(selectedEvent);
					notesListAdapter.add(selectedEvent);
				}

				notesListAdapter.sort(Event.COMPARATOR);
				notesListAdapter.refilter();
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onEventChanged(Event e) {
		((ArrayAdapter<?>) listView.getAdapter()).notifyDataSetChanged();
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
