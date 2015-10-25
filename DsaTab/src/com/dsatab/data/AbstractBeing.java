package com.dsatab.data;

import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;

import com.dsatab.data.Hero.CombatStyle;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.data.modifier.Modifier;
import com.dsatab.util.Debug;
import com.dsatab.view.listener.HeroChangedListener;

import java.net.URI;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public abstract class AbstractBeing {
	private String name;

	private static final String KEY_PORTRAIT_URI = "portraitUri";

	private Uri portraitUri;

	private Map<AttributeType, Attribute> attributes;
	private Map<FeatureType, Feature> featuresByType;
	private Map<String, Art> artsByName;

	private Map<TalentGroupType, TalentGroup> talentGroups;
	private Map<TalentType, Talent> talentByType;

	private Set<HeroChangedListener> listener = new HashSet<HeroChangedListener>();

	protected HeroConfiguration configuration;

	public AbstractBeing() {
		this.featuresByType = new EnumMap<FeatureType, Feature>(FeatureType.class);
		this.attributes = new EnumMap<AttributeType, Attribute>(AttributeType.class);

		this.artsByName = new TreeMap<String, Art>();

		this.talentGroups = new EnumMap<TalentGroupType, TalentGroup>(TalentGroupType.class);
		this.talentByType = new EnumMap<TalentType, Talent>(TalentType.class);
	}

	protected abstract String getId();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public HeroConfiguration getHeroConfiguration() {
		return configuration;
	}

	public void setPortraitUri(Uri uri) {
		this.portraitUri = uri;
		if (uri != null)
			getHeroConfiguration().setProperty(getId() + KEY_PORTRAIT_URI, uri.toString());
		else
			getHeroConfiguration().setProperty(getId() + KEY_PORTRAIT_URI, null);


		for (HeroChangedListener l : listener) {
			l.onPortraitChanged();
		}
	}

	public void setPortraitUri(URI uri) {
		setPortraitUri(Uri.parse(uri.toString()));
	}

	public Uri getPortraitUri() {
		if (portraitUri == null) {
			if (!TextUtils.isEmpty(getHeroConfiguration().getProperty(getId() + KEY_PORTRAIT_URI))) {

				portraitUri = Uri.parse(getHeroConfiguration().getProperty(getId() + KEY_PORTRAIT_URI));
			}
		}
		return portraitUri;
	}

	public void fireValueChangedEvent(Value value) {
		for (HeroChangedListener l : listener) {
			l.onValueChanged(value);
		}
	}

	public void fireModifierChangedEvent(Modificator modifier) {

		Debug.trace("ON Modifier changed " + modifier);
		clearModifiersCache();
		for (HeroChangedListener l : listener) {
			l.onModifierChanged(modifier);
		}
	}

	/**
	 * 
	 */
	protected void clearModifiersCache() {
		BaseProbe.cacheValidationDate = SystemClock.uptimeMillis();
	}

	void fireModifierAddedEvent(Modificator modifier) {
		Debug.trace("ON modifier added " + modifier);
		clearModifiersCache();

		for (HeroChangedListener l : listener) {
			l.onModifierAdded(modifier);
		}

	}

	void fireModifierRemovedEvent(Modificator modifier) {
		Debug.trace("ON modifier removed " + modifier);
		clearModifiersCache();

		for (HeroChangedListener l : listener) {
			l.onModifierRemoved(modifier);
		}
	}

	public void addHeroChangedListener(HeroChangedListener v) {
		listener.add(v);
	}

	public void removeHeroChangedListener(HeroChangedListener v) {
		listener.remove(v);
	}

	public void addAttribute(Attribute attr) {
		this.attributes.put(attr.getType(), attr);
	}

	public Attribute getAttribute(AttributeType type) {
		Attribute attribute = attributes.get(type);
		return attribute;
	}

	public Map<AttributeType, Attribute> getAttributes() {
		return attributes;
	}

	public Integer getAttributeValue(AttributeType type) {
		Attribute attribute = getAttribute(type);

		if (attribute != null)
			return attribute.getValue();
		else
			return null;
	}

	public boolean hasAttribute(AttributeType type) {
		return attributes.containsKey(type);
	}

	public Map<FeatureType, Feature> getSpecialFeatures() {
		return featuresByType;
	}

	/**
	 * Used in rules.xml by code
	 * 
	 * @param type
	 * @return
	 */
	public Feature getFeature(String type) {
		Debug.trace("getFeature " + type);
		return getFeature(FeatureType.byXmlName(type));
	}

	public Feature getFeature(FeatureType type) {
		Debug.trace("getFeature " + type);
		return featuresByType.get(type);
	}

	public boolean hasFeature(FeatureType type) {
		Debug.trace("hasFeature " + type);
		return featuresByType.containsKey(type);
	}

	/**
	 * @param adv
	 */
	public void addFeature(Feature adv) {
		if (adv != null) {
			Feature existingAdv = featuresByType.get(adv.getType());
			if (existingAdv == null) {
				featuresByType.put(adv.getType(), adv);
			} else {
				existingAdv.addAllValues(adv.getValues());
			}
		}
	}

	public void removeFeature(Feature adv) {
		if (adv != null) {
			featuresByType.remove(adv.getType());
		}
	}

	public Art getArt(String name) {
		Debug.trace("getArt " + name);
		return artsByName.get(name);
	}

	public Map<String, Art> getArts() {
		return artsByName;
	}

	/**
	 * @param art
	 */
	public void addArt(Art art) {
		artsByName.put(art.getName(), art);
	}

	public Talent getTalent(TalentType talentName) {
		return talentByType.get(talentName);
	}

	public Talent getTalent(String talentName) {
		Debug.trace("getTalent " + talentName);
		TalentType type = TalentType.byXmlName(talentName);
		return talentByType.get(type);

	}

	public Map<TalentGroupType, TalentGroup> getTalentGroups() {
		return talentGroups;
	}

	public TalentGroup getTalentGroup(TalentGroupType groupType) {
		return talentGroups.get(groupType);
	}

	/**
	 * @param talent
	 */
	public void addTalent(Talent talent) {
		addTalent(talent, true);
	}

	public void addTalent(Talent talent, boolean visible) {

		if (visible) {
			TalentGroup tg = talentGroups.get(talent.type.type());
			if (tg != null) {
				tg.getTalents().add(talent);
			} else {
				tg = new TalentGroup(talent.type.type());
				tg.getTalents().add(talent);
				talentGroups.put(talent.type.type(), tg);
			}
		}

		talentByType.put(talent.getType(), talent);

	}

	public abstract Integer getModifiedValue(AttributeType type, boolean includeBe, boolean includeLeAu);

	public abstract int getModifier(Probe probe, boolean includeBe, boolean includeLeAu);

	public abstract List<Modifier> getModifiers(Probe probe, boolean includeBe, boolean includeLeAu);

	public abstract int getModifierTP(EquippedItem weapon);

	public int getModifier(Probe probe) {
		return getModifier(probe, true, true);
	}

	public abstract int getArmorBe();

	public abstract CombatStyle getCombatStyle();

	public abstract void setCombatStyle(CombatStyle style);

	public abstract void setBeCalculation(boolean auto);

	public abstract boolean isBeCalculation();

}
