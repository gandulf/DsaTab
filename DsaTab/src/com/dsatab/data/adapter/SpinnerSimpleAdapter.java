/*
 * Copyright (C) 2010 Gandulf Kohlweiss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
			textView.setText(e.toString());
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
			textView.setText(e.toString());
		}
		return textView;
	}

}
