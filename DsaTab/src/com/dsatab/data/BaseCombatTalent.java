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

import com.dsatab.data.enums.TalentType;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;

/**
 * 
 * 
 */
public abstract class BaseCombatTalent extends Talent implements CombatTalent {

	/**
	 * 
	 */
	public BaseCombatTalent(Hero hero) {
		super(hero);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Talent#setType(com.dsatab.data.TalentType)
	 */
	@Override
	public void setType(TalentType type) {
		super.setType(type);
		this.probeInfo.applyBePattern(type.getBe());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Talent#getModificatorType()
	 */
	@Override
	public ModificatorType getModificatorType() {
		return ModificatorType.CombatTalent;
	}

}
