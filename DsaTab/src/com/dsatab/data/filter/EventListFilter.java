package com.dsatab.data.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.dsatab.data.Event;
import com.dsatab.data.adapter.OpenArrayAdapter;
import com.dsatab.data.adapter.OpenFilter;
import com.dsatab.data.enums.EventCategory;

public class EventListFilter extends OpenFilter<Event> {

	private List<EventCategory> types;

	/**
	 * 
	 */
	public EventListFilter(OpenArrayAdapter<Event> list) {
		super(list);
	}

	public List<EventCategory> getTypes() {
		return types;
	}

	public void setTypes(List<EventCategory> type) {
		this.types = type;
	}

	public void setType(EventCategory type) {
		if (type != null)
			this.types = Arrays.asList(type);
		else
			this.types = null;
	}

	@Override
	protected boolean isFilterSet() {
		return constraint != null || (types != null);
	}

	@Override
	public boolean filter(Event m) {
		boolean valid = true;
		if (types != null) {
			boolean found = false;

			if (types.contains(m.getCategory())) {
				found = true;
			}

			valid &= found;
		}

		if (constraint != null) {
			valid &= m.getComment().toLowerCase(Locale.GERMAN).startsWith(constraint);
		}

		return valid;
	}

}
