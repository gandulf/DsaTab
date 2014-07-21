package com.dsatab.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.data.Hero.CombatStyle;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.data.modifier.CustomModificator;
import com.dsatab.data.notes.Event;
import com.dsatab.fragment.AnimalFragment;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.fragment.BodyFragment;
import com.dsatab.fragment.CharacterFragment;
import com.dsatab.fragment.ItemsFragment;
import com.dsatab.fragment.ListableFragment;
import com.dsatab.fragment.MapFragment;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;

public class HeroConfiguration {

	private static final String FIELD_TABS_PORTRAIT = "tabsPortrait";
	private static final String FIELD_MODIFICATORS = "modificators";
	private static final String FIELD_WOUNDS = "wounds";
	private static final String FIELD_ARMOR_ATTRIBUTES = "armors";
	private static final String FIELD_ATTRIBUTES = "attributes";
	private static final String FIELD_COMBAT_STYLE = "combatStyle";
	private static final String FIELD_BE_CALCULATION = "beCalculation";
	private static final String FIELD_META_TALENTS = "metaTalents";
	private static final String FIELD_EVENTS = "events";
	private static final String FIELD_PROPERTIES = "properties";

	private static final String FIELD_LE_MODIFIER = "leModifier";
	private static final String FIELD_AU_MODIFIER = "auModifier";
	private static final String FIELD_VERSION = "version";
	private static final String FIELD_CUSTOM_PROBES = "customProbes";

	private List<TabInfo> tabInfos;

	private List<CustomModificator> modificators;
	private List<WoundAttribute> wounds;
	private List<ArmorAttribute>[] armorAttributes;
	private Set<CustomAttribute> attributes;
	private Set<MetaTalent> metaTalents;
	private List<Event> events;
	private List<CustomProbe> customProbes;

	private List<ItemContainer> itemContainers;

	private CombatStyle combatStyle;
	private boolean beCalculation;

	private boolean leModifierActive;
	private boolean auModifierActive;
	private Hero hero;

	private Map<String, String> properties;

	/**
	 * 
	 */
	public HeroConfiguration(Hero hero) {
		this.hero = hero;

		modificators = new ArrayList<CustomModificator>();
		wounds = new ArrayList<WoundAttribute>();
		armorAttributes = new ArrayList[Hero.MAXIMUM_SET_NUMBER];
		attributes = new HashSet<CustomAttribute>();
		metaTalents = new HashSet<MetaTalent>();
		events = new ArrayList<Event>();
		customProbes = new ArrayList<CustomProbe>();
		combatStyle = CombatStyle.Offensive;
		beCalculation = true;

		leModifierActive = true;
		auModifierActive = true;

		properties = new HashMap<String, String>();

		tabInfos = getDefaultTabs(null);
	}

	public HeroConfiguration(Hero hero, JSONObject in) throws JSONException {
		this.hero = hero;

		int version = in.optInt(FIELD_VERSION);

		JSONArray array = null;

		if (version < 75) {
			tabInfos = getDefaultTabs(tabInfos);
		} else {
			if (in.has(FIELD_TABS_PORTRAIT)) {
				array = in.getJSONArray(FIELD_TABS_PORTRAIT);
				tabInfos = new ArrayList<TabInfo>(array.length());
				for (int i = 0; i < array.length(); i++) {
					JSONObject tab = array.getJSONObject(i);
					try {
						TabInfo info = new TabInfo(tab);
						tabInfos.add(info);
					} catch (ClassNotFoundException e) {
						Debug.error(e);
					}
				}
			} else {
				tabInfos = new ArrayList<TabInfo>();
			}
		}

		if (in.has(FIELD_MODIFICATORS)) {
			array = in.getJSONArray(FIELD_MODIFICATORS);
			modificators = new ArrayList<CustomModificator>(array.length());
			for (int i = 0; i < array.length(); i++) {
				JSONObject tab = array.getJSONObject(i);
				CustomModificator info = new CustomModificator(this.hero, tab);
				modificators.add(info);
			}
		} else {
			modificators = new ArrayList<CustomModificator>();
		}
		if (in.has(FIELD_WOUNDS)) {
			array = in.getJSONArray(FIELD_WOUNDS);
			wounds = new ArrayList<WoundAttribute>(array.length());
			for (int i = 0; i < array.length(); i++) {
				JSONObject tab = array.getJSONObject(i);
				try {
					WoundAttribute info = new WoundAttribute(this.hero, tab);
					wounds.add(info);
				} catch (Exception e) {
					Debug.warning("Unknown WoundAttribute, skipping it: " + tab);
				}
			}
		} else {
			wounds = new ArrayList<WoundAttribute>();
		}

		if (in.has(FIELD_ARMOR_ATTRIBUTES)) {
			array = in.getJSONArray(FIELD_ARMOR_ATTRIBUTES);
			armorAttributes = new ArrayList[array.length()];
			for (int s = 0; s < array.length(); s++) {
				JSONArray inArray = array.getJSONArray(s);

				armorAttributes[s] = new ArrayList<ArmorAttribute>(inArray.length());

				for (int i = 0; i < inArray.length(); i++) {
					JSONObject tab = inArray.getJSONObject(i);
					try {
						ArmorAttribute info = new ArmorAttribute(this.hero, tab);
						armorAttributes[s].add(info);
					} catch (Exception e) {
						Debug.warning("Unknown ArmorAttribute, skipping it: " + tab);
					}
				}
			}
		} else {
			armorAttributes = new ArrayList[Hero.MAXIMUM_SET_NUMBER];
		}

		if (in.has(FIELD_ATTRIBUTES)) {
			array = in.getJSONArray(FIELD_ATTRIBUTES);
			attributes = new HashSet<CustomAttribute>(array.length());
			for (int i = 0; i < array.length(); i++) {
				JSONObject tab = array.getJSONObject(i);
				try {
					CustomAttribute info = new CustomAttribute(this.hero, tab);
					attributes.add(info);
				} catch (Exception e) {
					Debug.warning("Unknown Attribute, skipping it: " + tab);
				}
			}
		} else {
			attributes = new HashSet<CustomAttribute>();
		}

		if (in.has(FIELD_META_TALENTS)) {
			array = in.getJSONArray(FIELD_META_TALENTS);
			metaTalents = new HashSet<MetaTalent>(array.length());

			for (int i = 0; i < array.length(); i++) {
				JSONObject tab = array.getJSONObject(i);
				try {
					MetaTalent info = new MetaTalent(this.hero, tab);
					metaTalents.add(info);
				} catch (Exception e) {
					Debug.error(e);
				}
			}
		} else {
			metaTalents = new HashSet<MetaTalent>();
		}

		if (in.has(FIELD_EVENTS)) {
			array = in.getJSONArray(FIELD_EVENTS);
			events = new ArrayList<Event>(array.length());
			for (int i = 0; i < array.length(); i++) {
				JSONObject tab = array.getJSONObject(i);
				Event info = new Event(tab);
				events.add(info);
			}
		} else {
			events = new ArrayList<Event>();
		}

		if (in.has(FIELD_COMBAT_STYLE)) {
			combatStyle = CombatStyle.valueOf(in.getString(FIELD_COMBAT_STYLE));
		} else
			combatStyle = CombatStyle.Offensive;
		if (in.has(FIELD_BE_CALCULATION)) {
			beCalculation = in.getBoolean(FIELD_BE_CALCULATION);
		}

		if (in.has(FIELD_LE_MODIFIER)) {
			leModifierActive = in.getBoolean(FIELD_LE_MODIFIER);
		}

		if (in.has(FIELD_AU_MODIFIER)) {
			auModifierActive = in.getBoolean(FIELD_AU_MODIFIER);
		}

		properties = new HashMap<String, String>();
		if (in.has(FIELD_PROPERTIES)) {

			JSONObject map = in.getJSONObject(FIELD_PROPERTIES);
			Iterator<String> keys = map.keys();

			while (keys.hasNext()) {
				String key = keys.next();
				properties.put(key, map.optString(key));
			}
		}

		if (in.has(FIELD_CUSTOM_PROBES)) {
			array = in.getJSONArray(FIELD_CUSTOM_PROBES);
			customProbes = new ArrayList<CustomProbe>(array.length());
			for (int i = 0; i < array.length(); i++) {
				JSONObject tab = array.getJSONObject(i);
				CustomProbe info = new CustomProbe(this.hero, tab);
				customProbes.add(info);
			}
		} else {
			customProbes = new ArrayList<CustomProbe>();
		}
	}

	public String getProperty(String key) {
		return properties.get(key);
	}

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * 
	 * @param orientation
	 *            one of {@link ActivityInfo} SCREEN_ORIENTATION_
	 * @return
	 */
	public List<TabInfo> getTabs() {
		return tabInfos;
	}

	public void setTabs(List<TabInfo> tabs) {
		tabInfos = tabs;
	}

	public TabInfo getTab(int index) {
		return getTabs().get(index);
	}

	@SuppressLint("InlinedApi")
	private boolean isDualPanel() {
		Configuration configuration = DsaTabApplication.getInstance().getResources().getConfiguration();
		int size = Configuration.SCREENLAYOUT_SIZE_MASK & configuration.screenLayout;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return size == Configuration.SCREENLAYOUT_SIZE_XLARGE;
		} else {
			return size == Configuration.SCREENLAYOUT_SIZE_LARGE;
		}
	}

	private int getTabResourceId(Class<? extends BaseFragment> clazz) {
		return DsaTabApplication.getInstance().getConfiguration().getTabResourceId(clazz);
	}

	public void addModificator(CustomModificator modificator) {
		modificators.add(modificator);
	}

	public void removeModificator(CustomModificator modificator) {
		modificators.remove(modificator);
	}

	public void addMetaTalent(MetaTalent talent) {
		metaTalents.add(talent);
	}

	public void removeMetaTalent(MetaTalent talent) {
		metaTalents.remove(talent);
	}

	public Set<MetaTalent> getMetaTalents() {
		return metaTalents;
	}

	public CustomProbe getCustomProbe(String name) {
		for (CustomProbe customProbe : customProbes) {
			if (Util.equalsOrNull(customProbe.getName(), name))
				return customProbe;
		}
		return null;
	}

	public List<CustomProbe> getCustomProbes() {
		return customProbes;
	}

	public CustomProbe getCustomProbe(UUID id) {
		for (CustomProbe customProbe : customProbes) {
			if (Util.equalsOrNull(customProbe.getId(), id))
				return customProbe;
		}
		return null;
	}

	public List<CustomModificator> getModificators() {
		return modificators;
	}

	public List<WoundAttribute> getWounds() {
		return wounds;
	}

	public void addWound(WoundAttribute wound) {
		wounds.add(wound);
	}

	public void removeWound(WoundAttribute wound) {
		wounds.remove(wound);
	}

	public void addCustomProbe(CustomProbe probe) {
		customProbes.add(probe);
	}

	public void removeCustomProbe(CustomProbe probe) {
		customProbes.remove(probe);
	}

	public CombatStyle getCombatStyle() {
		return combatStyle;
	}

	public void setCombatStyle(CombatStyle combatStyle) {
		this.combatStyle = combatStyle;
	}

	public List<ArmorAttribute> getArmorAttributes(int index) {
		return armorAttributes[index];
	}

	public void addArmorAttribute(int index, ArmorAttribute modificator) {
		if (armorAttributes[index] == null)
			armorAttributes[index] = new ArrayList<ArmorAttribute>();

		armorAttributes[index].add(modificator);
	}

	public void removeArmorAttribute(int index, ArmorAttribute modificator) {
		if (armorAttributes[index] != null)
			armorAttributes[index].remove(modificator);
	}

	public List<Event> getEvents() {
		return events;
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public void removeEvent(Event event) {
		events.remove(event);
	}

	public Set<CustomAttribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(CustomAttribute modificator) {
		attributes.add(modificator);
	}

	public void removeAttribute(CustomAttribute modificator) {
		attributes.remove(modificator);
	}

	public boolean isBeCalculation() {
		return beCalculation;
	}

	public void setBeCalculation(boolean beCalculation) {
		this.beCalculation = beCalculation;
	}

	public boolean isLeModifierActive() {
		return leModifierActive;
	}

	public void setLeModifierActive(boolean leModifierActive) {
		this.leModifierActive = leModifierActive;
	}

	public boolean isAuModifierActive() {
		return auModifierActive;
	}

	public void setAuModifierActive(boolean auModifierActive) {
		this.auModifierActive = auModifierActive;
	}

	public List<ItemContainer> getItemContainers() {
		if (itemContainers == null) {
			itemContainers = new ArrayList<ItemContainer>();
			itemContainers.add(new ItemContainer(3, "Rucksack"));
			itemContainers.add(new ItemContainer(4, "Gürtel"));
			itemContainers.add(new ItemContainer(5, "Maultier"));
		}
		return itemContainers;
	}

	public void setItemContainers(List<ItemContainer> itemContainers) {
		this.itemContainers = itemContainers;
	}

	public List<TabInfo> getDefaultTabs(List<TabInfo> tabInfos) {

		if (tabInfos == null) {
			tabInfos = new ArrayList<TabInfo>(15);
		} else {
			tabInfos.clear();
		}
		if (isDualPanel()) {
			ListSettings listSettings;

			TabInfo tabInfo = new TabInfo(CharacterFragment.class, ListableFragment.class,
					getTabResourceId(CharacterFragment.class), true, false);
			tabInfo.setTitle("Talente");
			listSettings = (ListSettings) tabInfo.getListSettings(1);
			listSettings.addListItem(new ListItem(ListItemType.Talent));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, ListableFragment.class, R.drawable.dsa_spells);
			tabInfo.setTitle("Zauber und Künste");
			listSettings = (ListSettings) tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(AttributeType.Astralenergie_Aktuell));
			listSettings.addListItem(new ListItem(ListItemType.Spell));
			listSettings = (ListSettings) tabInfo.getListSettings(1);
			listSettings.addListItem(new ListItem(AttributeType.Karmaenergie_Aktuell));
			listSettings.addListItem(new ListItem(ListItemType.Talent, TalentGroupType.Gaben.name()));
			listSettings.addListItem(new ListItem(ListItemType.Header, ListItemType.Art));
			listSettings.addListItem(new ListItem(ListItemType.Art));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, BodyFragment.class, R.drawable.dsa_fight);
			tabInfo.setTitle("Kampf");
			listSettings = (ListSettings) tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(AttributeType.Lebensenergie_Aktuell));
			listSettings.addListItem(new ListItem(AttributeType.Ausdauer_Aktuell));
			listSettings.addListItem(new ListItem(AttributeType.Initiative_Aktuell));
			listSettings.addListItem(new ListItem(ListItemType.EquippedItem));
			listSettings.addListItem(new ListItem(AttributeType.Ausweichen));
			listSettings.addListItem(new ListItem(ListItemType.Wound));
			listSettings.addListItem(new ListItem(ListItemType.Probe));
			listSettings.addListItem(new ListItem(ListItemType.Modificator));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ItemsFragment.class, getTabResourceId(ItemsFragment.class), false, true);
			tabInfo.setTitle("Gegenstände");
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, ListableFragment.class, R.drawable.dsa_notes, false, false);
			tabInfo.setTitle("Notizen");
			listSettings = (ListSettings) tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(ListItemType.Notes));
			listSettings.addListItem(new ListItem(ListItemType.Document));
			listSettings = (ListSettings) tabInfo.getListSettings(1);
			listSettings.addListItem(new ListItem(ListItemType.Purse));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(AnimalFragment.class, getTabResourceId(AnimalFragment.class), true, false);
			tabInfo.setTitle("Begleiter");
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(MapFragment.class, getTabResourceId(MapFragment.class), false, false);
			tabInfo.setTitle("Karten");
			tabInfos.add(tabInfo);
		} else {

			TabInfo tabInfo = new TabInfo(CharacterFragment.class, getTabResourceId(CharacterFragment.class), true,
					false);
			tabInfo.setTitle("Charakter");
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.dsa_talents);
			tabInfo.setTitle("Talente");
			ListSettings listSettings = (ListSettings) tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(ListItemType.Talent));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.dsa_spells);
			tabInfo.setTitle("Zauber");
			listSettings = (ListSettings) tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(AttributeType.Astralenergie_Aktuell));
			listSettings.addListItem(new ListItem(ListItemType.Spell));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.dsa_arts);
			tabInfo.setTitle("Künste");
			listSettings = (ListSettings) tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(AttributeType.Karmaenergie_Aktuell));
			listSettings.addListItem(new ListItem(ListItemType.Talent, TalentGroupType.Gaben.name()));
			listSettings.addListItem(new ListItem(ListItemType.Header, ListItemType.Art));
			listSettings.addListItem(new ListItem(ListItemType.Art));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(BodyFragment.class, getTabResourceId(BodyFragment.class));
			tabInfo.setTitle("Wunden");
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.dsa_fight);
			tabInfo.setTitle("Kampf");
			listSettings = (ListSettings) tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(AttributeType.Lebensenergie_Aktuell));
			listSettings.addListItem(new ListItem(AttributeType.Ausdauer_Aktuell));
			listSettings.addListItem(new ListItem(AttributeType.Initiative_Aktuell));
			listSettings.addListItem(new ListItem(ListItemType.EquippedItem));
			listSettings.addListItem(new ListItem(AttributeType.Ausweichen));
			listSettings.addListItem(new ListItem(ListItemType.Wound));
			listSettings.addListItem(new ListItem(ListItemType.Probe));
			listSettings.addListItem(new ListItem(ListItemType.Modificator));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ItemsFragment.class, getTabResourceId(ItemsFragment.class), false, true);
			tabInfo.setTitle("Gegenstände");
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.dsa_notes, false, false);
			tabInfo.setTitle("Notizen");
			listSettings = (ListSettings) tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(ListItemType.Notes));
			listSettings.addListItem(new ListItem(ListItemType.Document));
			listSettings.addListItem(new ListItem(ListItemType.Purse));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(AnimalFragment.class, getTabResourceId(AnimalFragment.class), true, false);
			tabInfo.setTitle("Begleiter");
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(MapFragment.class, getTabResourceId(MapFragment.class), false, false);
			tabInfo.setTitle("Karten");
			tabInfos.add(tabInfo);
		}

		return tabInfos;
	}

	/**
	 * Constructs a json object with the current data
	 * 
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSONObject() throws JSONException {
		JSONObject out = new JSONObject();

		out.put(FIELD_VERSION, DsaTabApplication.getInstance().getPackageVersion());

		JSONArray array = new JSONArray();
		for (int s = 0; s < armorAttributes.length; s++) {
			JSONArray inArray = new JSONArray();
			if (armorAttributes[s] != null) {
				for (int i = 0; i < armorAttributes[s].size(); i++) {
					inArray.put(i, armorAttributes[s].get(i).toJSONObject());
				}
			}
			array.put(s, inArray);
		}
		out.put(FIELD_ARMOR_ATTRIBUTES, array);

		Util.putArray(out, tabInfos, FIELD_TABS_PORTRAIT);
		Util.putArray(out, modificators, FIELD_MODIFICATORS);
		Util.putArray(out, wounds, FIELD_WOUNDS);
		Util.putArray(out, attributes, FIELD_ATTRIBUTES);
		Util.putArray(out, metaTalents, FIELD_META_TALENTS);
		Util.putArray(out, events, FIELD_EVENTS);
		Util.putArray(out, customProbes, FIELD_CUSTOM_PROBES);

		out.put(FIELD_COMBAT_STYLE, combatStyle.name());
		out.put(FIELD_BE_CALCULATION, beCalculation);
		out.put(FIELD_LE_MODIFIER, leModifierActive);
		out.put(FIELD_AU_MODIFIER, auModifierActive);
		out.put(FIELD_PROPERTIES, new JSONObject(properties));
		return out;
	}

}
