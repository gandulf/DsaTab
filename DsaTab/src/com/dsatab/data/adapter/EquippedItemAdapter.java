package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.dsatab.R;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.view.ItemListItem;

public class EquippedItemAdapter extends ArrayAdapter<EquippedItem> {

	private LayoutInflater inflater;

	public EquippedItemAdapter(Context context, List<EquippedItem> objects) {
		super(context, 0, objects);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// View view = super.getView(position, convertView, parent);

		ItemListItem view;
		if (!(convertView instanceof ItemListItem)) {
			view = (ItemListItem) inflater.inflate(R.layout.item_listitem_view, parent, false);
		} else {
			view = (ItemListItem) convertView;
		}

		EquippedItem equippedItem = getItem(position);
		Item e = equippedItem.getItem();
		view.setItem(e);

		return view;
	}
}
