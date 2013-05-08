/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.data.adapter;

import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;

/**
 * 
 *
 */
public class ItemContainerAdapter extends OpenArrayAdapter<ItemContainer> {

	private LayoutInflater inflater;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public ItemContainerAdapter(Context context, Collection<ItemContainer> spells) {
		super(context, 0, 0, spells);

		inflater = LayoutInflater.from(getContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View listItem;
		ViewHolder holder;
		if (convertView == null) {
			listItem = inflater.inflate(R.layout.item_listitem_view, parent, false);

			holder = new ViewHolder();
			holder.text1 = (TextView) listItem.findViewById(android.R.id.text1);
			holder.text2 = (TextView) listItem.findViewById(android.R.id.text2);
			holder.text3 = (TextView) listItem.findViewById(R.id.text3);
			holder.icon1 = (ImageView) listItem.findViewById(android.R.id.icon1);
			holder.icon2 = (ImageView) listItem.findViewById(android.R.id.icon2);

			listItem.setTag(holder);
		} else {
			listItem = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		ItemContainer item = getItem(position);

		holder.text1.setText(item.getName());
		holder.icon1.setImageURI(item.getIconUri());
		if (item.getId() >= Hero.FIRST_INVENTORY_SCREEN) {
			holder.text2.setText(item.getItems().size() + " "
					+ getContext().getResources().getQuantityString(R.plurals.items, item.getItems().size()));
			if (item.getCapacity() != 0 || item.getWeight() != 0) {
				holder.text3.setVisibility(View.VISIBLE);
				holder.text3.setText(getContext().getResources().getString(R.string.label_capacity, item.getWeight(),
						item.getCapacity()));
			} else {
				holder.text3.setVisibility(View.GONE);
			}
		} else {
			holder.text2.setText(null);
			holder.text3.setText(null);
		}
		Util.applyRowStyle(listItem, position);

		return listItem;

	}

	private static class ViewHolder {
		TextView text1, text2, text3;
		ImageView icon1, icon2;
	}

}
