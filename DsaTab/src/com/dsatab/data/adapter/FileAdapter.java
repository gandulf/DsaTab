package com.dsatab.data.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dsatab.R;

public class FileAdapter extends ArrayAdapter<File> {

	private static final String PDF_SUFFIX = ".pdf";

	private int textViewResourceId;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public FileAdapter(Context context, int textViewResourceId, List<File> objects) {
		super(context, textViewResourceId, objects);
		this.textViewResourceId = textViewResourceId;
	}

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 */
	public FileAdapter(Context context, int resource, int textViewResourceId, List<File> objects) {
		super(context, resource, textViewResourceId, objects);
		this.textViewResourceId = textViewResourceId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		File file = getItem(position);

		TextView textView = null;
		if (view instanceof TextView) {
			textView = ((TextView) view);
		} else {
			textView = ((TextView) view.findViewById(textViewResourceId));
		}

		if (textView != null) {
			textView.setText(file.getName());

			if (file.getName().endsWith(PDF_SUFFIX))
				textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tab_pdf, 0, 0, 0);
		}

		return view;
	}

}
