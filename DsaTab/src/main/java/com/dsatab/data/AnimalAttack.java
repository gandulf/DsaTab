package com.dsatab.data;

import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.enums.Position;
import com.dsatab.data.enums.TalentType;

import java.io.Serializable;

public class AnimalAttack implements CombatTalent, Serializable {

	private static final long serialVersionUID = -3715752247828889650L;

	private String name;

	private CombatMeleeAttribute pa;

	private CombatMeleeAttribute at;

	private String distance;

	private Dice tp;

	public AnimalAttack(String name, CombatMeleeAttribute at, CombatMeleeAttribute pa, Dice tpdice, String distance) {

		this.name = name;

		if (at != null)
			at.setCombatMeleeTalent(null);
		if (pa != null)
			pa.setCombatMeleeTalent(null);
		this.at = at;
		this.pa = pa;
		this.tp = tpdice;
		this.distance = distance;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public Dice getTp() {
		return tp;
	}

	public void setTp(Dice tp) {
		this.tp = tp;
	}

	@Override
	public TalentType getType() {
		return TalentType.Raufen;
	}

	@Override
	public CombatMeleeAttribute getAttack() {
		return at;
	}

	@Override
	public CombatMeleeAttribute getDefense() {
		return pa;
	}

	public String getName() {
		return name;
	}

	public String getInfo() {
		return tp.toString() + " " + getDistance();
	}

	@Override
	public Position getPosition(int w20) {

		if (DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_MORE_TARGET_ZONES,
				false)) {
			return Position.box_rauf_hruru[w20];
		} else {
			return Position.official[w20];
		}
	}

	@Override
	public String toString() {
		return getName();
	}

}
