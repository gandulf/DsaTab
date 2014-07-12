package com.dsatab.data;

import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.Position;
import com.dsatab.data.enums.TalentType;

public class CombatDistanceTalent extends BaseCombatTalent implements Value {

	private Integer referenceValue;

	public CombatDistanceTalent(Hero hero, TalentType type) {
		super(hero, type);
	}

	@Override
	public Probe getAttack() {
		return this;
	}

	@Override
	public Probe getDefense() {
		return null;
	}

	@Override
	public int getMinimum() {
		return 0;
	}

	@Override
	public int getMaximum() {
		return 32;
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
	public Integer getReferenceValue() {
		if (referenceValue == null)
			this.referenceValue = getValue();
		return referenceValue;
	}

	@Override
	public Integer getValue() {
		if (value != null) {
			return value + getBaseValue();
		} else
			return null;
	}

	public int getBaseValue() {
		int baseValue = 0;

		if (type == TalentType.Lanzenreiten)
			baseValue = being.getAttributeValue(AttributeType.at);
		else
			baseValue = being.getAttributeValue(AttributeType.fk);

		return baseValue;
	}

	@Override
	public void setValue(Integer value) {
		if (this.value != value) {
			this.value = value;

			being.fireValueChangedEvent(this);
		}
	}

	@Override
	public Position getPosition(int w20) {
		if (DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_MORE_TARGET_ZONES,
				false)) {
			return Position.fern_wurf[w20];
		} else {
			return Position.official[w20];
		}
	}

	@Override
	public String toString() {
		return getName();
	}
}
