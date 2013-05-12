package com.dsatab.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.modifier.RulesModificator;
import com.gandulf.guilib.util.Debug;

public class RulesParser {

	private Context context;
	private Hero hero;

	private List<AttributeType> affectedAttributeTypes = new ArrayList<AttributeType>();
	private List<String> affectedTalentNames = new ArrayList<String>();
	private List<String> affectedSpellNames = new ArrayList<String>();
	private List<String> affectedArtNames = new ArrayList<String>();
	private List<TalentType> affectedTalentTypes = new ArrayList<TalentType>();
	private List<String> affectedItemSpecifications = new ArrayList<String>();
	private List<FeatureType> requiredSpecialFeatures = new ArrayList<FeatureType>();
	private List<FeatureType> excludeSpecialFeatures = new ArrayList<FeatureType>();
	private List<String> requiredTalentNames = new ArrayList<String>();

	/**
	 * 
	 */
	public RulesParser(Context context, Hero hero) {
		this.context = context;
		this.hero = hero;
	}

	public List<RulesModificator> parseRules() {

		List<RulesModificator> rules = new ArrayList<RulesModificator>();

		XmlResourceParser xml = context.getResources().getXml(R.xml.rules);
		try {
			int eventType = xml.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if ((eventType == XmlPullParser.START_TAG) && (xml.getName().equals("rule"))) {
					RulesModificator rule = parseRulesTag(xml);
					rules.add(rule);
				}
				eventType = xml.next();
			}
		} catch (XmlPullParserException e) {
			Debug.error(e);
		} catch (IOException e) {
			Debug.error(e);

		} finally {
			xml.close();
		}

		return rules;
	}

	// Parse a the release tag and return html code
	private RulesModificator parseRulesTag(XmlResourceParser aXml) throws XmlPullParserException, IOException {

		String title = null, description = null, modifierExpression = null;
		Integer modifier = null;
		boolean dynamic = false;

		affectedAttributeTypes.clear();
		affectedTalentNames.clear();
		affectedArtNames.clear();
		affectedSpellNames.clear();
		affectedTalentTypes.clear();
		affectedItemSpecifications.clear();
		requiredSpecialFeatures.clear();
		excludeSpecialFeatures.clear();
		requiredTalentNames.clear();

		int eventType = aXml.getEventType();
		while (eventType != XmlPullParser.END_TAG || !aXml.getName().equals("rule")) {

			if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("rule"))) {
				title = aXml.getAttributeValue(null, "title");
				description = aXml.getAttributeValue(null, "description");
				modifier = aXml.getAttributeIntValue(null, "modifier", 0);
				modifierExpression = aXml.getAttributeValue(null, "expression");
				dynamic = aXml.getAttributeBooleanValue(null, "dynamic", false);
			}

			if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("affected"))) {
				eventType = aXml.next();
				while (eventType != XmlPullParser.END_TAG || !aXml.getName().equals("affected")) {
					if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("attribute"))) {
						affectedAttributeTypes.add(AttributeType.byCode(aXml.getAttributeValue(null, "code")));
					} else if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("talent"))) {
						affectedTalentNames.add(aXml.getAttributeValue(null, "name"));
					} else if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("spell"))) {
						affectedSpellNames.add(aXml.getAttributeValue(null, "name"));
					} else if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("art"))) {
						affectedArtNames.add(aXml.getAttributeValue(null, "name"));
					} else if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("combattalent"))) {
						affectedTalentTypes.add(TalentType.byXmlName(aXml.getAttributeValue(null, "name")));
					} else if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("combatprobe"))) {
						affectedItemSpecifications.add(aXml.getAttributeValue(null, "type"));
					}
					eventType = aXml.next();
				}
			}
			if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("required"))) {
				eventType = aXml.next();
				while (eventType != XmlPullParser.END_TAG || !aXml.getName().equals("required")) {
					if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("specialfeature"))) {

						if (aXml.getAttributeBooleanValue(null, "exclude", false)) {
							excludeSpecialFeatures.add(FeatureType.byXmlName(aXml.getAttributeValue(null, "name")));
						} else {
							requiredSpecialFeatures.add(FeatureType.byXmlName(aXml.getAttributeValue(null, "name")));
						}
					}
					if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("talent"))) {
						requiredTalentNames.add(aXml.getAttributeValue(null, "name"));
					}
					eventType = aXml.next();
				}
			}
			if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("modifier"))) {
				eventType = aXml.next();
				modifierExpression = aXml.getText();
			}

			eventType = aXml.next();
		}

		RulesModificator rule = new RulesModificator(hero, title, description, modifier);
		rule.setDynamic(dynamic);
		rule.setModifierExpression(modifierExpression);
		if (!affectedAttributeTypes.isEmpty())
			rule.setAffectedAttributeTypes(affectedAttributeTypes);
		if (!affectedTalentTypes.isEmpty())
			rule.setAffectedTalentTypes(affectedTalentTypes);
		if (!affectedTalentNames.isEmpty())
			rule.setAffectedTalentNames(affectedTalentNames);
		if (!affectedSpellNames.isEmpty())
			rule.setAffectedSpellNames(affectedSpellNames);
		if (!affectedArtNames.isEmpty())
			rule.setAffectedArtNames(affectedArtNames);
		if (!excludeSpecialFeatures.isEmpty())
			rule.setExcludeSpecialFeatures(excludeSpecialFeatures);
		if (!requiredSpecialFeatures.isEmpty())
			rule.setRequiredSpecialFeatures(requiredSpecialFeatures);
		if (!requiredTalentNames.isEmpty())
			rule.setRequiredTalentNames(requiredTalentNames);
		if (!affectedItemSpecifications.isEmpty())
			rule.setAffectedItemSpecifications(affectedItemSpecifications);

		return rule;
	}
}
