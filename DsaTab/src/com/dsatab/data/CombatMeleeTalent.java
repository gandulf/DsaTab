package com.dsatab.data;

import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.enums.Position;
import com.dsatab.data.enums.TalentType;

public class CombatMeleeTalent extends BaseCombatTalent {

	private CombatMeleeAttribute pa;

	private CombatMeleeAttribute at;

	public CombatMeleeTalent(Hero hero, CombatMeleeAttribute at, CombatMeleeAttribute pa) {
		super(hero);
		if (at != null)
			at.setCombatMeleeTalent(this);
		if (pa != null)
			pa.setCombatMeleeTalent(this);
		this.at = at;
		this.pa = pa;
	}

	public void setType(TalentType type) {
		super.setType(type);

		// we have to set the talent again to refresh some values
		if (at != null)
			at.setCombatMeleeTalent(this);
		if (pa != null)
			pa.setCombatMeleeTalent(this);
	}

	public CombatMeleeAttribute getAttack() {
		return at;
	}

	public CombatMeleeAttribute getDefense() {
		return pa;
	}

	public Position getPosition(int w20) {

		if (DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_MORE_TARGET_ZONES,
				false)) {

			switch (type) {

			case Dolche:
			case Fechtwaffen:
				return Position.messer_dolch_stich[w20];
			case Hiebwaffen:
			case Kettenwaffen:
			case Kettenstäbe:
				return Position.hieb_ketten[w20];
			case Schwerter:
			case Säbel:
				return Position.schwert_saebel[w20];
			case Speere:
				return Position.stangen_zweih_stich[w20];
			case Stäbe:
			case Zweihandflegel:
			case Anderthalbhänder:
			case Infanteriewaffen:
			case Zweihandhiebwaffen:
			case Zweihandschwertersäbel:
				return Position.stangen_zweih_hieb[w20];
			case Raufen:
			case Ringen:
				return Position.box_rauf_hruru[w20];
			case Peitsche:
				return Position.fern_wurf[w20];
			default:
				return Position.fern_wurf[w20];
			}
		} else {
			return Position.official[w20];
		}
	}

	@Override
	public String toString() {
		return getName();
	}

}
