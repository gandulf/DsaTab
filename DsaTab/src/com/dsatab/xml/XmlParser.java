package com.dsatab.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.net.Uri;
import android.text.TextUtils;
import android.text.TextUtils.StringSplitter;
import au.com.bytecode.opencsv.CSVReader;

import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.common.DsaTabRuntimeException;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.SpellInfo;
import com.dsatab.data.enums.Position;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemType;
import com.dsatab.data.items.MiscSpecification;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class XmlParser {

	public static final String IMAGE_POSTFIX = ".jpg";

	public static final String ENCODING = "UTF-8";

	public static void fillItems() {

		try {
			readItems("data/items.csv");

			if (DsaTabApplication.getPreferences().getBoolean(
					DsaTabPreferenceActivity.KEY_HOUSE_RULES_MORE_WOUND_ZONES, false)) {
				readItems("data/items_armor_house.csv");
			} else {
				readItems("data/items_armor.csv");
			}
		} catch (IOException e) {
			throw new DsaTabRuntimeException("Could not parse items from items.csv", e);
		}

	}

	public static void fillArts() {

		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(DsaTabApplication.getInstance().getAssets()
					.open("data/arts.csv"), ENCODING), 1024 * 8);

			CSVReader reader = new CSVReader(r);

			RuntimeExceptionDao<ArtInfo, Integer> artDao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeDao(ArtInfo.class);

			ArtInfo item = null;
			String lineData[];
			while ((lineData = reader.readNext()) != null) {

				if (lineData.length == 0 || lineData[0].startsWith("#"))
					continue;

				item = new ArtInfo();

				item.setName(lineData[0].trim());
				if (lineData.length > 1)
					item.setGrade(Util.gradeToInt(lineData[1].trim()));
				if (lineData.length > 2)
					item.setTarget(lineData[2].trim());
				if (lineData.length > 3)
					item.setRange(lineData[3].trim());
				if (lineData.length > 4)
					item.setCastDuration(lineData[4].trim());
				if (lineData.length > 5)
					item.setEffect(lineData[5].trim());
				if (lineData.length > 6)
					item.setEffectDuration(lineData[6].trim());
				if (lineData.length > 7)
					item.setOrigin(lineData[7].trim());
				if (lineData.length > 8)
					item.setSource(lineData[8].trim());
				if (lineData.length > 9)
					item.setProbe(lineData[9].trim());
				if (lineData.length > 10)
					item.setMerkmale(lineData[10].trim());

				artDao.create(item);

			}
		} catch (IOException e) {
			throw new DsaTabRuntimeException("Could not read arts from arts.csv", e);
		} finally {
			try {
				if (r != null)
					r.close();
			} catch (IOException e) {
			}
		}

	}

	public static void fillSpells() {

		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(DsaTabApplication.getInstance().getAssets()
					.open("data/spells.csv"), ENCODING), 1024 * 8);

			CSVReader reader = new CSVReader(r);

			RuntimeExceptionDao<SpellInfo, Integer> spellDao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeDao(SpellInfo.class);

			String lineData[];
			SpellInfo item = null;
			while ((lineData = reader.readNext()) != null) {

				if (lineData.length == 0 || lineData[0].startsWith("#"))
					continue;

				item = new SpellInfo();
				item.setName(lineData[0].trim());
				if (lineData.length > 1)
					item.setSource(lineData[1].trim());
				if (lineData.length > 2)
					item.setProbe(lineData[2].trim());
				if (lineData.length > 3)
					item.setComplexity(lineData[3].trim());
				if (lineData.length > 4)
					item.setRepresentation(lineData[4].trim());
				if (lineData.length > 5)
					item.setMerkmale(lineData[5].trim());
				if (lineData.length > 6)
					item.setCastDuration(lineData[6].trim());
				if (lineData.length > 7)
					item.setCosts(lineData[7].trim());
				if (lineData.length > 8)
					item.setTarget(lineData[8].trim());
				if (lineData.length > 9)
					item.setRange(lineData[9].trim());
				if (lineData.length > 10)
					item.setEffectDuration(lineData[10].trim());
				if (lineData.length > 11)
					item.setEffect(lineData[11].trim());

				spellDao.create(item);

			}
		} catch (IOException e) {
			throw new DsaTabRuntimeException("Could nor read spells from spells.csv", e);
		} finally {
			try {
				if (r != null)
					r.close();
			} catch (IOException e) {
			}
		}

	}

	private static void readItems(String file) throws IOException {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(DsaTabApplication.getInstance().getAssets().open(file),
					ENCODING), 1024 * 8);

			CSVReader reader = new CSVReader(r);
			String[] lineData;
			StringSplitter splitter = new TextUtils.SimpleStringSplitter(';');

			List<Position> armorPositions = DsaTabApplication.getInstance().getConfiguration().getArmorPositions();

			RuntimeExceptionDao<Weapon, Integer> weaponDao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeDao(Weapon.class);

			RuntimeExceptionDao<Shield, Integer> shieldDao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeDao(Shield.class);

			RuntimeExceptionDao<Armor, Integer> armorDao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeDao(Armor.class);

			RuntimeExceptionDao<DistanceWeapon, Integer> distanceWeaponDao = DsaTabApplication.getInstance()
					.getDBHelper().getRuntimeDao(DistanceWeapon.class);

			RuntimeExceptionDao<MiscSpecification, Integer> miscspecDao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeDao(MiscSpecification.class);

			RuntimeExceptionDao<Item, UUID> itemDao = DsaTabApplication.getInstance().getDBHelper().getItemDao();

			ItemType type = null;
			Item item = null;
			String specLabel, name;
			while ((lineData = reader.readNext()) != null) {

				if (lineData.length == 0 || lineData[0].startsWith("#"))
					continue;

				item = new Item();

				type = parseBase(item, lineData);

				specLabel = null;
				name = item.getName();
				// we have a encoded specLabel added to the item name e.g.
				// [einhändig]
				if (name.contains("[")) {
					int startSpec = name.indexOf('[');
					int endSpec = name.indexOf(']', startSpec);
					if (endSpec == -1) {
						throw new DsaTabRuntimeException(
								"Malformed items.txt file: Opening item specificaton '[' without closing bracket found at item "
										+ name);
					}
					specLabel = name.substring(startSpec + 1, endSpec);
					name = name.substring(0, startSpec);

					item.setName(name);
				} else {
					specLabel = null;
				}

				Item existingItem = DataManager.getItemByName(item.getName());
				if (existingItem != null)
					item = existingItem;
				else
					itemDao.create(item);

				if (lineData[0].equals("W")) {
					Weapon w = readWeapon(item, lineData);
					w.setSpecificationLabel(specLabel);
					item.addSpecification(w);
					weaponDao.create(w);
				} else if (lineData[0].equals("D")) {
					DistanceWeapon w = readDistanceWeapon(item, lineData);
					w.setSpecificationLabel(specLabel);
					item.addSpecification(w);
					distanceWeaponDao.create(w);
				} else if (lineData[0].equals("A")) {
					Armor w = readArmor(item, lineData, armorPositions);
					w.setSpecificationLabel(specLabel);
					item.addSpecification(w);
					armorDao.create(w);
				} else if (lineData[0].equals("S")) {
					Shield w = readShield(item, lineData);
					w.setSpecificationLabel(specLabel);
					item.addSpecification(w);
					shieldDao.create(w);
				} else {
					MiscSpecification m = new MiscSpecification(item, type);
					m.setSpecificationLabel(specLabel);
					item.addSpecification(m);
					miscspecDao.create(m);
				}
				itemDao.update(item);

			}
		} finally {
			try {
				if (r != null)
					r.close();
			} catch (IOException e) {
			}
		}
	}

	private static Weapon readWeapon(Item item, String[] lineData) {

		try {

			Weapon w = new Weapon(item);

			w.setTp(lineData[4]); // TP

			String tpKK = lineData[5]; // TPKK
			String tpKKMin = tpKK.substring(0, tpKK.indexOf("/"));
			String tpKKStep = tpKK.substring(tpKK.indexOf("/") + 1);
			w.setTpKKMin(Integer.valueOf(tpKKMin));
			w.setTpKKStep(Integer.valueOf(tpKKStep));

			String wm = lineData[6]; // WM
			String wmAt = wm.substring(0, wm.indexOf("/"));
			String wmPa = wm.substring(wm.indexOf("/") + 1);

			w.setWmAt(Util.parseInteger(wmAt));
			w.setWmPa(Util.parseInteger(wmPa));

			w.setIni(Util.parseInteger(lineData[7])); // INI

			w.setBf(Util.parseInteger(lineData[8])); // BF

			w.setDistance(lineData[9].trim()); // distance

			String twohanded = lineData[10]; // twohanded
			if (twohanded != null && twohanded.contains("z"))
				w.setTwoHanded(true);

			for (int i = 11; i < lineData.length; i++) { // type
				if (!TextUtils.isEmpty(lineData[i]))
					w.addTalentType(TalentType.byXmlName(lineData[i]));
			}

			return w;

		} catch (NumberFormatException e) {
			Debug.error(e);
		}
		return null;
	}

	private static Armor readArmor(Item item, String[] lineData, List<Position> armorPositions) {

		Armor w = new Armor(item);

		w.setTotalBe(Util.parseFloat(lineData[4]));

		int i = 5;
		for (Position pos : armorPositions) {
			w.setRs(pos, Util.parseInt(lineData[i++], 0));
		}

		w.setZonenRs(Util.parseInt(lineData[i++], 0));
		w.setTotalRs(Util.parseInt(lineData[i++], 0));
		w.setStars(Util.parseInt(lineData[i++], 0));

		String mod = lineData[i++];
		if (mod.contains("Z"))
			w.setZonenHalfBe(true);

		w.setTotalPieces(Util.parseInt(lineData[i++], 1));

		return w;
	}

	private static DistanceWeapon readDistanceWeapon(Item item, String[] lineData) {

		try {
			DistanceWeapon w = new DistanceWeapon(item);

			w.setTp(lineData[4]);
			w.setDistances(lineData[5]);
			w.setTpDistances(lineData[6]);

			for (int i = 7; i < lineData.length; i++) { // type
				if (!TextUtils.isEmpty(lineData[i]))
					w.setTalentType(TalentType.valueOf(lineData[i]));
			}

			return w;
		} catch (NumberFormatException e) {
			Debug.error(e);
		}
		return null;
	}

	// private static void appendItem(BufferedWriter r, Item i, String category)
	// throws IOException {
	// int lineLength = i.getName().length();
	// r.append(i.getName());
	//
	// lineLength++;
	// r.append(";");
	// if (i.path != null) {
	// r.append(i.path);
	// lineLength += i.path.length();
	// }
	// r.append(";");
	// lineLength++;
	// if (i.getCategory() == null) {
	// r.append(category);
	// lineLength += category.length();
	// } else {
	// r.append(i.getCategory());
	// lineLength += i.getCategory().length();
	// }
	//
	// int totalpad = 70 - lineLength;
	//
	// while (totalpad > 0) {
	// r.append(" ");
	// totalpad--;
	// }
	//
	// r.append(";");
	// lineLength++;
	//
	// }

	// public static void writeItems() {
	// Map<String, Item> items = null;
	// // TODO read items from database;
	//
	// try {
	//
	// File itemsFile = new File(DSATabApplication.getDsaTabPath(),
	// "items_new.txt");
	// OutputStreamWriter itemsWriter = new OutputStreamWriter(new
	// FileOutputStream(itemsFile), ENCODING);
	// BufferedWriter itemsW = new BufferedWriter(itemsWriter, 1024 * 8);
	//
	// List<Item> its = new ArrayList<Item>(items.values());
	// Collections.sort(its);
	//
	// BufferedWriter r = itemsW;
	// String guessCategory = null;
	//
	// for (Item item : its) {
	//
	// for (ItemSpecification i : item.getSpecifications()) {
	// if (i instanceof Weapon) {
	//
	// Weapon w = (Weapon) i;
	//
	// guessCategory = null;
	// if (item.getCategory() == null) {
	//
	// if (w.getTalentType() == TalentType.Zweihandhiebwaffen) {
	// guessCategory = "Zweihandhiebwaffen und -flegel";
	// } else if (w.getTalentType() == TalentType.Zweihandflegel) {
	// guessCategory = "Zweihandhiebwaffen und -flegel";
	// } else if (w.getTalentType() == TalentType.Zweihandschwerter)
	// {
	// guessCategory = "Zweihandschwerter und -säbel";
	// } else if (w.getTalentType() == TalentType.Speere) {
	// guessCategory = "Speere und Stäbe";
	// } else if (w.getTalentType() == TalentType.Stäbe) {
	// guessCategory = "Speere und Stäbe";
	// } else if (w.getTalentTypes().size() == 1) {
	// guessCategory = w.getTalentType().name();
	// }
	//
	// }
	//
	// r.append("W;");
	// appendItem(r, item, guessCategory);
	// r.append(w.getTp());
	// r.append(";");
	// r.append(Util.toString(w.getTpKKMin()) + "/" +
	// Util.toString(w.getTpKKStep()));
	// r.append(";");
	// r.append(Util.toString(w.getWmAt()) + "/" + Util.toString(w.getWmPa()));
	// r.append(";");
	// r.append(Util.toString(w.getIni()));
	// r.append(";");
	// r.append(Util.toString(w.getBf()));
	// r.append(";");
	// r.append(w.getDistance());
	// r.append(";");
	// r.append(w.isTwoHanded() ? "Z" : "");
	// r.append(";");
	//
	// for (TalentType t : w.getTalentTypes()) {
	// r.append(t.name());
	// r.append(";");
	// }
	// } else if (i instanceof DistanceWeapon) {
	//
	// DistanceWeapon w = (DistanceWeapon) i;
	//
	// guessCategory = null;
	// if (item.getCategory() == null) {
	//
	// if (w.getTalentType() == TalentType.Armbrust) {
	// guessCategory = "Schusswaffen";
	// } else if (w.getTalentType() == TalentType.Bogen) {
	// guessCategory = "Schusswaffen";
	// } else if (w.getTalentType() == TalentType.Blasrohr) {
	// guessCategory = "Schusswaffen";
	// } else if (w.getTalentType() == TalentType.Wurfbeile) {
	// guessCategory = "Wurfwaffen";
	// } else if (w.getTalentType() == TalentType.Wurfmesser) {
	// guessCategory = "Wurfwaffen";
	// } else if (w.getTalentType() == TalentType.Wurfspeere) {
	// guessCategory = "Wurfwaffen";
	// } else if (w.getTalentType() == TalentType.Schleuder) {
	// guessCategory = "Wurfwaffen";
	// }
	//
	// }
	//
	// r.append("D;");
	// appendItem(r, item, guessCategory);
	// r.append(w.getTp());
	// r.append(";");
	// r.append(w.getDistances());
	// r.append(";");
	// r.append(w.getTpDistances());
	// r.append(";");
	// r.append(w.getTalentType().name());
	// r.append(";");
	//
	// } else if (i instanceof Shield) {
	//
	// Shield w = (Shield) i;
	// guessCategory = null;
	//
	// if (item.getCategory() == null) {
	// if (w.isParadeWeapon()) {
	// guessCategory = "Dolche";
	// } else {
	// guessCategory = "Schilde";
	// }
	// }
	//
	// r.append("S;");
	// appendItem(r, item, guessCategory);
	// r.append(w.getWmAt() + "/" + w.getWmPa());
	// r.append(";");
	// r.append(Util.toString(w.getIni()));
	// r.append(";");
	// r.append(Util.toString(w.getBf()));
	// r.append(";");
	// r.append(w.isShield() ? "S" : "");
	// r.append(w.isParadeWeapon() ? "P" : "");
	// r.append(";");
	//
	// for (TalentType t : w.getTalentTypes()) {
	// r.append(t.name());
	// r.append(";");
	// }
	//
	// } else if (i instanceof Armor) {
	//
	// Armor w = (Armor) i;
	// r.append("A;");
	// appendItem(r, item, null);
	//
	// if (w.getTotalBe() < 10)
	// r.append(" ");
	// r.append(Util.toString(w.getTotalBe()));
	// r.append(";");
	//
	// for (Position pos :
	// DSATabApplication.getInstance().getConfiguration().getArmorPositions()) {
	// int rs = w.getRs(pos);
	// if (rs < 10)
	// r.append(" ");
	// r.append(Util.toString(rs));
	// r.append(";");
	// }
	//
	// r.append(Util.toString(w.getZonenRs()));
	// r.append(";");
	// r.append(Util.toString(w.getTotalRs()));
	// r.append(";");
	// r.append(Util.toString(w.getStars()));
	// r.append(";");
	// if (w.isZonenHalfBe()) {
	// r.append("Z");
	// r.append(";");
	// }
	//
	// } else {
	// r = itemsW;
	// r.append(i.getType().character());
	// r.append(";");
	// appendItem(r, item, null);
	// }
	//
	// r.append("\n");
	// }
	// }
	//
	// itemsW.close();
	// } catch (IOException e) {
	// Debug.error(e);
	// }
	// }

	private static ItemType parseBase(Item item, String[] lineData) {

		ItemType type = ItemType.Sonstiges;
		String typeString = lineData[0];// itemtype
		if (!TextUtils.isEmpty(typeString))
			type = ItemType.fromCharacter(typeString.charAt(0)); // type

		item.setName(lineData[1].replace('_', ' ').trim());

		String path = lineData[2].trim();
		File imageFile = null;
		if (!TextUtils.isEmpty(path)) {
			imageFile = new File(path);
			if (!imageFile.exists())
				imageFile = new File(DsaTabApplication.getDirectory(DsaTabApplication.DIR_CARDS), path);
			if (!imageFile.exists())
				imageFile = null;
		}

		// try to find a image with name of item in cards directory
		if (imageFile == null && !TextUtils.isEmpty(item.getName())) {
			imageFile = new File(DsaTabApplication.getDirectory(DsaTabApplication.DIR_CARDS), item.getName()
					+ IMAGE_POSTFIX);
			if (!imageFile.exists())
				imageFile = null;
		}

		if (imageFile != null) {
			item.setImageTextOverlay(false);
			item.setImageUri(Uri.fromFile(imageFile));
		}

		item.setCategory(lineData[3].trim()); // category

		return type;
	}

	private static Shield readShield(Item item, String[] lineData) {

		Shield w = new Shield(item);

		String wm = lineData[4];
		String wmAt = wm.substring(0, wm.indexOf("/"));
		String wmPa = wm.substring(wm.indexOf("/") + 1);

		w.setWmAt(Util.parseInteger(wmAt));
		w.setWmPa(Util.parseInteger(wmPa));

		w.setIni(Util.parseInteger(lineData[5]));
		w.setBf(Util.parseInteger(lineData[6]));

		String type = lineData[7].toLowerCase(Locale.GERMAN); // typ

		if (type.contains("p"))
			w.setParadeWeapon(true);
		if (type.contains("s"))
			w.setShield(true);

		for (int i = 8; i < lineData.length; i++) { // type
			if (!TextUtils.isEmpty(lineData[i]))
				w.addTalentType(TalentType.valueOf(lineData[i]));
		}

		return w;

	}
}
