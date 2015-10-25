package com.dsatab.fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.activity.BaseEditActivity;
import com.dsatab.data.adapter.EventCatgoryAdapter;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.notes.Connection;
import com.dsatab.data.notes.Event;
import com.dsatab.data.notes.NotesItem;
import com.dsatab.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotesEditFragment extends BaseEditFragment implements OnItemSelectedListener {

	public static void insert(Activity activity, int requestCode) {
		Intent intent = new Intent(activity, BaseEditActivity.class);
		intent.setAction(Intent.ACTION_INSERT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, NotesEditFragment.class);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void edit(Connection event, Activity activity, int requestCode) {

		Intent intent = new Intent(activity, BaseEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, NotesEditFragment.class);
		if (event != null) {
			intent.putExtra(INTENT_NOTES_ITEM, event);
			intent.putExtra(INTENT_NAME_EVENT_TEXT, event.getDescription());
			intent.putExtra(INTENT_NAME_EVENT_NAME, event.getName());
			intent.putExtra(INTENT_NAME_EVENT_SOZIALSTATUS, event.getSozialStatus());
			intent.putExtra(INTENT_NAME_EVENT_CATEGORY, event.getCategory());
		}
		activity.startActivityForResult(intent, requestCode);
	}

	public static void edit(Event event, String audioPath, Activity activity, int requestCode) {
		Intent intent = new Intent(activity, BaseEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, NotesEditFragment.class);
		if (event != null) {
			intent.putExtra(INTENT_NOTES_ITEM, event);
			intent.putExtra(INTENT_NAME_EVENT_TEXT, event.getComment());
			intent.putExtra(INTENT_NAME_EVENT_NAME, event.getName());
			intent.putExtra(INTENT_NAME_EVENT_CATEGORY, event.getCategory());
		}
		if (audioPath != null) {
			intent.putExtra(INTENT_NAME_AUDIO_PATH, audioPath);
		}
		activity.startActivityForResult(intent, requestCode);
	}

	public static final String INTENT_NOTES_ITEM = "notesItem";

	public static final String INTENT_NAME_EVENT_CATEGORY = "eventCategory";
	public static final String INTENT_NAME_EVENT_TEXT = "eventText";
	public static final String INTENT_NAME_EVENT_NAME = "eventNAme";
	public static final String INTENT_NAME_EVENT_SOZIALSTATUS = "eventSo";

	public static final String INTENT_NAME_AUDIO_PATH = "audioPath";

	private EventCatgoryAdapter categoryAdapter;

	private TextView categoryLabel;
	private EditText editComment;
	private EditText editName;
	private EditText editSozialStatus;

	private Spinner categorySpn;

	private EventCategory category;

	private String audioPath;

	private NotesItem notesItem;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_edit_notes, container, false));

		editComment = (EditText) root.findViewById(R.id.popup_notes_edit_text);
		editName = (EditText) root.findViewById(R.id.popup_notes_edit_name);
		editSozialStatus = (EditText) root.findViewById(R.id.popup_notes_edit_so);

		categorySpn = (Spinner) root.findViewById(R.id.popup_notes_spn_category);

		categoryLabel = (TextView) root.findViewById(R.id.popup_notes_spn_category_label);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		List<EventCategory> categories = new ArrayList<EventCategory>(Arrays.asList(EventCategory.values()));
		categories.remove(EventCategory.Heldensoftware);
		categoryAdapter = new EventCatgoryAdapter(getActivity(), android.R.layout.simple_spinner_item, categories);
		categorySpn.setAdapter(categoryAdapter);
		categorySpn.setOnItemSelectedListener(this);

		Bundle extra = getActivity().getIntent().getExtras();
		if (extra != null) {
			category = (EventCategory) extra.getSerializable(INTENT_NAME_EVENT_CATEGORY);
			if (category == null)
				category = EventCategory.Misc;

			notesItem = (NotesItem) extra.getSerializable(INTENT_NOTES_ITEM);

			String event = extra.getString(INTENT_NAME_EVENT_TEXT);
			String name = extra.getString(INTENT_NAME_EVENT_NAME);
			int sozial = extra.getInt(INTENT_NAME_EVENT_SOZIALSTATUS, 1);
			audioPath = extra.getString(INTENT_NAME_AUDIO_PATH);

			if (notesItem != null) {
				event = notesItem.getComment();
				name = notesItem.getName();
				category = notesItem.getCategory();

				if (notesItem instanceof Connection) {
					Connection connection = (Connection) notesItem;
					sozial = connection.getSozialStatus();
				} else if (notesItem instanceof Event) {
					Event eventItem = (Event) notesItem;
					audioPath = eventItem.getAudioPath();
				}
			}

			if (category == EventCategory.Heldensoftware) {
				categorySpn.setVisibility(View.GONE);
				categoryLabel.setVisibility(View.GONE);
			} else {
				categorySpn.setSelection(categoryAdapter.getPosition(category));
			}

			editComment.setText(event);
			editName.setText(name);
			editSozialStatus.setText(String.valueOf(sozial));
		}

		updateView();

		super.onActivityCreated(savedInstanceState);
	}

	private void updateView() {
		if (category != null) {

			if (category == EventCategory.Bekanntschaft) {
				editSozialStatus.setVisibility(View.VISIBLE);

			} else {
				editSozialStatus.setVisibility(View.GONE);

			}

			if (category.hasName()) {
				editName.setVisibility(View.VISIBLE);

			} else {
				editName.setVisibility(View.GONE);

			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View,
	 * int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		if (parent.getId() == R.id.popup_notes_spn_category) {
			category = (EventCategory) categorySpn.getSelectedItem();
			updateView();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		category = null;
		updateView();
	}

	/**
	 * 
	 */
	public void cancel() {

	}

	/**
	 * 
	 */
	public Bundle accept() {
		Util.hideKeyboard(editComment);

		String descrition = editComment.getText().toString();
		String name = editName.getText().toString();
		int so = Util.parseInt(editSozialStatus.getText().toString(), 1);

		Bundle data = new Bundle(5);

		data.putString(INTENT_NAME_EVENT_TEXT, descrition);
		data.putString(INTENT_NAME_EVENT_NAME, name);
		data.putInt(INTENT_NAME_EVENT_SOZIALSTATUS, so);
		data.putSerializable(INTENT_NAME_EVENT_CATEGORY, category);
		data.putString(INTENT_NAME_AUDIO_PATH, audioPath);

		if (getHero() != null) {
			if (category == EventCategory.Bekanntschaft) {
				if (notesItem instanceof Event) {
					getHero().removeEvent((Event) notesItem);
				} else if (notesItem instanceof Connection) {
					Connection selectedEvent = (Connection) notesItem;

					// we have to fetch the original object from heroes, since we are working with a serialized copy of
					// it
					// here
					for (Connection c : getHero().getConnections()) {
						if (selectedEvent.equals(c)) {
							selectedEvent = c;
						}
					}

					selectedEvent.setDescription(descrition.trim());
					selectedEvent.setName(name);
					selectedEvent.setSozialStatus(so);
					// TODO notify event changed
				} else if (notesItem == null) {
					Connection connection = new Connection();
					connection.setName(name);
					connection.setDescription(descrition);
					connection.setSozialStatus(so);
					getHero().addConnection(connection);
				}
			} else {
				if (notesItem instanceof Connection) {
					getHero().removeConnection((Connection) notesItem);
				} else if (notesItem instanceof Event) {
					Event selectedEvent = (Event) notesItem;
					// we have to fetch the original object from heroes, since we are working with a serialized copy of
					// it
					// here
					for (Event c : getHero().getEvents()) {
						if (selectedEvent.equals(c)) {
							selectedEvent = c;
						}
					}

					selectedEvent.setName(name);
					selectedEvent.setComment(descrition);
					selectedEvent.setAudioPath(audioPath);
					selectedEvent.setCategory(category);

					// TODO notify event changed
				} else if (notesItem == null) {
					Event selectedEvent = new Event();
					selectedEvent.setName(name);
					selectedEvent.setCategory(category);
					selectedEvent.setComment(descrition);
					selectedEvent.setAudioPath(audioPath);
					getHero().addEvent(selectedEvent);
				}
			}
		}

		return data;

	}

}
