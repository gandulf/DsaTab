package com.dsatab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import com.dsatab.view.ListSettings;
import com.gandulf.guilib.util.ListViewCompat;
import com.gandulf.guilib.view.DynamicListViewEx.OnItemCheckedListener;

public abstract class BaseListFragment extends BaseFragment implements OnItemLongClickListener, OnItemClickListener,
		OnItemCheckedListener, View.OnClickListener {

	protected ActionMode mMode;

	protected ActionMode.Callback mCallback;

	protected ListSettings listSettings;

	protected ListSettings getListSettings() {
		if (listSettings == null) {
			if (getTabPosition() >= 0 && getTabInfo() != null) {
				listSettings = getTabInfo().getListSettings(getTabPosition());
			}
		}
		return listSettings;
	}

	@Override
	public void onItemChecked(AdapterView<?> parent, int position, boolean value) {
		onCheckedChanged(parent);
	}

	protected void onCheckedChanged(AdapterView<?> parent) {
		List<Object> checkedObjects = new ArrayList<Object>();

		SparseBooleanArray checked = ListViewCompat.getCheckedItemPositions(parent);

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
					mMode = ((ActionBarActivity) getActivity()).startSupportActionMode(callback);
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
		ListViewCompat.setItemChecked(parent, position, !ListViewCompat.isItemChecked(parent, position));

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

				View list = getListView();
				if (list != null) {
					list.setVisibility(View.GONE);
				}
			} else {
				emptyView.setVisibility(View.GONE);
				View list = getListView();
				if (list != null) {
					list.setVisibility(View.VISIBLE);
				}
			}
			emptyView.setOnClickListener(this);
		}

	}

	protected View getListView() {
		return findViewById(android.R.id.list);
	}

	protected void refreshEmptyView(Adapter adapter) {
		refreshEmptyView(adapter, null);
	}

}
