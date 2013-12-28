package com.dsatab.data.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.dsatab.data.Connection;
import com.dsatab.data.Event;
import com.dsatab.data.NotesItem;
import com.dsatab.data.enums.EventCategory;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.data.OpenFilter;

public class NotesListFilter extends OpenFilter<NotesItem> {

	private List<EventCategory> types;

	public NotesListFilter(OpenArrayAdapter<NotesItem> list) {
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
	public boolean filter(NotesItem m) {
		if (m instanceof Event) {
			return filterEvent((Event) m);
		} else if (m instanceof Connection) {
			return filterConection((Connection) m);
		} else {
			return true;
		}
	}

	public boolean filterConection(Connection m) {
		boolean valid = true;
		if (types != null) {
			boolean found = false;

			if (types.contains(m.getCategory())) {
				found = true;
			}

			valid &= found;
		}

		if (constraint != null) {
			valid &= m.getName().toLowerCase(Locale.GERMAN).startsWith(constraint);
		}

		return valid;
	}

	protected boolean filterEvent(Event m) {
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
