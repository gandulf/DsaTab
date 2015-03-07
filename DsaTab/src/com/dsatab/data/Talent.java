package com.dsatab.data;

import com.dsatab.data.enums.TalentType;
import com.dsatab.data.listable.Listable;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;

import java.util.Comparator;
import java.util.EnumSet;

public class Talent extends MarkableElement implements Value, Listable {

	private static final long serialVersionUID = -3361581759226651028L;

	public static final Comparator<Talent> NAME_COMPARATOR = new Comparator<Talent>() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Talent object1, Talent object2) {
			return object1.getName().compareTo(object2.getName());
		}

	};

	protected Integer value;

	protected Integer complexity;

	protected TalentType type;

	private String talentSpezialisierung;

	public enum Flags {
		Meisterhandwerk, Begabung, Talentschub, TalentSpezialisierung
	}

	private EnumSet<Flags> flags = EnumSet.noneOf(Flags.class);

	public Talent(AbstractBeing hero, TalentType type) {
		super(hero);
		setType(type);
	}

	@Override
	public String getName() {
		return type.xmlName();
	}

	public TalentType getType() {
		return type;
	}

	protected void setType(TalentType type) {
		this.type = type;
	}

	public Integer getComplexity() {
		return complexity;
	}

	public void setComplexity(Integer complexity) {
		this.complexity = complexity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Probe#getModificatorType()
	 */
	@Override
	public ModificatorType getModificatorType() {
		return ModificatorType.Talent;
	}

	public void setProbeBe(String be) {
		this.probeInfo.applyBePattern(be);
	}

	public void setProbePattern(String pattern) {
		this.probeInfo.applyProbePattern(pattern);
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

	@Override
	public ProbeType getProbeType() {
		return ProbeType.ThreeOfThree;
	}

	public String getTalentSpezialisierung() {
		return talentSpezialisierung;
	}

	public void setTalentSpezialisierung(String talentSpezialisierung) {
		this.talentSpezialisierung = talentSpezialisierung;
	}

	public boolean hasFlag(Flags flag) {
		return flags.contains(flag);
	}

	public void addFlag(Flags flag) {
		flags.add(flag);
	}

	@Override
	public Integer getProbeBonus() {
		return getValue();
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public void setValue(Integer value) {
		Integer oldValue = getValue();
		this.value = value;

		if (oldValue != this.value && being != null)
			being.fireValueChangedEvent(this);
	}

	@Override
	public Integer getReferenceValue() {
		return getValue();
	}

	@Override
	public int getMinimum() {
		return 0;
	}

	@Override
	public int getMaximum() {
		return 25;
	}

	@Override
	public String toString() {
		return getName();
	}

}
