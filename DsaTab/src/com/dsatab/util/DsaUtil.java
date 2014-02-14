package com.dsatab.util;

import com.dsatab.R;
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

	public static int getResourceId(ItemType itemType) {
		switch (itemType) {
		case Waffen:
			return R.drawable.icon_sword;
		case Fernwaffen:
			return R.drawable.icon_bow;
		case Rüstung:
			return R.drawable.icon_armor;
		case Schilde:
			return R.drawable.icon_shield;
		case Sonstiges:
			return R.drawable.icon_misc;
		default:
			return R.drawable.icon_misc;
		}
	}

	public static int getResourceId(DistanceWeapon spec) {
		switch (spec.getTalentType()) {
		case Wurfmesser:
			return R.drawable.icon_wurfdolch;
		case Armbrust:
			return R.drawable.icon_crossbow;
		case Wurfbeile:
			return R.drawable.icon_wurfbeil;
		case Wurfspeere:
			return R.drawable.icon_speer;
		case Diskus:
			return R.drawable.icon_diskus;
		case Schleuder:
			return R.drawable.icon_sling;
		default:
			return R.drawable.icon_bow;

		}
	}

	public static int getResourceId(MiscSpecification spec) {
		if (spec.getType() != null) {
			switch (spec.getType()) {
			case Sonstiges:
				return R.drawable.icon_misc;
			default:
				return R.drawable.icon_other;
			}
		} else {
			return R.drawable.icon_other;
		}
	}

	public static int getResourceId(Weapon weapon) {

		switch (weapon.getTalentType()) {
		case Anderthalbhänder:
		case Zweihandschwertersäbel:
			return R.drawable.icon_2schwert;
		case Hiebwaffen:
			return R.drawable.icon_hieb;
		case Stäbe:
			return R.drawable.icon_stab;
		case Fechtwaffen:
			return R.drawable.icon_fecht;
		case Dolche:
			return R.drawable.icon_messer;
		case Speere:
			return R.drawable.icon_speer;
		case Infanteriewaffen:
			return R.drawable.icon_halberd;
		case Zweihandhiebwaffen:
			if (weapon.getItem().getName().contains("hammer")) {
				return R.drawable.icon_hammer;
			} else {
				return R.drawable.icon_2hieb;
			}
		case Zweihandflegel:
			return R.drawable.icon_2hieb;
		case Kettenstäbe:
		case Kettenwaffen:
			return R.drawable.icon_kettenwaffen;
		case Raufen:
		case Ringen:
		case Peitsche:
			return R.drawable.icon_whip;
		default:
			return R.drawable.icon_sword;
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
			return R.drawable.icon_misc;
	}

	public static int getResourceId(Shield spec) {
		if (spec.isParadeWeapon() && !spec.isShield())
			return R.drawable.icon_messer;
		else
			return R.drawable.icon_shield;
	}

	public static int getResourceId(Armor armor) {
		Item item = armor.getItem();

		if (Armor.CATEGORY_HELM.equalsIgnoreCase(item.getCategory())) {
			if (armor.getRs(ArmorPosition.Kopf) > 5)
				return R.drawable.icon_helm_full;
			else if (armor.getRs(ArmorPosition.Kopf) > 0)
				return R.drawable.icon_helm_half;
			else
				return R.drawable.icon_helm;
		} else if (Armor.CATEGORY_TORSO.equalsIgnoreCase(item.getCategory())
				|| Armor.CATEGORY_FULL.equalsIgnoreCase(item.getCategory())) {
			if (armor.getMaxRs() > 6)
				return R.drawable.icon_armor_metal;
			else if (armor.getMaxRs() > 2)
				return R.drawable.icon_armor_chain;
			else
				return R.drawable.icon_armor_cloth;
		} else if (Armor.CATEGORY_ARME.equalsIgnoreCase(item.getCategory())
				|| Armor.CATEGORY_BEINE.equalsIgnoreCase(item.getCategory())) {
			return R.drawable.icon_greaves;
		} else {
			return R.drawable.icon_armor;
		}
	}
}
