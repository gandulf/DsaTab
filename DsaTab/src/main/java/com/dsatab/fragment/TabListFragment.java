package com.dsatab.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.config.TabInfo;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.BaseRecyclerAdapter;
import com.dsatab.data.adapter.TabInfoDraggableItemAdapter;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.github.clans.fab.FloatingActionButton;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ganymedes on 04.10.2015.
 */
public class TabListFragment extends BaseRecyclerFragment implements View.OnClickListener {

    private TabInfo currentInfo = null;

    private TabInfoDraggableItemAdapter mAdapter;

    private TabListListener tabListListener;

    public interface TabListListener {
        void onTabInfoClicked(TabInfo tabInfo);

        void onTabInfoSelected(TabInfo tabInfo);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        List<TabInfo> tabs;
        if (DsaTabApplication.getInstance().getHero() != null
                && DsaTabApplication.getInstance().getHero().getHeroConfiguration() != null) {
            tabs = new ArrayList<TabInfo>(DsaTabApplication.getInstance().getHero().getHeroConfiguration().getTabs());
        } else {
            tabs = new ArrayList<TabInfo>();
        }
        mAdapter = new TabInfoDraggableItemAdapter(tabs);
        mAdapter.setEventListener(this);
    }

    /*
         * (non-Javadoc)
         *
         * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
         */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = configureContainerView(inflater.inflate(R.layout.tab_list_content, container, false));
        return root;
    }

    protected void initRecycler() {
        recyclerView = (RecyclerView) findViewById(android.R.id.list);

        initRecyclerView(recyclerView,mAdapter,new LinearLayoutManager(getActivity()),true,true,true);
    }

    public TabListListener getTabListListener() {
        return tabListListener;
    }

    public void setTabListListener(TabListListener tabListListener) {
        this.tabListListener = tabListListener;
    }

    private void createTabInfo() {
        TabInfo info = new TabInfo();
        info.setIconUri(Util.getUriForResourceId(R.drawable.vd_mailed_fist));
        mAdapter.add(info);

        if (tabListListener != null) {
            tabListListener.onTabInfoClicked(info);
        }
    }

    @Override
    public void onItemClicked(BaseRecyclerAdapter adapter, int position, View v) {
        if (tabListListener != null) {
            tabListListener.onTabInfoClicked(mAdapter.getItem(position));
        }
        mRecyclerViewSelectionManager.setSelected(position,true);
    }

    @Override
    public boolean onItemLongClicked(BaseRecyclerAdapter adapter, int position, View v) {
        return false;
    }


    @Override
    public void onItemRemoved(BaseRecyclerAdapter adapter, int position) {

    }
    public void selectTabInfo(int index) {
        mRecyclerViewSelectionManager.setSelected(index,true);
        recyclerView.scrollToPosition(index);

        if (tabListListener != null) {
            tabListListener.onTabInfoSelected(mAdapter.getItem(index));
        }
    }

    public void setTabInfo(int index, TabInfo tabInfo) {
        mAdapter.set(tabInfo, index);
    }
    public void addTabInfo(TabInfo tabInfo) {
        mAdapter.add(tabInfo);
    }

    public List<TabInfo> getTabInfos() {
        if (mAdapter != null)
            return mAdapter.getItems();
        else
            return Collections.emptyList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menuitem_reset, menu);

        if (menu.findItem(R.id.option_reset) != null) {
            menu.findItem(R.id.option_reset).setIcon(ViewUtils.toolbarIcon(getToolbarThemedContext(), MaterialDrawableBuilder.IconValue.BACKUP_RESTORE));
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.option_reset:
                mAdapter.clear();
                mAdapter.addAll(DsaTabApplication.getInstance().getHero().getHeroConfiguration().getDefaultTabs(null));
                selectTabInfo(0);
                break;
        }

        return false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecycler();

        FloatingActionButton fab =(FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                createTabInfo();
        }
        super.onClick(v);
    }

    @Override
    public void onDestroy() {
        mAdapter = null;
        super.onDestroy();
    }

    @Override
    public void onHeroLoaded(Hero hero) {

    }
}
