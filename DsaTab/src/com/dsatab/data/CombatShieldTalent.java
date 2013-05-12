package com.dsatab.data;

import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.Position;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.UsageType;

public class CombatShieldTalent extends BaseCombatTalent {

	/**
	 * 
	 */
	public static final String PARIERWAFFENPARADE = "Parierwaffenparade";

	/**
	 * 
	 */
	public static final String SCHILDPARADE = "Schildparade";

	protected UsageType usageType;

	private int set;

	private String equippedName;

	public CombatShieldTalent(Hero hero, UsageType usageType, int set, String equippedName) {
		super(hero);
		this.usageType = usageType;
		this.set = set;
		this.equippedName = equippedName;

		if (UsageType.Paradewaffe == usageType)
			setType(TalentType.Dolche);
		else
			setType(TalentType.Raufen);

		this.value = 0;
	}

	@Override
	public String getName() {
		switch (usageType) {
		case Schild:
			return SCHILDPARADE;
		case Paradewaffe:
			return PARIERWAFFENPARADE;
		}
		return null;
	}

	@Override
	public Probe getAttack() {
		return null;
	}

	@Override
	public Probe getDefense() {
		return this;
	}

	public TalentType getTalentType() {
		return type;
	}

	@Override
	public ProbeType getProbeType() {
		return ProbeType.TwoOfThree;
	}

	@Override
	public Integer getProbeValue(int i) {
		return getValue();
	}

	@Override
	public Integer getProbeBonus() {
		return null;
	}

	@Override
	public Integer getValue() {

		if (this.value != null) {
			return this.value + getBaseValue();
		} else
			return null;
	}

	protected int getBaseValue() {
		int baseValue = 0;

		if (UsageType.Paradewaffe == usageType) {
			if (hero != null) {
				// der basiswert eine paradewaffe ist der paradewert der
				// geführten
				// hauptwaffe -/+ evtl. parierwaffen WdS 75
				EquippedItem paradeItem = hero.getEquippedItem(set, equippedName);
				if (paradeItem != null && paradeItem.getSecondaryItem() != null
						&& (hero.hasFeature(FeatureType.ParierwaffenI) || hero.hasFeature(FeatureType.ParierwaffenII))) {
					EquippedItem equippedWeapon = paradeItem.getSecondaryItem();
					// check wether mainweapon has a defense value
					// TODO modifiers on main weapon should be considered here
					// too!!!
					if (equippedWeapon.getTalent() instanceof CombatMeleeTalent
							&& equippedWeapon.getTalent().getDefense() != null) {
						baseValue = equippedWeapon.getTalent().getDefense().getValue();

						int weaponPaMod = hero.getModifier(new CombatProbe(equippedWeapon, false));
						baseValue += weaponPaMod;

					} else {
						baseValue = hero.getAttributeValue(AttributeType.pa);
					}
				} else {
					baseValue = hero.getAttributeValue(AttributeType.pa);
				}
			}
		} else {
			baseValue = hero.getAttributeValue(AttributeType.pa);
		}

		return baseValue;
	}

	@Override
	public Position getPosition(int w20) {
		if (DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_MORE_TARGET_ZONES,
				false)) {
			return Position.box_rauf_hruru[w20];
		} else {
			return Position.official[w20];
		}
	}

	@Override
	public String toString() {
		return getName();
	}

	public UsageType getUsageType() {
		return usageType;
	}

}
