package com.dsatab.data.adapter;

import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.config.TabInfo;
import com.dsatab.data.adapter.TabInfoDraggableItemAdapter.TabViewHolder;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableSelectableItemViewHolder;

import java.util.List;

public class TabInfoDraggableItemAdapter extends ListRecyclerAdapter<TabViewHolder, TabInfo> implements
        DraggableItemAdapter<TabViewHolder>, SwipeableItemAdapter<TabViewHolder> {

    public class TabViewHolder extends AbstractDraggableSwipeableSelectableItemViewHolder {
        public ViewGroup mContainer;
        public View mDragHandle;
        public TextView mTextView;
        public ImageView mImageView;
        public TabViewHolder(View v) {
            super(v);
            mContainer = (ViewGroup) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
            mImageView = (ImageView) v.findViewById(R.id.gen_tab);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    public TabInfoDraggableItemAdapter(List<TabInfo> tabInfos) {
        super(tabInfos);
        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.

        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public TabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.item_drag_tab, parent, false);
        return new TabViewHolder(v);
    }


    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        moveItem(fromPosition, toPosition);
    }

    @Override
    public void onBindViewHolder(TabViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final TabInfo item = getItem(position);

        // set text
        holder.mTextView.setText(item.getTitle());
        holder.mImageView.setImageDrawable(ViewUtils.circleIcon(holder.mImageView.getContext(), item.getIconUri()));
        Util.applyRowStyle(holder.itemView, position);

        // set background resource (target view ID: container)
        // final int dragState = holder.getDragStateFlags();
        //
        // if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0)) {
        // int bgResId;
        //
        // if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
        // bgResId = R.drawable.bg_item_dragging_active_state;
        // } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
        // bgResId = R.drawable.bg_item_dragging_state;
        // } else {
        // bgResId = R.drawable.bg_item_normal_state;
        // }
        //
        // holder.mContainer.setBackgroundResource(bgResId);
        // }
    }


    @Override
    public boolean onCheckCanStartDrag(TabViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;
        if (dragHandleView != null) {
            final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
            final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

            return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
        } else {
            return false;
        }
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(TabViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public int onGetSwipeReactionType(TabViewHolder holder, int position, int x, int y) {
        if (onCheckCanStartDrag(holder, position, x, y)) {
            return RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_ANY;
        } else {
            return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT;
        }
    }

    @Override
    public void onSetSwipeBackground(TabViewHolder holder, int position, int type) {

    }

    @Override
    public SwipeResultAction onSwipeItem(TabViewHolder holder, final int position, int result) {
        switch (result) {
            // swipe left -- remove
            case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                return new SwipeResultActionRemoveItem() {
                    @Override
                    protected void onPerformAction() {
                        super.onPerformAction();
                        remove(position);
                    }
                };
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return new SwipeResultActionDefault();
        }
    }

}