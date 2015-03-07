package com.dsatab.data.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.enums.ItemType;
import com.dsatab.util.DsaUtil;

import java.util.List;

public class ItemTypeAdapter extends SpinnerSimpleAdapter<ItemType> {

	public ItemTypeAdapter(Context context, int textViewResourceId, List<ItemType> objects) {
		super(context, textViewResourceId, objects);
	}

	public ItemTypeAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getDropDownView(position, convertView, parent);

		if (view instanceof TextView) {
			TextView textView = (TextView) view;
			ItemType e = getItem(position);

			textView.setCompoundDrawablesWithIntrinsicBounds(DsaUtil.getResourceId(e), 0, 0, 0);
			textView.setCompoundDrawablePadding(getContext().getResources().getDimensionPixelSize(R.dimen.default_gap));
			textView.setText(e.name());
			textView.setGravity(Gravity.CENTER_VERTICAL);
		}

		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		if (view instanceof TextView) {
			TextView textView = (TextView) view;
			ItemType e = getItem(position);

			textView.setCompoundDrawablesWithIntrinsicBounds(DsaUtil.getResourceId(e), 0, 0, 0);
			textView.setCompoundDrawablePadding(getContext().getResources().getDimensionPixelSize(R.dimen.default_gap));
			textView.setText(e.name());
			textView.setGravity(Gravity.CENTER_VERTICAL);
		}

		return view;
	}

}
