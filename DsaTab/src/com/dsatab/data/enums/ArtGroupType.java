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
package com.dsatab.data.enums;

import java.util.HashMap;
import java.util.Map;

import com.dsatab.data.Art;

/**
 * @author Ganymede
 * 
 */
public enum ArtGroupType {

	Liturige(Art.LITURGIE_PREFIX, TalentType.Liturgiekenntnis), Ritual(Art.RITUAL_PREFIX, TalentType.Ritualkenntnis), RitualSeher(
			Art.RITUAL_SEHER_PREFIX, TalentType.Ritualkenntnis), Stabzauber(Art.STABZAUBER_PREFIX,
			TalentType.RitualkenntnisGildenmagie), Schalenzauber(Art.SCHALENZAUBER_PREFIX,
			TalentType.RitualkenntnisAlchimist), SchlangenringZauber(Art.SCHLANGENRING_ZAUBER_PEFIX,
			TalentType.RitualkenntnisGeode), Schuppenbeutel(Art.SCHUPPENBEUTEL_PREFIX,
			TalentType.RitualkenntnisKristallomantie), Trommelzauber(Art.TROMMELZAUBER_PREFIX,
			TalentType.RitualkenntnisDerwisch), Runen(Art.RUNEN_PREFIX), Kugelzauber(Art.KUGELZAUBER_PREFIX), KristallomantischesRitual(
			Art.KRISTALLOMANTISCHES_RITUAL_PREFIX, TalentType.RitualkenntnisKristallomantie), Hexenfluch(
			Art.HEXENFLUCH_PREFIX, TalentType.RitualkenntnisHexe), GabeDesOdun(Art.GABE_DES_ODUN_PREFIX,
			TalentType.RitualkenntnisDurroDûn), DruidischesHerrschaftsritual(Art.DRUIDISCHES_HERRSCHAFTSRITUAL_PREFIX,
			TalentType.RitualkenntnisDruide), DruidischesDolchritual(Art.DRUIDISCHES_DOLCHRITUAL_PREFIX,
			TalentType.RitualkenntnisDruide), Zaubertanz(Art.ZAUBERTANZ_PREFIX, TalentType.RitualkenntnisZaubertänzer), Zauberzeichen(
			Art.ZAUBERZEICHEN_PREFIX), ZibiljaRitual(Art.ZIBILJA_RITUAL_PREFIX, TalentType.RitualkenntnisZibilja), Elfenlieder(
			Art.ELFENLIEDER_PREFIX, TalentType.Musizieren), Keulenritual(Art.KEULEN_RITUAL_PREFIX,
			TalentType.Ritualkenntnis), Szepter(Art.SZEPTER_RITUAL_PREFIX, null), RituelleStrafe(
			Art.RITUELLE_STRAFE_PREFIX, null), Kristallpendel(Art.KISTALLPENDEL_PREFIX, null), Tapasuul(
			Art.TAPASUUL_PREFIX, null);
	;
	// public static final String RITUELLE_STRAFE_PREFIX = "Rituelle Strafe: ";
	// public static final String KISTALLPENDEL_PREFIX = "Kristallpendel: ";
	// public static final String TAPASUUL_PREFIX = "Tapasuul: ";;

	public static final Map<String, ArtGroupType> artMappings = new HashMap<String, ArtGroupType>();
	static {
		artMappings.put(FeatureType.Apport.xmlName(), ArtGroupType.Stabzauber);
		artMappings.put(FeatureType.Bannschwert.xmlName(), ArtGroupType.Stabzauber);
		artMappings.put(FeatureType.DieGestaltAusRauch.xmlName(), ArtGroupType.Ritual);
		artMappings.put(FeatureType.KristallkraftBündeln.xmlName(), ArtGroupType.KristallomantischesRitual);
	}

	private String prefix;

	private TalentType talentType;

	private ArtGroupType(String prefix) {
		this.prefix = prefix;
		this.talentType = null;
	}

	private ArtGroupType(String prefix, TalentType talentType) {
		this.prefix = prefix;
		this.talentType = talentType;
	}

	public String prefix() {
		return prefix;
	}

	public TalentType talentType() {
		return talentType;
	}

	public String getName() {
		String name = prefix.trim();
		if (name.endsWith(":"))
			name = name.substring(0, name.length() - 1);

		return name;
	}

	public static ArtGroupType getTypeOfArt(String artName) {
		ArtGroupType result = artMappings.get(artName);
		if (result == null) {
			ArtGroupType[] types = ArtGroupType.values();
			for (ArtGroupType type : types) {
				if (artName.startsWith(type.prefix)) {
					result = type;
					break;
				}
			}
		}
		return result;
	}

	public String truncateName(String artName) {
		if (artName.startsWith(prefix)) {
			return artName.substring(prefix.length()).trim();
		} else {
			return artName.trim();
		}
	}
}
