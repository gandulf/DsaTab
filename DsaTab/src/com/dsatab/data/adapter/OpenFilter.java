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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.text.TextUtils;
import android.widget.Filter;

/**
 * @author Ganymede
 * 
 */
public class OpenFilter<T> extends Filter {

	protected String constraint;

	protected OpenArrayAdapter<T> list;

	/**
	 * 
	 */
	public OpenFilter(OpenArrayAdapter<T> list) {
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
	 * @see android.widget.Filter#publishResults(java.lang.CharSequence,
	 * android.widget.Filter.FilterResults)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		list.mObjects = (List<T>) results.values;

		if (results.count > 0) {
			list.notifyDataSetChanged(false);
		} else {
			list.notifyDataSetInvalidated();
		}
	}

}
