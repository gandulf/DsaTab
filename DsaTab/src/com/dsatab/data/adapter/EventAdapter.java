package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Event;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.filter.EventListFilter;
import com.dsatab.util.Util;

public class EventAdapter extends OpenArrayAdapter<Event> {

	private EventListFilter filter;

	public EventAdapter(Context context, Event[] objects) {
		super(context, 0, objects);
	}

	public EventAdapter(Context context, List<Event> objects) {
		super(context, 0, objects);
	}

	public void filter(String constraint, List<EventCategory> types) {
		getFilter().setTypes(types);
		filter.filter(constraint);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder = null;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.event_list_item, parent, false);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		if (holder == null) {
			holder = new ViewHolder();

			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			holder.text2 = (TextView) view.findViewById(android.R.id.text2);
			holder.icon1 = (ImageView) view.findViewById(android.R.id.icon1);
			holder.icon2 = (ImageView) view.findViewById(android.R.id.icon2);
			view.setTag(holder);
		}

		Event e = getItem(position);

		if (e.getCategory() != null) {

			if (holder.icon1 != null) {
				holder.icon1.setImageResource(e.getCategory().getDrawableId());
			}

			if (holder.icon2 != null) {
				if (e.getAudioPath() != null) {
					holder.icon2.setVisibility(View.VISIBLE);
					holder.icon2.setImageResource(Util.getThemeResourceId(getContext(), R.attr.imgActionMicrophone));
				} else {
					holder.icon2.setVisibility(View.GONE);
				}
			}
		}

		if (e.getCategory().hasName() && !TextUtils.isEmpty(e.getName())) {
			holder.text1.setText(e.getName().trim());
			holder.text2.setText(e.getComment().trim());
			holder.text2.setVisibility(View.VISIBLE);
		} else {
			holder.text1.setText(e.getComment().trim());
			holder.text2.setVisibility(View.GONE);
		}

		Util.applyRowStyle(view, position);

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public EventListFilter getFilter() {
		if (filter == null)
			filter = new EventListFilter(this);

		return filter;
	}

	private static class ViewHolder {
		TextView text1, text2;
		ImageView icon1, icon2;
	}

}
