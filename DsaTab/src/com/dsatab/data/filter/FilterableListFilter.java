package com.dsatab.data.filter;

import java.util.Locale;

import com.dsatab.data.Markable;
import com.dsatab.data.adapter.OpenArrayAdapter;
import com.dsatab.data.adapter.OpenFilter;
import com.dsatab.view.ListFilterSettings;

public class FilterableListFilter<T extends Markable> extends OpenFilter<T> {

	private ListFilterSettings settings;

	/**
	 * 
	 */
	public FilterableListFilter(OpenArrayAdapter<T> list) {
		super(list);
	}

	@Override
	protected boolean isFilterSet() {
		return constraint != null || (settings != null && !settings.isAllVisible());
	}

	public ListFilterSettings getSettings() {
		return settings;
	}

	public void setSettings(ListFilterSettings settings) {
		this.settings = settings;
	}

	@Override
	public boolean filter(Markable m) {
		boolean valid = true;
		if (settings != null) {
			valid = settings.isVisible(m);
		}

		if (constraint != null) {
			valid &= m.getName().toLowerCase(Locale.GERMAN).startsWith(constraint);
		}

		return valid;
	}

}
