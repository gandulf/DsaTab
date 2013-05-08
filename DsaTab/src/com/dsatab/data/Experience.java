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
package com.dsatab.data;

public class Experience extends EditableValue {

	/**
	 * 
	 */
	public Experience(Hero hero, String name) {
		super(hero, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#setValue(java.lang.Integer)
	 */
	@Override
	public void setValue(Integer value) {
		Integer oldValue = getValue();

		super.setValue(value);

		int added = 0;
		if (oldValue != null && value != null)
			added = value - oldValue;
		else if (value != null)
			added = value;
		else if (oldValue != null)
			added = -oldValue;

		hero.getFreeExperience().addValue(added);
	}

}
