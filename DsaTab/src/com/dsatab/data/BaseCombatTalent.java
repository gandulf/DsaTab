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
