package com.dsatab.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.util.SparseBooleanArray;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.ItemsActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.GridItemAdapter;
import com.dsatab.data.adapter.ItemAdapter;
import com.dsatab.data.adapter.ItemContainerAdapter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.db.DataManager;
import com.dsatab.util.Debug;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.view.listener.HeroInventoryChangedListener;
import com.gandulf.guilib.util.ListViewCompat;
import com.gandulf.guilib.util.ResUtil;
import com.gandulf.guilib.view.DynamicListViewEx;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class ItemsFragment extends BaseListFragment implements OnItemClickListener, HeroInventoryChangedListener {

	private static final int ACTION_ADD = 1099;
	private static final int ACTION_EDIT = 1098;

	private static final int MENU_FILTER_GROUP = 98;
	private static final int MENU_MOVE_GROUP = 97;

	private static final int INVALID_SET = -1;

	private static final String PREF_KEY_LAST_OPEN_SCREEN = "_lastopenscreen";
	private static final String PREF_KEY_SCREEN_TYPE = "_screen_type";

	public static final String TYPE_GRID = "grid";
	public static final String TYPE_LIST = "list";

	private ListView containerList;
	private ItemContainerAdapter containerAdapter;

	private ItemContainerAdapter containerSpinnerAdapter;

	private GridView itemGridCompat;
	private GridItemAdapter itemGridAdapter;

	private SlidingPaneLayout slidingPaneLayout;

	private DynamicListViewEx itemList;
	private ItemAdapter itemListAdapter;

	private FloatingActionsMenu fabMenu;

	private int mCurrentContainerId = INVALID_SET;
	private String mScreenType = TYPE_LIST;

	protected Callback mContainerCallback;
	protected Callback mItemGridCallback;
	protected Callback mItemListCallback;

	private Set<ItemType> categoriesSelected = new HashSet<ItemType>(Arrays.asList(ItemType.values()));

	private static final class ItemsActionMode implements ActionMode.Callback {

		private WeakReference<AbsListView> listView;
		private WeakReference<ItemsFragment> listFragment;

		public ItemsActionMode(ItemsFragment fragment, AbsListView listView) {
			this.listFragment = new WeakReference<ItemsFragment>(fragment);
			this.listView = new WeakReference<AbsListView>(listView);
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			boolean notifyChanged = false;

			AbsListView list = listView.get();
			ItemsFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			SparseBooleanArray checkedPositions = ListViewCompat.getCheckedItemPositions(list);

			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						Object obj = list.getItemAtPosition(checkedPositions.keyAt(i));

						if (obj instanceof ItemCard) {
							ItemCard itemCard = (ItemCard) obj;

							Item selectedItem = itemCard.getItem();

							if (item.getGroupId() == MENU_MOVE_GROUP) {
								int newScreen = item.getItemId();
								if (newScreen != selectedItem.getContainerId()) {
									getHero().moveItem(selectedItem, newScreen);
									notifyChanged = true;
								}
							} else {
								switch (item.getItemId()) {
								case R.id.option_delete:
									getHero().removeItem(selectedItem);
									notifyChanged = false;
									break;
								case R.id.option_view:
									if (fragment.getActivity() instanceof ItemsActivity) {
										ItemsActivity itemsActivity = ((ItemsActivity) fragment.getActivity());
										itemsActivity.viewItem(selectedItem, null);
									} else {
										ItemsActivity.view(fragment.getActivity(), getHero(), selectedItem);
									}
									mode.finish();
									return true;
								case R.id.option_edit:
									if (fragment.getActivity() instanceof ItemsActivity) {
										ItemsActivity itemsActivity = ((ItemsActivity) fragment.getActivity());
										itemsActivity.editItem(selectedItem, null);
									} else {
										ItemsActivity
												.edit(fragment.getActivity(), getHero(), selectedItem, ACTION_EDIT);
									}
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

				}
				if (notifyChanged) {
					Util.notifyDatasetChanged(list);
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
			AbsListView list = listView.get();
			ItemsFragment fragment = listFragment.get();
			if (list == null || fragment == null)
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
			return true;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			AbsListView list = listView.get();
			ItemsFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return;

			fragment.mMode = null;
			list.clearChoices();

			Util.notifyDatasetChanged(list);
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

			AbsListView list = listView.get();
			ItemsFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			SparseBooleanArray checkedPositions = ListViewCompat.getCheckedItemPositions(list);
			int selected = 0;
			boolean isEquippable = true;
			boolean changed = false;
			// only moveable if we are not on a set
			boolean isMoveable = !isSetIndex(fragment.getCurrentContainerId());

			MenuItem move = menu.findItem(R.id.option_move);
			MenuItem view = menu.findItem(R.id.option_view);
			MenuItem equipped = menu.findItem(R.id.option_equipped);
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						Object obj = list.getItemAtPosition(checkedPositions.keyAt(i));

						if (obj instanceof ItemCard) {
							ItemCard itemCard = (ItemCard) obj;

							Item selectedItem = itemCard.getItem();
							selected++;

							isEquippable &= selectedItem.isEquipable();

						}
					}
				}
			}

			mode.setSubtitle(selected + " ausgewählt");

			if (selected == 1) {
				if (!view.isEnabled()) {
					view.setEnabled(true);
					changed = true;
				}
			} else {
				if (view.isEnabled()) {
					view.setEnabled(false);
					changed = true;
				}
			}

			if (isEquippable) {
				if (!equipped.isEnabled()) {
					equipped.setEnabled(true);
					changed = true;
				}
			} else {
				if (equipped.isEnabled()) {
					equipped.setEnabled(false);
					changed = true;
				}
			}

			if (isMoveable) {
				if (!move.isEnabled()) {
					move.setEnabled(true);
					changed = true;
				}
			} else {
				if (move.isEnabled()) {
					move.setEnabled(false);
					changed = true;
				}
			}

			return changed;
		}
	}

	private static final class ItemsContainerActionMode implements ActionMode.Callback {

		private WeakReference<ListView> listView;
		private WeakReference<ItemsFragment> listFragment;

		public ItemsContainerActionMode(ItemsFragment fragment, ListView listView) {
			this.listFragment = new WeakReference<ItemsFragment>(fragment);
			this.listView = new WeakReference<ListView>(listView);

		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			final ListView list = listView.get();
			final ItemsFragment fragment = listFragment.get();

			if (list == null || fragment == null)
				return false;

			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = ListViewCompat.getCheckedItemPositions(list);
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						ItemContainer itemContainer = (ItemContainer) list.getItemAtPosition(checkedPositions.keyAt(i));

						switch (item.getItemId()) {
						case R.id.option_delete:
							int pos = checkedPositions.keyAt(i);
							ItemContainer container = (ItemContainer) list.getItemAtPosition(pos);
							fragment.getHero().removeItemContainer(container);
							break;
						case R.id.option_edit: {
							ItemContainerEditFragment.edit(fragment.getActivity(), itemContainer);
							mode.finish();
							return true;
						}
						case R.id.option_add: {
							ItemContainerEditFragment.insert(fragment.getActivity());
							mode.finish();
							return true;
						}
						}
					}

				}
				if (notifyChanged) {
					Util.notifyDatasetChanged(list);
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.item_container_popupmenu, menu);
			mode.setTitle("Behälter");
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			ListView list = listView.get();
			ItemsFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return;

			fragment.mMode = null;
			list.clearChoices();

			Util.notifyDatasetChanged(list);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode
		 * (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			final ListView list = listView.get();
			final ItemsFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			SparseBooleanArray checkedPositions = ListViewCompat.getCheckedItemPositions(list);
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						ItemContainer<?> itemContainer = (ItemContainer<?>) list.getItemAtPosition(checkedPositions
								.keyAt(i));

						MenuItem editItem = menu.findItem(R.id.option_edit);
						MenuItem deleteItem = menu.findItem(R.id.option_delete);

						editItem.setEnabled(itemContainer.getId() >= Hero.FIRST_INVENTORY_SCREEN);
						deleteItem.setEnabled(itemContainer.getId() > Hero.FIRST_INVENTORY_SCREEN);
						return true;
					}
				}
			}

			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {

		ItemContainer<EquippedItem> set1 = new ItemContainer<EquippedItem>(ItemContainer.SET1, "Set I",
				Util.getUriForResourceId(Util.getThemeResourceId(getActivity(), R.attr.imgSet1)));
		set1.addAll(getHero().getEquippedItems(0));

		ItemContainer<EquippedItem> set2 = new ItemContainer<EquippedItem>(ItemContainer.SET2, "Set II",
				Util.getUriForResourceId(Util.getThemeResourceId(getActivity(), R.attr.imgSet2)));
		set2.addAll(getHero().getEquippedItems(1));

		ItemContainer<EquippedItem> set3 = new ItemContainer<EquippedItem>(ItemContainer.SET3, "Set III",
				Util.getUriForResourceId(Util.getThemeResourceId(getActivity(), R.attr.imgSet3)));
		set3.addAll(getHero().getEquippedItems(2));

		containerAdapter = new ItemContainerAdapter(getActivity(), R.layout.item_listitem_view);
		containerAdapter.add(set1);
		containerAdapter.add(set2);
		containerAdapter.add(set3);
		containerAdapter.addAll(getHero().getItemContainers());

		containerSpinnerAdapter = new ItemContainerAdapter(getActionBarActivity().getSupportActionBar()
				.getThemedContext(), R.layout.item_actionbar_spinneritem_view);
		containerSpinnerAdapter.setDropDownViewResource(R.layout.item_actionbar_spinneritem_view);
		containerSpinnerAdapter.add(set1);
		containerSpinnerAdapter.add(set2);
		containerSpinnerAdapter.add(set3);
		containerSpinnerAdapter.addAll(getHero().getItemContainers());

		if (containerList != null) {
			containerList.setAdapter(containerAdapter);
			mContainerCallback = new ItemsContainerActionMode(this, containerList);
		}
		itemListAdapter = new ItemAdapter(getActivity(), hero);
		itemList.setAdapter(itemListAdapter);

		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		int containerID = pref.getInt(PREF_KEY_LAST_OPEN_SCREEN, ItemContainer.SET1);

		showScreen(containerID);
	}

	private void showItemPopup() {
		ItemsActivity.pick(getActivity(), itemGridAdapter.getFilter().getTypes(), ACTION_ADD);
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

		setHasOptionsMenu(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fab_container_add:
			ItemContainerEditFragment.insert(getActivity());
			fabMenu.collapse();
			break;
		case R.id.fab_items_add:
			showItemPopup();
			fabMenu.collapse();
			break;
		case android.R.id.empty:
			// do not remove tab in itemsfragment
			break;
		default:
			super.onClick(v);
		}

	}

	@Override
	public void hideActionBarItems() {
		super.hideActionBarItems();
		removeItemsNavigation();
	}

	@Override
	public void showActionBarItems() {
		super.showActionBarItems();
		initItemsNavigation();
	}

	private void removeItemsNavigation() {
		ActionBar actionBar = getActionBarActivity().getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setListNavigationCallbacks(null, null);
	}

	private void initItemsNavigation() {
		ActionBar actionBar = getActionBarActivity().getSupportActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(containerSpinnerAdapter, new ActionBar.OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				showScreen(itemPosition);
				return false;
			}
		});

		for (int i = 0; i < containerSpinnerAdapter.getCount(); i++) {
			ItemContainer<?> container = containerSpinnerAdapter.getItem(i);

			if (container.getId() == mCurrentContainerId) {
				actionBar.setSelectedNavigationItem(i);
				break;
			}
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
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.item_list_menu, menu);

		if (menu.findItem(R.id.option_item_filter) != null) {
			SubMenu filterSet = menu.findItem(R.id.option_item_filter).getSubMenu();
			if (filterSet != null) {
				ItemType[] itemType = ItemType.values();
				for (int i = 0; i < itemType.length; i++) {
					MenuItem item = filterSet.add(MENU_FILTER_GROUP, i, Menu.NONE, itemType[i].name()).setIcon(
							DsaUtil.getResourceId(itemType[i]));
					item.setCheckable(true);
					item.setChecked(categoriesSelected.contains(itemType[item.getItemId()]));
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onPrepareOptionsMenu(com. actionbarsherlock.view.Menu)
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (menu.findItem(R.id.option_item_add_table) != null) {
			menu.findItem(R.id.option_item_add_table).setVisible(!isDrawerOpened());
		}

		if (menu.findItem(R.id.option_add_container) != null) {
			menu.findItem(R.id.option_add_container).setVisible(!isDrawerOpened());
		}

		if (menu.findItem(R.id.option_item_filter) != null) {
			menu.findItem(R.id.option_item_filter).setVisible(!isDrawerOpened());
			SubMenu filterSet = menu.findItem(R.id.option_item_filter).getSubMenu();
			ItemType[] itemType = ItemType.values();
			for (int i = 0; i < filterSet.size(); i++) {
				MenuItem item = filterSet.getItem(i);
				item.setChecked(categoriesSelected.contains(itemType[item.getItemId()]));
			}
		}

		if (menu.findItem(R.id.option_itemgrid_type) != null) {
			menu.findItem(R.id.option_itemgrid_type).setVisible(!isDrawerOpened());
			if (TYPE_GRID.equals(mScreenType)) {
				menu.findItem(R.id.option_itemgrid_type).setIcon(R.drawable.dsa_grid);
				menu.findItem(R.id.option_itemgrid_type_grid).setChecked(true);
			} else {
				menu.findItem(R.id.option_itemgrid_type).setIcon(R.drawable.dsa_list);
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

			itemListAdapter.filter(new ArrayList<ItemType>(categoriesSelected), null, null);
			itemGridAdapter.filter(new ArrayList<ItemType>(categoriesSelected), null, null);

			return true;
		}

		switch (item.getItemId()) {
		case R.id.option_item_add_table:
			showItemPopup();
			return true;
		case R.id.option_add_container:
			ItemContainerEditFragment.insert(getActivity());
			return true;
		case R.id.option_itemgrid_type_grid:
			setScreenType(TYPE_GRID);
			item.setChecked(true);
			getActionBarActivity().invalidateOptionsMenu();
			return true;
		case R.id.option_itemgrid_type_list:
			setScreenType(TYPE_LIST);
			item.setChecked(true);
			getActionBarActivity().invalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_items, container, false));

		containerList = (ListView) root.findViewById(R.id.container_list);
		itemGridCompat = (GridView) root.findViewById(R.id.workspace);

		itemList = (DynamicListViewEx) root.findViewById(android.R.id.list);
		itemList.setOnItemCheckedListener(this);

		mItemGridCallback = new ItemsActionMode(this, itemGridCompat);
		mItemListCallback = new ItemsActionMode(this, itemList);

		slidingPaneLayout = (SlidingPaneLayout) root.findViewById(R.id.sheet_items);
		slidingPaneLayout.setCoveredFadeColor(0);
		slidingPaneLayout.setSliderFadeColor(0);

		fabMenu = (FloatingActionsMenu) root.findViewById(R.id.fab_menu);

		root.findViewById(R.id.fab_container_add).setOnClickListener(this);
		root.findViewById(R.id.fab_items_add).setOnClickListener(this);

		fabMenu.attachToListView(itemGridCompat);
		fabMenu.attachToListView(itemList);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 192*288
		// 120*180

		itemGridAdapter = new GridItemAdapter(getActivity());
		itemGridCompat.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		itemGridCompat.setAdapter(itemGridAdapter);
		itemGridCompat.setOnItemClickListener(this);
		itemGridCompat.setOnItemLongClickListener(this);

		itemList.setOnItemClickListener(this);
		itemList.setOnItemLongClickListener(this);
		itemList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		if (containerList != null) {
			containerList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
			containerList.setOnItemClickListener(this);
			containerList.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick (android.widget.AdapterView,
				 * android.view.View, int, long)
				 */
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					containerList.setItemChecked(position, true);

					ItemContainer container = (ItemContainer) containerList.getItemAtPosition(position);
					showScreen(container.getId());

					if (mMode == null) {
						if (mContainerCallback != null) {
							mMode = getActionBarActivity().startActionMode(mContainerCallback);
							mMode.invalidate();
							return true;
						} else {
							return false;
						}
					} else {
						mMode.invalidate();
					}
					return false;
				}
			});
		}

		slidingPaneLayout.openPane();

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_ADD) {
			if (resultCode == Activity.RESULT_OK) {
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
				if (TYPE_GRID.equals(mScreenType))
					return mItemGridCallback;
				else
					return mItemListCallback;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View,
	 * int, long)
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		// containerlist
		if (parent == containerList) {
			if (mMode != null) {
				mMode.invalidate();
			}
			containerList.setItemChecked(position, true);
			ItemContainer container = (ItemContainer) containerList.getItemAtPosition(position);
			showScreen(container.getId());
		} else {
			// items grid
			if (mMode == null) {
				itemGridCompat.setItemChecked(position, false);
				itemList.setItemChecked(position, false);
				ItemsActivity.view(getActivity(), getHero(), itemGridAdapter.getItem(position));
			} else {
				super.onItemClick(parent, view, position, id);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();

		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putInt(PREF_KEY_LAST_OPEN_SCREEN, mCurrentContainerId);
		edit.putString(PREF_KEY_SCREEN_TYPE, mScreenType);
		edit.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		initItemsNavigation();
	}

	@Override
	public void onPause() {
		super.onPause();
		removeItemsNavigation();
	}

	private void setScreenType(String type) {
		if (!mScreenType.equals(type)) {
			mScreenType = type;
			updateScreenType();
		}

	}

	private void updateScreenType() {
		if (TYPE_GRID.equals(mScreenType)) {
			itemGridCompat.setVisibility(View.VISIBLE);
			itemList.setVisibility(View.GONE);
		} else {
			itemGridCompat.setVisibility(View.GONE);
			itemList.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected View getListView() {
		if (TYPE_GRID.equals(mScreenType)) {
			return itemGridCompat;
		} else {
			return itemList;
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showScreen(int containerId) {
		Debug.trace("Show screen id:" + containerId);

		updateScreenType();

		if (getHero() != null && containerId >= 0) {
			if (mMode != null) {
				mMode.finish();
			}
			itemGridAdapter.setNotifyOnChange(false);
			itemGridAdapter.clear();
			itemGridCompat.clearChoices();

			itemListAdapter.setNotifyOnChange(false);
			itemListAdapter.clear();
			itemList.clearChoices();

			if (isSetIndex(containerId)) {
				mCurrentContainerId = containerId;
				itemGridAdapter.addAll(getHero().getEquippedItems(containerId));
				itemListAdapter.addAll(getHero().getEquippedItems(containerId));
				if (containerList != null) {
					containerList.setItemChecked(containerId, true);
				}
				itemListAdapter.setContainerId(containerId);
			} else {
				ItemContainer itemContainer = getHero().getItemContainer(containerId);
				if (itemContainer != null) {
					mCurrentContainerId = itemContainer.getId();
					itemGridAdapter.addAll(itemContainer.getItems());
					itemListAdapter.addAll(itemContainer.getItems());

					if (containerList != null) {
						int index = containerAdapter.indexOf(itemContainer);
						containerList.setItemChecked(index, true);
					}
				} else {
					mCurrentContainerId = INVALID_SET;

				}
				itemListAdapter.setContainerId(mCurrentContainerId);
			}

			itemGridAdapter.notifyDataSetChanged();
			// itemGridAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

			itemListAdapter.notifyDataSetChanged();
			// itemListAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

			ActionBar actionBar = getActionBarActivity().getSupportActionBar();
			if (actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST) {
				for (int i = 0; i < containerSpinnerAdapter.getCount(); i++) {
					ItemContainer container = containerSpinnerAdapter.getItem(i);

					if (container.getId() == mCurrentContainerId) {
						actionBar.setSelectedNavigationItem(i);
						break;
					}
				}
			}

			refreshEmptyView(itemListAdapter);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemAdded(com.dsatab .data.items.Item)
	 */
	@Override
	public void onItemAdded(Item item) {
		Debug.trace("onItemAdded " + item);
		if (item.getContainerId() == mCurrentContainerId) {
			// skip items that are equippable since they will be equipped using
			// a onItemEquipped Event. this would cause duplicates
			if (item.isEquipable() && isSetIndex(mCurrentContainerId))
				return;

			itemGridAdapter.add(item);
			// itemGridAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

			itemListAdapter.add(item);
			// itemListAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

			refreshEmptyView(itemListAdapter);
		}
		containerAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemChanged(com.dsatab .data.items.EquippedItem)
	 */
	@Override
	public void onItemChanged(EquippedItem item) {
		Debug.trace("onItemChanged " + item);
		if (item.getSet() == mCurrentContainerId) {
			// itemGridAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
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
		Debug.trace("onItemChanged " + item);
		if (item.getContainerId() == mCurrentContainerId) {
			// itemGridAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
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
		Debug.trace("onItemRemoved " + item);
		if (item.getContainerId() == mCurrentContainerId) {
			itemGridAdapter.remove(item);
			itemListAdapter.remove(item);

			refreshEmptyView(itemListAdapter);
		}
		containerAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemEquipped(com. dsatab.data.items.EquippedItem)
	 */
	@Override
	public void onItemEquipped(EquippedItem item) {
		Debug.trace("onItemEquipped " + item);

		if (item.getSet() == mCurrentContainerId) {
			itemGridAdapter.add(item);
			// itemGridAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

			itemListAdapter.add(item);
			// itemListAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

			refreshEmptyView(itemListAdapter);
		}

		for (ItemContainer container : containerAdapter.getItems()) {
			if (container.getId() == item.getContainerId()) {
				container.add(item);
			}
		}
		containerAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemUnequipped(com .dsatab.data.items.EquippedItem)
	 */
	@Override
	public void onItemUnequipped(EquippedItem item) {
		Debug.trace("onItemUnequipped " + item);
		if (item.getSet() == mCurrentContainerId) {
			itemGridAdapter.remove(item);
			itemListAdapter.remove(item);

			refreshEmptyView(itemListAdapter);
		}

		for (ItemContainer container : containerAdapter.getItems()) {
			if (container.getId() == item.getContainerId()) {
				container.remove(item);
			}
		}
		containerAdapter.notifyDataSetChanged();
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
		Debug.trace("onItemContainerAdded " + itemContainer);

		containerAdapter.add(itemContainer);
		if (getActivity() != null) {
			getActivity().invalidateOptionsMenu();
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
		Debug.trace("onItemContainerRemoved " + itemContainer);

		containerAdapter.remove(itemContainer);
		if (getActivity() != null) {
			getActivity().invalidateOptionsMenu();
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
		Debug.trace("onItemContainerChanged " + itemContainer);
		containerAdapter.notifyDataSetChanged();
	}

}
