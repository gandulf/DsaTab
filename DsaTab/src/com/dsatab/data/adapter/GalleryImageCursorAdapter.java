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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;

import com.dsatab.R;
import com.dsatab.data.ItemLocationInfo;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.util.Util;
import com.dsatab.view.CardView;

public class GalleryImageCursorAdapter extends SimpleCursorAdapter {

	private int width, height;

	private Context mContext;

	public GalleryImageCursorAdapter(Context context, Cursor c) {
		super(context, 0, c, new String[0], new int[0], 0);

		init(context);
	}

	private void init(Context context) {
		mContext = context;
		width = context.getResources().getDimensionPixelSize(R.dimen.gallery_thumb_width);
		height = context.getResources().getDimensionPixelSize(R.dimen.gallery_thumb_height);
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
		Cursor item = (Cursor) getItem(position);

		if (convertView instanceof CardView) {
			i = (CardView) convertView;
			i.setLayoutParams(new Gallery.LayoutParams(width, height));
		} else {
			i = new CardView(mContext);
			/* Set the Width/Height of the ImageView. */
			i.setLayoutParams(new Gallery.LayoutParams(width, height));
		}
		i.setItem(new ItemCardCursor(item));
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

	class ItemCardCursor implements ItemCard {

		private Uri imageUri;
		private String title;
		private boolean imageTextOverlay;

		/**
		 * 
		 */
		public ItemCardCursor(Cursor c) {

			this.title = c.getString(c.getColumnIndex("title"));
			String name = c.getString(c.getColumnIndex("name"));
			String imageTextOverlay = c.getString(c.getColumnIndex("imageTextOverlay"));

			if (TextUtils.isEmpty(title)) {
				this.title = name;
			}

			String imageUriHelper = c.getString(c.getColumnIndex("imageUriHelper"));
			if (!TextUtils.isEmpty(imageUriHelper)) {
				imageUri = Uri.parse(imageUriHelper);
			}

			this.imageTextOverlay = Util.parseBoolean(imageTextOverlay);
		}

		@Override
		public ItemLocationInfo getItemInfo() {
			return null;
		}

		@Override
		public Uri getImageUri() {
			return imageUri;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.dsatab.data.items.ItemCard#isImageTextOverlay()
		 */
		@Override
		public boolean isImageTextOverlay() {
			return imageTextOverlay;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.dsatab.data.items.ItemCard#hasImage()
		 */
		@Override
		public boolean hasImage() {
			return imageUri != null;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public Item getItem() {
			return null;
		}

	}
}
