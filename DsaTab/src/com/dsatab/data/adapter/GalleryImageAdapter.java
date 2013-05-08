/*
 * Copyright (C) 2010 Gandulf Kohlweiss
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.dsatab.data.adapter;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;

import com.dsatab.R;
import com.dsatab.data.filter.ItemListFilter;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemType;
import com.dsatab.view.CardView;

public class GalleryImageAdapter extends OpenArrayAdapter<Item> {

	private int width, height;

	private ItemListFilter filter;

	public GalleryImageAdapter(Context context, List<Item> items) {
		super(context, 0, items);

		width = context.getResources().getDimensionPixelSize(R.dimen.gallery_thumb_width);
		height = context.getResources().getDimensionPixelSize(R.dimen.gallery_thumb_height);
	}

	public void filter(Collection<ItemType> types, String category, String constraint) {
		getFilter().setTypes(types);
		filter.setCategory(category);
		filter.filter(constraint);
	}

	public void filter(ItemType type, String category, String constraint) {
		getFilter().setType(type);
		filter.setCategory(category);
		filter.filter(constraint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public ItemListFilter getFilter() {
		if (filter == null)
			filter = new ItemListFilter(this);

		return filter;
	}

	public int getPositionByName(Item item) {
		if (item != null) {
			for (int i = 0; i < mObjects.size(); i++) {
				if (item.getName().equals(mObjects.get(i).getName()))
					return i;
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		CardView i;
		Item item = getItem(position);

		if (convertView instanceof CardView) {
			i = (CardView) convertView;
			i.setLayoutParams(new Gallery.LayoutParams(width, height));
		} else {
			i = new CardView(getContext());
			/* Set the Width/Height of the ImageView. */
			i.setLayoutParams(new Gallery.LayoutParams(width, height));
		}
		i.setItem(item);
		return i;
	}

	/**
	 * Returns the size (0.0f to 1.0f) of the views
	 * 
	 * depending on the 'offset' to the center.
	 */
	public float getScale(boolean focused, int offset) {
		/* Formula: 1 / (2 ^ offset) */
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}
}
