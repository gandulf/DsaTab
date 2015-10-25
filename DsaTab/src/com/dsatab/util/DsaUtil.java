package com.dsatab.util;

import android.content.SharedPreferences;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.Art;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.CombatShieldTalent;
import com.dsatab.data.CustomProbe;
import com.dsatab.data.Probe;
import com.dsatab.data.Spell;
import com.dsatab.data.Talent;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.enums.Position;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.MiscSpecification;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.view.ListSettings.ListItemType;

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

	/**
	 * Returns the probability of this probe 0..1
	 * 
	 * @param e1
	 * @param taw
	 * @return double (0..1)
	 */
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

	public static int getResourceId(ListItemType itemType) {
		switch (itemType) {
		case Art:
			return R.drawable.vd_beams_aura;
		case Attribute:
			return R.drawable.vd_biceps;
		case Document:
			return R.drawable.vd_tied_scroll;
		case EquippedItem:
			return R.drawable.vd_crossed_swords;
		case Modificator:
			return R.drawable.vd_orb_direction;
		case Notes:
			return R.drawable.vd_tied_scroll;
		case Purse:
			return R.drawable.vd_cash;
		case Spell:
			return R.drawable.vd_bookmark;
		case Talent:
			return R.drawable.vd_anatomy;
		case Wound:
			return R.drawable.vd_broken_heart;
		case Probe:
			return R.drawable.vd_dice_six_faces_two;
		case Header:
			return R.drawable.vd_nothing_to_say;
		default:
			return 0;
		}

	}

	public static int getResourceId(ItemType itemType) {
		switch (itemType) {
		case Waffen:
			return R.drawable.vd_sword_hilt;
		case Fernwaffen:
			return R.drawable.vd_heavy_arrow;
		case Rüstung:
			return R.drawable.vd_leather_vest;
		case Schilde:
			return R.drawable.vd_checked_shield;
		case Sonstiges:
			return R.drawable.vd_diamond_ring;
		default:
			return R.drawable.vd_cubes;
		}
	}

	public static int getResourceId(DistanceWeapon spec) {
		switch (spec.getTalentType()) {
		case Wurfmesser:
			return R.drawable.vd_thrown_daggers;
		case Armbrust:
			return R.drawable.vd_crossbow;
		case Wurfbeile:
			return R.drawable.vd_axe_swing;
		case Wurfspeere:
			return R.drawable.vd_spears;
		case Diskus:
			return R.drawable.vd_spiked_collar;
		case Schleuder:
			return R.drawable.vd_slingshot;
		default:
			return R.drawable.vd_heavy_arrow;

		}
	}

	public static int getResourceId(MiscSpecification spec) {
		if (spec.getType() != null) {
			switch (spec.getType()) {
			case Sonstiges:
				return R.drawable.vd_diamond_ring;
			default:
				return R.drawable.vd_cubes;
			}
		} else {
			return R.drawable.vd_cubes;
		}
	}

	public static int getResourceId(Weapon weapon) {

		switch (weapon.getTalentType()) {
		case Anderthalbhänder:
		case Zweihandschwertersäbel:
			return R.drawable.vd_croc_sword;
		case Hiebwaffen:
			return R.drawable.vd_battered_axe;
		case Stäbe:
			return R.drawable.vd_crystal_wand;
		case Fechtwaffen:
			return R.drawable.vd_energy_arrow;
		case Dolche:
			return R.drawable.vd_plain_dagger;
		case Speere:
			return R.drawable.vd_stone_spear;
		case Infanteriewaffen:
			return R.drawable.vd_halberd;
		case Zweihandhiebwaffen:
			if (weapon.getItem().getName().contains("hammer")) {
				return R.drawable.vd_gavel;
			} else {
				return R.drawable.vd_halberd;
			}
		case Zweihandflegel:
			return R.drawable.vd_halberd;
		case Kettenstäbe:
		case Kettenwaffen:
			return R.drawable.vd_nunchaku;
		case Raufen:
		case Ringen:
			return R.drawable.vd_palm;
		case Peitsche:
			return R.drawable.vd_whip;
		default:
			return R.drawable.vd_sword_hilt;
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
			return R.drawable.vd_cubes;
	}

	public static int getResourceId(Shield spec) {
		if (spec.isParadeWeapon() && !spec.isShield())
			return R.drawable.vd_sai;
		else
			return R.drawable.vd_checked_shield;
	}

	public static int getResourceId(Probe probe) {

		if (probe instanceof CombatProbe) {
			CombatProbe combatProbe = (CombatProbe) probe;

			EquippedItem equippedItem = combatProbe.getEquippedItem();

			if (combatProbe.isAttack()) {
				if (equippedItem != null && equippedItem.getItemSpecification() != null) {
					return getResourceId(equippedItem.getItemSpecification());
				} else {
					return R.drawable.vd_all_for_one;
				}
			} else {
				return R.drawable.vd_round_shield;
			}
		} else if (probe instanceof CombatMeleeAttribute) {
			CombatMeleeAttribute combatProbe = (CombatMeleeAttribute) probe;
			if (combatProbe.isAttack())
				return R.drawable.vd_all_for_one;
			else
				return R.drawable.vd_round_shield;
		} else if (probe instanceof CustomProbe) {
			return R.drawable.vd_dice_six_faces_two;
		} else if (probe instanceof CombatDistanceTalent) {
			return R.drawable.vd_heavy_arrow;
		} else if (probe instanceof CombatShieldTalent) {
			return R.drawable.vd_round_shield;
		} else if (probe instanceof CombatMeleeAttribute) {
			return R.drawable.vd_all_for_one;
		} else if (probe instanceof Spell) {
			return R.drawable.vd_bookmark;
		} else if (probe instanceof Art) {
			return R.drawable.vd_beams_aura;
		} else if (probe instanceof Talent) {
			return R.drawable.vd_biceps;
		} else if (probe instanceof Attribute) {
			Attribute attribute = (Attribute) probe;

			switch (attribute.getType()) {
			case Ausweichen:
				return R.drawable.vd_boots;
			default:
				return R.drawable.vd_anatomy;
			}
		} else {
            return R.drawable.vd_dice_six_faces_two;
        }
	}

	public static int getResourceId(Armor armor) {
		Item item = armor.getItem();

		if (Armor.CATEGORY_HELM.equalsIgnoreCase(item.getCategory())) {
			if (armor.getRs(Position.Kopf) > 5)
				return R.drawable.vd_visored_helm;
			else if (armor.getRs(Position.Kopf) > 0)
				return R.drawable.vd_crested_helmet;
			else
				return R.drawable.vd_horned_helm;
		} else if (Armor.CATEGORY_TORSO.equalsIgnoreCase(item.getCategory())
				|| Armor.CATEGORY_FULL.equalsIgnoreCase(item.getCategory())) {
			if (armor.getMaxRs() >= 5)
				return R.drawable.vd_breastplate;
			else if (armor.getMaxRs() > 2)
				return R.drawable.vd_chain_mail;
			else
				return R.drawable.vd_robe;
		} else if (Armor.CATEGORY_ARME.equalsIgnoreCase(item.getCategory())) {
			return R.drawable.vd_trousers;
		} else if (Armor.CATEGORY_BEINE.equalsIgnoreCase(item.getCategory())) {
			return R.drawable.vd_mailed_fist;
		} else {
			return R.drawable.vd_leather_vest;
		}
	}
}
