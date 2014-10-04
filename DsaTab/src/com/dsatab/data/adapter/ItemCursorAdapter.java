package com.dsatab.data.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.db.DataManager;
import com.dsatab.util.Util;
import com.dsatab.view.ItemListItem;

public class ItemCursorAdapter extends SimpleCursorAdapter {

	private LayoutInflater inflater;

	private Context mContext;

	public ItemCursorAdapter(Context context, Cursor c) {
		super(context, 0, c, new String[0], new int[0], 0);
		this.mContext = context;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public long getItemId(int position) {

		if (getCursor() != null && !getCursor().isClosed()) {
			Cursor cursor = (Cursor) getItem(position);
			if (cursor != null) {
				String _id = cursor.getString(cursor.getColumnIndex("_id"));
				return _id.hashCode();
			} else {
				return ListView.INVALID_ROW_ID;
			}
		} else {
			return ListView.INVALID_ROW_ID;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = (ItemListItem) inflater.inflate(R.layout.item_listitem_view, parent, false);

			holder = new ViewHolder();
			holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
			holder.text3 = (TextView) convertView.findViewById(R.id.text3);
			holder.icon1 = (ImageView) convertView.findViewById(android.R.id.icon1);

			holder.icon2 = (ImageView) convertView.findViewById(android.R.id.icon2);
			holder.icon_chain_bottom = (ImageView) convertView.findViewById(R.id.icon_chain_bottom);
			holder.icon_chain_top = (ImageView) convertView.findViewById(R.id.icon_chain_top);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// this seems to be called even after stop in some rare occasions...
		if (getCursor() != null && !getCursor().isClosed()) {
			Cursor item = (Cursor) getItem(position);
			((ItemListItem) convertView).setItem(DataManager.getItemByCursor(item));
		}

		Util.applyRowStyle(convertView, position);
		return convertView;
	}

	private static class ViewHolder {
		TextView text1, text2, text3;
		ImageView icon1, icon2, icon_chain_top, icon_chain_bottom;
	}

}
