package com.dsatab.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.View;
import android.widget.TextView;

import com.dsatab.data.adapter.OpenRecyclerAdapter;
import com.dsatab.view.ListSettings;
import com.h6ah4i.android.widget.advrecyclerview.selectable.RecyclerViewSelectionManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import java.util.List;

public abstract class BaseRecyclerFragment extends BaseFragment implements OpenRecyclerAdapter.EventListener,
		View.OnClickListener {

	protected RecyclerView recyclerView;
	protected RecyclerViewSelectionManager mRecyclerViewSelectionManager;

	protected ActionMode mMode;

	protected Callback mCallback;

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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mRecyclerViewSelectionManager = new RecyclerViewSelectionManager();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (mRecyclerViewSelectionManager != null) {
			mRecyclerViewSelectionManager.release();
			mRecyclerViewSelectionManager = null;
		}
	}

	@Override
	public void onItemSelected(int position, boolean value) {
		onCheckedChanged(recyclerView,mRecyclerViewSelectionManager);
	}

	protected void onCheckedChanged(RecyclerView view, RecyclerViewSelectionManager manager) {

		if (!manager.getSelectedItems().isEmpty()) {
			if (mMode == null) {
				Callback callback = getActionModeCallback(manager.getSelectedItems());
				if (callback != null) {
					mMode = recyclerView.startActionMode(callback);
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

	@Override
	public void onItemRemoved(int position) {

	}

	@Override
	public void onItemClicked(int position, View v) {
		if (mMode != null) {
			mRecyclerViewSelectionManager.toggleSelection(position);
			onCheckedChanged(recyclerView,mRecyclerViewSelectionManager);
		}
	}

	@Override
	public boolean onItemLongClicked(int position, View v) {
		mRecyclerViewSelectionManager.toggleSelection(position);

		onCheckedChanged(recyclerView, mRecyclerViewSelectionManager);
		return true;
	}

	/*
         * (non-Javadoc)
         *
         * @see android.app.Fragment#setUserVisibleHint(boolean)
         */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser && mMode != null) {
			mMode.finish();
		}
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

	protected void refreshEmptyView(RecyclerView.Adapter adapter, String emptyText) {
		View emptyView = findViewById(android.R.id.empty);
		if (emptyView != null) {
			if (adapter.getItemCount() ==0) {
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

	protected void refreshEmptyView(RecyclerView.Adapter adapter) {
		refreshEmptyView(adapter, null);
	}

}
