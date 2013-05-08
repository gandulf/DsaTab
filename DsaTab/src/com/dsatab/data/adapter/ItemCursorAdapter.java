package com.dsatab.data.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dsatab.R;
import com.dsatab.util.Util;
import com.dsatab.view.ItemListItem;
import com.dsatab.xml.DataManager;

public class ItemCursorAdapter extends SimpleCursorAdapter {

	private LayoutInflater inflater;

	private Context mContext;

	public ItemCursorAdapter(Context context, Cursor c) {
		super(context, 0, c, new String[0], new int[0], 0);
		this.mContext = context;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// View view = super.getView(position, convertView, parent);

		ItemListItem view = null;

		if (!(convertView instanceof ItemListItem)) {
			view = (ItemListItem) inflater.inflate(R.layout.item_listitem_view, parent, false);
		} else {
			view = (ItemListItem) convertView;
		}

		// this seems to be called even after stop in some rare occasions...
		if (!getCursor().isClosed()) {
			Cursor item = (Cursor) getItem(position);
			view.setItem(DataManager.getItemByCursor(item));
		}

		Util.applyRowStyle(view, position);
		return view;
	}

}
