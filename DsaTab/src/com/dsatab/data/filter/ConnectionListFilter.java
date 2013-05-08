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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.dsatab.data.Connection;
import com.dsatab.data.adapter.OpenArrayAdapter;
import com.dsatab.data.adapter.OpenFilter;
import com.dsatab.data.enums.EventCategory;

/**
 * @author Ganymede
 * 
 */
public class ConnectionListFilter extends OpenFilter<Connection> {

	private List<EventCategory> types;

	/**
	 * 
	 */
	public ConnectionListFilter(OpenArrayAdapter<Connection> list) {
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

	protected boolean isFilterSet() {
		return constraint != null || types != null;
	}

	public boolean filter(Connection m) {
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

}
