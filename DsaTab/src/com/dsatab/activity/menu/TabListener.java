package com.dsatab.activity.menu;

import java.lang.ref.WeakReference;

import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.dsatab.activity.DsaTabActivity;

public class TabListener implements ActionBar.TabListener {

	private final WeakReference<DsaTabActivity> mActivityRef;

	private final int tabIndex;

	/**
	 * Constructor used each time a new tab is created.
	 * 
	 * @param activity
	 *            The host Activity, used to instantiate the fragment
	 * @param tabIndex
	 *            The index of the tab to show
	 */
	public TabListener(DsaTabActivity activity, int tabIndex) {
		this.mActivityRef = new WeakReference<DsaTabActivity>(activity);
		this.tabIndex = tabIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabSelected(com. actionbarsherlock.app.ActionBar.Tab, android.support.v4.app.FragmentTransaction)
	 */
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		DsaTabActivity mainActivity = mActivityRef.get();
		if (mainActivity != null) {
			mainActivity.showTab(tabIndex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabReselected(com. actionbarsherlock.app.ActionBar.Tab,
	 * android.support.v4.app.FragmentTransaction)
	 */
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// User selected the already selected tab. Usually do nothing.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabUnselected(com. actionbarsherlock.app.ActionBar.Tab,
	 * android.support.v4.app.FragmentTransaction)
	 */
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

}