package com.dsatab.fragment;

import net.saik0.android.unifiedpreference.UnifiedPreferenceFragment;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.util.Debug;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class BasePreferenceFragment extends UnifiedPreferenceFragment implements
		OnSharedPreferenceChangeListener {

	/**
	 * 
	 */
	public BasePreferenceFragment() {
		// always make sure the res arg is set
		if (getArguments() == null) {
			Bundle bundle = new Bundle();
			bundle.putInt(ARG_PREFERENCE_RES, getPreferenceResourceId());
			setArguments(bundle);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceFragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		// initPreferences(getPreferenceManager(), getPreferenceScreen());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DsaTabPreferenceActivity.initPreferences(getPreferenceManager(), getPreferenceScreen());
		onBindPreferenceSummariesToValues();

		SharedPreferences preferences = DsaTabApplication.getPreferences();
		preferences.registerOnSharedPreferenceChangeListener(this);

	}

	public abstract int getPreferenceResourceId();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceFragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		SharedPreferences preferences = DsaTabApplication.getPreferences();
		preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener
	 * #onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		DsaTabPreferenceActivity.handlePreferenceChange(findPreference(key), sharedPreferences, key);
	}

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
				Bundle args = new Bundle();
				args.putInt(UnifiedPreferenceFragment.ARG_PREFERENCE_RES, fragment.getPreferenceResourceId());
				fragment.setArguments(args);
				((DsaTabPreferenceActivity) getActivity()).startPreferenceFragment(fragment, true);
				return true;
			} catch (Exception e) {
				Debug.error(e);
			}
			return false;
		} else {
			return DsaTabPreferenceActivity.handlePreferenceClick(getActivity(), preference, preference.getKey(),
					PreferenceManager.getDefaultSharedPreferences(getActivity()));
		}
	}
}