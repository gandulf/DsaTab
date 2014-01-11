package com.dsatab.data.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;
import com.gandulf.guilib.data.OpenArrayAdapter;

public class ItemContainerAdapter extends OpenArrayAdapter<ItemContainer> {

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public ItemContainerAdapter(Context context, Collection<ItemContainer> container) {
		super(context, 0, 0, container);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_listitem_view, parent, false);

			holder = new ViewHolder();
			holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
			holder.text3 = (TextView) convertView.findViewById(R.id.text3);
			holder.icon1 = (ImageView) convertView.findViewById(android.R.id.icon1);
			holder.icon2 = (ImageView) convertView.findViewById(android.R.id.icon2);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ItemContainer itemContainer = getItem(position);

		holder.text1.setText(itemContainer.getName());
		holder.icon1.setImageURI(itemContainer.getIconUri());
		holder.icon1.setBackgroundResource(0);
		if (itemContainer.getId() >= Hero.FIRST_INVENTORY_SCREEN) {
			holder.text2.setText(itemContainer.size() + " "
					+ getContext().getResources().getQuantityString(R.plurals.items, itemContainer.size()));

			if (itemContainer.getCapacity() != 0 || itemContainer.getWeight() != 0.0f) {
				holder.text3.setVisibility(View.VISIBLE);
				holder.text3.setText(getContext().getResources().getString(R.string.label_capacity,
						itemContainer.getWeight(), itemContainer.getCapacity()));
			} else {
				holder.text3.setVisibility(View.GONE);
			}
		} else {
			holder.text2.setText(null);
			holder.text3.setText(null);
		}

		Util.applyRowStyle(convertView, position);

		return convertView;

	}

	private static class ViewHolder {
		TextView text1, text2, text3;
		ImageView icon1, icon2;
	}

}
