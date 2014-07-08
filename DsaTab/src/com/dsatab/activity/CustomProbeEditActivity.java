package com.dsatab.activity;

import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.CustomProbe;
import com.dsatab.fragment.CustomProbeEditFragment;

public class CustomProbeEditActivity extends BaseFragmentActivity {

	public static final String INTENT_PROBE_CHOOSER_ID = "com.dsatab.data.intent.customProbeId";

	private CustomProbeEditFragment fragment;

	public static void insert(Activity activity, int requestCode) {
		Intent intent = new Intent(activity, CustomProbeEditActivity.class);
		intent.setAction(Intent.ACTION_INSERT);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void edit(Activity activity, CustomProbe probe, int requestCode) {
		Intent intent = new Intent(activity, CustomProbeEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra(INTENT_PROBE_CHOOSER_ID, probe.getId().toString());
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
		setContentView(R.layout.main_custom_probe_edit);

		fragment = (CustomProbeEditFragment) getSupportFragmentManager().findFragmentById(
				R.id.fragment_custom_probe_edit);

		// Inflate a "Done/Discard" custom action bar view.
		LayoutInflater inflater = LayoutInflater.from(getSupportActionBar().getThemedContext());
		final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_discard, null);
		customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomProbe itemContainer = fragment.accept();
				if (Intent.ACTION_INSERT.equals(getIntent().getAction())) {
					DsaTabApplication.getInstance().getHero().getHeroConfiguration().addCustomProbe(itemContainer);
				}
				setResult(RESULT_OK);
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

		//

		CustomProbe probe = null;
		Bundle extra = getIntent().getExtras();
		if (extra != null && extra.containsKey(INTENT_PROBE_CHOOSER_ID)) {
			UUID containerId = UUID.fromString(extra.getString(INTENT_PROBE_CHOOSER_ID));
			if (containerId != null) {
				probe = DsaTabApplication.getInstance().getHero().getHeroConfiguration().getCustomProbe(containerId);
			}
		}
		fragment.setCustomProbe(probe);
	}

}
