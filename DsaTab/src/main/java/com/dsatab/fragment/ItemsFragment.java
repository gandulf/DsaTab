package com.dsatab.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.ItemsActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.BaseRecyclerAdapter;
import com.dsatab.data.adapter.EquippedItemRecyclerAdapter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.db.DataManager;
import com.dsatab.util.Debug;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.ResUtil;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.view.FABToolbarHelper;
import com.dsatab.view.listener.HeroInventoryChangedListener;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.h6ah4i.android.widget.advrecyclerview.selectable.RecyclerViewSelectionManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ItemsFragment extends BaseRecyclerFragment implements HeroInventoryChangedListener, TabLayout.OnTabSelectedListener {

    private static final int ACTION_ADD = 1099;
    private static final int ACTION_EDIT = 1098;

    private static final int MENU_FILTER_GROUP = 98;
    private static final int MENU_MOVE_GROUP = 97;

    private static final int INVALID_SET = -1;

    private static final String PREF_KEY_LAST_OPEN_SCREEN = "_lastopenscreen";
    private static final String PREF_KEY_SCREEN_TYPE = "_screen_type";

    public static final String TYPE_GRID = "grid";
    public static final String TYPE_LIST = "list";

    private List<ItemContainer> containers;
    private TabLayout tabLayout;

    private EquippedItemRecyclerAdapter itemsAdapter;

    private FABToolbarLayout fabToolbar;
    private FABToolbarHelper fabToolbarHelper;

    private int mCurrentContainerId = INVALID_SET;
    private String mScreenType = TYPE_LIST;

    protected Callback mContainerCallback;
    protected Callback mItemGridCallback;

    private Set<ItemType> categoriesSelected = new HashSet<ItemType>(Arrays.asList(ItemType.values()));

    private static final class ItemsActionMode extends BaseListableActionMode<ItemsFragment> {

        public ItemsActionMode(ItemsFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            RecyclerView list = listView.get();
            final ItemsFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            EquippedItemRecyclerAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), EquippedItemRecyclerAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                final Object obj = adapter.getItem(index);
                if (obj instanceof ItemCard) {
                    ItemCard itemCard = (ItemCard) obj;

                    Item selectedItem = itemCard.getItem();

                    if (item.getGroupId() == MENU_MOVE_GROUP) {
                        int newScreen = item.getItemId();
                        if (newScreen != selectedItem.getContainerId()) {
                            fragment.getHero().moveItem(selectedItem, newScreen);
                        }
                    } else {
                        switch (item.getItemId()) {
                            case R.id.option_delete:
                                fragment.getHero().removeItem(selectedItem);
                                break;
                            case R.id.option_edit:
                                ItemsActivity.edit(fragment, fragment.getHero(), selectedItem, ACTION_EDIT);
                                mode.finish();
                                return true;
                            case R.id.option_equipped:
                                return false;
                            case R.id.option_move:
                                return false;
                            case R.id.option_equipped_set1:
                                fragment.getHero().addEquippedItem(fragment.getActivity(), selectedItem, null,
                                        null, 0);
                                break;
                            case R.id.option_equipped_set2:
                                fragment.getHero().addEquippedItem(fragment.getActivity(), selectedItem, null,
                                        null, 1);
                                break;
                            case R.id.option_equipped_set3:
                                fragment.getHero().addEquippedItem(fragment.getActivity(), selectedItem, null,
                                        null, 2);
                                break;
                        }
                    }
                }
            }

            mode.finish();
            return true;
        }

        private Hero getHero() {
            return DsaTabApplication.getInstance().getHero();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            ItemsFragment fragment = listFragment.get();
            if (fragment == null)
                return false;

            mode.getMenuInflater().inflate(R.menu.item_list_popupmenu, menu);

            MenuItem move = menu.findItem(R.id.option_move);
            SubMenu moveMenu = move.getSubMenu();
            int order = 4;
            for (ItemContainer<Item> itemContainer : getHero().getItemContainers()) {
                moveMenu.add(MENU_MOVE_GROUP, itemContainer.getId(), order++, itemContainer.getName()).setIcon(
                        ResUtil.getDrawableByUri(fragment.getActivity(), itemContainer.getIconUri()));
            }

            mode.setTitle("Ausrüstung");
            return super.onCreateActionMode(mode, menu);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode
         * (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            RecyclerView list = listView.get();
            final ItemsFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            int selected = 0;
            boolean isEquippable = true;
            boolean changed = false;
            // only moveable if we are not on a set
            boolean isMoveable = !isSetIndex(fragment.getCurrentContainerId());

            MenuItem move = menu.findItem(R.id.option_move);
            MenuItem equipped = menu.findItem(R.id.option_equipped);
            EquippedItemRecyclerAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), EquippedItemRecyclerAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                final Object obj = adapter.getItem(index);

                if (obj instanceof ItemCard) {
                    ItemCard itemCard = (ItemCard) obj;

                    Item selectedItem = itemCard.getItem();
                    selected++;

                    isEquippable &= selectedItem.isEquipable();

                }
            }

            mode.setSubtitle(selected + " ausgewählt");

            changed |= ViewUtils.menuIconState(equipped, isEquippable);
            changed |= ViewUtils.menuIconState(move, isMoveable);
            if (move != null)
                move.setVisible(isMoveable);

            return changed;
        }
    }

    private static final class ItemsContainerActionMode extends BaseListableActionMode<ItemsFragment> {

        private WeakReference<List<ItemContainer>> containersRef;

        protected WeakReference<TabLayout> listView;

        public ItemsContainerActionMode(ItemsFragment fragment, TabLayout listView, List<ItemContainer> containers) {
            super(fragment, null, null);
            this.containersRef = new WeakReference<List<ItemContainer>>(containers);
            this.listView = new WeakReference<TabLayout>(listView);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            TabLayout list = listView.get();
            List<ItemContainer> containers = containersRef.get();
            final ItemsFragment fragment = listFragment.get();
            if (list == null || fragment == null || containers == null)
                return false;

            ItemContainer itemContainer = containers.get(list.getSelectedTabPosition());


            switch (item.getItemId()) {
                case R.id.option_delete:
                    fragment.getHero().removeItemContainer(itemContainer);
                    break;
                case R.id.option_edit: {
                    ItemContainerEditFragment.edit(fragment, itemContainer);
                    mode.finish();
                    return true;
                }
                case R.id.option_add: {
                    ItemContainerEditFragment.insert(fragment);
                    mode.finish();
                    return true;
                }
            }

            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.item_container_popupmenu, menu);
            mode.setTitle("Behälter");
            return super.onCreateActionMode(mode, menu);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode
         * (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            TabLayout list = listView.get();
            List<ItemContainer> containers = containersRef.get();
            final ItemsFragment fragment = listFragment.get();
            if (list == null || fragment == null || containers == null)
                return false;

            boolean changable = true;
            ItemContainer itemContainer = containers.get(list.getSelectedTabPosition());

            changable &= itemContainer.getId() >= Hero.FIRST_INVENTORY_SCREEN;

            MenuItem editItem = menu.findItem(R.id.option_edit);
            MenuItem deleteItem = menu.findItem(R.id.option_delete);

            boolean changed = false;
            changed = ViewUtils.menuIconState(editItem, changable);
            changed |= ViewUtils.menuIconState(deleteItem, changable);

            return changed;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
     */
    @Override
    public void onHeroLoaded(Hero hero) {

        ItemContainer<EquippedItem> set1 = new ItemContainer<>(ItemContainer.SET1, "Set I",
                Util.getUriForResourceId(Util.getThemeResourceId(getActivity(), R.attr.imgSet1)));
        set1.addAll(getHero().getEquippedItems(0));

        ItemContainer<EquippedItem> set2 = new ItemContainer<>(ItemContainer.SET2, "Set II",
                Util.getUriForResourceId(Util.getThemeResourceId(getActivity(), R.attr.imgSet2)));
        set2.addAll(getHero().getEquippedItems(1));

        ItemContainer<EquippedItem> set3 = new ItemContainer<>(ItemContainer.SET3, "Set III",
                Util.getUriForResourceId(Util.getThemeResourceId(getActivity(), R.attr.imgSet3)));
        set3.addAll(getHero().getEquippedItems(2));

        containers.clear();
        containers.add(set1);
        containers.add(set2);
        containers.add(set3);
        containers.addAll(hero.getItemContainers());

        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int containerID = pref.getInt(PREF_KEY_LAST_OPEN_SCREEN, ItemContainer.SET1);

        tabLayout.removeAllTabs();
        // --
        for (ItemContainer container : containers) {
            addContainerTab(container);
        }
        tabLayout.setOnTabSelectedListener(this);

        // --
        showScreen(containerID);
    }

    private void configureContainerTab(TabLayout.Tab tab, ItemContainer container) {
        tab.setText(container.getName());
        Drawable drawable = ResUtil.getDrawableByUri(tabLayout.getContext(), container.getIconUri());
        if (drawable!=null) {
            android.support.v4.graphics.drawable.DrawableCompat.setTint(drawable, tabLayout.getResources().getColor(R.color.white));
        }
        tab.setIcon(drawable);
        tab.setTag(container);
    }

    private void addContainerTab(ItemContainer container) {
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int containerID = pref.getInt(PREF_KEY_LAST_OPEN_SCREEN, ItemContainer.SET1);

        TabLayout.Tab tab = tabLayout.newTab();
        configureContainerTab(tab, container);

        tabLayout.addTab(tab, containerID == container.getId());
    }

    private void showItemPopup() {
        ItemsActivity.pick(this, itemsAdapter.getFilter().getTypes(), ACTION_ADD);
    }

    public static boolean isSetIndex(int index) {
        return index >= ItemContainer.SET1 && index <= ItemContainer.SET3;
    }

    public int getCurrentContainerId() {
        return mCurrentContainerId;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.fragment.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mScreenType = pref.getString(PREF_KEY_SCREEN_TYPE, TYPE_LIST);

        categoriesSelected = new HashSet<ItemType>(Arrays.asList(ItemType.values()));

        containers = new ArrayList<>();

        setHasOptionsMenu(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.empty:
                // do not remove tab in itemsfragment
                break;
            default:
                super.onClick(v);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com. actionbarsherlock.view.Menu,
     * com.actionbarsherlock.view.MenuInflater)
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menuitem_filter, menu);
        inflater.inflate(R.menu.item_list_menu, menu);

        if (menu.findItem(R.id.option_filter) != null) {
            SubMenu filterSet = menu.findItem(R.id.option_filter).getSubMenu();
            if (filterSet != null) {
                ItemType[] itemType = ItemType.values();
                for (int i = 0; i < itemType.length; i++) {
                    MenuItem item = filterSet.add(MENU_FILTER_GROUP, i, Menu.NONE, itemType[i].name()).setIcon(DsaUtil.getResourceId(itemType[i]));
                    item.setCheckable(true);
                    item.setChecked(categoriesSelected.contains(itemType[item.getItemId()]));
                }
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.actionbarsherlock.app.SherlockFragment#onPrepareOptionsMenu(com. actionbarsherlock.view.Menu)
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (menu.findItem(R.id.option_filter) != null) {
            SubMenu filterSet = menu.findItem(R.id.option_filter).getSubMenu();
            ItemType[] itemType = ItemType.values();
            for (int i = 0; i < filterSet.size(); i++) {
                MenuItem item = filterSet.getItem(i);
                item.setChecked(categoriesSelected.contains(itemType[item.getItemId()]));
            }
        }

        if (menu.findItem(R.id.option_itemgrid_type) != null) {
            if (TYPE_GRID.equals(mScreenType)) {
                ViewUtils.menuIcon(getToolbarThemedContext(), menu, R.id.option_itemgrid_type, MaterialDrawableBuilder.IconValue.VIEW_GRID);
                menu.findItem(R.id.option_itemgrid_type_grid).setChecked(true);
            } else {
                ViewUtils.menuIcon(getToolbarThemedContext(), menu, R.id.option_itemgrid_type, MaterialDrawableBuilder.IconValue.VIEW_LIST);
                menu.findItem(R.id.option_itemgrid_type_list).setChecked(true);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com. actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getGroupId() == MENU_FILTER_GROUP) {

            item.setChecked(!item.isChecked());

            ItemType category = ItemType.values()[item.getItemId()];
            if (item.isChecked())
                categoriesSelected.add(category);
            else
                categoriesSelected.remove(category);

            itemsAdapter.filter(new ArrayList<ItemType>(categoriesSelected), null, null);

            return true;
        }

        switch (item.getItemId()) {
            case R.id.option_itemgrid_type_grid:
                setScreenType(TYPE_GRID);
                item.setChecked(true);
                getActionBarActivity().supportInvalidateOptionsMenu();
                return true;
            case R.id.option_itemgrid_type_list:
                setScreenType(TYPE_LIST);
                item.setChecked(true);
                getActionBarActivity().supportInvalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = configureContainerView(inflater.inflate(R.layout.sheet_items, container, false));

        recyclerView = (RecyclerView) root.findViewById(android.R.id.list);

        fabToolbar = (FABToolbarLayout) root.findViewById(R.id.fabtoolbar);
        fabToolbarHelper = new FABToolbarHelper(fabToolbar);

        fabToolbarHelper.addToolbarItem(R.drawable.vd_swap_bag, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemContainerEditFragment.insert(ItemsFragment.this);
                fabToolbar.hide();
            }
        });

        fabToolbarHelper.addToolbarItem(R.drawable.vd_battle_gear, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showItemPopup();
                fabToolbar.hide();
            }
        });

        return root;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        ItemContainer container = (ItemContainer) tab.getTag();
        showScreen(container.getId());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        ItemContainer container = (ItemContainer) tab.getTag();
        showScreen(container.getId());

        if (mMode == null) {
            if (mContainerCallback != null) {
                mMode = recyclerView.startActionMode(mContainerCallback);
                mMode.invalidate();
            }
        } else {
            mMode.invalidate();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        itemsAdapter = new EquippedItemRecyclerAdapter(getHero());
        itemsAdapter.setEventListener(this);

        initRecyclerView(recyclerView, itemsAdapter, false, false, true);
        mRecyclerViewSelectionManager.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        // -- container

        mContainerCallback = new ItemsContainerActionMode(this, tabLayout, containers);
        mItemGridCallback = new ItemsActionMode(this, recyclerView, mRecyclerViewSelectionManager);

        updateScreenType();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_ADD && resultCode == Activity.RESULT_OK) {

            UUID itemId = (UUID) data.getSerializableExtra(ItemsActivity.INTENT_EXTRA_ITEM_ID);

            if (itemId != null) {
                Item item = DataManager.getItemById(itemId).duplicate();
                if (isSetIndex(mCurrentContainerId)) {
                    getHero().addEquippedItem(getDsaActivity(), item, null, null, mCurrentContainerId);
                } else {
                    item.setContainerId(mCurrentContainerId);
                    getHero().addItem(item);
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected Callback getActionModeCallback(List<Object> objects) {
        if (objects == null || objects.isEmpty())
            return null;

        for (Object obj : objects) {
            if (obj instanceof ItemContainer) {
                return mContainerCallback;
            } else if (obj instanceof ItemCard) {
                return mItemGridCallback;
            }
        }
        return null;

    }

    @Override
    public void onItemClicked(BaseRecyclerAdapter adapter, int position, View v) {

        if (mMode == null) {
            mRecyclerViewSelectionManager.setSelected(position, false);
            ItemsActivity.view(getActivity(), getHero(), itemsAdapter.getItem(position));
        } else {
            super.onItemClicked(adapter, position, v);
        }

    }

    /*
         * (non-Javadoc)
         *
         * @see android.support.v4.app.Fragment#onStop()
         */
    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        Editor edit = pref.edit();
        edit.putInt(PREF_KEY_LAST_OPEN_SCREEN, mCurrentContainerId);
        edit.putString(PREF_KEY_SCREEN_TYPE, mScreenType);
        edit.apply();
    }

    private void setScreenType(String type) {
        if (!mScreenType.equals(type)) {
            mScreenType = type;
            updateScreenType();
        }

    }

    private void updateScreenType() {
        if (TYPE_GRID.equals(mScreenType)) {
            itemsAdapter.setDisplayType(EquippedItemRecyclerAdapter.GRID);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        } else {
            itemsAdapter.setDisplayType(EquippedItemRecyclerAdapter.LIST);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showScreen(int containerId) {
        Debug.d("Show screen id:" + containerId);

        updateScreenType();

        if (getHero() != null && containerId >= 0) {
            if (mMode != null) {
                mMode.finish();
            }

            itemsAdapter.clear();
            mRecyclerViewSelectionManager.clearSelections();

            if (isSetIndex(containerId)) {
                mCurrentContainerId = containerId;
                itemsAdapter.addAll(getHero().getEquippedItems(containerId));
                itemsAdapter.setContainerId(containerId);
            } else {
                ItemContainer itemContainer = getHero().getItemContainer(containerId);
                if (itemContainer != null) {
                    mCurrentContainerId = itemContainer.getId();
                    itemsAdapter.addAll(itemContainer.getItems());
                } else {
                    mCurrentContainerId = INVALID_SET;

                }
                itemsAdapter.setContainerId(mCurrentContainerId);
            }

            itemsAdapter.notifyDataSetChanged();
            // itemsAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

            refreshEmptyView(itemsAdapter);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.InventoryChangedListener#onItemAdded(com.dsatab .data.items.Item)
     */
    @Override
    public void onItemAdded(Item item) {
        Debug.d("onItemAdded " + item);
        if (item.getContainerId() == mCurrentContainerId) {
            // skip items that are equippable since they will be equipped using
            // a onItemEquipped Event. this would cause duplicates
            if (item.isEquipable() && isSetIndex(mCurrentContainerId))
                return;

            itemsAdapter.add(item);
            // itemsAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
            refreshEmptyView(itemsAdapter);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.InventoryChangedListener#onItemChanged(com.dsatab .data.items.EquippedItem)
     */
    @Override
    public void onItemChanged(EquippedItem item) {
        Debug.d("onItemChanged " + item);
        if (item.getSet() == mCurrentContainerId) {
            // itemsAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
            // itemListAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.HeroChangedListener#onItemChanged(com.dsatab .data.items.Item)
     */
    @Override
    public void onItemChanged(Item item) {
        Debug.d("onItemChanged " + item);
        if (item.getContainerId() == mCurrentContainerId) {
            // itemsAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
            // itemListAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.InventoryChangedListener#onItemRemoved(com.dsatab .data.items.Item)
     */
    @Override
    public void onItemRemoved(Item item) {
        Debug.d("onItemRemoved " + item);
        if (item.getContainerId() == mCurrentContainerId) {
            itemsAdapter.remove(item);

            refreshEmptyView(itemsAdapter);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.InventoryChangedListener#onItemEquipped(com. dsatab.data.items.EquippedItem)
     */
    @Override
    public void onItemEquipped(EquippedItem item) {
        Debug.d("onItemEquipped " + item);

        if (item.getSet() == mCurrentContainerId) {
            itemsAdapter.add(item);
            // itemsAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

            refreshEmptyView(itemsAdapter);
        } else if (mCurrentContainerId >= Hero.FIRST_INVENTORY_SCREEN) {
            // if we are in a bag check if item is present
            int index = itemsAdapter.indexOf(item.getItem());
            if (index >= 0) {
                itemsAdapter.notifyItemChanged(index);
            }
        }

        for (ItemContainer container : containers) {
            if (container.getId() == item.getContainerId()) {
                container.add(item);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.InventoryChangedListener#onItemUnequipped(com .dsatab.data.items.EquippedItem)
     */
    @Override
    public void onItemUnequipped(EquippedItem item) {
        Debug.d("onItemUnequipped " + item);
        if (item.getSet() == mCurrentContainerId) {
            itemsAdapter.remove(item);

            refreshEmptyView(itemsAdapter);
        }

        for (ItemContainer container : containers) {
            if (container.getId() == item.getContainerId()) {
                container.remove(item);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.HeroInventoryChangedListener#onActiveSetChanged (int, int)
     */
    @Override
    public void onActiveSetChanged(int newSet, int oldSet) {

    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerAdded
     * (com.dsatab.data.items.ItemContainer)
     */
    @Override
    public void onItemContainerAdded(ItemContainer itemContainer) {
        Debug.d("onItemContainerAdded " + itemContainer);

        containers.add(itemContainer);
        addContainerTab(itemContainer);
        if (getActivity() != null) {
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerRemoved
     * (com.dsatab.data.items.ItemContainer)
     */
    @Override
    public void onItemContainerRemoved(ItemContainer itemContainer) {
        Debug.d("onItemContainerRemoved " + itemContainer);

        int index = containers.indexOf(itemContainer);
        containers.remove(index);
        tabLayout.removeTabAt(index);
        if (getActivity() != null) {
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerChanged
     * (com.dsatab.data.items.ItemContainer)
     */
    @Override
    public void onItemContainerChanged(ItemContainer itemContainer) {
        Debug.d("onItemContainerChanged " + itemContainer);

        int index = containers.indexOf(itemContainer);
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        configureContainerTab(tab, itemContainer);
    }
}