package com.dsatab.data.modifier;

import com.dsatab.data.BaseCombatTalent;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.Weapon;
import com.dsatab.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Rules {

	private static class RulesModificatorAusdauernd extends RulesModificator {

		public RulesModificatorAusdauernd(Hero hero) {
			super(hero, "Ausdauernd", "Wache Ausdauernd TaW/3", 0);

			setAffectedTalentTypes(TalentType.WacheHalten);
			setRequiredSpecialFeatures(FeatureType.Ausdauernd);
		}

		protected void handleModifierExpression(com.dsatab.data.Probe probe, AttributeType type) {
			modifierObject.setModifier(Util.parseInt(hero.getFeature(FeatureType.Ausdauernd).getValue()) / 3);
		}
	}

	private static class RulesModificatorGefahreninstinkt extends RulesModificator {

		public RulesModificatorGefahreninstinkt(Hero hero) {
			super(hero, "Gefahreninstinkt", "Wache Gefahreninstinkt TaW/2", 0);

			setAffectedTalentTypes(TalentType.WacheHalten);
			setRequiredSpecialFeatures(FeatureType.Gefahreninstinkt);
		}

		protected void handleModifierExpression(com.dsatab.data.Probe probe, AttributeType type) {
			modifierObject.setModifier(hero.getTalent(TalentType.Gefahreninstinkt).getValue() / 2);
		}
	}

	private static class RulesModificatorTalentSpezialisierungFernkampf extends RulesModificator {

		public RulesModificatorTalentSpezialisierungFernkampf(Hero hero) {
			super(hero, "Talentspezialisierung", "Fernkampf Talentspezialisierung +2", 2);
			setAffectedItemSpecifications(DistanceWeapon.class.getSimpleName());
		}

		protected void handleModifierExpression(com.dsatab.data.Probe probe, AttributeType type) {

			modifierObject.setModifier(0);
			if (probe instanceof CombatProbe) {
				CombatProbe combatProbe = (CombatProbe) probe;
				if (combatProbe.getCombatTalent() instanceof BaseCombatTalent) {
					BaseCombatTalent talent = (BaseCombatTalent) combatProbe.getCombatTalent();
					Item item = combatProbe.getEquippedItem().getItem();

					if (talent != null && talent.getTalentSpezialisierung() != null
							&& talent.getTalentSpezialisierung().equalsIgnoreCase(item.getName())) {
						modifierObject.setModifier(2);
					}
				}
			}
		}

	}

	private static class JagdwaffeReichweite extends RulesModificator {

		public JagdwaffeReichweite(Hero hero) {
			super(hero, "Jagdwaffe Reichweite", "Pirsch- und Ansitzjagd Jagdwaffe Reichweite", 0);
			setAffectedTalentTypes(TalentType.PirschUndAnsitzjagd);
			setDynamic(true);
		}

		protected void handleModifierExpression(com.dsatab.data.Probe probe, AttributeType type) {
			modifierObject.setModifier(0);

			EquippedItem huntingWeapon = hero.getHuntingWeapon();
			if (huntingWeapon != null && huntingWeapon.getItemSpecification() instanceof DistanceWeapon) {
				int maxDistance = ((DistanceWeapon) (huntingWeapon.getItemSpecification())).getMaxDistance();
				if (maxDistance <= 20) {
					modifierObject.setModifier(-7);
				} else if (maxDistance <= 50) {
					modifierObject.setModifier(-3);
				}
			}
		}
	}

	private static class RulesModificatorTalentSpezialisierungNahkampf extends RulesModificator {

		public RulesModificatorTalentSpezialisierungNahkampf(Hero hero) {
			super(hero, "Talentspezialisierung", "Nahkampf Talentspezialisierung +1", 1);

			setAffectedItemSpecifications(Weapon.class.getSimpleName());
		}

		protected void handleModifierExpression(com.dsatab.data.Probe probe, AttributeType type) {

			modifierObject.setModifier(0);
			if (probe instanceof CombatProbe) {
				CombatProbe combatProbe = (CombatProbe) probe;
				if (combatProbe.getCombatTalent() instanceof BaseCombatTalent) {
					BaseCombatTalent talent = (BaseCombatTalent) combatProbe.getCombatTalent();
					Item item = combatProbe.getEquippedItem().getItem();

					if (talent != null && talent.getTalentSpezialisierung() != null
							&& talent.getTalentSpezialisierung().equalsIgnoreCase(item.getName())) {
						modifierObject.setModifier(1);
					}
				}
			}
		}

	}

	public static List<RulesModificator> prepareRules(Hero hero) {

		List<RulesModificator> rules = new ArrayList<RulesModificator>();

		rules.add(new RulesModificatorAusdauernd(hero));
		rules.add(new RulesModificatorGefahreninstinkt(hero));
		rules.add(new RulesModificatorTalentSpezialisierungFernkampf(hero));
		rules.add(new RulesModificatorTalentSpezialisierungNahkampf(hero));
		rules.add(new JagdwaffeReichweite(hero));

		RulesModificator kampfgespuehr = new RulesModificator(hero, "Kampfgespür", "Kampfgespür INI +2", 2);
		kampfgespuehr.setAffectedAttributeTypes(AttributeType.ini);
		kampfgespuehr.setRequiredSpecialFeatures(FeatureType.Kampfgespür);
		rules.add(kampfgespuehr);

		RulesModificator kampfreflexe = new RulesModificator(hero, "Kampfreflexe", "Kampfreflexe INI +4", 4);
		kampfreflexe.setAffectedAttributeTypes(AttributeType.ini);
		kampfreflexe.setRequiredSpecialFeatures(FeatureType.Kampfreflexe);
		rules.add(kampfreflexe);

		RulesModificator waffenlosKampfstilRaufen = new RulesModificator(hero, "Waffenloser Kampfstil",
				"Raufen Waffenloser Kampfstil +1/+1", 1);
		waffenlosKampfstilRaufen.setAffectedTalentTypes(TalentType.Raufen);

		waffenlosKampfstilRaufen.setRequiredSpecialFeatures(FeatureType.WaffenloserKampfstilGladiatorenstil,
				FeatureType.WaffenloserKampfstilGladiatorenstilDDZ, FeatureType.WaffenloserKampfstilHammerfaust,
				FeatureType.WaffenloserKampfstilHruruzat, FeatureType.WaffenloserKampfstilMercenario);
		rules.add(waffenlosKampfstilRaufen);

		RulesModificator waffenlosKampfstilRingen = new RulesModificator(hero, "Waffenloser Kampfstil",
				"Ringen Waffenloser Kampfstil +1/+1", 1);
		waffenlosKampfstilRingen.setAffectedTalentTypes(TalentType.Ringen);
		waffenlosKampfstilRingen.setRequiredSpecialFeatures(FeatureType.WaffenloserKampfstilUnauerSchule,
				FeatureType.WaffenloserKampfstilBornländisch, FeatureType.WaffenloserKampfstilGladiatorenstil,
				FeatureType.WaffenloserKampfstilGladiatorenstilDDZ);
		rules.add(waffenlosKampfstilRingen);

		RulesModificator meisterschuetze = new RulesModificator(hero, "Meisterschütze",
				"Pirsch- und Ansitzjagd Meisterschütze +7", 7);
		meisterschuetze.setAffectedTalentTypes(TalentType.PirschUndAnsitzjagd);
		meisterschuetze.setRequiredSpecialFeatures(FeatureType.Meisterschütze);
		rules.add(meisterschuetze);

		RulesModificator scharfschuetze = new RulesModificator(hero, "Scharfschütze",
				"Pirsch- und Ansitzjagd Scharfschütze +3", 3);
		scharfschuetze.setAffectedTalentTypes(TalentType.PirschUndAnsitzjagd);
		scharfschuetze.setRequiredSpecialFeatures(FeatureType.Scharfschütze);
		scharfschuetze.setExcludeSpecialFeatures(FeatureType.Meisterschütze);
		rules.add(scharfschuetze);

		RulesModificator aufmerksamkeit = new RulesModificator(hero, "Aufmerksamkeit", "Wache Aufmerksamkeit +1", 1);
		aufmerksamkeit.setAffectedTalentTypes(TalentType.WacheHalten);
		aufmerksamkeit.setRequiredSpecialFeatures(FeatureType.Aufmerksamkeit);
		rules.add(aufmerksamkeit);

		RulesModificator daemmersicht = new RulesModificator(hero, "Dämmerungssicht", "Wache Dämmerungssicht +1", 1);
		daemmersicht.setAffectedTalentTypes(TalentType.WacheHalten);
		daemmersicht.setRequiredSpecialFeatures(FeatureType.Dämmerungssicht);
		rules.add(daemmersicht);

		RulesModificator nachtsicht = new RulesModificator(hero, "Nachtsicht", "Wache Nachtsicht +3", 3);
		nachtsicht.setAffectedTalentTypes(TalentType.WacheHalten);
		nachtsicht.setRequiredSpecialFeatures(FeatureType.Nachtsicht);
		rules.add(nachtsicht);

		RulesModificator hsinn = new RulesModificator(hero, "Herausragender Sinn", "Wache Herausragender Sinn +1", 1);
		hsinn.setAffectedTalentTypes(TalentType.WacheHalten);
		hsinn.setRequiredSpecialFeatures(FeatureType.HerausragenderSinn);
		rules.add(hsinn);

		RulesModificator einaug = new RulesModificator(hero, "Einäugig", "Wache Einäugig -2", -2);
		hsinn.setAffectedTalentTypes(TalentType.WacheHalten);
		hsinn.setRequiredSpecialFeatures(FeatureType.Einäugig);
		rules.add(einaug);

		RulesModificator einbildungen = new RulesModificator(hero, "Einbildungen", "Wache Einbildungen -2", -2);
		einbildungen.setAffectedTalentTypes(TalentType.WacheHalten);
		einbildungen.setRequiredSpecialFeatures(FeatureType.Einbildungen);
		rules.add(einbildungen);

		RulesModificator dunkelangst = new RulesModificator(hero, "Dunkelangst", "Wache Dunkelangst -3", -3);
		dunkelangst.setAffectedTalentTypes(TalentType.WacheHalten);
		dunkelangst.setRequiredSpecialFeatures(FeatureType.Dunkelangst);
		rules.add(dunkelangst);

		RulesModificator nachtblind = new RulesModificator(hero, "Nachtblind", "Wache Nachtblind -3", -3);
		nachtblind.setAffectedTalentTypes(TalentType.WacheHalten);
		nachtblind.setRequiredSpecialFeatures(FeatureType.Nachtblind);
		rules.add(nachtblind);

		RulesModificator unstet = new RulesModificator(hero, "Unstet", "Wache Unstet -2", -2);
		unstet.setAffectedTalentTypes(TalentType.WacheHalten);
		unstet.setRequiredSpecialFeatures(FeatureType.Nachtblind);
		rules.add(unstet);

		RulesModificator entfernsinn = new RulesModificator(hero, "Entfernungssinn", "Fernkampf Entfernungssinn +2", 2);
		entfernsinn.setAffectedItemSpecifications(DistanceWeapon.class.getSimpleName());
		entfernsinn.setRequiredSpecialFeatures(FeatureType.Entfernungssinn);
		rules.add(entfernsinn);

		RulesModificator eisern = new RulesModificator(hero, "Eisern", "Wundschwelle Eisern +2", 2);
		eisern.setAffectedAttributeTypes(AttributeType.Wundschwelle);
		eisern.setRequiredSpecialFeatures(FeatureType.Eisern);
		rules.add(eisern);

		RulesModificator glasknochen = new RulesModificator(hero, "Glasknochen", "Wundschwelle Glasknochen -2", -2);
		glasknochen.setAffectedAttributeTypes(AttributeType.Wundschwelle);
		glasknochen.setRequiredSpecialFeatures(FeatureType.Glasknochen);
		rules.add(glasknochen);

		return rules;
	}

}
