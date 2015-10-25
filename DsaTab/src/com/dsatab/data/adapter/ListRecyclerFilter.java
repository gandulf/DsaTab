package com.dsatab.data.adapter;

import android.text.TextUtils;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListRecyclerFilter<T> extends Filter {

	protected String constraint;

	protected ListRecyclerAdapter<?,T> list;

	/**
	 *
	 */
	public ListRecyclerFilter(ListRecyclerAdapter<?, T> list) {
		this.list = list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
	 */
	@Override
	protected FilterResults performFiltering(CharSequence cons) {
		// NOTE: this function is *always* called from a background thread,
		// and not the UI thread.

		FilterResults result = new FilterResults();

		if (list.mOriginalValues == null) {
			synchronized (list.mLock) {
				list.mOriginalValues = list.mObjects;
				list.mObjects = new ArrayList<T>(list.mObjects);
			}
		}

		if (!TextUtils.isEmpty(cons)) {
			this.constraint = cons.toString().toLowerCase(Locale.GERMAN);
		} else {
			this.constraint = null;
		}

		if (isFilterSet()) {

			final List<T> values = list.mOriginalValues;
			final int count = values.size();
			final ArrayList<T> filt = new ArrayList<T>(count);

			for (int i = 0; i < count; i++) {
				T m = values.get(i);

				if (filter(m)) {
					filt.add(m);
				}
			}

			result.count = filt.size();
			result.values = filt;

		} else {
			synchronized (list.mLock) {
				result.values = list.mOriginalValues;
				result.count = list.mOriginalValues.size();
			}
		}
		return result;

	}

	protected boolean isFilterSet() {
		return constraint != null;
	}

	public boolean filter(T m) {
		boolean valid = true;

		if (constraint != null && m.toString().toLowerCase(Locale.GERMAN).startsWith(constraint)) {
			valid &= true;
		}

		return valid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Filter#publishResults(java.lang.CharSequence, android.widget.Filter.FilterResults)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		list.mObjects = (List<T>) results.values;

		list.notifyDataSetChanged();
//		if (results.count > 0) {
//			list.notifyDataSetChanged(false);
//		} else {
//			list.notifyDataSetChanged();
//		}
	}

}
