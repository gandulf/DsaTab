package com.dsatab.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.Purse.Currency;
import com.dsatab.data.Talent.Flags;
import com.dsatab.data.enums.ArmorPosition;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.Hand;
import com.dsatab.data.enums.Position;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.HuntingWeapon;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.data.modifier.AbstractModificator;
import com.dsatab.data.modifier.AuModificator;
import com.dsatab.data.modifier.CustomModificator;
import com.dsatab.data.modifier.LeModificator;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.data.modifier.Modifier;
import com.dsatab.data.modifier.Rules;
import com.dsatab.data.modifier.RulesModificator;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;
import com.dsatab.data.notes.ChangeEvent;
import com.dsatab.data.notes.Connection;
import com.dsatab.data.notes.Event;
import com.dsatab.exception.TalentTypeUnknownException;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.view.listener.HeroInventoryChangedListener;

public class Hero extends AbstractBeing {

	public static final String JAGTWAFFE = "jagtwaffe";
	public static final String PREFIX_NKWAFFE = "nkwaffe";
	public static final String PREFIX_FKWAFFE = "fkwaffe";
	public static final String PREFIX_BK = "bk";

	public static final int MAXIMUM_SET_NUMBER = 3;
	public static final int FIRST_INVENTORY_SCREEN = MAXIMUM_SET_NUMBER;

	public enum CombatStyle {
		Offensive, Defensive
	}

	private HeroFileInfo fileInfo;

	private EditableValue experience, freeExperience;

	private Map<String, Spell> spellsByName;

	private Map<ArmorPosition, ArmorAttribute>[] armorAttributes;
	private Map<Position, WoundAttribute> wounds;

	private List<EquippedItem>[] equippedItems = null;
	private List<Connection> connections = null;

	private List<Animal> animals = null;

	private HuntingWeapon[] huntingWeapons;

	private Set<Modificator> modificators = null;
	private Map<ModificatorType, Set<Modificator>> modificatorsByType = null;

	LeModificator leModificator;
	AuModificator auModificator;

	private Purse purse = null;

	private int activeSet = 0;

	private List<File> deletableAudioFiles = new LinkedList<File>();

	private HeroBaseInfo baseInfo;

	private List<ChangeEvent> changeEvents;

	private Integer oldAuRatioLevel, oldLeRatioLevel, beCache;

	// event listener

	private Set<HeroInventoryChangedListener> itemListener = new HashSet<HeroInventoryChangedListener>();

	@SuppressWarnings("unchecked")
	public Hero(HeroFileInfo path) throws IOException, JSONException {
		this.fileInfo = path;

		this.equippedItems = new List[MAXIMUM_SET_NUMBER];
		this.huntingWeapons = new HuntingWeapon[MAXIMUM_SET_NUMBER];

		this.changeEvents = new ArrayList<ChangeEvent>();
		this.animals = new ArrayList<Animal>();

		// load modifiers
		this.leModificator = new LeModificator(this);
		this.auModificator = new AuModificator(this);

		this.baseInfo = new HeroBaseInfo();

		// preload values

		this.experience = new Experience(this, "Abenteuerpunkte");
		this.experience.setMaximum(100000);

		this.freeExperience = new EditableValue(this, "Freie Abenteuerpunkte");
		this.freeExperience.setMaximum(100000);

		this.spellsByName = new HashMap<String, Spell>();

		for (int i = 0; i < equippedItems.length; i++) {
			this.equippedItems[i] = new LinkedList<EquippedItem>();
		}
		this.purse = new Purse();
		this.connections = new ArrayList<Connection>();
	}

	/**
	 * 
	 */
	public void setHeroConfiguration(HeroConfiguration configuration) {
		this.configuration = configuration;

		// refill modificators
		// clear modificators no make sure the user defined ones are loaded into modificators map too
		this.modificators = null;
		this.modificatorsByType = null;

		// fill metatalents

		List<TalentType> metaTalentTypes = new ArrayList<TalentType>(Arrays.asList(TalentType.WacheHalten,
				TalentType.Kräutersuchen, TalentType.NahrungSammeln, TalentType.PirschUndAnsitzjagd));

		for (MetaTalent metaTalent : configuration.getMetaTalents()) {
			metaTalentTypes.remove(metaTalent.getType());
			addTalent(metaTalent);
		}
		for (TalentType metaType : metaTalentTypes) {
			MetaTalent metaTalent = new MetaTalent(this, metaType);
			addTalent(metaTalent);
		}

		// fill wounds
		final List<Position> woundPositions = DsaTabApplication.getInstance().getConfiguration().getWoundPositions();
		wounds = new EnumMap<Position, WoundAttribute>(Position.class);
		for (WoundAttribute rs : configuration.getWounds()) {
			if (woundPositions.contains(rs.getPosition()))
				wounds.put(rs.getPosition(), rs);
		}
		// fill not existing values with 0
		for (Position pos : woundPositions) {
			WoundAttribute wound = wounds.get(pos);
			if (wound == null) {
				wound = new WoundAttribute(this, pos);
				configuration.addWound(wound);
				wounds.put(pos, wound);
			}
		}
	}

	public HeroBaseInfo getBaseInfo() {
		return baseInfo;
	}

	public void addHeroInventoryChangedListener(HeroInventoryChangedListener v) {
		itemListener.add(v);
	}

	public void removeHeroInventoryChangedListener(HeroInventoryChangedListener v) {
		itemListener.remove(v);
	}

	public EquippedItem getEquippedItem(int set, String name) {
		if (name == null || set < 0 || set >= MAXIMUM_SET_NUMBER)
			return null;

		for (EquippedItem item : getEquippedItems(set)) {
			if (name.equals(item.getName()))
				return item;
		}
		return null;
	}

	public EquippedItem getEquippedItem(UUID id) {
		for (int i = 0; i < MAXIMUM_SET_NUMBER; i++) {
			for (EquippedItem equippedItem : getEquippedItems(i)) {
				if (equippedItem.getId().equals(id))
					return equippedItem;
			}
		}
		return null;

	}

	public Purse getPurse() {
		return purse;
	}

	public List<EquippedItem> getEquippedItems(Class<? extends ItemSpecification>... itemClass) {

		List<EquippedItem> items = new LinkedList<EquippedItem>();

		for (EquippedItem ei : getEquippedItems()) {
			ItemSpecification item = ei.getItemSpecification();
			for (Class<? extends ItemSpecification> clazz : itemClass) {
				if (clazz.isAssignableFrom(item.getClass())) {
					items.add(ei);
					break;
				}
			}
		}

		return items;

	}

	public List<EquippedItem> getAllEquippedItems() {
		LinkedList<EquippedItem> items = new LinkedList<EquippedItem>();

		for (int i = 0; i < MAXIMUM_SET_NUMBER; i++) {
			items.addAll(getEquippedItems(i));
		}

		return items;
	}

	public List<EquippedItem> getEquippedItems() {
		return getEquippedItems(activeSet);
	}

	public void setHuntingWeapon(EquippedItem item) {
		if (huntingWeapons[activeSet] != null) {
			if (item != null && item.isDistanceWeapon()) {
				huntingWeapons[activeSet].setNumber(item.getNameId());
			} else {
				huntingWeapons[activeSet].setNumber(0);
			}
		}
	}

	public EquippedItem getHuntingWeapon() {
		if (huntingWeapons[activeSet] != null) {
			Integer number = huntingWeapons[activeSet].getNumber();
			if (number != null && number > 0) {
				return getEquippedItem(activeSet, PREFIX_FKWAFFE + number);
			}
		}
		return null;
	}

	public List<EquippedItem> getEquippedItems(int selectedSet) {
		return equippedItems[selectedSet];
	}

	@Override
	protected String getId() {
		return getFileInfo().getKey();
	}

	public int getActiveSet() {
		return activeSet;
	}

	public void setActiveSet(int activeSet) {

		if (activeSet != this.activeSet) {
			int oldSet = activeSet;
			this.activeSet = activeSet;

			resetBe();

			fireActiveSetChangedEvent(activeSet, oldSet);
		}
	}

	public void fireValueChangedEvent(Value value) {
		if (value instanceof Attribute) {

			Attribute attribute = (Attribute) value;
			Attribute attr;
			switch (attribute.getType()) {
			case Behinderung:
				clearModifiersCache();
				attr = getAttribute(AttributeType.Geschwindigkeit);
				if (attr != null)
					attr.checkBaseValue();
				break;
			case Gewandtheit:
				attr = getAttribute(AttributeType.Geschwindigkeit);
				if (attr != null)
					attr.checkBaseValue();
			case Mut:
			case Klugheit:
			case Intuition:
			case Charisma:
			case Fingerfertigkeit:
			case Konstitution:
			case Körperkraft:
				attr = getAttribute(AttributeType.at);
				if (attr != null)
					attr.setValue(attr.getReferenceValue());

				attr = getAttribute(AttributeType.pa);
				if (attr != null)
					attr.setValue(attr.getReferenceValue());

				attr = getAttribute(AttributeType.fk);
				if (attr != null)
					attr.setValue(attr.getReferenceValue());

				attr = getAttribute(AttributeType.ini);
				if (attr != null)
					attr.setValue(attr.getReferenceValue());

				// check for magic resistance changes:
				attr = getAttribute(AttributeType.Magieresistenz);
				if (attr != null)
					attr.checkBaseValue();

				attr = getAttribute(AttributeType.Astralenergie_Aktuell);
				if (attr != null)
					attr.checkBaseValue();

				attr = getAttribute(AttributeType.Astralenergie);
				if (attr != null)
					attr.checkBaseValue();

				attr = getAttribute(AttributeType.Ausdauer_Aktuell);
				if (attr != null)
					attr.checkBaseValue();

				attr = getAttribute(AttributeType.Ausdauer);
				if (attr != null)
					attr.checkBaseValue();

				attr = getAttribute(AttributeType.Lebensenergie_Aktuell);
				if (attr != null)
					attr.checkBaseValue();

				attr = getAttribute(AttributeType.Lebensenergie);
				if (attr != null)
					attr.checkBaseValue();

				break;
			case Ausdauer_Aktuell:
				postAuRatioCheck();
				break;
			case Lebensenergie_Aktuell:
				postLeRatioCheck();
				break;
			case Lebensenergie:
				attr = getAttribute(AttributeType.Lebensenergie_Aktuell);
				if (attr != null && attr.checkValue(attr.getMaximum())) {
					postLeRatioCheck();
				}
				break;
			case Ausdauer:
				attr = getAttribute(AttributeType.Ausdauer_Aktuell);
				if (attr != null && attr.checkValue(attr.getMaximum())) {
					postAuRatioCheck();
				}
				break;
			case Astralenergie:
				attr = getAttribute(AttributeType.Astralenergie_Aktuell);
				if (attr != null && attr.checkValue(attr.getMaximum())) {
					fireValueChangedEvent(attr);
				}
				break;
			case Karmaenergie:
				attr = getAttribute(AttributeType.Karmaenergie_Aktuell);
				if (attr != null && attr.checkValue(attr.getMaximum())) {
					fireValueChangedEvent(attr);
				}
				break;
			default:
				// do nothing
				break;
			}
		} else if (value instanceof WoundAttribute) {
			WoundAttribute woundAttribute = (WoundAttribute) value;
			fireModifierChangedEvent(woundAttribute.getModificator());
		}

		super.fireValueChangedEvent(value);

	}

	public void fireModifierChangedEvent(Modificator modifier) {
		if (modifier.fulfills()) {
			if (!getModificators().contains(modifier)) {
				addModificator(modifier);
				return;
			}
		} else {
			if (getModificators().contains(modifier)) {
				removeModificator(modifier);
				return;
			}
		}

		super.fireModifierChangedEvent(modifier);

		Attribute le = getAttribute(AttributeType.Lebensenergie);
		if (modifier.affects(le)) {
			fireValueChangedEvent(le);
		}
		Attribute au = getAttribute(AttributeType.Ausdauer);
		if (modifier.affects(au)) {
			fireValueChangedEvent(au);
		}
	}

	public void onPostHeroLoaded(Context context) {
		for (int i = 0; i < equippedItems.length; i++) {
			Util.sort(equippedItems[i]);
		}

		for (int i = 0; i < huntingWeapons.length; i++) {
			if (huntingWeapons[i] == null) {
				HuntingWeapon huntingWeapon = new HuntingWeapon(i, 0);
				huntingWeapons[i] = huntingWeapon;
			}
		}

		prepareAdvantages(context);
		prepareSpeciaFeatures(context);
		prepareSystemRules(context);

		if (getPurse() != null && getPurse().getActiveCurrency() == null) {
			getPurse().setActiveCurrency(Currency.Mittelreich);
		}
	}

	private void prepareSpeciaFeatures(Context context) {

		String spezialisierungsName = null;
		String spezialisierungsParam = null;

		Feature adv = getFeature(FeatureType.Talentspezialisierung);
		if (adv != null) {
			for (int i = adv.getValues().size() - 1; i >= 0; i--) {
				String[] values = adv.getValues(i);
				if (values != null) {
					spezialisierungsName = values.length > 0 ? values[0] : null;
					spezialisierungsParam = values.length > 1 ? values[1] : null;

					if (!TextUtils.isEmpty(spezialisierungsName)) {
						Talent talent = getTalent(spezialisierungsName);
						if (talent != null) {
							talent.addFlag(Talent.Flags.TalentSpezialisierung);
							if (!TextUtils.isEmpty(spezialisierungsParam)) {
								talent.setTalentSpezialisierung(spezialisierungsParam);
							}
							adv.getValues().remove(i);
						} else {
							Debug.error("Could not find talent for spezialisierung " + spezialisierungsName);
						}
					}
				}
			}
			if (adv.getValues().isEmpty()) {
				removeFeature(adv);
			}
		}
		adv = getFeature(FeatureType.Zauberspezialisierung);
		if (adv != null) {
			for (int i = adv.getValues().size() - 1; i >= 0; i--) {
				String[] values = adv.getValues(i);
				if (values != null) {
					spezialisierungsName = values.length > 0 ? values[0] : null;
					spezialisierungsParam = values.length > 1 ? values[1] : null;

					if (!TextUtils.isEmpty(spezialisierungsName)) {
						Spell spell = getSpell(spezialisierungsName);
						if (spell != null) {
							spell.addFlag(Spell.Flags.ZauberSpezialisierung);
							if (!TextUtils.isEmpty(spezialisierungsParam)) {
								spell.setZauberSpezialisierung(spezialisierungsParam);
							}
							adv.getValues().remove(i);
						} else {
							Debug.error("Could not find spell for spezialisierung " + spezialisierungsName);
						}
					}
				}
			}
			if (adv.getValues().isEmpty()) {
				removeFeature(adv);
			}
		}

	}

	private void prepareAdvantages(Context context) {
		Feature adv = getFeature(FeatureType.BegabungFürTalent);
		if (adv != null) {
			for (Iterator<String> iter = adv.getValues().iterator(); iter.hasNext();) {
				String value = iter.next();
				Talent talent = getTalent(value);
				if (talent != null) {
					talent.addFlag(Flags.Begabung);
					iter.remove();
				} else {
					Debug.error("Could not find talent for begabung " + value);
				}
			}
			if (adv.getValues().isEmpty()) {
				removeFeature(adv);
			}
		}
		adv = getFeature(FeatureType.Talentschub);
		if (adv != null) {
			for (Iterator<String> iter = adv.getValues().iterator(); iter.hasNext();) {
				String value = iter.next();
				Talent talent = getTalent(value);
				if (talent != null) {
					talent.addFlag(Flags.Talentschub);
					iter.remove();
				} else {
					Debug.error("Could not find talent for talentschub " + value);
				}
			}
			if (adv.getValues().isEmpty()) {
				removeFeature(adv);
			}
		}
		adv = getFeature(FeatureType.Meisterhandwerk);
		if (adv != null) {
			for (Iterator<String> iter = adv.getValues().iterator(); iter.hasNext();) {
				String value = iter.next();
				try {
					Talent talent = getTalent(value);
					if (talent != null) {
						talent.addFlag(Flags.Meisterhandwerk);
						iter.remove();
					} else {
						Debug.error("Could not find talent for meisterhandwerk " + value);
					}
				} catch (TalentTypeUnknownException e) {
					// Meisterhandwerk can also be used for attributes in this case we have no special handling for it
				}
			}
			if (adv.getValues().isEmpty()) {
				removeFeature(adv);
			}
		}
		adv = getFeature(FeatureType.BegabungFürTalentgruppe);
		if (adv != null) {
			for (Iterator<String> iter = adv.getValues().iterator(); iter.hasNext();) {
				String value = iter.next();
				try {
					TalentGroupType groupType = TalentGroupType.valueOf(value);
					TalentGroup talentGroup = getTalentGroup(groupType);
					if (talentGroup != null) {
						talentGroup.addFlag(Flags.Begabung);
						iter.remove();
					} else {
						Debug.error("Could not find talentgroup for begabung " + value);
					}
				} catch (Exception e) {
					Debug.warning("Begabung für [Talentgruppe], unknown talentgroup:" + value);
				}
			}
			if (adv.getValues().isEmpty()) {
				removeFeature(adv);
			}
		}
		adv = getFeature(FeatureType.BegabungFürZauber);
		if (adv != null) {
			for (Iterator<String> iter = adv.getValues().iterator(); iter.hasNext();) {
				String value = iter.next();
				Spell spell = getSpells().get(value);
				if (spell != null) {
					spell.addFlag(com.dsatab.data.Spell.Flags.Begabung);
					iter.remove();
				} else {
					Debug.error("Could not find spell for begabung " + value);
				}
			}
			if (adv.getValues().isEmpty()) {
				removeFeature(adv);
			}
		}
		adv = getFeature(FeatureType.BegabungFürRitual);
		if (adv != null) {
			for (Iterator<String> iter = adv.getValues().iterator(); iter.hasNext();) {
				String value = iter.next();
				Art art = getArt(value);
				if (art != null) {
					art.addFlag(com.dsatab.data.Art.Flags.Begabung);
					iter.remove();
				} else {
					Debug.error("Could not find art for begabung " + value);
				}
			}
			if (adv.getValues().isEmpty()) {
				removeFeature(adv);
			}
		}
		adv = getFeature(FeatureType.ÜbernatürlicheBegabung);
		if (adv != null) {
			for (Iterator<String> iter = adv.getValues().iterator(); iter.hasNext();) {
				String value = iter.next();
				Spell spell = getSpell(value);
				if (spell != null) {
					spell.addFlag(com.dsatab.data.Spell.Flags.ÜbernatürlicheBegabung);
					iter.remove();
				} else {
					Debug.error("Could not find Spell for ÜbernatürlicheBegabung " + value);
				}
			}
			if (adv.getValues().isEmpty()) {
				removeFeature(adv);
			}
		}
	}

	protected void prepareSystemRules(Context context) {
		// add build in modificators for rules:
		List<RulesModificator> systemRules = Rules.prepareRules(this);

		SharedPreferences preferences = DsaTabApplication.getPreferences();
		if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_EASIER_WOUNDS, false)) {
			for (RulesModificator mod : systemRules) {
				if (FeatureType.Eisern.xmlName().equals(mod.getTitle())) {
					mod.setModifier(1);
					mod.setDescription("Wundschwelle Eisern +1");
				}
				if (FeatureType.Glasknochen.xmlName().equals(mod.getTitle())) {
					mod.setModifier(-1);
					mod.setDescription("Wundschwelle Glasknochen -1");
				}
			}
		}

		for (RulesModificator mod : systemRules) {
			if (mod.fulfills() || mod.isDynamic()) {
				getModificators().add(mod);
			}
		}
	}

	public void onPostHeroSaved() {
		for (File f : deletableAudioFiles) {
			f.delete();
		}
	}

	void fireItemAddedEvent(Item item) {
		for (HeroInventoryChangedListener l : itemListener) {
			l.onItemAdded(item);
		}
	}

	void fireItemContainerAddedEvent(ItemContainer item) {
		for (HeroInventoryChangedListener l : itemListener) {
			l.onItemContainerAdded(item);
		}
	}

	void fireItemContainerRemovedEvent(ItemContainer item) {
		for (HeroInventoryChangedListener l : itemListener) {
			l.onItemContainerRemoved(item);
		}
	}

	public void fireItemContainerChangedEvent(ItemContainer item) {
		for (HeroInventoryChangedListener l : itemListener) {
			l.onItemContainerChanged(item);
		}
	}

	void fireItemRemovedEvent(Item item) {
		for (HeroInventoryChangedListener l : itemListener) {
			l.onItemRemoved(item);
		}
	}

	void fireItemEquippedEvent(EquippedItem item) {
		for (HeroInventoryChangedListener l : itemListener) {
			l.onItemEquipped(item);
		}
	}

	public void fireItemChangedEvent(EquippedItem item) {
		for (HeroInventoryChangedListener l : itemListener) {
			l.onItemChanged(item);
		}
	}

	public void fireItemChangedEvent(Item item) {
		for (HeroInventoryChangedListener l : itemListener) {
			l.onItemChanged(item);
		}
	}

	public void fireActiveSetChangedEvent(int newSet, int oldSet) {
		Debug.trace("ON set changed from " + oldSet + " to " + newSet);
		for (HeroInventoryChangedListener l : itemListener) {
			l.onActiveSetChanged(newSet, oldSet);
		}
	}

	void fireItemUnequippedEvent(EquippedItem item) {
		for (HeroInventoryChangedListener l : itemListener) {
			l.onItemUnequipped(item);
		}
	}

	void fireModifierAddedEvent(Modificator modifier) {
		super.fireModifierAddedEvent(modifier);

		Attribute le = getAttribute(AttributeType.Lebensenergie);
		if (modifier.affects(le)) {
			fireValueChangedEvent(le);
		}
		Attribute au = getAttribute(AttributeType.Ausdauer);
		if (modifier.affects(au)) {
			fireValueChangedEvent(au);
		}
	}

	public void clearModifiersCache(Probe probe) {
		probe.clearModifierCache();
	}

	void fireModifierRemovedEvent(Modificator modifier) {
		super.fireModifierRemovedEvent(modifier);

		Attribute le = getAttribute(AttributeType.Lebensenergie);
		if (modifier.affects(le)) {
			fireValueChangedEvent(le);
		}
		Attribute au = getAttribute(AttributeType.Ausdauer);
		if (modifier.affects(au)) {
			fireValueChangedEvent(au);
		}

	}

	public void moveItem(Item item, int newScreen) {
		removeItem(item, false);
		item.setContainerId(newScreen);
		addItem(item);
	}

	/**
	 * @param item
	 */
	public void removeItem(Item item) {
		removeItem(item, true);
	}

	/**
	 * @param item
	 */
	protected void removeItem(Item item, boolean cleanupEquippedItems) {
		boolean found = false;
		for (ItemContainer itemContainer : getItemContainers()) {
			found = itemContainer.remove(item);
			if (found) {
				break;
			}
		}

		if (found) {
			fireItemRemovedEvent(item);

			if (cleanupEquippedItems) {
				List<EquippedItem> toremove = new ArrayList<EquippedItem>();

				for (int i = 0; i < equippedItems.length; i++) {

					for (EquippedItem equippedItem : getEquippedItems(i)) {

						if (equippedItem.getItem() == null) {
							Debug.warning("Empty EquippedItem found during item delete:" + equippedItem.getName()
									+ " - " + equippedItem.getItem().getName());
							continue;
						}

						if (equippedItem.getItem().equals(item)) {
							toremove.add(equippedItem);
						}
					}

				}

				for (EquippedItem equippedItem : toremove) {
					removeEquippedItem(equippedItem);
				}
			}
		}
	}

	public List<CombatTalent> getAvailableCombatTalents(Weapon weapon) {

		List<CombatTalent> combatTalents = new LinkedList<CombatTalent>();

		for (TalentType ctt : weapon.getTalentTypes()) {
			BaseCombatTalent combatTalent = getCombatTalent(ctt);
			if (combatTalent != null) {
				combatTalents.add(combatTalent);
			}
		}

		return combatTalents;

	}

	/**
	 * @param item
	 * @return <code>true</code> if item has been added successfully, otherwise <code>false</code>
	 */
	public boolean addItem(Item item) {

		ItemContainer itemContainer = getItemContainer(item.getContainerId());
		if (itemContainer == null) {
			itemContainer = getItemContainers().get(0);
			item.setContainerId(Hero.FIRST_INVENTORY_SCREEN);
		}

		// item already added, no need to add again
		if (itemContainer.contains(item))
			return false;

		itemContainer.add(item);
		fireItemAddedEvent(item);

		return true;
	}

	public List<ItemContainer> getItemContainers() {
		return getHeroConfiguration().getItemContainers();
	}

	public void addEquippedItem(final Context context, final Item item, ItemSpecification itemSpecification,
			final CombatTalent talent, final int set) {

		// if hero does not have item yet, add it first.
		addItem(item);

		if (itemSpecification == null && item.getSpecifications().size() > 0) {
			itemSpecification = item.getSpecifications().get(0);
		}

		EquippedItem equippedItem = null;
		if (itemSpecification != null) {
			equippedItem = new EquippedItem(this, talent, item, itemSpecification);
			equippedItem.setSet(set);

			if (equippedItem.getName() == null) {
				String namePrefix = null;

				if (equippedItem.getItemSpecification() instanceof Weapon) {
					namePrefix = EquippedItem.NAME_PREFIX_NK;
				}
				if (equippedItem.getItemSpecification() instanceof DistanceWeapon) {
					namePrefix = EquippedItem.NAME_PREFIX_FK;
				}
				if (equippedItem.getItemSpecification() instanceof Shield) {
					namePrefix = EquippedItem.NAME_PREFIX_SCHILD;
				}
				if (equippedItem.getItemSpecification() instanceof Armor) {
					namePrefix = EquippedItem.NAME_PREFIX_RUESTUNG;
				}

				// find first free slot
				int i = 1;
				while (getEquippedItem(set, namePrefix + i) != null) {
					i++;
				}
				equippedItem.setName(namePrefix + i);
			}
			addEquippedItem(equippedItem);

			if (equippedItem.getItemSpecification() instanceof Armor) {
				recalcArmorAttributes(set);
				if (set == activeSet) {
					resetBe();
				}
			}
			fireItemEquippedEvent(equippedItem);
		}
	}

	public HeroFileInfo getFileInfo() {
		return fileInfo;
	}

	public void setBeCalculation(boolean auto) {
		if (auto != isBeCalculation()) {
			getHeroConfiguration().setBeCalculation(auto);
			if (auto) {
				resetBe();
			}
		}
	}

	public boolean isBeCalculation() {
		return getHeroConfiguration().isBeCalculation();
	}

	public void addAttribute(Attribute attr) {
		super.addAttribute(attr);
		if (attr instanceof CustomAttribute) {
			getHeroConfiguration().addAttribute((CustomAttribute) attr);
		}
	}

	public Map<ArmorPosition, ArmorAttribute> getArmorAttributes() {
		return getArmorAttributes(activeSet);
	}

	@SuppressWarnings("unchecked")
	public Map<ArmorPosition, ArmorAttribute> getArmorAttributes(int set) {
		if (armorAttributes == null) {
			armorAttributes = new EnumMap[MAXIMUM_SET_NUMBER];
		}

		if (armorAttributes[set] == null) {

			final List<ArmorPosition> armorPositions = DsaTabApplication.getInstance().getConfiguration()
					.getArmorPositions();

			Map<ArmorPosition, ArmorAttribute> map = new EnumMap<ArmorPosition, ArmorAttribute>(ArmorPosition.class);

			if (getHeroConfiguration().getArmorAttributes(set) != null) {
				for (ArmorAttribute rs : getHeroConfiguration().getArmorAttributes(set)) {
					if (armorPositions.contains(rs.getPosition())) {
						map.put(rs.getPosition(), rs);
					}
				}
			}

			// fill not existing values with 0
			for (ArmorPosition pos : armorPositions) {
				ArmorAttribute rs = map.get(pos);

				if (rs == null) {
					rs = new ArmorAttribute(this, pos);
					rs.setValue(getArmorRs(pos));

					getHeroConfiguration().addArmorAttribute(set, rs);
					map.put(pos, rs);
				}
			}

			armorAttributes[set] = map;
		}

		return armorAttributes[set];
	}

	public Map<Position, WoundAttribute> getWounds() {
		return wounds;
	}

	public EditableValue getExperience() {
		return experience;
	}

	public int getLevel() {
		int level = getExperience().getValue() - getFreeExperience().getValue();

		level = level / 1000;

		return level;
	}

	public EditableValue getFreeExperience() {
		return freeExperience;
	}

	protected void postLeRatioCheck() {

		if (DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_LE_MODIFIER, true)) {
			float newLeRatioCheck = getRatio(AttributeType.Lebensenergie_Aktuell);

			int newLeRatioLevel = 0;

			if (newLeRatioCheck < LeModificator.LEVEL_3)
				newLeRatioLevel = 3;
			else if (newLeRatioCheck < LeModificator.LEVEL_2)
				newLeRatioLevel = 2;
			else if (newLeRatioCheck < LeModificator.LEVEL_1)
				newLeRatioLevel = 1;

			if (oldLeRatioLevel == null || oldLeRatioLevel != newLeRatioLevel)
				fireModifierChangedEvent(leModificator);

			oldLeRatioLevel = newLeRatioLevel;
		} else {
			oldLeRatioLevel = null;
			fireModifierRemovedEvent(leModificator);
		}

	}

	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (DsaTabPreferenceActivity.KEY_HOUSE_RULES_AU_MODIFIER.equals(key)) {
			postAuRatioCheck();
		}

		if (DsaTabPreferenceActivity.KEY_HOUSE_RULES_LE_MODIFIER.equals(key)) {
			postLeRatioCheck();
		}
	}

	protected void postAuRatioCheck() {
		if (DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_AU_MODIFIER, true)) {

			double newAuRatioCheck = getRatio(AttributeType.Ausdauer_Aktuell);

			int newAuRatioLevel = 0;
			if (newAuRatioCheck < AuModificator.LEVEL_2)
				newAuRatioLevel = 2;
			else if (newAuRatioCheck < AuModificator.LEVEL_1)
				newAuRatioLevel = 1;

			if (oldAuRatioLevel == null || oldAuRatioLevel != newAuRatioLevel)
				fireModifierChangedEvent(auModificator);

			oldAuRatioLevel = newAuRatioLevel;
		} else {
			oldAuRatioLevel = null;
			fireModifierRemovedEvent(auModificator);
		}
	}

	/**
	 * @return
	 */
	public float getRatio(AttributeType type) {
		Attribute attr = getAttribute(type);
		if (attr != null) {
			return attr.getRatio();
		} else {
			return 1.0f;
		}
	}

	public int getModifier(Probe probe, boolean includeBe, boolean includeLeAu) {
		int result = 0;
		if (probe.getModifierCache() == Integer.MIN_VALUE) {
			Integer[] outMod = new Integer[1];
			populateModifiers(probe, null, outMod);
			result = outMod[0];

			probe.setModifierCache(result);
		} else {
			result = probe.getModifierCache();
		}

		if (includeBe) {
			result -= getBe(probe);
		}
		if (includeLeAu) {

			if (leModificator.isActive() && leModificator.fulfills() && leModificator.affects(probe)) {
				result += leModificator.getModifierValue(probe);
			}
			if (auModificator.isActive() && auModificator.fulfills() && auModificator.affects(probe)) {
				result += auModificator.getModifierValue(probe);
			}

		}
		return result;
	}

	private void populateModifiers(Probe probe, List<Modifier> outModifiers, Integer[] outValue) {
		if (outValue != null)
			outValue[0] = 0;

		for (Modificator modificator : getModificators(probe.getModificatorType())) {
			if (modificator == leModificator || modificator == auModificator)
				continue;

			if (modificator.isActive() && modificator.fulfills() && modificator.affects(probe)) {
				if (outValue != null && outModifiers == null) {
					outValue[0] += modificator.getModifierValue(probe);
				} else {
					Modifier mod = modificator.getModifier(probe);
					if (mod != null && mod.getModifier() != 0) {
						if (outValue != null)
							outValue[0] += mod.getModifier();
						if (outModifiers != null)
							outModifiers.add(mod);
					}
				}
			}
		}

		for (Modificator modificator : getModificators(ModificatorType.ALL)) {
			if (modificator == leModificator || modificator == auModificator)
				continue;

			if (modificator.isActive() && modificator.fulfills() && modificator.affects(probe)) {
				if (outValue != null && outModifiers == null) {
					outValue[0] += modificator.getModifierValue(probe);
				} else {
					Modifier mod = modificator.getModifier(probe);
					if (mod != null && mod.getModifier() != 0) {
						if (outValue != null)
							outValue[0] += mod.getModifier();
						if (outModifiers != null)
							outModifiers.add(mod);
					}
				}
			}
		}

		if (probe instanceof CombatProbe) {
			CombatProbe combatProbe = (CombatProbe) probe;
			EquippedItem equippedItem = combatProbe.getEquippedItem();

			if (equippedItem.getItemSpecification() instanceof Weapon) {
				Weapon weapon = (Weapon) equippedItem.getItemSpecification();

				int kkModifier = weapon.getKKModifier(getModifiedValue(AttributeType.Körperkraft, true, true));

				if (kkModifier < 0) {
					if (outValue != null)
						outValue[0] += kkModifier;
					if (outModifiers != null)
						outModifiers.add(new Modifier(kkModifier, DsaTabApplication.getInstance().getString(
								R.string.modifier_waffe_geringe_kk), ""));
				}

				if (combatProbe.isAttack()) {
					if (weapon.getWmAt() != null && weapon.getWmAt() != 0)
						if (outValue != null)
							outValue[0] += weapon.getWmAt();
					if (outModifiers != null)
						outModifiers.add(new Modifier(weapon.getWmAt(), DsaTabApplication.getInstance().getString(
								R.string.modifier_waffe_waffenmodifikator_at)));
				} else {
					if (weapon.getWmPa() != null && weapon.getWmPa() != 0)
						if (outValue != null)
							outValue[0] += weapon.getWmPa();
					if (outModifiers != null)
						outModifiers.add(new Modifier(weapon.getWmPa(), DsaTabApplication.getInstance().getString(
								R.string.modifier_waffe_waffenmodifikator_pa)));
				}

				addModForBeidhändigerKampf(outModifiers, outValue, equippedItem);

				// modify weapon attack with shield wmAt modifier if second
				// weapon is shield
				if (combatProbe.isAttack()) {
					EquippedItem equippedShield = equippedItem.getSecondaryItem();
					if (equippedShield != null && equippedShield.getItemSpecification() instanceof Shield) {
						Shield shield = (Shield) equippedShield.getItemSpecification();
						if (outValue != null)
							outValue[0] += shield.getWmAt();
						if (outModifiers != null)
							outModifiers.add(new Modifier(shield.getWmAt(), DsaTabApplication.getInstance().getString(
									R.string.modifier_schildkampf_modifikator_at)));
					}
				}

			} else if (equippedItem.getItemSpecification() instanceof Shield) {
				Shield shield = (Shield) equippedItem.getItemSpecification();

				if (combatProbe.isAttack()) {
					if (outValue != null)
						outValue[0] += shield.getWmAt();
					if (outModifiers != null)
						outModifiers.add(new Modifier(shield.getWmAt(), DsaTabApplication.getInstance().getString(
								R.string.modifier_schildmodifikator_at)));
				} else {
					if (outValue != null)
						outValue[0] += shield.getWmPa();
					if (outModifiers != null)
						outModifiers.add(new Modifier(shield.getWmPa(), DsaTabApplication.getInstance().getString(
								R.string.modifier_schildmodifikator_pa)));

					// paradevalue is increased by 1 if weaponparade is above 15
					if (equippedItem.getUsageType() != null) {
						switch (equippedItem.getUsageType()) {
						case Schild: {

							if (hasFeature(FeatureType.Linkhand)) {
								if (outValue != null)
									outValue[0] += 1;
								if (outModifiers != null)
									outModifiers.add(new Modifier(1, FeatureType.Linkhand.xmlName()));
							}
							if (hasFeature(FeatureType.SchildkampfI)) {
								if (outValue != null)
									outValue[0] += 2;
								if (outModifiers != null)
									outModifiers.add(new Modifier(2, FeatureType.SchildkampfI.xmlName()));
							}
							if (hasFeature(FeatureType.SchildkampfII)) {
								if (outValue != null)
									outValue[0] += 2;
								if (outModifiers != null)
									outModifiers.add(new Modifier(2, FeatureType.SchildkampfII.xmlName()));
							}
							EquippedItem equippedPrimaryWeapon = equippedItem.getSecondaryItem();

							if (equippedPrimaryWeapon != null) {
								int defenseValue = 0;
								if (equippedPrimaryWeapon.getTalent().getDefense() != null) {
									defenseValue = equippedPrimaryWeapon.getTalent().getDefense().getProbeValue(0);
								}

								if (defenseValue >= 21) {
									if (outValue != null)
										outValue[0] += 3;
									if (outModifiers != null)
										outModifiers.add(new Modifier(3, DsaTabApplication.getInstance().getString(
												R.string.modifier_shield_hauptwaffe_paradewert, defenseValue, "+3")));
								} else if (defenseValue >= 18) {
									if (outValue != null)
										outValue[0] += 2;
									if (outModifiers != null)
										outModifiers.add(new Modifier(2, DsaTabApplication.getInstance().getString(
												R.string.modifier_shield_hauptwaffe_paradewert, defenseValue, "+2")));
								} else if (defenseValue >= 15) {
									if (outValue != null)
										outValue[0] += 1;
									if (outModifiers != null)
										outModifiers.add(new Modifier(1, DsaTabApplication.getInstance().getString(
												R.string.modifier_shield_hauptwaffe_paradewert, defenseValue, "+1")));
								}
							}
							break;
						}
						case Paradewaffe:
							boolean primaryWeapon = false;
							if (equippedItem.getItem() != null && equippedItem.getSecondaryItem() != null) {
								EquippedItem equippedPrimaryWeapon = equippedItem.getSecondaryItem();
								if (equippedPrimaryWeapon.getTalent() instanceof CombatMeleeTalent
										&& equippedPrimaryWeapon.getTalent().getDefense() != null) {
									primaryWeapon = true;
								}
							}

							if (primaryWeapon && hasFeature(FeatureType.ParierwaffenI)) {
								if (outValue != null)
									outValue[0] += 2;
								if (outModifiers != null)
									outModifiers.add(new Modifier(2, FeatureType.ParierwaffenII.xmlName()));
							} else if (primaryWeapon && hasFeature(FeatureType.ParierwaffenI)) {
								if (outValue != null)
									outValue[0] += -1;
								if (outModifiers != null)
									outModifiers.add(new Modifier(-1, FeatureType.ParierwaffenI.xmlName()));
							} else if (hasFeature(FeatureType.Linkhand)) {
								if (outValue != null)
									outValue[0] += 1;
								if (outModifiers != null)
									outModifiers.add(new Modifier(1, FeatureType.Linkhand.xmlName()));
							}

							break;
						}
					}
				}
			}
		}
	}

	public List<Modifier> getModifiers(Probe probe, boolean includeBe, boolean includeLeAu) {
		List<Modifier> modifiers = new LinkedList<Modifier>();
		populateModifiers(probe, modifiers, null);

		if (includeBe) {
			int heroBe = getBe(probe);
			if (heroBe != 0) {
				modifiers.add(new Modifier(-heroBe, DsaTabApplication.getInstance().getString(
						R.string.modifier_behinderung, probe.getProbeInfo().getBe())));
			}
		}

		if (includeLeAu) {
			if (leModificator.isActive() && leModificator.fulfills() && leModificator.affects(probe)) {
				Modifier mod = leModificator.getModifier(probe);
				if (mod != null && mod.getModifier() != 0) {
					modifiers.add(mod);
				}
			}
			if (auModificator.isActive() && auModificator.fulfills() && auModificator.affects(probe)) {
				Modifier mod = auModificator.getModifier(probe);
				if (mod != null && mod.getModifier() != 0) {
					modifiers.add(mod);
				}
			}
		}
		return modifiers;
	}

	/**
	 * Adds modifiers for lefthanded battle.
	 * 
	 * @param outModifiers
	 * @param equippedItem
	 */
	private void addModForBeidhändigerKampf(List<Modifier> outModifiers, Integer[] outValue, EquippedItem equippedItem) {

		EquippedItem equippedSecondaryWeapon = equippedItem.getSecondaryItem();
		if (equippedItem.isBeidhändigerKampf() && equippedSecondaryWeapon != null
				&& equippedSecondaryWeapon.getItemSpecification() instanceof Weapon
				&& equippedItem.getItemSpecification() instanceof Weapon) {
			Item secondItem = equippedSecondaryWeapon.getItem();
			Item primaryItem = equippedItem.getItem();
			// WdS 72, beim beidhändigen kampf mit gleichen waffen 0/0, beim
			// kampf mit waffen des selben talent -1/-1, beim kampf mit
			// unterschiedlichen talenten -2/-2

			if (secondItem.getName().equals(primaryItem.getName())) {
				// 0/0 no modifier
			} else if (equippedItem.getTalent().equals(equippedSecondaryWeapon.getTalent())) {
				if (outValue != null)
					outValue[0] += -1;
				if (outModifiers != null) {
					outModifiers
							.add(new Modifier(-1, "BK - gleiches Talent",
									"Beim Beidhändigenkampf mit 2 Waffen des selben Talentes bekommt man einen Abzug von -1/-1."));
				}
			} else {
				if (outValue != null)
					outValue[0] += -2;
				if (outModifiers != null) {
					outModifiers
							.add(new Modifier(-2, "BK - unterschiedliches Talent",
									"Beim Beidhändigenkampf mit 2 Waffen unterschiedlichen Talentes bekommt man einen Abzug von -2/-2."));
				}
			}
		}

		// check for beidhändiger kampf
		if (equippedItem.getHand() == Hand.links) {
			int m = 0;
			if (!hasFeature(FeatureType.Beidhändig)) {
				m = -9;
				if (hasFeature(FeatureType.Linkhand))
					m += 3;
				if (hasFeature(FeatureType.BeidhändigerKampfI))
					m += 3;
				if (hasFeature(FeatureType.BeidhändigerKampfII))
					m += 3;
			}

			if (outValue != null)
				outValue[0] += m;
			if (outModifiers != null) {
				outModifiers.add(new Modifier(m, "Falsche Hand ",
						"Beim Kampf bekommt man je nach Sonderfertigkeiten bei Aktionen mit der linken Hand Abzüge."));
			}
		}
	}

	public Integer getModifiedValue(AttributeType type, boolean includeBe, boolean includeLeAu) {
		Attribute attr = getAttribute(type);
		if (attr == null || attr.getValue() == null)
			return null;

		return attr.getValue() + getModifier(attr, includeBe, includeLeAu);
	}

	public void addConnection(Connection connection) {
		getConnections().add(connection);
	}

	public void removeConnection(Connection connection) {
		getConnections().remove(connection);
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void addChangeEvent(ChangeEvent event) {
		changeEvents.add(event);
	}

	public void addEvent(Event event) {
		getHeroConfiguration().addEvent(event);
	}

	public List<Event> getEvents() {
		return getHeroConfiguration().getEvents();
	}

	/**
	 * @param probe
	 * @return
	 */
	public int getBe(Probe probe) {
		int heroBe = 0;

		if (probe != null && probe.getProbeInfo().getBe() != null) {

			// base hero be
			heroBe = getAttributeValue(AttributeType.Behinderung);

			heroBe = Math.abs(probe.getProbeInfo().getBe(0, heroBe));

			boolean isAttack = false;
			boolean isDefense = false;
			boolean halfBe = false;
			if (probe instanceof Attribute) {
				Attribute attribute = (Attribute) probe;
				if (attribute.getType() == AttributeType.at) {
					halfBe = true;
					isAttack = true;
				} else if (attribute.getType() == AttributeType.pa) {
					halfBe = true;
					isDefense = true;
				}

				if (attribute.getType() == AttributeType.Geschwindigkeit) {
					// WdH274
					if (hasFeature(FeatureType.Zwergenwuchs)) {
						halfBe = true;
					}
				}
			}

			if (probe instanceof CombatMeleeAttribute) {
				CombatMeleeAttribute meleeAttr = (CombatMeleeAttribute) probe;
				isAttack = meleeAttr.isAttack();
				isDefense = !meleeAttr.isAttack();
				halfBe = true;
			} else if (probe instanceof CombatProbe) {
				CombatProbe combatProbe = (CombatProbe) probe;
				isAttack = combatProbe.isAttack();
				isDefense = !combatProbe.isAttack();

				if (combatProbe.getCombatTalent() instanceof CombatDistanceTalent)
					halfBe = false;
				else
					halfBe = true;
			}

			if (halfBe) {
				if (getCombatStyle() == CombatStyle.Offensive && isAttack) {
					heroBe = (int) Math.floor(heroBe / 2.0);
				} else if (getCombatStyle() == CombatStyle.Defensive && isDefense) {
					heroBe = (int) Math.floor(heroBe / 2.0);
				} else {
					heroBe = (int) Math.ceil(heroBe / 2.0);
				}
			}

		}
		return heroBe;
	}

	public int[] getWundschwelle() {
		int[] ws = new int[3];

		SharedPreferences preferences = DsaTabApplication.getPreferences();
		int wsBase = 0;
		int wsMod = 0;
		if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_EASIER_WOUNDS, false)) {
			wsBase = (int) Math.ceil(getAttributeValue(AttributeType.Konstitution) / 3.0f);
			wsMod = getModifier(AttributeType.Wundschwelle);

			ws[0] = wsBase + wsMod;
			ws[1] = (wsBase * 2) + wsMod;
			ws[2] = getAttributeValue(AttributeType.Konstitution) + wsMod;

		} else {
			wsBase = (int) Math.ceil(getAttributeValue(AttributeType.Konstitution) / 2.0f);
			wsMod = getModifier(AttributeType.Wundschwelle);

			ws[0] = wsBase + wsMod;
			ws[1] = getAttributeValue(AttributeType.Konstitution) + wsMod;
			ws[2] = getAttributeValue(AttributeType.Konstitution) + wsBase + wsMod;
		}

		return ws;

	}

	/**
	 * @param wundschwelle
	 * @return
	 */
	private int getModifier(AttributeType type) {
		Integer[] outValue = new Integer[1];
		outValue[0] = 0;
		List<Modifier> outModifiers = null;

		for (Modificator modificator : getModificators()) {
			if (modificator.isActive() && modificator.fulfills() && modificator.affects(type)) {
				Modifier mod = modificator.getModifier(type);
				if (mod != null && mod.getModifier() != 0) {
					if (outValue != null)
						outValue[0] += mod.getModifier();
					if (outModifiers != null)
						outModifiers.add(mod);
				}
			}
		}
		return outValue[0];
	}

	public int getArmorBe() {

		if (beCache == null) {

			float be = 0.0f;

			String rs1Armor = null;
			if (hasFeature(FeatureType.RüstungsgewöhnungIII)) {
				be -= 2.0;
			} else if (hasFeature(FeatureType.RüstungsgewöhnungII)) {
				be -= 1.0;
			} else {
				Feature rs1 = getFeature(FeatureType.RüstungsgewöhnungI);
				if (rs1 != null) {
					rs1Armor = rs1.getValue();
				}
			}

			switch (DsaTabApplication.getInstance().getConfiguration().getArmorType()) {

			case ZonenRuestung: {

				int stars = 0;
				float totalRs = 0;
				float itemRs = 0;
				for (EquippedItem equippedItem : getEquippedItems()) {

					if (equippedItem.getItemSpecification() instanceof Armor) {
						itemRs = 0;
						Armor armor = (Armor) equippedItem.getItemSpecification();

						if (rs1Armor != null && rs1Armor.equals(equippedItem.getItem().getName())) {
							be -= 1.0;
							rs1Armor = null;
						}

						for (ArmorPosition pos : ArmorPosition.values()) {
							float armorRs = armor.getRs(pos);

							if (armor.isZonenHalfBe())
								armorRs = armorRs / 2.0f;

							itemRs += (armorRs * pos.getMultiplier());
						}

						if (itemRs >= 20) {
							stars += armor.getStars();
						}
						totalRs += itemRs;
					}
				}

				totalRs = (float) Math.ceil(totalRs / 20);
				be += (totalRs - stars);
				break;
			}

			case GesamtRuestung: {

				for (EquippedItem equippedItem : getEquippedItems()) {
					ItemSpecification itemSpec = equippedItem.getItemSpecification();
					if (itemSpec instanceof Armor) {
						Armor armor = (Armor) itemSpec;
						if (armor.getTotalPieces() > 1) {
							be += (armor.getTotalBe() / armor.getTotalPieces());
						} else {
							be += armor.getTotalBe();
						}

						if (rs1Armor != null && rs1Armor.equals(equippedItem.getItem().getName())) {
							be -= 1.0;
							rs1Armor = null;
						}
					}
				}
				// be in gesamtrüstung is being rounded
				be = Math.round(be);
				break;

			}
			}
			beCache = Math.max(0, (int) Math.ceil(be));
		}
		return beCache;

	}

	/**
	 * A general overall Rs value calculated using the zone system sum with multipliers
	 * 
	 * @return
	 */
	public int getArmorRs() {

		float totalRs = 0;

		switch (DsaTabApplication.getInstance().getConfiguration().getArmorType()) {

		case ZonenRuestung:
			for (ArmorPosition pos : ArmorPosition.values()) {
				totalRs += (getArmorRs(pos) * pos.getMultiplier());
			}
			totalRs = (int) Math.round(totalRs / 20.0);
			break;
		case GesamtRuestung:
			for (EquippedItem equippedItem : getEquippedItems()) {
				ItemSpecification itemSpec = equippedItem.getItemSpecification();
				if (itemSpec instanceof Armor) {
					Armor armor = (Armor) itemSpec;
					if (armor.getTotalPieces() > 1)
						totalRs += (((float) armor.getTotalRs()) / armor.getTotalPieces());
					else
						totalRs += armor.getTotalRs();
				}
			}

			Feature natRs = getFeature(FeatureType.NatürlicherRüstungsschutz);
			if (natRs != null && natRs.getValue() != null) {
				totalRs += Util.parseInt(natRs.getValue());
			}

			break;
		}
		return (int) Math.ceil(totalRs);
	}

	public List<EquippedItem> getArmor(ArmorPosition pos) {
		List<EquippedItem> items = new LinkedList<EquippedItem>();

		for (EquippedItem equippedItem : getEquippedItems()) {
			Item item = equippedItem.getItem();
			Armor armor = item.getSpecification(Armor.class);
			if (armor != null && armor.getRs(pos) > 0) {
				items.add(equippedItem);
			}
		}

		return items;
	}

	public int getArmorRs(ArmorPosition pos) {

		int rs = 0;
		for (EquippedItem equippedItem : getEquippedItems()) {
			Item item = equippedItem.getItem();
			Armor armor = item.getSpecification(Armor.class);
			if (armor != null) {
				rs += armor.getRs(pos);
			}
		}

		Feature natRs = getFeature(FeatureType.NatürlicherRüstungsschutz);
		if (natRs != null && natRs.getValue() != null)
			rs += Util.parseInt(natRs.getValue());

		return rs;
	}

	public List<Item> getItems(int screen) {
		ItemContainer itemContainer = getItemContainer(screen);
		if (itemContainer != null)
			return itemContainer.getItems();
		else
			return Collections.emptyList();
	}

	public Spell getSpell(String spellName) {
		Debug.trace("getSpell " + spellName);
		return spellsByName.get(spellName);
	}

	public void addSpell(Spell spell) {
		spellsByName.put(spell.getName(), spell);
	}

	public CombatStyle getCombatStyle() {
		return getHeroConfiguration().getCombatStyle();
	}

	public void setCombatStyle(CombatStyle style) {
		getHeroConfiguration().setCombatStyle(style);
	}

	public Map<String, Spell> getSpells() {
		return spellsByName;
	}

	public Item getItem(String name, String slot) {
		Debug.trace("getItem " + name + ", slot=" + slot);

		for (ItemContainer itemContainer : getItemContainers()) {
			for (Item item : itemContainer.getItems()) {
				if (item.getName().equals(name)) {
					if (slot != null) {
						if (slot.equals(item.getSlot()))
							return item;
					} else {
						return item;
					}
				}
			}
		}
		return null;
	}

	public Item getItem(UUID id) {
		for (ItemContainer itemContainer : getItemContainers()) {
			for (Item item : itemContainer.getItems()) {
				if (item.getId().equals(id)) {
					return item;
				}
			}
		}
		return null;
	}

	public BaseCombatTalent getCombatTalent(String talentName) {
		Debug.trace("getCombatTalent " + talentName);
		TalentType type = TalentType.byXmlName(talentName);
		return getCombatTalent(type);
	}

	public BaseCombatTalent getCombatTalent(TalentType talentType) {
		Talent talent = getTalent(talentType);
		if (talent == null) {
			// add missing combat talents with a value of base.
			if (talentType != null) {
				if (talentType.type() == TalentGroupType.Fernkampf) {
					CombatDistanceTalent distanceTalent = new CombatDistanceTalent(this);
					distanceTalent.setValue(-4);
					distanceTalent.setType(talentType);
					talent = distanceTalent;
				} else {
					CombatMeleeAttribute at = new CombatMeleeAttribute(this, CombatMeleeAttribute.ATTACKE);
					at.setValue(getAttributeValue(AttributeType.at));

					CombatMeleeAttribute pa = new CombatMeleeAttribute(this, CombatMeleeAttribute.PARADE);
					pa.setValue(getAttributeValue(AttributeType.pa));

					talent = new CombatMeleeTalent(this, talentType, at, pa);
					talent.setValue(0);

					if (talent.getProbeInfo().getBe() != null) {
						talent.setProbeBe(talent.getProbeInfo().getBe());
					}
				}
			}
		}

		if (talent instanceof BaseCombatTalent)
			return (BaseCombatTalent) talent;
		else
			return null;
	}

	public void removeEvent(Event event) {
		if (event.getAudioPath() != null) {

			File audioFile = new File(event.getAudioPath());
			if (audioFile.exists())
				deletableAudioFiles.add(audioFile);
		}

		getHeroConfiguration().removeEvent(event);
	}

	public void removeEquippedItem(EquippedItem equippedItem) {

		if (equippedItem.getSecondaryItem() != null) {
			equippedItem.getSecondaryItem().setSecondaryItem(null);
		}

		if (getHuntingWeapon() == equippedItem) {
			setHuntingWeapon(null);
		}

		int set = equippedItem.getSet();
		getEquippedItems(set).remove(equippedItem);

		if (equippedItem.getItem().hasSpecification(Armor.class)) {
			recalcArmorAttributes(set);
			if (set == activeSet) {
				resetBe();
			}
		}

		fireItemUnequippedEvent(equippedItem);
	}

	/**
	 * 
	 */
	private void resetBe() {

		if (isBeCalculation()) {
			int oldBe = getAttributeValue(AttributeType.Behinderung);

			beCache = null;
			if (oldBe != getArmorBe()) {
				getAttribute(AttributeType.Behinderung).setValue(getArmorBe());
			}
		} else {
			beCache = null;
		}
	}

	public void addModificator(Modificator modificator) {
		if (modificator != null) {
			if (modificatorsByType != null) {
				for (ModificatorType modType : modificator.getAffectedModifierTypes()) {
					Set<Modificator> modificators = modificatorsByType.get(modType);

					if (modificators == null) {
						modificators = new HashSet<Modificator>();
						modificatorsByType.put(modType, modificators);
					}
					modificators.add(modificator);
				}
			}

			if (modificator instanceof CustomModificator) {
				getHeroConfiguration().addModificator((CustomModificator) modificator);
			}
			if (getModificators().add(modificator)) {
				fireModifierAddedEvent(modificator);
			}
		}
	}

	public void removeModificator(Modificator modificator) {
		if (modificator != null) {
			if (modificatorsByType != null) {
				for (ModificatorType modType : modificator.getAffectedModifierTypes()) {
					Set<Modificator> modificators = modificatorsByType.get(modType);

					if (modificators != null) {
						modificators.add(modificator);
					}
				}
			}

			if (modificator instanceof CustomModificator) {
				getHeroConfiguration().removeModificator((CustomModificator) modificator);
			}
			if (getModificators().remove(modificator)) {
				fireModifierRemovedEvent(modificator);
			}
		}
	}

	public Set<Modificator> getModificators(ModificatorType type) {
		if (modificatorsByType == null) {
			modificatorsByType = new EnumMap<ModificatorType, Set<Modificator>>(ModificatorType.class);
			for (Modificator modificator : getModificators()) {
				for (ModificatorType modType : modificator.getAffectedModifierTypes()) {
					Set<Modificator> modificators = modificatorsByType.get(modType);

					if (modificators == null) {
						modificators = new HashSet<Modificator>();
						modificatorsByType.put(modType, modificators);
					}
					modificators.add(modificator);
				}
			}
		}

		Set<Modificator> result = modificatorsByType.get(type);
		if (result == null)
			return Collections.emptySet();
		else
			return result;
	}

	public Modificator getUserModificators(String name) {
		for (Modificator mod : getModificators()) {
			if (!(mod instanceof RulesModificator) && mod.getModificatorName().equals(name)) {
				return mod;
			}
		}
		return null;
	}

	public List<Modificator> getUserModificators() {
		Set<Modificator> modificators = new HashSet<Modificator>();

		for (Modificator mod : getModificators()) {
			if (mod instanceof RulesModificator)
				continue;

			modificators.add(mod);
		}

		List<Modificator> mods = new ArrayList<Modificator>(modificators);
		Collections.sort(mods, AbstractModificator.NAME_COMPARATOR);
		return mods;
	}

	public Set<Modificator> getModificators() {

		// init modifiers
		if (modificators == null) {
			modificators = new HashSet<Modificator>();

			if (leModificator.fulfills())
				modificators.add(leModificator);

			if (auModificator.fulfills())
				modificators.add(auModificator);

			for (WoundAttribute attr : getWounds().values()) {
				if (attr.getModificator().fulfills()) {
					modificators.add(attr.getModificator());
				}
			}

			// add custom modificators
			modificators.addAll(getHeroConfiguration().getModificators());
		}
		return modificators;

	}

	public void reloadArmorAttributes() {
		armorAttributes = null;
	}

	protected void recalcArmorAttributes(int set) {
		for (ArmorAttribute a : getArmorAttributes(set).values()) {
			a.recalcValue();
		}
	}

	/**
	 * @param weapon
	 * @return
	 */
	public int getModifierTP(EquippedItem weapon) {
		int modifierTP = 0;
		for (CustomModificator modificator : getHeroConfiguration().getModificators()) {
			if (modificator.fulfills() && modificator.isActive()) {
				modifierTP += modificator.getModifierValue(weapon);
			}
		}
		return modifierTP;
	}

	public void addTalent(Talent talent, boolean visible) {
		super.addTalent(talent, visible);

		if (talent instanceof MetaTalent) {
			getHeroConfiguration().addMetaTalent((MetaTalent) talent);
		}
	}

	public void addAnimal(Animal animal) {
		this.animals.add(animal);
	}

	public List<Animal> getAnimals() {
		return animals;
	}

	public Animal getAnimal(String name, String slot) {
		Debug.trace("getAnimal " + name + ", slot=" + slot);

		for (Animal animal : getAnimals()) {
			if (animal.getName().equals(name)) {
				if (slot != null) {
					if (slot.equals(animal.getSlot()))
						return animal;
				} else {
					return animal;
				}
			}

		}
		return null;
	}

	public void addEquippedItem(EquippedItem equippedItem, boolean sort) {
		equippedItems[equippedItem.getSet()].add(equippedItem);
		if (sort)
			Util.sort(equippedItems[equippedItem.getSet()]);
	}

	/**
	 * @param equippedItem
	 */
	public void addEquippedItem(EquippedItem equippedItem) {
		addEquippedItem(equippedItem, true);
	}

	/**
	 * @param huntingWeapon
	 */
	public void addHuntingWeapon(HuntingWeapon huntingWeapon) {
		if (huntingWeapon.getSet() < 0) {
			// no hunting weapon found ignore it
		} else {
			huntingWeapons[huntingWeapon.getSet()] = huntingWeapon;
		}
	}

	/**
	 * @param i
	 * @return
	 */
	public HuntingWeapon getHuntingWeapons(int i) {
		return huntingWeapons[i];
	}

	/**
	 * @return
	 */
	public List<ChangeEvent> getChangeEvents() {
		return changeEvents;
	}

	/**
	 * @param containerId
	 * @return
	 */
	public ItemContainer getItemContainer(int containerId) {
		for (ItemContainer container : getItemContainers()) {
			if (container.getId() == containerId)
				return container;
		}
		return null;
	}

	/**
	 * @param itemContainer
	 */
	public void addItemContainer(ItemContainer itemContainer) {
		if (itemContainer.getId() == ItemContainer.INVALID_ID) {
			int maxId = MAXIMUM_SET_NUMBER;
			for (ItemContainer container : getItemContainers()) {
				maxId = Math.max(maxId, container.getId());
			}
			itemContainer.setId(maxId + 1);
		}
		getItemContainers().add(itemContainer);

		fireItemContainerAddedEvent(itemContainer);
	}

	/**
	 * @param itemContainer
	 */
	public void removeItemContainer(ItemContainer itemContainer) {
		getItemContainers().get(0).addAll(itemContainer.getItems());
		getItemContainers().remove(itemContainer);

		fireItemContainerRemovedEvent(itemContainer);
	}

	public List<Talent> getTalents() {
		List<Talent> talents = new ArrayList<Talent>();

		for (TalentGroupType type : TalentGroupType.values()) {
			if (getTalentGroup(type) != null) {
				talents.addAll(getTalentGroup(type).getTalents());
			}
		}

		return talents;
	}

}
