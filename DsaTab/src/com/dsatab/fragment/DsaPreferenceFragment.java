package com.dsatab.fragment;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import com.dsatab.R;

public class DsaPreferenceFragment extends PreferenceFragment {

	public static final String TAG = "PreferencesFragment";

	public DsaPreferenceFragment() {

	}

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		addPreferencesFromResource(R.xml.preferences_hc_diceslider);
	}

}
