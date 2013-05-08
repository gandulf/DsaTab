/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.activity.menu;

import java.lang.ref.WeakReference;

import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.dsatab.activity.DsaTabActivity;

/**
 * @author Ganymede
 * 
 */
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
	 * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabSelected(com.
	 * actionbarsherlock.app.ActionBar.Tab,
	 * android.support.v4.app.FragmentTransaction)
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
	 * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabReselected(com.
	 * actionbarsherlock.app.ActionBar.Tab,
	 * android.support.v4.app.FragmentTransaction)
	 */
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// User selected the already selected tab. Usually do nothing.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.ActionBar.TabListener#onTabUnselected(com.
	 * actionbarsherlock.app.ActionBar.Tab,
	 * android.support.v4.app.FragmentTransaction)
	 */
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

}