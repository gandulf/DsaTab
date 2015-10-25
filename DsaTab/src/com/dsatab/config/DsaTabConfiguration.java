package com.dsatab.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.enums.Position;
import com.dsatab.fragment.AnimalFragment;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.fragment.BodyFragment;
import com.dsatab.fragment.CharacterFragment;
import com.dsatab.fragment.ItemsFragment;
import com.dsatab.fragment.ListableFragment;
import com.dsatab.fragment.MapFragment;
import com.dsatab.fragment.NotesEditFragment;
import com.dsatab.fragment.item.ItemListFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  
 */
public class DsaTabConfiguration {

	private Map<Class<? extends BaseFragment>, Integer> tabResourceIds = new HashMap<Class<? extends BaseFragment>, Integer>(
			15);

	private Context context;

	private List<Integer> itemIcons;

	private static List<String> IGNORE_ICONS = Arrays.asList("dsa_set","dsa_set1","dsa_set2","dsa_set3");

	public enum WoundType {
		Standard("Standard"), Trefferzonen("Trefferzonen");

		private String title;

		WoundType(String t) {
			title = t;
		}

		public String title() {
			return title;
		}
	}

	public enum ArmorType {
		ZonenRuestung("Zonenrüstung"), GesamtRuestung("Gesamte Rüstung");

		private String title;

		ArmorType(String t) {
			title = t;
		}

		public String title() {
			return title;
		}
	}

	public DsaTabConfiguration(Context context) {
		this.context = context;

		tabResourceIds.put(CharacterFragment.class, R.drawable.vd_anatomy);
		tabResourceIds.put(BodyFragment.class, R.drawable.vd_broken_heart);
		tabResourceIds.put(ListableFragment.class, R.drawable.vd_all_for_one);
		tabResourceIds.put(ItemsFragment.class, R.drawable.vd_battle_gear);
		tabResourceIds.put(ItemListFragment.class, R.drawable.vd_battle_gear);
		tabResourceIds.put(NotesEditFragment.class, R.drawable.vd_tied_scroll);
		tabResourceIds.put(MapFragment.class, R.drawable.vd_treasure_map);
		tabResourceIds.put(AnimalFragment.class, R.drawable.vd_horse_head);

		itemIcons = new ArrayList<Integer>();
		Field[] allFields = R.drawable.class.getDeclaredFields();
		for (Field field : allFields) {
			if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
				String fieldName = field.getName();
				if ((fieldName.startsWith("vd_") ||fieldName.startsWith("dsa_")) && !fieldName.endsWith("_add") && !fieldName.endsWith("_light")
						&& !fieldName.endsWith("_dark") && !IGNORE_ICONS.contains(fieldName)) {
					try {
						itemIcons.add(field.getInt(null));
					} catch (IllegalAccessException e) {
					} catch (IllegalArgumentException e) {
					}
				}
			}
		}

	}

	public List<Integer> getDsaIcons() {
		return itemIcons;
	}

	public int getTabResourceId(Class<? extends BaseFragment> clazz) {
		if (tabResourceIds.containsKey(clazz)) {
			return tabResourceIds.get(clazz);
		} else
			return 0;
	}

	public List<Position> getArmorPositions() {
		return Position.ARMOR_POSITIONS;
	}

	public List<Position> getWoundPositions() {
		if (DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_MORE_WOUND_ZONES,
				false)) {
			return Position.WOUND_POSITIONS;
		} else
			return Position.WOUND_POSITIONS;
	}

	public ArmorType getArmorType() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return ArmorType.valueOf(preferences.getString(DsaTabPreferenceActivity.KEY_ARMOR_TYPE,
				ArmorType.ZonenRuestung.name()));
	}

	public WoundType getWoundType() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return WoundType.valueOf(preferences.getString(DsaTabPreferenceActivity.KEY_WOUND_TYPE,
				WoundType.Trefferzonen.name()));
	}

}
