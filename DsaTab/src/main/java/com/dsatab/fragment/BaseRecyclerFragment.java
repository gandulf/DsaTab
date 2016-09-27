package com.dsatab.fragment;

import android.content.Context;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.adapter.BaseRecyclerAdapter;
import com.dsatab.util.ViewUtils;
import com.dsatab.view.ListSettings;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.selectable.RecyclerViewSelectionManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class BaseRecyclerFragment extends BaseFragment implements BaseRecyclerAdapter.EventListener,
        View.OnClickListener {

    protected RecyclerView recyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;

    protected RecyclerViewSelectionManager mRecyclerViewSelectionManager;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;

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
    public void onPause() {
        super.onPause();
        if (mRecyclerViewDragDropManager != null)
            mRecyclerViewDragDropManager.cancelDrag();
    }

    protected void initRecyclerView(RecyclerView rv, RecyclerView.Adapter adapter, boolean dragDrop, boolean swipe, boolean selection) {
        initRecyclerView(rv, adapter, new LinearLayoutManager(getActivity()), dragDrop, swipe, selection);
    }
    protected void initRecyclerView(RecyclerView rv, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager, boolean dragDrop, boolean swipe, boolean selection) {
        recyclerView = rv;
        mWrappedAdapter = adapter;

        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // drag & drop manager
        if (dragDrop) {
            mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
            mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                    (NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z3));

            mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mWrappedAdapter);     // wrap for dragging
        }
        if (swipe) {
            mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();
            mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);      // wrap for swiping

            //recyclerView.setItemAnimator(new SwipeDismissItemAnimator());
        }

        if (selection) {
            mRecyclerViewSelectionManager = new RecyclerViewSelectionManager();
            mWrappedAdapter = mRecyclerViewSelectionManager.createWrappedAdapter(mWrappedAdapter);      // wrap for swiping
        }

        mLayoutManager = layoutManager;
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(mWrappedAdapter);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
        }
        //recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard> Selection > Swipe > DragAndDrop
        if (mRecyclerViewTouchActionGuardManager != null)
            mRecyclerViewTouchActionGuardManager.attachRecyclerView(recyclerView);

        if (mRecyclerViewSelectionManager != null)
            mRecyclerViewSelectionManager.attachRecyclerView(recyclerView);

        if (mRecyclerViewSwipeManager != null)
            mRecyclerViewSwipeManager.attachRecyclerView(recyclerView);

        if (mRecyclerViewDragDropManager != null)
            mRecyclerViewDragDropManager.attachRecyclerView(recyclerView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
            mRecyclerViewTouchActionGuardManager.release();
            mRecyclerViewTouchActionGuardManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }

        mLayoutManager = null;
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
    public void onItemSelected(BaseRecyclerAdapter adapter, int position, boolean value) {

        if (mRecyclerViewSelectionManager != null) {
            if (mRecyclerViewSelectionManager.isSelected(position) != value) {
                mRecyclerViewSelectionManager.setSelected(position, value);

                onCheckedChanged(recyclerView, mRecyclerViewSelectionManager, false);
            }
        }
    }

    protected void onCheckedChanged(RecyclerView view, RecyclerViewSelectionManager manager, boolean triggerMode) {

        if (!manager.getSelectedItems().isEmpty()) {
            if (mMode == null) {
                if (triggerMode) {
                    Callback callback = getActionModeCallback(manager.getSelectedItems());
                    if (callback != null) {
                        mMode = view.startActionMode(callback);
                        mMode.invalidate();
                    }
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
    public void onItemRemoved(BaseRecyclerAdapter adapter, int position) {

    }

    @Override
    public void onItemClicked(BaseRecyclerAdapter adapter, int position, View v) {
        if (mMode != null && mRecyclerViewSelectionManager!=null) {
            mRecyclerViewSelectionManager.toggleSelection(position);
            onCheckedChanged(recyclerView, mRecyclerViewSelectionManager, false);
        }
    }

    @Override
    public boolean onItemLongClicked(BaseRecyclerAdapter adapter, int position, View v) {
        if (mRecyclerViewSelectionManager!=null) {
            mRecyclerViewSelectionManager.toggleSelection(position);
            onCheckedChanged(recyclerView, mRecyclerViewSelectionManager, true);
            return true;
        } else {
            return false;
        }
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
            if (adapter.getItemCount() == 0) {
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

    protected final boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    protected View getListView() {
        return findViewById(android.R.id.list);
    }

    protected void refreshEmptyView(RecyclerView.Adapter adapter) {
        refreshEmptyView(adapter, null);
    }


    protected static abstract class BaseListableActionMode<T extends BaseRecyclerFragment> implements ActionMode.Callback {
        protected WeakReference<RecyclerViewSelectionManager> manager;
        protected WeakReference<RecyclerView> listView;
        protected WeakReference<T> listFragment;

        private int origFabVisibility;
        private int origFabMenuVisibility;

        public BaseListableActionMode(T fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            this.listFragment = new WeakReference<T>(fragment);
            this.listView = new WeakReference<RecyclerView>(listView);
            this.manager = new WeakReference<RecyclerViewSelectionManager>(manager);
        }

        protected RecyclerViewSelectionManager getManager() {
            return manager.get();
        }

        protected Context getContext() {
            T fragment = listFragment.get();
            if (fragment == null)
                return null;
            else {
                if (fragment.getDsaActivity() != null && fragment.getDsaActivity().getToolbar() != null)
                    return fragment.getDsaActivity().getToolbar().getContext();
                else if (fragment.getActivity() != null && fragment.getActivity().getActionBar() != null)
                    return fragment.getActivity().getActionBar().getThemedContext();
                else if (fragment.getActivity() != null)
                    return fragment.getActivity();
                else
                    return null;
            }
        }

        @Override
        @SuppressWarnings({"ResourceType","WrongConstant"})
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            ViewUtils.menuIcons(getContext(), menu);

            T fragment = listFragment.get();
            if (fragment == null)
                return false;

            if (fragment.getBaseActivity() != null) {
                View fab = fragment.getBaseActivity().findViewById(R.id.fab);
                if (fab != null) {
                    origFabVisibility = fab.getVisibility();
                    fab.setVisibility(View.GONE);
                }
                View fabMenu = fragment.getBaseActivity().findViewById(R.id.fab_menu);
                if (fabMenu != null) {
                    origFabMenuVisibility = fabMenu.getVisibility();
                    fabMenu.setVisibility(View.GONE);
                }
            }

            if (getManager() != null) {
                getManager().setCheckable(true);
            }

            return true;
        }

        @Override
        @SuppressWarnings({"ResourceType","WrongConstant"})
        public void onDestroyActionMode(ActionMode mode) {
            T fragment = listFragment.get();
            if (fragment == null)
                return;

            fragment.mMode = null;
            if (getManager()!=null) {
                getManager().clearSelections();
            }

            if (fragment != null && fragment.getBaseActivity() != null) {
                View fab = fragment.getBaseActivity().findViewById(R.id.fab);
                if (fab != null) {
                    fab.setVisibility(origFabVisibility);
                }
                View fabMenu = fragment.getBaseActivity().findViewById(R.id.fab_menu);
                if (fabMenu != null) {
                    fabMenu.setVisibility(origFabMenuVisibility);
                }
            }

            if (getManager() != null) {
                getManager().setCheckable(false);
            }
        }
    }
}
