package com.dsatab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.FilterType;
import com.dsatab.view.listener.FilterChangedListener;
import com.gandulf.guilib.util.Debug;
import com.haarman.listviewanimations.view.OnItemCheckedListener;
import com.rokoder.android.lib.support.v4.widget.GridViewCompat;

public abstract class BaseListFragment extends BaseFragment implements OnItemLongClickListener, OnItemClickListener,
		FilterChangedListener, OnItemCheckedListener, View.OnClickListener {

	protected ActionMode mMode;

	protected ActionMode.Callback mCallback;

	protected ListSettings listSettings;

	protected ListSettings getListSettings() {
		if (listSettings == null) {
			if (getTabPosition() >= 0 && getTabInfo() != null) {
				// update filter settings to be correct type just to be sure
				getTabInfo().updateListSettings();
				listSettings = getTabInfo().getListSettings(getTabPosition());
			}
		}
		return listSettings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.FilterChangedListener#onFilterChanged(com.dsatab. view.FilterSettings.FilterType,
	 * com.dsatab.view.FilterSettings)
	 */
	@Override
	public void onFilterChanged(FilterType type, ListSettings settings) {

	}

	@Override
	public void onItemChecked(AdapterView<?> parent, int position, boolean value) {
		onCheckedChanged(parent);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void onCheckedChanged(AdapterView<?> parent) {
		List<Object> checkedObjects = new ArrayList<Object>();

		SparseBooleanArray checked = null;
		if (parent instanceof GridViewCompat)
			checked = ((GridViewCompat) parent).getCheckedItemPositions();
		else if (parent instanceof ListView) {
			checked = ((ListView) parent).getCheckedItemPositions();
		} else if (parent instanceof GridView) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				checked = ((GridView) parent).getCheckedItemPositions();
			} else {
				Debug.warning("Using GridView with checked items does not work before honeycomb use gridviewcompat!");
			}
		}

		boolean hasCheckedElement = false;
		if (checked != null) {
			for (int i = 0; i < checked.size() && !hasCheckedElement; i++) {
				hasCheckedElement = checked.valueAt(i);
				checkedObjects.add(parent.getItemAtPosition(checked.keyAt(i)));
			}
		}

		if (hasCheckedElement) {
			if (mMode == null) {
				Callback callback = getActionModeCallback(checkedObjects);
				if (callback != null) {
					mMode = ((SherlockFragmentActivity) getActivity()).startActionMode(callback);
					customizeActionModeCloseButton();
					mMode.invalidate();
				}
			} else {
				mMode.invalidate();
			}
		} else {
			if (mMode != null) {
				mMode.finish();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View,
	 * int, long)
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (mMode != null) {
			onCheckedChanged(parent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#setUserVisibleHint(boolean)
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser && mMode != null) {
			mMode.finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android .widget.AdapterView,
	 * android.view.View, int, long)
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		GridViewCompat gridViewCompat = null;
		if (parent instanceof GridViewCompat) {
			gridViewCompat = (GridViewCompat) parent;
		}

		if (parent instanceof GridViewCompat)
			((GridViewCompat) parent).setItemChecked(position, !gridViewCompat.isItemChecked(position));
		else if (parent instanceof ListView) {
			((ListView) parent).setItemChecked(position, !((ListView) parent).isItemChecked(position));
		} else if (parent instanceof GridView) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				((GridView) parent).setItemChecked(position, !((GridView) parent).isItemChecked(position));
			} else {
				Debug.warning("Using GridView with checked items does not work before honeycomb use gridviewcompat!");
			}
		}

		onCheckedChanged(parent);

		return true;
	}

	protected Callback getActionModeCallback(List<Object> objects) {
		return mCallback;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case android.R.id.empty:
			removeTab();
			break;
		}

	}

	protected void refreshEmptyView(Adapter adapter, String emptyText) {
		View emptyView = findViewById(android.R.id.empty);
		if (emptyView != null) {
			if (adapter.isEmpty()) {
				emptyView.setVisibility(View.VISIBLE);
				if (emptyText != null && emptyView instanceof TextView) {
					((TextView) emptyView).setText(emptyText);
				}
				findViewById(android.R.id.list).setVisibility(View.GONE);
			} else {
				emptyView.setVisibility(View.GONE);
				findViewById(android.R.id.list).setVisibility(View.VISIBLE);
			}
			emptyView.setOnClickListener(this);
		}

	}

	protected void refreshEmptyView(Adapter adapter) {
		refreshEmptyView(adapter, null);
	}

}
