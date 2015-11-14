package com.dsatab.fragment.item;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dsatab.R;
import com.dsatab.activity.ItemsActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.BaseRecyclerAdapter;
import com.dsatab.data.adapter.CursorRecyclerAdapter;
import com.dsatab.data.adapter.ItemCursorAdapter;
import com.dsatab.data.adapter.ItemCursorRecyclerAdapter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.db.DataManager;
import com.dsatab.fragment.BaseRecyclerFragment;
import com.dsatab.util.DsaUtil;
import com.github.clans.fab.FloatingActionButton;
import com.h6ah4i.android.widget.advrecyclerview.selectable.RecyclerViewSelectionManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.wnafee.vector.compat.ResourcesCompat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ItemListFragment extends BaseRecyclerFragment implements CursorRecyclerAdapter.EventListener, LoaderCallbacks<Cursor> {

    private static final int MENU_FILTER_GROUP = 98;

    public interface OnItemSelectedListener {
        boolean onItemSelected(Item item);
    }

    private ItemCursorAdapter searchViewCursorAdapter;

    private ItemCursorRecyclerAdapter itemAdapter = null;

    private Collection<ItemType> itemTypes = null;
    private String constraint, category;

    private FloatingActionButton fab;

    private OnItemSelectedListener itemSelectedListener;

    protected static class ItemCursorLoader extends CursorLoader {

        private String constraint;
        private Collection<ItemType> itemTypes = null;
        private String category;

        public ItemCursorLoader(Context context, String contraint, Collection<ItemType> itemTypes, String category) {
            super(context);
            this.constraint = contraint;
            this.itemTypes = itemTypes;
            this.category = category;
        }

        @Override
        public Cursor loadInBackground() {
            return DataManager.getItemsCursor(constraint, itemTypes, category);
        }
    }

    protected static final class ItemActionMode extends BaseListableActionMode<ItemListFragment> {

        public ItemActionMode(ItemListFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
            RecyclerView list = listView.get();
            final ItemListFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            boolean notifyChanged = false;

            ItemCursorRecyclerAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ItemCursorRecyclerAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                final Object obj = adapter.getItem(index);
                if (obj instanceof Cursor) {

                    final Item item = DataManager.getItemByCursor((Cursor) obj);

                    switch (menuItem.getItemId()) {
                        case R.id.option_delete: {
                            DataManager.deleteItem(item);
                            notifyChanged = true;
                            break;
                        }
                    }

                }
            }
            if (notifyChanged) {
                fragment.refresh();
            }

            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menuitem_delete, menu);
            mode.setTitle("Gegenstände");
            return super.onCreateActionMode(mode, menu);
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            RecyclerView list = listView.get();
            final ItemListFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            int selected = getManager().getSelectedPositions().size();

            mode.setSubtitle(selected + " ausgewählt");
            return true;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.fragment.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        itemTypes = new HashSet<ItemType>(Arrays.asList(ItemType.values()));

        getActivity().getLoaderManager().initLoader(0, null, this);
    }

    public String getHeroKey() {
        if (getActivity() != null && getActivity().getIntent() != null)
            return getActivity().getIntent().getStringExtra(ItemsActivity.INTENT_EXTRA_HERO_KEY);
        else
            return null;
    }


    @Override
    public void onItemClicked(BaseRecyclerAdapter adapter, int position, View v) {
        if (mMode == null) {
            mRecyclerViewSelectionManager.clearSelections();
            mRecyclerViewSelectionManager.setSelected(position, true);
            Item item = DataManager.getItemByCursor((Cursor) itemAdapter.getItem(position));
            itemSelectedListener.onItemSelected(item);
        } else {
            super.onItemClicked(adapter, position, v);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sheet_item_chooser, container, false);

        recyclerView = (RecyclerView) root.findViewById(android.R.id.list);

        fab =(FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab:
                ItemsActivity.insert(getActivity(), getHeroKey(), ItemsActivity.ACTION_CREATE);
                break;
            default:
                super.onClick(v);
                break;
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemAdapter = new ItemCursorRecyclerAdapter(null);
        itemAdapter.setEventListener(this);

        initRecyclerView(recyclerView,itemAdapter,false,false,true);

        mRecyclerViewSelectionManager.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mCallback = new ItemActionMode(this, recyclerView, mRecyclerViewSelectionManager);

    }

    @Override
    protected Callback getActionModeCallback(List<Object> objects) {
        return mCallback;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.fragment.BaseFragment#onHeroLoaded(com.dsatab.data.Hero)
     */
    @Override
    public void onHeroLoaded(Hero hero) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menuitem_search, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setTitle(android.R.string.search_go);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchViewCursorAdapter.getCount() > 0) {
                    Item item = DataManager.getItemByCursor((Cursor) searchViewCursorAdapter.getItem(0));
                    if (getOnItemSelectedListener() != null) {
                        getOnItemSelectedListener().onItemSelected(item);
                        MenuItemCompat.collapseActionView(menuItem);
                        searchView.setIconified(true);
                        searchView.clearFocus();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                constraint = DataManager.likify(newText, true);
                searchViewCursorAdapter.changeCursor(DataManager.getItemsCursor(constraint, itemTypes, category));
                return false;
            }
        });
        searchViewCursorAdapter = new ItemCursorAdapter(searchView.getContext(), DataManager.getItemsCursor(constraint, itemTypes, category));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchViewCursorAdapter.getItem(position);
                Item item = DataManager.getItemByCursor(cursor);
                if (getOnItemSelectedListener() != null) {
                    getOnItemSelectedListener().onItemSelected(item);
                    MenuItemCompat.collapseActionView(menuItem);
                    searchView.setIconified(true);
                    searchView.clearFocus();
                    return true;
                }
                return false;
            }
        });
        searchView.setSuggestionsAdapter(searchViewCursorAdapter);
        // --

        inflater.inflate(R.menu.menuitem_filter, menu);

        if (menu.findItem(R.id.option_filter) != null) {
            SubMenu filterSet = menu.findItem(R.id.option_filter).getSubMenu();
            if (filterSet != null) {
                ItemType[] itemType = ItemType.values();
                for (int i = 0; i < itemType.length; i++) {
                    Drawable icon = ResourcesCompat.getDrawable(getActivity(),DsaUtil.getResourceId(itemType[i]));
                    MenuItem childItem = filterSet.add(MENU_FILTER_GROUP, i, Menu.NONE, itemType[i].name()).setIcon(icon);
                    childItem.setCheckable(true);
                    childItem.setChecked(itemTypes.contains(itemType[childItem.getItemId()]));
                }
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (menu.findItem(R.id.option_filter) != null) {
            menu.findItem(R.id.option_filter).setVisible(!isDrawerOpened());
            SubMenu filterSet = menu.findItem(R.id.option_filter).getSubMenu();
            ItemType[] itemType = ItemType.values();
            for (int i = 0; i < filterSet.size(); i++) {
                MenuItem item = filterSet.getItem(i);
                item.setChecked(itemTypes.contains(itemType[item.getItemId()]));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ItemsActivity.ACTION_EDIT || requestCode == ItemsActivity.ACTION_CREATE) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().getLoaderManager().initLoader(0, null, this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem )
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getGroupId() == MENU_FILTER_GROUP) {
            item.setChecked(!item.isChecked());
            ItemType itemType = ItemType.values()[item.getItemId()];
            if (item.isChecked())
                itemTypes.add(itemType);
            else
                itemTypes.remove(itemType);

            filter(itemTypes, category, constraint);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public Cursor filter(Collection<ItemType> type, String category, String constraint) {
        Cursor cursor = DataManager.getItemsCursor(constraint, type, category);
        if (itemAdapter != null) {
            itemAdapter.changeCursor(cursor);
        }
        return cursor;
    }

    public void refresh() {
        if (getActivity() != null) {
            getActivity().getLoaderManager().restartLoader(0, null, this);
        }
    }

    public void setItemType(ItemType itemType) {
        itemTypes.clear();
        itemTypes.add(itemType);
    }

    public Collection<ItemType> getItemTypes() {
        return Collections.unmodifiableCollection(itemTypes);
    }

    public void setItemTypes(Collection<ItemType> itemType) {
        this.itemTypes = itemType;
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return itemSelectedListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemClickListener) {
        itemSelectedListener = onItemClickListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ItemCursorLoader(getActivity(), constraint, itemTypes, category);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        itemAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemAdapter.swapCursor(null);
    }

}
