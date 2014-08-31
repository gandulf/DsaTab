package com.dsatab.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.bugsense.trace.BugSenseHandler;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Animal;
import com.dsatab.data.AnimalAttack;
import com.dsatab.data.Art;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.Attribute;
import com.dsatab.data.BaseCombatTalent;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.CombatShieldTalent;
import com.dsatab.data.CombatTalent;
import com.dsatab.data.CustomAttribute;
import com.dsatab.data.Dice;
import com.dsatab.data.Feature;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroBaseInfo;
import com.dsatab.data.HeroConfiguration;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.Markable;
import com.dsatab.data.Purse;
import com.dsatab.data.Purse.PurseUnit;
import com.dsatab.data.Spell;
import com.dsatab.data.SpellInfo;
import com.dsatab.data.Talent;
import com.dsatab.data.enums.ArtGroupType;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.Hand;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.enums.Position;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.enums.UsageType;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.HuntingWeapon;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.MiscSpecification;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.data.notes.ChangeEvent;
import com.dsatab.data.notes.Connection;
import com.dsatab.data.notes.Event;
import com.dsatab.db.DataManager;
import com.dsatab.exception.FeatureTypeUnknownException;
import com.dsatab.exception.InconsistentDataException;
import com.dsatab.exception.TalentTypeUnknownException;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;

/**
 * Xml Reader and Writer for the Heldensoftware HeldenXML
 * 
 * @author Gandulf
 * 
 */
public class HeldenXmlParser {

	public static final String RUESTUNGSNAME = "ruestungsname";

	public static final String SCHILDNAME = "schildname";

	public static final String WAFFENNAME = "waffenname";

	public static final String ENCODING = "UTF-8";

	public static Document readDocument(InputStream in) throws JDOMException, IOException {
		SAXBuilder saxBuilder = new SAXBuilder();

		InputStreamReader isr = new InputStreamReader(in, ENCODING);
		InputSource is = new InputSource();
		is.setCharacterStream(isr);
		is.setEncoding(ENCODING);

		org.jdom2.Document dom = saxBuilder.build(is);

		if (dom == null) {
			Debug.error("Error: DOM was null.");
		}

		return dom;
	}

	public static Hero readHero(Context context, HeroFileInfo fileInfo, InputStream in, InputStream configIn)
			throws JDOMException, IOException, JSONException {

		Hero hero = null;

		Debug.TRACE = true;

		Document dom = readDocument(in);
		if (dom == null || dom.getRootElement() == null) {
			throw new InconsistentDataException("Invalid Xml file, could not find root element.");
		}

		String hsVersion = fileInfo.getVersion();
		Element heroElement = dom.getRootElement().getChild(Xml.KEY_HELD);
		// check for valid hero node
		if (heroElement == null) {
			throw new InconsistentDataException("Invalid Hero xml file, could not find <" + Xml.KEY_HELD
					+ "> element with in root node. HS-Version=" + hsVersion);
		}

		BugSenseHandler.clearCrashExtraData();
		BugSenseHandler.addCrashExtraData("HS-Version", hsVersion);

		hero = new Hero(fileInfo);

		// Debug.verbose("Hero successfully parsed");
		HeroConfiguration configuration = null;
		if (configIn != null) {
			String data = Util.slurp(configIn, 1024);
			if (!TextUtils.isEmpty(data)) {
				JSONObject jsonObject = new JSONObject(new String(data));
				configuration = new HeroConfiguration(hero, jsonObject);
			}
		}

		if (configuration == null) {
			configuration = new HeroConfiguration(hero);
		}

		hero.setHeroConfiguration(configuration);

		hero.setName(heroElement.getAttributeValue(Xml.KEY_NAME));
		if (heroElement.getAttributeValue(Xml.KEY_PORTRAIT_PATH) != null) {
			hero.setPortraitUri(Uri.parse(heroElement.getAttributeValue(Xml.KEY_PORTRAIT_PATH)));
		}

		Element xpElement = DomUtil.getChildByTagName(heroElement, Xml.KEY_BASIS, Xml.KEY_ABENTEUERPUNKTE);
		hero.getExperience().setValue(Util.parseInteger(xpElement.getAttributeValue(Xml.KEY_VALUE)));

		Element freeXpElement = DomUtil.getChildByTagName(heroElement, Xml.KEY_BASIS, Xml.KEY_FREIE_ABENTEUERPUNKTE);
		hero.getFreeExperience().setValue(Util.parseInteger(freeXpElement.getAttributeValue(Xml.KEY_VALUE)));

		Debug.verbose("--- fillArtsAndSpecialFeatures");
		fillArtsAndSpecialFeatures(hero, heroElement);
		Debug.verbose("--- fillAdvantages");

		// has to be done before attributes because vollzauber features have a effect on astralenergie
		fillAdvantages(hero, heroElement);

		Debug.verbose("--- fillAttributes");
		fillAttributes(hero, heroElement);

		Element basisElement = heroElement.getChild(Xml.KEY_BASIS);
		if (basisElement == null)
			basisElement = heroElement;
		Debug.verbose("--- fillBaseInfo");
		fillBaseInfo(hero, basisElement);

		Debug.verbose("--- fillTalents");
		fillTalents(hero, heroElement);
		Debug.verbose("--- fillSpells");
		fillSpells(hero, heroElement);

		Debug.verbose("--- fillItems");
		fillItems(hero, heroElement);
		Debug.verbose("--- fillEquippedItems");
		fillEquippedItems(hero, heroElement);
		fillPurse(hero, heroElement);
		fillEvents(hero, heroElement);
		fillConnections(hero, heroElement);
		fillComments(hero, heroElement);
		Debug.verbose("--- onPostHeroLoaded");
		hero.onPostHeroLoaded(context);

		Debug.TRACE = false;
		return hero;
	}

	/**
	 * @param heldElement
	 */
	private static void fillAdvantages(AbstractBeing being, Element heldElement) {

		List<Element> list = new ArrayList<Element>();

		list.addAll(DomUtil.getChildrenByTagName(heldElement, Xml.KEY_VT, Xml.KEY_VORTEIL));

		list.addAll(DomUtil.getChildrenByTagName(heldElement, Xml.KEY_VORTEILE, Xml.KEY_VORTEIL));

		for (Element element : list) {
			FeatureType featureType = null;
			try {
				featureType = FeatureType.byXmlName(element.getAttributeValue(Xml.KEY_NAME).trim());
			} catch (FeatureTypeUnknownException e) {
				Debug.error(e);
				continue;
			}
			if (featureType != null) {
				Feature adv = new Feature(featureType);
				String value = element.getAttributeValue(Xml.KEY_VALUE);
				if (!TextUtils.isEmpty(value)) {
					adv.addValue(value);
				}
				adv.setComment(element.getAttributeValue(Xml.KEY_COMMENT));

				List<Element> auswahls = element.getChildren(Xml.KEY_AUSWAHL);
				if (auswahls != null) {
					for (Element auswahl : auswahls) {
						adv.addValue(auswahl.getAttributeValue(Xml.KEY_VALUE));
					}
				}

				being.addFeature(adv);
			}
		}
	}

	/**
	 * 
	 * @param hero
	 * @param heldElement
	 */
	private static void fillArtsAndSpecialFeatures(AbstractBeing hero, Element heldElement) {

		List<Element> list = new ArrayList<Element>();

		list.addAll(DomUtil.getChildrenByTagName(heldElement, Xml.KEY_SF, Xml.KEY_SONDERFERTIGKEIT));

		list.addAll(DomUtil.getChildrenByTagName(heldElement, Xml.KEY_SONDERFERTIGKEITEN, Xml.KEY_SONDERFERTIGKEIT));

		for (Element element : list) {

			String name = element.getAttributeValue(Xml.KEY_NAME).trim();
			ArtGroupType type = ArtGroupType.getTypeOfArt(name);

			if (type == null) {

				FeatureType featureType = null;
				try {
					featureType = FeatureType.byXmlName(element.getAttributeValue(Xml.KEY_NAME));
				} catch (FeatureTypeUnknownException e) {
					Debug.error(e);
					continue;
				}
				Feature specialFeature = new Feature(featureType);
				specialFeature.setComment(element.getAttributeValue(Xml.KEY_KOMMENTAR));

				List<Element> kulturChildren = element.getChildren(Xml.KEY_KULTUR);
				if (kulturChildren != null) {
					for (Element child : kulturChildren) {
						specialFeature.addValue(child.getAttributeValue(Xml.KEY_NAME));
					}
				}

				List<Element> auswahlChildren = element.getChildren(Xml.KEY_AUSWAHL);
				if (auswahlChildren != null) {
					for (Element child : auswahlChildren) {
						specialFeature.addValue(child.getAttributeValue(Xml.KEY_NAME));

						// in some cases we have a subelement with value in it (e.g. Trick)
						if (child.getChildren() != null) {
							for (Element subchild : child.getChildren()) {
								specialFeature.addValue(subchild.getAttributeValue(Xml.KEY_VALUE));
							}
						}
					}
				}

				Element child = element.getChild(Xml.KEY_GEGENSTAND);
				if (child != null) {
					specialFeature.addValue(child.getAttributeValue(Xml.KEY_NAME));
				}
				String spezialisierungsName = null;
				String spezialisierungsParam = null;

				if (specialFeature.getType() == FeatureType.Talentspezialisierung) {
					child = element.getChild(Xml.KEY_TALENT);
					if (child != null) {
						spezialisierungsName = child.getAttributeValue(Xml.KEY_NAME);
					}
					child = element.getChild(Xml.KEY_SPEZIALISIERUNG);
					if (child != null) {
						spezialisierungsParam = child.getAttributeValue(Xml.KEY_NAME);
					}

					specialFeature.addValues(spezialisierungsName, spezialisierungsParam);

				} else if (specialFeature.getType() == FeatureType.Zauberspezialisierung) {
					child = element.getChild(Xml.KEY_ZAUBER);
					if (child != null) {
						spezialisierungsName = child.getAttributeValue(Xml.KEY_NAME);
					}
					child = element.getChild(Xml.KEY_SPEZIALISIERUNG);
					if (child != null) {
						spezialisierungsParam = child.getAttributeValue(Xml.KEY_NAME);
					}
					specialFeature.addValues(spezialisierungsName, spezialisierungsParam);

				} else if (specialFeature.getType().xmlName().startsWith(Feature.RITUAL_KENNTNIS_PREFIX)) {
					// skip specialfeature ritualkenntnis since it's listed as talent anyway.
					continue;
				} else if (specialFeature.getType().xmlName().startsWith(Feature.LITURGIE_KENNTNIS_PREFIX)) {
					// skip specialfeature liturgiekenntnis since it's listed as talent anyway.
					continue;
				}

				hero.addFeature(specialFeature);
			} else {
				Art art = new Art(hero, element.getAttributeValue(Xml.KEY_NAME));
				art.setUnused(Boolean.parseBoolean(element.getAttributeValue(Xml.KEY_UNUSED)));
				art.setFavorite(Boolean.parseBoolean(element.getAttributeValue(Xml.KEY_FAVORITE)));

				if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_PROBE))) {
					art.setProbePattern(element.getAttributeValue(Xml.KEY_PROBE));
				}

				ArtInfo info = art.getInfo();

				if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_WIRKUNG))) {
					info.setEffect(element.getAttributeValue(Xml.KEY_WIRKUNG));
				}
				if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_DAUER))) {
					info.setCastDuration(element.getAttributeValue(Xml.KEY_DAUER));
				}
				if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_KOSTEN))) {
					info.setCosts(element.getAttributeValue(Xml.KEY_KOSTEN));
				}

				hero.addArt(art);
			}

		}
	}

	private static void fillBaseInfo(Hero hero, Element basisElement) {
		HeroBaseInfo info = hero.getBaseInfo();

		Element rasse = basisElement.getChild(Xml.KEY_RASSE);
		Element ausbildungen = basisElement.getChild(Xml.KEY_AUSBILDUNGEN);
		Element kultur = basisElement.getChild(Xml.KEY_KULTUR);
		Element aussehen = null, groesse = null;
		if (rasse != null) {
			aussehen = rasse.getChild(Xml.KEY_AUSSEHEN);
			groesse = rasse.getChild(Xml.KEY_GROESSE);
		}

		if (groesse != null) {
			info.setWeight(Util.parseInteger(groesse.getAttributeValue(Xml.KEY_GEWICHT)));
			info.setHeight(Util.parseInteger(groesse.getAttributeValue(Xml.KEY_VALUE)));
		}

		if (aussehen != null) {
			info.setAge(Util.parseInteger(aussehen.getAttributeValue(Xml.KEY_ALTER)));
			info.setEyeColor(aussehen.getAttributeValue(Xml.KEY_EYECOLOR));
			info.setHairColor(aussehen.getAttributeValue(Xml.KEY_HAIRCOLOR));

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 4; i++) {
				sb.append(aussehen.getAttributeValue(Xml.KEY_AUSSEHENTEXT_PREFIX + i));
				if (!TextUtils.isEmpty(aussehen.getAttributeValue(Xml.KEY_AUSSEHENTEXT_PREFIX + (i + 1))))
					sb.append(", ");
			}
			info.setLook(sb.toString());

			info.setTitle(aussehen.getAttributeValue(Xml.KEY_TITEL));

			info.setRank(aussehen.getAttributeValue(Xml.KEY_STAND));

		}

		if (rasse != null) {
			info.setRace(rasse.getAttributeValue(Xml.KEY_STRING));
		}

		if (ausbildungen != null) {
			List<Element> ausbildungElements = ausbildungen.getChildren();

			StringBuilder sb = new StringBuilder();

			for (Element ausbildung : ausbildungElements) {
				String value = ausbildung.getAttributeValue(Xml.KEY_STRING);
				if (!TextUtils.isEmpty(value)) {
					if (sb.length() > 0)
						sb.append(", ");
					sb.append(value);
				}
			}
			info.setEducation(sb.toString());
		}

		if (kultur != null) {
			info.setCulture(kultur.getAttributeValue(Xml.KEY_STRING));
		}
	}

	private static void fillAttributes(Hero hero, Element heldElement) {

		List<Element> domAttributes = DomUtil.getChildrenByTagName(heldElement, Xml.KEY_EIGENSCHAFTEN,
				Xml.KEY_EIGENSCHAFT);

		for (Element attributeElement : domAttributes) {

			Attribute attr = new Attribute(hero);
			attr.setName(attributeElement.getAttributeValue(Xml.KEY_NAME));
			attr.setType(AttributeType.valueOf(attributeElement.getAttributeValue(Xml.KEY_NAME)));
			attr.setValue(Util.parseInteger(attributeElement.getAttributeValue(Xml.KEY_VALUE)));
			attr.setMod(Util.parseInteger(attributeElement.getAttributeValue(Xml.KEY_MOD)));
			hero.addAttribute(attr);
		}

		for (CustomAttribute attr : hero.getHeroConfiguration().getAttributes()) {
			hero.addAttribute(attr);
		}

		if (!hero.hasAttribute(AttributeType.Lebensenergie_Aktuell)) {
			CustomAttribute le = new CustomAttribute(hero, AttributeType.Lebensenergie_Aktuell);
			le.setValue(hero.getAttributeValue(AttributeType.Lebensenergie));
			le.setReferenceValue(le.getValue());
			hero.addAttribute(le);
		}
		if (!hero.hasAttribute(AttributeType.Ausdauer_Aktuell)) {
			CustomAttribute le = new CustomAttribute(hero, AttributeType.Ausdauer_Aktuell);
			le.setValue(hero.getAttributeValue(AttributeType.Ausdauer));
			le.setReferenceValue(le.getValue());
			hero.addAttribute(le);
		}

		if (!hero.hasAttribute(AttributeType.Karmaenergie_Aktuell)) {
			CustomAttribute le = new CustomAttribute(hero, AttributeType.Karmaenergie_Aktuell);
			le.setValue(hero.getAttributeValue(AttributeType.Karmaenergie));
			le.setReferenceValue(le.getValue());
			hero.addAttribute(le);
		}

		if (!hero.hasAttribute(AttributeType.Astralenergie_Aktuell)) {
			CustomAttribute le = new CustomAttribute(hero, AttributeType.Astralenergie_Aktuell);
			le.setValue(hero.getAttributeValue(AttributeType.Astralenergie));
			le.setReferenceValue(le.getValue());
			hero.addAttribute(le);
		}

		if (!hero.hasAttribute(AttributeType.Behinderung)) {
			CustomAttribute be = new CustomAttribute(hero, AttributeType.Behinderung);
			hero.addAttribute(be);
		}
		if (!hero.hasAttribute(AttributeType.Ausweichen)) {
			CustomAttribute aw = new CustomAttribute(hero, AttributeType.Ausweichen);
			hero.addAttribute(aw);
		}
		if (!hero.hasAttribute(AttributeType.Geschwindigkeit)) {
			CustomAttribute gs = new CustomAttribute(hero, AttributeType.Geschwindigkeit);
			hero.addAttribute(gs);
		}

		if (!hero.hasAttribute(AttributeType.Initiative_Aktuell)) {
			CustomAttribute ini = new CustomAttribute(hero, AttributeType.Initiative_Aktuell);
			ini.setValue(0);
			hero.addAttribute(ini);
		}

		if (!hero.hasAttribute(AttributeType.Entrueckung)) {
			CustomAttribute entr = new CustomAttribute(hero, AttributeType.Entrueckung);
			entr.setValue(0);
			hero.addAttribute(entr);
		}

		if (!hero.hasAttribute(AttributeType.Verzueckung)) {
			CustomAttribute entr = new CustomAttribute(hero, AttributeType.Verzueckung);
			entr.setValue(0);
			hero.addAttribute(entr);
		}

		if (!hero.hasAttribute(AttributeType.Erschoepfung)) {
			CustomAttribute entr = new CustomAttribute(hero, AttributeType.Erschoepfung);
			entr.setValue(0);
			hero.addAttribute(entr);
		}

	}

	private static Element getItemElement(Element held) {
		Element itemsNode = held.getChild(Xml.KEY_GEGENSTAENDE_AE);
		if (itemsNode != null) {
			itemsNode.setName(Xml.KEY_GEGENSTAENDE);
		} else {
			itemsNode = held.getChild(Xml.KEY_GEGENSTAENDE);
		}
		if (itemsNode == null) {
			itemsNode = held;
		}

		return itemsNode;
	}

	private static Element getConnectionsElement(Element heroElement) {
		Element connectionsElement = heroElement.getChild(Xml.KEY_VERBINDUNGEN);
		if (connectionsElement == null) {
			connectionsElement = new Element(Xml.KEY_VERBINDUNGEN);
			heroElement.addContent(connectionsElement);
		}
		return connectionsElement;
	}

	private static Element getEquippmentElement(Element held) {
		Element equippmentNode = held.getChild(Xml.KEY_AUSRUESTUNGEN_UE);
		if (equippmentNode != null) {
			// for newer android versions rename ausrüstung back to ü
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
				equippmentNode.setName(Xml.KEY_AUSRUESTUNGEN);
		} else {
			equippmentNode = held.getChild(Xml.KEY_AUSRUESTUNGEN);
		}
		// for older heldensoftware verson there was no ausrüstungen tag, just
		// the held tag use this one.
		if (equippmentNode == null) {
			equippmentNode = held;
		}

		return equippmentNode;

	}

	protected static void fillConnections(Hero hero, Element heroElement) {
		List<Element> connectionElements = DomUtil.getChildrenByTagName(heroElement, Xml.KEY_VERBINDUNGEN,
				Xml.KEY_VERBINDUNG);

		for (Element element : connectionElements) {

			Connection connection = new Connection();
			connection.setDescription(element.getAttributeValue(Xml.KEY_DESCRIPTION));
			connection.setSozialStatus(Util.parseInt(element.getAttributeValue(Xml.KEY_SO), 1));
			connection.setName(element.getAttributeValue(Xml.KEY_NAME));
			hero.addConnection(connection);
		}

	}

	protected static void fillAnimal(Hero hero, Element animalItemElement) {
		Element animalElement = animalItemElement.getChild(Xml.KEY_TIER);

		Animal animal = new Animal(hero);

		animal.setName(animalItemElement.getAttributeValue(Xml.KEY_NAME));
		animal.setSlot(animalItemElement.getAttributeValue(Xml.KEY_SLOT));
		animal.setCount(Util.parseInt(animalItemElement.getAttributeValue(Xml.KEY_ANZAHL), 1));

		Element mod = animalItemElement.getChild(Xml.KEY_MOD_ALLGEMEIN);
		if (mod != null) {
			Element weightElement = mod.getChild(Xml.KEY_GEWICHT);
			Element priceElement = mod.getChild(Xml.KEY_PREIS);
			Element nameElement = mod.getChild(Xml.KEY_NAME);
			if (weightElement != null) {
				animal.setWeight(Util.parseFloat(weightElement.getAttributeValue(Xml.KEY_VALUE)));
			}
			if (priceElement != null) {
				animal.setPrice(Util.parseInt(priceElement.getAttributeValue(Xml.KEY_VALUE)));
			}
			if (nameElement != null) {
				animal.setTitle(nameElement.getAttributeValue(Xml.KEY_VALUE));
			}
		}

		Element groesse = animalElement.getChild(Xml.KEY_GROESSE);
		if (groesse != null) {
			animal.setHeight(Util.parseInteger(groesse.getAttributeValue(Xml.KEY_VALUE)));
		}
		Element species = animalElement.getChild(Xml.KEY_GATTUNG);
		if (species != null) {
			animal.setSpecies(species.getAttributeValue(Xml.KEY_VALUE));
		}
		Element family = animalElement.getChild(Xml.KEY_FAMILIE);
		if (family != null) {
			animal.setFamily(family.getAttributeValue(Xml.KEY_VALUE));
		}

		Element ini = animalElement.getChild(Xml.KEY_INI);
		if (ini != null) {
			Dice inidice = new Dice();
			inidice.diceType = Util.parseInt(ini.getAttributeValue(Xml.KEY_INI_W), 6);
			inidice.constant = Util.parseInt(ini.getAttributeValue(Xml.KEY_INI_SUM), 0);
			inidice.diceCount = Util.parseInt(ini.getAttributeValue(Xml.KEY_INI_MUL), 1);
			animal.setIniDice(inidice);
		}

		// fill attributes
		List<Element> domAttributes = DomUtil.getChildrenByTagName(animalElement, Xml.KEY_EIGENSCHAFTEN,
				Xml.KEY_EIGENSCHAFT);

		for (Element attributeElement : domAttributes) {
			Attribute attr = new Attribute(animal);
			attr.setName(attributeElement.getAttributeValue(Xml.KEY_NAME));
			attr.setType(AttributeType.valueOfTrim(attributeElement.getAttributeValue(Xml.KEY_NAME)));
			attr.setValue(Util.parseInteger(attributeElement.getAttributeValue(Xml.KEY_VALUE)));
			attr.setMod(Util.parseInteger(attributeElement.getAttributeValue(Xml.KEY_MOD)));
			// all animal attributes are absolute
			attr.setAbsolute(true);
			animal.addAttribute(attr);
		}

		if (!animal.hasAttribute(AttributeType.Lebensenergie_Aktuell)) {
			CustomAttribute le = new CustomAttribute(animal, AttributeType.Lebensenergie_Aktuell);
			le.setValue(animal.getAttributeValue(AttributeType.Lebensenergie));
			le.setReferenceValue(le.getValue());
			le.setAbsolute(true);
			animal.addAttribute(le);
		}

		if (!animal.hasAttribute(AttributeType.Ausdauer_Aktuell)) {
			CustomAttribute le = new CustomAttribute(animal, AttributeType.Ausdauer_Aktuell);
			le.setValue(animal.getAttributeValue(AttributeType.Ausdauer));
			le.setReferenceValue(le.getValue());
			le.setAbsolute(true);
			animal.addAttribute(le);
		}

		if (!animal.hasAttribute(AttributeType.Karmaenergie_Aktuell)) {
			CustomAttribute le = new CustomAttribute(animal, AttributeType.Karmaenergie_Aktuell);
			le.setValue(animal.getAttributeValue(AttributeType.Karmaenergie));
			le.setReferenceValue(le.getValue());
			le.setAbsolute(true);
			animal.addAttribute(le);
		}

		if (!animal.hasAttribute(AttributeType.Astralenergie_Aktuell)) {
			CustomAttribute le = new CustomAttribute(animal, AttributeType.Astralenergie_Aktuell);
			le.setValue(animal.getAttributeValue(AttributeType.Astralenergie));
			le.setReferenceValue(le.getValue());
			le.setAbsolute(true);
			animal.addAttribute(le);
		}

		// ---

		fillAdvantages(animal, animalElement);
		fillArtsAndSpecialFeatures(animal, animalElement);

		// fill attacks
		List<Element> domAttacks = DomUtil.getChildrenByTagName(animalElement, Xml.KEY_ANGRIFFE, Xml.KEY_ANGRIFF);

		for (Element attackElement : domAttacks) {
			String attackName = attackElement.getAttributeValue(Xml.KEY_NAME);

			Element at = attackElement.getChild(Xml.KEY_AT);
			Element pa = attackElement.getChild(Xml.KEY_PA);
			Element tp = attackElement.getChild(Xml.KEY_TP);
			Integer atValue = null, paValue = null;
			CombatMeleeAttribute atAttribute = null, paAttribute = null;
			Dice tpDice = null;
			String distance = null;

			if (at != null) {
				atValue = Util.parseInteger(at.getAttributeValue(Xml.KEY_VALUE));
				atAttribute = new CombatMeleeAttribute(animal, CombatMeleeAttribute.ATTACKE);
				atAttribute.setValue(atValue);
			}
			if (pa != null) {
				paValue = Util.parseInteger(pa.getAttributeValue(Xml.KEY_VALUE));
				paAttribute = new CombatMeleeAttribute(animal, CombatMeleeAttribute.PARADE);
				paAttribute.setValue(paValue);
			}
			if (tp != null) {
				tpDice = new Dice();
				tpDice.constant = Util.parseInt(tp.getAttributeValue(Xml.KEY_INI_SUM), 0);
				tpDice.diceType = Util.parseInt(tp.getAttributeValue(Xml.KEY_INI_W), 6);
				tpDice.diceCount = Util.parseInt(tp.getAttributeValue(Xml.KEY_INI_MUL), 1);
			}

			Element dk = attackElement.getChild(Xml.KEY_DK);
			if (dk != null) {
				distance = dk.getAttributeValue(Xml.KEY_VALUE);
			}
			AnimalAttack animalAttack = new AnimalAttack(animal, attackName, atAttribute, paAttribute, tpDice, distance);
			animal.addAnimalAttack(animalAttack);
		}

		hero.addAnimal(animal);
	}

	protected static void fillComments(Hero hero, Element heroElement) {
		Element kommentareElement = heroElement.getChild(Xml.KEY_KOMMENTARE);
		if (kommentareElement != null) {
			List<Element> kommentare = kommentareElement.getChildren(Xml.KEY_KOMMENTAR);

			for (Element kommentar : kommentare) {
				// <kommentar key="Akklimatisierung: Hitze" kommentar="Muuh" />
				String key = kommentar.getAttributeValue(Xml.KEY_KEY);

				try {

					Art art = hero.getArt(key);
					if (art != null && !TextUtils.isEmpty(kommentar.getAttributeValue(Xml.KEY_KOMMENTAR))) {
						art.getInfo().setMerkmale(kommentar.getAttributeValue(Xml.KEY_KOMMENTAR));
					}
					FeatureType featureType = FeatureType.byXmlName(key);
					Feature feature = hero.getFeature(featureType);
					if (feature != null && !TextUtils.isEmpty(kommentar.getAttributeValue(Xml.KEY_KOMMENTAR))) {
						feature.setComment(kommentar.getAttributeValue(Xml.KEY_KOMMENTAR));
					}
				} catch (FeatureTypeUnknownException e) {
					Debug.warning(e);
					// heldensofteare comments add values to key which makes it hard so find, so we just ignore them for
					// now
				}
			}

			List<Element> sfInfos = kommentareElement.getChildren(Xml.KEY_SF_INFOS);
			for (Element sfInfo : sfInfos) {
				// <sfInfos dauer="" kosten="" probe="" sf="" sfname="Stabzauber: Flammenschwert" wirkung="" />

				String infoName = sfInfo.getAttributeValue(Xml.KEY_SF_NAME);

				Art art = hero.getArt(infoName);
				if (art != null) {
					if (!TextUtils.isEmpty(sfInfo.getAttributeValue(Xml.KEY_PROBE)))
						art.setProbePattern(sfInfo.getAttributeValue(Xml.KEY_PROBE));
					if (!TextUtils.isEmpty(sfInfo.getAttributeValue(Xml.KEY_KOSTEN)))
						art.getInfo().setCosts(sfInfo.getAttributeValue(Xml.KEY_KOSTEN));
					if (!TextUtils.isEmpty(sfInfo.getAttributeValue(Xml.KEY_WIRKUNG)))
						art.getInfo().setEffect(sfInfo.getAttributeValue(Xml.KEY_WIRKUNG));
					if (!TextUtils.isEmpty(sfInfo.getAttributeValue(Xml.KEY_DAUER)))
						art.getInfo().setCastDuration(sfInfo.getAttributeValue(Xml.KEY_DAUER));
				}

			}
		}
	}

	protected static void fillEvents(Hero hero, Element heroElement) {
		Element notiz = DomUtil.getChildByTagName(heroElement, Xml.KEY_BASIS, Xml.KEY_NOTIZ);

		if (notiz != null) {
			Event event = new Event();
			event.setCategory(EventCategory.Heldensoftware);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <= 11; i++) {
				String commentLine = notiz.getAttributeValue(Xml.KEY_NOTIZ_PREFIX + i);
				if (!TextUtils.isEmpty(commentLine)) {
					sb.append(commentLine);
				}
				sb.append("\n");
			}
			event.setComment(sb.toString());
			hero.getHeroConfiguration().addEvent(event);
		}

		Collections.sort(hero.getHeroConfiguration().getEvents(), Event.COMPARATOR);

		// public ChangeEvent(Element element) {
		// time = new
		// Date(Util.parseLong(element.getAttributeValue(Xml.KEY_TIME)));
		// xps =
		// Util.parseInteger(element.getAttributeValue(Xml.KEY_ABENTEUERPUNKTE_UPPER));
		// oldValue = Util.parseInteger(element.getAttributeValue(Xml.KEY_ALT));
		// newValue = Util.parseInteger(element.getAttributeValue(Xml.KEY_NEU));
		// info = element.getAttributeValue(Xml.KEY_INFO);
		// object = element.getAttributeValue(Xml.KEY_OBJ);
		// version = element.getAttributeValue(Xml.KEY_VERSION);
		// text = element.getAttributeValue(Xml.KEY_TEXT);
		// }
	}

	protected static void fillPurse(Hero hero, Element heroElement) {
		Element purseElement = heroElement.getChild(Xml.KEY_GELDBOERSE);
		if (purseElement != null) {
			List<?> nodes = purseElement.getChildren(Xml.KEY_MUENZE);

			for (int i = 0; i < nodes.size(); i++) {
				Element m = (Element) nodes.get(i);
				PurseUnit w = PurseUnit.getByXmlName(m.getAttributeValue(Xml.KEY_NAME));
				Integer value = Util.parseInteger(m.getAttributeValue(Xml.KEY_ANZAHL));
				hero.getPurse().setCoins(w, value);
			}
		}
	}

	protected static void fillEquippedItems(Hero hero, Element heroElement) {

		Element equippementNode = getEquippmentElement(heroElement);
		List<Element> equippedElements = equippementNode.getChildren(Xml.KEY_HELDENAUSRUESTUNG);

		List<Element> beidhaendigerKampfElements = new ArrayList<Element>();
		List<EquippedItem> secondaryItems = new ArrayList<EquippedItem>();

		for (int i = 0; i < equippedElements.size(); i++) {
			Element element = equippedElements.get(i);

			String name = element.getAttributeValue(Xml.KEY_NAME);
			if (name.equals(Hero.JAGTWAFFE)) {
				int number = Util.parseInt(element.getAttributeValue(Xml.KEY_NUMMER), -1);
				int set = Util.parseInt(element.getAttributeValue(Xml.KEY_SET), -1);
				if (set >= 0 && number >= 0) {
					HuntingWeapon huntingWeapon = new HuntingWeapon(set, number);
					hero.addHuntingWeapon(huntingWeapon);
				}
				continue;
			}

			if (name.startsWith(Hero.PREFIX_BK)) {
				beidhaendigerKampfElements.add(element);
				continue;
			}

			String itemName = element.getAttributeValue(WAFFENNAME);
			String itemSlot = element.getAttributeValue(Xml.KEY_SLOT);
			if (itemName == null)
				itemName = element.getAttributeValue(SCHILDNAME);
			if (itemName == null)
				itemName = element.getAttributeValue(RUESTUNGSNAME);

			Item item = hero.getItem(itemName, itemSlot);

			if (item == null) {
				Debug.error(new InconsistentDataException("Unable to find an item with the name '" + itemName
						+ "' in slot '" + itemSlot + "':" + element.toString()));
				continue;
			}

			UsageType usageType = null;
			int set = Util.parseInt(element.getAttributeValue(Xml.KEY_SET), 0);

			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_VERWENDUNGSART))) {
				usageType = UsageType.valueOf(element.getAttributeValue(Xml.KEY_VERWENDUNGSART));
			}
			String bezeichner = element.getAttributeValue(Xml.KEY_BEZEICHNER);

			ItemSpecification itemSpecification = EquippedItem.getItemSpecification(hero, name, item, usageType,
					bezeichner);

			CombatTalent combatTalent = null;
			String combatTalentName = element.getAttributeValue(Xml.KEY_TALENT);
			if (!TextUtils.isEmpty(combatTalentName) && !CombatShieldTalent.SCHILDPARADE.equals(combatTalentName)
					&& !CombatShieldTalent.PARIERWAFFENPARADE.equals(combatTalentName)) {
				combatTalent = hero.getCombatTalent(combatTalentName);
			} else {
				combatTalent = EquippedItem.getCombatTalent(hero, usageType, set, name, itemSpecification);
			}

			EquippedItem equippedItem = new EquippedItem(hero, combatTalent, item, itemSpecification);
			equippedItem.setCellNumber(Util.parseInt(element.getAttributeValue(Xml.KEY_CELL_NUMBER)));

			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_HAND))) {
				equippedItem.setHand(Hand.valueOf(element.getAttributeValue(Xml.KEY_HAND)));
			}

			equippedItem.setSet(set);
			equippedItem.setSlot(itemSlot);
			equippedItem.setName(element.getAttributeValue(Xml.KEY_NAME));
			equippedItem.setItemSpecification(itemSpecification);
			equippedItem.setSchildIndex(Util.parseInteger(element.getAttributeValue(Xml.KEY_SCHILD)));
			equippedItem.setUsageType(usageType);

			if (Util.parseInt(element.getAttributeValue(Xml.KEY_SCHILD)) > 0) {
				secondaryItems.add(equippedItem);
			}

			hero.addEquippedItem(equippedItem, false);
		}

		// handle bk elements
		for (Iterator<Element> iter = beidhaendigerKampfElements.iterator(); iter.hasNext();) {

			Element element = iter.next();

			if (element.getAttributeValue(Xml.KEY_NAME).startsWith(Hero.PREFIX_BK)) {

				Integer set = Util.parseInteger(element.getAttributeValue(Xml.KEY_SET));
				String bk = element.getAttributeValue(Xml.KEY_NAME);
				int bk1 = Util.parseInt(bk.substring(2, 3));
				int bk2 = Util.parseInt(bk.substring(3, 4));

				EquippedItem item1 = hero.getEquippedItem(set, Hero.PREFIX_NKWAFFE + bk1);
				EquippedItem item2 = hero.getEquippedItem(set, Hero.PREFIX_NKWAFFE + bk2);

				if (item2 != null && item1 != null) {
					item1.setSecondaryItem(item2);
					item2.setSecondaryItem(item1);

					item1.setBeidhändigerKampf(true);
					item2.setBeidhändigerKampf(true);
				} else {
					Debug.warning("Incorrect BeidhaendigerKampf setting " + bk);
					heroElement.removeContent(element);
					iter.remove();
				}
			}
		}

		for (EquippedItem equippedItem : secondaryItems) {
			if (equippedItem.getSchildIndex() != null) {
				if (equippedItem.getSchildIndex() > 0) {
					EquippedItem secondaryEquippedItem = hero.getEquippedItem(equippedItem.getSet(),
							EquippedItem.NAME_PREFIX_SCHILD + equippedItem.getSchildIndex());
					if (secondaryEquippedItem != null) {
						equippedItem.setSecondaryItem(secondaryEquippedItem);
						secondaryEquippedItem.setSecondaryItem(equippedItem);
					}
				}
			}
		}

	}

	private static boolean isAnimalItemElement(Element element) {
		return element.getChild(Xml.KEY_TIER) != null;
	}

	private static void fillItems(Hero hero, Element heroElement) {

		List<Element> itemsElements = DomUtil.getChildrenByTagName(heroElement, Xml.KEY_GEGENSTAENDE,
				Xml.KEY_GEGENSTAND);

		for (Element element : itemsElements) {

			if (isAnimalItemElement(element)) {
				fillAnimal(hero, element);
				continue;
			}

			if (element.getAttribute(Xml.KEY_NAME) != null) {

				Item item = DataManager.getItemByName(element.getAttributeValue(Xml.KEY_NAME));

				if (item != null) {
					item = item.duplicate();
				} else {
					Debug.warning("Item not found generating it:" + element.getAttributeValue(Xml.KEY_NAME));

					item = new Item();
					item.setName(element.getAttributeValue(Xml.KEY_NAME));
					item.addSpecification(new MiscSpecification(item, ItemType.Sonstiges));
					item.setId(UUID.randomUUID());
					item.setCategory("Sonstiges");
				}

				if (element.getAttribute(Xml.KEY_SCREEN) != null) {
					// there is only one inventory screen left...
					int screen = Util.parseInt(element.getAttributeValue(Xml.KEY_SCREEN));
					item.setContainerId(screen);
				}

				Element domallgemein = element.getChild(Xml.KEY_MOD_ALLGEMEIN);
				if (domallgemein != null) {
					Element name = domallgemein.getChild(Xml.KEY_NAME);
					if (name != null) {
						item.setTitle(name.getAttributeValue(Xml.KEY_VALUE));
					}
				}

				item.setCount(Util.parseInt(element.getAttributeValue(Xml.KEY_ANZAHL), 1));
				item.setSlot(element.getAttributeValue(Xml.KEY_SLOT));

				for (ItemSpecification itemSpecification : item.getSpecifications()) {
					if (itemSpecification instanceof Armor) {
						Armor armor = (Armor) itemSpecification;

						Element ruestung = element.getChild(Xml.KEY_RUESTUNG);

						// revert changes made due to harmony parsing bug of
						// umlauts
						if (ruestung == null) {
							ruestung = element.getChild(Xml.KEY_RUESTUNG_UE);
							if (ruestung != null) {
								ruestung.setName(Xml.KEY_RUESTUNG);
							}
						}

						if (ruestung != null) {

							String be = DomUtil.getChildValue(ruestung, Xml.KEY_GESAMT_BE, Xml.KEY_VALUE);
							if (be != null) {
								armor.setTotalBe(Util.parseFloat(be));
							}
							for (Position pos : Position.ARMOR_POSITIONS) {
								String rs = DomUtil.getChildValue(ruestung, pos.name().toLowerCase(Locale.GERMAN),
										Xml.KEY_VALUE);
								if (rs != null) {
									armor.setRs(pos, Util.parseInteger(rs));
								}
							}

							String rs = DomUtil.getChildValue(ruestung, Xml.KEY_RS, Xml.KEY_VALUE);
							if (rs != null) {
								armor.setTotalRs(Util.parseInteger(rs));
							}

							rs = DomUtil.getChildValue(ruestung, Xml.KEY_STERNE, Xml.KEY_VALUE);
							if (rs != null) {
								armor.setStars(Util.parseInteger(rs));
							}

						}
					}

					if (itemSpecification instanceof DistanceWeapon) {
						DistanceWeapon distanceWeapon = (DistanceWeapon) itemSpecification;

						List<Element> waffen = element.getChildren(Xml.KEY_FERNKAMPWAFFE);

						Element child;
						for (Element waffe : waffen) {

							child = waffe.getChild(Xml.KEY_ENTFERNUNG);
							if (child != null) {
								for (int i = 0; i < DistanceWeapon.DISTANCE_COUNT; i++) {
									String value = child.getAttributeValue("E" + i);
									if (!TextUtils.isEmpty(value)) {
										distanceWeapon.setDistances(i, value);
									}
								}
							}

							child = waffe.getChild(Xml.KEY_TPMOD);
							if (child != null) {
								for (int i = 0; i < DistanceWeapon.DISTANCE_COUNT; i++) {
									String value = child.getAttributeValue("M" + i);
									if (!TextUtils.isEmpty(value)) {
										distanceWeapon.setTpDistance(i, value);
									}
								}
							}

							child = waffe.getChild(Xml.KEY_TREFFERPUNKTE);
							if (child != null) {
								String tp = child.getAttributeValue(Xml.KEY_TREFFERPUNKTE_MUL) + "W"
										+ child.getAttributeValue(Xml.KEY_TREFFERPUNKTE_DICE) + "+"
										+ child.getAttributeValue(Xml.KEY_TREFFERPUNKTE_SUM);
								distanceWeapon.setTp(tp);
							}
						}

					}

					if (itemSpecification instanceof Shield) {
						Shield shield = (Shield) itemSpecification;

						List<Element> waffen = element.getChildren(Xml.KEY_SCHILDWAFFE);

						for (Element waffe : waffen) {
							Element wm = waffe.getChild(Xml.KEY_WAFFENMODIF);
							if (wm != null) {
								shield.setWmAt(Util.parseInteger(wm.getAttributeValue(Xml.KEY_WAFFENMODIF_AT)));
								shield.setWmPa(Util.parseInteger(wm.getAttributeValue(Xml.KEY_WAFFENMODIF_PA)));
							}
							Element bf = waffe.getChild(Xml.KEY_BRUCHFAKTOR);
							if (bf != null) {
								shield.setBf(Util.parseInteger(bf.getAttributeValue(Xml.KEY_BRUCHFAKTOR_AKT)));
							}
							Element ini = waffe.getChild(Xml.KEY_INI_MOD);
							if (ini != null) {
								shield.setIni(Util.parseInteger(ini.getAttributeValue(Xml.KEY_INI_MOD_INI)));
							}
						}

					}

					if (itemSpecification instanceof Weapon) {
						Weapon weapon = (Weapon) itemSpecification;

						List<Element> waffen = element.getChildren(Xml.KEY_NAHKAMPWAFFE);

						for (Element waffe : waffen) {

							if (waffe.getAttribute(Xml.KEY_VARIANTE) != null) {

								int variante = Util.parseInteger(waffe.getAttributeValue(Xml.KEY_VARIANTE));
								if (variante == weapon.getVersion()) {
									Element trefferpunkte = waffe.getChild(Xml.KEY_TREFFERPUNKTE);
									if (trefferpunkte != null) {
										String tp = trefferpunkte.getAttributeValue(Xml.KEY_TREFFERPUNKTE_MUL) + "W"
												+ trefferpunkte.getAttributeValue(Xml.KEY_TREFFERPUNKTE_DICE) + "+"
												+ trefferpunkte.getAttributeValue(Xml.KEY_TREFFERPUNKTE_SUM);
										weapon.setTp(tp);
									}

									Element tpKK = waffe.getChild(Xml.KEY_TREFFERPUNKTE_KK);
									if (tpKK != null) {
										weapon.setTpKKMin(Util.parseInteger(tpKK
												.getAttributeValue(Xml.KEY_TREFFERPUNKTE_KK_MIN)));
										weapon.setTpKKStep(Util.parseInteger(tpKK
												.getAttributeValue(Xml.KEY_TREFFERPUNKTE_KK_STEP)));
									}
									Element wm = waffe.getChild(Xml.KEY_WAFFENMODIF);
									if (wm != null) {
										weapon.setWmAt(Util.parseInteger(wm.getAttributeValue(Xml.KEY_WAFFENMODIF_AT)));
										weapon.setWmPa(Util.parseInteger(wm.getAttributeValue(Xml.KEY_WAFFENMODIF_PA)));
									}
									Element bf = waffe.getChild(Xml.KEY_BRUCHFAKTOR);
									if (bf != null) {
										weapon.setBf(Util.parseInteger(bf.getAttributeValue(Xml.KEY_BRUCHFAKTOR_AKT)));
									}
									Element ini = waffe.getChild(Xml.KEY_INI_MOD);
									if (ini != null) {
										weapon.setIni(Util.parseInteger(ini.getAttributeValue(Xml.KEY_INI_MOD_INI)));
									}
								}
							}

						}

					}
				}

				hero.addItem(item);
			}
		}

	}

	protected static void fillSpells(Hero hero, Element heldElement) {
		List<Element> spellList = DomUtil.getChildrenByTagName(heldElement, Xml.KEY_ZAUBERLISTE, Xml.KEY_ZAUBER);

		for (int i = 0; i < spellList.size(); i++) {
			Element element = spellList.get(i);
			Spell spell = new Spell(hero, element.getAttributeValue(Xml.KEY_NAME));

			spell.setProbePattern(element.getAttributeValue(Xml.KEY_PROBE));

			spell.setValue(Util.parseInteger(element.getAttributeValue(Xml.KEY_VALUE)));

			spell.setComments(element.getAttributeValue(Xml.KEY_ANMERKUNGEN));
			spell.setVariant(element.getAttributeValue(Xml.KEY_VARIANTE));

			spell.setUnused(Boolean.parseBoolean(element.getAttributeValue(Xml.KEY_UNUSED)));
			spell.setFavorite(Boolean.parseBoolean(element.getAttributeValue(Xml.KEY_FAVORITE)));

			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_HAUSZAUBER))
					&& Boolean.valueOf(element.getAttributeValue(Xml.KEY_HAUSZAUBER))) {
				spell.addFlag(com.dsatab.data.Spell.Flags.Hauszauber);
			}

			SpellInfo info = spell.getInfo();
			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_K)))
				info.setComplexity(element.getAttributeValue(Xml.KEY_K));

			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_ZAUBERKOMMENTAR)))
				info.setEffect(element.getAttributeValue(Xml.KEY_ZAUBERKOMMENTAR));

			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_KOSTEN)))
				info.setCosts(element.getAttributeValue(Xml.KEY_KOSTEN));

			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_REICHWEITE)))
				info.setRange(element.getAttributeValue(Xml.KEY_REICHWEITE));

			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_REPRESENTATION)))
				info.setRepresentation(element.getAttributeValue(Xml.KEY_REPRESENTATION));

			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_WIRKUNGSDAUER)))
				info.setEffectDuration(element.getAttributeValue(Xml.KEY_WIRKUNGSDAUER));

			if (!TextUtils.isEmpty(element.getAttributeValue(Xml.KEY_ZAUBERDAUER)))
				info.setCastDuration(element.getAttributeValue(Xml.KEY_ZAUBERDAUER));

			hero.addSpell(spell);
		}
	}

	protected static void fillTalents(Hero hero, Element heroElement) {

		List<Element> combatAttributesList = DomUtil.getChildrenByTagName(heroElement, Xml.KEY_KAMPF,
				Xml.KEY_KAMPFWERTE);

		List<Element> talentList = DomUtil.getChildrenByTagName(heroElement, Xml.KEY_TALENTLISTE, Xml.KEY_TALENT);
		Talent talent = null;

		for (int i = 0; i < talentList.size(); i++) {
			Element element = talentList.get(i);

			talent = null;
			TalentType talentType = TalentType.byXmlName(element.getAttributeValue(Xml.KEY_NAME));
			if (talentType == null) {
				Debug.error(new TalentTypeUnknownException(element.getAttributeValue(Xml.KEY_NAME)));
				continue;
			}
			int talentValue = Util.parseInt(element.getAttributeValue(Xml.KEY_VALUE));

			// combattalenttypes have to be handled special!!!
			if (talentType.type() == TalentGroupType.Nahkampf || talentType.type() == TalentGroupType.Fernkampf) {
				// add Peitsche as CombatTalent although
				// Heldensoftware doesn't treat is as one
				if (TalentType.Peitsche == talentType) {
					CombatMeleeAttribute at = new CombatMeleeAttribute(hero, CombatMeleeAttribute.ATTACKE);
					at.setValue(hero.getAttributeValue(AttributeType.at) + talentValue);

					talent = new CombatMeleeTalent(hero, talentType, at, null);
				} else if (talentType.type() == TalentGroupType.Fernkampf) {
					talent = new CombatDistanceTalent(hero, talentType);
				} else {
					Element combatElement;
					for (Iterator<Element> iter = combatAttributesList.iterator(); iter.hasNext();) {
						combatElement = iter.next();
						String combatTalentName = combatElement.getAttributeValue(Xml.KEY_NAME);

						if (talentType.xmlName().equals(combatTalentName)) {
							List<Element> nodes = combatElement.getChildren();

							CombatMeleeAttribute at = null, pa = null;
							for (Element node : nodes) {
								Element item = node;
								if (Xml.KEY_ATTACKE.equals(item.getName())) {
									at = new CombatMeleeAttribute(hero, CombatMeleeAttribute.ATTACKE);
									at.setValue(Util.parseInteger(item.getAttributeValue(Xml.KEY_VALUE)));
								} else if (Xml.KEY_PARADE.equals(item.getName())) {
									pa = new CombatMeleeAttribute(hero, CombatMeleeAttribute.PARADE);
									pa.setValue(Util.parseInteger(item.getAttributeValue(Xml.KEY_VALUE)));
								}
							}
							talent = new CombatMeleeTalent(hero, talentType, at, pa);
							iter.remove();
							break;
						}
					}
				}
			}

			if (talent == null) {
				talent = new Talent(hero, talentType);
			}
			talent.setUnused(Boolean.parseBoolean(element.getAttributeValue(Xml.KEY_UNUSED)));
			talent.setFavorite(Boolean.parseBoolean(element.getAttributeValue(Xml.KEY_FAVORITE)));
			talent.setProbePattern(element.getAttributeValue(Xml.KEY_PROBE));
			talent.setProbeBe(element.getAttributeValue(Xml.KEY_BE));
			talent.setValue(talentValue);
			talent.setComplexity(Util.parseInteger(element.getAttributeValue(Xml.KEY_K)));

			hero.addTalent(talent);
		}

		// now add missing talents with combattalents
		for (Element combatElement : combatAttributesList) {

			String talentName = combatElement.getAttributeValue(Xml.KEY_NAME);
			Debug.warning("Adding missing CombatTalent:" + talentName);
			//
			List<Element> nodes = combatElement.getChildren();
			CombatMeleeAttribute at = null, pa = null;
			for (Element node : nodes) {
				Element item = node;
				if (Xml.KEY_ATTACKE.equals(item.getName())) {
					at = new CombatMeleeAttribute(hero, CombatMeleeAttribute.ATTACKE);
					at.setValue(Util.parseInteger(item.getAttributeValue(Xml.KEY_VALUE)));
				} else if (Xml.KEY_PARADE.equals(item.getName())) {
					pa = new CombatMeleeAttribute(hero, CombatMeleeAttribute.PARADE);
					pa.setValue(Util.parseInteger(item.getAttributeValue(Xml.KEY_VALUE)));
				}
			}
			CombatMeleeTalent combatTalent = new CombatMeleeTalent(hero, TalentType.byXmlName(talentName), at, pa);

			hero.addTalent(combatTalent, false);
		}
	}

	public static void writeHero(Hero hero, Document dom, OutputStream out) throws IOException {
		// Create an output formatter, and have it write the doc.
		XMLOutputter output = new XMLOutputter();
		output.output(dom, out);
	}

	private static void writeAttribute(Hero hero, AbstractBeing being, Attribute attr, Element element) {
		if (element != null) {
			if (attr.getValue() != null) {
				Integer newValue = attr.getValue();
				if (attr.getMod() != null) {
					newValue -= attr.getMod();
				}
				newValue -= attr.getBaseValue();
				Integer oldValue = Util.parseInteger(element.getAttributeValue(Xml.KEY_VALUE));
				if (!Util.equalsOrNull(newValue, oldValue)) {
					ChangeEvent changeEvent = new ChangeEvent(newValue, oldValue, attr.getType().name(),
							"Eigenschaft geändert");
					hero.addChangeEvent(changeEvent);
					element.setAttribute(Xml.KEY_VALUE, Integer.toString(newValue));
				}
			} else {
				element.removeAttribute(Xml.KEY_VALUE);
			}
		}
	}

	private static void writeCombatTalent(Hero hero, BaseCombatTalent talent, Element element) {

		if (talent instanceof CombatMeleeTalent) {
			CombatMeleeTalent meleeTalent = (CombatMeleeTalent) talent;
			if (Xml.KEY_KAMPFWERTE.equals(element.getName())) {
				List<Element> nodes = element.getChildren();

				for (Element node : nodes) {
					Element item = node;
					if (Xml.KEY_ATTACKE.equals(item.getName()))
						writeCombatMeleeAttribute(hero, meleeTalent.getAttack(), item);
					else if (Xml.KEY_PARADE.equals(item.getName()))
						writeCombatMeleeAttribute(hero, meleeTalent.getDefense(), item);
				}
			} else {
				writeTalent(hero, talent, element);
			}
		} else {
			writeTalent(hero, talent, element);
		}
	}

	/**
	 * @param attr
	 * @param item
	 */
	private static void writeCombatMeleeAttribute(Hero hero, CombatMeleeAttribute attr, Element element) {
		if (attr.hasValue()) {
			Integer newValue = attr.getValue();
			Integer oldValue = Util.parseInteger(element.getAttributeValue(Xml.KEY_VALUE));
			if (!Util.equalsOrNull(newValue, oldValue)) {
				ChangeEvent changeEvent = new ChangeEvent(newValue, oldValue, attr.getName(), "Kampfwerte geändert");
				hero.addChangeEvent(changeEvent);
				element.setAttribute(Xml.KEY_VALUE, newValue.toString());
			}
		} else {
			element.removeAttribute(Xml.KEY_VALUE);
		}
	}

	private static void writeSpell(Hero hero, Spell spell, Element element) {
		writeMarkable(spell, element);

		if (spell.getValue() != null) {
			Integer newValue = spell.getValue();
			Integer oldValue = Util.parseInteger(element.getAttributeValue(Xml.KEY_VALUE));
			if (!Util.equalsOrNull(newValue, oldValue)) {
				ChangeEvent changeEvent = new ChangeEvent(newValue, oldValue, spell.getName(), "Zauber geändert");
				hero.addChangeEvent(changeEvent);
				element.setAttribute(Xml.KEY_VALUE, Integer.toString(spell.getValue()));
			}
		} else {
			element.removeAttribute(Xml.KEY_VALUE);
		}

		element.setAttribute(Xml.KEY_ANMERKUNGEN, Xml.toString(spell.getComments()));
		element.setAttribute(Xml.KEY_VARIANTE, Xml.toString(spell.getVariant()));
	}

	private static void writeTalent(Hero hero, Talent talent, Element element) {
		writeMarkable(talent, element);

		if (talent instanceof CombatDistanceTalent) {
			CombatDistanceTalent distanceTalent = (CombatDistanceTalent) talent;

			if (distanceTalent.getValue() != null) {
				Integer newValue = distanceTalent.getValue() - distanceTalent.getBaseValue();
				Integer oldValue = Util.parseInteger(element.getAttributeValue(Xml.KEY_VALUE));
				if (!Util.equalsOrNull(newValue, oldValue)) {
					ChangeEvent changeEvent = new ChangeEvent(newValue, oldValue, talent.getName(), "Talent geändert");
					hero.addChangeEvent(changeEvent);
					element.setAttribute(Xml.KEY_VALUE, Integer.toString(newValue));
				}
			} else {
				element.removeAttribute(Xml.KEY_VALUE);
			}
		} else if (talent.getValue() != null) {
			Integer newValue = talent.getValue();
			Integer oldValue = Util.parseInteger(element.getAttributeValue(Xml.KEY_VALUE));
			if (!Util.equalsOrNull(newValue, oldValue)) {
				ChangeEvent changeEvent = new ChangeEvent(newValue, oldValue, talent.getName(), "Talent geändert");
				hero.addChangeEvent(changeEvent);
				element.setAttribute(Xml.KEY_VALUE, Integer.toString(newValue));
			}
		} else {
			element.removeAttribute(Xml.KEY_VALUE);
		}
	}

	private static void writePurse(Purse purse, Element element) {

		for (Entry<Purse.PurseUnit, Integer> entry : purse.getCoins().entrySet()) {
			boolean found = false;
			for (Element p : element.getChildren(Xml.KEY_MUENZE)) {
				if (entry.getKey().xmlName().equals(p.getAttributeValue(Xml.KEY_NAME))) {
					if (entry.getValue() != null)
						p.setAttribute(Xml.KEY_ANZAHL, entry.getValue().toString());
					else
						p.setAttribute(Xml.KEY_ANZAHL, "0");

					found = true;
					break;
				}
			}
			if (found == false) {
				Element m = new Element(Xml.KEY_MUENZE);
				m.setAttribute(Xml.KEY_WAEHRUNG, entry.getKey().currency().xmlName());
				m.setAttribute(Xml.KEY_NAME, entry.getKey().xmlName());
				if (entry.getValue() != null)
					m.setAttribute(Xml.KEY_ANZAHL, entry.getValue().toString());
				else
					m.setAttribute(Xml.KEY_ANZAHL, "0");

				element.addContent(m);
			}
		}
	}

	private static void writeMarkable(Markable markable, Element element) {
		if (markable.isFavorite())
			element.setAttribute(Xml.KEY_FAVORITE, Boolean.TRUE.toString());
		else
			element.removeAttribute(Xml.KEY_FAVORITE);

		if (markable.isUnused())
			element.setAttribute(Xml.KEY_UNUSED, Boolean.TRUE.toString());
		else
			element.removeAttribute(Xml.KEY_UNUSED);
	}

	/**
	 * 
	 */
	public static void onPreHeroSaved(Hero hero, Element heldElement) {
		Debug.verbose("Preparing hero to be saved. Populating data to XML.");

		Element equippmentNode = getEquippmentElement(heldElement);
		Element itemsNode = getItemElement(heldElement);

		Element ereignisse = heldElement.getChild(Xml.KEY_EREIGNISSE);
		if (ereignisse == null) {
			ereignisse = new Element(Xml.KEY_EREIGNISSE);
			heldElement.addContent(ereignisse);
		}

		if (hero.getPortraitUri() != null)
			heldElement.setAttribute(Xml.KEY_PORTRAIT_PATH, hero.getPortraitUri().toString());
		else
			heldElement.removeAttribute(Xml.KEY_PORTRAIT_PATH);

		List<Element> domAttributes = DomUtil.getChildrenByTagName(heldElement, Xml.KEY_EIGENSCHAFTEN,
				Xml.KEY_EIGENSCHAFT);
		for (Element attributeElement : domAttributes) {

			Attribute attribute = hero.getAttribute(AttributeType.valueOf(attributeElement
					.getAttributeValue(Xml.KEY_NAME)));

			if (attribute != null) {
				writeAttribute(hero, hero, attribute, attributeElement);
				Debug.trace("Xml popuplate attr " + attributeElement);
			} else {
				Debug.trace("Xml popuplate attr not found " + attributeElement);
			}
		}

		List<Element> talentList = DomUtil.getChildrenByTagName(heldElement, Xml.KEY_TALENTLISTE, Xml.KEY_TALENT);

		for (Element talentElement : talentList) {
			try {
				Talent talent = hero.getTalent(talentElement.getAttributeValue(Xml.KEY_NAME));
				if (talent != null) {
					writeTalent(hero, talent, talentElement);
					Debug.trace("Xml popuplate talent " + talentElement);
				} else {
					Debug.trace("Xml popuplate talent not found " + talentElement);
				}
			} catch (TalentTypeUnknownException e) {
				Debug.error("Skipping talent since it's unknown " + talentElement, e);
			}
		}

		List<Element> combatAttributesList = DomUtil.getChildrenByTagName(heldElement, Xml.KEY_KAMPF,
				Xml.KEY_KAMPFWERTE);

		for (Element combatTalentElement : combatAttributesList) {
			try {

				BaseCombatTalent combatTalent = hero.getCombatTalent(combatTalentElement
						.getAttributeValue(Xml.KEY_NAME));
				if (combatTalent != null) {
					writeCombatTalent(hero, combatTalent, combatTalentElement);
					Debug.trace("Xml popuplate combattalent " + combatTalentElement);
				} else {
					Debug.trace("Xml popuplate combattalent not found " + combatTalentElement);
				}
			} catch (TalentTypeUnknownException e) {
				Debug.error("Skipping combattalent since it's unknown " + combatTalentElement, e);
			}
		}

		List<Element> spellList = DomUtil.getChildrenByTagName(heldElement, Xml.KEY_ZAUBERLISTE, Xml.KEY_ZAUBER);

		for (Element spellElement : spellList) {
			Spell spell = hero.getSpell(spellElement.getAttributeValue(Xml.KEY_NAME));
			if (spell != null) {
				writeSpell(hero, spell, spellElement);
				Debug.trace("Xml popuplate spell " + spellElement);
			} else {
				Debug.trace("Xml popuplate spell not found " + spellElement);
			}

		}

		List<Element> sfs = DomUtil.getChildrenByTagName(heldElement, Xml.KEY_SF, Xml.KEY_SONDERFERTIGKEIT);

		for (Element sf : sfs) {
			Art art = hero.getArt(Art.normalizeName(sf.getAttributeValue(Xml.KEY_NAME)));
			if (art != null) {
				writeArt(art, sf);
				Debug.trace("Xml popuplate art " + sf);
			} else {
				Debug.trace("Xml popuplate art not found " + sf);
			}
		}

		Element purseElement = heldElement.getChild(Xml.KEY_GELDBOERSE);
		if (purseElement != null) {
			writePurse(hero.getPurse(), purseElement);
			Debug.trace("Xml popuplate purse " + purseElement);
		}

		if (hero.getExperience() != null) {
			Element experienceElement = DomUtil.getChildByTagName(heldElement, Xml.KEY_BASIS, Xml.KEY_ABENTEUERPUNKTE);

			if (hero.getExperience().getValue() != null) {
				experienceElement.setAttribute(Xml.KEY_VALUE, Util.toString(hero.getExperience().getValue()));
			} else
				experienceElement.removeAttribute(Xml.KEY_VALUE);

			Debug.trace("Xml popuplate xp " + experienceElement);
		}

		if (hero.getFreeExperience() != null) {
			Element freeExperienceElement = DomUtil.getChildByTagName(heldElement, Xml.KEY_BASIS,
					Xml.KEY_FREIE_ABENTEUERPUNKTE);

			if (hero.getFreeExperience().getValue() != null) {
				freeExperienceElement.setAttribute(Xml.KEY_VALUE, Util.toString(hero.getFreeExperience().getValue()));
			} else
				freeExperienceElement.removeAttribute(Xml.KEY_VALUE);

			Debug.trace("Xml popuplate free xp " + freeExperienceElement);
		}

		List<Element> equippedElements = equippmentNode.getChildren(Xml.KEY_HELDENAUSRUESTUNG);

		List<EquippedItem> allEquippedItems = new ArrayList<EquippedItem>(hero.getAllEquippedItems());
		List<Element> huntingWeaponElements = new ArrayList<Element>();

		for (Iterator<Element> iter = equippedElements.iterator(); iter.hasNext();) {
			Element itemElement = iter.next();

			// remove all old once and add the new
			if (itemElement.getAttributeValue(Xml.KEY_NAME).startsWith(Hero.PREFIX_BK)) {
				iter.remove();
				continue;
			}

			if (itemElement.getAttributeValue(Xml.KEY_NAME).equals(Hero.JAGTWAFFE)) {
				huntingWeaponElements.add(itemElement);
				continue;
			}

			EquippedItem equippedItem = hero.getEquippedItem(Util.parseInt(itemElement.getAttributeValue(Xml.KEY_SET)),
					itemElement.getAttributeValue(Xml.KEY_NAME));
			if (equippedItem != null) {
				allEquippedItems.remove(equippedItem);
				writeEquippedItem(hero, equippedItem, itemElement);
				Debug.trace("Xml popuplate equippeditem " + itemElement);
			} else {
				Debug.trace("Xml popuplate NO EQUIPPED ITEM found, removing it: " + itemElement);
				iter.remove();
			}
		}

		for (EquippedItem newItem : allEquippedItems) {
			Element element = new Element(Xml.KEY_HELDENAUSRUESTUNG);
			writeEquippedItem(hero, newItem, element);

			equippmentNode.addContent(element);

		}

		// -- beidhändigerkampf
		for (EquippedItem equippedItem1 : hero.getAllEquippedItems()) {
			if (equippedItem1.isBeidhändigerKampf() && equippedItem1.getSecondaryItem() != null) {

				EquippedItem equippedItem2 = equippedItem1.getSecondaryItem();

				if (equippedItem2 != null && equippedItem2.isBeidhändigerKampf()
						&& equippedItem1.getNameId() < equippedItem2.getNameId()) {
					Element bk = new Element(Xml.KEY_HELDENAUSRUESTUNG);
					writeBeidhändigerKampf(equippedItem1, equippedItem2, bk);
					equippmentNode.addContent(bk);
				}
			}
		}

		// hunting weapon
		for (int i = 0; i < Hero.INVENTORY_SET_COUNT; i++) {
			boolean found = false;
			for (Iterator<Element> iter = huntingWeaponElements.iterator(); iter.hasNext();) {
				Element element = iter.next();
				int elementSet = Util.parseInt(element.getAttributeValue(Xml.KEY_SET), -1);
				if (elementSet == i) {
					found = true;
					if (hero.getHuntingWeapons(i) != null)
						writeHuntingWeapon(hero.getHuntingWeapons(i), element);
					else
						iter.remove();
					break;
				}
				// there was a bug that added hunting weapons without sets
				// remove them from dom again!
				if (elementSet == -1) {
					iter.remove();
				}
			}

			if (!found) {
				Element element = new Element(Xml.KEY_HELDENAUSRUESTUNG);
				writeHuntingWeapon(hero.getHuntingWeapons(i), element);

				equippmentNode.addContent(element);
			}
		}

		// items and animals

		List<Element> itemsElements = itemsNode.getChildren(Xml.KEY_GEGENSTAND);

		List<Animal> allAnimals = new ArrayList<Animal>(hero.getAnimals());

		List<Item> allItems = new ArrayList<Item>();
		for (ItemContainer itemContainer : hero.getItemContainers()) {
			allItems.addAll(itemContainer.getItems());
		}

		for (Iterator<Element> iter = itemsElements.iterator(); iter.hasNext();) {
			Element itemElement = iter.next();

			if (isAnimalItemElement(itemElement)) {
				Animal animal = hero.getAnimal(itemElement.getAttributeValue(Xml.KEY_NAME),
						itemElement.getAttributeValue(Xml.KEY_SLOT));

				if (animal != null) {
					allAnimals.remove(animal);
					writeAnimal(hero, animal, itemElement);
					Debug.trace("Xml popuplate animal " + itemElement);
				} else {
					Debug.trace("Xml popuplate NO ANIMAL found remove it " + itemElement);
					iter.remove();
				}
			} else {

				Item item = hero.getItem(itemElement.getAttributeValue(Xml.KEY_NAME),
						itemElement.getAttributeValue(Xml.KEY_SLOT));

				if (item != null) {
					allItems.remove(item);
					writeItem(item, itemElement);
					Debug.trace("Xml popuplate item " + itemElement);
				} else {
					Debug.trace("Xml popuplate NO ITEM found remove it " + itemElement);
					iter.remove();
				}
			}

		}

		for (Item newItem : allItems) {
			Element element = new Element(Xml.KEY_GEGENSTAND);
			writeItem(newItem, element);
			itemsNode.addContent(element);
		}

		for (Animal newAnimal : allAnimals) {
			Element element = new Element(Xml.KEY_GEGENSTAND);
			writeAnimal(hero, newAnimal, element);
			itemsNode.addContent(element);
		}

		// connections

		Element connectionsElement = getConnectionsElement(heldElement);
		List<Element> connectionElements = DomUtil.getChildrenByTagName(heldElement, Xml.KEY_VERBINDUNGEN,
				Xml.KEY_VERBINDUNG);

		List<Connection> allConnections = new ArrayList<Connection>(hero.getConnections());

		for (Iterator<Element> iter = connectionElements.iterator(); iter.hasNext();) {
			Element element = iter.next();

			boolean found = false;
			for (Connection connection : hero.getConnections()) {
				if (connection.getName().equals(element.getAttributeValue(Xml.KEY_NAME))) {
					allConnections.remove(connection);

					writeConnection(connection, element);

					found = true;
					break;
				}
			}

			if (!found) {
				iter.remove();
			}
		}

		for (Connection connection : allConnections) {
			Element element = new Element(Xml.KEY_VERBINDUNG);
			writeConnection(connection, element);
			connectionsElement.addContent(element);
		}

		// events
		for (ChangeEvent changeEvent : hero.getChangeEvents()) {
			Element element = new Element(Xml.KEY_EREIGNIS);
			writeChangeEvent(changeEvent, element);
			ereignisse.addContent(element);
		}

		Element notiz = DomUtil.getChildByTagName(heldElement, Xml.KEY_BASIS, Xml.KEY_NOTIZ);
		for (Event event : hero.getEvents()) {
			writeEvent(event, notiz);
		}

	}

	/**
	 * @param art
	 * @param sf
	 */
	private static void writeArt(Art art, Element element) {
		writeMarkable(art, element);
	}

	private static void writeEvent(Event event, Element element) {
		if (event.getCategory() == EventCategory.Heldensoftware && Xml.KEY_NOTIZ.equals(element.getName())) {
			String[] events = event.getComment().split("\\r?\\n");

			for (int i = 0; i < events.length; i++) {
				element.setAttribute(Xml.KEY_NOTIZ_PREFIX + i, events[i]);
			}
			// fill up empty values if necessary
			for (int i = events.length; i <= 11; i++) {
				element.setAttribute(Xml.KEY_NOTIZ_PREFIX + i, "");
			}
		}
	}

	/**
	 * @param newItem
	 * @param element
	 */
	private static void writeEquippedItem(Hero hero, EquippedItem equippedItem, Element element) {
		if (equippedItem.getHand() != null)
			element.setAttribute(Xml.KEY_HAND, equippedItem.getHand().name());

		Item item = equippedItem.getItem();
		if (item != null) {
			ItemSpecification itemSpecification = equippedItem.getItemSpecification();
			if (itemSpecification instanceof Weapon || itemSpecification instanceof DistanceWeapon) {
				element.setAttribute(WAFFENNAME, item.getName());
				element.removeAttribute(SCHILDNAME);
				element.removeAttribute(RUESTUNGSNAME);
			} else if (itemSpecification instanceof Shield) {
				element.setAttribute(SCHILDNAME, item.getName());
				element.removeAttribute(WAFFENNAME);
				element.removeAttribute(RUESTUNGSNAME);
			} else if (itemSpecification instanceof Armor) {
				element.setAttribute(RUESTUNGSNAME, item.getName());
				element.removeAttribute(SCHILDNAME);
				element.removeAttribute(WAFFENNAME);
			}
		}

		element.setAttribute(Xml.KEY_SET, Util.toString(equippedItem.getSet()));
		if (equippedItem.getSlot() != null) {
			element.setAttribute(Xml.KEY_SLOT, equippedItem.getSlot());
		}

		if (equippedItem.getName() == null) {
			if (element.getAttribute(Xml.KEY_NAME) == null) {
				String namePrefix = null;

				if (item.hasSpecification(Weapon.class)) {
					namePrefix = EquippedItem.NAME_PREFIX_NK;
				}
				if (item.hasSpecification(DistanceWeapon.class)) {
					namePrefix = EquippedItem.NAME_PREFIX_FK;
				}
				if (item.hasSpecification(Shield.class)) {
					namePrefix = EquippedItem.NAME_PREFIX_SCHILD;
				}
				if (item.hasSpecification(Armor.class)) {
					namePrefix = EquippedItem.NAME_PREFIX_RUESTUNG;
				}

				// find first free slot
				int i = 1;
				while (hero.getEquippedItem(equippedItem.getSet(), namePrefix + i) != null) {
					i++;
				}
				element.setAttribute(Xml.KEY_NAME, namePrefix + i);
			}
		} else {
			element.setAttribute(Xml.KEY_NAME, equippedItem.getName());
		}

		if (equippedItem.getTalent() != null)
			if (equippedItem.getTalent() instanceof CombatShieldTalent) {
				element.removeAttribute(Xml.KEY_TALENT);
			} else {
				element.setAttribute(Xml.KEY_TALENT, equippedItem.getTalent().getName());
			}
		else
			element.removeAttribute(Xml.KEY_TALENT);

		if (equippedItem.getUsageType() != null)
			element.setAttribute(Xml.KEY_VERWENDUNGSART, equippedItem.getUsageType().name());
		else
			element.removeAttribute(Xml.KEY_VERWENDUNGSART);

		if (TextUtils.isEmpty(equippedItem.getItemSpecification().getSpecificationLabel())) {
			if (equippedItem.getItemSpecification() instanceof Weapon)
				element.setAttribute(Xml.KEY_BEZEICHNER, "");
			else
				element.removeAttribute(Xml.KEY_BEZEICHNER);
		} else {
			element.setAttribute(Xml.KEY_BEZEICHNER, equippedItem.getItemSpecification().getSpecificationLabel());
		}

		if (equippedItem.getSchildIndex() != null) {
			element.setAttribute(Xml.KEY_SCHILD, Util.toString(equippedItem.getSchildIndex()));
		}

	}

	/**
	 * @param bhKampf
	 * @param bk
	 */
	private static void writeBeidhändigerKampf(EquippedItem item1, EquippedItem item2, Element element) {
		element.setAttribute(Xml.KEY_SET, Util.toString(item1.getSet()));

		if (item1.getNameId() < item2.getNameId())
			element.setAttribute(Xml.KEY_NAME, Hero.PREFIX_BK + item1.getNameId() + item2.getNameId());
		else
			element.setAttribute(Xml.KEY_NAME, Hero.PREFIX_BK + item2.getNameId() + item1.getNameId());

	}

	/**
	 * @param newItem
	 * @param element
	 */
	private static void writeAnimal(Hero hero, Animal animal, Element element) {
		element.setAttribute(Xml.KEY_NAME, animal.getName());
		element.setAttribute(Xml.KEY_ANZAHL, Integer.toString(animal.getCount()));
		if (!TextUtils.isEmpty(animal.getSlot()))
			element.setAttribute(Xml.KEY_SLOT, animal.getSlot());

		if (!animal.getTitle().equals(animal.getName())) {
			Element modallgemein = Xml.getOrCreateElement(element, Xml.KEY_MOD_ALLGEMEIN);

			Element name = Xml.getOrCreateElement(modallgemein, Xml.KEY_NAME);
			name.setAttribute(Xml.KEY_VALUE, animal.getTitle());

			Element price = Xml.getOrCreateElement(modallgemein, Xml.KEY_PREIS);
			price.setAttribute(Xml.KEY_VALUE, Xml.toString(animal.getPrice()));

			Element weight = Xml.getOrCreateElement(modallgemein, Xml.KEY_GEWICHT);
			weight.setAttribute(Xml.KEY_VALUE, Xml.toString(animal.getWeight()));

		}

		Element tierElement = Xml.getOrCreateElement(element, Xml.KEY_TIER);
		List<Element> domAttributes = DomUtil.getChildrenByTagName(tierElement, Xml.KEY_EIGENSCHAFTEN,
				Xml.KEY_EIGENSCHAFT);
		for (Element attribute : domAttributes) {
			writeAttribute(hero, animal,
					animal.getAttribute(AttributeType.valueOfTrim(attribute.getAttributeValue(Xml.KEY_NAME))),
					attribute);
			Debug.verbose("Xml popuplate animal attr " + attribute);
		}

	}

	/**
	 * @param newItem
	 * @param element
	 */
	private static void writeItem(Item item, Element element) {
		element.setAttribute(Xml.KEY_NAME, item.getName());
		element.setAttribute(Xml.KEY_ANZAHL, Integer.toString(item.getCount()));
		element.setAttribute(Xml.KEY_SLOT, item.getSlot());

		if (!item.getTitle().equals(item.getName())) {
			Element modallgemein = Xml.getOrCreateElement(element, Xml.KEY_MOD_ALLGEMEIN);

			Element name = Xml.getOrCreateElement(modallgemein, Xml.KEY_NAME);
			name.setAttribute(Xml.KEY_VALUE, item.getTitle());

			Element price = Xml.getOrCreateElement(modallgemein, Xml.KEY_PREIS);
			price.setAttribute(Xml.KEY_VALUE, Xml.toString(item.getPrice()));

			Element weight = Xml.getOrCreateElement(modallgemein, Xml.KEY_GEWICHT);
			weight.setAttribute(Xml.KEY_VALUE, Xml.toString(item.getWeight()));

		}

		writeItemInfo(item, element);

	}

	/**
	 * @param itemInfo
	 * @param element
	 */
	private static void writeItemInfo(ItemCard itemInfo, Element element) {
		if (itemInfo.getContainerId() != Hero.FIRST_INVENTORY_SCREEN) {
			element.setAttribute(Xml.KEY_SCREEN, Util.toString(itemInfo.getContainerId()));
		} else {
			element.removeAttribute(Xml.KEY_SCREEN);
		}
	}

	/**
	 * @param changeEvent
	 * @param element
	 */
	private static void writeChangeEvent(ChangeEvent changeEvent, Element element) {
		element.setAttribute(Xml.KEY_TIME, Xml.toString(changeEvent.getTime().getTime()));
		element.setAttribute(Xml.KEY_ABENTEUERPUNKTE_UPPER, Xml.toString(changeEvent.getExperiencePoints(), "0"));
		element.setAttribute(Xml.KEY_ALT, Xml.toString(changeEvent.getOldValue()));
		element.setAttribute(Xml.KEY_NEU, Xml.toString(changeEvent.getNewValue()));
		if (!TextUtils.isEmpty(changeEvent.getInfo())) {
			element.setAttribute(Xml.KEY_INFO, Xml.toString(changeEvent.getInfo()));
		} else {
			element.removeAttribute(Xml.KEY_INFO);
		}
		element.setAttribute(Xml.KEY_OBJ, Xml.toString(changeEvent.getObject()));
		element.setAttribute(Xml.KEY_VERSION, Xml.toString(changeEvent.getVersion()));
		element.setAttribute(Xml.KEY_TEXT, Xml.toString(changeEvent.getText()));

	}

	/**
	 * @param connection
	 * @param element
	 */
	private static void writeConnection(Connection connection, Element element) {
		element.setAttribute(Xml.KEY_DESCRIPTION, connection.getDescription());
		element.setAttribute(Xml.KEY_SO, Xml.toString(connection.getSozialStatus()));
		element.setAttribute(Xml.KEY_NAME, connection.getName());
	}

	/**
	 * @param huntingWeapons
	 * @param element
	 */
	private static void writeHuntingWeapon(HuntingWeapon huntingWeapon, Element element) {
		element.setAttribute(Xml.KEY_SET, Xml.toString(huntingWeapon.getSet()));
		element.setAttribute(Xml.KEY_NUMMER, Xml.toString(huntingWeapon.getNumber()));
		element.setAttribute(Xml.KEY_NAME, Hero.JAGTWAFFE);
	}
}
