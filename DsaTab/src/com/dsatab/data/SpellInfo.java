/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.data;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Ganymede
 * 
 */
@DatabaseTable(tableName = "spell")
public class SpellInfo {

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField
	private String name;
	@DatabaseField
	private String source;
	@DatabaseField
	private String probe;
	@DatabaseField
	private String complexity;
	@DatabaseField
	private String target;
	@DatabaseField
	private String range;
	@DatabaseField
	private String merkmale;
	@DatabaseField
	private String castDuration;
	@DatabaseField
	private String effect;
	@DatabaseField
	private String effectDuration;
	@DatabaseField
	private String representation;
	@DatabaseField
	private String costs;

	@DatabaseField
	private String comments;
	@DatabaseField
	private String variant;

	/**
	 * 
	 */
	public SpellInfo() {

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

	public void setName(String name) {
		this.name = name;
	}

	public String getMerkmale() {
		return merkmale;
	}

	public void setMerkmale(String merkmale) {
		this.merkmale = merkmale;
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

	public String getTargetDetailed() {
		if (!TextUtils.isEmpty(target)) {
			if (target.equals("S"))
				return "der Zauberer selbst";
			else if (target.equals("P"))
				return "einzelne Person";
			else if (target.equals("PP"))
				return "bis zu 10 Personen";
			else if (target.equals("PPP"))
				return "10 bis 100 Personen";
			else if (target.equals("PPPP"))
				return "100 bis 1000 Personen";
			else if (target.equals("W"))
				return "einzelnes Wesen";
			else if (target.equals("WW"))
				return "bis zu 10 Wesen";
			else if (target.equals("WWW"))
				return "10 bis 100 Wesen";
			else if (target.equals("WWWW"))
				return "100 bis 1000 Wesen";
			else if (target.equals("O"))
				return "einzelnes Objekt";
			else if (target.equals("OO"))
				return "bis zu 10 Objekte";
			else if (target.equals("OOO"))
				return "10 bis 100 Objekte";
			else if (target.equals("OOOO"))
				return "100 bis 1000 Objekte";
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
		return costs;
	}

	public String getComplexity() {
		return complexity;
	}

	public void setComplexity(String complexity) {
		this.complexity = complexity;
	}

	public void setCosts(String costs) {
		this.costs = costs;
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
				return "der Zauberer selbst";
			else if (range.equals("B"))
				return "Berührung";
			else if (range.equals("s,B"))
				return "der Zauberer selbst oder Berührung";
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
			if (castDuration.startsWith("A(") && castDuration.endsWith(")"))
				return castDuration.substring(2, castDuration.length() - 1) + " Aktionen";
			else if (castDuration.startsWith("SR(") && castDuration.endsWith(")"))
				return castDuration.substring(2, castDuration.length() - 1) + " Spielrunden";
			else
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

	public String getEffectDurationDetailed() {
		if (!TextUtils.isEmpty(effectDuration)) {
			if (effectDuration.equals("A"))
				return "Augenblicklich";
			else if (effectDuration.equals("KR"))
				return "ZfP* Kampfrunden";
			else if (effectDuration.equals("KR10"))
				return "ZfP* 10 Kampfrunden";
			else if (effectDuration.equals("SR"))
				return "ZfP* Spielrunden";
			else if (effectDuration.equals("St"))
				return "ZfP* Stunden";
			else if (effectDuration.equals("T"))
				return "ZfP* Tage";
			else if (effectDuration.equals("W"))
				return "ZfP* Wochen";
			else if (effectDuration.equals("M"))
				return "ZfP* Monate";
			else if (effectDuration.equals("J"))
				return "ZfP* Jahre";
			else if (effectDuration.startsWith("P")) {
				return "Permanent";
			} else
				return effectDuration;
		} else {
			return null;
		}
	}

	public String getEffectDuration() {
		return effectDuration;
	}

	public void setEffectDuration(String effectDuration) {
		this.effectDuration = effectDuration;
	}

	public String getRepresentation() {
		return representation;
	}

	public void setRepresentation(String rep) {
		this.representation = rep;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
