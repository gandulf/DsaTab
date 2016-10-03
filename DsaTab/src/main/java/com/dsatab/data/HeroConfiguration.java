package com.dsatab.data;

import android.text.TextUtils;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.config.TabInfo;
import com.dsatab.data.Hero.CombatStyle;
import com.dsatab.data.Purse.Currency;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.items.Item;
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

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

	private static final String FIELD_ACTIVE_CURRENCY = "acticeCurrency";

    private static final String FIELD_STORAGE_TYPE = "storageType";
    private static final String FIELD_STORAGE_HERO_ID = "storageHeroId";
    private static final String FIELD_STORAGE_CONFIG_ID = "storageConfigId";

	private List<TabInfo> tabInfos;

	private List<CustomModificator> modificators;
	private List<WoundAttribute> wounds;
	private List<ArmorAttribute>[] armorAttributes;
	private Set<CustomAttribute> attributes;
	private Set<MetaTalent> metaTalents;
	private List<Event> events;
	private List<CustomProbe> customProbes;

	private List<ItemContainer<Item>> itemContainers;

	private CombatStyle combatStyle;
	private boolean beCalculation;

	private boolean leModifierActive;
	private boolean auModifierActive;

	private Map<String, String> properties;


    private HeroExchange.StorageType storageType;
    private String storageHeroId,storageConfigId;
	/**
	 * 
	 */
	public HeroConfiguration(Hero hero) {
		modificators = new ArrayList<CustomModificator>();
		wounds = new ArrayList<WoundAttribute>();
		armorAttributes = new ArrayList[Hero.INVENTORY_SET_COUNT];
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

    public static boolean updateStorageInfo(JSONObject json, HeroExchange.StorageType storageType, String heroId, String configId) throws JSONException {
        boolean changes = false;

        changes |= ObjectUtils.notEqual(json.opt(FIELD_STORAGE_HERO_ID),heroId);
        changes |= ObjectUtils.notEqual(json.opt(FIELD_STORAGE_CONFIG_ID),configId);


        json.putOpt(FIELD_STORAGE_HERO_ID, heroId);
        json.putOpt(FIELD_STORAGE_CONFIG_ID, configId);
        if (storageType!=null) {
            json.put(FIELD_STORAGE_TYPE, storageType.name());
            changes |= ObjectUtils.notEqual(json.opt(FIELD_STORAGE_TYPE),storageType.name());
        }

        return changes;
    }

	public HeroConfiguration(Hero hero, JSONObject in) throws JSONException {
		int version = in.optInt(FIELD_VERSION);

        if (in.has(FIELD_STORAGE_TYPE)) {
            storageType = HeroExchange.StorageType.valueOf(in.optString(FIELD_STORAGE_TYPE));
        }
        storageHeroId = in.optString(FIELD_STORAGE_HERO_ID,null);
        storageConfigId = in.optString(FIELD_STORAGE_CONFIG_ID,null);

		JSONArray array = null;

		if (version < 95) {
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

        if (hero !=null) {

            if (in.has(FIELD_MODIFICATORS)) {
                array = in.getJSONArray(FIELD_MODIFICATORS);
                modificators = new ArrayList<CustomModificator>(array.length());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject tab = array.getJSONObject(i);
                    CustomModificator info = new CustomModificator(hero, tab);
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
                        WoundAttribute info = new WoundAttribute(hero, tab);
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
                            ArmorAttribute info = new ArmorAttribute(hero, tab);
                            armorAttributes[s].add(info);
                        } catch (Exception e) {
                            Debug.warning("Unknown ArmorAttribute, skipping it: " + tab);
                        }
                    }
                }
            } else {
                armorAttributes = new ArrayList[Hero.INVENTORY_SET_COUNT];
            }

            if (in.has(FIELD_ATTRIBUTES)) {
                array = in.getJSONArray(FIELD_ATTRIBUTES);
                attributes = new HashSet<CustomAttribute>(array.length());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject tab = array.getJSONObject(i);
                    try {
                        CustomAttribute info = new CustomAttribute(hero, tab);
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
                        MetaTalent info = new MetaTalent(hero, tab);
                        metaTalents.add(info);
                    } catch (Exception e) {
                        Debug.error(e);
                    }
                }
            } else {
                metaTalents = new HashSet<MetaTalent>();
            }

            if (in.has(FIELD_CUSTOM_PROBES)) {
                array = in.getJSONArray(FIELD_CUSTOM_PROBES);
                customProbes = new ArrayList<CustomProbe>(array.length());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject tab = array.getJSONObject(i);
                    CustomProbe info = new CustomProbe(hero, tab);
                    customProbes.add(info);
                }
            } else {
                customProbes = new ArrayList<CustomProbe>();
            }
        }
	}

	public String getProperty(String key) {
		return properties.get(key);
	}

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	public List<TabInfo> getTabs() {
		return tabInfos;
	}

    public List<TabInfo> getActiveTabs(Hero hero) {
        List<TabInfo> activeTabs = new ArrayList<>();

        ListItem spells = new ListItem(ListItemType.Spell);
        ListItem ae = new ListItem(AttributeType.Astralenergie_Aktuell);

        ListItem arts = new ListItem(ListItemType.Art);
        ListItem ke = new ListItem(AttributeType.Karmaenergie_Aktuell);
        ListItem gaben = new ListItem(TalentGroupType.Gaben);

        if (hero !=null) {
            for (TabInfo tabInfo : tabInfos) {
                if (tabInfo == null)
                    continue;

                // skip animal tab if animals is empty
                if (!hero.hasAnimals() && tabInfo.hasOnlyActivityClazz(AnimalFragment.class)) {
                    continue;
                } else if (tabInfo.hasOnlyActivityClazz(ListableFragment.class)) {
                    if (!hero.hasSpells() && tabInfo.hasOnlyListItem(spells, ae)) {
                        continue;
                    } else if (!hero.hasArts() && !hero.hasTalents(TalentGroupType.Gaben) && tabInfo.hasOnlyListItem(arts, ke, gaben)) {
                        continue;
                    }
                }
                activeTabs.add(tabInfo);
            }
        } else {
            activeTabs.addAll(tabInfos);
        }
        return activeTabs;
    }

	public void setTabs(List<TabInfo> tabs) {
		tabInfos = tabs;
	}

	private boolean isDualPanel() {
		return DsaTabApplication.getInstance().getResources().getBoolean(R.bool.dual_panes);
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

    public static String getFieldTabsPortrait() {
        return FIELD_TABS_PORTRAIT;
    }

    public String getStorageHeroId() {
        return storageHeroId;
    }

    public void setStorageHeroId(String storageHeroId) {
        this.storageHeroId = storageHeroId;
    }

    public String getStorageConfigId() {
        return storageConfigId;
    }

    public void setStorageConfigId(String storageConfigId) {
        this.storageConfigId = storageConfigId;
    }

    public HeroExchange.StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(HeroExchange.StorageType storageType) {
        this.storageType = storageType;
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

	public Currency getActiveCurrency() {
		if (!TextUtils.isEmpty(getProperty(FIELD_ACTIVE_CURRENCY))) {
			return Currency.valueOf(getProperty(FIELD_ACTIVE_CURRENCY));
		} else {
			return Currency.Mittelreich;
		}
	}

	public void setActiveCurrency(Currency activeCurrency) {
		if (activeCurrency != null) {
			setProperty(FIELD_ACTIVE_CURRENCY, activeCurrency.name());
		} else {
			setProperty(FIELD_ACTIVE_CURRENCY, Currency.Mittelreich.name());
		}
	}

	public List<ItemContainer<Item>> getItemContainers() {
		if (itemContainers == null) {
			itemContainers = new ArrayList<ItemContainer<Item>>();
			itemContainers.add(new ItemContainer<Item>(3, "Rucksack", Util.getUriForResourceId(R.drawable.vd_backpack)));
		}
		return itemContainers;
	}

	public void setItemContainers(List<ItemContainer<Item>> itemContainers) {
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
			tabInfo.setTitle("Charakter");
			listSettings = tabInfo.getListSettings(1);
			listSettings.addListItem(new ListItem(ListItemType.Talent));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, ListableFragment.class, R.drawable.vd_bookmark);
			tabInfo.setTitle("Zauber und Künste");
            tabInfo.setBackgroundId(R.drawable.backdrop_spells);
			listSettings = tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(AttributeType.Astralenergie_Aktuell));
			listSettings.addListItem(new ListItem(ListItemType.Spell));
			listSettings = tabInfo.getListSettings(1);
			listSettings.addListItem(new ListItem(AttributeType.Karmaenergie_Aktuell));
			listSettings.addListItem(new ListItem(TalentGroupType.Gaben));
			listSettings.addListItem(new ListItem(ListItemType.Header, ListItemType.Art));
			listSettings.addListItem(new ListItem(ListItemType.Art));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, BodyFragment.class, R.drawable.vd_all_for_one);
			tabInfo.setTitle("Kampf");
            tabInfo.setBackgroundId(R.drawable.backdrop_combat);
			listSettings = tabInfo.getListSettings(0);
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
			tabInfo.setTitle("Ausrüstung");
            tabInfo.setBackgroundId(R.drawable.backdrop_combat);
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, ListableFragment.class, R.drawable.vd_tied_scroll, false, false);
			tabInfo.setTitle("Notizen");
            tabInfo.setBackgroundId(R.drawable.backdrop_notes);
			listSettings = tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(ListItemType.Notes));
			listSettings.addListItem(new ListItem(ListItemType.Document));
			listSettings = tabInfo.getListSettings(1);
			listSettings.addListItem(new ListItem(ListItemType.Purse));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(AnimalFragment.class, getTabResourceId(AnimalFragment.class), true, false);
			tabInfo.setTitle("Begleiter");
            tabInfo.setBackgroundId(R.drawable.backdrop_dsa);
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(MapFragment.class, getTabResourceId(MapFragment.class), false, false);
			tabInfo.setTitle("Karten");
            tabInfo.setBackgroundId(R.drawable.backdrop_notes);
			tabInfos.add(tabInfo);
		} else {

			TabInfo tabInfo = new TabInfo(CharacterFragment.class, getTabResourceId(CharacterFragment.class), true,
					false);
			tabInfo.setTitle("Charakter");
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.vd_biceps);
			tabInfo.setTitle("Talente");
            tabInfo.setBackgroundId(R.drawable.backdrop_talents);
			ListSettings listSettings = tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(ListItemType.Talent));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.vd_bookmark);
			tabInfo.setTitle("Zauber");
            tabInfo.setBackgroundId(R.drawable.backdrop_spells);
			listSettings = tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(AttributeType.Astralenergie_Aktuell));
			listSettings.addListItem(new ListItem(ListItemType.Spell));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.vd_beams_aura);
			tabInfo.setTitle("Künste");
            tabInfo.setBackgroundId(R.drawable.backdrop_spells);
			listSettings = tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(AttributeType.Karmaenergie_Aktuell));
			listSettings.addListItem(new ListItem(TalentGroupType.Gaben));
			listSettings.addListItem(new ListItem(ListItemType.Header, ListItemType.Art));
			listSettings.addListItem(new ListItem(ListItemType.Art));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(BodyFragment.class, getTabResourceId(BodyFragment.class));
			tabInfo.setTitle("Wunden");
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.vd_all_for_one);
			tabInfo.setTitle("Kampf");
            tabInfo.setBackgroundId(R.drawable.backdrop_combat);
			listSettings = tabInfo.getListSettings(0);
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
			tabInfo.setTitle("Ausrüstung");
            tabInfo.setBackgroundId(R.drawable.backdrop_combat);
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(ListableFragment.class, R.drawable.vd_tied_scroll, false, false);
			tabInfo.setTitle("Notizen");
            tabInfo.setBackgroundId(R.drawable.backdrop_notes);
			listSettings = tabInfo.getListSettings(0);
			listSettings.addListItem(new ListItem(ListItemType.Notes));
			listSettings.addListItem(new ListItem(ListItemType.Document));
			listSettings.addListItem(new ListItem(ListItemType.Purse));
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(AnimalFragment.class, getTabResourceId(AnimalFragment.class), true, false);
			tabInfo.setTitle("Begleiter");
            tabInfo.setBackgroundId(R.drawable.backdrop_dsa);
			tabInfos.add(tabInfo);

			tabInfo = new TabInfo(MapFragment.class, getTabResourceId(MapFragment.class), false, false);
			tabInfo.setTitle("Karten");
            tabInfo.setBackgroundId(R.drawable.backdrop_notes);
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

        updateStorageInfo(out,storageType,storageHeroId,storageConfigId);
		return out;
	}

}
