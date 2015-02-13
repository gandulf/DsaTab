package com.dsatab.test;

import java.io.InputStream;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import android.test.InstrumentationTestCase;

import com.dsatab.cloud.HeroExchange.StorageType;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.Hand;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Weapon;
import com.dsatab.util.Util;
import com.dsatab.xml.HeldenXmlParser;

public class XmlParserTest extends InstrumentationTestCase {

	XPathFactory factory;

	/**
	 * 
	 */
	public XmlParserTest() {
		factory = XPathFactory.instance();
	}

	protected Integer attrIntegerValue(String xpath, Document document) {
		return Util.parseInteger(attrValue(xpath, document));
	}

	protected String attrValue(String xpath, Document document) {
		XPathExpression<Attribute> xp = factory.compile(xpath, Filters.attribute());
		return xp.evaluateFirst(document).getValue();

	}

	public void testHeroLoading() throws Exception {

		final int TEST_GS = 5;
		final int TEST_LP = 28;
		final int TEST_AU = 38;
		final int TEST_AE = 0;
		final int TEST_KE = 0;
		final int TEST_MR = 4;
		final int TEST_AT = 8;
		final int TEST_PA = 8;
		final int TEST_FK = 8;
		final int TEST_INI = 11;
		final int TEST_BE = 2;
		final int TEST_RS = 3;
		final int TEST_AW = 7;

		// in = Global.gContext.openFileInput("testxml.xml");
		InputStream in = getInstrumentation().getContext().getAssets().open("minarax_menschenfreund.xml");

		Document dom = HeldenXmlParser.readDocument(in);
		in.close();

		in = getInstrumentation().getContext().getAssets().open("minarax_menschenfreund.xml");

		HeroFileInfo fileInfo = new HeroFileInfo("Minarax Menschenfreund", "0", "minar");
		fileInfo.setStorageType(StorageType.FileSystem);
		Hero hero = HeldenXmlParser.readHero(getInstrumentation().getTargetContext(), fileInfo, in, null);
		in.close();

		assertNotNull(hero);
		assertEquals(attrValue("/helden/held/@name", dom), hero.getName());
		assertEquals(attrValue("/helden/held/basis/ausbildungen/ausbildung/@string", dom), hero.getBaseInfo()
				.getAusbildung());

		for (AttributeType type : AttributeType.EIGENSCHAFTEN) {
			assertEquals(
					(int) attrIntegerValue(
							"/helden/held/eigenschaften/eigenschaft[@name='" + type.name() + "']/@value", dom)
							+ attrIntegerValue("/helden/held/eigenschaften/eigenschaft[@name='" + type.name()
									+ "']/@mod", dom), (int) hero.getAttributeValue(type));
		}

		assertEquals(TEST_LP, (int) hero.getAttributeValue(AttributeType.Lebensenergie_Aktuell));
		assertEquals(TEST_LP, (int) hero.getAttributeValue(AttributeType.Lebensenergie));
		assertEquals(TEST_AU, (int) hero.getAttributeValue(AttributeType.Ausdauer_Aktuell));
		assertEquals(TEST_AU, (int) hero.getAttributeValue(AttributeType.Ausdauer));
		assertEquals(TEST_AE, (int) hero.getAttributeValue(AttributeType.Astralenergie_Aktuell));
		assertEquals(TEST_AE, (int) hero.getAttributeValue(AttributeType.Astralenergie));
		assertEquals(TEST_KE, (int) hero.getAttributeValue(AttributeType.Karmaenergie_Aktuell));
		assertEquals(TEST_KE, (int) hero.getAttributeValue(AttributeType.Karmaenergie));

		assertEquals(TEST_MR, (int) hero.getAttributeValue(AttributeType.Magieresistenz));

		assertEquals(
				(int) attrIntegerValue(
						"/helden/held/eigenschaften/eigenschaft[@name='" + AttributeType.Sozialstatus.name()
								+ "']/@value", dom)
						+ attrIntegerValue("/helden/held/eigenschaften/eigenschaft[@name='"
								+ AttributeType.Sozialstatus.name() + "']/@mod", dom),
				(int) hero.getAttributeValue(AttributeType.Sozialstatus));

		assertEquals(TEST_AT, (int) hero.getAttributeValue(AttributeType.at));
		assertEquals(TEST_PA, (int) hero.getAttributeValue(AttributeType.pa));
		assertEquals(TEST_FK, (int) hero.getAttributeValue(AttributeType.fk));
		assertEquals(TEST_INI, (int) hero.getAttributeValue(AttributeType.ini));

		assertEquals(attrIntegerValue("/helden/held/basis/abenteuerpunkte/@value", dom), hero.getExperience()
				.getValue());
		assertEquals(attrIntegerValue("/helden/held/basis/freieabenteuerpunkte/@value", dom), hero.getFreeExperience()
				.getValue());

		assertEquals(TEST_BE, (int) hero.getAttributeValue(AttributeType.Behinderung));

		assertEquals(TEST_RS, (int) hero.getArmorRs());

		assertEquals(TEST_GS, (int) hero.getModifiedValue(AttributeType.Geschwindigkeit, true, true));

		assertTrue(hero.hasFeature(FeatureType.Dämmerungssicht));
		assertTrue(hero.hasFeature(FeatureType.GutesGedächtnis));
		assertTrue(hero.hasFeature(FeatureType.Zwergenwuchs));

		assertTrue(hero.hasFeature(FeatureType.Eitelkeit));
		assertEquals(7, (int) Util.parseInt(hero.getFeature(FeatureType.Eitelkeit).getValue()));

		assertTrue(hero.hasFeature(FeatureType.RüstungsgewöhnungI));
		assertEquals("Fünflagenharnisch", hero.getFeature(FeatureType.RüstungsgewöhnungI).getValue());

		assertTrue(hero.hasFeature(FeatureType.Kulturkunde));

		assertEquals(TEST_AW, (int) hero.getModifiedValue(AttributeType.Ausweichen, true, true));

		assertEquals(21, (int) hero.getCombatTalent(TalentType.Armbrust).getValue());

		assertEquals(attrIntegerValue("/helden/held/talentliste/talent[@name='Dolche']/@value", dom), hero
				.getCombatTalent(TalentType.Dolche).getValue());

		assertEquals(attrIntegerValue("/helden/held/talentliste/talent[@name='Akrobatik']/@value", dom), hero
				.getTalent(TalentType.Akrobatik).getValue());

		assertEquals(attrIntegerValue("/helden/held/kampf/kampfwerte[@name='Dolche']/attacke/@value", dom), hero
				.getCombatTalent(TalentType.Dolche).getAttack().getValue());
		assertEquals(attrIntegerValue("/helden/held/kampf/kampfwerte[@name='Dolche']/parade/@value", dom), hero
				.getCombatTalent(TalentType.Dolche).getDefense().getValue());

		EquippedItem langSchwert = hero.getEquippedItem(0, "nkwaffe1");
		assertEquals(11,
				langSchwert.getCombatProbeAttacke().getValue() + hero.getModifier(langSchwert.getCombatProbeAttacke()));
		assertEquals(16,
				langSchwert.getCombatProbeDefense().getValue() + hero.getModifier(langSchwert.getCombatProbeDefense()));
		assertEquals(TalentType.Schwerter, langSchwert.getCombatProbeAttacke().getCombatTalent().getType());
		assertEquals(TalentType.Schwerter, langSchwert.getCombatProbeDefense().getCombatTalent().getType());
		assertTrue(langSchwert.getItemSpecification() instanceof Weapon);
		assertEquals(hero.getCombatTalent(TalentType.Schwerter), langSchwert.getTalent());
		assertEquals(Hand.rechts, langSchwert.getHand());

		EquippedItem drachenzahn = hero.getEquippedItem(0, "nkwaffe2");
		assertEquals(-1,
				drachenzahn.getCombatProbeAttacke().getValue() + hero.getModifier(drachenzahn.getCombatProbeAttacke()));
		assertEquals(-1,
				drachenzahn.getCombatProbeDefense().getValue() + hero.getModifier(drachenzahn.getCombatProbeDefense()));
		assertEquals(TalentType.Dolche, drachenzahn.getCombatProbeAttacke().getCombatTalent().getType());
		assertEquals(TalentType.Dolche, drachenzahn.getCombatProbeDefense().getCombatTalent().getType());
		assertTrue(drachenzahn.getItemSpecification() instanceof Weapon);
		assertEquals(hero.getCombatTalent(TalentType.Dolche), drachenzahn.getTalent());
		assertEquals(Hand.links, drachenzahn.getHand());

	}
}
