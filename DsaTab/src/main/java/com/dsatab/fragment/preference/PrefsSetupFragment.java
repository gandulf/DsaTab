package com.dsatab.fragment.preference;

import android.os.Bundle;

import com.dsatab.R;

/**
 * Created by Ganymedes on 25.10.2015.
 */
public class PrefsSetupFragment extends BasePreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.activity.DsaTabPreferenceActivity.BasePreferenceFragment #getPreferenceResourceId()
     */
    @Override
    public int getPreferenceResourceId() {
        return R.xml.preferences_hc_setup;
    }
}