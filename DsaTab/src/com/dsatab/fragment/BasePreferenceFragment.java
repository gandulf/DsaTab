package com.dsatab.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.AppCompatPreferenceActivity;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.util.Debug;

public abstract class BasePreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	public abstract int getPreferenceResourceId();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceFragment#onPreferenceTreeClick(android .preference.PreferenceScreen,
	 * android.preference.Preference)
	 */
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (!TextUtils.isEmpty(preference.getFragment())) {
			try {
				BasePreferenceFragment fragment = (BasePreferenceFragment) Class.forName(preference.getFragment())
						.newInstance();
				((DsaTabPreferenceActivity) getActivity()).startPreferenceFragment(fragment, true);
				return true;
			} catch (Exception e) {
				Debug.error(e);
			}
			return false;
		} else {
			return DsaTabPreferenceActivity.handlePreferenceClick((DsaTabPreferenceActivity)getActivity(), preference, preference.getKey(),
					PreferenceManager.getDefaultSharedPreferences(getActivity()));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTheme(DsaTabApplication.getInstance().getCustomPreferencesTheme());

		//getPreferenceManager().setSharedPreferencesName("preferences");
		//getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

		addPreferencesFromResource(getPreferenceResourceId());

		DsaTabPreferenceActivity.initPreferences(getPreferenceManager(), getPreferenceScreen());

		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++)
			initSummary(getPreferenceScreen().getPreference(i));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.main_preferences, container, false);
		if (layout != null) {
			ListView list = (ListView) layout.findViewById(android.R.id.list);
			list.setPadding(0, 0, 0, 0);

			AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();
			Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
			activity.setSupportActionBar(toolbar);

			ActionBar bar = activity.getSupportActionBar();
			bar.setHomeButtonEnabled(true);
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayShowTitleEnabled(true);
			bar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
			bar.setTitle(getPreferenceScreen().getTitle());
		}
		return layout;
	}

	private void initSummary(Preference pref) {
		if (pref instanceof PreferenceScreen) {
			final PreferenceScreen screen = (PreferenceScreen) pref;
			for (int i = 0; i < screen.getPreferenceCount(); i++)
				initSummary(screen.getPreference(i));
		}
		else if (pref instanceof PreferenceCategory) {
			final PreferenceCategory category = (PreferenceCategory) pref;
			for (int i = 0; i < category.getPreferenceCount(); i++)
				initSummary(category.getPreference(i));
		}
		else
			updatePrefSummary(pref);
	}

	private void updatePrefSummary(Preference pref) {
		if (pref instanceof ListPreference) {
			final ListPreference list = (ListPreference) pref;
			pref.setSummary(list.getEntry());
		}
		else if (pref instanceof EditTextPreference) {
			final EditTextPreference edit = (EditTextPreference) pref;
			if (!pref.getKey().equalsIgnoreCase("editKey"))
				pref.setSummary(edit.getText());
		}
	}

	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		DsaTabPreferenceActivity.handlePreferenceChange(findPreference(key), sharedPreferences, key);
		final Preference preference = findPreference(key);
		if (preference instanceof ListPreference)
			preference.setSummary(((ListPreference) preference).getEntry());
	}
}