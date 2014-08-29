package com.dsatab.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.fragment.BaseEditFragment;
import com.gandulf.guilib.util.Debug;

public class FragmentEditActivity extends BaseActivity {

	public static final String EDIT_FRAGMENT_CLASS = "editFragmentClass";

	private BaseEditFragment fragment;

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

		try {

			if (!getIntent().getExtras().containsKey(EDIT_FRAGMENT_CLASS)) {
				throw new IllegalArgumentException("Called FragmentEditActivit without EDIT_FRAGMENT_CLASS in intent: "
						+ getIntent());
			}

			fragment = (BaseEditFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);

			Class<? extends BaseEditFragment> fragmentClass = (Class<? extends BaseEditFragment>) getIntent()
					.getExtras().getSerializable(EDIT_FRAGMENT_CLASS);

			if (fragment != null && fragmentClass.isAssignableFrom(fragment.getClass())) {
				// all ok keep current fragment
			} else {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				if (fragment != null) {
					ft.remove(fragment);
				}

				fragment = fragmentClass.newInstance();
				fragment.setArguments(getIntent().getExtras());
				ft.add(android.R.id.content, fragment);
				ft.commit();

			}
		} catch (InstantiationException e) {
			Debug.error(e);
			setResult(RESULT_CANCELED);
			finish();
			return;
		} catch (IllegalAccessException e) {
			Debug.error(e);
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		if (getIntent() != null) {
			if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
				inflateDone();
			} else {
				inflateDoneDiscard();
			}
		} else {
			inflateDone();
		}
	}

	public void inflateDoneDiscard() {
		// Inflate a "Done/Discard" custom action bar view.
		LayoutInflater inflater = LayoutInflater.from(getSupportActionBar().getThemedContext());
		final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_discard, null);
		customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle data = fragment.accept();
				if (data != null) {
					Intent intent = new Intent();
					intent.putExtras(data);
					setResult(RESULT_OK, intent);
					finish();
				}
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

	public void inflateDone() {
		// Inflate a "Done/Discard" custom action bar view.
		LayoutInflater inflater = LayoutInflater.from(getSupportActionBar().getThemedContext());
		final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done, null);
		customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle data = fragment.accept();
				if (data != null) {
					Intent intent = new Intent();
					intent.putExtras(data);
					setResult(RESULT_OK, intent);
					finish();
				}
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
