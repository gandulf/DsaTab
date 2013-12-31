package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Event;
import com.dsatab.data.NotesItem;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.filter.NotesListFilter;
import com.dsatab.util.Util;
import com.dsatab.view.listener.CheckableListenable;
import com.dsatab.view.listener.OnCheckedChangeListener;
import com.gandulf.guilib.data.OpenArrayAdapter;

import fr.castorflex.android.flipimageview.library.FlipImageView;

public class NotesAdapter extends OpenArrayAdapter<NotesItem> implements OnCheckedChangeListener, OnClickListener {

	private NotesListFilter filter;

	public NotesAdapter(Context context, NotesItem[] objects) {
		super(context, 0, objects);
	}

	public NotesAdapter(Context context, List<NotesItem> objects) {
		super(context, 0, objects);
	}

	public void filter(String constraint, List<EventCategory> types) {
		getFilter().setTypes(types);
		filter.filter(constraint);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.event_list_item, parent, false);

			EventViewHolder holder = new EventViewHolder();
			holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
			holder.icon1 = (FlipImageView) convertView.findViewById(android.R.id.icon1);
			holder.icon2 = (ImageView) convertView.findViewById(android.R.id.icon2);
			if (parent instanceof ListView) {
				holder.icon1.setOnClickListener(this);
				holder.icon1.setTag(new ListHolder());
			} else {
				holder.icon1.setOnClickListener(null);
			}

			convertView.setTag(holder);
		}

		NotesItem e = getItem(position);

		convertView = prepareView(e, position, convertView, parent);

		Util.applyRowStyle(convertView, position);

		return convertView;
	}

	protected View prepareView(NotesItem e, int position, View convertView, ViewGroup parent) {
		EventViewHolder holder = (EventViewHolder) convertView.getTag();

		ListHolder listHolder = (ListHolder) holder.icon1.getTag();
		if (listHolder != null && parent instanceof ListView) {
			listHolder.position = position;
			listHolder.list = (ListView) parent;
		}

		if (e.getCategory() != null) {

			if (holder.icon1 != null) {
				holder.icon1.setImageResource(e.getCategory().getDrawableId());

				if (convertView instanceof CheckableListenable) {
					CheckableListenable checkable = (CheckableListenable) convertView;
					holder.icon1.setFlipped(checkable.isChecked());
					checkable.setOnCheckedChangeListener(this);
				}
			}

			if (holder.icon2 != null) {
				if (e instanceof Event) {
					if (((Event) e).getAudioPath() != null) {
						holder.icon2.setVisibility(View.VISIBLE);
						holder.icon2
								.setImageResource(Util.getThemeResourceId(getContext(), R.attr.imgActionMicrophone));
					} else {
						holder.icon2.setVisibility(View.GONE);
					}
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

		return convertView;
	}

	@Override
	public void onClick(View v) {
		FlipImageView flipImageView = (FlipImageView) v;
		ListHolder listHolder = (ListHolder) v.getTag();
		listHolder.list.setItemChecked(listHolder.position, !flipImageView.isFlipped());
	}

	@Override
	public void onCheckedChanged(View checkableView, boolean isChecked) {
		if (checkableView.getTag() instanceof EventViewHolder) {
			EventViewHolder holder = (EventViewHolder) checkableView.getTag();
			holder.icon1.setFlipped(isChecked, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public NotesListFilter getFilter() {
		if (filter == null)
			filter = new NotesListFilter(this);

		return filter;
	}

	static class ListHolder {
		ListView list;
		int position;
	}

	static class EventViewHolder {
		TextView text1, text2;
		ImageView icon2;
		FlipImageView icon1;
	}

}
