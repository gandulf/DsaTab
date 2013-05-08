package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Connection;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.filter.ConnectionListFilter;
import com.dsatab.util.Util;

public class ConnectionAdapter extends OpenArrayAdapter<Connection> {

	private ConnectionListFilter filter;

	public ConnectionAdapter(Context context, Connection[] objects) {
		super(context, 0, objects);
	}

	public ConnectionAdapter(Context context, List<Connection> objects) {
		super(context, 0, objects);
	}

	public void filter(String constraint, List<EventCategory> types) {
		getFilter().setTypes(types);
		filter.filter(constraint);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.simple_list_item_2_icon, parent, false);

			holder = new ViewHolder();
			holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
			holder.icon = (ImageView) convertView.findViewById(android.R.id.icon1);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Connection e = getItem(position);

		if (holder.icon != null) {
			holder.icon.setImageResource(e.getCategory().getDrawableId());
		}
		holder.text1.setText(e.getName());
		holder.text2.setText(e.getDescription());

		Util.applyRowStyle(convertView, position);

		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public ConnectionListFilter getFilter() {
		if (filter == null)
			filter = new ConnectionListFilter(this);

		return filter;
	}

	static class ViewHolder {
		TextView text1, text2;
		ImageView icon;
	}

}
