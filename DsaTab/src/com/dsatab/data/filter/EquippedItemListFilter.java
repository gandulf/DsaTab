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
package com.dsatab.data.filter;

import java.util.Locale;

import com.dsatab.data.adapter.OpenArrayAdapter;
import com.dsatab.data.adapter.OpenFilter;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.view.FightFilterSettings;

/**
 * @author Ganymede
 * 
 */
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

	public void setSettings(FightFilterSettings settings) {
		this.settings = settings;
	}

	protected boolean isFilterSet() {
		return constraint != null || (settings != null && !settings.isShowArmor());
	}

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
