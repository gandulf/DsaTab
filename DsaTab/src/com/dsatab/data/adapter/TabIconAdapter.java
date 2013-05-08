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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.dsatab.R;

/**
 * @author Ganymede
 * 
 */
public class TabIconAdapter extends ArrayAdapter<Integer> {

	public TabIconAdapter(Context context, List<Integer> objects) {
		super(context, 0, objects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView view;
		if (convertView instanceof ImageView) {
			view = (ImageView) convertView;
		} else {
			view = new ImageView(getContext());
			int tabSize = getContext().getResources().getDimensionPixelSize(R.dimen.icon_button_size);
			view.setLayoutParams(new AbsListView.LayoutParams(tabSize, tabSize));
		}
		view.setFocusable(false);
		view.setClickable(false);
		view.setImageResource(getItem(position));
		return view;
	}

}