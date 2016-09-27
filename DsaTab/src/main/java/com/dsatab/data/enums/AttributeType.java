package com.dsatab.data.enums;


public enum AttributeType {

	Mut("MU", false, true), Klugheit("KL", false, true), Intuition("IN", false, true), Charisma("CH", false, true), Fingerfertigkeit(
			"FF", false, true), Gewandtheit("GE", true, true), Konstitution("KO", false, true), Körperkraft("KK",
			false, true), Sozialstatus("SO"), Lebensenergie("LE Total"), Ausdauer("AU Total"), Astralenergie("AE Total"), Karmaenergie(
			"KE Total"), Magieresistenz("MR"), ini("INI", true, true), at("AT", true, true, false), pa("PA", true,
			true, false), fk("FK", true, true, false),

	// Animal Attribute Types
	Loyalität("LO", false, true), Magieresistenz2("MR 2"), Rüstungsschutz("RS"), Gefahrenwert("Gefahrenwert"), Geschwindigkeit2(
			"GS 2"), Geschwindigkeit3("GS 3"), Fährtensuche("Fährtensuche"),

	// CUSTOM Attribute Types
	Lebensenergie_Aktuell("LE"), Karmaenergie_Aktuell("KE"), Astralenergie_Aktuell("AE"), Initiative_Aktuell("INI",
			true, false), Ausdauer_Aktuell("AU"), Geschwindigkeit("GS", true, false, false), Behinderung("BE", false), Ausweichen(
			"AW", true, true), Entrueckung("ENT", false), Verzueckung("VZ", false), Erschoepfung("ERS", false),

	// Attribute Types with no attribute object, just used for modificators
	Wundschwelle("WS", false, false, false);

	public static final AttributeType[] EIGENSCHAFTEN = { Mut, Klugheit, Intuition, Charisma, Fingerfertigkeit,
			Gewandtheit, Konstitution, Körperkraft };

	private String code = null;
	private boolean be;
	private boolean probable = false;
	private boolean editable = true;

	AttributeType() {
		this(null, false, false);
	}

	AttributeType(String code) {
		this(code, false);
	}

	AttributeType(String code, boolean be) {
		this(code, false, false);
	}

	AttributeType(String code, boolean be, boolean probe) {
		this(code, be, probe, true);
	}

	AttributeType(String code, boolean be, boolean probe, boolean editable) {
		this.code = code;
		this.be = be;
		this.probable = probe;
		this.editable = editable;
	}

	public static boolean isFight(AttributeType type) {

		switch (type) {
		case at:
		case pa:
		case fk:
		case ini:
		case Initiative_Aktuell:
		case Ausweichen:
			return true;
		default:
			return false;
		}
	}

	public static boolean isEigenschaft(AttributeType type) {
		switch (type) {
		case Mut:
		case Klugheit:
		case Intuition:
		case Charisma:
		case Fingerfertigkeit:
		case Gewandtheit:
		case Konstitution:
		case Körperkraft:
			return true;
		default:
			return false;

		}
	}

	public boolean hasBe() {
		return be;
	}

	public static AttributeType valueOfTrim(String value) {
		return AttributeType.valueOf(value.replace(" ", ""));
	}

	public static AttributeType byCode(String code) {

		if (code == null)
			return null;

		AttributeType attributeType;

		try {
			attributeType = AttributeType.valueOf(code);
		} catch (Exception e) {
			attributeType = null;
		}

		for (AttributeType attr : AttributeType.values()) {
			if (attr.code != null && attr.code.equalsIgnoreCase(code)) {
				attributeType = attr;
				break;
			}
		}

		return attributeType;
	}

	public boolean probable() {
		return probable;
	}

	public boolean editable() {
		return editable;
	}

	public String code() {
		return code;
	}
}
