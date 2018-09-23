package com.dsatab.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;

import com.dsatab.R;
import com.franlopez.flipcheckbox.CheckableListenable;
import com.franlopez.flipcheckbox.OnCheckedChangeListener;
import com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableSelectableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.mikepenz.aboutlibraries.util.RippleForegroundListener;

/**
 * Created by Ganymedes on 24.10.2015.
 */
public abstract class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> implements SelectableItemAdapter<VH> {

    public interface EventListener {

        void onItemRemoved(BaseRecyclerAdapter adapter, int position);

        void onItemClicked(BaseRecyclerAdapter adapter,int position, View v);

        boolean onItemLongClicked(BaseRecyclerAdapter adapter, int position, View v);

        void onItemSelected(BaseRecyclerAdapter adapter, int position, boolean value);
    }

    protected abstract static class BaseListableViewHolder extends AbstractDraggableSwipeableSelectableItemViewHolder {

        private Checkable check;

        public BaseListableViewHolder(View v) {
            super(v);
            this.check = (Checkable) v.findViewById(android.R.id.checkbox);
        }

        @Override
        public void setActivated(boolean activated) {
            super.setActivated(activated);

            if (check != null) {
                check.setChecked(activated);
            }
        }
    }

    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;
    private View.OnLongClickListener mItemViewOnLongClickListener;
    private View.OnClickListener mCheckboxOnClickListener;
    private OnCheckedChangeListener mCheckedChangeListener;
    private RippleForegroundListener rippleForegroundListener;

    protected BaseRecyclerAdapter() {
        rippleForegroundListener = new RippleForegroundListener(R.id.list_item_container);

        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VH holder =(VH) RecyclerViewAdapterUtils.getViewHolder(v);
                if (holder!=null) {
                    onItemClicked(holder,v);
                }
            }
        };

        mItemViewOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                VH holder =(VH) RecyclerViewAdapterUtils.getViewHolder(v);
                if (holder!=null) {
                    return onItemLongClicked(holder,v);
                } else {
                    return false;
                }
            }
        };

        mCheckboxOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VH holder =(VH) RecyclerViewAdapterUtils.getViewHolder(v);
                if (holder!=null) {
                    Checkable check = (Checkable) v;
                    onItemSelected(holder, check.isChecked());
                }
            }
        };

        mCheckedChangeListener = new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(View v, boolean isChecked) {
                VH holder =(VH) RecyclerViewAdapterUtils.getViewHolder(v);
                if (holder!=null) {
                    onItemSelected(holder, isChecked);
                }
            }
        };

    }


    protected void onItemClicked(VH viewHolder, View v) {
        if (mEventListener != null) {
            mEventListener.onItemClicked(BaseRecyclerAdapter.this, viewHolder.getAdapterPosition(), v);
        }
    }

    protected boolean onItemLongClicked(VH viewHolder, View v) {
        if (mEventListener != null) {
            return mEventListener.onItemLongClicked(BaseRecyclerAdapter.this,viewHolder.getAdapterPosition(), v);
        } else {
            return false;
        }
    }

    @Override
    public void onItemSelected(RecyclerView.ViewHolder holder, boolean value) {
        View check = holder.itemView.findViewById(android.R.id.checkbox);
        if (check instanceof CheckableListenable) {
            ((CheckableListenable)check).setOnCheckedChangeListener(null);
            ((CheckableListenable)check).setChecked(value);
            ((CheckableListenable)check).setOnCheckedChangeListener(mCheckedChangeListener);
        } else if (check instanceof Checkable) {
            ((Checkable)check).setChecked(value);
        }

        if (getEventListener() != null) {
            getEventListener().onItemSelected(BaseRecyclerAdapter.this, holder.getAdapterPosition(), value);
        }
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        // set listeners
        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        holder.itemView.setOnLongClickListener(mItemViewOnLongClickListener);
        holder.itemView.setOnTouchListener(rippleForegroundListener);

        View check = holder.itemView.findViewById(android.R.id.checkbox);
        if (check instanceof Checkable) {

            if (holder instanceof SelectableItemViewHolder) {
                if (check instanceof CheckableListenable) {
                    ((CheckableListenable) check).setCheckedImmediate(((SelectableItemViewHolder) holder).isActivated());
                } else {
                    ((Checkable) check).setChecked(((SelectableItemViewHolder) holder).isActivated());
                }
            }

            if (check instanceof CheckBox) {
                check.setOnClickListener(mCheckboxOnClickListener);
            }
        }
    }
}