package com.dsatab.data;

import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;
import com.dsatab.util.Util;
import com.gandulf.guilib.util.MathUtil;

public class Attribute extends BaseProbe implements Value, Cloneable {

	private static final String CONSTANT_BE = "BE";

	protected AttributeType type;

	protected Hero hero;

	protected Integer referenceValue;

	protected Integer originalBaseValue;
	protected Integer currentBaseValue;
	protected Integer value;
	protected Integer mod;
	protected Integer coreValue;
	protected String name;

	private boolean lazyInit = false;

	public Attribute(Hero hero) {
		this.hero = hero;
	}

	public AttributeType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Probe#getModificatorType()
	 */
	@Override
	public ModificatorType getModificatorType() {
		return ModificatorType.Attribute;
	}

	public float getRatio() {
		return MathUtil.getRatio(getValue(), getReferenceValue());
	}

	public void setType(AttributeType type) {
		this.type = type;
		if (this.type != null) {
			if (this.type == AttributeType.Ausweichen) {
				probeInfo.setErschwernis(0);
			}
			if (this.type.hasBe()) {
				probeInfo.applyBePattern(CONSTANT_BE);
			}
		}
	}

	public Hero getHero() {
		return hero;
	}

	@Override
	public String getName() {
		switch (type) {
		case pa:
			return "Parade Basiswert";
		case at:
			return "Attacke Basiswert";
		case fk:
			return "Fernkampf Basiswert";
		case ini:
			return "Initiative";
		case Lebensenergie:
			return "Lebensenergie Total";
		case Ausdauer:
			return "Ausdauer Total";
		case Astralenergie:
			return "Astralenergie Total";
		case Karmaenergie:
			return "Karmaenergie Total";
		default:
			return name;
		}

	}

	public void setName(String name) {
		this.name = name;
	}

	private void lazyInit() {
		if (lazyInit)
			return;

		if (value != null) {
			if (mod != null)
				value += mod;

			value += getBaseValue();

			if (getReferenceValue() == null)
				setReferenceValue(value);
		}
		lazyInit = true;
	}

	@Override
	public ProbeType getProbeType() {
		return ProbeType.TwoOfThree;
	}

	@Override
	public Integer getProbeBonus() {
		return null;
	}

	@Override
	public Integer getProbeValue(int i) {
		return hero.getAttributeValue(type);
	}

	@Override
	public Integer getValue() {
		if (!lazyInit)
			lazyInit();
		return value;
	}

	public Integer getMod() {
		return mod;
	}

	public void setMod(Integer mod) {
		this.mod = mod;
	}

	@Override
	public void setValue(Integer value) {
		if (!Util.equalsOrNull(this.value, value)) {
			this.value = value;
			hero.fireValueChangedEvent(this);
		}
	}

	/**
	 * Checks wether the base value has changed
	 * 
	 * @return
	 */
	public boolean checkBaseValue() {
		currentBaseValue = null;

		int currentBaseValue = getBaseValue();

		if (currentBaseValue != this.originalBaseValue) {
			this.originalBaseValue = currentBaseValue;
			hero.fireValueChangedEvent(this);
			return true;
		} else {
			return false;
		}
	}

	public boolean checkValue(Integer newRefValue) {
		boolean changed = false;

		if (!Util.equalsOrNull(getReferenceValue(), newRefValue)) {
			setReferenceValue(newRefValue);
			changed = true;
		}
		Integer value = getValue();
		if (value != null) {
			int max = getMaximum();
			int min = getMinimum();
			if (value > max) {
				setValue(max);
				changed = true;
			} else if (value < min) {
				setValue(min);
				changed = true;
			}
		}
		return changed;
	}

	public int getBaseValue() {

		if (currentBaseValue == null) {
			currentBaseValue = 0;
			if (hero != null) {

				switch (type) {
				case Lebensenergie_Aktuell:
				case Lebensenergie:
					currentBaseValue = (int) Math.round((hero.getAttributeValue(AttributeType.Konstitution) * 2 + hero
							.getAttributeValue(AttributeType.Körperkraft)) / 2.0);
					break;
				case Astralenergie_Aktuell:
				case Astralenergie:
					if (hero.hasFeature(FeatureType.GefäßDerSterne)) {
						currentBaseValue = (int) Math.round((hero.getAttributeValue(AttributeType.Mut)
								+ hero.getAttributeValue(AttributeType.Intuition)
								+ hero.getAttributeValue(AttributeType.Charisma) + hero
								.getAttributeValue(AttributeType.Charisma)) / 2.0);
					} else if (hero.hasFeature(FeatureType.Vollzauberer) || hero.hasFeature(FeatureType.Halbzauberer)
							|| hero.hasFeature(FeatureType.Viertelzauberer)
							|| hero.hasFeature(FeatureType.UnbewussterViertelzauberer)) {
						currentBaseValue = (int) Math.round((hero.getAttributeValue(AttributeType.Mut)
								+ hero.getAttributeValue(AttributeType.Intuition) + hero
								.getAttributeValue(AttributeType.Charisma)) / 2.0);
					}
					break;
				case Ausdauer_Aktuell:
				case Ausdauer:
					currentBaseValue = (int) Math.round((hero.getAttributeValue(AttributeType.Mut)
							+ hero.getAttributeValue(AttributeType.Konstitution) + hero
							.getAttributeValue(AttributeType.Gewandtheit)) / 2.0);
					break;
				case Magieresistenz:
					currentBaseValue = (int) Math.round((hero.getAttributeValue(AttributeType.Mut)
							+ hero.getAttributeValue(AttributeType.Klugheit) + hero
							.getAttributeValue(AttributeType.Konstitution)) / 5.0);
					break;
				case Ausweichen:
					currentBaseValue = (int) hero.getAttributeValue(AttributeType.pa);
					break;
				default:
					// do nothing
					break;
				}
			}
		}

		if (this.originalBaseValue == null)
			this.originalBaseValue = currentBaseValue;

		return currentBaseValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#reset()
	 */
	@Override
	public void reset() {
		setValue(getReferenceValue());
	}

	public void setReferenceValue(Integer referenceValue) {
		this.referenceValue = referenceValue;
	}

	@Override
	public Integer getReferenceValue() {

		switch (type) {
		case at: {
			int mu = hero.getAttributeValue(AttributeType.Mut);
			int ge = hero.getAttributeValue(AttributeType.Gewandtheit);
			int kk = hero.getAttributeValue(AttributeType.Körperkraft);
			return (int) Math.round((mu + ge + kk) / 5.0);
		}
		case pa: {
			int in = hero.getAttributeValue(AttributeType.Intuition);
			int ge = hero.getAttributeValue(AttributeType.Gewandtheit);
			int kk = hero.getAttributeValue(AttributeType.Körperkraft);
			return (int) Math.round((in + ge + kk) / 5.0);
		}
		case fk: {
			int in = hero.getAttributeValue(AttributeType.Intuition);
			int ff = hero.getAttributeValue(AttributeType.Fingerfertigkeit);
			int kk = hero.getAttributeValue(AttributeType.Körperkraft);
			return (int) Math.round((in + ff + kk) / 5.0);
		}
		case ini: {
			int mu = hero.getAttributeValue(AttributeType.Mut);
			int in = hero.getAttributeValue(AttributeType.Intuition);
			int ge = hero.getAttributeValue(AttributeType.Gewandtheit);
			return (int) Math.round((mu + mu + in + ge) / 5.0);
		}

		case Ausdauer:
		case Karmaenergie:
		case Astralenergie:
		case Lebensenergie:
			return null;
		case Behinderung:
			return hero.getArmorBe();
		default:
			return referenceValue;
		}
	}

	@Override
	public int getMinimum() {
		switch (type) {
		case Lebensenergie_Aktuell:
			return -10;
		default:
			return 0;
		}
	}

	@Override
	public int getMaximum() {
		int max = 0;

		switch (type) {
		case Lebensenergie_Aktuell:
			max = hero.getModifiedValue(AttributeType.Lebensenergie, false, false);
			break;
		case Astralenergie_Aktuell:
			max = hero.getModifiedValue(AttributeType.Astralenergie, false, false);
			break;
		case Ausdauer_Aktuell:
			max = hero.getModifiedValue(AttributeType.Ausdauer, false, false);
			break;
		case Karmaenergie_Aktuell:
			max = hero.getModifiedValue(AttributeType.Karmaenergie, false, false);
			break;
		case Behinderung:
			max = 15;
			break;
		case Mut:
		case Klugheit:
		case Intuition:
		case Charisma:
		case Fingerfertigkeit:
		case Gewandtheit:
		case Konstitution:
		case Körperkraft:
		case Sozialstatus:
			max = 25;
			break;
		case Lebensenergie:
		case Astralenergie:
		case Ausdauer:
		case Karmaenergie:
			max = 200;
			break;
		default:
			max = 99;
			break;
		}

		return max;
	}

	@Override
	public String toString() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Attribute clone() {
		try {
			return (Attribute) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
