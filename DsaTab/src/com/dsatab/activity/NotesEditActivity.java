package com.dsatab.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.notes.Connection;
import com.dsatab.data.notes.Event;
import com.dsatab.fragment.NotesEditFragment;

public class NotesEditActivity extends BaseFragmentActivity {

	private NotesEditFragment fragment;

	public static void edit(Event event, String audioPath, Activity activity, int requestCode) {
		Intent intent = new Intent(activity, NotesEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		if (event != null) {
			intent.putExtra(NotesEditFragment.INTENT_NOTES_ITEM, event);
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_TEXT, event.getComment());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_NAME, event.getName());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_CATEGORY, event.getCategory());
		}
		if (audioPath != null) {
			intent.putExtra(NotesEditFragment.INTENT_NAME_AUDIO_PATH, audioPath);
		}
		activity.startActivityForResult(intent, requestCode);
	}

	public static void edit(Connection event, Activity activity, int requestCode) {

		Intent intent = new Intent(activity, NotesEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		if (event != null) {
			intent.putExtra(NotesEditFragment.INTENT_NOTES_ITEM, event);
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_TEXT, event.getDescription());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_NAME, event.getName());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_SOZIALSTATUS, event.getSozialStatus());
			intent.putExtra(NotesEditFragment.INTENT_NAME_EVENT_CATEGORY, event.getCategory());
		}
		activity.startActivityForResult(intent, requestCode);
	}

	public static void insert(Activity activity, int requestCode) {
		Intent intent = new Intent(activity, NotesEditActivity.class);
		intent.setAction(Intent.ACTION_INSERT);
		activity.startActivityForResult(intent, requestCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_notes_edit);

		fragment = (NotesEditFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_notes_edit);

		// Inflate a "Done/Discard" custom action bar view.
		LayoutInflater inflater = LayoutInflater.from(this);
		final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_discard, null);
		customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtras(fragment.accept());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		customActionBarView.findViewById(R.id.actionbar_discard).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment.cancel();
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

}
