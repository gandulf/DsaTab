package com.dsatab.xml;

import org.jdom2.Element;

public abstract class Xml {
	public static final String KEY_VALUE = "value";
	public static final String KEY_NAME = "name";
	public static final String KEY_MOD = "mod";
	public static final String KEY_GROSSE_MEDIDATION = "grossemeditation";

	public static final String KEY_TEXT = "text";
	public static final String KEY_TIME = "time";
	public static final String KEY_VERSION = "version";

	public static final String KEY_KOMMENTAR = "kommentar";
	public static final String KEY_KOMMENTARE = "kommentare";
	public static final String KEY_DSATAB_VALUE = "dsatab_value";
	public static final String KEY_GELDBOERSE = "geldboerse";
	public static final String KEY_HELDENAUSRUESTUNG = "heldenausruestung";
	public static final String KEY_SET = "set";
	public static final String KEY_ANZAHL = "anzahl";
	public static final String KEY_SLOT = "slot";
	public static final String KEY_GEGENSTAND = "gegenstand";
	public static final String KEY_HELD = "held";
	public static final String KEY_EIGENSCHAFT = "eigenschaft";
	public static final String KEY_ABENTEUERPUNKTE = "abenteuerpunkte";
	public static final String KEY_FREIE_ABENTEUERPUNKTE = "freieabenteuerpunkte";

	public static final String KEY_SF = "sf";
	public static final String KEY_SONDERFERTIGKEITEN = "sonderfertigkeiten";
	public static final String KEY_SONDERFERTIGKEIT = "sonderfertigkeit";

	public static final String KEY_VT = "vt";
	public static final String KEY_VORTEILE = "vorteile";
	public static final String KEY_VORTEIL = "vorteil";
	public static final String KEY_NACHTEIL = "nachteil";
	public static final String KEY_EREIGNIS = "ereignis";
	public static final String KEY_EREIGNISSE = "ereignisse";
	public static final String KEY_ABENTEUERPUNKTE_UPPER = "Abenteuerpunkte";
	public static final String KEY_OBJ = "obj";
	public static final String KEY_TALENT = "talent";

	public static final String KEY_KAMPF = "kampf";
	public static final String KEY_KAMPFWERTE = "kampfwerte";
	public static final String KEY_ZAUBER = "zauber";
	public static final String KEY_MUENZE = "muenze";
	public static final String KEY_WAEHRUNG = "waehrung";
	public static final String KEY_KULTUR = "kultur";
	public static final String KEY_PROBE = "probe";
	public static final String KEY_BE = "be";
	public static final String KEY_VERWENDUNGSART = "verwendungsArt";
	public static final String KEY_HAND = "hand";
	public static final String KEY_SCHILD = "schild";
	public static final String KEY_GROESSE = "groesse";
	public static final String KEY_ALTER = "alter";
	public static final String KEY_EYECOLOR = "augenfarbe";
	public static final String KEY_HAIRCOLOR = "haarfarbe";
	public static final String KEY_AUSSEHEN = "aussehen";
	public static final String KEY_GEWICHT = "gewicht";
	public static final String KEY_COMMENT = "kommentar";
	public static final String KEY_CARD_ID = "card_id";
	public static final String KEY_PATH = "path";

	public static final String KEY_PARADE = "parade";
	public static final String KEY_ATTACKE = "attacke";
	public static final String KEY_CELL_NUMBER = "cellNumber";
	public static final String KEY_SCREEN = "screen";

	public static final String KEY_RUESTUNG = "Rüstung";
	public static final String KEY_RUESTUNG_UE = "Ruestung";
	public static final String KEY_GESAMT_BE = "gesbe";
	public static final String KEY_RS = "rs";
	public static final String KEY_STERNE = "sterne";
	public static final String KEY_TEILE = "teile";
	public static final String KEY_PROFESSION = "profession";
	public static final String KEY_STRING = "string";
	public static final String KEY_AUSBILDUNGEN = "ausbildungen";
	public static final String KEY_AUSBILDUNG = "ausbildung";
	public static final String KEY_RASSE = "rasse";

	public static final String KEY_SCHILDWAFFE = "Schild";

	public static final String KEY_FERNKAMPWAFFE = "Fernkampfwaffe";
	public static final String KEY_TPMOD = "tpmod";

	public static final String KEY_NAHKAMPWAFFE = "Nahkampfwaffe";
	public static final String KEY_TREFFERPUNKTE = "trefferpunkte";
	public static final String KEY_TREFFERPUNKTE_MUL = "mul";
	public static final String KEY_TREFFERPUNKTE_DICE = "w";
	public static final String KEY_TREFFERPUNKTE_SUM = "sum";
	public static final String KEY_TREFFERPUNKTE_KK = "tpkk";
	public static final String KEY_TREFFERPUNKTE_KK_MIN = "kk";
	public static final String KEY_TREFFERPUNKTE_KK_STEP = "schrittweite";
	public static final String KEY_WAFFENMODIF = "wm";
	public static final String KEY_WAFFENMODIF_PA = "pa";
	public static final String KEY_WAFFENMODIF_AT = "at";
	public static final String KEY_BRUCHFAKTOR = "bf";
	public static final String KEY_BRUCHFAKTOR_AKT = "akt";
	public static final String KEY_INI_MOD = "inimod";
	public static final String KEY_INI_MOD_INI = "ini";
	public static final String KEY_MOD_ALLGEMEIN = "modallgemein";
	public static final String KEY_ANMERKUNGEN = "anmerkungen";
	public static final String KEY_HAUSZAUBER = "hauszauber";
	public static final String KEY_KOSTEN = "kosten";
	public static final String KEY_REICHWEITE = "reichweite";
	public static final String KEY_REPRESENTATION = "repraesentation";
	public static final String KEY_VARIANTE = "variante";
	public static final String KEY_WIRKUNGSDAUER = "wirkungsdauer";
	public static final String KEY_ZAUBERDAUER = "zauberdauer";
	public static final String KEY_ZAUBERKOMMENTAR = "zauberkommentar";
	public static final String KEY_KEY = "key";

	public static final String KEY_FAVORITE = "fav";
	public static final String KEY_UNUSED = "unused";
	public static final String KEY_PORTRAIT_PATH = "portrait_path";

	public static final String KEY_EIGENSCHAFTEN = "eigenschaften";
	public static final String KEY_AUSRUESTUNGEN_UE = "ausruestungen";
	public static final String KEY_AUSRUESTUNGEN = "ausrüstungen";
	public static final String KEY_GEGENSTAENDE_AE = "gegenstaende";
	public static final String KEY_GEGENSTAENDE = "gegenstände";
	public static final String KEY_ZAUBERLISTE = "zauberliste";
	public static final String KEY_TALENTLISTE = "talentliste";
	public static final String KEY_BASIS = "basis";
	public static final String KEY_BEZEICHNER = "bezeichner";

	public static final String KEY_DAUER = "dauer";
	public static final String KEY_WIRKUNG = "wirkung";
	public static final String KEY_AUSWAHL = "auswahl";
	public static final String KEY_NUMMER = "nummer";

	public static final String KEY_VERBINDUNGEN = "verbindungen";
	public static final String KEY_VERBINDUNG = "verbindung";
	public static final String KEY_DESCRIPTION = "beschreibung";
	public static final String KEY_SO = "so";

	public static final String KEY_NOTIZ_PREFIX = "notiz";
	public static final String KEY_NOTIZ = "notiz";
	public static final String KEY_AUSSEHENTEXT_PREFIX = "aussehentext";
	public static final String KEY_TITEL = "titel";
	public static final String KEY_STAND = "stand";
	public static final String KEY_ACTIVE = "active";
	public static final String KEY_SPEZIALISIERUNG = "spezialisierung";
	public static final String KEY_K = "k";
	public static final String KEY_MRMOD = "mrmod";
	public static final String KEY_GESBE = "gesbe";
	public static final String KEY_ENTFERNUNG = "entfernung";
	public static final String KEY_PREIS = "preis";

	public static final String KEY_ID = "id";
	public static final String KEY_ALT = "Alt";
	public static final String KEY_NEU = "Neu";
	public static final String KEY_INFO = "Info";
	public static final String KEY_SF_INFOS = "sfInfos";
	public static final String KEY_SF_NAME = "sfname";

	// Animal stuff
	public static final String KEY_TIER = "Tier";
	public static final String KEY_GATTUNG = "gattung";
	public static final String KEY_FAMILIE = "familie";
	public static final String KEY_INI = "ini";
	public static final String KEY_INI_MUL = "mul";
	public static final String KEY_INI_SUM = "sum";
	public static final String KEY_INI_W = "w";
	public static final String KEY_ANGRIFFE = "angriffe";
	public static final String KEY_ANGRIFF = "angriff";
	public static final String KEY_AT = "at";
	public static final String KEY_PA = "pa";
	public static final String KEY_TP = "tp";
	public static final String KEY_DK = "dk";
	public static final Object KEY_HELDEN = "helden";

	public static String toString(String s) {
		if (s == null)
			return "";
		else
			return s;
	}

	public static String toString(Integer value, String defaultValue) {
		if (value != null)
			return Integer.toString(value);
		else
			return defaultValue;
	}

	public static String toString(Integer value) {
		return toString(value, "");
	}

	public static String toString(Float value) {
		if (value != null)
			return Float.toString(value);
		else
			return "";
	}

	public static String toString(Long value) {
		if (value != null)
			return Long.toString(value);
		else
			return "";

	}

	public static Element getOrCreateElement(Element parent, String name) {
		Element elem = parent.getChild(name);
		if (elem == null) {
			elem = new Element(name);
			parent.addContent(elem);
		}
		return elem;
	}
}
