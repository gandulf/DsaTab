package com.dsatab.data.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.dsatab.TabInfo;
import com.dsatab.data.HeroConfiguration;
import com.dsatab.fragment.DualPaneFragment;
import com.dsatab.fragment.EmptyFragment;
import com.gandulf.guilib.util.Debug;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

	private List<TabInfo> tabInfos;

	private Map<Integer, Fragment> mPageReferenceMap;

	/**
	 * 
	 */
	public TabPagerAdapter(FragmentManager fm, HeroConfiguration configuration) {
		super(fm);

		tabInfos = new ArrayList<TabInfo>();
		mPageReferenceMap = new HashMap<Integer, Fragment>();

		for (TabInfo tabInfo : configuration.getTabs()) {
			tabInfos.add(tabInfo.clone());
		}
	}

	public void setHeroConfiguration(FragmentManager fragmentManager, HeroConfiguration configuration) {

		FragmentTransaction ft = fragmentManager.beginTransaction();
		for (Fragment fragment : mPageReferenceMap.values()) {
			ft.remove(fragment);
		}
		ft.commitAllowingStateLoss();

		tabInfos.clear();
		for (TabInfo tabInfo : configuration.getTabs()) {
			tabInfos.add(tabInfo.clone());
		}
		mPageReferenceMap.clear();
		notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentStatePagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int pos) {
		Fragment f = mPageReferenceMap.get(pos);
		if (f == null) {
			try {
				TabInfo tabInfo = tabInfos.get(pos);
				if (tabInfo.getTabCount() == 0) {
					f = new EmptyFragment();
				} else if (tabInfo.getTabCount() == 1) {
					f = tabInfo.getFragment(0);
				} else {
					f = new DualPaneFragment(tabInfos.get(pos));
				}

				mPageReferenceMap.put(pos, f);
			} catch (InstantiationException e) {
				Debug.error(e);
			} catch (IllegalAccessException e) {
				Debug.error(e);
			}
		}
		return f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getItemPosition(java.lang.Object)
	 */
	@Override
	public int getItemPosition(Object object) {

		if (object instanceof DualPaneFragment) {
			DualPaneFragment fragment = (DualPaneFragment) object;

			if (tabInfos.contains(fragment.getTabInfo())) {
				return POSITION_UNCHANGED;
			}
		}

		return POSITION_NONE;
	}

	public Fragment getFragment(int key) {
		return mPageReferenceMap.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentStatePagerAdapter#destroyItem(android. view.ViewGroup, int, java.lang.Object)
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		mPageReferenceMap.remove(position);
		super.destroyItem(container, position, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return tabInfos.size();
	}

	/**
	 * @return
	 */
	public Collection<Fragment> getFragments() {
		return mPageReferenceMap.values();
	}

}
