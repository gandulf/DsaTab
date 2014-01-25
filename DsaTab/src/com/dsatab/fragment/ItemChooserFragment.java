package com.dsatab.fragment;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.ItemEditActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.ItemCursorAdapter;
import com.dsatab.data.adapter.ItemTypeAdapter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.util.Util;
import com.dsatab.xml.DataManager;
import com.gandulf.guilib.util.DefaultTextWatcher;
import com.haarman.listviewanimations.view.DynamicListView;

public class ItemChooserFragment extends BaseListFragment implements TabListener, OnItemClickListener {

	private ItemCursorAdapter itemAdapter = null;

	private DynamicListView itemList;

	private Collection<ItemType> itemTypes = null;

	private OnItemClickListener itemClickListener;

	protected static final class ItemActionMode implements ActionMode.Callback {

		private WeakReference<ListView> listView;
		private WeakReference<ItemChooserFragment> listFragment;

		public ItemActionMode(ItemChooserFragment fragment, ListView listView) {
			this.listFragment = new WeakReference<ItemChooserFragment>(fragment);
			this.listView = new WeakReference<ListView>(listView);
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem menuItem) {
			final ListView list = listView.get();
			final ItemChooserFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = list.getItemAtPosition(checkedPositions.keyAt(i));

						if (obj instanceof Cursor) {

							final Item item = DataManager.getItemByCursor((Cursor) obj);

							switch (menuItem.getItemId()) {
							case R.id.option_edit:
								ItemEditActivity.edit(fragment.getActivity(), getHero(), item);
								break;
							case R.id.option_delete: {
								DataManager.deleteItem(item);
								notifyChanged = true;
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

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.menuitem_edit, menu);
			mode.getMenuInflater().inflate(R.menu.menuitem_delete, menu);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			ListView list = listView.get();
			ItemChooserFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return;

			fragment.mMode = null;
			list.clearChoices();

			Util.notifyDatasetChanged(list);
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			ListView list = listView.get();
			ItemChooserFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			int selected = 0;
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						selected++;
						// Object obj = list.getItemAtPosition(checkedPositions.keyAt(i));
					}
				}
			}

			menu.findItem(R.id.option_edit).setEnabled(selected == 1);

			return true;
		}

		protected Hero getHero() {
			return DsaTabApplication.getInstance().getHero();
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

		itemTypes = new HashSet<ItemType>();

		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		final ItemTypeAdapter adapter = new ItemTypeAdapter(actionBar.getThemedContext(),
				android.R.layout.simple_spinner_item, Arrays.asList(ItemType.values()));
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				ItemType cardType = adapter.getItem(itemPosition);
				if (cardType != null) {
					filter(Arrays.asList(cardType), null, null);
				} else {
					filter(null, null, null);
				}
				return false;
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (mMode == null) {
			itemList.setItemChecked(position, false);
			itemClickListener.onItemClick(parent, v, position, id);
		} else {
			super.onItemClick(parent, v, position, id);
		}

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getTag() instanceof ItemType) {
			ItemType cardType = (ItemType) tab.getTag();
			filter(Arrays.asList(cardType), null, null);
		} else {
			filter(null, null, null);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.sheet_item_chooser, container, false);

		itemList = (DynamicListView) root.findViewById(android.R.id.list);
		itemList.setOnItemCheckedListener(this);
		itemList.setOnItemLongClickListener(this);
		itemList.setOnItemClickListener(this);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		ItemType itemType = null;
		if (!itemTypes.isEmpty()) {
			itemType = itemTypes.iterator().next();
		}

		final int tabCount = actionBar.getTabCount();
		for (int i = 0; i < tabCount; i++) {

			Tab tab = actionBar.getTabAt(i);
			if (itemType == null && tab.getTag() == null) {
				actionBar.selectTab(tab);
				break;
			} else if (itemType != null && itemType.equals(tab.getTag())) {
				actionBar.selectTab(tab);
				break;
			}

		}

		mCallback = new ItemActionMode(this, itemList);
		itemList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		super.onActivityCreated(savedInstanceState);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com. actionbarsherlock.view.Menu,
	 * com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		com.actionbarsherlock.view.MenuItem item = menu.add(Menu.NONE, R.id.option_search, Menu.NONE, "Suche");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarSearch));

		final AutoCompleteTextView searchView = new AutoCompleteTextView(getSherlockActivity().getSupportActionBar()
				.getThemedContext());
		searchView.setCompoundDrawablesWithIntrinsicBounds(Util.getThemeResourceId(getActivity(), R.attr.imgBarSearch),
				0, 0, 0);
		searchView.addTextChangedListener(new DefaultTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				filter(itemTypes, null, "%" + s.toString() + "%");
			}
		});
		searchView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		item.setActionView(searchView);

		// --

		inflater.inflate(R.menu.menuitem_add, menu);

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem )
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.option_add) {
			ItemEditActivity.create(getActivity(), null);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		itemAdapter = new ItemCursorAdapter(getActivity(), DataManager.getItemsCursor(null, itemTypes, null));
		itemList.setAdapter(itemAdapter);

		super.onStart();
	}

	@Override
	public void onStop() {

		if (itemAdapter.getCursor() != null) {
			itemAdapter.getCursor().close();
		}

		super.onStop();
	}

	protected void filter(Collection<ItemType> type, String category, String constraint) {
		if (itemAdapter != null) {
			itemAdapter.changeCursor(DataManager.getItemsCursor(constraint, type, category));
		}
	}

	public Item getItem(int position) {
		Cursor cursor = (Cursor) itemAdapter.getItem(position);
		return DataManager.getItemByCursor(cursor);
	}

	public Collection<ItemType> getItemTypes() {
		return itemTypes;
	}

	public void setItemTypes(Collection<ItemType> itemType) {
		this.itemTypes = itemType;
	}

	public AdapterView.OnItemClickListener getOnItemClickListener() {
		return itemClickListener;
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		itemClickListener = onItemClickListener;
	}

}
