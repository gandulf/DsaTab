package com.dsatab.data.filter;

import java.util.Locale;

import com.dsatab.data.adapter.OpenArrayAdapter;
import com.dsatab.data.adapter.OpenFilter;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.view.FightFilterSettings;

public class EquippedItemListFilter extends OpenFilter<EquippedItem> {

	private FightFilterSettings settings;

	/**
	 * 
	 */
	public EquippedItemListFilter(OpenArrayAdapter<EquippedItem> list) {
		super(list);
		settings = new FightFilterSettings();
	}

	public FightFilterSettings getSettings() {
		return settings;
	}

	@Override
	protected boolean isFilterSet() {
		return constraint != null || (settings != null && !settings.isShowArmor());
	}

	@Override
	public boolean filter(EquippedItem m) {
		boolean valid = true;
		if (settings != null) {
			if (m.getItemSpecification() instanceof Armor)
				valid &= settings.isShowArmor();

		}

		if (constraint != null) {
			valid &= m.getName().toLowerCase(Locale.GERMAN).startsWith(constraint);
		}

		return valid;
	}

}
