package com.dsatab.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;

import android.text.TextUtils;

import com.bugsense.trace.BugSenseHandler;
import com.dsatab.common.DsaTabRuntimeException;
import com.dsatab.data.enums.ArtGroupType;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.xml.DataManager;

public class Art extends MarkableElement implements Value {

	public static final String LITURGIE_PREFIX = "Liturgie: ";
	public static final String RITUAL_PREFIX = "Ritual: ";
	public static final String RITUAL_SEHER_PREFIX = "Seher: ";
	public static final String STABZAUBER_PREFIX = "Stabzauber: ";
	public static final String SCHALENZAUBER_PREFIX = "Schalenzauber: ";
	public static final String SCHLANGENRING_ZAUBER_PEFIX = "Schlangenring-Zauber: ";
	public static final String SCHUPPENBEUTEL_PREFIX = "Schuppenbeutel: ";
	public static final String TROMMELZAUBER_PREFIX = "Trommelzauber: ";
	public static final String RUNEN_PREFIX = "Runen: ";
	public static final String KUGELZAUBER_PREFIX = "Kugelzauber: ";
	public static final String KRISTALLOMANTISCHES_RITUAL_PREFIX = "Kristallomantisches Ritual: ";
	public static final String HEXENFLUCH_PREFIX = "Hexenfluch: ";
	public static final String GABE_DES_ODUN_PREFIX = "Gabe des Odûn: ";
	public static final String DRUIDISCHES_HERRSCHAFTSRITUAL_PREFIX = "Druidisches Herrschaftsritual: ";
	public static final String DRUIDISCHES_DOLCHRITUAL_PREFIX = "Druidisches Dolchritual: ";
	public static final String ZAUBERTANZ_PREFIX = "Zaubertanz: ";
	public static final String ZAUBERZEICHEN_PREFIX = "Zauberzeichen: ";
	public static final String ZIBILJA_RITUAL_PREFIX = "Zibilja-Ritual: ";
	public static final String ELFENLIEDER_PREFIX = "Elfenlied: ";
	public static final String KEULEN_RITUAL_PREFIX = "Keulenritual: ";
	public static final String SZEPTER_RITUAL_PREFIX = "Szepter: ";
	public static final String RITUELLE_STRAFE_PREFIX = "Rituelle Strafe: ";
	public static final String KISTALLPENDEL_PREFIX = "Kristallpendel: ";
	public static final String TAPASUUL_PREFIX = "Tapasuul: ";

	public static final Comparator<Art> NAME_COMPARATOR = new Comparator<Art>() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Art object1, Art object2) {
			return object1.getName().compareTo(object2.getName());
		}

	};

	private ArtGroupType groupType;

	private Hero hero;

	private String name;
	private FeatureType type;

	private ArtInfo info;

	private Talent kenntnis;

	public enum Flags {
		Begabung
	}

	private EnumSet<Flags> flags = EnumSet.noneOf(Flags.class);

	private boolean customProbe;

	public Art(Hero hero, String name) {
		super();
		this.hero = hero;
		setName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Probe#getModificatorType()
	 */
	@Override
	public ModificatorType getModificatorType() {
		return ModificatorType.Art;
	}

	public static String normalizeName(String name) {
		String grade;

		name = name.trim();
		ArtGroupType type = ArtGroupType.getTypeOfArt(name);
		if (type != null) {
			name = type.truncateName(name);
		}

		if (name.endsWith(")")) {
			grade = name.substring(name.lastIndexOf("(") + 1);
			grade = grade.substring(0, grade.length() - 1);

			// we acutally found a grade (I)
			if (Util.gradeToInt(grade) >= 0) {
				name = name.substring(0, name.lastIndexOf("(")).trim();
			} else {
				grade = null;
			}
		}

		return name;
	}

	public FeatureType getType() {
		return type;
	}

	public ArtGroupType getGroupType() {
		return groupType;
	}

	protected void setGroupType(ArtGroupType type) {
		this.groupType = type;

		switch (type) {
		case Ritual:
			kenntnis = hero.getTalent(TalentType.GeisterRufen);
			break;
		default:
			if (type.talentType() != null)
				kenntnis = hero.getTalent(type.talentType());
			break;
		}

		if (kenntnis != null) {
			probeInfo = kenntnis.getProbeInfo().clone();
		}
	}

	public String getFullName() {
		if (info != null)
			return info.getFullName();
		else
			return name;

	}

	public boolean hasFlag(Flags flag) {
		return flags.contains(flag);
	}

	public void addFlag(Flags flag) {
		flags.add(flag);
	}

	public String getName() {
		if (info != null) {
			return info.getName();
		} else {
			return name;
		}
	}

	public void setName(String name) {
		this.name = name.trim();
		this.type = FeatureType.byXmlName(this.name);

		setGroupType(ArtGroupType.getTypeOfArt(name));
		if (groupType != null) {
			name = groupType.truncateName(name);
		} else {
			throw new DsaTabRuntimeException("Unknown Art type for: " + name);
		}

		String grade = null;

		// we have a grade specification in the name: Erdsegen (III)
		if (name.endsWith(")")) {
			grade = name.substring(name.lastIndexOf("(") + 1);
			grade = grade.substring(0, grade.length() - 1);

			// we acutally found a grade (I)
			if (Util.gradeToInt(grade) >= 0) {
				name = name.substring(0, name.lastIndexOf("(")).trim();
			} else {
				grade = null;
			}
		}

		// TODO Check for art that do not have a grade on first grade. to get
		// loaded right!!!

		if (grade == null) {
			info = DataManager.getArtByName(name);
		} else {
			info = DataManager.getArtByNameAndGrady(name, grade);
		}

		if (info == null) {
			info = new ArtInfo();
			info.setName(name);
			if (grade != null) {
				info.setGrade(Util.gradeToInt(grade));
			}
			BugSenseHandler.sendEvent("No unique art found for " + name + " : " + grade + " creating a new one");
			Debug.warning("No unique art found for " + name + " : " + grade + " creating a new one");
		}

		setProbePattern(info.getProbe());

		if (probeInfo.getErschwernis() == null && info.getGrade() > 0) {
			probeInfo.setErschwernis(info.getGrade() * 2 - 2);
		}
	}

	public void setProbePattern(String pattern) {
		probeInfo.applyProbePattern(pattern);
		if (!TextUtils.isEmpty(pattern)) {
			info.setProbe(pattern);
		}

		if (getArtTalent() != null) {
			customProbe = !Arrays.equals(probeInfo.getAttributeTypes(), getArtTalent().getProbeInfo()
					.getAttributeTypes());
		} else {
			customProbe = true;
		}
	}

	public ArtInfo getInfo() {
		return info;
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

	private Talent getArtTalent() {
		return kenntnis;
	}

	public ProbeInfo getProbeInfo() {
		return probeInfo;
	}

	public boolean hasCustomProbe() {
		return customProbe;
	}

	@Override
	public ProbeType getProbeType() {
		return ProbeType.ThreeOfThree;
	}

	public Integer getProbeValue(int i) {
		if (probeInfo.getAttributeTypes() != null) {
			AttributeType type = probeInfo.getAttributeTypes()[i];
			return hero.getModifiedValue(type, false, false);
		} else if (getArtTalent() != null) {
			return getArtTalent().getProbeValue(i);
		} else {
			return null;
		}
	}

	@Override
	public Integer getProbeBonus() {
		return getValue();
	}

	public Integer getValue() {
		if (getArtTalent() != null)
			return getArtTalent().getValue();
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#setValue(java.lang.Integer)
	 */
	@Override
	public void setValue(Integer value) {
		// todo cannot change value of liturgie
	}

	public Integer getReferenceValue() {
		return getValue();
	}

	public int getMinimum() {
		return 0;
	}

	public int getMaximum() {
		return 25;
	}

}
