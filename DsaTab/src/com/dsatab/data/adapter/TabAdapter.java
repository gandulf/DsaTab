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

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.util.Util;

/**
 * @author Ganymede
 * 
 */
public class TabAdapter extends OpenArrayAdapter<TabInfo> {

	private LayoutInflater inflater;

	public TabAdapter(Context context, List<TabInfo> objects) {
		super(context, 0, objects);

		inflater = LayoutInflater.from(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view;
		if (convertView != null) {
			view = (TextView) convertView;
		} else {
			view = (TextView) inflater.inflate(R.layout.sherlock_spinner_dropdown_item, parent, false);
		}
		TabInfo tabInfo = getItem(position);
		view.setText(tabInfo.getTitle());
		Drawable d = Util.getDrawableByUri(tabInfo.getIconUri());

		d.setBounds(0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.abs__dropdownitem_icon_width),
				getContext().getResources().getDimensionPixelSize(R.dimen.abs__dropdownitem_icon_width));
		view.setCompoundDrawables(d, null, null, null);
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view;
		if (convertView != null) {
			view = (TextView) convertView;
		} else {
			view = (TextView) inflater.inflate(R.layout.sherlock_spinner_item, parent, false);
		}
		TabInfo tabInfo = getItem(position);
		view.setText(tabInfo.getTitle());
		Drawable d = Util.getDrawableByUri(tabInfo.getIconUri());
		d.setBounds(0, 0, getContext().getResources().getDimensionPixelSize(R.dimen.abs__dropdownitem_icon_width),
				getContext().getResources().getDimensionPixelSize(R.dimen.abs__dropdownitem_icon_width));
		view.setGravity(Gravity.CENTER);
		view.setCompoundDrawables(d, null, null, null);
		return view;
	}

}
