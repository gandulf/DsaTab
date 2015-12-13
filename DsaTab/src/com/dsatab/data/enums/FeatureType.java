package com.dsatab.data.enums;

import android.text.TextUtils;

import com.dsatab.exception.FeatureTypeUnknownException;

public enum FeatureType {
	Amtsadel("Amtsadel", FeatureGroupType.Advantage), AdligeAbstammung("Adlige Abstammung", FeatureGroupType.Advantage), AdligesErbe(
			"Adliges Erbe", FeatureGroupType.Advantage), AffinitätZu("Affinität zu", FeatureGroupType.Advantage), AkademischeAusbildungGelehrter(
			"Akademische Ausbildung (Gelehrter)", FeatureGroupType.Advantage), AkademischeAusbildungKrieger(
			"Akademische Ausbildung (Krieger)", FeatureGroupType.Advantage), AkademischeAusbildungMagier(
			"Akademische Ausbildung (Magier)", FeatureGroupType.Advantage), Altersresistenz("Altersresistenz",
			FeatureGroupType.Advantage), AstraleRegeneration("Astrale Regeneration", FeatureGroupType.Advantage), Astralmacht(
			"Astralmacht", FeatureGroupType.Advantage), Ausdauernd("Ausdauernd", FeatureGroupType.Advantage), AusdauernderZauberer(
			"Ausdauernder Zauberer", FeatureGroupType.Advantage), Ausrüstungsvorteil("Ausrüstungsvorteil",
			FeatureGroupType.Advantage), Balance("Balance", FeatureGroupType.Advantage), BegabungFürMerkmal(
			"Begabung für [Merkmal]", FeatureGroupType.Advantage), BegabungFürRitual("Begabung für [Ritual]",
			FeatureGroupType.Advantage), BegabungFürTalent("Begabung für [Talent]", FeatureGroupType.Advantage), BegabungFürTalentgruppe(
			"Begabung für [Talentgruppe]", FeatureGroupType.Advantage), BegabungFürZauber("Begabung für [Zauber]",
			FeatureGroupType.Advantage), Beidhändig("Beidhändig", FeatureGroupType.Advantage), BeseelteKnochenkeule(
			"Beseelte Knochenkeule", FeatureGroupType.Advantage), BesondererBesitz("Besonderer Besitz",
			FeatureGroupType.Advantage), BreitgefächerteBildung("Breitgefächerte Bildung", FeatureGroupType.Advantage), Dämmerungssicht(
			"Dämmerungssicht", FeatureGroupType.Advantage), Dschinngeboren("Dschinngeboren", FeatureGroupType.Advantage), EidetischesGedächtnis(
			"Eidetisches Gedächtnis", FeatureGroupType.Advantage), Eigeboren("Eigeboren", FeatureGroupType.Advantage), EisenaffineAura(
			"Eisenaffine Aura", FeatureGroupType.Advantage), Eisern("Eisern", FeatureGroupType.Advantage), Empathie(
			"Empathie", FeatureGroupType.Advantage), Entfernungssinn("Entfernungssinn", FeatureGroupType.Advantage), ErerbteKnochenkeule(
			"Ererbte Knochenkeule", FeatureGroupType.Advantage), Feenfreund("Feenfreund", FeatureGroupType.Advantage), FesteMatrix(
			"Feste Matrix", FeatureGroupType.Advantage), FrüherVertrauter("Früher Vertrauter",
			FeatureGroupType.Advantage), Flink("Flink", FeatureGroupType.Advantage), Gebildet("Gebildet",
			FeatureGroupType.Advantage), Gefahreninstinkt("Gefahreninstinkt", FeatureGroupType.Advantage), Geräuschhexerei(
			"Geräuschhexerei", FeatureGroupType.Advantage), GeweihtAngrosch("Geweiht [Angrosch]",
			FeatureGroupType.Advantage), GeweihtGravesh("Geweiht [Gravesh]", FeatureGroupType.Advantage), GeweihtnichtalveranischeGottheit(
			"Geweiht [nicht-alveranische Gottheit]", FeatureGroupType.Advantage), GeweihtHRanga("Geweiht [H'Ranga]",
			FeatureGroupType.Advantage), GeweihtzwölfgöttlicheKirche("Geweiht [zwölfgöttliche Kirche]",
			FeatureGroupType.Advantage), Glück("Glück", FeatureGroupType.Advantage), GlückImSpiel("Glück im Spiel",
			FeatureGroupType.Advantage), Gutaussehend("Gutaussehend", FeatureGroupType.Advantage), GuterRuf(
			"Guter Ruf", FeatureGroupType.Advantage), GutesGedächtnis("Gutes Gedächtnis", FeatureGroupType.Advantage), Halbzauberer(
			"Halbzauberer", FeatureGroupType.Advantage), HerausragendeBalance("Herausragende Balance",
			FeatureGroupType.Advantage), HerausragendeEigenschaft("Herausragende Eigenschaft",
			FeatureGroupType.Advantage), HerausragendeEigenschaftCharisma("Herausragende Eigenschaft: Charisma",
			FeatureGroupType.Advantage), HerausragendeEigenschaftFingerfertigkeit(
			"Herausragende Eigenschaft: Fingerfertigkeit", FeatureGroupType.Advantage), HerausragendeEigenschaftGewandtheit(
			"Herausragende Eigenschaft: Gewandtheit", FeatureGroupType.Advantage), HerausragendeEigenschaftIntuition(
			"Herausragende Eigenschaft: Intuition", FeatureGroupType.Advantage), HerausragendeEigenschaftKlugheit(
			"Herausragende Eigenschaft: Klugheit", FeatureGroupType.Advantage), HerausragendeEigenschaftKörperkraft(
			"Herausragende Eigenschaft: Körperkraft", FeatureGroupType.Advantage), HerausragendeEigenschaftKonstitution(
			"Herausragende Eigenschaft: Konstitution", FeatureGroupType.Advantage), HerausragendeEigenschaftMut(
			"Herausragende Eigenschaft: Mut", FeatureGroupType.Advantage), HerausragenderSechsterSinn(
			"Herausragender Sechster Sinn", FeatureGroupType.Advantage), HerausragenderSinn("Herausragender Sinn",
			FeatureGroupType.Advantage), HerausragendesAussehen("Herausragendes Aussehen", FeatureGroupType.Advantage), Hitzeresistenz(
			"Hitzeresistenz", FeatureGroupType.Advantage), HoheLebenskraft("Hohe Lebenskraft",
			FeatureGroupType.Advantage), HoheMagieresistenz("Hohe Magieresistenz", FeatureGroupType.Advantage), ImmunitätGegenGift(
			"Immunität gegen Gift", FeatureGroupType.Advantage), ImmunitätGegenKrankheiten(
			"Immunität gegen Krankheiten", FeatureGroupType.Advantage), InnererKompass("Innerer Kompass",
			FeatureGroupType.Advantage), Kälteresistenz("Kälteresistenz", FeatureGroupType.Advantage), Kampfrausch(
			"Kampfrausch", FeatureGroupType.Advantage), Koboldfreund("Koboldfreund", FeatureGroupType.Advantage), Kräfteschub(
			"Kräfteschub", FeatureGroupType.Advantage), Talentschub("Talentschub", FeatureGroupType.Advantage), Linkshänder(
			"Linkshänder", FeatureGroupType.Advantage), MachtvollerVertrauter("Machtvoller Vertrauter",
			FeatureGroupType.Advantage), Magiedilletant("Magiedilletant", FeatureGroupType.Advantage), Magiegespür(
			"Magiegespür", FeatureGroupType.Advantage), Meisterhandwerk("Meisterhandwerk", FeatureGroupType.Advantage), Nachtsicht(
			"Nachtsicht", FeatureGroupType.Advantage), NatürlicherRüstungsschutz("Natürlicher Rüstungsschutz",
			FeatureGroupType.Advantage), NatürlicheWaffen("Natürliche Waffen", FeatureGroupType.Advantage), NiedrigeSchlechteEigenschaft(
			"Niedrige Schlechte Eigenschaft", FeatureGroupType.Advantage), Prophezeien("Prophezeien",
			FeatureGroupType.Advantage), ResistenzGegenGift("Resistenz gegen Gift", FeatureGroupType.Advantage), ResistenzGegenKrankheiten(
			"Resistenz gegen Krankheiten", FeatureGroupType.Advantage), Richtungssinn("Richtungssinn",
			FeatureGroupType.Advantage), Schlangenmensch("Schlangenmensch", FeatureGroupType.Advantage), SchnelleHeilung(
			"Schnelle Heilung", FeatureGroupType.Advantage), Schutzgeist("Schutzgeist", FeatureGroupType.Advantage), SchwerZuVerzaubern(
			"Schwer zu verzaubern", FeatureGroupType.Advantage), SozialeAnpassungsfähigkeit(
			"Soziale Anpassungsfähigkeit", FeatureGroupType.Advantage), Sprachgefühl("Sprachgefühl",
			FeatureGroupType.Advantage), Tierfreund("Tierfreund", FeatureGroupType.Advantage), Tierempathiealle(
			"Tierempathie (alle)", FeatureGroupType.Advantage), Tierempathiespeziell("Tierempathie (speziell)",
			FeatureGroupType.Advantage), Titularadel("Titularadel", FeatureGroupType.Advantage), ÜbernatürlicheBegabung(
			"Übernatürliche Begabung", FeatureGroupType.Advantage), UnbeschwertesZaubern("Unbeschwertes Zaubern",
			FeatureGroupType.Advantage), Verbindungen("Verbindungen", FeatureGroupType.Advantage), VerhüllteAura(
			"Verhüllte Aura", FeatureGroupType.Advantage), Veteran("Veteran", FeatureGroupType.Advantage), Viertelzauberer(
			"Viertelzauberer", FeatureGroupType.Advantage), UnbewussterViertelzauberer("Unbewusster Viertelzauberer",
			FeatureGroupType.Advantage), Vollzauberer("Vollzauberer", FeatureGroupType.Advantage), VomSchicksalBegünstigt(
			"Vom Schicksal begünstigt", FeatureGroupType.Advantage), WesenDerNacht("Wesen der Nacht",
			FeatureGroupType.Advantage), Wolfskind("Wolfskind", FeatureGroupType.Advantage), Wohlklang("Wohlklang",
			FeatureGroupType.Advantage), ZäherHund("Zäher Hund", FeatureGroupType.Advantage), Zauberhaar("Zauberhaar",
			FeatureGroupType.Advantage), Zeitgefühl("Zeitgefühl", FeatureGroupType.Advantage), ZusätzlicheGliedmaßen(
			"Zusätzliche Gliedmaßen", FeatureGroupType.Advantage), ZweistimmigerGesang("Zweistimmiger Gesang",
			FeatureGroupType.Advantage), Zwergennase("Zwergennase", FeatureGroupType.Advantage), AbneigungGegenOrks(
			"Abneigung gegen Orks", FeatureGroupType.Advantage), AusdauerndTier("Ausdauernd (Tier)",
			FeatureGroupType.Advantage), AusdauerndTier1("Ausdauernd (Tier) 1", FeatureGroupType.Advantage), AusdauerndTier2(
			"Ausdauernd (Tier) 2", FeatureGroupType.Advantage), Eigenwillig("Eigenwillig", FeatureGroupType.Advantage), Einzelgänger(
			"Einzelgänger", FeatureGroupType.Advantage), EleganteErscheinung("Elegante Erscheinung",
			FeatureGroupType.Advantage), Feurig("Feurig", FeatureGroupType.Advantage), Flinkheit("Flinkheit",
			FeatureGroupType.Advantage), Friedliebend("Friedliebend", FeatureGroupType.Advantage), Geduldig("Geduldig",
			FeatureGroupType.Advantage), Genügsam("Genügsam", FeatureGroupType.Advantage), GuterOrientierungssinn(
			"Guter Orientierungssinn", FeatureGroupType.Advantage), Gutmütig("Gutmütig", FeatureGroupType.Advantage), Halbwild(
			"Halbwild", FeatureGroupType.Advantage), Intelligent("Intelligent", FeatureGroupType.Advantage), Kehlenbiss(
			"Kehlenbiss", FeatureGroupType.Advantage), Kläffer("Kläffer", FeatureGroupType.Advantage), Lernfähig(
			"Lernfähig", FeatureGroupType.Advantage), Nervosität("Nervosität", FeatureGroupType.Advantage), RuhigesTemperament(
			"Ruhiges Temperament", FeatureGroupType.Advantage), Schnell("Schnell", FeatureGroupType.Advantage), Schnelligkeit(
			"Schnelligkeit", FeatureGroupType.Advantage), SchwerZuErziehen("Schwer zu erziehen",
			FeatureGroupType.Advantage), SehrLernfähig("Sehr lernfähig", FeatureGroupType.Advantage), Spieltrieb(
			"Spieltrieb", FeatureGroupType.Advantage), Stark("Stark", FeatureGroupType.Advantage), Störrisch(
			"Störrisch", FeatureGroupType.Advantage), Tragkraft("Tragkraft", FeatureGroupType.Advantage), Tragkraft1(
			"Tragkraft 1", FeatureGroupType.Advantage), Tragkraft2("Tragkraft 2", FeatureGroupType.Advantage), Trendeln(
			"Trendeln", FeatureGroupType.Advantage), Trittsicherheit("Trittsicherheit", FeatureGroupType.Advantage), Wasserliebend(
			"Wasserliebend", FeatureGroupType.Advantage), WeicheGänge("Weiche Gänge", FeatureGroupType.Advantage), Zäh(
			"Zäh", FeatureGroupType.Advantage), Zugkraft("Zugkraft", FeatureGroupType.Advantage), Zugkraft1(
			"Zugkraft 1", FeatureGroupType.Advantage), Zugkraft2("Zugkraft 2", FeatureGroupType.Advantage), GöttergeschenkDelfin(
			"Göttergeschenk: Delfin", FeatureGroupType.Advantage), GöttergeschenkEidechse("Göttergeschenk: Eidechse",
			FeatureGroupType.Advantage), GöttergeschenkFirunsbär("Göttergeschenk: Firunsbär",
			FeatureGroupType.Advantage), GöttergeschenkFuchs("Göttergeschenk: Fuchs", FeatureGroupType.Advantage), GöttergeschenkGans(
			"Göttergeschenk: Gans", FeatureGroupType.Advantage), GöttergeschenkGreif("Göttergeschenk: Greif",
			FeatureGroupType.Advantage), GöttergeschenkHammerAmboss("Göttergeschenk: Hammer/Amboss",
			FeatureGroupType.Advantage), GöttergeschenkRabe("Göttergeschenk: Rabe", FeatureGroupType.Advantage), GöttergeschenkSchlange(
			"Göttergeschenk: Schlange", FeatureGroupType.Advantage), GöttergeschenkSchwert("Göttergeschenk: Schwert",
			FeatureGroupType.Advantage), GöttergeschenkStorch("Göttergeschenk: Storch", FeatureGroupType.Advantage), GöttergeschenkStute(
			"Göttergeschenk: Stute", FeatureGroupType.Advantage), Aberglaube("Aberglaube",
			FeatureGroupType.Disadvantage), Agrimothwahn("Agrimothwahn", FeatureGroupType.Disadvantage), Albino(
			"Albino", FeatureGroupType.Disadvantage), AngstVorInsekten("Angst vor Insekten",
			FeatureGroupType.Disadvantage), AngstVorMenschenmassen("Angst vor Menschenmassen",
			FeatureGroupType.Disadvantage), AngstVorSpinnen("Angst vor Spinnen", FeatureGroupType.Disadvantage), AngstVorReptilien(
			"Angst vor Reptilien", FeatureGroupType.Disadvantage), AngstVorPelztieren("Angst vor Pelztieren",
			FeatureGroupType.Disadvantage), AngstVorWasser("Angst vor Wasser", FeatureGroupType.Disadvantage), AngstVorFeuer(
			"Angst vor Feuer", FeatureGroupType.Disadvantage), AngstVorNagetieren("Angst vor Nagetieren",
			FeatureGroupType.Disadvantage), AnimalischeMagie("Animalische Magie", FeatureGroupType.Disadvantage), Arkanophobie(
			"Arkanophobie", FeatureGroupType.Disadvantage), Arroganz("Arroganz", FeatureGroupType.Disadvantage), Artefaktgebunden(
			"Artefaktgebunden", FeatureGroupType.Disadvantage), AstralerBlock("Astraler Block",
			FeatureGroupType.Disadvantage), Autoritätsgläubig("Autoritätsgläubig", FeatureGroupType.Disadvantage), Behäbig(
			"Behäbig", FeatureGroupType.Disadvantage), Blutdurst("Blutdurst", FeatureGroupType.Disadvantage), Grausamkeit(
			"Grausamkeit", FeatureGroupType.Disadvantage), Blutrausch("Blutrausch", FeatureGroupType.Disadvantage), Brünstigkeit(
			"Brünstigkeit", FeatureGroupType.Disadvantage), Charyptophilie("Charyptophilie",
			FeatureGroupType.Disadvantage), Comes("Comes", FeatureGroupType.Disadvantage), Dunkelangst("Dunkelangst",
			FeatureGroupType.Disadvantage), Einarmig("Einarmig", FeatureGroupType.Disadvantage), Einäugig("Einäugig",
			FeatureGroupType.Disadvantage), Einbeinig("Einbeinig", FeatureGroupType.Disadvantage), Einbildungen(
			"Einbildungen", FeatureGroupType.Disadvantage), EingeschränkteElementarnähe("Eingeschränkte Elementarnähe",
			FeatureGroupType.Disadvantage), EingeschränkterSinn("Eingeschränkter Sinn", FeatureGroupType.Disadvantage), Einhändig(
			"Einhändig", FeatureGroupType.Disadvantage), Eitelkeit("Eitelkeit", FeatureGroupType.Disadvantage), ElfischeWeltsicht(
			"Elfische Weltsicht", FeatureGroupType.Disadvantage), ErstgeborenerComes("Erstgeborener Comes",
			FeatureGroupType.Disadvantage), Farbenblind("Farbenblind", FeatureGroupType.Disadvantage), Feind("Feind",
			FeatureGroupType.Disadvantage), FesteGewohnheit("Feste Gewohnheit", FeatureGroupType.Disadvantage), FestgefügtesDenken(
			"Festgefügtes Denken", FeatureGroupType.Disadvantage), Fettleibig("Fettleibig",
			FeatureGroupType.Disadvantage), FluchDerFinsternis("Fluch der Finsternis", FeatureGroupType.Disadvantage), Geiz(
			"Geiz", FeatureGroupType.Disadvantage), Gesucht("Gesucht", FeatureGroupType.Disadvantage), Gerechtigkeitswahn(
			"Gerechtigkeitswahn", FeatureGroupType.Disadvantage), Glasknochen("Glasknochen",
			FeatureGroupType.Disadvantage), Goldgier("Goldgier", FeatureGroupType.Disadvantage), Größenwahn(
			"Größenwahn", FeatureGroupType.Disadvantage), Herrschsucht("Herrschsucht", FeatureGroupType.Disadvantage), Heimwehkrank(
			"Heimwehkrank", FeatureGroupType.Disadvantage), Hitzeempfindlichkeit("Hitzeempfindlichkeit",
			FeatureGroupType.Disadvantage), HoherAmtsadel("Hoher Amtsadel", FeatureGroupType.Disadvantage), Höhenangst(
			"Höhenangst", FeatureGroupType.Disadvantage), Impulsiv("Impulsiv", FeatureGroupType.Disadvantage), Jagdfieber(
			"Jagdfieber", FeatureGroupType.Disadvantage), Jähzorn("Jähzorn", FeatureGroupType.Disadvantage), Streitsucht(
			"Streitsucht", FeatureGroupType.Disadvantage), Kälteempfindlichkeit("Kälteempfindlichkeit",
			FeatureGroupType.Disadvantage), Kältestarre("Kältestarre", FeatureGroupType.Disadvantage), KeinVertrauter(
			"Kein Vertrauter", FeatureGroupType.Disadvantage), Kleinwüchsig("Kleinwüchsig",
			FeatureGroupType.Disadvantage), KörpergebundeneKraft("Körpergebundene Kraft", FeatureGroupType.Disadvantage), Konstruktionswahn(
			"Konstruktionswahn", FeatureGroupType.Disadvantage), KrankhafteNekromantie("Krankhafte Nekromantie",
			FeatureGroupType.Disadvantage), KrankhafteReinlichkeit("Krankhafte Reinlichkeit",
			FeatureGroupType.Disadvantage), KrankhaftesEhrgefühl("Krankhaftes Ehrgefühl", FeatureGroupType.Disadvantage), Krankheitsanfällig(
			"Krankheitsanfällig", FeatureGroupType.Disadvantage), Kristallgebunden("Kristallgebunden",
			FeatureGroupType.Disadvantage), Kurzatmig("Kurzatmig", FeatureGroupType.Disadvantage), Lahm("Lahm",
			FeatureGroupType.Disadvantage), Landangst("Landangst", FeatureGroupType.Disadvantage), LästigeMindergeister(
			"Lästige Mindergeister", FeatureGroupType.Disadvantage), Lichtempfindlich("Lichtempfindlich",
			FeatureGroupType.Disadvantage), Lichtscheu("Lichtscheu", FeatureGroupType.Disadvantage), MadasFluch(
			"Madas Fluch", FeatureGroupType.Disadvantage), Medium("Medium", FeatureGroupType.Disadvantage), Meeresangst(
			"Meeresangst", FeatureGroupType.Disadvantage), MiserableEigenschaft("Miserable Eigenschaft",
			FeatureGroupType.Disadvantage), MiserableEigenschaftCharisma("Miserable Eigenschaft: Charisma",
			FeatureGroupType.Disadvantage), MiserableEigenschaftFingerfertigkeit(
			"Miserable Eigenschaft: Fingerfertigkeit", FeatureGroupType.Disadvantage), MiserableEigenschaftGewandtheit(
			"Miserable Eigenschaft: Gewandtheit", FeatureGroupType.Disadvantage), MiserableEigenschaftIntuition(
			"Miserable Eigenschaft: Intuition", FeatureGroupType.Disadvantage), MiserableEigenschaftKlugheit(
			"Miserable Eigenschaft: Klugheit", FeatureGroupType.Disadvantage), MiserableEigenschaftKörperkraft(
			"Miserable Eigenschaft: Körperkraft", FeatureGroupType.Disadvantage), MiserableEigenschaftKonstitution(
			"Miserable Eigenschaft: Konstitution", FeatureGroupType.Disadvantage), MiserableEigenschaftMut(
			"Miserable Eigenschaft: Mut", FeatureGroupType.Disadvantage), Mondsüchtig("Mondsüchtig",
			FeatureGroupType.Disadvantage), Moralkodex("Moralkodex", FeatureGroupType.Disadvantage), MoralkodexAngroschKult(
			"Moralkodex [Angrosch-Kult]", FeatureGroupType.Disadvantage), MoralkodexBadalikaner(
			"Moralkodex [Badalikaner]", FeatureGroupType.Disadvantage), MoralkodexBoronKirche(
			"Moralkodex [Boron-Kirche]", FeatureGroupType.Disadvantage), MoralkodexBundDesWahrenGlaubens(
			"Moralkodex [Bund des wahren Glaubens]", FeatureGroupType.Disadvantage), MoralkodexDreischwesternorden(
			"Moralkodex [Dreischwesternorden]", FeatureGroupType.Disadvantage), MoralkodexEfferdKirche(
			"Moralkodex [Efferd-Kirche]", FeatureGroupType.Disadvantage), MoralkodexFirunKirche(
			"Moralkodex [Firun-Kirche]", FeatureGroupType.Disadvantage), MoralkodexHesindeKirche(
			"Moralkodex [Hesinde-Kirche]", FeatureGroupType.Disadvantage), MoralkodexHSzintKult(
			"Moralkodex [H'Szint-Kult]", FeatureGroupType.Disadvantage), MoralkodexIfirnKirche(
			"Moralkodex [Ifirn-Kirche]", FeatureGroupType.Disadvantage), MoralkodexIngerimmKirche(
			"Moralkodex [Ingerimm-Kirche]", FeatureGroupType.Disadvantage), MoralkodexKorKirche(
			"Moralkodex [Kor-Kirche]", FeatureGroupType.Disadvantage), MoralkodexNandusKirche(
			"Moralkodex [Nandus-Kirche]", FeatureGroupType.Disadvantage), MoralkodexPeraineKirche(
			"Moralkodex [Peraine-Kirche]", FeatureGroupType.Disadvantage), MoralkodexPhexKirche(
			"Moralkodex [Phex-Kirche]", FeatureGroupType.Disadvantage), MoralkodexPraiosKirche(
			"Moralkodex [Praios-Kirche]", FeatureGroupType.Disadvantage), MoralkodexRahjaKirche(
			"Moralkodex [Rahja-Kirche]", FeatureGroupType.Disadvantage), MoralkodexRondraKirche(
			"Moralkodex [Rondra-Kirche]", FeatureGroupType.Disadvantage), MoralkodexSwafnirKult(
			"Moralkodex [Swafnir-Kult]", FeatureGroupType.Disadvantage), MoralkodexTraviaKirche(
			"Moralkodex [Travia-Kirche]", FeatureGroupType.Disadvantage), MoralkodexHeshinja("Moralkodex [Heshinja]",
			FeatureGroupType.Disadvantage), MoralkodexTsaKirche("Moralkodex [Tsa-Kirche]",
			FeatureGroupType.Disadvantage), MoralkodexZsahhKult("Moralkodex [Zsahh-Kult]",
			FeatureGroupType.Disadvantage), MoralkodexDDZ("Moralkodex [DDZ]", FeatureGroupType.Disadvantage), Morbidität(
			"Morbidität", FeatureGroupType.Disadvantage), Nachtblind("Nachtblind", FeatureGroupType.Disadvantage), Nagrachwahn(
			"Nagrachwahn", FeatureGroupType.Disadvantage), Nahrungsrestriktion("Nahrungsrestriktion",
			FeatureGroupType.Disadvantage), Neid("Neid", FeatureGroupType.Disadvantage), Neugier("Neugier",
			FeatureGroupType.Disadvantage), NiedrigeAstralkraft("Niedrige Astralkraft", FeatureGroupType.Disadvantage), NiedrigeLebenskraft(
			"Niedrige Lebenskraft", FeatureGroupType.Disadvantage), NiedrigeMagieresistenz("Niedrige Magieresistenz",
			FeatureGroupType.Disadvantage), Pechmagnet("Pechmagnet", FeatureGroupType.Disadvantage), Platzangst(
			"Platzangst", FeatureGroupType.Disadvantage), Prinzipientreue("Prinzipientreue",
			FeatureGroupType.Disadvantage), Rachsucht("Rachsucht", FeatureGroupType.Disadvantage), Randgruppe(
			"Randgruppe", FeatureGroupType.Disadvantage), Raubtiergeruch("Raubtiergeruch",
			FeatureGroupType.Disadvantage), Raumangst("Raumangst", FeatureGroupType.Disadvantage), Rückschlag(
			"Rückschlag", FeatureGroupType.Disadvantage), Ruhelosigkeit("Ruhelosigkeit", FeatureGroupType.Disadvantage), Sacerdos(
			"Sacerdos", FeatureGroupType.Disadvantage), Schlaflosigkeit("Schlaflosigkeit",
			FeatureGroupType.Disadvantage), Schlafstörungen("Schlafstörungen", FeatureGroupType.Disadvantage), Schlafwandler(
			"Schlafwandler", FeatureGroupType.Disadvantage), SensiblerGeruchssinn("Sensibler Geruchssinn",
			FeatureGroupType.Disadvantage), SchlechteRegeneration("Schlechte Regeneration",
			FeatureGroupType.Disadvantage), SchlechterRuf("Schlechter Ruf", FeatureGroupType.Disadvantage), SchnellerAlternd(
			"Schneller alternd", FeatureGroupType.Disadvantage), Schöpferwahn("Schöpferwahn",
			FeatureGroupType.Disadvantage), Schulden("Schulden", FeatureGroupType.Disadvantage), SchwacheAusstrahlung(
			"Schwache Ausstrahlung", FeatureGroupType.Disadvantage), SchwacherAstralkörper("Schwacher Astralkörper",
			FeatureGroupType.Disadvantage), SchwacherWille("Schwacher Wille", FeatureGroupType.Disadvantage), Schwanzlos(
			"Schwanzlos", FeatureGroupType.Disadvantage), SefferManich("Seffer Manich", FeatureGroupType.Disadvantage), Selbstgespräche(
			"Selbstgespräche", FeatureGroupType.Disadvantage), Sippenlosigkeit("Sippenlosigkeit",
			FeatureGroupType.Disadvantage), Sonnensucht("Sonnensucht", FeatureGroupType.Disadvantage), Speisegebote(
			"Speisegebote", FeatureGroupType.Disadvantage), Sprachfehler("Sprachfehler", FeatureGroupType.Disadvantage), Spielsucht(
			"Spielsucht", FeatureGroupType.Disadvantage), Spruchhemmung("Spruchhemmung", FeatureGroupType.Disadvantage), Stigma(
			"Stigma", FeatureGroupType.Disadvantage), Stubenhocker("Stubenhocker", FeatureGroupType.Disadvantage), Sucht(
			"Sucht", FeatureGroupType.Disadvantage), Thesisgebunden("Thesisgebunden", FeatureGroupType.Disadvantage), Tollpatsch(
			"Tollpatsch", FeatureGroupType.Disadvantage), Totenangst("Totenangst", FeatureGroupType.Disadvantage), Trägheit(
			"Trägheit", FeatureGroupType.Disadvantage), Treulosigkeit("Treulosigkeit", FeatureGroupType.Disadvantage), ÜblerGeruch(
			"Übler Geruch", FeatureGroupType.Disadvantage), UnangenehmeStimme("Unangenehme Stimme",
			FeatureGroupType.Disadvantage), Unansehnlich("Unansehnlich", FeatureGroupType.Disadvantage), UnangenehmeÄußerlichkeit(
			"Unangenehme Äußerlichkeit", FeatureGroupType.Disadvantage), UnfähigkeitFürMerkmal(
			"Unfähigkeit für [Merkmal]", FeatureGroupType.Disadvantage), UnfähigkeitFürTalent(
			"Unfähigkeit für [Talent]", FeatureGroupType.Disadvantage), UnfähigkeitFürTalentgruppe(
			"Unfähigkeit für [Talentgruppe]", FeatureGroupType.Disadvantage), Unfrei("Unfrei",
			FeatureGroupType.Disadvantage), Ungebildet("Ungebildet", FeatureGroupType.Disadvantage), Unstet("Unstet",
			FeatureGroupType.Disadvantage), UnverträglichkeitMitVerarbeitetemMetall(
			"Unverträglichkeit mit verarbeitetem Metall", FeatureGroupType.Disadvantage), Vergesslichkeit(
			"Vergesslichkeit", FeatureGroupType.Disadvantage), Verpflichtungen("Verpflichtungen",
			FeatureGroupType.Disadvantage), Verschwendungssucht("Verschwendungssucht", FeatureGroupType.Disadvantage), Verwöhnt(
			"Verwöhnt", FeatureGroupType.Disadvantage), Vorurteile("Vorurteile", FeatureGroupType.Disadvantage), Vorurteilestark(
			"Vorurteile (stark)", FeatureGroupType.Disadvantage), VorurteileGegen("Vorurteile gegen",
			FeatureGroupType.Disadvantage), VorurteileGegenstark("Vorurteile gegen (stark)",
			FeatureGroupType.Disadvantage), WahrerName("Wahrer Name", FeatureGroupType.Disadvantage), WahrerNameDschinn(
			"Wahrer Name: Dschinn", FeatureGroupType.Disadvantage), WahrerNameDämon("Wahrer Name: Dämon",
			FeatureGroupType.Disadvantage), Wahnvorstellungen("Wahnvorstellungen", FeatureGroupType.Disadvantage), Weltfremd(
			"Weltfremd", FeatureGroupType.Disadvantage), WeltfremdBzgl("Weltfremd bzgl.", FeatureGroupType.Disadvantage), WiderwärtigesAussehen(
			"Widerwärtiges Aussehen", FeatureGroupType.Disadvantage), WildeMagie("Wilde Magie",
			FeatureGroupType.Disadvantage), Zielschwierigkeiten("Zielschwierigkeiten", FeatureGroupType.Disadvantage), ZögerlicherZauberer(
			"Zögerlicher Zauberer", FeatureGroupType.Disadvantage), Zwergenwuchs("Zwergenwuchs",
			FeatureGroupType.Disadvantage), SchlechteEigenschaft("Schlechte Eigenschaft", FeatureGroupType.Disadvantage), Ritualspezialisierung(
			"Ritualspezialisierung", FeatureGroupType.SpecialFeature), Akoluth("Akoluth",
			FeatureGroupType.SpecialFeature), Apport("Apport", FeatureGroupType.SpecialFeature), AstraleMeditation(
			"Astrale Meditation", FeatureGroupType.SpecialFeature), Aufmerksamkeit("Aufmerksamkeit",
			FeatureGroupType.SpecialFeature), AuraDerHeiligkeit("Aura der Heiligkeit", FeatureGroupType.SpecialFeature), AuraVerhüllen(
			"Aura verhüllen", FeatureGroupType.SpecialFeature), Aurapanzer("Aurapanzer",
			FeatureGroupType.SpecialFeature), Ausfall("Ausfall", FeatureGroupType.SpecialFeature), Auspendeln(
			"Auspendeln", FeatureGroupType.SpecialFeature), AusweichenI("Ausweichen I", FeatureGroupType.SpecialFeature), AusweichenII(
			"Ausweichen II", FeatureGroupType.SpecialFeature), AusweichenIII("Ausweichen III",
			FeatureGroupType.SpecialFeature), Bannschwert("Bannschwert", FeatureGroupType.SpecialFeature), Befreiungsschlag(
			"Befreiungsschlag", FeatureGroupType.SpecialFeature), BeidhändigerKampfI("Beidhändiger Kampf I",
			FeatureGroupType.SpecialFeature), BeidhändigerKampfII("Beidhändiger Kampf II",
			FeatureGroupType.SpecialFeature), Beinarbeit("Beinarbeit", FeatureGroupType.SpecialFeature), BerittenerSchütze(
			"Berittener Schütze", FeatureGroupType.SpecialFeature), Betäubungsschlag("Betäubungsschlag",
			FeatureGroupType.SpecialFeature), Binden("Binden", FeatureGroupType.SpecialFeature), Biss("Biss",
			FeatureGroupType.SpecialFeature), Blindkampf("Blindkampf", FeatureGroupType.SpecialFeature), Block("Block",
			FeatureGroupType.SpecialFeature), Blutmagie("Blutmagie", FeatureGroupType.SpecialFeature), DämonenbindungI(
			"Dämonenbindung I", FeatureGroupType.SpecialFeature), DämonenbindungII("Dämonenbindung II",
			FeatureGroupType.SpecialFeature), DefensiverKampfstil("Defensiver Kampfstil",
			FeatureGroupType.SpecialFeature), Doppelangriff("Doppelangriff", FeatureGroupType.SpecialFeature), Doppelschlag(
			"Doppelschlag", FeatureGroupType.SpecialFeature), Druidenrache("Druidenrache",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualWeihe("Druidisches Dolchritual: Weihe",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualGespür("Druidisches Dolchritual: Gespür",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualLicht("Druidisches Dolchritual: Licht",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualBann("Druidisches Dolchritual: Bann",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualLeib("Druidisches Dolchritual: Leib",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualSchutz("Druidisches Dolchritual: Schutz",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualWeg("Druidisches Dolchritual: Weg",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualErnte("Druidisches Dolchritual: Ernte",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualLebenskraft("Druidisches Dolchritual: Lebenskraft",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualOpferdolch("Druidisches Dolchritual: Opferdolch",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualSchneide("Druidisches Dolchritual: Schneide",
			FeatureGroupType.SpecialFeature), DruidischesDolchritualWeisung("Druidisches Dolchritual: Weisung",
			FeatureGroupType.SpecialFeature), DruidischesHerrschaftsritualWachsDerHerrschaft(
			"Druidisches Herrschaftsritual: Wachs der Herrschaft", FeatureGroupType.SpecialFeature), DruidischesHerrschaftsritualMiniaturDerHerrschaft(
			"Druidisches Herrschaftsritual: Miniatur der Herrschaft", FeatureGroupType.SpecialFeature), DruidischesHerrschaftsritualKristallDerHerrschaft(
			"Druidisches Herrschaftsritual: Kristall der Herrschaft", FeatureGroupType.SpecialFeature), DruidischesHerrschaftsritualAmulettDerHerrschaft(
			"Druidisches Herrschaftsritual: Amulett der Herrschaft", FeatureGroupType.SpecialFeature), DruidischesHerrschaftsritualWurzelDesBlutes(
			"Druidisches Herrschaftsritual: Wurzel des Blutes", FeatureGroupType.SpecialFeature), Dschungelkundig(
			"Dschungelkundig", FeatureGroupType.SpecialFeature), Eisenarm("Eisenarm", FeatureGroupType.SpecialFeature), Eisenhagel(
			"Eisenhagel", FeatureGroupType.SpecialFeature), EisernerWilleI("Eiserner Wille I",
			FeatureGroupType.SpecialFeature), EisernerWilleII("Eiserner Wille II", FeatureGroupType.SpecialFeature), Eiskundig(
			"Eiskundig", FeatureGroupType.SpecialFeature), ElementarharmonisierteAura("Elementarharmonisierte Aura",
			FeatureGroupType.SpecialFeature), ElfenliedSorgenlied("Elfenlied: Sorgenlied",
			FeatureGroupType.SpecialFeature), ElfenliedZaubermelodie("Elfenlied: Zaubermelodie",
			FeatureGroupType.SpecialFeature), ElfenliedFriedenslied("Elfenlied: Friedenslied",
			FeatureGroupType.SpecialFeature), ElfenliedWindgeflüster("Elfenlied: Windgeflüster",
			FeatureGroupType.SpecialFeature), ElfenliedLiedDerLieder("Elfenlied: Lied der Lieder",
			FeatureGroupType.SpecialFeature), ElfenliedFreundschaftslied("Elfenlied: Freundschaftslied",
			FeatureGroupType.SpecialFeature), ElfenliedErinnerungsmelodie("Elfenlied: Erinnerungsmelodie",
			FeatureGroupType.SpecialFeature), ElfenliedMelodieDerKunstfertigkeit(
			"Elfenlied: Melodie der Kunstfertigkeit", FeatureGroupType.SpecialFeature), ElfenliedLiedDesTrostes(
			"Elfenlied: Lied des Trostes", FeatureGroupType.SpecialFeature), ElfenliedLiedDerReinheit(
			"Elfenlied: Lied der Reinheit", FeatureGroupType.SpecialFeature), Entwaffnen("Entwaffnen",
			FeatureGroupType.SpecialFeature), Exorzist("Exorzist", FeatureGroupType.SpecialFeature), Fernzauberei(
			"Fernzauberei", FeatureGroupType.SpecialFeature), Festnageln("Festnageln", FeatureGroupType.SpecialFeature), Finte(
			"Finte", FeatureGroupType.SpecialFeature), Formation("Formation", FeatureGroupType.SpecialFeature), FormDerFormlosigkeit(
			"Form der Formlosigkeit", FeatureGroupType.SpecialFeature), Fußfeger("Fußfeger",
			FeatureGroupType.SpecialFeature), GeberDerGestalt("Geber der Gestalt", FeatureGroupType.SpecialFeature), Gebirgskundig(
			"Gebirgskundig", FeatureGroupType.SpecialFeature), Gedankenschutz("Gedankenschutz",
			FeatureGroupType.SpecialFeature), GefäßDerSterne("Gefäß der Sterne", FeatureGroupType.SpecialFeature), Gegenhalten(
			"Gegenhalten", FeatureGroupType.SpecialFeature), Gerade("Gerade", FeatureGroupType.SpecialFeature), GezielterStich(
			"Gezielter Stich", FeatureGroupType.SpecialFeature), Griff("Griff", FeatureGroupType.SpecialFeature), GroßeMeditation(
			"Große Meditation", FeatureGroupType.SpecialFeature), Golembauer("Golembauer",
			FeatureGroupType.SpecialFeature), Halten("Halten", FeatureGroupType.SpecialFeature), Hammerschlag(
			"Hammerschlag", FeatureGroupType.SpecialFeature), Handkante("Handkante", FeatureGroupType.SpecialFeature), HexenfluchÄngsteMehren(
			"Hexenfluch: Ängste mehren", FeatureGroupType.SpecialFeature), HexenfluchBeißAufGranit(
			"Hexenfluch: Beiß auf Granit", FeatureGroupType.SpecialFeature), HexenfluchBeute("Hexenfluch: Beute",
			FeatureGroupType.SpecialFeature), HexenfluchHagelschlag("Hexenfluch: Hagelschlag",
			FeatureGroupType.SpecialFeature), HexenfluchHexenschuss("Hexenfluch: Hexenschuss",
			FeatureGroupType.SpecialFeature), HexenfluchKornfäule("Hexenfluch: Kornfäule",
			FeatureGroupType.SpecialFeature), HexenfluchKrötenkuss("Hexenfluch: Krötenkuss",
			FeatureGroupType.SpecialFeature), HexenfluchMitBlindheitSchlagen("Hexenfluch: Mit Blindheit schlagen",
			FeatureGroupType.SpecialFeature), HexenfluchPechAnDenHals("Hexenfluch: Pech an den Hals",
			FeatureGroupType.SpecialFeature), HexenfluchPestilenz("Hexenfluch: Pestilenz",
			FeatureGroupType.SpecialFeature), HexenfluchSchlafRauben("Hexenfluch: Schlaf rauben",
			FeatureGroupType.SpecialFeature), HexenfluchTodesfluch("Hexenfluch: Todesfluch",
			FeatureGroupType.SpecialFeature), HexenfluchUnfruchtbarkeit("Hexenfluch: Unfruchtbarkeit",
			FeatureGroupType.SpecialFeature), HexenfluchViehverstümmelung("Hexenfluch: Viehverstümmelung",
			FeatureGroupType.SpecialFeature), HexenfluchWarzenSprießen("Hexenfluch: Warzen sprießen",
			FeatureGroupType.SpecialFeature), HexenfluchZungeLähmen("Hexenfluch: Zunge lähmen",
			FeatureGroupType.SpecialFeature), Höhlenkundig("Höhlenkundig", FeatureGroupType.SpecialFeature), HöhereDämonenbindung(
			"Höhere Dämonenbindung", FeatureGroupType.SpecialFeature), HoherTritt("Hoher Tritt",
			FeatureGroupType.SpecialFeature), HexenritualHexensalbe("Hexenritual: Hexensalbe",
			FeatureGroupType.SpecialFeature), Hypervehemenz("Hypervehemenz", FeatureGroupType.SpecialFeature), ImprovisierteWaffen(
			"Improvisierte Waffen", FeatureGroupType.SpecialFeature), InvocatioIntegra("Invocatio Integra",
			FeatureGroupType.SpecialFeature), KampfImWasser("Kampf im Wasser", FeatureGroupType.SpecialFeature), Kampfgespür(
			"Kampfgespür", FeatureGroupType.SpecialFeature), Kampfreflexe("Kampfreflexe",
			FeatureGroupType.SpecialFeature), Karmalqueste("Karmalqueste", FeatureGroupType.SpecialFeature), KeulenritualWeiheDerKeule(
			"Keulenritual: Weihe der Keule", FeatureGroupType.SpecialFeature), KeulenritualHärteDerKeule(
			"Keulenritual: Härte der Keule", FeatureGroupType.SpecialFeature), KeulenritualOpferkeule(
			"Keulenritual: Opferkeule", FeatureGroupType.SpecialFeature), KeulenritualGespürDerKeule(
			"Keulenritual: Gespür der Keule", FeatureGroupType.SpecialFeature), KeulenritualKraftDerKeule(
			"Keulenritual: Kraft der Keule", FeatureGroupType.SpecialFeature), KeulenritualHilfeDerKeule(
			"Keulenritual: Hilfe der Keule", FeatureGroupType.SpecialFeature), KeulenritualNäheZurNatur(
			"Keulenritual: Nähe zur Natur", FeatureGroupType.SpecialFeature), KeulenritualZauberDerKeule(
			"Keulenritual: Zauber der Keule", FeatureGroupType.SpecialFeature), KeulenritualBannDerKeule(
			"Keulenritual: Bann der Keule", FeatureGroupType.SpecialFeature), KeulenritualGeistDerKeule(
			"Keulenritual: Geist der Keule", FeatureGroupType.SpecialFeature), KeulenritualApportDerKeule(
			"Keulenritual: Apport der Keule", FeatureGroupType.SpecialFeature), Klammer("Klammer",
			FeatureGroupType.SpecialFeature), Klingensturm("Klingensturm", FeatureGroupType.SpecialFeature), Klingenwand(
			"Klingenwand", FeatureGroupType.SpecialFeature), Knaufschlag("Knaufschlag", FeatureGroupType.SpecialFeature), Knie(
			"Knie", FeatureGroupType.SpecialFeature), KontaktZumGroßenGeist("Kontakt zum Großen Geist",
			FeatureGroupType.SpecialFeature), Konzentrationsstärke("Konzentrationsstärke",
			FeatureGroupType.SpecialFeature), Kopfstoß("Kopfstoß", FeatureGroupType.SpecialFeature), Kraftkontrolle(
			"Kraftkontrolle", FeatureGroupType.SpecialFeature), KraftlinienmagieI("Kraftlinienmagie I",
			FeatureGroupType.SpecialFeature), KraftlinienmagieII("Kraftlinienmagie II", FeatureGroupType.SpecialFeature), Kraftspeicher(
			"Kraftspeicher", FeatureGroupType.SpecialFeature), Kreuzblock("Kreuzblock", FeatureGroupType.SpecialFeature), Kriegsreiterei(
			"Kriegsreiterei", FeatureGroupType.SpecialFeature), KristallkraftBündeln("Kristallkraft bündeln",
			FeatureGroupType.SpecialFeature), KristallomantischesRitualKristallbindung(
			"Kristallomantisches Ritual: Kristallbindung", FeatureGroupType.SpecialFeature), KristallomantischesRitualKristallformung(
			"Kristallomantisches Ritual: Kristallformung", FeatureGroupType.SpecialFeature), KristallomantischesRitualThesiskristall(
			"Kristallomantisches Ritual: Thesiskristall", FeatureGroupType.SpecialFeature), KristallomantischesRitualMadakristall(
			"Kristallomantisches Ritual: Madakristall", FeatureGroupType.SpecialFeature), KristallomantischesRitualMatrixkristall(
			"Kristallomantisches Ritual: Matrixkristall", FeatureGroupType.SpecialFeature), KugelzauberBindung(
			"Kugelzauber: Bindung", FeatureGroupType.SpecialFeature), KugelzauberBrennglasUndPrisma(
			"Kugelzauber: Brennglas und Prisma", FeatureGroupType.SpecialFeature), KugelzauberSchutzGegenUntote(
			"Kugelzauber: Schutz gegen Untote", FeatureGroupType.SpecialFeature), KugelzauberWarnendesLeuchten(
			"Kugelzauber: Warnendes Leuchten", FeatureGroupType.SpecialFeature), KugelzauberKugelDesHellsehers(
			"Kugelzauber: Kugel des Hellsehers", FeatureGroupType.SpecialFeature), KugelzauberKugelDesIllusionisten(
			"Kugelzauber: Kugel des Illusionisten", FeatureGroupType.SpecialFeature), KugelzauberOrbitarium(
			"Kugelzauber: Orbitarium", FeatureGroupType.SpecialFeature), KugelzauberBilderspiel(
			"Kugelzauber: Bilderspiel", FeatureGroupType.SpecialFeature), KugelzauberFernbild("Kugelzauber: Fernbild",
			FeatureGroupType.SpecialFeature), KugelzauberBildergalerie("Kugelzauber: Bildergalerie",
			FeatureGroupType.SpecialFeature), KugelzauberHSzintsAuge("Kugelzauber: H'Szints Auge",
			FeatureGroupType.SpecialFeature), KugelzauberFarbenDesGeistes("Kugelzauber: Farben des Geistes",
			FeatureGroupType.SpecialFeature), KugelzauberWachendesAuge("Kugelzauber: Wachendes Auge",
			FeatureGroupType.SpecialFeature), Kulturkunde("Kulturkunde", FeatureGroupType.SpecialFeature), Linkhand(
			"Linkhand", FeatureGroupType.SpecialFeature), LiturgiekenntnisAngrosch("Liturgiekenntnis (Angrosch)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisAves("Liturgiekenntnis (Aves)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisBoron("Liturgiekenntnis (Boron)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisEfferd("Liturgiekenntnis (Efferd)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisFirun("Liturgiekenntnis (Firun)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisGravesh("Liturgiekenntnis (Gravesh)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisHesinde("Liturgiekenntnis (Hesinde)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisHimmelswölfe("Liturgiekenntnis (Himmelswölfe)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisHRanga("Liturgiekenntnis (H'Ranga)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisHSzint("Liturgiekenntnis (H'Szint)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisIfirn("Liturgiekenntnis (Ifirn)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisIngerimm("Liturgiekenntnis (Ingerimm)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisKamaluq("Liturgiekenntnis (Kamaluq)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisKor("Liturgiekenntnis (Kor)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisNandus("Liturgiekenntnis (Nandus)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisNamenloser("Liturgiekenntnis (Namenloser)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisPeraine("Liturgiekenntnis (Peraine)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisPhex("Liturgiekenntnis (Phex)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisPraios("Liturgiekenntnis (Praios)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisRahja("Liturgiekenntnis (Rahja)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisRondra("Liturgiekenntnis (Rondra)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisSwafnir("Liturgiekenntnis (Swafnir)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisTairach("Liturgiekenntnis (Tairach)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisTravia("Liturgiekenntnis (Travia)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisTsa("Liturgiekenntnis (Tsa)",
			FeatureGroupType.SpecialFeature), LiturgiekenntnisZsahh("Liturgiekenntnis (Zsahh)",
			FeatureGroupType.SpecialFeature), LiturgieAchmadayanAnkhrellaAlnurachShaitanim(
			"Liturgie: Achmad'ayan ankhrella al'nurach Shaitanim", FeatureGroupType.SpecialFeature), LiturgieAllerWeltFreund(
			"Liturgie: Aller Welt Freund", FeatureGroupType.SpecialFeature), LiturgieAllmachtDerLohe(
			"Liturgie: Allmacht der Lohe", FeatureGroupType.SpecialFeature), LiturgieAlteSchuppen(
			"Liturgie: Alte Schuppen", FeatureGroupType.SpecialFeature), LiturgieAmBusenDerNaturZufluchtFinden(
			"Liturgie: Am Busen der Natur (Zuflucht finden)", FeatureGroupType.SpecialFeature), LiturgieAnathema(
			"Liturgie: Anathema", FeatureGroupType.SpecialFeature), LiturgieAngroschsOpfergabe(
			"Liturgie: Angroschs Opfergabe", FeatureGroupType.SpecialFeature), LiturgieAngroschsZornWaliburiasWehr(
			"Liturgie: Angroschs Zorn (Waliburias Wehr)", FeatureGroupType.SpecialFeature), LiturgieAnrufugDerErdkraft(
			"Liturgie: Anrufug der Erdkraft", FeatureGroupType.SpecialFeature), LiturgieAnrufungDerWinde(
			"Liturgie: Anrufung der Winde", FeatureGroupType.SpecialFeature), LiturgieAnrufungDerWindeIII(
			"Liturgie: Anrufung der Winde (III)", FeatureGroupType.SpecialFeature), LiturgieAnrufungNuiannas(
			"Liturgie: Anrufung Nuiannas", FeatureGroupType.SpecialFeature), LiturgieArcanumInterdictum(
			"Liturgie: Arcanum Interdictum", FeatureGroupType.SpecialFeature), LiturgieArgelionsBannendeHand(
			"Liturgie: Argelions bannende Hand", FeatureGroupType.SpecialFeature), LiturgieArgelionsMantel(
			"Liturgie: Argelions Mantel", FeatureGroupType.SpecialFeature), LiturgieArgelionsSpiegel(
			"Liturgie: Argelions Spiegel", FeatureGroupType.SpecialFeature), LiturgieAscandearsHingabe(
			"Liturgie: Ascandears Hingabe", FeatureGroupType.SpecialFeature), LiturgieAugeDerWartendenSeelenNemekathsGeisterblick(
			"Liturgie: Auge der wartenden Seelen (Nemekaths Geisterblick)", FeatureGroupType.SpecialFeature), LiturgieAugeDesHändlers(
			"Liturgie: Auge des Händlers", FeatureGroupType.SpecialFeature), LiturgieAugeDesMondes(
			"Liturgie: Auge des Mondes", FeatureGroupType.SpecialFeature), LiturgieAuraDerForm(
			"Liturgie: Aura der Form", FeatureGroupType.SpecialFeature), LiturgieAuraDesRegenbogens(
			"Liturgie: Aura des Regenbogens", FeatureGroupType.SpecialFeature), LiturgieAzilasQuellgesang(
			"Liturgie: Azilas Quellgesang", FeatureGroupType.SpecialFeature), LiturgieBannDerGeisterkräftePraiosMagiebann(
			"Liturgie: Bann der Geisterkräfte (Praios' Magiebann)", FeatureGroupType.SpecialFeature), LiturgieBannfluchDesHeiligenKhalid(
			"Liturgie: Bannfluch des Heiligen Khalid", FeatureGroupType.SpecialFeature), LiturgieBegehenDerHeiligenWasser(
			"Liturgie: Begehen der Heiligen Wasser", FeatureGroupType.SpecialFeature), LiturgieBindungDerSchlange(
			"Liturgie: Bindung der Schlange", FeatureGroupType.SpecialFeature), LiturgieBelemansHochzeit(
			"Liturgie: Belemans Hochzeit", FeatureGroupType.SpecialFeature), LiturgieBishdarielsAngesichtKleineLiturgieDesHlNemekath(
			"Liturgie: Bishdariels Angesicht (Kleine Liturgie des Hl. Nemekath)", FeatureGroupType.SpecialFeature), LiturgieBishdarielsAuge(
			"Liturgie: Bishdariels Auge", FeatureGroupType.SpecialFeature), LiturgieBishdarielsAugeIII(
			"Liturgie: Bishdariels Auge (III)", FeatureGroupType.SpecialFeature), LiturgieBishdarielsAugeIV(
			"Liturgie: Bishdariels Auge (IV)", FeatureGroupType.SpecialFeature), LiturgieBishdarielsWarnung(
			"Liturgie: Bishdariels Warnung", FeatureGroupType.SpecialFeature), LiturgieBlendstrahlAusAlveran(
			"Liturgie: Blendstrahl aus Alveran", FeatureGroupType.SpecialFeature), LiturgieBlickAnDenKlarenHimmelSterneFunkelnImmerfort(
			"Liturgie: Blick an den klaren Himmel (Sterne funkeln immerfort)", FeatureGroupType.SpecialFeature), LiturgieBlickAufDasGeisterwirkenSichtAufMadasWelt(
			"Liturgie: Blick auf das Geisterwirken (Sicht auf Madas Welt)", FeatureGroupType.SpecialFeature), LiturgieBlickDerWeberin(
			"Liturgie: Blick der Weberin", FeatureGroupType.SpecialFeature), LiturgieBlickDurchTairachsAugenBlickDerWeberin(
			"Liturgie: Blick durch Tairachs Augen (Blick der Weberin)", FeatureGroupType.SpecialFeature), LiturgieBlickFürDasHandwerk(
			"Liturgie: Blick für das Handwerk", FeatureGroupType.SpecialFeature), LiturgieBlickInDieErinnerungKleineLiturgieDesHeiligenNemekath(
			"Liturgie: Blick in die Erinnerung (Kleine Liturgie des Heiligen Nemekath)",
			FeatureGroupType.SpecialFeature), LiturgieBlickInDieFlammen("Liturgie: Blick in die Flammen",
			FeatureGroupType.SpecialFeature), LiturgieBlutschwurGroßerEidsegen(
			"Liturgie: Blutschwur (Großer Eidsegen)", FeatureGroupType.SpecialFeature), LiturgieBootssegen(
			"Liturgie: Bootssegen", FeatureGroupType.SpecialFeature), LiturgieBoronsSüßeGnade(
			"Liturgie: Borons süße Gnade", FeatureGroupType.SpecialFeature), LiturgieBoronsSüßeGnadeV(
			"Liturgie: Borons süße Gnade (V)", FeatureGroupType.SpecialFeature), LiturgieBuchprüfung(
			"Liturgie: Buchprüfung", FeatureGroupType.SpecialFeature), LiturgieCanyzethsWeisheit(
			"Liturgie: Canyzeths Weisheit", FeatureGroupType.SpecialFeature), LiturgieCerebornsHandreichungHandwerkssegen(
			"Liturgie: Cereborns Handreichung (Handwerkssegen)", FeatureGroupType.SpecialFeature), LiturgieDaradorsBannDerSchatten(
			"Liturgie: Daradors Bann der Schatten", FeatureGroupType.SpecialFeature), LiturgieDaradorsPrüfenderBlickUnverstellterBlick(
			"Liturgie: Daradors prüfender Blick (Unverstellter Blick)", FeatureGroupType.SpecialFeature), LiturgieDasSchwarzeFellDurchDasRoteBlut(
			"Liturgie: Das Schwarze Fell durch das Rote Blut", FeatureGroupType.SpecialFeature), LiturgieDerGänsemutterWarmesNestZufluchtFinden(
			"Liturgie: Der Gänsemutter warmes Nest (Zuflucht finden)", FeatureGroupType.SpecialFeature), LiturgieDesHerrenGoldenerMittagWeisungDesHimmels(
			"Liturgie: Des Herren Goldener Mittag (Weisung des Himmels)", FeatureGroupType.SpecialFeature), LiturgieDorlensVerbrüderung(
			"Liturgie: Dorlens Verbrüderung", FeatureGroupType.SpecialFeature), LiturgieDreifacherSaatsegen(
			"Liturgie: Dreifacher Saatsegen", FeatureGroupType.SpecialFeature), LiturgieDythlindsWandelnImFeuerVertrauterDerFlamme(
			"Liturgie: Dythlinds Wandeln im Feuer (Vertrauter der Flamme)", FeatureGroupType.SpecialFeature), LiturgieEfferdsegen(
			"Liturgie: Efferdsegen", FeatureGroupType.SpecialFeature), LiturgieEherneKraftLodernderZorn(
			"Liturgie: Eherne Kraft - lodernder Zorn", FeatureGroupType.SpecialFeature), LiturgieEhrenhafterZweikampf(
			"Liturgie: Ehrenhafter Zweikampf", FeatureGroupType.SpecialFeature), LiturgieEidechsenhaut(
			"Liturgie: Eidechsenhaut", FeatureGroupType.SpecialFeature), LiturgieEidsegen("Liturgie: Eidsegen",
			FeatureGroupType.SpecialFeature), LiturgieEinBildFürDieEwigkeit("Liturgie: Ein Bild für die Ewigkeit",
			FeatureGroupType.SpecialFeature), LiturgieEinFreundInZeitenDerNot("Liturgie: Ein Freund in Zeiten der Not",
			FeatureGroupType.SpecialFeature), LiturgieElementwandlung("Liturgie: Elementwandlung",
			FeatureGroupType.SpecialFeature), LiturgieEntzugDesWissensEntzugVonNandusGaben(
			"Liturgie: Entzug des Wissens (Entzug von Nandus' Gaben)", FeatureGroupType.SpecialFeature), LiturgieEntzugVonNandusGaben(
			"Liturgie: Entzug von Nandus' Gaben", FeatureGroupType.SpecialFeature), LiturgieErlösungDesTapamsBoronsSüßeGnade(
			"Liturgie: Erlösung des Tapams (Borons süße Gnade)", FeatureGroupType.SpecialFeature), LiturgieErlösungDesTapamsBoronsSüßeGnadeV(
			"Liturgie: Erlösung des Tapams (Borons süße Gnade (V))", FeatureGroupType.SpecialFeature), LiturgieErneuerungDesGeborstenen(
			"Liturgie: Erneuerung des Geborstenen", FeatureGroupType.SpecialFeature), LiturgieEtiliasGnade(
			"Liturgie: Etilias Gnade", FeatureGroupType.SpecialFeature), LiturgieEtiliasZeitDerMeditationRufZurRuhe(
			"Liturgie: Etilias Zeit der Meditation (Ruf zur Ruhe)", FeatureGroupType.SpecialFeature), LiturgieEwigerWächter(
			"Liturgie: Ewiger Wächter", FeatureGroupType.SpecialFeature), LiturgieEwigesWissen(
			"Liturgie: Ewiges Wissen", FeatureGroupType.SpecialFeature), LiturgieExkommunikation(
			"Liturgie: Exkommunikation", FeatureGroupType.SpecialFeature), LiturgieExkommunikationIV(
			"Liturgie: Exkommunikation (IV)", FeatureGroupType.SpecialFeature), LiturgieExkommunikationV(
			"Liturgie: Exkommunikation (V)", FeatureGroupType.SpecialFeature), LiturgieExorzismus(
			"Liturgie: Exorzismus", FeatureGroupType.SpecialFeature), LiturgieExorzismusIV("Liturgie: Exorzismus (IV)",
			FeatureGroupType.SpecialFeature), LiturgieExorzismusV("Liturgie: Exorzismus (V)",
			FeatureGroupType.SpecialFeature), LiturgieExorzismusVI("Liturgie: Exorzismus (VI)",
			FeatureGroupType.SpecialFeature), LiturgieFeuersegen("Liturgie: Feuersegen",
			FeatureGroupType.SpecialFeature), LiturgieFeuertaufeInitiation("Liturgie: Feuertaufe (Initiation)",
			FeatureGroupType.SpecialFeature), LiturgieFirunsEinsicht("Liturgie: Firuns Einsicht",
			FeatureGroupType.SpecialFeature), LiturgieFlaggeDesRegenbogens("Liturgie: Flagge des Regenbogens",
			FeatureGroupType.SpecialFeature), LiturgieFreundlicheAufnahme("Liturgie: Freundliche Aufnahme",
			FeatureGroupType.SpecialFeature), LiturgieFriedenDerMelodie("Liturgie: Frieden der Melodie",
			FeatureGroupType.SpecialFeature), LiturgieFünfteLobpreisungDesFrühlings(
			"Liturgie: Fünfte Lobpreisung des Frühlings", FeatureGroupType.SpecialFeature), LiturgieFürbittenDesHeiligenTherbun(
			"Liturgie: Fürbitten des Heiligen Therbun", FeatureGroupType.SpecialFeature), LiturgieGarafansGleißendeSchwingen(
			"Liturgie: Garafans Gleißende Schwingen", FeatureGroupType.SpecialFeature), LiturgieGebieterDerLava(
			"Liturgie: Gebieter der Lava", FeatureGroupType.SpecialFeature), LiturgieGeburtssegen(
			"Liturgie: Geburtssegen", FeatureGroupType.SpecialFeature), LiturgieGeistermantelArgelionsMantel(
			"Liturgie: Geistermantel (Argelions Mantel)", FeatureGroupType.SpecialFeature), LiturgieGeläutertSeiErzUndGoldgestein(
			"Liturgie: Geläutert sei Erz und Goldgestein", FeatureGroupType.SpecialFeature), LiturgieGemeinschaftDerTreuenGefährten(
			"Liturgie: Gemeinschaft der treuen Gefährten", FeatureGroupType.SpecialFeature), LiturgieGesangDerDelphine(
			"Liturgie: Gesang der Delphine", FeatureGroupType.SpecialFeature), LiturgieGesangDerWaleRufDerGefährten(
			"Liturgie: Gesang der Wale (Ruf der Gefährten)", FeatureGroupType.SpecialFeature), LiturgieGesangDerWaleRufDerGefährtenIV(
			"Liturgie: Gesang der Wale (Ruf der Gefährten) (IV)", FeatureGroupType.SpecialFeature), LiturgieGesegneterFang(
			"Liturgie: Gesegneter Fang", FeatureGroupType.SpecialFeature), LiturgieGeteiltesLeid(
			"Liturgie: Geteiltes Leid", FeatureGroupType.SpecialFeature), LiturgieGiftDerErkenntnis(
			"Liturgie: Gift der Erkenntnis", FeatureGroupType.SpecialFeature), LiturgieGilbornsHeiligeAuraArgelionsMantel(
			"Liturgie: Gilborns heilige Aura (Argelions Mantel)", FeatureGroupType.SpecialFeature), LiturgieGleichklangDesGeistes(
			"Liturgie: Gleichklang des Geistes", FeatureGroupType.SpecialFeature), LiturgieGlückssegen(
			"Liturgie: Glückssegen", FeatureGroupType.SpecialFeature), LiturgieGoldeneRüstung(
			"Liturgie: Goldene Rüstung", FeatureGroupType.SpecialFeature), LiturgieGoldenerBlick(
			"Liturgie: Goldener Blick", FeatureGroupType.SpecialFeature), LiturgieGorfangsFluch(
			"Liturgie: Gorfangs Fluch", FeatureGroupType.SpecialFeature), LiturgieGorfangsFluchV(
			"Liturgie: Gorfangs Fluch (V)", FeatureGroupType.SpecialFeature), LiturgieGorfangsFluchVI(
			"Liturgie: Gorfangs Fluch (VI)", FeatureGroupType.SpecialFeature), LiturgieGöttlicheVerständigung(
			"Liturgie: Göttliche Verständigung", FeatureGroupType.SpecialFeature), LiturgieGöttlicheVerständigungIII(
			"Liturgie: Göttliche Verständigung (III)", FeatureGroupType.SpecialFeature), LiturgieGöttlicheVerständigungIV(
			"Liturgie: Göttliche Verständigung (IV)", FeatureGroupType.SpecialFeature), LiturgieGöttlichesZeichen(
			"Liturgie: Göttliches Zeichen", FeatureGroupType.SpecialFeature), LiturgieGöttlichesZeichenIII(
			"Liturgie: Göttliches Zeichen (III)", FeatureGroupType.SpecialFeature), LiturgieGrabsegen(
			"Liturgie: Grabsegen", FeatureGroupType.SpecialFeature), LiturgieGrauesSiegel("Liturgie: Graues Siegel",
			FeatureGroupType.SpecialFeature), LiturgieGrauesSiegelIV("Liturgie: Graues Siegel (IV)",
			FeatureGroupType.SpecialFeature), LiturgieGrispelzAckersegenDreifacherSaatsegen(
			"Liturgie: Grispelz' Ackersegen (Dreifacher Saatsegen)", FeatureGroupType.SpecialFeature), LiturgieGrispelzFruchtbarkeitTsasWundersameFruchtbarkeit(
			"Liturgie: Grispelz' Fruchtbarkeit (Tsas Wundersame Fruchtbarkeit", FeatureGroupType.SpecialFeature), LiturgieGroßeSeelenwaschungExorzismus(
			"Liturgie: Große Seelenwaschung (Exorzismus)", FeatureGroupType.SpecialFeature), LiturgieGroßeSeelenwaschungExorzismusIV(
			"Liturgie: Große Seelenwaschung (Exorzismus) (IV)", FeatureGroupType.SpecialFeature), LiturgieGroßeSeelenwaschungExorzismusV(
			"Liturgie: Große Seelenwaschung (Exorzismus) (V)", FeatureGroupType.SpecialFeature), LiturgieGroßeSeelenwaschungExorzismusVI(
			"Liturgie: Große Seelenwaschung (Exorzismus) (VI)", FeatureGroupType.SpecialFeature), LiturgieGroßeWeiheDesHeimsteins(
			"Liturgie: Große Weihe des Heimsteins", FeatureGroupType.SpecialFeature), LiturgieGroßerEidsegen(
			"Liturgie: Großer Eidsegen", FeatureGroupType.SpecialFeature), LiturgieGroßerGiftbannGroßerSpeisesegen(
			"Liturgie: Großer Giftbann (Großer Speisesegen)", FeatureGroupType.SpecialFeature), LiturgieGroßerReisesegen(
			"Liturgie: Großer Reisesegen", FeatureGroupType.SpecialFeature), LiturgieGroßerSpeisesegen(
			"Liturgie: Großer Speisesegen", FeatureGroupType.SpecialFeature), LiturgieGroßerWeihesegenDerWaffe(
			"Liturgie: Großer Weihesegen der Waffe", FeatureGroupType.SpecialFeature), LiturgieGroßesTabuSiegelBorons(
			"Liturgie: Großes Tabu (Siegel Borons)", FeatureGroupType.SpecialFeature), LiturgieGrußDesVersunkenen(
			"Liturgie: Gruß des Versunkenen", FeatureGroupType.SpecialFeature), LiturgieHändlersegen(
			"Liturgie: Händlersegen", FeatureGroupType.SpecialFeature), LiturgieHandwerkssegen(
			"Liturgie: Handwerkssegen", FeatureGroupType.SpecialFeature), LiturgieHarmoniesegen(
			"Liturgie: Harmoniesegen", FeatureGroupType.SpecialFeature), LiturgieHashnabithsFlehen(
			"Liturgie: Hashnabiths Flehen", FeatureGroupType.SpecialFeature), LiturgieHauchBorons(
			"Liturgie: Hauch Borons", FeatureGroupType.SpecialFeature), LiturgieHauchDerLeidenschaftHandwerkssegen(
			"Liturgie: Hauch der Leidenschaft (Handwerkssegen)", FeatureGroupType.SpecialFeature), LiturgieHausfrieden(
			"Liturgie: Hausfrieden", FeatureGroupType.SpecialFeature), LiturgieHautDesChamäleonsVerborgenWieDerNeumond(
			"Liturgie: Haut des Chamäleons (Verborgen wie der Neumond)", FeatureGroupType.SpecialFeature), LiturgieHeiligeSalbungDerPeraineTsasHeiligesLebensgeschenk(
			"Liturgie: Heilige Salbung der Peraine (Tsas Heiliges Lebensgeschenk)", FeatureGroupType.SpecialFeature), LiturgieHeiligeSchmiedeglut(
			"Liturgie: Heilige Schmiedeglut", FeatureGroupType.SpecialFeature), LiturgieHeiligerBefehl(
			"Liturgie: Heiliger Befehl", FeatureGroupType.SpecialFeature), LiturgieHeiligerLehnseid(
			"Liturgie: Heiliger Lehnseid", FeatureGroupType.SpecialFeature), LiturgieHeiligesLiebesspiel(
			"Liturgie: Heiliges Liebesspiel", FeatureGroupType.SpecialFeature), LiturgieHeilungDesTapamsSegenDerHeiligenNoiona(
			"Liturgie: Heilung des Tapams (Segen der Heiligen Noiona)", FeatureGroupType.SpecialFeature), LiturgieHeilungDesTapamsSegenDerHeiligenNoionaIV(
			"Liturgie: Heilung des Tapams (Segen der Heiligen Noiona (IV))", FeatureGroupType.SpecialFeature), LiturgieHeilungssegen(
			"Liturgie: Heilungssegen", FeatureGroupType.SpecialFeature), LiturgieHerrÜberFeuerUndGlut(
			"Liturgie: Herr über Feuer und Glut", FeatureGroupType.SpecialFeature), LiturgieHesindesFingerzeigBuchprüfung(
			"Liturgie: Hesindes Fingerzeig (Buchprüfung)", FeatureGroupType.SpecialFeature), LiturgieHilfeInDerNot(
			"Liturgie: Hilfe in der Not", FeatureGroupType.SpecialFeature), LiturgieHoftagDerSprachen(
			"Liturgie: Hoftag der Sprachen", FeatureGroupType.SpecialFeature), LiturgieIndoktrination(
			"Liturgie: Indoktrination", FeatureGroupType.SpecialFeature), LiturgieIngalfsAlchimie(
			"Liturgie: Ingalfs Alchimie", FeatureGroupType.SpecialFeature), LiturgieIngerimmsZornVerschoneUns(
			"Liturgie: Ingerimms Zorn verschone uns", FeatureGroupType.SpecialFeature), LiturgieInitiation(
			"Liturgie: Initiation", FeatureGroupType.SpecialFeature), LiturgieInnereRuhe("Liturgie: Innere Ruhe",
			FeatureGroupType.SpecialFeature), LiturgieJagdglück("Liturgie: Jagdglück", FeatureGroupType.SpecialFeature), LiturgieKälbchensegen(
			"Liturgie: Kälbchensegen", FeatureGroupType.SpecialFeature), LiturgieKamaluqsUnerbittlicherSpeerBannfluchDesHeiligenKhalid(
			"Liturgie: Kamaluqs unerbittlicher Speer (Bannfluch des Heiligen Khalid)", FeatureGroupType.SpecialFeature), LiturgieKamaluqsFluch(
			"Liturgie: Kamaluqs Fluch", FeatureGroupType.SpecialFeature), LiturgieKamaluqsFluchV(
			"Liturgie: Kamaluqs Fluch (V)", FeatureGroupType.SpecialFeature), LiturgieKamaluqsFluchVI(
			"Liturgie: Kamaluqs Fluch (VI)", FeatureGroupType.SpecialFeature), LiturgieKetaAjabanKudaWundersamesTeilenDesMartyriums(
			"Liturgie: Keta ajaban kud'a - Wundersames Teilen des Martyriums", FeatureGroupType.SpecialFeature), LiturgieKetaAjabanKudaWundersamesTeilenDesMartyriumsV(
			"Liturgie: Keta ajaban kud'a - Wundersames Teilen des Martyriums (V)", FeatureGroupType.SpecialFeature), LiturgieKetaAjabanKudaWundersamesTeilenDesMartyriumsVI(
			"Liturgie: Keta ajaban kud'a - Wundersames Teilen des Martyriums (VI)", FeatureGroupType.SpecialFeature), LiturgieKhablasJugend(
			"Liturgie: Khablas Jugend", FeatureGroupType.SpecialFeature), LiturgieKhablasJugendIV(
			"Liturgie: Khablas Jugend (IV)", FeatureGroupType.SpecialFeature), LiturgieKhablasMakelloserLeib(
			"Liturgie: Khablas makelloser Leib", FeatureGroupType.SpecialFeature), LiturgieKhablasMakelloserLeibVI(
			"Liturgie: Khablas makelloser Leib (VI)", FeatureGroupType.SpecialFeature), LiturgieKirschblütenregen(
			"Liturgie: Kirschblütenregen", FeatureGroupType.SpecialFeature), LiturgieKleineLiturgieDesHeiligenNemekath(
			"Liturgie: Kleine Liturgie des Heiligen Nemekath", FeatureGroupType.SpecialFeature), LiturgieKleineSegnungDesHeimsteins(
			"Liturgie: Kleine Segnung des Heimsteins", FeatureGroupType.SpecialFeature), LiturgieKleinerGiftbann(
			"Liturgie: Kleiner Giftbann", FeatureGroupType.SpecialFeature), LiturgieKleinesTabuRufZurRuhe(
			"Liturgie: Kleines Tabu (Ruf zur Ruhe)", FeatureGroupType.SpecialFeature), LiturgieKonsekration(
			"Liturgie: Konsekration", FeatureGroupType.SpecialFeature), LiturgieKräftigungDerSchwachenUndVersehrten(
			"Liturgie: Kräftigung der Schwachen und Versehrten", FeatureGroupType.SpecialFeature), LiturgieKräutersegenDesHeiligenNemekathRahjasRauschsegen(
			"Liturgie: Kräutersegen des Heiligen Nemekath (Rahjas Rauschsegen)", FeatureGroupType.SpecialFeature), LiturgieLagoraxHammerRufen(
			"Liturgie: Lagorax' Hammer rufen", FeatureGroupType.SpecialFeature), LiturgieLevthansFesseln(
			"Liturgie: Levthans Fesseln", FeatureGroupType.SpecialFeature), LiturgieLichtDesHerrn(
			"Liturgie: Licht des Herrn", FeatureGroupType.SpecialFeature), LiturgieLichtDesVerborgenenPfades(
			"Liturgie: Licht des verborgenen Pfades", FeatureGroupType.SpecialFeature), LiturgieLohnDerUnverzagten(
			"Liturgie: Lohn der Unverzagten", FeatureGroupType.SpecialFeature), LiturgieLugUndTrugUnverstellterBlick(
			"Liturgie: Lug und Trug (Unverstellter Blick)", FeatureGroupType.SpecialFeature), LiturgieMannschaftssegen(
			"Liturgie: Mannschaftssegen", FeatureGroupType.SpecialFeature), LiturgieMarbosGeisterblickNemekathsGeisterblick(
			"Liturgie: Marbos Geisterblick (Nemekaths Geisterblick)", FeatureGroupType.SpecialFeature), LiturgieMarbosGeleit(
			"Liturgie: Marbos Geleit", FeatureGroupType.SpecialFeature), LiturgieMärtyrersegen(
			"Liturgie: Märtyrersegen", FeatureGroupType.SpecialFeature), LiturgieMeisterstückWandelnInHesindesHain(
			"Liturgie: Meisterstück (Wandeln in Hesindes Hain)", FeatureGroupType.SpecialFeature), LiturgieMondsilberzunge(
			"Liturgie: Mondsilberzunge", FeatureGroupType.SpecialFeature), LiturgieNandusSchriftkenntnisSchrifttumFernerLande(
			"Liturgie: Nandus Schriftkenntnis (Schrifttum ferner Lande)", FeatureGroupType.SpecialFeature), LiturgieNemekathsBannfluchBishdarielsWarnung(
			"Liturgie: Nemekaths Bannfluch (Bishdariels Warnung)", FeatureGroupType.SpecialFeature), LiturgieNemekathsGeisterblick(
			"Liturgie: Nemekaths Geisterblick", FeatureGroupType.SpecialFeature), LiturgieNemekathsZwiesprache(
			"Liturgie: Nemekaths Zwiesprache", FeatureGroupType.SpecialFeature), LiturgieNeunStreicheInEinem(
			"Liturgie: Neun Streiche in Einem", FeatureGroupType.SpecialFeature), LiturgieNimmermüdeWanderschaft(
			"Liturgie: Nimmermüde Wanderschaft", FeatureGroupType.SpecialFeature), LiturgieNoionasZuspruchSegenDerHlVelvenya(
			"Liturgie: Noionas Zuspruch (Segen der Hl. Velvenya)", FeatureGroupType.SpecialFeature), LiturgieObaransBannstrahlZerschmetternderBannstrahl(
			"Liturgie: Obarans Bannstrahl (Zerschmetternder Bannstrahl)", FeatureGroupType.SpecialFeature), LiturgieObjektsegen(
			"Liturgie: Objektsegen", FeatureGroupType.SpecialFeature), LiturgieObjektsegenII(
			"Liturgie: Objektsegen (II)", FeatureGroupType.SpecialFeature), LiturgieObjektsegenIV(
			"Liturgie: Objektsegen (IV)", FeatureGroupType.SpecialFeature), LiturgieObjektsegenV(
			"Liturgie: Objektsegen (V)", FeatureGroupType.SpecialFeature), LiturgieObjektweihe("Liturgie: Objektweihe",
			FeatureGroupType.SpecialFeature), LiturgieObjektweiheIII("Liturgie: Objektweihe (III)",
			FeatureGroupType.SpecialFeature), LiturgieObjektweiheIV("Liturgie: Objektweihe (IV)",
			FeatureGroupType.SpecialFeature), LiturgieObjektweiheV("Liturgie: Objektweihe (V)",
			FeatureGroupType.SpecialFeature), LiturgieObjektweiheVI("Liturgie: Objektweihe (VI)",
			FeatureGroupType.SpecialFeature), LiturgieOrdination("Liturgie: Ordination",
			FeatureGroupType.SpecialFeature), LiturgieParinorsVermächtnis("Liturgie: Parinors Vermächtnis",
			FeatureGroupType.SpecialFeature), LiturgiePerainesPflanzengespür("Liturgie: Peraines Pflanzengespür",
			FeatureGroupType.SpecialFeature), LiturgiePhexensAugenzwinkern("Liturgie: Phexens Augenzwinkern",
			FeatureGroupType.SpecialFeature), LiturgiePhexensElsterflug("Liturgie: Phexens Elsterflug",
			FeatureGroupType.SpecialFeature), LiturgiePhexensKunstverstandBlickFürDasHandwerk(
			"Liturgie: Phexens Kunstverstand (Blick für das Handwerk)", FeatureGroupType.SpecialFeature), LiturgiePhexensMeisterschlüssel(
			"Liturgie: Phexens Meisterschlüssel", FeatureGroupType.SpecialFeature), LiturgiePhexensNebelleib(
			"Liturgie: Phexens Nebelleib", FeatureGroupType.SpecialFeature), LiturgiePhexensSchatten(
			"Liturgie: Phexens Schatten", FeatureGroupType.SpecialFeature), LiturgiePhexensSternenwurf(
			"Liturgie: Phexens Sternenwurf", FeatureGroupType.SpecialFeature), LiturgiePhexensWunderbareVerständigung(
			"Liturgie: Phexens wunderbare Verständigung", FeatureGroupType.SpecialFeature), LiturgiePraiosMagiebann(
			"Liturgie: Praios' Magiebann", FeatureGroupType.SpecialFeature), LiturgiePraiosMahnung(
			"Liturgie: Praios Mahnung", FeatureGroupType.SpecialFeature), LiturgieProphezeiung(
			"Liturgie: Prophezeiung", FeatureGroupType.SpecialFeature), LiturgiePurgation("Liturgie: Purgation",
			FeatureGroupType.SpecialFeature), LiturgieQuellsegen("Liturgie: Quellsegen",
			FeatureGroupType.SpecialFeature), LiturgieRahjalinasKuss("Liturgie: Rahjalinas Kuss",
			FeatureGroupType.SpecialFeature), LiturgieRahjalinasWeinrankeParinorsVermächtnis(
			"Liturgie: Rahjalinas Weinranke (Parinors Vermächtnis)", FeatureGroupType.SpecialFeature), LiturgieRahjasErquickungSchlafDesGesegneten(
			"Liturgie: Rahjas Erquickung (Schlaf des Gesegneten)", FeatureGroupType.SpecialFeature), LiturgieRahjasFestDerFreude(
			"Liturgie: Rahjas Fest der Freude", FeatureGroupType.SpecialFeature), LiturgieRahjasFreiheit(
			"Liturgie: Rahjas Freiheit", FeatureGroupType.SpecialFeature), LiturgieRahjasGeheiligterWein(
			"Liturgie: Rahjas geheiligter Wein", FeatureGroupType.SpecialFeature), LiturgieRahjasHeitereGelassenheitSegenDerHeiligenNoiona(
			"Liturgie: Rahjas heitere Gelassenheit (Segen der Heiligen Noiona)", FeatureGroupType.SpecialFeature), LiturgieRahjasHeitereGelassenheitSegenDerHeiligenNoionaIV(
			"Liturgie: Rahjas heitere Gelassenheit (Segen der Heiligen Noiona (IV))", FeatureGroupType.SpecialFeature), LiturgieRahjasRauschsegen(
			"Liturgie: Rahjas Rauschsegen", FeatureGroupType.SpecialFeature), LiturgieRahjasSchoß(
			"Liturgie: Rahjas Schoß", FeatureGroupType.SpecialFeature), LiturgieRahjasSinnlichkeit(
			"Liturgie: Rahjas Sinnlichkeit", FeatureGroupType.SpecialFeature), LiturgieReichungDesAmethyst(
			"Liturgie: Reichung des Amethyst", FeatureGroupType.SpecialFeature), LiturgieReichungDesAmethystII(
			"Liturgie: Reichung des Amethyst (II)", FeatureGroupType.SpecialFeature), LiturgieReichungDesAmethystIII(
			"Liturgie: Reichung des Amethyst (III)", FeatureGroupType.SpecialFeature), LiturgieReichungDesAmethystV(
			"Liturgie: Reichung des Amethyst (V)", FeatureGroupType.SpecialFeature), LiturgieReisesegen(
			"Liturgie: Reisesegen", FeatureGroupType.SpecialFeature), LiturgieRondrasHochzeit(
			"Liturgie: Rondras Hochzeit", FeatureGroupType.SpecialFeature), LiturgieRondrasWundersameRüstung(
			"Liturgie: Rondras wundersame Rüstung", FeatureGroupType.SpecialFeature), LiturgieRufDerGefährten(
			"Liturgie: Ruf der Gefährten", FeatureGroupType.SpecialFeature), LiturgieRufDerGefährtenIV(
			"Liturgie: Ruf der Gefährten (IV)", FeatureGroupType.SpecialFeature), LiturgieRufInBoronsArme(
			"Liturgie: Ruf in Borons Arme", FeatureGroupType.SpecialFeature), LiturgieRufInBoronsArmeIII(
			"Liturgie: Ruf in Borons Arme (III)", FeatureGroupType.SpecialFeature), LiturgieRufZurRuhe(
			"Liturgie: Ruf zur Ruhe", FeatureGroupType.SpecialFeature), LiturgieSalbungDerHeiligenNoionaExorzismus(
			"Liturgie: Salbung der Heiligen Noiona (Exorzismus)", FeatureGroupType.SpecialFeature), LiturgieSalbungDerHeiligenNoionaExorzismusIV(
			"Liturgie: Salbung der Heiligen Noiona (Exorzismus) (IV)", FeatureGroupType.SpecialFeature), LiturgieSalbungDerHeiligenNoionaExorzismusV(
			"Liturgie: Salbung der Heiligen Noiona (Exorzismus) (V)", FeatureGroupType.SpecialFeature), LiturgieSalbungDerHeiligenNoionaExorzismusVI(
			"Liturgie: Salbung der Heiligen Noiona (Exorzismus) (VI)", FeatureGroupType.SpecialFeature), LiturgieSanktGilbornsBannfluchArgelionsBannendeHand(
			"Liturgie: Sankt Gilborns Bannfluch (Argelions bannende Hand)", FeatureGroupType.SpecialFeature), LiturgieSchattenlarve(
			"Liturgie: Schattenlarve", FeatureGroupType.SpecialFeature), LiturgieSchiffssegen("Liturgie: Schiffssegen",
			FeatureGroupType.SpecialFeature), LiturgieSchlafDesGesegneten("Liturgie: Schlaf des Gesegneten",
			FeatureGroupType.SpecialFeature), LiturgieSchlangenstab("Liturgie: Schlangenstab",
			FeatureGroupType.SpecialFeature), LiturgieSchneesturmEissturm("Liturgie: Schneesturm/Eissturm",
			FeatureGroupType.SpecialFeature), LiturgieSchrifttumFernerLande("Liturgie: Schrifttum ferner Lande",
			FeatureGroupType.SpecialFeature), LiturgieSchutzDesGeleges("Liturgie: Schutz des Geleges",
			FeatureGroupType.SpecialFeature), LiturgieSchutzsegen("Liturgie: Schutzsegen",
			FeatureGroupType.SpecialFeature), LiturgieSchutzsegenII("Liturgie: Schutzsegen (II)",
			FeatureGroupType.SpecialFeature), LiturgieSchutzsegenIII("Liturgie: Schutzsegen (III)",
			FeatureGroupType.SpecialFeature), LiturgieSchwitzhütteFünfteLobpreisungDesFrühlings(
			"Liturgie: Schwitzhütte (Fünfte Lobpreisung des Frühlings)", FeatureGroupType.SpecialFeature), LiturgieSeelenprüfung(
			"Liturgie: Seelenprüfung", FeatureGroupType.SpecialFeature), LiturgieAuraprüfung("Liturgie: Auraprüfung",
			FeatureGroupType.SpecialFeature), LiturgieGroßeSeelenprüfung("Liturgie: Große Seelenprüfung",
			FeatureGroupType.SpecialFeature), LiturgieSegenDerGabetajParinorsVermächtnis(
			"Liturgie: Segen der Gabetaj (Parinors Vermächtnis)", FeatureGroupType.SpecialFeature), LiturgieSegenDerHeiligenArdare(
			"Liturgie: Segen der Heiligen Ardare", FeatureGroupType.SpecialFeature), LiturgieSegenDerHeiligenNoiona(
			"Liturgie: Segen der Heiligen Noiona", FeatureGroupType.SpecialFeature), LiturgieSegenDerHeiligenNoionaIV(
			"Liturgie: Segen der Heiligen Noiona (IV)", FeatureGroupType.SpecialFeature), LiturgieSegenDerHeiligenTheria(
			"Liturgie: Segen der Heiligen Theria", FeatureGroupType.SpecialFeature), LiturgieSegenDerHeiligenVelvenya(
			"Liturgie: Segen der Heiligen Velvenya", FeatureGroupType.SpecialFeature), LiturgieSegenDesHeiligenBadilakSegenDerHeiligenNoiona(
			"Liturgie: Segen des Heiligen Badilak (Segen der Heiligen Noiona)", FeatureGroupType.SpecialFeature), LiturgieSegenDesHeiligenBadilakSegenDerHeiligenNoionaIV(
			"Liturgie: Segen des Heiligen Badilak (Segen der Heiligen Noiona (IV))", FeatureGroupType.SpecialFeature), LiturgieSegenDesHeiligenHlûthar(
			"Liturgie: Segen des Heiligen Hlûthar", FeatureGroupType.SpecialFeature), LiturgieSegenDesPlättlings(
			"Liturgie: Segen des Plättlings", FeatureGroupType.SpecialFeature), LiturgieSegensreichesWasser(
			"Liturgie: Segensreiches Wasser", FeatureGroupType.SpecialFeature), LiturgieSegnungDerSchlacht(
			"Liturgie: Segnung der Schlacht", FeatureGroupType.SpecialFeature), LiturgieSegnungDerStählernenStirn(
			"Liturgie: Segnung der Stählernen Stirn", FeatureGroupType.SpecialFeature), LiturgieSegnungDesHeimes(
			"Liturgie: Segnung des Heimes", FeatureGroupType.SpecialFeature), LiturgieSichereWanderungImSchnee(
			"Liturgie: Sichere Wanderung im Schnee", FeatureGroupType.SpecialFeature), LiturgieSichererWegDurchFels(
			"Liturgie: Sicherer Weg durch Fels", FeatureGroupType.SpecialFeature), LiturgieSichtAufMadasWelt(
			"Liturgie: Sicht auf Madas Welt", FeatureGroupType.SpecialFeature), LiturgieSiegelBorons(
			"Liturgie: Siegel Borons", FeatureGroupType.SpecialFeature), LiturgieSimiasKelchTsasSegensreicherNeuanfang(
			"Liturgie: Simias Kelch (Tsas Segensreicher Neuanfang)", FeatureGroupType.SpecialFeature), LiturgieSippenfluch(
			"Liturgie: Sippenfluch", FeatureGroupType.SpecialFeature), LiturgieSpeisesegen("Liturgie: Speisesegen",
			FeatureGroupType.SpecialFeature), LiturgieSpeisungDerBedürftigen("Liturgie: Speisung der Bedürftigen",
			FeatureGroupType.SpecialFeature), LiturgieSpeisungDerBedürftigenIV(
			"Liturgie: Speisung der Bedürftigen (IV)", FeatureGroupType.SpecialFeature), LiturgieSpeisungDerHungerndenSeele(
			"Liturgie: Speisung der hungernden Seele", FeatureGroupType.SpecialFeature), LiturgieSprechendeSymbole(
			"Liturgie: Sprechende Symbole", FeatureGroupType.SpecialFeature), LiturgieSterneFunkelnImmerfort(
			"Liturgie: Sterne funkeln immerfort", FeatureGroupType.SpecialFeature), LiturgieSternenglanz(
			"Liturgie: Sternenglanz", FeatureGroupType.SpecialFeature), LiturgieSternenspur("Liturgie: Sternenspur",
			FeatureGroupType.SpecialFeature), LiturgieSternenstaub("Liturgie: Sternenstaub",
			FeatureGroupType.SpecialFeature), LiturgieSulvasGnade("Liturgie: Sulvas Gnade",
			FeatureGroupType.SpecialFeature), LiturgieSwafnirsFluke("Liturgie: Swafnirs Fluke",
			FeatureGroupType.SpecialFeature), LiturgieSwafnirsRuhelied("Liturgie: Swafnirs Ruhelied",
			FeatureGroupType.SpecialFeature), LiturgieTabuSiegelBorons("Liturgie: Tabu (Siegel Borons)",
			FeatureGroupType.SpecialFeature), LiturgieTairachsFluch("Liturgie: Tairachs Fluch",
			FeatureGroupType.SpecialFeature), LiturgieTairachsFluchV("Liturgie: Tairachs Fluch (V)",
			FeatureGroupType.SpecialFeature), LiturgieTairachsFluchVI("Liturgie: Tairachs Fluch (VI)",
			FeatureGroupType.SpecialFeature), LiturgieThalionmelsSchlachtgesang("Liturgie: Thalionmels Schlachtgesang",
			FeatureGroupType.SpecialFeature), LiturgieTherbûnsErkenntnis("Liturgie: Therbûns Erkenntnis",
			FeatureGroupType.SpecialFeature), LiturgieTierempathie("Liturgie: Tierempathie",
			FeatureGroupType.SpecialFeature), LiturgieTiergestalt("Liturgie: Tiergestalt",
			FeatureGroupType.SpecialFeature), LiturgieTiersprache("Liturgie: Tiersprache",
			FeatureGroupType.SpecialFeature), LiturgieTranksegen("Liturgie: Tranksegen",
			FeatureGroupType.SpecialFeature), LiturgieTraviabundGroßerEidsegen(
			"Liturgie: Traviabund (Großer Eidsegen)", FeatureGroupType.SpecialFeature), LiturgieTraviasGebetDerSicherenZuflucht(
			"Liturgie: Travias Gebet der sicheren Zuflucht", FeatureGroupType.SpecialFeature), LiturgieTraviasGebetDerVerborgenenHalle(
			"Liturgie: Travias Gebet der verborgenen Halle", FeatureGroupType.SpecialFeature), LiturgieTraviniansSegenDerSchwelle(
			"Liturgie: Travinians Segen der Schwelle", FeatureGroupType.SpecialFeature), LiturgieTrophäeErhalten(
			"Liturgie: Trophäe erhalten", FeatureGroupType.SpecialFeature), LiturgieTsasEwigeJugend(
			"Liturgie: Tsas ewige Jugend", FeatureGroupType.SpecialFeature), LiturgieTsasHeiligesLebensgeschenk(
			"Liturgie: Tsas Heiliges Lebensgeschenk", FeatureGroupType.SpecialFeature), LiturgieTsasLebensschutz(
			"Liturgie: Tsas Lebensschutz", FeatureGroupType.SpecialFeature), LiturgieTsasSegensreicherNeuanfang(
			"Liturgie: Tsas Segensreicher Neuanfang", FeatureGroupType.SpecialFeature), LiturgieTsasWunderbareErneuerung(
			"Liturgie: Tsas Wunderbare Erneuerung", FeatureGroupType.SpecialFeature), LiturgieTsasWundersameFruchtbarkeit(
			"Liturgie: Tsas Wundersame Fruchtbarkeit", FeatureGroupType.SpecialFeature), LiturgieÜberDieWolken(
			"Liturgie: Über die Wolken", FeatureGroupType.SpecialFeature), LiturgieÜberDieWolkenVI(
			"Liturgie: Über die Wolken (VI)", FeatureGroupType.SpecialFeature), LiturgieUcurisGeleit(
			"Liturgie: Ucuris Geleit", FeatureGroupType.SpecialFeature), LiturgieUnterpfandDesHeiligenRhys(
			"Liturgie: Unterpfand des Heiligen Rhys", FeatureGroupType.SpecialFeature), LiturgieUnverstellterBlick(
			"Liturgie: Unverstellter Blick", FeatureGroupType.SpecialFeature), LiturgieUrischarsOrdnenderBlick(
			"Liturgie: Urischars ordnender Blick", FeatureGroupType.SpecialFeature), LiturgieVaêsTränen(
			"Liturgie: Vaês Tränen", FeatureGroupType.SpecialFeature), LiturgieVerborgenWieDerNeumond(
			"Liturgie: Verborgen wie der Neumond", FeatureGroupType.SpecialFeature), LiturgieVersiegeltesWissenGrauesSiegel(
			"Liturgie: Versiegeltes Wissen (Graues Siegel)", FeatureGroupType.SpecialFeature), LiturgieVersiegeltesWissenGrauesSiegelIV(
			"Liturgie: Versiegeltes Wissen (Graues Siegel) (IV)", FeatureGroupType.SpecialFeature), LiturgieVertrauterDerFlamme(
			"Liturgie: Vertrauter der Flamme", FeatureGroupType.SpecialFeature), LiturgieVertrauterDesFelsens(
			"Liturgie: Vertrauter des Felsens", FeatureGroupType.SpecialFeature), LiturgieVertreibungDesDunkelsinns(
			"Liturgie: Vertreibung des Dunkelsinns", FeatureGroupType.SpecialFeature), LiturgieVisionssuche(
			"Liturgie: Visionssuche", FeatureGroupType.SpecialFeature), LiturgieWaliburiasWehr(
			"Liturgie: Waliburias Wehr", FeatureGroupType.SpecialFeature), LiturgieWandelnInHesindesHain(
			"Liturgie: Wandeln in Hesindes Hain", FeatureGroupType.SpecialFeature), LiturgieWegDesFuchses(
			"Liturgie: Weg des Fuchses", FeatureGroupType.SpecialFeature), LiturgieWeiheDerEwigenFlamme(
			"Liturgie: Weihe der Ewigen Flamme", FeatureGroupType.SpecialFeature), LiturgieWeiheDerLetztenRuhestatt(
			"Liturgie: Weihe der letzten Ruhestatt", FeatureGroupType.SpecialFeature), LiturgieWeihegesangDerHeiligenElidaVonSalza(
			"Liturgie: Weihegesang der Heiligen Elida von Salza", FeatureGroupType.SpecialFeature), LiturgieWeisheitssegen(
			"Liturgie: Weisheitssegen", FeatureGroupType.SpecialFeature), LiturgieWeisungDesHimmels(
			"Liturgie: Weisung des Himmels", FeatureGroupType.SpecialFeature), LiturgieWilleZurWahrheit(
			"Liturgie: Wille zur Wahrheit", FeatureGroupType.SpecialFeature), LiturgieWinterschlaf(
			"Liturgie: Winterschlaf", FeatureGroupType.SpecialFeature), LiturgieWortDerWahrheitHeiligerBefehl(
			"Liturgie: Wort der Wahrheit (Heiliger Befehl)", FeatureGroupType.SpecialFeature), LiturgieWunderbarerGeschlechterwandel(
			"Liturgie: Wunderbarer Geschlechterwandel", FeatureGroupType.SpecialFeature), LiturgieWundersameBlütenpracht(
			"Liturgie: Wundersame Blütenpracht", FeatureGroupType.SpecialFeature), LiturgieWundsegen(
			"Liturgie: Wundsegen", FeatureGroupType.SpecialFeature), LiturgieWundsegenIII("Liturgie: Wundsegen (III)",
			FeatureGroupType.SpecialFeature), LiturgieWundsegenIV("Liturgie: Wundsegen (IV)",
			FeatureGroupType.SpecialFeature), LiturgieZerschmetternderBannstrahl(
			"Liturgie: Zerschmetternder Bannstrahl", FeatureGroupType.SpecialFeature), LiturgieZufluchtFinden(
			"Liturgie: Zuflucht finden", FeatureGroupType.SpecialFeature), LiturgieNamenlosesVergessen(
			"Liturgie: Namenloses Vergessen", FeatureGroupType.SpecialFeature), LiturgieHerbeirufungDerDienerDesHerren(
			"Liturgie: Herbeirufung der Diener des Herren", FeatureGroupType.SpecialFeature), LiturgieNamenloseKälte(
			"Liturgie: Namenlose Kälte", FeatureGroupType.SpecialFeature), LiturgieDesEinenBezaubernderSphärenklang(
			"Liturgie: Des Einen bezaubernder Sphärenklang", FeatureGroupType.SpecialFeature), LiturgieNamenloserZweifelNamenloseErleuchtung(
			"Liturgie: Namenloser Zweifel - Namenlose Erleuchtung", FeatureGroupType.SpecialFeature), LiturgieSeelenschatten(
			"Liturgie: Seelenschatten", FeatureGroupType.SpecialFeature), LiturgieSchwindendeZauberkraft(
			"Liturgie: Schwindende Zauberkraft", FeatureGroupType.SpecialFeature), LiturgieFluchWiderDieUngläubigen(
			"Liturgie: Fluch wider die Ungläubigen", FeatureGroupType.SpecialFeature), LiturgieGoldeneHand(
			"Liturgie: Goldene Hand", FeatureGroupType.SpecialFeature), LiturgieNamenloseRaserei(
			"Liturgie: Namenlose Raserei", FeatureGroupType.SpecialFeature), LiturgiePechUndSchwefel(
			"Liturgie: Pech und Schwefel", FeatureGroupType.SpecialFeature), LiturgieGottDerGötter(
			"Liturgie: Gott der Götter", FeatureGroupType.SpecialFeature), LiturgieGottDerGötterIII(
			"Liturgie: Gott der Götter (III)", FeatureGroupType.SpecialFeature), LiturgieGottDerGötterIV(
			"Liturgie: Gott der Götter (IV)", FeatureGroupType.SpecialFeature), LiturgieGottDerGötterV(
			"Liturgie: Gott der Götter (V)", FeatureGroupType.SpecialFeature), LiturgieGottDerGötterVI(
			"Liturgie: Gott der Götter (VI)", FeatureGroupType.SpecialFeature), LiturgieHerbeirufungDerHeerscharenDesRattenkindes(
			"Liturgie: Herbeirufung der Heerscharen des Rattenkindes", FeatureGroupType.SpecialFeature), LiturgieWaffenfluch(
			"Liturgie: Waffenfluch", FeatureGroupType.SpecialFeature), LiturgieSchleichendeFäulnis(
			"Liturgie: Schleichende Fäulnis", FeatureGroupType.SpecialFeature), LiturgieEwigeJugend(
			"Liturgie: Ewige Jugend", FeatureGroupType.SpecialFeature), LiturgieSeelenbannung(
			"Liturgie: Seelenbannung", FeatureGroupType.SpecialFeature), LockeresZaubern("Lockeres Zaubern",
			FeatureGroupType.SpecialFeature), Maraskankundig("Maraskankundig", FeatureGroupType.SpecialFeature), Matrixgeber(
			"Matrixgeber", FeatureGroupType.SpecialFeature), Matrixkontrolle("Matrixkontrolle",
			FeatureGroupType.SpecialFeature), MatrixregenerationI("Matrixregeneration I",
			FeatureGroupType.SpecialFeature), MatrixregenerationII("Matrixregeneration II",
			FeatureGroupType.SpecialFeature), Matrixverständnis("Matrixverständnis", FeatureGroupType.SpecialFeature), Meereskundig(
			"Meereskundig", FeatureGroupType.SpecialFeature), MeisterDerImprovisation("Meister der Improvisation",
			FeatureGroupType.SpecialFeature), MeisterDerWünsche("Meister der Wünsche", FeatureGroupType.SpecialFeature), MeisterlicheRegeneration(
			"Meisterliche Regeneration", FeatureGroupType.SpecialFeature), MeisterlicheZauberkontrolle(
			"Meisterliche Zauberkontrolle", FeatureGroupType.SpecialFeature), MeisterlicheZauberkontrolleI(
			"Meisterliche Zauberkontrolle I", FeatureGroupType.SpecialFeature), MeisterlicheZauberkontrolleII(
			"Meisterliche Zauberkontrolle II", FeatureGroupType.SpecialFeature), MeisterlichesEntwaffnen(
			"Meisterliches Entwaffnen", FeatureGroupType.SpecialFeature), Meisterparade("Meisterparade",
			FeatureGroupType.SpecialFeature), Meisterschütze("Meisterschütze", FeatureGroupType.SpecialFeature), MerkmalskenntnisAntimagie(
			"Merkmalskenntnis: Antimagie", FeatureGroupType.SpecialFeature), MerkmalskenntnisBeschwörung(
			"Merkmalskenntnis: Beschwörung", FeatureGroupType.SpecialFeature), MerkmalskenntnisDämonisch(
			"Merkmalskenntnis: Dämonisch", FeatureGroupType.SpecialFeature), MerkmalskenntnisDämonischAgrimoth(
			"Merkmalskenntnis: Dämonisch (Agrimoth)", FeatureGroupType.SpecialFeature), MerkmalskenntnisDämonischAmazeroth(
			"Merkmalskenntnis: Dämonisch (Amazeroth)", FeatureGroupType.SpecialFeature), MerkmalskenntnisDämonischAsfaloth(
			"Merkmalskenntnis: Dämonisch (Asfaloth)", FeatureGroupType.SpecialFeature), MerkmalskenntnisDämonischBelhalhar(
			"Merkmalskenntnis: Dämonisch (Belhalhar)", FeatureGroupType.SpecialFeature), MerkmalskenntnisDämonischBelzhorash(
			"Merkmalskenntnis: Dämonisch (Belzhorash)", FeatureGroupType.SpecialFeature), MerkmalskenntnisDämonischBlakharaz(
			"Merkmalskenntnis: Dämonisch (Blakharaz)", FeatureGroupType.SpecialFeature), MerkmalskenntnisDämonischLolgramoth(
			"Merkmalskenntnis: Dämonisch (Lolgramoth)", FeatureGroupType.SpecialFeature), MerkmalskenntnisDämonischThargunitoth(
			"Merkmalskenntnis: Dämonisch (Thargunitoth)", FeatureGroupType.SpecialFeature), MerkmalskenntnisEigenschaften(
			"Merkmalskenntnis: Eigenschaften", FeatureGroupType.SpecialFeature), MerkmalskenntnisEinfluss(
			"Merkmalskenntnis: Einfluss", FeatureGroupType.SpecialFeature), MerkmalskenntnisElementar(
			"Merkmalskenntnis: Elementar", FeatureGroupType.SpecialFeature), MerkmalskenntnisElementarEis(
			"Merkmalskenntnis: Elementar (Eis)", FeatureGroupType.SpecialFeature), MerkmalskenntnisElementarErz(
			"Merkmalskenntnis: Elementar (Erz)", FeatureGroupType.SpecialFeature), MerkmalskenntnisElementarFeuer(
			"Merkmalskenntnis: Elementar (Feuer)", FeatureGroupType.SpecialFeature), MerkmalskenntnisElementarHumus(
			"Merkmalskenntnis: Elementar (Humus)", FeatureGroupType.SpecialFeature), MerkmalskenntnisElementarLuft(
			"Merkmalskenntnis: Elementar (Luft)", FeatureGroupType.SpecialFeature), MerkmalskenntnisElementarWasser(
			"Merkmalskenntnis: Elementar (Wasser)", FeatureGroupType.SpecialFeature), MerkmalskenntnisForm(
			"Merkmalskenntnis: Form", FeatureGroupType.SpecialFeature), MerkmalskenntnisGeisterwesen(
			"Merkmalskenntnis: Geisterwesen", FeatureGroupType.SpecialFeature), MerkmalskenntnisHeilung(
			"Merkmalskenntnis: Heilung", FeatureGroupType.SpecialFeature), MerkmalskenntnisHellsicht(
			"Merkmalskenntnis: Hellsicht", FeatureGroupType.SpecialFeature), MerkmalskenntnisHerbeirufung(
			"Merkmalskenntnis: Herbeirufung", FeatureGroupType.SpecialFeature), MerkmalskenntnisHerrschaft(
			"Merkmalskenntnis: Herrschaft", FeatureGroupType.SpecialFeature), MerkmalskenntnisIllusion(
			"Merkmalskenntnis: Illusion", FeatureGroupType.SpecialFeature), MerkmalskenntnisKraft(
			"Merkmalskenntnis: Kraft", FeatureGroupType.SpecialFeature), MerkmalskenntnisLimbus(
			"Merkmalskenntnis: Limbus", FeatureGroupType.SpecialFeature), MerkmalskenntnisMetamagie(
			"Merkmalskenntnis: Metamagie", FeatureGroupType.SpecialFeature), MerkmalskenntnisObjekt(
			"Merkmalskenntnis: Objekt", FeatureGroupType.SpecialFeature), MerkmalskenntnisSchaden(
			"Merkmalskenntnis: Schaden", FeatureGroupType.SpecialFeature), MerkmalskenntnisTelekinese(
			"Merkmalskenntnis: Telekinese", FeatureGroupType.SpecialFeature), MerkmalskenntnisTemporal(
			"Merkmalskenntnis: Temporal", FeatureGroupType.SpecialFeature), MerkmalskenntnisUmwelt(
			"Merkmalskenntnis: Umwelt", FeatureGroupType.SpecialFeature), MerkmalskenntnisVerständigung(
			"Merkmalskenntnis: Verständigung", FeatureGroupType.SpecialFeature), NandusgefälligesWissen(
			"Nandusgefälliges Wissen", FeatureGroupType.SpecialFeature), Nekromant("Nekromant",
			FeatureGroupType.SpecialFeature), Niederringen("Niederringen", FeatureGroupType.SpecialFeature), Niederwerfen(
			"Niederwerfen", FeatureGroupType.SpecialFeature), GabeDesOdûnHauchDesOdûn("Gabe des Odûn: Hauch des Odûn",
			FeatureGroupType.SpecialFeature), GabeDesOdûnHautDesOdûn("Gabe des Odûn: Haut des Odûn",
			FeatureGroupType.SpecialFeature), GabeDesOdûnBlutDesOdûn("Gabe des Odûn: Blut des Odûn",
			FeatureGroupType.SpecialFeature), GabeDesOdûnRufDesOdûn("Gabe des Odûn: Ruf des Odûn",
			FeatureGroupType.SpecialFeature), GabeDesOdûnSeeleDesOdûn("Gabe des Odûn: Seele des Odûn",
			FeatureGroupType.SpecialFeature), Ortskenntnis("Ortskenntnis", FeatureGroupType.SpecialFeature), OrtskenntnisÖrtlichkeit1(
			"Ortskenntnis (Örtlichkeit 1)", FeatureGroupType.SpecialFeature), OrtskenntnisÖrtlichkeit2(
			"Ortskenntnis (Örtlichkeit 2)", FeatureGroupType.SpecialFeature), OrtskenntnisStrecke1(
			"Ortskenntnis (Strecke 1)", FeatureGroupType.SpecialFeature), OrtskenntnisStrecke2(
			"Ortskenntnis (Strecke 2)", FeatureGroupType.SpecialFeature), Ottagaldr("Ottagaldr",
			FeatureGroupType.SpecialFeature), ParierwaffenI("Parierwaffen I", FeatureGroupType.SpecialFeature), ParierwaffenII(
			"Parierwaffen II", FeatureGroupType.SpecialFeature), RegenerationI("Regeneration I",
			FeatureGroupType.SpecialFeature), RegenerationII("Regeneration II", FeatureGroupType.SpecialFeature), Reiterkampf(
			"Reiterkampf", FeatureGroupType.SpecialFeature), ReiterkampfStreitwagen("Reiterkampf (Streitwagen)",
			FeatureGroupType.SpecialFeature), RepräsentationAchaz("Repräsentation: Achaz",
			FeatureGroupType.SpecialFeature), RepräsentationBorbaradianer("Repräsentation: Borbaradianer",
			FeatureGroupType.SpecialFeature), RepräsentationDruide("Repräsentation: Druide",
			FeatureGroupType.SpecialFeature), RepräsentationElf("Repräsentation: Elf", FeatureGroupType.SpecialFeature), RepräsentationGeode(
			"Repräsentation: Geode", FeatureGroupType.SpecialFeature), RepräsentationHexe("Repräsentation: Hexe",
			FeatureGroupType.SpecialFeature), RepräsentationMagier("Repräsentation: Magier",
			FeatureGroupType.SpecialFeature), RepräsentationScharlatan("Repräsentation: Scharlatan",
			FeatureGroupType.SpecialFeature), RepräsentationSchelm("Repräsentation: Schelm",
			FeatureGroupType.SpecialFeature), RitualArngrimsHöhle("Ritual: Arngrims Höhle",
			FeatureGroupType.SpecialFeature), RitualAufmerksamerWächter("Ritual: Aufmerksamer Wächter",
			FeatureGroupType.SpecialFeature), RitualBlickInLiskasAuge("Ritual: Blick in Liskas Auge",
			FeatureGroupType.SpecialFeature), RitualBlickInsGeisterreich("Ritual: Blick ins Geisterreich",
			FeatureGroupType.SpecialFeature), RitualBlickInsGeisterreichII("Ritual: Blick ins Geisterreich (II)",
			FeatureGroupType.SpecialFeature), RitualBlickInsGeisterreichIII("Ritual: Blick ins Geisterreich (III)",
			FeatureGroupType.SpecialFeature), RitualBlutsbund("Ritual: Blutsbund", FeatureGroupType.SpecialFeature), RitualBrazoraghGhorkai(
			"Ritual: Brazoragh Ghorkai", FeatureGroupType.SpecialFeature), RitualErgochaiTairachi(
			"Ritual: Ergochai Tairachi", FeatureGroupType.SpecialFeature), RitualExorzismus("Ritual: Exorzismus",
			FeatureGroupType.SpecialFeature), RitualExorzismusIII("Ritual: Exorzismus (III)",
			FeatureGroupType.SpecialFeature), RitualExorzismusIV("Ritual: Exorzismus (IV)",
			FeatureGroupType.SpecialFeature), RitualExorzismusV("Ritual: Exorzismus (V)",
			FeatureGroupType.SpecialFeature), RitualFarbenDesKrieges("Ritual: Farben des Krieges",
			FeatureGroupType.SpecialFeature), RitualFluchDerVerwirrung("Ritual: Fluch der Verwirrung",
			FeatureGroupType.SpecialFeature), RitualFreieSeelenfahrt("Ritual: Freie Seelenfahrt",
			FeatureGroupType.SpecialFeature), RitualGabenDerErde("Ritual: Gaben der Erde",
			FeatureGroupType.SpecialFeature), RitualGeisterbote("Ritual: Geisterbote", FeatureGroupType.SpecialFeature), RitualGeisterkerker(
			"Ritual: Geisterkerker", FeatureGroupType.SpecialFeature), RitualGeistheilung("Ritual: Geistheilung",
			FeatureGroupType.SpecialFeature), RitualGeleitDesNipakau("Ritual: Geleit des Nipakau",
			FeatureGroupType.SpecialFeature), RitualGesangDerWölfe("Ritual: Gesang der Wölfe",
			FeatureGroupType.SpecialFeature), RitualGharyakMaruki("Ritual: Gharyak Maruki",
			FeatureGroupType.SpecialFeature), RitualGroßerGeisterbann("Ritual: Großer Geisterbann",
			FeatureGroupType.SpecialFeature), RitualHairuf("Ritual: Hairuf", FeatureGroupType.SpecialFeature), RitualHauchDesElements(
			"Ritual: Hauch des Elements", FeatureGroupType.SpecialFeature), RitualHeimführungDerHerde(
			"Ritual: Heimführung der Herde", FeatureGroupType.SpecialFeature), RitualHerzDesTieres(
			"Ritual: Herz des Tieres", FeatureGroupType.SpecialFeature), RitualHilferuf("Ritual: Hilferuf",
			FeatureGroupType.SpecialFeature), RitualJagdfieber("Ritual: Jagdfieber", FeatureGroupType.SpecialFeature), RitualKhurkachaiTairachi(
			"Ritual: Khurkachai Tairachi", FeatureGroupType.SpecialFeature), RitualKraftDerTayas(
			"Ritual: Kraft der Tayas", FeatureGroupType.SpecialFeature), RitualKraftDesTieres(
			"Ritual: Kraft des Tieres", FeatureGroupType.SpecialFeature), RitualMcharUtrakRikaii(
			"Ritual: M´char Utrak Rikaii", FeatureGroupType.SpecialFeature), RitualMachtDerElemente(
			"Ritual: Macht der Elemente", FeatureGroupType.SpecialFeature), RitualMachtDesBlutes(
			"Ritual: Macht des Blutes", FeatureGroupType.SpecialFeature), RitualMailamRekdaisSegen(
			"Ritual: Mailam Rekdais Segen", FeatureGroupType.SpecialFeature), RitualMammutruf("Ritual: Mammutruf",
			FeatureGroupType.SpecialFeature), RitualOgerruf("Ritual: Ogerruf", FeatureGroupType.SpecialFeature), RitualPfadDerBlutrache(
			"Ritual: Pfad der Blutrache", FeatureGroupType.SpecialFeature), RitualRangildUndRissasHochzeit(
			"Ritual: Rangild und Rissas Hochzeit", FeatureGroupType.SpecialFeature), RitualRatDerAhnen(
			"Ritual: Rat der Ahnen", FeatureGroupType.SpecialFeature), RitualRegentanz("Ritual: Regentanz",
			FeatureGroupType.SpecialFeature), RitualReinigenDesWassers("Ritual: Reinigen des Wassers",
			FeatureGroupType.SpecialFeature), RitualReißgramsFährte("Ritual: Reißgrams Fährte",
			FeatureGroupType.SpecialFeature), RitualReitenderGeist("Ritual: Reitender Geist",
			FeatureGroupType.SpecialFeature), RitualRikaisVerderben("Ritual: Rikais Verderben",
			FeatureGroupType.SpecialFeature), RitualRinderruf("Ritual: Rinderruf", FeatureGroupType.SpecialFeature), RitualRufDesSchamanen(
			"Ritual: Ruf des Schamanen", FeatureGroupType.SpecialFeature), RitualSchlangenfluch(
			"Ritual: Schlangenfluch", FeatureGroupType.SpecialFeature), RitualSchlangenfluchV(
			"Ritual: Schlangenfluch (V)", FeatureGroupType.SpecialFeature), RitualSchlangengeist(
			"Ritual: Schlangengeist", FeatureGroupType.SpecialFeature), RitualSchlingerruf("Ritual: Schlingerruf",
			FeatureGroupType.SpecialFeature), RitualSchomasKraft("Ritual: Schomas Kraft",
			FeatureGroupType.SpecialFeature), RitualSchutzDerJurte("Ritual: Schutz der Jurte",
			FeatureGroupType.SpecialFeature), RitualSchützendeRotte("Ritual: Schützende Rotte",
			FeatureGroupType.SpecialFeature), RitualSeeschlangenruf("Ritual: Seeschlangenruf",
			FeatureGroupType.SpecialFeature), RitualStimmeDesNipakau("Ritual: Stimme des Nipakau",
			FeatureGroupType.SpecialFeature), RitualTabuzone("Ritual: Tabuzone", FeatureGroupType.SpecialFeature), RitualTauschplatz(
			"Ritual: Tauschplatz", FeatureGroupType.SpecialFeature), RitualTiereAusFarben("Ritual: Tiere aus Farben",
			FeatureGroupType.SpecialFeature), RitualTränkeMeineHerde("Ritual: Tränke meine Herde",
			FeatureGroupType.SpecialFeature), RitualWeckruf("Ritual: Weckruf", FeatureGroupType.SpecialFeature), RitualWegDesWindes(
			"Ritual: Weg des Windes", FeatureGroupType.SpecialFeature), RitualWegzeichen("Ritual: Wegzeichen",
			FeatureGroupType.SpecialFeature), RitualWeidegründeFinden("Ritual: Weidegründe finden",
			FeatureGroupType.SpecialFeature), RitualWildFinden("Ritual: Wild finden", FeatureGroupType.SpecialFeature), RitualWildschweinruf(
			"Ritual: Wildschweinruf", FeatureGroupType.SpecialFeature), RitualWolfsfluch("Ritual: Wolfsfluch",
			FeatureGroupType.SpecialFeature), RitualWolfsfluchV("Ritual: Wolfsfluch (V)",
			FeatureGroupType.SpecialFeature), RitualWolfsruf("Ritual: Wolfsruf", FeatureGroupType.SpecialFeature), RitualZeichenSetzen(
			"Ritual: Zeichen setzen", FeatureGroupType.SpecialFeature), RitualZornDesBerglöwen(
			"Ritual: Zorn des Berglöwen", FeatureGroupType.SpecialFeature), RitualZornDesSchneelaurers(
			"Ritual: Zorn des Schneelaurers", FeatureGroupType.SpecialFeature), RitualZornDerKhoramsbestie(
			"Ritual: Zorn der Khoramsbestie", FeatureGroupType.SpecialFeature), RitualZornDesFirunsbären(
			"Ritual: Zorn des Firunsbären", FeatureGroupType.SpecialFeature), RitualkenntnisAchazSchamane(
			"Ritualkenntnis: Achaz-Schamane", FeatureGroupType.SpecialFeature), RitualkenntnisAlchimist(
			"Ritualkenntnis: Alchimist", FeatureGroupType.SpecialFeature), RitualkenntnisDerwisch(
			"Ritualkenntnis: Derwisch", FeatureGroupType.SpecialFeature), RitualkenntnisDruide(
			"Ritualkenntnis: Druide", FeatureGroupType.SpecialFeature), RitualkenntnisDurroDûn(
			"Ritualkenntnis: Durro-Dûn", FeatureGroupType.SpecialFeature), RitualkenntnisFerkinaSchamane(
			"Ritualkenntnis: Ferkina-Schamane", FeatureGroupType.SpecialFeature), RitualkenntnisGeode(
			"Ritualkenntnis: Geode", FeatureGroupType.SpecialFeature), RitualkenntnisGjalskerSchamane(
			"Ritualkenntnis: Gjalsker-Schamane", FeatureGroupType.SpecialFeature), RitualkenntnisGoblinSchamanin(
			"Ritualkenntnis: Goblin-Schamanin", FeatureGroupType.SpecialFeature), RitualkenntnisHexe(
			"Ritualkenntnis: Hexe", FeatureGroupType.SpecialFeature), RitualkenntnisKristallomantie(
			"Ritualkenntnis: Kristallomantie", FeatureGroupType.SpecialFeature), RitualkenntnisGildenmagie(
			"Ritualkenntnis: Gildenmagie", FeatureGroupType.SpecialFeature), RitualkenntnisNivesenSchamane(
			"Ritualkenntnis: Nivesen-Schamane", FeatureGroupType.SpecialFeature), RitualkenntnisOrkSchamane(
			"Ritualkenntnis: Ork-Schamane", FeatureGroupType.SpecialFeature), RitualkenntnisScharlatan(
			"Ritualkenntnis: Scharlatan", FeatureGroupType.SpecialFeature), RitualkenntnisRunenzauberei(
			"Ritualkenntnis: Runenzauberei", FeatureGroupType.SpecialFeature), RitualkenntnisTrollzackerSchamane(
			"Ritualkenntnis: Trollzacker-Schamane", FeatureGroupType.SpecialFeature), RitualkenntnisWaldmenschenSchamane(
			"Ritualkenntnis: Waldmenschen-Schamane", FeatureGroupType.SpecialFeature), RitualkenntnisWaldmenschenSchamaneUtulus(
			"Ritualkenntnis: Waldmenschen-Schamane (Utulus)", FeatureGroupType.SpecialFeature), RitualkenntnisWaldmenschenSchamaneTocamuyac(
			"Ritualkenntnis: Waldmenschen-Schamane (Tocamuyac)", FeatureGroupType.SpecialFeature), RitualkenntnisZaubertänzer(
			"Ritualkenntnis: Zaubertänzer", FeatureGroupType.SpecialFeature), RitualkenntnisZaubertänzertulamidischeSharisad(
			"Ritualkenntnis: Zaubertänzer (tulamidische Sharisad)", FeatureGroupType.SpecialFeature), RitualkenntnisZaubertänzernovadischeSharisad(
			"Ritualkenntnis: Zaubertänzer (novadische Sharisad)", FeatureGroupType.SpecialFeature), RitualkenntnisZaubertänzerMajuna(
			"Ritualkenntnis: Zaubertänzer (Majuna)", FeatureGroupType.SpecialFeature), RitualkenntnisZaubertänzerHazaqi(
			"Ritualkenntnis: Zaubertänzer (Hazaqi)", FeatureGroupType.SpecialFeature), RitualkenntnisZibilja(
			"Ritualkenntnis: Zibilja", FeatureGroupType.SpecialFeature), RitualkenntnisAlhanisch(
			"Ritualkenntnis: Alhanisch", FeatureGroupType.SpecialFeature), RitualkenntnisDruidischGeodisch(
			"Ritualkenntnis: Druidisch-Geodisch", FeatureGroupType.SpecialFeature), RitualkenntnisGüldenländisch(
			"Ritualkenntnis: Güldenländisch", FeatureGroupType.SpecialFeature), RitualkenntnisGrolmisch(
			"Ritualkenntnis: Grolmisch", FeatureGroupType.SpecialFeature), RitualkenntnisKophtanisch(
			"Ritualkenntnis: Kophtanisch", FeatureGroupType.SpecialFeature), RitualkenntnisMudramulisch(
			"Ritualkenntnis: Mudramulisch", FeatureGroupType.SpecialFeature), RitualkenntnisSatuarisch(
			"Ritualkenntnis: Satuarisch", FeatureGroupType.SpecialFeature), RitualkenntnisTapasuul(
			"Ritualkenntnis: Tapasuul", FeatureGroupType.SpecialFeature), RüstungsgewöhnungI("Rüstungsgewöhnung I",
			FeatureGroupType.SpecialFeature), RüstungsgewöhnungII("Rüstungsgewöhnung II",
			FeatureGroupType.SpecialFeature), RüstungsgewöhnungIII("Rüstungsgewöhnung III",
			FeatureGroupType.SpecialFeature), Runenkunde("Runenkunde", FeatureGroupType.SpecialFeature), RunenRauschrune(
			"Runen: Rauschrune", FeatureGroupType.SpecialFeature), RunenFriedensrune("Runen: Friedensrune",
			FeatureGroupType.SpecialFeature), RunenOttarune("Runen: Ottarune", FeatureGroupType.SpecialFeature), RunenFinsterrune(
			"Runen: Finsterrune", FeatureGroupType.SpecialFeature), RunenWogensturmrune("Runen: Wogensturmrune",
			FeatureGroupType.SpecialFeature), RunenFelsenrune("Runen: Felsenrune", FeatureGroupType.SpecialFeature), RunenSchicksalsrune(
			"Runen: Schicksalsrune", FeatureGroupType.SpecialFeature), RunenPfeilrune("Runen: Pfeilrune",
			FeatureGroupType.SpecialFeature), RunenWaffenrune("Runen: Waffenrune", FeatureGroupType.SpecialFeature), RunenDrachenrune(
			"Runen: Drachenrune", FeatureGroupType.SpecialFeature), RunenFurchtrune("Runen: Furchtrune",
			FeatureGroupType.SpecialFeature), RunenBlutrune("Runen: Blutrune", FeatureGroupType.SpecialFeature), Salasandra(
			"Salasandra", FeatureGroupType.SpecialFeature), Schmetterschlag("Schmetterschlag",
			FeatureGroupType.SpecialFeature), SchmutzigeTricks("Schmutzige Tricks", FeatureGroupType.SpecialFeature), Schwanzschlag(
			"Schwanzschlag", FeatureGroupType.SpecialFeature), Schwanzfeger("Schwanzfeger",
			FeatureGroupType.SpecialFeature), Schwinger("Schwinger", FeatureGroupType.SpecialFeature), Schwitzkasten(
			"Schwitzkasten", FeatureGroupType.SpecialFeature), SchalenzauberWeiheDerSchale(
			"Schalenzauber: Weihe der Schale", FeatureGroupType.SpecialFeature), SchalenzauberAllegorischeAnalyse(
			"Schalenzauber: Allegorische Analyse", FeatureGroupType.SpecialFeature), SchalenzauberChymischeHochzeit(
			"Schalenzauber: Chymische Hochzeit", FeatureGroupType.SpecialFeature), SchalenzauberMandriconsBindung(
			"Schalenzauber: Mandricons Bindung", FeatureGroupType.SpecialFeature), SchalenzauberFeuerUndEis(
			"Schalenzauber: Feuer und Eis", FeatureGroupType.SpecialFeature), SchalenzauberTransmutationDerElemente(
			"Schalenzauber: Transmutation der Elemente", FeatureGroupType.SpecialFeature), Scharfschütze(
			"Scharfschütze", FeatureGroupType.SpecialFeature), Sprung("Sprung", FeatureGroupType.SpecialFeature), Sprungtritt(
			"Sprungtritt", FeatureGroupType.SpecialFeature), SchlangenringZauberWeiheDerSchlange(
			"Schlangenring-Zauber: Weihe der Schlange", FeatureGroupType.SpecialFeature), SchlangenringZauberMagnetismus(
			"Schlangenring-Zauber: Magnetismus", FeatureGroupType.SpecialFeature), SchlangenringZauberWasserbann(
			"Schlangenring-Zauber: Wasserbann", FeatureGroupType.SpecialFeature), SchlangenringZauberMachtÜberDenRegen(
			"Schlangenring-Zauber: Macht über den Regen", FeatureGroupType.SpecialFeature), SchlangenringZauberSeelenfeuer(
			"Schlangenring-Zauber: Seelenfeuer", FeatureGroupType.SpecialFeature), SchlangenringZauberHerrDerFlammen(
			"Schlangenring-Zauber: Herr der Flammen", FeatureGroupType.SpecialFeature), SchlangenringZauberMachtDesLebens(
			"Schlangenring-Zauber: Macht des Lebens", FeatureGroupType.SpecialFeature), SchlangenringZauberKräfteDerNatur(
			"Schlangenring-Zauber: Kräfte der Natur", FeatureGroupType.SpecialFeature), SchlangenringZauberWirbelnderLuftschild(
			"Schlangenring-Zauber: Wirbelnder Luftschild", FeatureGroupType.SpecialFeature), SchlangenringZauberLaunenDesWindes(
			"Schlangenring-Zauber: Launen des Windes", FeatureGroupType.SpecialFeature), SchlangenringZauberWegDurchSumusLeib(
			"Schlangenring-Zauber: Weg durch Sumus Leib", FeatureGroupType.SpecialFeature), DieGestaltAusRauch(
			"Die Gestalt aus Rauch", FeatureGroupType.SpecialFeature), SchuppenbeutelBindungDesSchuppenbeutels(
			"Schuppenbeutel: Bindung des Schuppenbeutels", FeatureGroupType.SpecialFeature), SchuppenbeutelSuchendeFinger(
			"Schuppenbeutel: Suchende Finger", FeatureGroupType.SpecialFeature), SchuppenbeutelEwigeWegzehrung(
			"Schuppenbeutel: Ewige Wegzehrung", FeatureGroupType.SpecialFeature), SchildkampfI("Schildkampf I",
			FeatureGroupType.SpecialFeature), SchildkampfII("Schildkampf II", FeatureGroupType.SpecialFeature), Schildspalter(
			"Schildspalter", FeatureGroupType.SpecialFeature), Signaturkenntnis("Signaturkenntnis",
			FeatureGroupType.SpecialFeature), Schnellladen("Schnellladen", FeatureGroupType.SpecialFeature), Schnellziehen(
			"Schnellziehen", FeatureGroupType.SpecialFeature), SemipermanenzI("Semipermanenz I",
			FeatureGroupType.SpecialFeature), SemipermanenzII("Semipermanenz II", FeatureGroupType.SpecialFeature), Simultanzaubern(
			"Simultanzaubern", FeatureGroupType.SpecialFeature), SpätweiheAlveranischeGottheit(
			"Spätweihe Alveranische Gottheit", FeatureGroupType.SpecialFeature), SpätweiheNamenloser(
			"Spätweihe Namenloser", FeatureGroupType.SpecialFeature), SpätweiheNichtalveranischeGottheit(
			"Spätweihe Nichtalveranische Gottheit", FeatureGroupType.SpecialFeature), Spießgespann("Spießgespann",
			FeatureGroupType.SpecialFeature), StabzauberBindung("Stabzauber: Bindung", FeatureGroupType.SpecialFeature), StabzauberFackel(
			"Stabzauber: Fackel", FeatureGroupType.SpecialFeature), StabzauberSeil("Stabzauber: Seil",
			FeatureGroupType.SpecialFeature), StabzauberStabverlängerung("Stabzauber: Stabverlängerung",
			FeatureGroupType.SpecialFeature), StabzauberHammerDesMagus("Stabzauber: Hammer des Magus",
			FeatureGroupType.SpecialFeature), StabzauberKraftfokus("Stabzauber: Kraftfokus",
			FeatureGroupType.SpecialFeature), StabzauberModifikationsfokus("Stabzauber: Modifikationsfokus",
			FeatureGroupType.SpecialFeature), StabzauberZauberspeicher("Stabzauber: Zauberspeicher",
			FeatureGroupType.SpecialFeature), StabzauberMerkmalsfokus("Stabzauber: Merkmalsfokus",
			FeatureGroupType.SpecialFeature), StabzauberFlammenschwert("Stabzauber: Flammenschwert",
			FeatureGroupType.SpecialFeature), StabzauberSchuppenhaut("Stabzauber: Schuppenhaut",
			FeatureGroupType.SpecialFeature), StabzauberAstralschild("Stabzauber: Astralschild",
			FeatureGroupType.SpecialFeature), StabzauberLangerArm("Stabzauber: Langer Arm",
			FeatureGroupType.SpecialFeature), StabzauberSchutzGegenUntote("Stabzauber: Schutz gegen Untote",
			FeatureGroupType.SpecialFeature), Stapeleffekt("Stapeleffekt", FeatureGroupType.SpecialFeature), Steppenkundig(
			"Steppenkundig", FeatureGroupType.SpecialFeature), Sturmangriff("Sturmangriff",
			FeatureGroupType.SpecialFeature), Sumpfkundig("Sumpfkundig", FeatureGroupType.SpecialFeature), TrankDesUngehindertenWeges(
			"Trank des ungehinderten Weges", FeatureGroupType.SpecialFeature), TanzDerMada("Tanz der Mada",
			FeatureGroupType.SpecialFeature), TierischerBegleiter("Tierischer Begleiter",
			FeatureGroupType.SpecialFeature), TodVonLinks("Tod von links", FeatureGroupType.SpecialFeature), Todesstoß(
			"Todesstoß", FeatureGroupType.SpecialFeature), Traumgänger("Traumgänger", FeatureGroupType.SpecialFeature), Tritt(
			"Tritt", FeatureGroupType.SpecialFeature), TrommelzauberRufDesKrieges("Trommelzauber: Ruf des Krieges",
			FeatureGroupType.SpecialFeature), TrommelzauberSturmDerWüste("Trommelzauber: Sturm der Wüste",
			FeatureGroupType.SpecialFeature), TrommelzauberSchutzRastullahs("Trommelzauber: Schutz Rastullahs",
			FeatureGroupType.SpecialFeature), TrommelzauberRastullahsGüte("Trommelzauber: Rastullahs Güte",
			FeatureGroupType.SpecialFeature), TrommelzauberZornDesGottgefälligen(
			"Trommelzauber: Zorn des Gottgefälligen", FeatureGroupType.SpecialFeature), Turnierreiterei(
			"Turnierreiterei", FeatureGroupType.SpecialFeature), Umreißen("Umreißen", FeatureGroupType.SpecialFeature), Unterwasserkampf(
			"Unterwasserkampf", FeatureGroupType.SpecialFeature), VerbotenePforten("Verbotene Pforten",
			FeatureGroupType.SpecialFeature), VersteckteKlinge("Versteckte Klinge", FeatureGroupType.SpecialFeature), Vertrautenbindung(
			"Vertrautenbindung", FeatureGroupType.SpecialFeature), WaffeZerbrechen("Waffe zerbrechen",
			FeatureGroupType.SpecialFeature), Waffenmeister("Waffenmeister", FeatureGroupType.SpecialFeature), WaffenmeisterSchild(
			"Waffenmeister (Schild)", FeatureGroupType.SpecialFeature), Würgegriff("Würgegriff",
			FeatureGroupType.SpecialFeature), Wurf("Wurf", FeatureGroupType.SpecialFeature), WaffenloserKampfstilBornländisch(
			"Waffenloser Kampfstil: Bornländisch", FeatureGroupType.SpecialFeature), WaffenloserKampfstilGladiatorenstil(
			"Waffenloser Kampfstil: Gladiatorenstil", FeatureGroupType.SpecialFeature), WaffenloserKampfstilHammerfaust(
			"Waffenloser Kampfstil: Hammerfaust", FeatureGroupType.SpecialFeature), WaffenloserKampfstilHruruzat(
			"Waffenloser Kampfstil: Hruruzat", FeatureGroupType.SpecialFeature), WaffenloserKampfstilMercenario(
			"Waffenloser Kampfstil: Mercenario", FeatureGroupType.SpecialFeature), WaffenloserKampfstilUnauerSchule(
			"Waffenloser Kampfstil: Unauer Schule", FeatureGroupType.SpecialFeature), WaffenloserKampfstilGladiatorenstilDDZ(
			"Waffenloser Kampfstil: Gladiatorenstil (DDZ)", FeatureGroupType.SpecialFeature), VielfacheLadungen(
			"Vielfache Ladungen", FeatureGroupType.SpecialFeature), Waldkundig("Waldkundig",
			FeatureGroupType.SpecialFeature), Windmühle("Windmühle", FeatureGroupType.SpecialFeature), Wuchtschlag(
			"Wuchtschlag", FeatureGroupType.SpecialFeature), Wüstenkundig("Wüstenkundig",
			FeatureGroupType.SpecialFeature), ZauberBereithalten("Zauber bereithalten", FeatureGroupType.SpecialFeature), Zauberkontrolle(
			"Zauberkontrolle", FeatureGroupType.SpecialFeature), Zauberroutine("Zauberroutine",
			FeatureGroupType.SpecialFeature), ZauberUnterbrechen("Zauber unterbrechen", FeatureGroupType.SpecialFeature), ZauberVereinigen(
			"Zauber vereinigen", FeatureGroupType.SpecialFeature), ZauberzeichenLeuchtendesZeichen(
			"Zauberzeichen: Leuchtendes Zeichen", FeatureGroupType.SpecialFeature), ZauberzeichenSingendesZeichen(
			"Zauberzeichen: Singendes Zeichen", FeatureGroupType.SpecialFeature), ZauberzeichenSiegelDerSeelenruhe(
			"Zauberzeichen: Siegel der Seelenruhe", FeatureGroupType.SpecialFeature), ZauberzeichenHermetischesSiegel(
			"Zauberzeichen: Hermetisches Siegel", FeatureGroupType.SpecialFeature), ZauberzeichenGlypheDerElementarenAttraktion(
			"Zauberzeichen: Glyphe der Elementaren Attraktion", FeatureGroupType.SpecialFeature), ZauberzeichenUngesehenesZeichen(
			"Zauberzeichen: Ungesehenes Zeichen", FeatureGroupType.SpecialFeature), ZauberzeichenGlypheDerElementarenBannung(
			"Zauberzeichen: Glyphe der Elementaren Bannung", FeatureGroupType.SpecialFeature), ZauberzeichenZähneDesFeuers(
			"Zauberzeichen: Zähne des Feuers", FeatureGroupType.SpecialFeature), ZauberzeichenZeichenDerZauberschmiede(
			"Zauberzeichen: Zeichen der Zauberschmiede", FeatureGroupType.SpecialFeature), ZauberzeichenMarkierungDesTodes(
			"Zauberzeichen: Markierung des Todes", FeatureGroupType.SpecialFeature), ZauberzeichenFanalDerHerrschaft(
			"Zauberzeichen: Fanal der Herrschaft", FeatureGroupType.SpecialFeature), ZauberzeichenGlypheDesVerfluchtenGoldes(
			"Zauberzeichen: Glyphe des verfluchten Goldes", FeatureGroupType.SpecialFeature), ZauberzeichenAugeDerEwigenWacht(
			"Zauberzeichen: Auge der Ewigen Wacht", FeatureGroupType.SpecialFeature), ZauberzeichenAugeDesBasilisken(
			"Zauberzeichen: Auge des Basilisken", FeatureGroupType.SpecialFeature), ZauberzeichenSatinavsSiegel(
			"Zauberzeichen: Satinavs Siegel", FeatureGroupType.SpecialFeature), ZauberzeichenSchutzsiegel(
			"Zauberzeichen: Schutzsiegel", FeatureGroupType.SpecialFeature), ZauberzeichenSchutzkreisGegenTraumgänger(
			"Zauberzeichen: Schutzkreis gegen Traumgänger", FeatureGroupType.SpecialFeature), ZaubertanzTanzDerLiebe(
			"Zaubertanz: Tanz der Liebe", FeatureGroupType.SpecialFeature), ZaubertanzKhablasVerlockungTanzDerLiebe(
			"Zaubertanz: Khablas Verlockung (Tanz der Liebe)", FeatureGroupType.SpecialFeature), ZaubertanzRahjarraTanzDerLiebe(
			"Zaubertanz: Rahjarra (Tanz der Liebe)", FeatureGroupType.SpecialFeature), ZaubertanzTanzDerFreude(
			"Zaubertanz: Tanz der Freude", FeatureGroupType.SpecialFeature), ZaubertanzPerhinasSegenTanzDerFreude(
			"Zaubertanz: Perhinas Segen (Tanz der Freude)", FeatureGroupType.SpecialFeature), ZaubertanzPerainesLiebeTanzDerFreude(
			"Zaubertanz: Peraines Liebe (Tanz der Freude)", FeatureGroupType.SpecialFeature), ZaubertanzTanzDerErmutigung(
			"Zaubertanz: Tanz der Ermutigung", FeatureGroupType.SpecialFeature), ZaubertanzRhondarasForderungTanzDerErmutigung(
			"Zaubertanz: Rhondaras Forderung (Tanz der Ermutigung)", FeatureGroupType.SpecialFeature), ZaubertanzRondrasMutTanzDerErmutigung(
			"Zaubertanz: Rondras Mut (Tanz der Ermutigung)", FeatureGroupType.SpecialFeature), ZaubertanzPavonearseTanzDerErmutigung(
			"Zaubertanz: Pavonearse (Tanz der Ermutigung)", FeatureGroupType.SpecialFeature), ZaubertanzTanzDerWahrheit(
			"Zaubertanz: Tanz der Wahrheit", FeatureGroupType.SpecialFeature), ZaubertanzHeschinjasBlickTanzDerWahrheit(
			"Zaubertanz: Heschinjas Blick (Tanz der Wahrheit)", FeatureGroupType.SpecialFeature), ZaubertanzTanzDerErlösung(
			"Zaubertanz: Tanz der Erlösung", FeatureGroupType.SpecialFeature), ZaubertanzMarhibosHandTanzDerErlösung(
			"Zaubertanz: Marhibos Hand (Tanz der Erlösung)", FeatureGroupType.SpecialFeature), ZaubertanzHesindesMachtTanzDerErlösung(
			"Zaubertanz: Hesindes Macht (Tanz der Erlösung)", FeatureGroupType.SpecialFeature), ZaubertanzZarpadaTanzDerErlösung(
			"Zaubertanz: Zarpada (Tanz der Erlösung)", FeatureGroupType.SpecialFeature), ZaubertanzTanzDerBilder(
			"Zaubertanz: Tanz der Bilder", FeatureGroupType.SpecialFeature), ZaubertanzShimijasRauschTanzDerBilder(
			"Zaubertanz: Shimijas Rausch (Tanz der Bilder)", FeatureGroupType.SpecialFeature), ZaubertanzPhexensGeschmeideTanzDerBilder(
			"Zaubertanz: Phexens Geschmeide (Tanz der Bilder)", FeatureGroupType.SpecialFeature), ZaubertanzElVanidadTanzDerBilder(
			"Zaubertanz: El Vanidad (Tanz der Bilder)", FeatureGroupType.SpecialFeature), ZaubertanzTanzOhneEnde(
			"Zaubertanz: Tanz ohne Ende", FeatureGroupType.SpecialFeature), ZaubertanzNahemasTraumTanzOhneEnde(
			"Zaubertanz: Nahemas Traum (Tanz ohne Ende)", FeatureGroupType.SpecialFeature), ZaubertanzSatinavsGabeTanzOhneEnde(
			"Zaubertanz: Satinavs Gabe (Tanz ohne Ende)", FeatureGroupType.SpecialFeature), ZaubertanzSuenyoTanzOhneEnde(
			"Zaubertanz: Suenyo (Tanz ohne Ende)", FeatureGroupType.SpecialFeature), ZaubertanzTanzDesUngehorsams(
			"Zaubertanz: Tanz des Ungehorsams", FeatureGroupType.SpecialFeature), ZaubertanzTanzDerGemeinschaft(
			"Zaubertanz: Tanz der Gemeinschaft", FeatureGroupType.SpecialFeature), ZaubertanzOrhimasTanzTanzDerWeisheit(
			"Zaubertanz: Orhimas Tanz (Tanz der Weisheit)", FeatureGroupType.SpecialFeature), ZaubertanzTanzFürRastullahTanzDerUnantastbarkeit(
			"Zaubertanz: Tanz für Rastullah (Tanz der Unantastbarkeit)", FeatureGroupType.SpecialFeature), ZaubertanzSelinata(
			"Zaubertanz: Selinata", FeatureGroupType.SpecialFeature), ZaubertanzMadayana("Zaubertanz: Madayana",
			FeatureGroupType.SpecialFeature), ZaubertanzFirunsJagd("Zaubertanz: Firuns Jagd",
			FeatureGroupType.SpecialFeature), ZaubertanzRahjasBegehren("Zaubertanz: Rahjas Begehren",
			FeatureGroupType.SpecialFeature), Zauberzeichen("Zauberzeichen", FeatureGroupType.SpecialFeature), ZibiljaRitualSchwarmseele(
			"Zibilja-Ritual: Schwarmseele", FeatureGroupType.SpecialFeature), ZibiljaRitualMackestopp(
			"Zibilja-Ritual: Mackestopp", FeatureGroupType.SpecialFeature), ZibiljaRitualTraumseherin(
			"Zibilja-Ritual: Traumseherin", FeatureGroupType.SpecialFeature), ZibiljaRitualWeisheitDerSchrift(
			"Zibilja-Ritual: Weisheit der Schrift", FeatureGroupType.SpecialFeature), ZibiljaRitualSiegelDerEwigenRuhe(
			"Zibilja-Ritual: Siegel der Ewigen Ruhe", FeatureGroupType.SpecialFeature), ZibiljaRitualUnsichtbareChronik(
			"Zibilja-Ritual: Unsichtbare Chronik", FeatureGroupType.SpecialFeature), ZibiljaRitualRufDesBienenstocks(
			"Zibilja-Ritual: Ruf des Bienenstocks", FeatureGroupType.SpecialFeature), ZibiljaRitualWinterlager(
			"Zibilja-Ritual: Winterlager", FeatureGroupType.SpecialFeature), ZibiljaRitualBienenschwarm(
			"Zibilja-Ritual: Bienenschwarm", FeatureGroupType.SpecialFeature), ZibiljaRitualWachshaut(
			"Zibilja-Ritual: Wachshaut", FeatureGroupType.SpecialFeature), ZibiljaRitualBienenkönigin(
			"Zibilja-Ritual: Bienenkönigin", FeatureGroupType.SpecialFeature), ZibiljaRitualBienenfleiß(
			"Zibilja-Ritual: Bienenfleiß", FeatureGroupType.SpecialFeature), ZibiljaRitualBienentanz(
			"Zibilja-Ritual: Bienentanz", FeatureGroupType.SpecialFeature), ZibiljaRitualTraumwissen(
			"Zibilja-Ritual: Traumwissen", FeatureGroupType.SpecialFeature), AkklimatisierungHitze(
			"Akklimatisierung: Hitze", FeatureGroupType.SpecialFeature), AkklimatisierungKälte(
			"Akklimatisierung: Kälte", FeatureGroupType.SpecialFeature), Fälscher("Fälscher",
			FeatureGroupType.SpecialFeature), Rosstäuscher("Rosstäuscher", FeatureGroupType.SpecialFeature), Standfest(
			"Standfest", FeatureGroupType.SpecialFeature), Geschützmeister("Geschützmeister",
			FeatureGroupType.SpecialFeature), Halbschwert("Halbschwert", FeatureGroupType.SpecialFeature), Klingentänzer(
			"Klingentänzer", FeatureGroupType.SpecialFeature), Chimärenmeister("Chimärenmeister",
			FeatureGroupType.SpecialFeature), RitualkenntnisSeher("Ritualkenntnis Seher",
			FeatureGroupType.SpecialFeature), SeherRunenbindung("Seher: Runenbindung", FeatureGroupType.SpecialFeature), SeherTraumseher(
			"Seher: Traumseher", FeatureGroupType.SpecialFeature), SeherRunenDerWeissagung(
			"Seher: Runen der Weissagung", FeatureGroupType.SpecialFeature), SeherRatDerAhnen("Seher: Rat der Ahnen",
			FeatureGroupType.SpecialFeature), SeherRufDerRunjas("Seher: Ruf der Runjas",
			FeatureGroupType.SpecialFeature), Talentspezialisierung("Talentspezialisierung",
			FeatureGroupType.SpecialFeature), Zauberspezialisierung("Zauberspezialisierung",
			FeatureGroupType.SpecialFeature), RepräsentationAlhanisch("Repräsentation: Alhanisch",
			FeatureGroupType.SpecialFeature), RepräsentationDruidischGeodisch("Repräsentation: Druidisch-Geodisch",
			FeatureGroupType.SpecialFeature), RepräsentationGüldenländisch("Repräsentation: Güldenländisch",
			FeatureGroupType.SpecialFeature), RepräsentationGrolmisch("Repräsentation: Grolmisch",
			FeatureGroupType.SpecialFeature), RepräsentationKophtanisch("Repräsentation: Kophtanisch",
			FeatureGroupType.SpecialFeature), RepräsentationMudramulisch("Repräsentation: Mudramulisch",
			FeatureGroupType.SpecialFeature), RepräsentationSatuarisch("Repräsentation: Satuarisch",
			FeatureGroupType.SpecialFeature), SchlangenszeptersBindung("Schlangenszepters: Bindung",
			FeatureGroupType.SpecialFeature), SchlangenszeptersRufDerFliegendenSchlange(
			"Schlangenszepters: Ruf der fliegenden Schlange", FeatureGroupType.SpecialFeature), SzepterBindung(
			"Szepter: Bindung", FeatureGroupType.SpecialFeature), SzepterFliegenleib("Szepter: Fliegenleib",
			FeatureGroupType.SpecialFeature), SzepterGolemdiener("Szepter: Golemdiener",
			FeatureGroupType.SpecialFeature), SzepterHerrscherDerDjinnim("Szepter: Herrscher der Djinnim",
			FeatureGroupType.SpecialFeature), SzepterHerrscherDerIfriitim("Szepter: Herrscher der Ifriitim",
			FeatureGroupType.SpecialFeature), SzepterKraftDerKophtanim("Szepter: Kraft der Kophtanim",
			FeatureGroupType.SpecialFeature), SzepterSchutzDerAhnen("Szepter: Schutz der Ahnen",
			FeatureGroupType.SpecialFeature), SzepterStimmeDerMacht("Szepter: Stimme der Macht",
			FeatureGroupType.SpecialFeature), SzepterVermächtnisDerKophtanim("Szepter: Vermächtnis der Kophtanim",
			FeatureGroupType.SpecialFeature), SzepterWaffeDesGeistes("Szepter: Waffe des Geistes",
			FeatureGroupType.SpecialFeature), RituelleStrafeÄngsteMehren("Rituelle Strafe: Ängste mehren",
			FeatureGroupType.SpecialFeature), RituelleStrafeBeißAufGranit("Rituelle Strafe: Beiß auf Granit",
			FeatureGroupType.SpecialFeature), RituelleStrafeBeute("Rituelle Strafe: Beute",
			FeatureGroupType.SpecialFeature), RituelleStrafeHexenschuss("Rituelle Strafe: Hexenschuss",
			FeatureGroupType.SpecialFeature), RituelleStrafeKrötenkuss("Rituelle Strafe: Krötenkuss",
			FeatureGroupType.SpecialFeature), RituelleStrafeMitBlindheitSchlagen(
			"Rituelle Strafe: Mit Blindheit schlagen", FeatureGroupType.SpecialFeature), RituelleStrafePechAnDenHals(
			"Rituelle Strafe: Pech an den Hals", FeatureGroupType.SpecialFeature), RituelleStrafePestilenz(
			"Rituelle Strafe: Pestilenz", FeatureGroupType.SpecialFeature), RituelleStrafeSchlafRauben(
			"Rituelle Strafe: Schlaf rauben", FeatureGroupType.SpecialFeature), RituelleStrafeTodesfluch(
			"Rituelle Strafe: Todesfluch", FeatureGroupType.SpecialFeature), RituelleStrafeUnfruchtbarkeit(
			"Rituelle Strafe: Unfruchtbarkeit", FeatureGroupType.SpecialFeature), RituelleStrafeWarzenSprießen(
			"Rituelle Strafe: Warzen sprießen", FeatureGroupType.SpecialFeature), RituelleStrafeZungeLähmen(
			"Rituelle Strafe: Zunge lähmen", FeatureGroupType.SpecialFeature), KristallpendelAstralesZeichen(
			"Kristallpendel: Astrales Zeichen", FeatureGroupType.SpecialFeature), KristallpendelAuraDesFriedens(
			"Kristallpendel: Aura des Friedens", FeatureGroupType.SpecialFeature), KristallpendelBlutDerSippe(
			"Kristallpendel: Blut der Sippe", FeatureGroupType.SpecialFeature), KristallpendelGebetDerFürsorge(
			"Kristallpendel: Gebet der Fürsorge", FeatureGroupType.SpecialFeature), KristallpendelHilfeDesPendels(
			"Kristallpendel: Hilfe des Pendels", FeatureGroupType.SpecialFeature), KristallpendelLichtDerHoffnung(
			"Kristallpendel: Licht der Hoffnung", FeatureGroupType.SpecialFeature), KristallpendelPendelDerHellsicht(
			"Kristallpendel: Pendel der Hellsicht", FeatureGroupType.SpecialFeature), KristallpendelSeelengespür(
			"Kristallpendel: Seelengespür", FeatureGroupType.SpecialFeature), KristallpendelSteinDerWeisen(
			"Kristallpendel: Stein der Weisen", FeatureGroupType.SpecialFeature), KristallpendelTraumgespinste(
			"Kristallpendel: Traumgespinste", FeatureGroupType.SpecialFeature), KristallpendelWeiheDesPendels(
			"Kristallpendel: Weihe des Pendels", FeatureGroupType.SpecialFeature), ZauberzeichenFixierungszeichen(
			"Zauberzeichen: Fixierungszeichen", FeatureGroupType.SpecialFeature), ZauberzeichenHypnotischesZeichen(
			"Zauberzeichen: Hypnotisches Zeichen", FeatureGroupType.SpecialFeature), ZauberzeichenSiegelDerStille(
			"Zauberzeichen: Siegel der Stille", FeatureGroupType.SpecialFeature), ZauberzeichenSigilleDesUnsichtbarenWeges(
			"Zauberzeichen: Sigille des unsichtbaren Weges", FeatureGroupType.SpecialFeature), ZauberzeichenVerständigungszeichen(
			"Zauberzeichen: Verständigungszeichen", FeatureGroupType.SpecialFeature), ZauberzeichenZeichenDesStillstands(
			"Zauberzeichen: Zeichen des Stillstands", FeatureGroupType.SpecialFeature), ZauberzeichenZeichenGegenMagie(
			"Zauberzeichen: Zeichen gegen Magie", FeatureGroupType.SpecialFeature), ZauberzeichenZusatzzeichenTarnung(
			"Zauberzeichen: Zusatzzeichen Tarnung", FeatureGroupType.SpecialFeature), ZauberzeichenZusatzzeichenKraftquellenspeisung(
			"Zauberzeichen: Zusatzzeichen Kraftquellenspeisung", FeatureGroupType.SpecialFeature), TapasuulBlutFürVisar(
			"Tapasuul: Blut für Visar", FeatureGroupType.SpecialFeature), TapasuulEinHerzFürVisar(
			"Tapasuul: Ein Herz für Visar", FeatureGroupType.SpecialFeature), TapasuulGestaltDesTapam(
			"Tapasuul: Gestalt des Tapam", FeatureGroupType.SpecialFeature), TapasuulKerkerDesSatuul(
			"Tapasuul: Kerker des Satuul", FeatureGroupType.SpecialFeature), TapasuulKraftDesTapam(
			"Tapasuul: Kraft des Tapam", FeatureGroupType.SpecialFeature), LiturgiekenntnisDunkleZeiten(
			"Liturgiekenntnis (Dunkle Zeiten)", FeatureGroupType.SpecialFeature), GöttlicheEssenzKanalisierenI(
			"Göttliche Essenz kanalisieren (I)", FeatureGroupType.SpecialFeature), GöttlichesPrinzipStärkenI(
			"Göttliches Prinzip stärken (I)", FeatureGroupType.SpecialFeature), GöttlichenWillenErzwingenI(
			"Göttlichen Willen erzwingen (I)", FeatureGroupType.SpecialFeature), GöttlicheMachtBindenI(
			"Göttliche Macht binden (I)", FeatureGroupType.SpecialFeature), GöttlicheBeseelungRufenI(
			"Göttliche Beseelung rufen (I)", FeatureGroupType.SpecialFeature), GöttlichenSchutzErflehenI(
			"Göttlichen Schutz erflehen (I)", FeatureGroupType.SpecialFeature), GöttlicheEssenzKanalisierenII(
			"Göttliche Essenz kanalisieren (II)", FeatureGroupType.SpecialFeature), GöttlichesPrinzipStärkenII(
			"Göttliches Prinzip stärken (II)", FeatureGroupType.SpecialFeature), GöttlichenWillenErzwingenII(
			"Göttlichen Willen erzwingen (II)", FeatureGroupType.SpecialFeature), GöttlicheMachtBindenII(
			"Göttliche Macht binden (II)", FeatureGroupType.SpecialFeature), GöttlicheBeseelungRufenII(
			"Göttliche Beseelung rufen (II)", FeatureGroupType.SpecialFeature), GöttlichenSchutzErflehenII(
			"Göttlichen Schutz erflehen (II)", FeatureGroupType.SpecialFeature), GöttlicheEssenzKanalisierenIII(
			"Göttliche Essenz kanalisieren (III)", FeatureGroupType.SpecialFeature), GöttlichesPrinzipStärkenIII(
			"Göttliches Prinzip stärken (III)", FeatureGroupType.SpecialFeature), GöttlichenWillenErzwingenIII(
			"Göttlichen Willen erzwingen (III)", FeatureGroupType.SpecialFeature), GöttlicheMachtBindenIII(
			"Göttliche Macht binden (III)", FeatureGroupType.SpecialFeature), GöttlicheBeseelungRufenIII(
			"Göttliche Beseelung rufen (III)", FeatureGroupType.SpecialFeature), GöttlichenSchutzErflehenIII(
			"Göttlichen Schutz erflehen (III)", FeatureGroupType.SpecialFeature), GöttlicheEssenzKanalisierenIV(
			"Göttliche Essenz kanalisieren (IV)", FeatureGroupType.SpecialFeature), GöttlichesPrinzipStärkenIV(
			"Göttliches Prinzip stärken (IV)", FeatureGroupType.SpecialFeature), GöttlichenWillenErzwingenIV(
			"Göttlichen Willen erzwingen (IV)", FeatureGroupType.SpecialFeature), GöttlicheMachtBindenIV(
			"Göttliche Macht binden (IV)", FeatureGroupType.SpecialFeature), GöttlicheBeseelungRufenIV(
			"Göttliche Beseelung rufen (IV)", FeatureGroupType.SpecialFeature), GöttlichenSchutzErflehenIV(
			"Göttlichen Schutz erflehen (IV)", FeatureGroupType.SpecialFeature), GöttlicheEssenzKanalisierenV(
			"Göttliche Essenz kanalisieren (V)", FeatureGroupType.SpecialFeature), GöttlichesPrinzipStärkenV(
			"Göttliches Prinzip stärken (V)", FeatureGroupType.SpecialFeature), GöttlichenWillenErzwingenV(
			"Göttlichen Willen erzwingen (V)", FeatureGroupType.SpecialFeature), GöttlicheMachtBindenV(
			"Göttliche Macht binden (V)", FeatureGroupType.SpecialFeature), GöttlicheBeseelungRufenV(
			"Göttliche Beseelung rufen (V)", FeatureGroupType.SpecialFeature), GöttlichenSchutzErflehenV(
			"Göttlichen Schutz erflehen (V)", FeatureGroupType.SpecialFeature), WaffenloserKampfstilCyclopeischesRingen(
			"Waffenloser Kampfstil: Cyclopeisches Ringen", FeatureGroupType.SpecialFeature), WaffenloserKampfstilEchsenzwinger(
			"Waffenloser Kampfstil: Echsenzwinger", FeatureGroupType.SpecialFeature), WaffenloserKampfstilLegionärsstil(
			"Waffenloser Kampfstil: Legionärsstil", FeatureGroupType.SpecialFeature), WaffenloserKampfstilGossenstil(
			"Waffenloser Kampfstil: Gossenstil", FeatureGroupType.SpecialFeature), SchalenzauberSatinavsBannung(
			"Schalenzauber: Satinavs Bannung", FeatureGroupType.SpecialFeature), KugelzauberGeschenkDesSsadNavv(
			"Kugelzauber: Geschenk des Ssad'Navv", FeatureGroupType.SpecialFeature), KugelzauberAlMahmoud(
			"Kugelzauber: Al'Mahmoud", FeatureGroupType.SpecialFeature), KugelzauberSiegelDerSechsfachenEhre(
			"Kugelzauber: Siegel der Sechsfachen Ehre", FeatureGroupType.SpecialFeature), KugelzauberUntrüglicherBlickDesFalken(
			"Kugelzauber: Untrüglicher Blick des Falken", FeatureGroupType.SpecialFeature), KugelzauberZauberspeicher(
			"Kugelzauber: Zauberspeicher", FeatureGroupType.SpecialFeature), ZauberzeichenSchutzkreisGegenChimären(
			"Zauberzeichen: Schutzkreis gegen Chimären", FeatureGroupType.SpecialFeature), SeeleDerGemeinschaft(
			"Seele der Gemeinschaft", FeatureGroupType.SpecialFeature), ZauberzeichenBannUndSchutzkreisGegenElementare(
			"Zauberzeichen: Bann- und Schutzkreis gegen Elementare", FeatureGroupType.SpecialFeature), ZauberzeichenBannUndSchutzkreisGegenGehörnteDämonen(
			"Zauberzeichen: Bann- und Schutzkreis gegen gehörnte Dämonen", FeatureGroupType.SpecialFeature), ZauberzeichenBannUndSchutzkreisGegenGeisterwesen(
			"Zauberzeichen: Bann- und Schutzkreis gegen Geisterwesen", FeatureGroupType.SpecialFeature), ZauberzeichenBannUndSchutzkreisGegenNiedereDämonen(
			"Zauberzeichen: Bann- und Schutzkreis gegen niedere Dämonen", FeatureGroupType.SpecialFeature), ZauberzeichenSchutzkreisGegenReptilien(
			"Zauberzeichen: Schutzkreis gegen Reptilien", FeatureGroupType.SpecialFeature), ZauberzeichenSchutzkreisGegenUngeziefer(
			"Zauberzeichen: Schutzkreis gegen Ungeziefer", FeatureGroupType.SpecialFeature), ZauberzeichenBannkreisGegenChimären(
			"Zauberzeichen: Bannkreis gegen Chimären", FeatureGroupType.SpecialFeature), ZauberzeichenBannkreisGegenTraumgänger(
			"Zauberzeichen: Bannkreis gegen Traumgänger", FeatureGroupType.SpecialFeature), Ablegen("Ablegen",
			FeatureGroupType.SpecialFeature), AlmadanerSchritt("Almadaner Schritt", FeatureGroupType.SpecialFeature), AnspringenTier(
			"Anspringen (Tier)", FeatureGroupType.SpecialFeature), Anzeige("Anzeige", FeatureGroupType.SpecialFeature), ApportTier(
			"Apport (Tier)", FeatureGroupType.SpecialFeature), Ausbildung("Ausbildung", FeatureGroupType.SpecialFeature), Capriola(
			"Capriola", FeatureGroupType.SpecialFeature), Corbetto("Corbetto", FeatureGroupType.SpecialFeature), Eingefahren(
			"Eingefahren", FeatureGroupType.SpecialFeature), FassI("Fass I", FeatureGroupType.SpecialFeature), FassII(
			"Fass II", FeatureGroupType.SpecialFeature), Flugangriff("Flugangriff", FeatureGroupType.SpecialFeature), Galoppwechsel(
			"Galoppwechsel", FeatureGroupType.SpecialFeature), Gelände("Gelände", FeatureGroupType.SpecialFeature), Geländehindernisse(
			"Geländehindernisse", FeatureGroupType.SpecialFeature), Gespanngewöhnung("Gespanngewöhnung",
			FeatureGroupType.SpecialFeature), GezielterAngriff("Gezielter Angriff", FeatureGroupType.SpecialFeature), GezielterBiss(
			"Gezielter Biss", FeatureGroupType.SpecialFeature), GezielterTritt("Gezielter Tritt",
			FeatureGroupType.SpecialFeature), GroßerGegner("Großer Gegner", FeatureGroupType.SpecialFeature), Hinlegen(
			"Hinlegen", FeatureGroupType.SpecialFeature), Hinterhalt("Hinterhalt", FeatureGroupType.SpecialFeature), Kehrtwende(
			"Kehrtwende", FeatureGroupType.SpecialFeature), Kniefall("Kniefall", FeatureGroupType.SpecialFeature), KommenAufSignal(
			"Kommen auf Signal", FeatureGroupType.SpecialFeature), Kreisel("Kreisel", FeatureGroupType.SpecialFeature), Lanzengang(
			"Lanzengang", FeatureGroupType.SpecialFeature), Laut("Laut", FeatureGroupType.SpecialFeature), LenkenOhneZügel(
			"Lenken ohne Zügel", FeatureGroupType.SpecialFeature), Mehrspännig("Mehrspännig",
			FeatureGroupType.SpecialFeature), NiederwerfenTier("Niederwerfen (Tier)", FeatureGroupType.SpecialFeature), Passage(
			"Passage", FeatureGroupType.SpecialFeature), Piaffe("Piaffe", FeatureGroupType.SpecialFeature), Platz(
			"Platz", FeatureGroupType.SpecialFeature), Raserei("Raserei", FeatureGroupType.SpecialFeature), RastullahsSchwingen(
			"Rastullahs Schwingen", FeatureGroupType.SpecialFeature), Reitertreue("Reitertreue",
			FeatureGroupType.SpecialFeature), Schrecksicher("Schrecksicher", FeatureGroupType.SpecialFeature), SehrKleinerGegner(
			"Sehr kleiner Gegner", FeatureGroupType.SpecialFeature), Seitengänge("Seitengänge",
			FeatureGroupType.SpecialFeature), Separieren("Separieren", FeatureGroupType.SpecialFeature), Sitz("Sitz",
			FeatureGroupType.SpecialFeature), Sprungsicherheit("Sprungsicherheit", FeatureGroupType.SpecialFeature), Steigen(
			"Steigen", FeatureGroupType.SpecialFeature), Still("Still", FeatureGroupType.SpecialFeature), StilleWacht(
			"Stille Wacht", FeatureGroupType.SpecialFeature), Stillstand("Stillstand", FeatureGroupType.SpecialFeature), Stopp(
			"Stopp", FeatureGroupType.SpecialFeature), Sturzflug("Sturzflug", FeatureGroupType.SpecialFeature), Such(
			"Such", FeatureGroupType.SpecialFeature), Trampeln("Trampeln", FeatureGroupType.SpecialFeature), Treiben(
			"Treiben", FeatureGroupType.SpecialFeature), Trick("Trick", FeatureGroupType.SpecialFeature), Unarten(
			"Unarten", FeatureGroupType.SpecialFeature), Verbeißen("Verbeißen", FeatureGroupType.SpecialFeature), Verkrallen(
			"Verkrallen", FeatureGroupType.SpecialFeature), Wache("Wache", FeatureGroupType.SpecialFeature), Wacht(
			"Wacht", FeatureGroupType.SpecialFeature), WeichesGangwerk("Weiches Gangwerk",
			FeatureGroupType.SpecialFeature), Zusammenarbeit("Zusammenarbeit", FeatureGroupType.SpecialFeature), Zählen(
			"Zählen", FeatureGroupType.SpecialFeature), FlüstererDer1000Namen("Flüsterer der 1000 Namen",FeatureGroupType.SpecialFeature);

	public enum FeatureGroupType {
		Advantage, Disadvantage, SpecialFeature
	}

	private String xmlName;
	private FeatureGroupType type;

	FeatureType(String xmlName) {
		this.xmlName = xmlName;
	}

	FeatureType(String xmlName, FeatureGroupType type) {
		this.xmlName = xmlName;
		this.type = type;
	}

	public String xmlName() {
		return xmlName;
	}

	public boolean isAdvantage() {
		return type == FeatureGroupType.Advantage;
	}

	public boolean isDisdvantage() {
		return type == FeatureGroupType.Disadvantage;
	}

	public boolean isSpecialFeature() {
		return type == FeatureGroupType.SpecialFeature;
	}

	public FeatureGroupType type() {
		return type;
	}

	public static FeatureType byXmlName(String code) {

		if (TextUtils.isEmpty(code))
			return null;

		for (FeatureType attr : FeatureType.values()) {
			if (attr == Talentspezialisierung) {
				if (code.startsWith(Talentspezialisierung.xmlName()))
					return attr;
			} else if (attr == Zauberspezialisierung) {
				if (code.startsWith(Zauberspezialisierung.xmlName()))
					return attr;
			}
			if (attr.xmlName().equals(code)) {
				return attr;
			}
		}

		// search for old values without the prefix, Rüstungsgewöhnung I (Kettenmantel) == Rüstungsgewöhnung I
		int index = code.indexOf("(");
		String shortCode = null;
		if (index >= 0) {
			shortCode = code.substring(0, index).trim();
		}

		for (FeatureType attr : FeatureType.values()) {
			index = attr.xmlName().indexOf(':') + 1;
			if (index >= 0 && index < attr.xmlName().length()) {
				String subAttr = attr.xmlName().substring(index);
				if (subAttr.trim().equals(code)) {
					return attr;
				}
			}

			if (attr.xmlName().equals(shortCode)) {
				return attr;
			}
		}

		throw new FeatureTypeUnknownException(code);
	}
}