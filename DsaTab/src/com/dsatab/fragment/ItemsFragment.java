package com.dsatab.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.rokoder.android.lib.support.v4.widget.GridViewCompat;

public class ItemsFragment extends BaseListFragment implements OnItemClickListener, HeroInventoryChangedListener {

	private static final int MENU_CONTAINER_GROUP = 99;

	private static final String PREF_KEY_LAST_OPEN_SCREEN = "_lastopenscreen";

	private ListView containerList;
	private ItemContainerAdapter containerAdapter;

	private GridViewCompat itemGridCompat;

	private GridItemAdapter itemAdapter;

	private int mCurrentScreen = -1;

	private ItemChooserDialog itemChooserDialog;

	protected ActionMode mContainerMode;
	protected Callback mContainerCallback;

	private final class ItemsActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = itemGridCompat.getCheckedItemPositionsC();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Item selectedItem = itemAdapter.getItem(checkedPositions.keyAt(i)).getItem();

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
								ItemViewActivity.view(getActivity(), getHero(), selectedItem);
								mode.finish();
								return true;
							case R.id.option_edit:
								ItemEditActivity.edit(getActivity(), getHero(), selectedItem);
								mode.finish();
								return true;
							case R.id.option_equipped:
								return false;
							case R.id.option_move:
								return false;
							case R.id.option_equipped_set1:
								getHero().addEquippedItem(getActivity(), selectedItem, null, null, 0);
								break;
							case R.id.option_equipped_set2:
								getHero().addEquippedItem(getActivity(), selectedItem, null, null, 1);
								break;
							case R.id.option_equipped_set3:
								getHero().addEquippedItem(getActivity(), selectedItem, null, null, 2);
								break;
							}
						}

					}

				}
				if (notifyChanged) {
					itemAdapter.notifyDataSetChanged();
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.item_list_popupmenu, menu);

			com.actionbarsherlock.view.MenuItem move = menu.findItem(R.id.option_move);
			if (isSetIndex(mCurrentScreen)) {
				move.setVisible(false);
			} else {
				move.setVisible(true);
				SubMenu moveMenu = move.getSubMenu();

				for (ItemContainer itemContainer : getHero().getItemContainers()) {
					moveMenu.add(MENU_CONTAINER_GROUP, itemContainer.getId(), Menu.NONE, itemContainer.getName())
							.setIcon(Util.getDrawableByUri(itemContainer.getIconUri()));
				}
			}

			mode.setTitle("Ausrüstung");
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			itemGridCompat.clearChoicesC();
			itemAdapter.notifyDataSetChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode
		 * (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			SparseBooleanArray checkedPositions = itemGridCompat.getCheckedItemPositionsC();
			int selected = 0;
			boolean isEquippable = true;
			boolean changed = false;
			com.actionbarsherlock.view.MenuItem view = menu.findItem(R.id.option_view);
			com.actionbarsherlock.view.MenuItem equipped = menu.findItem(R.id.option_equipped);
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Item selectedItem = itemAdapter.getItem(checkedPositions.keyAt(i)).getItem();
						selected++;

						isEquippable &= selectedItem.isEquipable();
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
			return changed;
		}
	}

	private final class ItemsContainerActionMode implements ActionMode.Callback {

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = containerList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						ItemContainer itemContainer = (ItemContainer) containerList.getItemAtPosition(checkedPositions
								.keyAt(i));

						switch (item.getItemId()) {
						case R.id.option_delete:
							getHero().removeItemContainer(itemContainer);
							notifyChanged = true;
							break;
						case R.id.option_edit: {
							Intent intent = new Intent(getActivity(), ItemContainerEditActivity.class);
							intent.putExtra(ItemContainerEditFragment.INTENT_ITEM_CHOOSER_ID, itemContainer.getId());
							startActivity(intent);
							mode.finish();
							return true;
						}
						case R.id.option_add: {
							Intent intent = new Intent(getActivity(), ItemContainerEditActivity.class);
							startActivity(intent);
							mode.finish();
							return true;
						}
						}
					}

				}
				if (notifyChanged) {
					updateContainerList();
					containerAdapter.notifyDataSetChanged();
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
			mContainerMode = null;
			containerAdapter.notifyDataSetChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode
		 * (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			SparseBooleanArray checkedPositions = containerList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						ItemContainer itemContainer = (ItemContainer) containerList.getItemAtPosition(checkedPositions
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

		updateContainerList();

	}

	/**
	 * 
	 */
	private void updateContainerList() {
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
		containerList.setAdapter(containerAdapter);

		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		int screen = pref.getInt(PREF_KEY_LAST_OPEN_SCREEN, 0);

		showScreen(screen);
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

	private int getActiveSet() {
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

		mCallback = new ItemsActionMode();
		mContainerCallback = new ItemsContainerActionMode();
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
			getSherlockActivity().invalidateOptionsMenu();
			return true;
		case R.id.option_itemgrid_set2:
			showScreen(1);
			getSherlockActivity().invalidateOptionsMenu();
			return true;
		case R.id.option_itemgrid_set3:
			showScreen(2);
			getSherlockActivity().invalidateOptionsMenu();
			return true;
		default:
			if (item.getGroupId() == MENU_CONTAINER_GROUP) {
				showScreen(item.getItemId());
				getSherlockActivity().invalidateOptionsMenu();
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

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 192*288
		// 120*180

		itemAdapter = new GridItemAdapter(getActivity());
		itemGridCompat.setChoiceModeC(AbsListView.CHOICE_MODE_MULTIPLE);
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
				if (mContainerMode == null) {
					if (mContainerCallback != null) {
						mContainerMode = ((SherlockFragmentActivity) getActivity()).startActionMode(mContainerCallback);
						customizeActionModeCloseButton();
						mContainerMode.invalidate();
					} else {
						return false;
					}
				} else {
					mContainerMode.invalidate();
				}
				return false;
			}
		});

		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View,
	 * int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == containerList) {
			if (mContainerMode != null) {
				mContainerMode.invalidate();
			}
			containerList.setItemChecked(position, true);
			showScreen(position);
		} else {
			if (mMode == null) {
				itemGridCompat.setItemCheckedC(position, false);
				ItemViewActivity.view(getActivity(), getHero(), itemAdapter.getItem(position));
			} else {
				super.onItemClick(parent, view, position, id);
			}
		}
	}

	private void updateActionBarIcons(Menu menu, int newScreen) {
		MenuItem item = menu.findItem(R.id.option_itemgrid_set);

		switch (newScreen) {
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
			ItemContainer itemContainer = getHero().getItemContainer(newScreen);
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

	private void showScreen(int screen) {
		if (getHero() != null && screen >= 0) {
			if (mMode != null) {
				mMode.finish();
			}
			itemAdapter.setNotifyOnChange(false);
			itemAdapter.clear();
			itemGridCompat.clearChoicesC();

			if (isSetIndex(screen)) {
				mCurrentScreen = screen;
				for (EquippedItem item : getHero().getEquippedItems(screen)) {
					itemAdapter.add(item);
				}
			} else {
				ItemContainer itemContainer = getHero().getItemContainer(screen);
				if (itemContainer == null) {
					itemContainer = getHero().getItemContainers().get(0);
				}
				mCurrentScreen = itemContainer.getId();
				for (Item item : itemContainer.getItems()) {
					itemAdapter.add(item);
				}
			}

			itemAdapter.setNotifyOnChange(true);
			itemAdapter.sort(ItemCard.CELL_NUMBER_COMPARATOR);

			containerList.setItemChecked(mCurrentScreen, true);

			getSherlockActivity().invalidateOptionsMenu();
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
