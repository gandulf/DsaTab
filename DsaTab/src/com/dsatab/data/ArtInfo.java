package com.dsatab.data;

import java.io.Serializable;

import android.text.TextUtils;

import com.dsatab.util.Util;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "art")
public class ArtInfo implements Serializable {

	private static final long serialVersionUID = -5634714445422591166L;

	private static final String[] LITURGIE_COSTS = { "2 KaP", "5 KaP", "10 KaP", "15 KaP", "20 KaP", "25 KaP/ 1 pKap",
			"30 KaP/ 3 pKaP", "35 Kap/ 5 pKap", "40 KaP/ 7 pKaP", "45 KaP/ 9 pKaP" };

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField
	private String name;
	@DatabaseField
	private String source;
	@DatabaseField
	private String probe;
	@DatabaseField
	private String target;
	@DatabaseField
	private String range;
	@DatabaseField
	private String merkmale;
	@DatabaseField
	private String castDuration;
	@DatabaseField
	private String costs;
	@DatabaseField
	private String effect;
	@DatabaseField
	private String effectDuration;
	@DatabaseField
	private String origin;
	@DatabaseField
	private int grade;

	/**
	 * 
	 */
	public ArtInfo() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		if (grade >= 0)
			return name + " " + Util.intToGrade(grade);
		else
			return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getProbe() {
		return probe;
	}

	public void setProbe(String probe) {
		this.probe = probe;
	}

	public String getTarget() {
		return target;
	}

	public String getMerkmale() {
		return merkmale;
	}

	public void setMerkmale(String merkmale) {
		this.merkmale = merkmale;
	}

	public void setCosts(String costs) {
		this.costs = costs;
	}

	public String getTargetDetailed() {
		if (!TextUtils.isEmpty(target)) {
			if (target.equals("G"))
				return "der Geweihter selbst";
			else if (target.equals("S"))
				return "der Schamane selbst";
			else if (target.equals("P"))
				return "einzelne Zielperson oder Objekt";
			else if (target.equals("PP"))
				return "bis zu 10 Personen/Objekte";
			else if (target.equals("PPP"))
				return "10 bis 100 Personen/Objekte";
			else if (target.equals("PPPP"))
				return "100 bis 1000 Personen/Objekte";
			else if (target.equals("Z"))
				return "Zone von ca. 10 Schritt Radius";
			else if (target.equals("ZZ"))
				return "Zone von ca. 30 Schritt Radius";
			else if (target.equals("ZZZ"))
				return "Zone von ca. 100 Schritt Radius";
			else if (target.equals("ZZZZ"))
				return "Zone von ca. 1000 Schritt Radius";
			else
				return target;
		} else {
			return null;
		}
	}

	public String getCosts() {
		if (TextUtils.isEmpty(costs) && grade >= 0) {
			return LITURGIE_COSTS[grade];
		} else {
			return costs;
		}
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getRange() {
		return range;
	}

	public String getRangeDetailed() {
		if (!TextUtils.isEmpty(range)) {
			if (range.equals("s"))
				return "der Geweihte selbst";
			else if (range.equals("B"))
				return "Berührung";
			else if (range.equals("s,B"))
				return "der Geweithe selbst oder Berührung";
			else if (range.equals("F"))
				return "Wirkung tritt an einemn frei wählbaren Ort ein";
			else if (range.equals("S"))
				return "beliebiger Ort innerhalb des Sichtfeldes";
			else
				return range;
		} else {
			return null;
		}
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getCastDuration() {
		return castDuration;
	}

	public String getCastDurationDetailed() {
		if (!TextUtils.isEmpty(castDuration)) {
			if (castDuration.equals("SR"))
				return "Spontanritual (5-15 Aktionen)";
			else if (castDuration.equals("G"))
				return "Gebet (ca. 1 Spielrunde)";
			else if (castDuration.equals("KR"))
				return "Kurzritual (ca. 1 Spielrunde)";
			else if (castDuration.equals("R"))
				return "Ritual (ca. halbe Stunde)";
			else if (castDuration.equals("A"))
				return "Andacht (ca. halbe Stunde)";
			else if (castDuration.equals("GR"))
				return "Großritual (mehrere Stunden)";
			else if (castDuration.equals("Ze"))
				return "Zeremonie (mehrere Stunden)";
			else if (castDuration.equals("Zy"))
				return "Zyklus (an mehrere Tagen wiederholte Andacht)";
			else if (castDuration.equals("ZR"))
				return "Zyklus (an mehrere Tagen wiederholtes Ritual)";
			else if (castDuration.startsWith("S")) {
				return "Stoßgebet (" + castDuration + " Aktionen)";
			} else
				return castDuration;
		} else {
			return null;
		}
	}

	public void setCastDuration(String castDuration) {
		this.castDuration = castDuration;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public String getEffectDuration() {
		return effectDuration;
	}

	public void setEffectDuration(String effectDuration) {
		this.effectDuration = effectDuration;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
