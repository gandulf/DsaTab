package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableSelectableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

public class TabInfoDraggableItemAdapter extends RecyclerView.Adapter<TabViewHolder> implements
		DraggableItemAdapter<TabViewHolder>, SwipeableItemAdapter<TabViewHolder>, SelectableItemAdapter<TabViewHolder> {

	public class TabViewHolder extends AbstractDraggableSwipeableSelectableItemViewHolder {
		public ViewGroup mContainer;
		public View mDragHandle;
		public TextView mTextView;
		public ImageView mImageView;

		public TabViewHolder(View v) {
			super(v);
			mContainer = (ViewGroup) v;
			mDragHandle = v.findViewById(R.id.drag);
			mTextView = (TextView) v.findViewById(android.R.id.text1);
			mImageView = (ImageView) v.findViewById(R.id.gen_tab);
		}

		@Override
		public View getSwipeableContainerView() {
			return mContainer;
		}
	}

	private List<TabInfo> data;

	private EventListener mEventListener;
	private View.OnClickListener mItemViewOnClickListener;

	public interface EventListener {
		void onItemRemoved(int position);

		void onItemViewClicked(int position, View v);

		void onItemSelected(int position, boolean value);
	}

	public TabInfoDraggableItemAdapter(Context context, List<TabInfo> tabInfos) {
		// DraggableItemAdapter requires stable ID, and also
		// have to implement the getItemId() method appropriately.
		data = tabInfos;

		mItemViewOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemViewClick(v);
			}
		};

		setHasStableIds(true);
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public TabViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
		final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		final View v = inflater.inflate(R.layout.item_drag_tab, parent, false);
		return new TabViewHolder(v);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public List<TabInfo> getItems() {
		return data;
	}

	public TabInfo get(int position) {
		return data.get(position);
	}

	public void insert(TabInfo info) {
		data.add(info);
		notifyItemInserted(data.indexOf(info));
	}

	public void clear() {
		data.clear();;
		notifyItemRangeRemoved(0,data.size());
	}


	public void setSelected(int position) {
//TODO
	}



	@Override
	public void onMoveItem(int fromPosition, int toPosition) {

		if (fromPosition == toPosition) {
			return;
		}

		final TabInfo item = data.remove(fromPosition);
		data.add(toPosition, item);

		notifyItemMoved(fromPosition, toPosition);
	}

	@Override
	public void onItemSelected(TabViewHolder holder, boolean value) {
		if (mEventListener != null) {
			mEventListener.onItemSelected(holder.getPosition(), value);
		}
	}

	private void onItemViewClick(View v) {
		if (mEventListener != null) {
			TabViewHolder holder =(TabViewHolder) RecyclerViewAdapterUtils.getViewHolder(v);
			mEventListener.onItemViewClicked(holder.getPosition(), v);
		}
	}

	@Override
	public void onBindViewHolder(TabViewHolder holder, int position) {
		TabViewHolder tabHolder = (TabViewHolder) holder;


		// set listeners
		// (if the item is *not pinned*, click event comes to the itemView)
		holder.itemView.setOnClickListener(mItemViewOnClickListener);

		final TabInfo item = data.get(position);

		// set text
		tabHolder.mTextView.setText(item.getTitle());
		tabHolder.mImageView.setImageURI(item.getIconUri());
		Util.applyRowStyle(tabHolder.mContainer, position);

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
	public ItemDraggableRange onGetItemDraggableRange(TabViewHolder holder, int position) {
		// no drag-sortable range specified
		return null;
	}

	@Override
	public int onGetSwipeReactionType(TabViewHolder holder, int position, int x, int y) {
		if (onCheckCanStartDrag(holder, position, x, y)) {
			return RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_BOTH;
		} else {
			return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT;
		}
	}

	@Override
	public void onSetSwipeBackground(TabViewHolder holder, int position, int type) {

	}

	@Override
	public int onSwipeItem(TabViewHolder holder, int position, int result) {
		switch (result) {
			// swipe left -- remove
			case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
				return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
			// other --- do nothing
			case RecyclerViewSwipeManager.RESULT_CANCELED:
			default:
				return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
		}
	}

	@Override
	public void onPerformAfterSwipeReaction(TabViewHolder holder, int position, int result, int reaction) {

		if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
			data.remove(position);
			notifyItemRemoved(position);

			if (mEventListener != null) {
				mEventListener.onItemRemoved(position);
			}
		}
	}

	public EventListener getEventListener() {
		return mEventListener;
	}

	public void setEventListener(EventListener eventListener) {
		mEventListener = eventListener;
	}

}