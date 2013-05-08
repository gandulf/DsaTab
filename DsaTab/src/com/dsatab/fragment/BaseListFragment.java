/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.rokoder.android.lib.support.v4.widget.GridViewCompat;

/**
 * @author Ganymede
 * 
 */
public abstract class BaseListFragment extends BaseFragment implements OnItemLongClickListener, OnItemClickListener {

	protected ActionMode mMode;

	protected ActionMode.Callback mCallback;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (mMode != null) {

			GridViewCompat gridViewCompat = null;
			if (parent instanceof GridViewCompat) {
				gridViewCompat = (GridViewCompat) parent;
			}

			SparseBooleanArray checked = null;
			if (gridViewCompat != null)
				checked = gridViewCompat.getCheckedItemPositionsC();
			else if (parent instanceof ListView)
				checked = ((ListView) parent).getCheckedItemPositions();
			else if (parent instanceof GridView)
				checked = ((GridView) parent).getCheckedItemPositions();

			boolean hasCheckedElement = false;
			if (checked != null) {
				for (int i = 0; i < checked.size() && !hasCheckedElement; i++) {
					hasCheckedElement = checked.valueAt(i);
				}
			}

			if (hasCheckedElement) {
				mMode.invalidate();
			} else {
				mMode.finish();
			}
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
	 * @see
	 * android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android
	 * .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mCallback == null) {
			throw new IllegalArgumentException("ListView with Contextual Action Bar needs mCallback to be defined!");
		}

		GridViewCompat gridViewCompat = null;
		if (parent instanceof GridViewCompat) {
			gridViewCompat = (GridViewCompat) parent;
		}

		if (gridViewCompat != null)
			gridViewCompat.setItemCheckedC(position, !gridViewCompat.isItemCheckedC(position));
		else if (parent instanceof ListView) {
			((ListView) parent).setItemChecked(position, !((ListView) parent).isItemChecked(position));
		} else {
			// TODO define what todo with regular gridview
		}

		List<Object> checkedObjects = new ArrayList<Object>();

		SparseBooleanArray checked;
		if (gridViewCompat != null)
			checked = gridViewCompat.getCheckedItemPositionsC();
		else if (parent instanceof ListView) {
			checked = ((ListView) parent).getCheckedItemPositions();
		} else {
			checked = null;
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
				} else {
					return false;
				}
			} else {
				mMode.invalidate();
			}
		} else {
			if (mMode != null) {
				mMode.finish();
			}
		}
		return true;
	}

	protected Callback getActionModeCallback(List<Object> objects) {
		return mCallback;
	}

	protected void refreshEmptyView(Adapter adapter) {
		if (findViewById(android.R.id.empty) != null) {
			if (adapter.isEmpty()) {
				findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
				findViewById(android.R.id.list).setVisibility(View.GONE);
			} else {
				findViewById(android.R.id.empty).setVisibility(View.GONE);
				findViewById(android.R.id.list).setVisibility(View.VISIBLE);
			}
		}
	}

	protected void refreshEmptyView(ExpandableListAdapter adapter) {
		if (findViewById(android.R.id.empty) != null) {
			if (adapter.isEmpty()) {

				findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
				findViewById(android.R.id.list).setVisibility(View.GONE);

			} else {
				findViewById(android.R.id.empty).setVisibility(View.GONE);
				findViewById(android.R.id.list).setVisibility(View.VISIBLE);
			}
		}
	}
}
