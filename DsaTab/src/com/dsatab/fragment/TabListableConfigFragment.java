package com.dsatab.fragment;

import android.app.Fragment;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.config.TabInfo;
import com.dsatab.data.adapter.ListItemConfigAdapter;
import com.dsatab.data.adapter.ListableItemAdapter;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.selectable.RecyclerViewSelectionManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.ArrayList;
import java.util.List;

public class TabListableConfigFragment extends Fragment implements View.OnClickListener,
		OnItemSelectedListener, OnCheckedChangeListener {

	private TabInfo info;
	private ListSettings listSettings;
	private int index;

	private Spinner spinner;
	private LinearLayout settingsLayout;

	private CheckBox modifier;

	private ImageButton addListItem;

	private RecyclerView mRecyclerView;
	private ListItemConfigAdapter mAdapter;

	private RecyclerView.LayoutManager mLayoutManager;
	private RecyclerView.Adapter mWrappedAdapter;
	//private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
	private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
	private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
	// private RecyclerViewSelectionManager mRecyclerViewSelectionManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout._edit_tabinfo_list, container, false);

		spinner = (Spinner) root.findViewById(R.id.popup_tab_type);
		spinner.setOnItemSelectedListener(this);
		settingsLayout = (LinearLayout) root.findViewById(R.id.popup_tab_content);

		mRecyclerView = (RecyclerView) root.findViewById(android.R.id.list);

		addListItem = (ImageButton) root.findViewById(R.id.popup_edit_add_list_item);
		addListItem.setOnClickListener(this);

		spinner.setAdapter(new SpinnerSimpleAdapter<String>(getActivity(), BaseFragment.activities));



		modifier = (CheckBox) root.findViewById(R.id.popup_edit_include_modifiers);



		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
		mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
		mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
		mRecyclerViewTouchActionGuardManager.setEnabled(true);

		// drag & drop manager
		mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
		mRecyclerViewDragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z3));

		mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager);

		mAdapter = new ListItemConfigAdapter(getActivity(), DsaTabApplication.getInstance().getHero(),
				new ArrayList<ListSettings.ListItem>());

		mWrappedAdapter = mAdapter;
		mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mWrappedAdapter);     // wrap for dragging
		//mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);      // wrap for swiping
		// mWrappedAdapter = mRecyclerViewSelectionManager.createWrappedAdapter(mWrappedAdapter);  // wrap for selection

		mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
		final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
		mRecyclerView.setItemAnimator(animator);

		// additional decorations
		//noinspection StatementWithEmptyBody
		if (supportsViewElevation()) {
			// Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
		} else {
			mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
		}

		// mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha), true));

		// NOTE:
		// The initialization order is very important! This order determines the priority of touch event handling.
		//
		// priority: TouchActionGuard> Selection > Swipe > DragAndDrop
		mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
		// mRecyclerViewSelectionManager.attachRecyclerView(mRecyclerView);
		//mRecyclerViewSwipeManager.attachRecyclerView(recyclerView);
		mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
	}

	@Override
	public void onPause() {
		mRecyclerViewDragDropManager.cancelDrag();
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (mRecyclerViewDragDropManager != null) {
		mRecyclerViewDragDropManager.release();
		mRecyclerViewDragDropManager = null;
		}

		//if (mRecyclerViewSwipeManager != null) {
		//          mRecyclerViewSwipeManager.release();
		//          mRecyclerViewSwipeManager = null;
		//    }

		if (mRecyclerViewTouchActionGuardManager != null) {
			mRecyclerViewTouchActionGuardManager.release();
			mRecyclerViewTouchActionGuardManager = null;
		}

		if (mRecyclerView != null) {
			mRecyclerView.setItemAnimator(null);
			mRecyclerView.setAdapter(null);
			mRecyclerView = null;
		}

		if (mWrappedAdapter != null) {
			WrapperAdapterUtils.releaseAll(mWrappedAdapter);
			mWrappedAdapter = null;
		}
		mAdapter = null;
		mLayoutManager = null;


	}
	private boolean supportsViewElevation() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
	}
	/*
         * (non-Javadoc)
         *
         * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView,
         * android.view.View, int, long)
         */
	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
		if (info != null && adapter == spinner) {
			Class<? extends BaseFragment> clazz = BaseFragment.activityValues.get(adapter.getSelectedItemPosition());
			info.setActivityClazz(index, clazz);
			updateListSettings();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged (android.widget.CompoundButton,
	 * boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (info != null) {
			switch (buttonView.getId()) {
			case R.id.popup_edit_include_modifiers:
				listSettings.setIncludeModifiers(isChecked);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> adapter) {
		if (info != null && adapter == spinner) {
			info.setActivityClazz(index, null);
			updateListSettings();
		}
	}

	public void setTabInfo(TabInfo info, int index) {
		this.info = info;
		this.index = index;
		spinner.setEnabled(info != null);
		if (info != null) {
			this.listSettings = info.getListSettings(index);

			Class<? extends BaseFragment> clazz = info.getActivityClazz(index);
			spinner.setSelection(BaseFragment.activityValues.indexOf(clazz));
		} else {
			this.listSettings = null;
		}

		updateListSettings();
	}

	private void updateListSettings() {

		if (info != null && info.getListSettings(index) != null) {
			listSettings = info.getListSettings(index);

			settingsLayout.setVisibility(View.VISIBLE);

			modifier.setChecked(listSettings.isIncludeModifiers());
			modifier.setOnCheckedChangeListener(this);

			if (info.getActivityClazz(index) == ListableFragment.class) {
				mRecyclerView.setVisibility(View.VISIBLE);
				mAdapter.clear();
				mAdapter.addAll(listSettings.getListItems());
			} else {
				settingsLayout.setVisibility(View.GONE);
				mRecyclerView.setVisibility(View.GONE);
			}
		} else {
			settingsLayout.setVisibility(View.GONE);
			mRecyclerView.setVisibility(View.GONE);
		}
	}



	public void accept() {
		if (listSettings != null && mAdapter != null) {
			listSettings.getListItems().clear();
			listSettings.getListItems().addAll(mAdapter.getItems());
		}
	}

	public void cancel() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_edit_add_list_item:

			List<String> listTypeTitles = new ArrayList<String>();
			for (ListItemType listItemType : ListItemType.values()) {
				listTypeTitles.add(listItemType.title());
			}

			PopupMenu popupMenu = new PopupMenu(getActivity(), v);
			for (int i = 0; i < listTypeTitles.size(); i++) {
				popupMenu.getMenu().add(0, i, i, listTypeTitles.get(i));
			}

			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					ListItem newListItem = new ListItem((ListItemType) ListItemType.values()[item.getItemId()]);
					mAdapter.add(newListItem);
					listSettings.getListItems().add(newListItem);

					mAdapter.editListItem(newListItem);
					return true;
				}
			});
			popupMenu.show();
			break;
		}
	}
}
