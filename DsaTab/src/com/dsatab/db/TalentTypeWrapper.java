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
package com.dsatab.db;

import com.dsatab.data.enums.TalentType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Ganymede
 * 
 */
@DatabaseTable
public class TalentTypeWrapper {

	@DatabaseField(id = true)
	private String combatTalentType;

	/**
	 * 
	 */
	public TalentTypeWrapper() {
	}

	public TalentTypeWrapper(TalentType type) {
		if (type != null)
			combatTalentType = type.name();
		else
			combatTalentType = null;
	}

	public TalentType get() {
		if (combatTalentType != null)
			return TalentType.valueOf(combatTalentType);
		else
			return null;
	}

}
