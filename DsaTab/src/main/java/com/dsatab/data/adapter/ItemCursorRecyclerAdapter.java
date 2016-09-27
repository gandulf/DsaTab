package com.dsatab.data.adapter;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.db.DataManager;
import com.dsatab.util.Util;
import com.dsatab.view.ItemListItem;
import com.franlopez.flipcheckbox.FlipCheckBox;
import com.h6ah4i.android.widget.advrecyclerview.selectable.ElevatingSelectableViewHolder;

public class ItemCursorRecyclerAdapter extends CursorRecyclerAdapter<ItemCursorRecyclerAdapter.ItemViewHolder> {



	public ItemCursorRecyclerAdapter(Cursor c) {
		super(c);
	}

    @Override
	public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return new ItemViewHolder(inflate(inflater, parent, R.layout.item_listitem_view, false));
	}

    @Override
	public void onBindViewHolder(ItemViewHolder viewHolder, Cursor cursor) {
		// this seems to be called even after stop in some rare occasions...
		if (cursor != null && !cursor.isClosed()) {
			((ItemListItem)viewHolder.itemView).setItem(DataManager.getItemByCursor(cursor));
		}

		Util.applyRowStyle(viewHolder.itemView, cursor.getPosition());
	}

	protected static class ItemViewHolder extends ElevatingSelectableViewHolder {

		TextView text1, text2, text3;
        FlipCheckBox icon1;
		ImageView  icon2, icon_chain_top, icon_chain_bottom;

		public ItemViewHolder(View v) {
			super(v);

			text1 = (TextView) v.findViewById(android.R.id.text1);
			text2 = (TextView) v.findViewById(android.R.id.text2);
			text3 = (TextView) v.findViewById(R.id.text3);
			icon1 = (FlipCheckBox) v.findViewById(android.R.id.checkbox);

			icon2 = (ImageView) v.findViewById(android.R.id.icon2);
			icon_chain_bottom = (ImageView) v.findViewById(R.id.icon_chain_bottom);
			icon_chain_top = (ImageView) v.findViewById(R.id.icon_chain_top);
		}
	}

}
