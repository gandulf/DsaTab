package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerSimpleAdapter<T> extends ArrayAdapter<T> {

	public SpinnerSimpleAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
		init();
	}

	public SpinnerSimpleAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
		init();
	}

	public SpinnerSimpleAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		init();
	}

	public SpinnerSimpleAdapter(Context context, int textViewResourceId, List<T> objects) {
		super(context, textViewResourceId, objects);
		init();
	}

	public SpinnerSimpleAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
		init();
	}

	public SpinnerSimpleAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		init();
	}

	public SpinnerSimpleAdapter(Context context, List<T> objects) {
		super(context, android.R.layout.simple_spinner_item, objects);
		init();

	}

	public SpinnerSimpleAdapter(Context context, T[] objects) {
		super(context, android.R.layout.simple_spinner_item, objects);
		init();
	}

	private void init() {
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		convertView = super.getDropDownView(position, convertView, parent);

		TextView textView = null;
		if (convertView instanceof TextView) {
			textView = (TextView) convertView;
		}

		if (textView != null) {
			T e = getItem(position);
			textView.setText(e.toString().replace('_', ' '));
		}
		return convertView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);

		TextView textView = null;
		if (convertView instanceof TextView) {
			textView = (TextView) convertView;
		}

		if (textView != null) {
			T e = getItem(position);
			textView.setText(e.toString().replace('_', ' '));
		}
		return textView;
	}

}
