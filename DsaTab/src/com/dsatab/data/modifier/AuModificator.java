package com.dsatab.data.modifier;

import java.util.Arrays;
import java.util.List;

import com.dsatab.data.Attribute;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.CombatShieldTalent;
import com.dsatab.data.Hero;
import com.dsatab.data.Modifier;
import com.dsatab.data.Probe;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;

public class AuModificator extends AbstractModificator {

	public static final float LEVEL_1 = 0.33f;
	public static final float LEVEL_2 = 0.25f;

	public AuModificator(Hero hero) {
		super(hero);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#getAffectedModifierTypes()
	 */
	@Override
	public List<ModificatorType> getAffectedModifierTypes() {
		return Arrays.asList(ModificatorType.Attribute, ModificatorType.CombatTalent, ModificatorType.DistanceWeapon,
				ModificatorType.Shield, ModificatorType.Weapon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.AbstractModificator#isActive()
	 */
	@Override
	public boolean isActive() {
		return hero.getHeroConfiguration().isAuModifierActive();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.AbstractModificator#setActive(boolean)
	 */
	@Override
	public void setActive(boolean active) {
		hero.getHeroConfiguration().setAuModifierActive(active);
		super.setActive(active);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#affects(com.dsatab.data.Probe)
	 */
	@Override
	public boolean affect(Probe probe) {
		if (probe instanceof CombatProbe || probe instanceof CombatShieldTalent
				|| probe instanceof CombatDistanceTalent || probe instanceof CombatMeleeAttribute) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#affects(com.dsatab.data.enums. AttributeType)
	 */
	@Override
	public boolean affects(AttributeType type) {
		return (type == AttributeType.ini || type == AttributeType.Initiative_Aktuell || AttributeType.isFight(type));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#fulfills(com.dsatab.data.Hero)
	 */
	@Override
	public boolean fulfills() {
		float ratio = hero.getRatio(AttributeType.Ausdauer_Aktuell);
		return ratio < LEVEL_1;
	}

	@Override
	public String getModificatorName() {

		float ratio = hero.getRatio(AttributeType.Ausdauer_Aktuell);
		if (ratio < LEVEL_2) {
			return "Ausdauer < 1/4";
		} else if (ratio < LEVEL_1) {
			return "Ausdauer < 1/3";
		} else {
			return null;
		}

	}

	@Override
	public String getModificatorInfo() {
		String info;
		float ratio = hero.getRatio(AttributeType.Ausdauer_Aktuell);
		if (ratio < LEVEL_2) {
			info = "AT,PA,INI -2";
		} else if (ratio < LEVEL_1) {
			info = "AT,PA,INI -1";
		} else {
			info = "";
		}
		return info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#getModifierValue(com.dsatab.data .Probe)
	 */
	@Override
	public int getModifierValue(Probe probe) {
		if (isActive()) {
			int modifier = 0;

			if (probe instanceof CombatProbe || probe instanceof CombatShieldTalent
					|| probe instanceof CombatDistanceTalent || probe instanceof CombatMeleeAttribute) {
				float ratio = hero.getRatio(AttributeType.Ausdauer_Aktuell);
				if (ratio < LEVEL_2) {
					modifier = -2;
				} else if (ratio < LEVEL_1) {
					modifier = -1;
				}
			} else if (probe instanceof Attribute) {
				Attribute attr = (Attribute) probe;
				return getModifierValue(attr.getType());
			}
			return modifier;
		} else {
			return 0;
		}
	}

	@Override
	public Modifier getModifier(AttributeType type) {
		int modifierValue = getModifierValue(type);

		if (modifierValue != 0) {
			this.modifier.setModifier(modifierValue);
			this.modifier.setTitle(getModificatorName());
			this.modifier.setDescription(getModificatorInfo());

			return modifier;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#getModifierValue(com.dsatab.data .enums.AttributeType)
	 */
	@Override
	public int getModifierValue(AttributeType type) {

		if (isActive()) {
			int modifier = 0;
			float ratio = hero.getRatio(AttributeType.Ausdauer_Aktuell);

			if (ratio < LEVEL_2) {
				if (type == AttributeType.ini || type == AttributeType.Initiative_Aktuell
						|| AttributeType.isFight(type)) {
					modifier = -2;
				}
			} else if (ratio < LEVEL_1) {
				if (type == AttributeType.ini || type == AttributeType.Initiative_Aktuell
						|| AttributeType.isFight(type)) {
					modifier = -1;
				}
			}
			return modifier;
		} else {
			return 0;
		}
	}

}
