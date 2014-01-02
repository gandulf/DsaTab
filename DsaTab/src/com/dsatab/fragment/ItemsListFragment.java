package com.dsatab.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.SubMenu;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.ItemEditActivity;
import com.dsatab.activity.ItemViewActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.ExpandableItemAdapter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;
import com.dsatab.view.ItemChooserDialog;
import com.dsatab.view.listener.HeroInventoryChangedListener;

public class ItemsListFragment extends BaseListFragment implements OnItemClickListener,
		DialogInterface.OnMultiChoiceClickListener, HeroInventoryChangedListener {

	private static final int MENU_CONTAINER_GROUP = 99;

	private static final String PREF_KEY_GROUP_EXPANDED = "ITEM_GROUP_EXPANDED";

	private ExpandableListView itemList;

	private ExpandableItemAdapter itemAdapter;

	private ItemChooserDialog itemChooserDialog;

	private Set<ItemType> categoriesSelected;
	private ItemType[] categories;

	private static final class ItemsActionMode implements ActionMode.Callback {

		private WeakReference<ListView> listView;
		private WeakReference<BaseListFragment> listFragment;

		public ItemsActionMode(BaseListFragment fragment, ListView listView) {
			this.listFragment = new WeakReference<BaseListFragment>(fragment);
			this.listView = new WeakReference<ListView>(listView);
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;

			final ListView list = listView.get();
			final BaseListFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						Object obj = list.getItemAtPosition(checkedPositions.keyAt(i));
						if (obj instanceof Item) {
							Item selectedItem = (Item) obj;

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

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			ListView list = listView.get();
			BaseListFragment fragment = listFragment.get();
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
			final BaseListFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			int selected = 0;
			boolean isEquippable = true;
			boolean changed = false;
			com.actionbarsherlock.view.MenuItem view = menu.findItem(R.id.option_view);
			com.actionbarsherlock.view.MenuItem equipped = menu.findItem(R.id.option_equipped);
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = list.getItemAtPosition(checkedPositions.keyAt(i));
						if (obj instanceof Item) {
							Item selectedItem = (Item) obj;
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
		fillBodyItems(hero);
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
		inflater.inflate(R.menu.item_list_menu, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com. actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == R.id.option_item_add_list) {
			showItemPopup();
			return true;
		} else if (item.getItemId() == R.id.option_item_filter) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			String[] categoryNames = new String[categories.length];
			boolean[] categoriesSet = new boolean[categories.length];

			for (int i = 0; i < categories.length; i++) {
				categoryNames[i] = categories[i].name();
				if (categoriesSelected.contains(categories[i]))
					categoriesSet[i] = true;
			}

			builder.setMultiChoiceItems(categoryNames, categoriesSet, this);
			builder.setTitle("Filtern");
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					itemAdapter.filter(new ArrayList<ItemType>(categoriesSelected), null, null);
				}
			});
			return true;

		} else {
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
		View root = configureContainerView(inflater.inflate(R.layout.sheet_items_list, container, false));

		itemList = (ExpandableListView) root.findViewById(android.R.id.list);

		mCallback = new ItemsActionMode(this, itemList);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		itemList.setOnItemLongClickListener(this);
		itemList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		itemList.setGroupIndicator(getResources().getDrawable(
				Util.getThemeResourceId(getActivity(), R.attr.imgExpander)));
		itemList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				if (mMode == null) {
					// Talent talent = talentAdapter.getChild(groupPosition,
					// childPosition);
					// getBaseActivity().checkProbe(talent);
				} else {
					int pos = itemList.getPositionForView(v);
					itemList.setItemChecked(pos, !itemList.isItemChecked(pos));
					ItemsListFragment.this.onItemClick(parent, v, pos, id);
				}
				return true;
			}
		});
		itemList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				Editor edit = preferences.edit();
				edit.putBoolean(PREF_KEY_GROUP_EXPANDED + groupPosition, false);
				edit.commit();
			}
		});
		itemList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				Editor edit = preferences.edit();
				edit.putBoolean(PREF_KEY_GROUP_EXPANDED + groupPosition, true);
				edit.commit();
			}
		});

		categories = ItemType.values();
		categoriesSelected = new HashSet<ItemType>(Arrays.asList(categories));

		super.onActivityCreated(savedInstanceState);
	}

	private void fillBodyItems(Hero hero) {
		// itemAdapter = new EquippedItemListAdapter(getActivity(), getHero(),
		// getHero().getItems());
		itemAdapter = new ExpandableItemAdapter(getActivity(), getHero());
		itemList.setAdapter(itemAdapter);
		refreshEmptyView(itemAdapter);

		for (int i = 0; i < itemAdapter.getGroupCount(); i++) {
			if (preferences.getBoolean(PREF_KEY_GROUP_EXPANDED + i, true))
				itemList.expandGroup(i);
			else
				itemList.collapseGroup(i);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mMode == null) {
			itemList.setItemChecked(position, false);
		} else {
			super.onItemClick(parent, view, position, id);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.DialogInterface.OnMultiChoiceClickListener#onClick(android .content.DialogInterface, int,
	 * boolean)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		if (isChecked)
			categoriesSelected.add(categories[which]);
		else
			categoriesSelected.remove(categories[which]);
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
						getHero().addItem(item);
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
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemAdded(com.dsatab .data.items.Item)
	 */
	@Override
	public void onItemAdded(Item item) {
		// itemAdapter.add(item);
		itemAdapter.notifyDataSetChanged();
		refreshEmptyView(itemAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemChanged(com.dsatab .data.items.EquippedItem)
	 */
	@Override
	public void onItemChanged(EquippedItem item) {
		itemAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemRemoved(com.dsatab .data.items.Item)
	 */
	@Override
	public void onItemRemoved(Item item) {
		// itemAdapter.remove(item);
		itemAdapter.notifyDataSetChanged();
		refreshEmptyView(itemAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemEquipped(com. dsatab.data.items.EquippedItem)
	 */
	@Override
	public void onItemEquipped(EquippedItem item) {
		itemAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemUnequipped(com .dsatab.data.items.EquippedItem)
	 */
	@Override
	public void onItemUnequipped(EquippedItem item) {
		itemAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemChanged(com .dsatab.data.items.Item)
	 */
	@Override
	public void onItemChanged(Item item) {
		itemAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerAdded
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerAdded(ItemContainer itemContainer) {
		itemAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerRemoved
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerRemoved(ItemContainer itemContainer) {
		itemAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerChanged
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerChanged(ItemContainer itemContainer) {
		itemAdapter.notifyDataSetChanged();
	}

}
