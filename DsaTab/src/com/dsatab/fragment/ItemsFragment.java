package com.dsatab.fragment;

import java.lang.ref.WeakReference;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.ItemContainerEditActivity;
import com.dsatab.activity.ItemEditActivity;
import com.dsatab.activity.ItemViewActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.GridItemAdapter;
import com.dsatab.data.adapter.ItemContainerAdapter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;
import com.dsatab.view.ItemChooserDialog;
import com.dsatab.view.listener.HeroInventoryChangedListener;
import com.haarman.listviewanimations.itemmanipulation.AnimateAdapter;
import com.haarman.listviewanimations.itemmanipulation.OnAnimateCallback;
import com.rokoder.android.lib.support.v4.widget.GridViewCompat;

public class ItemsFragment extends BaseListFragment implements OnItemClickListener, HeroInventoryChangedListener,
		OnAnimateCallback {

	private static final int MENU_CONTAINER_GROUP = 99;

	private static final String PREF_KEY_LAST_OPEN_SCREEN = "_lastopenscreen";

	private ListView containerList;
	private AnimateAdapter<ItemContainer> animateAdapter;
	private ItemContainerAdapter containerAdapter;

	private GridViewCompat itemGridCompat;

	private GridItemAdapter itemAdapter;

	private int mCurrentScreen = -1;

	private ItemChooserDialog itemChooserDialog;

	protected Callback mContainerCallback;
	protected Callback mItemCallback;

	private static final class ItemsActionMode implements ActionMode.Callback {

		private WeakReference<GridViewCompat> listView;
		private WeakReference<ItemsFragment> listFragment;

		public ItemsActionMode(ItemsFragment fragment, GridViewCompat listView) {
			this.listFragment = new WeakReference<ItemsFragment>(fragment);
			this.listView = new WeakReference<GridViewCompat>(listView);
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;

			GridViewCompat list = listView.get();
			ItemsFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						Object obj = list.getItemAtPosition(checkedPositions.keyAt(i));

						if (obj instanceof ItemCard) {
							ItemCard itemCard = (ItemCard) obj;

							Item selectedItem = itemCard.getItem();

							if (item.getGroupId() == MENU_CONTAINER_GROUP) {
								int newScreen = item.getItemId();
								if (newScreen != selectedItem.getScreen()) {
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
									ItemViewActivity.view(fragment.getActivity(), getHero(), selectedItem);
									mode.finish();
									return true;
								case R.id.option_edit:
									ItemEditActivity.edit(fragment.getActivity(), getHero(), selectedItem);
									mode.finish();
									return true;
								case R.id.option_equipped:
									return false;
								case R.id.option_move:
									return false;
								case R.id.option_equipped_set1:
									getHero().addEquippedItem(fragment.getActivity(), selectedItem, null, null, 0);
									break;
								case R.id.option_equipped_set2:
									getHero().addEquippedItem(fragment.getActivity(), selectedItem, null, null, 1);
									break;
								case R.id.option_equipped_set3:
									getHero().addEquippedItem(fragment.getActivity(), selectedItem, null, null, 2);
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
			GridViewCompat list = listView.get();
			ItemsFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			mode.getMenuInflater().inflate(R.menu.item_list_popupmenu, menu);

			com.actionbarsherlock.view.MenuItem move = menu.findItem(R.id.option_move);
			SubMenu moveMenu = move.getSubMenu();

			for (ItemContainer itemContainer : getHero().getItemContainers()) {
				moveMenu.add(MENU_CONTAINER_GROUP, itemContainer.getId(), Menu.NONE, itemContainer.getName()).setIcon(
						Util.getDrawableByUri(itemContainer.getIconUri()));
			}

			mode.setTitle("Ausrüstung");
			return true;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			GridViewCompat list = listView.get();
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

			GridViewCompat list = listView.get();
			ItemsFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			int selected = 0;
			boolean isEquippable = true;
			boolean changed = false;
			// only moveable if we are not on a set
			boolean isMoveable = fragment.getActiveSet() == -1;

			com.actionbarsherlock.view.MenuItem move = menu.findItem(R.id.option_move);
			com.actionbarsherlock.view.MenuItem view = menu.findItem(R.id.option_view);
			com.actionbarsherlock.view.MenuItem equipped = menu.findItem(R.id.option_equipped);
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
		private WeakReference<AnimateAdapter<ItemContainer>> animateAdapter;
		private WeakReference<ItemsFragment> listFragment;

		public ItemsContainerActionMode(ItemsFragment fragment, ListView listView,
				AnimateAdapter<ItemContainer> animateAdapter) {
			this.listFragment = new WeakReference<ItemsFragment>(fragment);
			this.listView = new WeakReference<ListView>(listView);
			this.animateAdapter = new WeakReference<AnimateAdapter<ItemContainer>>(animateAdapter);
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			final ListView list = listView.get();
			final ItemsFragment fragment = listFragment.get();
			final AnimateAdapter<ItemContainer> adapter = animateAdapter.get();
			if (list == null || fragment == null || adapter == null)
				return false;

			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						ItemContainer itemContainer = (ItemContainer) list.getItemAtPosition(checkedPositions.keyAt(i));

						switch (item.getItemId()) {
						case R.id.option_delete:
							adapter.animateDismiss(checkedPositions.keyAt(i));
							break;
						case R.id.option_edit: {
							Intent intent = new Intent(fragment.getActivity(), ItemContainerEditActivity.class);
							intent.putExtra(ItemContainerEditFragment.INTENT_ITEM_CHOOSER_ID, itemContainer.getId());
							fragment.startActivity(intent);
							mode.finish();
							return true;
						}
						case R.id.option_add: {
							Intent intent = new Intent(fragment.getActivity(), ItemContainerEditActivity.class);
							fragment.startActivity(intent);
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

			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						ItemContainer itemContainer = (ItemContainer) list.getItemAtPosition(checkedPositions.keyAt(i));

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

		ItemContainer set1 = new ItemContainer(0, "Set I");
		set1.setIconUri(Util.getUriForResourceId(Util.getThemeResourceId(getActivity(), R.attr.imgBarSet1)));

		ItemContainer set2 = new ItemContainer(1, "Set II");
		set2.setIconUri(Util.getUriForResourceId(Util.getThemeResourceId(getActivity(), R.attr.imgBarSet2)));

		ItemContainer set3 = new ItemContainer(2, "Set III");
		set3.setIconUri(Util.getUriForResourceId(Util.getThemeResourceId(getActivity(), R.attr.imgBarSet3)));

		containerAdapter = new ItemContainerAdapter(getActivity(), getHero().getItemContainers());
		containerAdapter.insert(set1, 0);
		containerAdapter.insert(set2, 1);
		containerAdapter.insert(set3, 2);

		animateAdapter = new AnimateAdapter<ItemContainer>(containerAdapter, this);
		animateAdapter.setAbsListView(containerList);
		containerList.setAdapter(animateAdapter);

		mContainerCallback = new ItemsContainerActionMode(this, containerList, animateAdapter);

		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		int screen = pref.getInt(PREF_KEY_LAST_OPEN_SCREEN, 0);

		showScreen(screen);
	}

	@Override
	public void onDismiss(AbsListView list, int[] positions) {
		for (int pos : positions) {
			ItemContainer container = containerAdapter.remove(pos);
			getHero().removeItemContainer(container);
		}
	}

	@Override
	public void onShow(AbsListView list, int[] positions) {

	}

	private void showItemPopup() {
		if (itemChooserDialog == null) {
			itemChooserDialog = new ItemChooserDialog(getActivity());
			itemChooserDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Item item = itemChooserDialog.getItem(position);

					if (item != null) {
						item = item.duplicate();
						if (isSetIndex(mCurrentScreen)) {
							getHero().addEquippedItem(getBaseActivity(), item, null, null, getActiveSet());
						} else {
							item.setScreen(mCurrentScreen);
							getHero().addItem(item);
						}
					}
					itemChooserDialog.dismiss();
				}
			});
		}

		if (itemAdapter.getFilter().getTypes() != null && !itemAdapter.getFilter().getTypes().isEmpty()) {
			itemChooserDialog.setItemTypes(itemAdapter.getFilter().getTypes());
		}

		itemChooserDialog.show();
	}

	public static boolean isSetIndex(int index) {
		return index >= 0 && index < Hero.MAXIMUM_SET_NUMBER;
	}

	protected int getActiveSet() {
		if (!isSetIndex(mCurrentScreen))
			return -1;
		else
			return mCurrentScreen;
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
		inflater.inflate(R.menu.item_grid_menu, menu);

		SubMenu gridSet = menu.findItem(R.id.option_itemgrid_set).getSubMenu();

		for (ItemContainer itemContainer : getHero().getItemContainers()) {
			gridSet.add(MENU_CONTAINER_GROUP, itemContainer.getId(), Menu.NONE, itemContainer.getName()).setIcon(
					Util.getDrawableByUri(itemContainer.getIconUri()));
		}
		updateActionBarIcons(menu, mCurrentScreen);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onPrepareOptionsMenu(com. actionbarsherlock.view.Menu)
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		SubMenu gridSet = menu.findItem(R.id.option_itemgrid_set).getSubMenu();

		for (int i = gridSet.size() - 1; i >= 3; i--) {
			MenuItem item = gridSet.getItem(i);
			gridSet.removeItem(item.getItemId());
		}

		for (ItemContainer itemContainer : getHero().getItemContainers()) {
			if (gridSet.findItem(itemContainer.getId()) == null) {
				gridSet.add(MENU_CONTAINER_GROUP, itemContainer.getId(), Menu.NONE, itemContainer.getName()).setIcon(
						Util.getDrawableByUri(itemContainer.getIconUri()));
			}
		}
		updateActionBarIcons(menu, mCurrentScreen);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com. actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.option_item_add_table:
			showItemPopup();
			return true;
		case R.id.option_itemgrid_set1:
			showScreen(0);
			getSherlockActivity().supportInvalidateOptionsMenu();
			return true;
		case R.id.option_itemgrid_set2:
			showScreen(1);
			getSherlockActivity().supportInvalidateOptionsMenu();
			return true;
		case R.id.option_itemgrid_set3:
			showScreen(2);
			getSherlockActivity().supportInvalidateOptionsMenu();
			return true;
		default:
			if (item.getGroupId() == MENU_CONTAINER_GROUP) {

				ItemContainer itemContainer = getHero().getItemContainer(item.getItemId());
				int index = containerAdapter.indexOf(itemContainer);
				showScreen(index);
				getSherlockActivity().supportInvalidateOptionsMenu();
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_items_table, container, false));

		itemGridCompat = (GridViewCompat) root.findViewById(R.id.workspace);
		containerList = (ListView) root.findViewById(R.id.container_list);

		mItemCallback = new ItemsActionMode(this, itemGridCompat);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 192*288
		// 120*180

		itemAdapter = new GridItemAdapter(getActivity());
		itemGridCompat.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		itemGridCompat.setAdapter(itemAdapter);
		itemGridCompat.setOnItemClickListener(this);
		itemGridCompat.setOnItemLongClickListener(this);

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
				showScreen(position);
				if (mMode == null) {
					if (mContainerCallback != null) {
						mMode = ((SherlockFragmentActivity) getActivity()).startActionMode(mContainerCallback);
						customizeActionModeCloseButton();
						mMode.invalidate();
					} else {
						return false;
					}
				} else {
					mMode.invalidate();
				}
				return false;
			}
		});

		super.onActivityCreated(savedInstanceState);
	}

	protected Callback getActionModeCallback(List<Object> objects) {
		if (objects == null || objects.isEmpty())
			return null;

		for (Object obj : objects) {
			if (obj instanceof ItemContainer) {
				return mContainerCallback;
			} else if (obj instanceof ItemCard) {
				return mItemCallback;
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
		if (parent == containerList) {
			if (mMode != null) {
				mMode.invalidate();
			}
			containerList.setItemChecked(position, true);
			showScreen(position);
		} else {
			if (mMode == null) {
				itemGridCompat.setItemChecked(position, false);
				ItemViewActivity.view(getActivity(), getHero(), itemAdapter.getItem(position));
			} else {
				super.onItemClick(parent, view, position, id);
			}
		}
	}

	private void updateActionBarIcons(Menu menu, int containerId) {
		MenuItem item = menu.findItem(R.id.option_itemgrid_set);

		switch (containerId) {
		case 0:
			item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarSet1));
			item.setTitle("Set");
			break;
		case 1:
			item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarSet2));
			item.setTitle("Set");
			break;
		case 2:
			item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarSet3));
			item.setTitle("Set");
			break;
		default:
			ItemContainer itemContainer = getHero().getItemContainer(containerId);
			if (itemContainer != null) {
				item.setIcon(Util.getDrawableByUri(itemContainer.getIconUri()));
				item.setTitle(itemContainer.getName());
			}
			break;
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
		edit.putInt(PREF_KEY_LAST_OPEN_SCREEN, mCurrentScreen);
		edit.commit();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showScreen(int index) {
		if (getHero() != null && index >= 0) {
			if (mMode != null) {
				mMode.finish();
			}
			itemAdapter.setNotifyOnChange(false);
			itemAdapter.clear();
			itemGridCompat.clearChoices();

			if (index < 0 || index >= containerAdapter.getCount())
				index = 0;

			if (isSetIndex(index)) {
				mCurrentScreen = index;
				for (EquippedItem item : getHero().getEquippedItems(index)) {
					itemAdapter.add(item);
				}
			} else {
				ItemContainer itemContainer = containerAdapter.getItem(index);
				mCurrentScreen = itemContainer.getId();
				for (Item item : itemContainer.getItems()) {
					itemAdapter.add(item);
				}
			}

			itemAdapter.setNotifyOnChange(true);
			itemAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

			containerList.setItemChecked(index, true);

			getSherlockActivity().supportInvalidateOptionsMenu();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemAdded(com.dsatab .data.items.Item)
	 */
	@Override
	public void onItemAdded(Item item) {
		if (item.getScreen() == mCurrentScreen) {
			// skip items that are equippable since they will be equipped using
			// a onItemEquipped Event. this would cause duplicates
			if (item.isEquipable() && isSetIndex(mCurrentScreen))
				return;

			itemAdapter.add(item);
			itemAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
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
		if (item.getSet() == mCurrentScreen) {
			itemAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroChangedListener#onItemChanged(com.dsatab .data.items.Item)
	 */
	@Override
	public void onItemChanged(Item item) {
		if (item.getScreen() == mCurrentScreen) {
			itemAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemRemoved(com.dsatab .data.items.Item)
	 */
	@Override
	public void onItemRemoved(Item item) {
		if (item.getScreen() == mCurrentScreen) {
			itemAdapter.remove(item);
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
		if (item.getSet() == mCurrentScreen) {
			itemAdapter.add(item);
			itemAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemUnequipped(com .dsatab.data.items.EquippedItem)
	 */
	@Override
	public void onItemUnequipped(EquippedItem item) {
		if (item.getSet() == mCurrentScreen) {
			itemAdapter.remove(item);
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
		containerAdapter.add(itemContainer);
		getSherlockActivity().supportInvalidateOptionsMenu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerRemoved
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerRemoved(ItemContainer itemContainer) {
		containerAdapter.remove(itemContainer);
		getSherlockActivity().supportInvalidateOptionsMenu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerChanged
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerChanged(ItemContainer itemContainer) {
		containerAdapter.notifyDataSetChanged();
	}

}
