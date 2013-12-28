package com.dsatab.data.filter;

import java.util.Locale;

import com.dsatab.data.Markable;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.listable.Listable;
import com.dsatab.view.ListSettings;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.data.OpenFilter;

public class FilterableListFilter<T extends Listable> extends OpenFilter<T> {

	private ListSettings settings;

	/**
	 * 
	 */
	public FilterableListFilter(OpenArrayAdapter<T> list) {
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
				valid = settings.isVisible((Markable) m);
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

}
