package com.dsatab.data.filter;

import com.dsatab.data.Markable;
import com.dsatab.data.adapter.OpenRecyclerAdapter;
import com.dsatab.data.adapter.OpenRecyclerFilter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.listable.Listable;
import com.dsatab.data.notes.Connection;
import com.dsatab.data.notes.Event;
import com.dsatab.view.ListSettings;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.data.OpenFilter;

import java.util.Locale;

public class ListableListFilter<T extends Listable> extends OpenRecyclerFilter<T> {

	private ListSettings settings;

	public ListableListFilter(OpenRecyclerAdapter<?,T> list) {
		super(list);
		settings = new ListSettings();
	}

	@Override
	protected boolean isFilterSet() {
		return constraint != null || (settings != null && !settings.isAllVisible());
	}

	public ListSettings getSettings() {
		return settings;
	}

	public void setSettings(ListSettings settings) {
		this.settings = settings;
	}

	@Override
	public boolean filter(Listable m) {
		boolean valid = true;
		if (settings != null) {

			if (m instanceof Markable) {
				valid &= settings.isVisible((Markable) m);
			}

			if (m instanceof Event) {
				valid &= filterEvent((Event) m);
			} else if (m instanceof Connection) {
				valid &= filterConection((Connection) m);
			}
		}

		if (constraint != null) {
			if (m instanceof Markable) {
				Markable markable = (Markable) m;
				valid &= markable.getName().toLowerCase(Locale.GERMAN).startsWith(constraint);
			}

			if (m instanceof EquippedItem) {
				EquippedItem equippedItem = (EquippedItem) m;

				if (constraint != null) {
					valid &= equippedItem.getName().toLowerCase(Locale.GERMAN).startsWith(constraint);
				}
			}
		}

		return valid;
	}

	public boolean filterConection(Connection m) {
		boolean valid = true;
		if (settings.getEventCategories() != null) {
			boolean found = false;

			if (settings.getEventCategories().contains(m.getCategory())) {
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
		if (settings.getEventCategories() != null) {
			boolean found = false;

			if (settings.getEventCategories().contains(m.getCategory())) {
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
