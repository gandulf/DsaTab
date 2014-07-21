package com.dsatab.util;

import android.content.SharedPreferences;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.enums.ArmorPosition;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.MiscSpecification;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;

public class DsaUtil {

	public static float unzenToStein(float unzen) {
		return unzen / 40.0f;
	}

	public static double getProbePercentage(int[] e, int t) {

		double v = 0;

		// see Wege des Meisters page 170
		if (t <= 0) {
			v = Math.min(20, e[1] + t);
			for (int i = 2; i <= 3; i++) {
				v *= Math.min(20, e[i] + t);
			}
		} else {

			v = Math.min(20, e[1]);
			for (int i = 2; i <= 3; i++) {
				v *= Math.min(20, e[i]);
			}

			// E i=1 - 3
			for (int i = 1; i <= 3; i++) {

				int ti = Math.min(20 - e[i], t);

				// E n=1 - Ti
				for (int n = 1; n <= ti; n++) {
					v += (Math.min(20, e[(i % 3) + 1] - n) * Math.min(20, e[((i + 1) % 3) + 1] - n));
				}

			}

		}

		return v / 8000;
	}

	public static double testEigen(int e1, int taw) {
		double result;
		SharedPreferences preferences = DsaTabApplication.getPreferences();

		if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_2_OF_3_DICE, false) == false) {
			result = Math.min(1.0, (e1 + taw) / 20.0);
		} else {

			e1 = e1 + taw;

			// negative values don't work and an one is always successful.
			if (e1 < 0)
				e1 = 0;

			int a = Math.min(20, e1) * Math.min(20, e1) * (20 - Math.min(20, e1));
			int d = Math.min(20, e1) * Math.min(20, e1) * Math.min(20, e1);

			result = (3 * a + d) / 8000.0;

		}

		// an 1 is always successful no matter how small the taw
		if (result < 0.05)
			result = 0.05;

		return result;
	}

	public static Integer min(Integer... values) {
		Integer min = null;

		for (Integer i : values) {
			if (i != null) {
				if (min == null)
					min = i;
				else
					min = Math.min(min, i);
			}
		}

		return min;

	}

	public static int sum(Integer... values) {
		int sum = 0;

		for (Integer i : values) {
			if (i != null)
				sum += i;
		}

		return sum;
	}

	public static double testTalent(int e1, int e2, int e3, int taw) {

		int success, restTaP;
		if (taw < 0)
			return testTalent(e1 + taw, e2 + taw, e3 + taw, 0);

		success = 0;
		for (int w1 = 1; w1 <= 20; w1++) {
			for (int w2 = 1; w2 <= 20; w2++) {
				for (int w3 = 1; w3 <= 20; w3++) {
					if (meisterhaft(w1, w2, w3)) {
						success++;
					} else {
						if (patzer(w1, w2, w3)) {

						} else {
							// schauen, ob die Rest-TaP nicht unter 0 fallen
							restTaP = taw - Math.max(0, w1 - e1) - Math.max(0, w2 - e2) - Math.max(0, w3 - e3);
							if (restTaP >= 0) {
								// hat gereicht
								success++;
							}
						}
					}
				}
			}
		}
		return (1d / 8000d * (success));
	}

	private static boolean meisterhaft(int w1, int w2, int w3) {
		return (w1 == 1) && (w2 == 1) || (w2 == 1) && (w3 == 1) || (w1 == 1) && (w3 == 1);
	}

	private static boolean patzer(int w1, int w2, int w3) {
		return (w1 == 20) && (w2 == 20) || (w2 == 20) && (w3 == 20) || (w1 == 20) && (w3 == 20);
	}

	public static int getResourceId(ItemType itemType) {
		switch (itemType) {
		case Waffen:
			return R.drawable.dsa_sword;
		case Fernwaffen:
			return R.drawable.dsa_bow;
		case Rüstung:
			return R.drawable.dsa_armor_leather;
		case Schilde:
			return R.drawable.dsa_shield;
		case Sonstiges:
			return R.drawable.dsa_special;
		default:
			return R.drawable.dsa_cubes;
		}
	}

	public static int getResourceId(DistanceWeapon spec) {
		switch (spec.getTalentType()) {
		case Wurfmesser:
			return R.drawable.dsa_dagger_throw;
		case Armbrust:
			return R.drawable.dsa_crossbow;
		case Wurfbeile:
			return R.drawable.dsa_axe_throw;
		case Wurfspeere:
			return R.drawable.dsa_spear;
		case Diskus:
			return R.drawable.dsa_diskus;
		case Schleuder:
			return R.drawable.dsa_sling;
		default:
			return R.drawable.dsa_bow;

		}
	}

	public static int getResourceId(MiscSpecification spec) {
		if (spec.getType() != null) {
			switch (spec.getType()) {
			case Sonstiges:
				return R.drawable.dsa_special;
			default:
				return R.drawable.dsa_cubes;
			}
		} else {
			return R.drawable.dsa_cubes;
		}
	}

	public static int getResourceId(Weapon weapon) {

		switch (weapon.getTalentType()) {
		case Anderthalbhänder:
		case Zweihandschwertersäbel:
			return R.drawable.dsa_2sword;
		case Hiebwaffen:
			return R.drawable.dsa_hieb;
		case Stäbe:
			return R.drawable.dsa_staff;
		case Fechtwaffen:
			return R.drawable.dsa_fecht;
		case Dolche:
			return R.drawable.dsa_dagger;
		case Speere:
			return R.drawable.dsa_spear;
		case Infanteriewaffen:
			return R.drawable.dsa_2hieb2;
		case Zweihandhiebwaffen:
			if (weapon.getItem().getName().contains("hammer")) {
				return R.drawable.dsa_hammer;
			} else {
				return R.drawable.dsa_2hieb;
			}
		case Zweihandflegel:
			return R.drawable.dsa_2hieb;
		case Kettenstäbe:
		case Kettenwaffen:
			return R.drawable.dsa_kettenwaffe;
		case Raufen:
		case Ringen:
			return R.drawable.dsa_fist;
		case Peitsche:
			return R.drawable.dsa_whip;
		default:
			return R.drawable.dsa_sword;
		}
	}

	public static int getResourceId(ItemSpecification spec) {
		if (spec instanceof Weapon)
			return getResourceId((Weapon) spec);
		else if (spec instanceof DistanceWeapon)
			return getResourceId((DistanceWeapon) spec);
		else if (spec instanceof Shield)
			return getResourceId((Shield) spec);
		else if (spec instanceof MiscSpecification)
			return getResourceId((MiscSpecification) spec);
		else if (spec instanceof Armor)
			return getResourceId((Armor) spec);
		else
			return R.drawable.dsa_cubes;
	}

	public static int getResourceId(Shield spec) {
		if (spec.isParadeWeapon() && !spec.isShield())
			return R.drawable.dsa_dagger;
		else
			return R.drawable.dsa_shield;
	}

	public static int getResourceId(Armor armor) {
		Item item = armor.getItem();

		if (Armor.CATEGORY_HELM.equalsIgnoreCase(item.getCategory())) {
			if (armor.getRs(ArmorPosition.Kopf) > 5)
				return R.drawable.dsa_helm_full2;
			else if (armor.getRs(ArmorPosition.Kopf) > 0)
				return R.drawable.dsa_helm_full;
			else
				return R.drawable.dsa_helm_half;
		} else if (Armor.CATEGORY_TORSO.equalsIgnoreCase(item.getCategory())
				|| Armor.CATEGORY_FULL.equalsIgnoreCase(item.getCategory())) {
			if (armor.getMaxRs() >= 5)
				return R.drawable.dsa_armor_plate;
			else if (armor.getMaxRs() > 2)
				return R.drawable.dsa_armor_mail;
			else
				return R.drawable.dsa_armor_cloth;
		} else if (Armor.CATEGORY_ARME.equalsIgnoreCase(item.getCategory())) {
			return R.drawable.dsa_trousers;
		} else if (Armor.CATEGORY_BEINE.equalsIgnoreCase(item.getCategory())) {
			return R.drawable.dsa_armor_fist;
		} else {
			return R.drawable.dsa_armor_leather;
		}
	}
}
