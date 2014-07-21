package com.dsatab.data.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.dsatab.TabInfo;
import com.dsatab.data.HeroConfiguration;
import com.dsatab.fragment.DualPaneFragment;
import com.dsatab.fragment.EmptyFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

	private List<TabInfo> tabInfos;
	private List<Fragment> mPageReferenceMap;

	/**
	 * 
	 */
	public TabPagerAdapter(FragmentManager fm, HeroConfiguration configuration) {
		super(fm);

		tabInfos = new ArrayList<TabInfo>();
		mPageReferenceMap = new ArrayList<Fragment>();
		if (configuration != null) {
			for (TabInfo tabInfo : configuration.getTabs()) {
				tabInfos.add(tabInfo.clone());
				mPageReferenceMap.add(null);
			}
		}
	}

	public void setHeroConfiguration(FragmentManager fragmentManager, HeroConfiguration configuration) {

		FragmentTransaction ft = fragmentManager.beginTransaction();
		for (Fragment fragment : mPageReferenceMap) {
			if (fragment != null) {
				ft.remove(fragment);
			}
		}
		ft.commitAllowingStateLoss();

		tabInfos.clear();
		mPageReferenceMap.clear();
		if (configuration != null) {
			for (TabInfo tabInfo : configuration.getTabs()) {
				tabInfos.add(tabInfo.clone());
				mPageReferenceMap.add(null);
			}
		}
		notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentStatePagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int pos) {
		Fragment f = null;
		if (pos < mPageReferenceMap.size()) {
			f = mPageReferenceMap.get(pos);
		}
		if (f == null) {

			TabInfo tabInfo = tabInfos.get(pos);
			if (tabInfo.getTabCount() == 0) {
				f = new EmptyFragment();
			} else if (tabInfo.getTabCount() == 1) {
				f = tabInfo.getFragment();
			} else {
				f = DualPaneFragment.makeInstance(tabInfos.get(pos));
			}
			ensurePageSize(pos);
			mPageReferenceMap.set(pos, f);

		}
		return f;
	}

	private void ensurePageSize(int index) {
		while (mPageReferenceMap.size() <= index) {
			mPageReferenceMap.add(null);
		}
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

	public Fragment getFragment(int index) {
		if (mPageReferenceMap.size() > index)
			return mPageReferenceMap.get(index);
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentStatePagerAdapter#destroyItem(android. view.ViewGroup, int, java.lang.Object)
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		ensurePageSize(position);
		mPageReferenceMap.set(position, null);
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
		return mPageReferenceMap;
	}

}
