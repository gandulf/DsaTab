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

import com.dsatab.data.Markable;
import com.dsatab.data.adapter.OpenArrayAdapter;
import com.dsatab.data.adapter.OpenFilter;
import com.dsatab.view.ListFilterSettings;

/**
 * @author Ganymede
 * 
 */
public class FilterableListFilter<T extends Markable> extends OpenFilter<T> {

	private ListFilterSettings settings;

	/**
	 * 
	 */
	public FilterableListFilter(OpenArrayAdapter<T> list) {
		super(list);
	}

	protected boolean isFilterSet() {
		return constraint != null || (settings != null && !settings.isAllVisible());
	}

	public ListFilterSettings getSettings() {
		return settings;
	}

	public void setSettings(ListFilterSettings settings) {
		this.settings = settings;
	}

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
