package com.dsatab.data.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.dsatab.R;
import com.wnafee.vector.compat.ResourcesCompat;

import java.util.List;

public class TabIconAdapter extends ArrayAdapter<Integer> {

	public TabIconAdapter(Context context, List<Integer> objects) {
		super(context, 0, objects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
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
		view.setImageDrawable(ResourcesCompat.getDrawable(parent.getContext(),getItem(position)));
		return view;
	}

}