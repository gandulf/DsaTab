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
package com.dsatab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.view.FilterSettings;
import com.dsatab.view.FilterSettings.FilterType;
import com.dsatab.view.listener.FilterChangedListener;
import com.gandulf.guilib.util.Debug;

/**
 * @author Ganymede
 * 
 */
public class DualPaneFragment extends SherlockFragment implements FilterChangedListener,
		OnSharedPreferenceChangeListener {

	private static final String TABINFO = "TABINFO";

	private List<Fragment> fragments;

	private ViewGroup[] panes;

	private TabInfo tabInfo;

	public DualPaneFragment(TabInfo tabInfo) {
		fragments = new ArrayList<Fragment>();
		this.tabInfo = tabInfo;

		try {
			for (int i = 0; i < TabInfo.MAX_TABS_PER_PAGE; i++) {
				if (tabInfo.getActivityClazz(i) != null) {
					BaseFragment f = tabInfo.getFragment(i);
					fragments.add(f);
				} else {
					fragments.add(null);
				}
			}
		} catch (java.lang.InstantiationException e) {
			Debug.error(e);
		} catch (IllegalAccessException e) {
			Debug.error(e);
		}
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	public TabInfo getTabInfo() {
		return tabInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		for (Fragment f : fragments) {
			if (f != null)
				f.onActivityResult(requestCode, resultCode, data);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.
	 * actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
			com.actionbarsherlock.view.MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);

		for (Fragment f : fragments) {
			if (f instanceof SherlockFragment)
				((SherlockFragment) f).onCreateOptionsMenu(menu, inflater);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem
	 * )
	 */
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		boolean handled = false;

		for (Fragment f : fragments) {
			if (f instanceof SherlockFragment) {
				handled |= ((SherlockFragment) f).onOptionsItemSelected(item);
				if (handled)
					return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(TABINFO, tabInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onViewStateRestored(android.os.Bundle)
	 */
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null) {
			tabInfo = savedInstanceState.getParcelable(TABINFO);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.dual_pane_layout, container, false);

		panes = new ViewGroup[TabInfo.MAX_TABS_PER_PAGE];
		panes[0] = (ViewGroup) root.findViewById(R.id.pane_left);
		panes[1] = (ViewGroup) root.findViewById(R.id.pane_right);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onViewCreated(android.view.View,
	 * android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction transaction = null;

		for (int i = 0; i < TabInfo.MAX_TABS_PER_PAGE; i++) {
			Fragment oldFragment = fragmentManager.findFragmentById(panes[i].getId());
			Fragment newFragment = fragments.get(i);

			if (newFragment != null) {
				if (newFragment != oldFragment) {

					if (transaction == null)
						transaction = getChildFragmentManager().beginTransaction();

					transaction.replace(panes[i].getId(), newFragment);
				}
				panes[i].setVisibility(View.VISIBLE);
			} else {
				panes[i].setVisibility(View.GONE);
			}
		}
		if (transaction != null) {
			transaction.commit();
		}
		setUserVisibleHint(getUserVisibleHint());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setUserVisibleHint(getUserVisibleHint());
	}

	public void setMenuVisibility(boolean value) {
		super.setMenuVisibility(value);

		for (Fragment left : fragments) {
			if (left != null)
				left.setMenuVisibility(value);
		}

	}

	public void setUserVisibleHint(boolean value) {
		super.setUserVisibleHint(value);

		for (Fragment left : fragments) {
			try {
				if (left != null)
					left.setUserVisibleHint(value);
			} catch (NullPointerException e) {
				// TODO find a better way to handle exception
				// if we call this to soon on the fragment before its added a
				// npe
				// is thrown ignore this here.
			}
		}

	}

	public Fragment get(int i) {
		return fragments.get(i);
	}

	public void set(int i, Fragment f) {
		while (i >= fragments.size())
			fragments.add(null);
		fragments.set(i, f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		setUserVisibleHint(getUserVisibleHint());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.FilterChangedListener#onFilterChanged(com.dsatab
	 * .view.FilterSettings.FilterType, com.dsatab.view.FilterSettings)
	 */
	@Override
	public void onFilterChanged(FilterType type, FilterSettings settings) {
		for (Fragment fragment : fragments) {
			if (fragment instanceof BaseFragment) {
				((BaseFragment) fragment).onFilterChanged(type, settings);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener
	 * #onSharedPreferenceChanged(android.content.SharedPreferences,
	 * java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		for (Fragment left : fragments) {
			if (left instanceof BaseFragment)
				((BaseFragment) left).onSharedPreferenceChanged(sharedPreferences, key);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#toString()
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("DP[");
		for (Fragment left : fragments) {
			if (left != null)
				sb.append(left.toString());
		}
		sb.append("]");
		return sb.toString();
	}
}
