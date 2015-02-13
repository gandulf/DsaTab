package com.dsatab.fragment.item;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.text.Editable;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.dsatab.R;
import com.dsatab.activity.ItemsActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.ItemCursorAdapter;
import com.dsatab.data.adapter.ItemTypeAdapter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.db.DataManager;
import com.dsatab.fragment.BaseListFragment;
import com.dsatab.util.Util;
import com.gandulf.guilib.util.DefaultTextWatcher;
import com.gandulf.guilib.util.ListViewCompat;
import com.gandulf.guilib.view.DynamicListViewEx;

public class ItemListFragment extends BaseListFragment implements OnItemClickListener, LoaderCallbacks<Cursor> {

	public interface OnItemSelectedListener {
		public boolean onItemSelected(Item item);
	}

	private ItemCursorAdapter itemAdapter = null;

	private DynamicListViewEx itemList;

	private Collection<ItemType> itemTypes = null;
	private String constraint, category;

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

	protected static final class ItemActionMode implements ActionMode.Callback {

		private WeakReference<ListView> listView;
		private WeakReference<ItemListFragment> listFragment;

		public ItemActionMode(ItemListFragment fragment, ListView listView) {
			this.listFragment = new WeakReference<ItemListFragment>(fragment);
			this.listView = new WeakReference<ListView>(listView);
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
			final ListView list = listView.get();
			final ItemListFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = ListViewCompat.getCheckedItemPositions(list);
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = list.getItemAtPosition(checkedPositions.keyAt(i));

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
				}
				if (notifyChanged) {
					fragment.refresh();
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.menuitem_delete, menu);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			ListView list = listView.get();
			ItemListFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return;

			fragment.mMode = null;
			list.clearChoices();

			Util.notifyDatasetChanged(list);
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			ListView list = listView.get();
			ItemListFragment fragment = listFragment.get();
			if (list == null || fragment == null)
				return false;

			SparseBooleanArray checkedPositions = ListViewCompat.getCheckedItemPositions(list);
			int selected = 0;
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						selected++;
						// Object obj = list.getItemAtPosition(checkedPositions.keyAt(i));
					}
				}
			}

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

		itemTypes = new HashSet<ItemType>();
		itemAdapter = new ItemCursorAdapter(getActivity(), null);

		getActivity().getLoaderManager().initLoader(0, null, this);
	}

	public String getHeroKey() {
		if (getActivity() != null && getActivity().getIntent() != null)
			return getActivity().getIntent().getStringExtra(ItemsActivity.INTENT_EXTRA_HERO_KEY);
		else
			return null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (mMode == null) {
			itemList.setItemChecked(position, false);
			Item item = DataManager.getItemByCursor((Cursor) itemAdapter.getItem(position));
			itemSelectedListener.onItemSelected(item);
		} else {
			super.onItemClick(parent, v, position, id);
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

		itemList = (DynamicListViewEx) root.findViewById(android.R.id.list);
		itemList.setOnItemCheckedListener(this);
		itemList.setOnItemLongClickListener(this);
		itemList.setOnItemClickListener(this);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		ActionBar actionBar = getActionBarActivity().getSupportActionBar();

		final ItemTypeAdapter adapter = new ItemTypeAdapter(actionBar.getThemedContext(),
				android.R.layout.simple_spinner_item, Arrays.asList(ItemType.values()));
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				ItemType cardType = adapter.getItem(itemPosition);
				itemTypes.clear();
				if (cardType != null) {
					itemTypes.add(cardType);
				}
				filter(itemTypes, null, null);
				return false;
			}
		});

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
		itemList.setAdapter(itemAdapter);
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

		MenuItem item = menu.add(Menu.NONE, R.id.option_search, Menu.NONE, "Suche");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarSearch));

		final AutoCompleteTextView searchView = new AutoCompleteTextView(getActionBarActivity().getSupportActionBar()
				.getThemedContext());
		searchView.setCompoundDrawablesWithIntrinsicBounds(Util.getThemeResourceId(getActivity(), R.attr.imgBarSearch),
				0, 0, 0);
		searchView.addTextChangedListener(new DefaultTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				filter(itemTypes, category, "%" + s.toString() + "%");
			}
		});
		searchView.setHint(R.string.filter);
		searchView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		MenuItemCompat.setActionView(item, searchView);
		// --

		inflater.inflate(R.menu.menuitem_add_items, menu);

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
		if (item.getItemId() == R.id.option_add) {

			if (getActivity() instanceof ItemsActivity) {
				ItemsActivity itemsActivity = (ItemsActivity) getActivity();
				itemsActivity.editItem(null, null);
			} else {
				ItemsActivity.insert(getActivity(), getHeroKey(), ItemsActivity.ACTION_CREATE);
			}
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	protected void filter(Collection<ItemType> type, String category, String constraint) {
		if (itemAdapter != null) {
			itemAdapter.changeCursor(DataManager.getItemsCursor(constraint, type, category));
		}
	}

	public void refresh() {
		if (getActivity() != null) {
			getActivity().getLoaderManager().restartLoader(0, null, this);
		}
	}

	public Collection<ItemType> getItemTypes() {
		return itemTypes;
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
