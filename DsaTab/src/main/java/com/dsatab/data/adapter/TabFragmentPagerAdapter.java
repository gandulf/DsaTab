package com.dsatab.data.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.AdapterView;

import com.dsatab.config.TabInfo;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.fragment.EmptyFragment;

import java.util.ArrayList;
import java.util.List;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<TabInfoPage> tabInfoPages;
    private List<TabInfo> tabInfos;

    private class TabInfoPage {
        private TabInfo tabInfo;
        private int index;

        public TabInfoPage(TabInfo info, int index) {
            this.tabInfo = info;
            this.index = index;
        }

        public float getPageWidth() {
            if (tabInfo != null)
                return 1.0f / tabInfo.getTabCount();
            else
                return 1.0f;
        }

        public Class<? extends BaseFragment> getActivityClazz() {
            if (tabInfo != null)
                return tabInfo.getActivityClazz(index);
            else
                return EmptyFragment.class;
        }

        public long getId() {
            if (tabInfo != null) {
                int hash = tabInfo.getId().hashCode();
                String itemId = hash + "" + index;
                return Long.parseLong(itemId);
            } else {
                return AdapterView.INVALID_ROW_ID;
            }
        }
    }

    public TabFragmentPagerAdapter(android.support.v4.app.FragmentManager fm, List<TabInfo> tabs) {
        super(fm);

        tabInfoPages = new ArrayList<>();
        if (tabs != null)
            tabInfos = new ArrayList<>(tabs);
        else {
            tabInfos = new ArrayList<>();

        }
        for (TabInfo info : tabInfos) {
            for (int i = 0; i < info.getTabCount(); i++) {
                tabInfoPages.add(new TabInfoPage(info, i));
            }
        }
    }

    @Override
    public float getPageWidth(int position) {
        TabInfoPage page = getTabInfoPage(position);
        if (page != null)
            return page.getPageWidth();
        else
            return 1.0f;
    }

    @Override
    public Fragment getItem(int position) {
        TabInfoPage infoPage = getTabInfoPage(position);
        if (infoPage != null && infoPage.tabInfo != null) {
            return BaseFragment.newInstance(infoPage.getActivityClazz(), infoPage.tabInfo, infoPage.index);
        } else {
            return BaseFragment.newInstance(EmptyFragment.class, null, 0);
        }
    }

    @Override
    public long getItemId(int position) {
        TabInfoPage infoPage = getTabInfoPage(position);
        if (infoPage != null) {
            return infoPage.getId();
        } else {
            return AdapterView.INVALID_ROW_ID;
        }
    }

    public TabInfo getTabInfoByIndex(int index) {
        if (index >= 0 && index < tabInfos.size())
            return tabInfos.get(index);
        else
            return null;
    }

    public TabInfoPage getTabInfoPage(int position) {
        if (position >= 0 && position < tabInfoPages.size())
            return tabInfoPages.get(position);
        else
            return null;
    }

    public TabInfo getTabInfo(int position) {
        TabInfoPage page = getTabInfoPage(position);
        if (page != null)
            return page.tabInfo;
        else
            return null;
    }

    public boolean isOddPosition(int position) {
        TabInfoPage page = tabInfoPages.get(position);
        if (page != null)
            return page.index != 0;
        else
            return false;
    }

    public int positionOf(TabInfo tabInfo) {
        if (tabInfo == null)
            return -1;

        for (int i = 0; i < tabInfoPages.size(); i++) {
            TabInfoPage page = tabInfoPages.get(i);
            if (page.tabInfo != null && page.tabInfo.equals(tabInfo)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(TabInfo tabInfo) {
        if (tabInfo == null)
            return -1;

        return tabInfos.indexOf(tabInfo);
    }

    @Override
    public int getCount() {
        return tabInfoPages.size();
    }
}