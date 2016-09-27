package com.dsatab.fragment;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.dsatab.activity.HeroChooserActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.Hand;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Weapon;
import com.dsatab.util.Util;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HeroChooserFragmentTest extends ActivityInstrumentationTestCase2<HeroChooserActivity> {

    private HeroChooserActivity mActivity;

    public HeroChooserFragmentTest() {
        super(HeroChooserActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Injecting the Instrumentation instance is required for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        mActivity = getActivity();
    }

    @Test
    @Ignore
    public void testHeroLoading() throws Exception {

        HeroChooserFragment fragment = mActivity.getFragment();
        HeroFileInfo dummy;

        fragment.loadExampleHeroes();
        fragment.refresh(HeroChooserFragment.LOCAL_LOADER);

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

        // Espresso.onView(ViewMatchers.withId(R.id.option_load_example_heroes)).perform(ViewActions.click());


        //TODO load dummy hero
        Hero hero = null;

        assertNotNull(hero);
        assertEquals("Dummy", hero.getName());
        assertEquals("Anatomische Akademie zu Vinsalt", hero.getBaseInfo().getAusbildung());

        assertEquals(12, (int) hero.getAttributeValue(AttributeType.Mut));
        assertEquals(TEST_LP, (int) hero.getAttributeValue(AttributeType.Lebensenergie_Aktuell));
        assertEquals(TEST_LP, (int) hero.getAttributeValue(AttributeType.Lebensenergie));
        assertEquals(TEST_AU, (int) hero.getAttributeValue(AttributeType.Ausdauer_Aktuell));
        assertEquals(TEST_AU, (int) hero.getAttributeValue(AttributeType.Ausdauer));
        assertEquals(TEST_AE, (int) hero.getAttributeValue(AttributeType.Astralenergie_Aktuell));
        assertEquals(TEST_AE, (int) hero.getAttributeValue(AttributeType.Astralenergie));
        assertEquals(TEST_KE, (int) hero.getAttributeValue(AttributeType.Karmaenergie_Aktuell));
        assertEquals(TEST_KE, (int) hero.getAttributeValue(AttributeType.Karmaenergie));

        assertEquals(TEST_MR, (int) hero.getAttributeValue(AttributeType.Magieresistenz));

        assertEquals(TEST_AT, (int) hero.getAttributeValue(AttributeType.at));
        assertEquals(TEST_PA, (int) hero.getAttributeValue(AttributeType.pa));
        assertEquals(TEST_FK, (int) hero.getAttributeValue(AttributeType.fk));
        assertEquals(TEST_INI, (int) hero.getAttributeValue(AttributeType.ini));

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

        assertEquals(1,(int) hero.getCombatTalent(TalentType.Dolche).getValue());

        assertEquals(7,(int) hero.getCombatTalent(TalentType.Dolche).getAttack().getValue());
        assertEquals(6,(int) hero.getCombatTalent(TalentType.Dolche).getDefense().getValue());

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
