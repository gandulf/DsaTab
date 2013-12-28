package com.dsatab.data;

import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.listable.Listable;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;
import com.dsatab.util.Util;
import com.gandulf.guilib.util.MathUtil;

public class Attribute extends BaseProbe implements Value, Cloneable, Listable {

	private static final String CONSTANT_BE = "BE";

	protected AttributeType type;

	protected AbstractBeing being;

	protected Integer referenceValue;

	protected Integer originalBaseValue;
	protected Integer currentBaseValue;
	protected Integer value;
	protected Integer mod;
	protected Integer coreValue;
	protected String name;

	private boolean absolute = false;
	private boolean lazyInit = false;

	public Attribute(AbstractBeing hero) {
		this.being = hero;
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

	public AbstractBeing getHero() {
		return being;
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

	public boolean isAbsolute() {
		return absolute;
	}

	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
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
		return being.getAttributeValue(type);
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
			being.fireValueChangedEvent(this);
		}
	}

	/**
	 * Checks wether the base value has changed
	 * 
	 * @return
	 */
	public boolean checkBaseValue() {
		if (absolute)
			return false;

		currentBaseValue = null;

		int currentBaseValue = getBaseValue();

		if (currentBaseValue != this.originalBaseValue) {
			this.originalBaseValue = currentBaseValue;
			being.fireValueChangedEvent(this);
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
		if (absolute)
			return 0;

		if (currentBaseValue == null) {
			currentBaseValue = 0;
			if (being != null) {

				switch (type) {
				case Lebensenergie_Aktuell:
				case Lebensenergie:
					currentBaseValue = (int) Math
							.round((being.getAttributeValue(AttributeType.Konstitution) * 2 + being
									.getAttributeValue(AttributeType.Körperkraft)) / 2.0);
					break;
				case Astralenergie_Aktuell:
				case Astralenergie:
					if (being.hasFeature(FeatureType.GefäßDerSterne)) {
						currentBaseValue = (int) Math.round((being.getAttributeValue(AttributeType.Mut)
								+ being.getAttributeValue(AttributeType.Intuition)
								+ being.getAttributeValue(AttributeType.Charisma) + being
								.getAttributeValue(AttributeType.Charisma)) / 2.0);
					} else if (being.hasFeature(FeatureType.Vollzauberer) || being.hasFeature(FeatureType.Halbzauberer)
							|| being.hasFeature(FeatureType.Viertelzauberer)
							|| being.hasFeature(FeatureType.UnbewussterViertelzauberer)) {
						currentBaseValue = (int) Math.round((being.getAttributeValue(AttributeType.Mut)
								+ being.getAttributeValue(AttributeType.Intuition) + being
								.getAttributeValue(AttributeType.Charisma)) / 2.0);
					}
					break;
				case Ausdauer_Aktuell:
				case Ausdauer:
					currentBaseValue = (int) Math.round((being.getAttributeValue(AttributeType.Mut)
							+ being.getAttributeValue(AttributeType.Konstitution) + being
							.getAttributeValue(AttributeType.Gewandtheit)) / 2.0);
					break;
				case Magieresistenz:
					currentBaseValue = (int) Math.round((being.getAttributeValue(AttributeType.Mut)
							+ being.getAttributeValue(AttributeType.Klugheit) + being
							.getAttributeValue(AttributeType.Konstitution)) / 5.0);
					break;
				case Ausweichen:
					currentBaseValue = (int) being.getAttributeValue(AttributeType.pa);
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
			int mu = being.getAttributeValue(AttributeType.Mut);
			int ge = being.getAttributeValue(AttributeType.Gewandtheit);
			int kk = being.getAttributeValue(AttributeType.Körperkraft);
			return (int) Math.round((mu + ge + kk) / 5.0);
		}
		case pa: {
			int in = being.getAttributeValue(AttributeType.Intuition);
			int ge = being.getAttributeValue(AttributeType.Gewandtheit);
			int kk = being.getAttributeValue(AttributeType.Körperkraft);
			return (int) Math.round((in + ge + kk) / 5.0);
		}
		case fk: {
			int in = being.getAttributeValue(AttributeType.Intuition);
			int ff = being.getAttributeValue(AttributeType.Fingerfertigkeit);
			int kk = being.getAttributeValue(AttributeType.Körperkraft);
			return (int) Math.round((in + ff + kk) / 5.0);
		}
		case ini: {
			int mu = being.getAttributeValue(AttributeType.Mut);
			int in = being.getAttributeValue(AttributeType.Intuition);
			int ge = being.getAttributeValue(AttributeType.Gewandtheit);
			return (int) Math.round((mu + mu + in + ge) / 5.0);
		}

		case Ausdauer:
		case Karmaenergie:
		case Astralenergie:
		case Lebensenergie:
			return null;
		case Behinderung:
			return being.getArmorBe();
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
			max = being.getModifiedValue(AttributeType.Lebensenergie, false, false);
			break;
		case Astralenergie_Aktuell:
			max = being.getModifiedValue(AttributeType.Astralenergie, false, false);
			break;
		case Ausdauer_Aktuell:
			max = being.getModifiedValue(AttributeType.Ausdauer, false, false);
			break;
		case Karmaenergie_Aktuell:
			max = being.getModifiedValue(AttributeType.Karmaenergie, false, false);
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
		case Initiative_Aktuell:
			max = 40;
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
