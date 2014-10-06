package com.dsatab.data;

import java.io.Serializable;
import java.util.Comparator;
import java.util.EnumSet;

import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;

import com.dsatab.data.listable.Listable;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;
import com.dsatab.db.DataManager;
import com.dsatab.exception.SpellUnknownException;
import com.dsatab.util.Debug;
import com.dsatab.util.StyleableSpannableStringBuilder;

public class Spell extends MarkableElement implements Value, Listable, Serializable {

	private static final long serialVersionUID = 1278111102609178789L;

	public static final Comparator<Spell> NAME_COMPARATOR = new Comparator<Spell>() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Spell object1, Spell object2) {
			return object1.getName().compareTo(object2.getName());
		}

	};

	private Integer value;
	private SpellInfo info;

	private String comments;
	private String variant;
	private String zauberSpezialisierung;

	public enum Flags {
		Begabung, ÜbernatürlicheBegabung, Hauszauber, ZauberSpezialisierung
	}

	private EnumSet<Flags> flags = EnumSet.noneOf(Flags.class);

	public Spell(Hero hero, String name) {
		super(hero);
		setName(name);
	}

	@Override
	public String getName() {
		return info.getName();
	}

	public CharSequence getTitle() {
		StyleableSpannableStringBuilder sb = new StyleableSpannableStringBuilder();
		sb.append(getName());

		StringBuilder addons = new StringBuilder();
		if (hasFlag(Spell.Flags.ZauberSpezialisierung) || !TextUtils.isEmpty(getZauberSpezialisierung())) {

			addons.append(", Spez.");
			if (!TextUtils.isEmpty(getZauberSpezialisierung())) {
				addons.append(" " + getZauberSpezialisierung());
			}
		}
		if (hasFlag(Spell.Flags.Hauszauber)) {
			addons.append(", Hauszauber");
		}
		if (hasFlag(Spell.Flags.ÜbernatürlicheBegabung)) {
			addons.append(", Übernat. Begabung");
		}
		if (hasFlag(Spell.Flags.Begabung)) {
			addons.append(", Begabung");
		}
		if (addons.length() > 0) {
			addons.delete(0, 2);
			addons.append(")");
			addons.insert(0, " (");
			sb.appendWithStyle(new RelativeSizeSpan(0.5f), addons);
		}

		return sb;
	}

	public void setName(String name) {
		name = name.trim();

		this.info = DataManager.getSpellByName(name);
		if (info == null) {
			Debug.error(new SpellUnknownException(name));

			info = new SpellInfo();
			info.setName(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Probe#getModificatorType()
	 */
	@Override
	public ModificatorType getModificatorType() {
		return ModificatorType.Spell;
	}

	public boolean hasFlag(Flags flag) {
		return flags.contains(flag);
	}

	public void addFlag(Flags flag) {
		flags.add(flag);
	}

	public SpellInfo getInfo() {
		return info;
	}

	@Override
	public ProbeType getProbeType() {
		return ProbeType.ThreeOfThree;
	}

	public String getZauberSpezialisierung() {
		return zauberSpezialisierung;
	}

	public void setZauberSpezialisierung(String zauberSpezialisierung) {
		this.zauberSpezialisierung = zauberSpezialisierung;
	}

	public void setProbePattern(String pattern) {
		this.probeInfo.applyProbePattern(pattern);
		if (!TextUtils.isEmpty(pattern)) {
			info.setProbe(pattern);
		}
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

	public void fireValueChangedEvent() {
		if (being != null) {
			being.fireValueChangedEvent(this);
		}
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

		if (oldValue != this.value)
			fireValueChangedEvent();
	}

	public void setInfo(SpellInfo info) {
		this.info = info;
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

	public String getComments() {
		if (TextUtils.isEmpty(comments))
			return info != null ? info.getComments() : null;
		else
			return comments;
	}

	public void setComments(String comment) {
		this.comments = comment;

	}

	public String getVariant() {
		if (TextUtils.isEmpty(variant))
			return info != null ? info.getVariant() : null;
		else
			return variant;
	}

	public void setVariant(String s) {
		this.variant = s;
	}

}
