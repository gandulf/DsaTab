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
package com.dsatab.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.util.Debug;

/**
 * @author Seraphim
 * 
 */
public class ItemListItem extends CheckableRelativeLayout {

	private int textColor = 0;

	public TextView text1, text2;
	public ImageView icon1, icon2;

	public ItemListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public ItemListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public ItemListItem(Context context) {
		super(context);
		init(null);
	}

	/**
	 * @param textColor
	 *            the textColor to set
	 */
	public void setTextColor(int textColor) {
		this.textColor = textColor;

		if (text1 != null && (textColor != Color.TRANSPARENT)) {
			text1.setTextColor(textColor);
		}
		if (text2 != null && (textColor != Color.TRANSPARENT)) {
			text2.setTextColor(textColor);
		}
	}

	/**
	 * @param attrs
	 */
	private void init(AttributeSet attrs) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.TwoLineListItem#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		text1 = (TextView) findViewById(android.R.id.text1);
		text2 = (TextView) findViewById(android.R.id.text2);
		icon1 = (ImageView) findViewById(android.R.id.icon1);
		icon2 = (ImageView) findViewById(android.R.id.icon2);

		if (text1 != null && (textColor != Color.TRANSPARENT)) {
			text1.setTextColor(textColor);
		}
		if (text2 != null && (textColor != Color.TRANSPARENT)) {
			text2.setTextColor(textColor);
		}

		if (icon1 != null) {
			icon1.setFocusable(false);
			icon1.setClickable(false);
		}
		if (icon2 != null) {
			icon2.setVisibility(View.GONE);
			icon2.setFocusable(false);
			icon2.setClickable(false);
		}

		super.onFinishInflate();
	}

	public void setItem(Item e) {
		if (e.getSpecifications().isEmpty())
			Debug.error("Item without spec found " + e.getName());
		setItem(e, e.getSpecifications().get(0));
	}

	public void setItem(Item e, ItemSpecification spec) {
		if (icon1 != null) {
			icon1.setVisibility(View.VISIBLE);
			if (e.getIconUri() != null)
				icon1.setImageURI(e.getIconUri());
			else
				icon1.setImageResource(spec.getResourceId());
		}
		// Set value for the first text field
		if (text1 != null) {
			text1.setText(e.getTitle());
			if (textColor != Color.TRANSPARENT)
				text1.setTextColor(textColor);
		}

		// set value for the second text field
		if (text2 != null) {
			text2.setText(spec.getInfo());
			if (textColor != Color.TRANSPARENT)
				text2.setTextColor(textColor);
		}
	}

}
