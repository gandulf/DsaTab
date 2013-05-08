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
package com.dsatab.data.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

import com.dsatab.data.enums.TalentType;
import com.j256.ormlite.field.DatabaseField;

/**
 * @author Ganymede
 * 
 */
public abstract class CloseCombatItem extends ItemSpecification {

	private static final String SEP = ",";

	@DatabaseField
	protected Integer bf;
	@DatabaseField
	protected Integer ini;
	@DatabaseField
	protected Integer wmAt;
	@DatabaseField
	protected Integer wmPa;

	// we need these wrapper since ormlite does not support collections of enums
	// yet
	@DatabaseField
	private String combatTalentTypesWrapper;

	private List<TalentType> combatTalentType = null;

	/**
	 * 
	 */
	public CloseCombatItem() {
		super(null, ItemType.Waffen, 0);
	}

	public CloseCombatItem(Item item, ItemType type, int version) {
		super(item, type, 0);
	}

	public Integer getBf() {
		return bf;
	}

	public void setBf(Integer bf) {
		this.bf = bf;
	}

	public Integer getIni() {
		return ini;
	}

	public void setIni(Integer ini) {
		this.ini = ini;
	}

	public Integer getWmAt() {
		return wmAt;
	}

	public void setWmAt(Integer wmAt) {
		this.wmAt = wmAt;
	}

	public Integer getWmPa() {
		return wmPa;
	}

	public void setWmPa(Integer wmPa) {
		this.wmPa = wmPa;
	}

	public TalentType getTalentType() {
		initTalentTypes();

		if (combatTalentType.isEmpty())
			return null;
		else
			return combatTalentType.get(0);
	}

	public List<TalentType> getTalentTypes() {
		initTalentTypes();
		return Collections.unmodifiableList(combatTalentType);
	}

	public void addTalentType(TalentType type) {
		initTalentTypes();
		if (!combatTalentType.contains(type)) {
			combatTalentType.add(type);

			if (TextUtils.isEmpty(combatTalentTypesWrapper))
				combatTalentTypesWrapper = type.name();
			else
				combatTalentTypesWrapper = combatTalentTypesWrapper.concat(SEP + type.name());
		}
	}

	private void initTalentTypes() {
		if (combatTalentType == null) {

			combatTalentType = new ArrayList<TalentType>();

			if (!TextUtils.isEmpty(combatTalentTypesWrapper)) {
				String[] types = combatTalentTypesWrapper.split(SEP);
				for (String type : types) {
					combatTalentType.add(TalentType.valueOf(type));
				}
			}
		}
	}

}
