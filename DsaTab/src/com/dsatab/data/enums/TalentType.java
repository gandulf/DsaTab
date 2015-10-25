package com.dsatab.data.enums;

import com.dsatab.exception.TalentTypeUnknownException;

public enum TalentType {
	Anderthalbhänder("Anderthalbhänder", TalentGroupType.Nahkampf, null), Armbrust("Armbrust",
			TalentGroupType.Fernkampf, null), Bastardstäbe("Bastardstäbe", TalentGroupType.Nahkampf, null), Belagerungswaffen(
			"Belagerungswaffen", TalentGroupType.Fernkampf, null), Blasrohr("Blasrohr", TalentGroupType.Fernkampf, null), Bogen(
			"Bogen", TalentGroupType.Fernkampf, null), Diskus("Diskus", TalentGroupType.Fernkampf, null), Dolche(
			"Dolche", TalentGroupType.Nahkampf, null), Fechtwaffen("Fechtwaffen", TalentGroupType.Nahkampf, null), Hiebwaffen(
			"Hiebwaffen", TalentGroupType.Nahkampf, null), Infanteriewaffen("Infanteriewaffen",
			TalentGroupType.Nahkampf, null), Kettenstäbe("Kettenstäbe", TalentGroupType.Nahkampf, null), Kettenwaffen(
			"Kettenwaffen", TalentGroupType.Nahkampf, null), Lanzenreiten("Lanzenreiten", TalentGroupType.Fernkampf,
			null), Peitsche("Peitsche", TalentGroupType.Nahkampf, null), Raufen("Raufen", TalentGroupType.Nahkampf,
			null), Ringen("Ringen", TalentGroupType.Nahkampf, null), Säbel("Säbel", TalentGroupType.Nahkampf, null), Schleuder(
			"Schleuder", TalentGroupType.Fernkampf, null), Schwerter("Schwerter", TalentGroupType.Nahkampf, null), Speere(
			"Speere", TalentGroupType.Nahkampf, null), Stäbe("Stäbe", TalentGroupType.Nahkampf, null), Wurfbeile(
			"Wurfbeile", TalentGroupType.Fernkampf, null), Wurfmesser("Wurfmesser", TalentGroupType.Fernkampf, null), Wurfspeere(
			"Wurfspeere", TalentGroupType.Fernkampf, null), Zweihandflegel("Zweihandflegel", TalentGroupType.Nahkampf,
			null), Zweihandhiebwaffen("Zweihandhiebwaffen", TalentGroupType.Nahkampf, null), Zweihandschwertersäbel(
			"Zweihandschwerter/-säbel", TalentGroupType.Nahkampf, null), Akrobatik("Akrobatik",
			TalentGroupType.Körperlich, null), Athletik("Athletik", TalentGroupType.Körperlich, null), Fliegen(
			"Fliegen", TalentGroupType.Körperlich, null), Gaukeleien("Gaukeleien", TalentGroupType.Körperlich, null), Klettern(
			"Klettern", TalentGroupType.Körperlich, null), Körperbeherrschung("Körperbeherrschung",
			TalentGroupType.Körperlich, null), Reiten("Reiten", TalentGroupType.Körperlich, null), Schleichen(
			"Schleichen", TalentGroupType.Körperlich, null), Schwimmen("Schwimmen", TalentGroupType.Körperlich, null), Selbstbeherrschung(
			"Selbstbeherrschung", TalentGroupType.Körperlich, null), SichVerstecken("Sich verstecken",
			TalentGroupType.Körperlich, null), Singen("Singen", TalentGroupType.Körperlich, null), Sinnenschärfe(
			"Sinnenschärfe", TalentGroupType.Körperlich, null), Skifahren("Skifahren", TalentGroupType.Körperlich, null), StimmenImitieren(
			"Stimmen imitieren", TalentGroupType.Körperlich, null), Tanzen("Tanzen", TalentGroupType.Körperlich, null), Taschendiebstahl(
			"Taschendiebstahl", TalentGroupType.Körperlich, null), Zechen("Zechen", TalentGroupType.Körperlich, null), Betören(
			"Betören", TalentGroupType.Gesellschaft, null), Etikette("Etikette", TalentGroupType.Gesellschaft, null), Gassenwissen(
			"Gassenwissen", TalentGroupType.Gesellschaft, null), Lehren("Lehren", TalentGroupType.Gesellschaft, null), Menschenkenntnis(
			"Menschenkenntnis", TalentGroupType.Gesellschaft, null), Schauspielerei("Schauspielerei",
			TalentGroupType.Gesellschaft, null), SchriftlicherAusdruck("Schriftlicher Ausdruck",
			TalentGroupType.Gesellschaft, null), SichVerkleiden("Sich verkleiden", TalentGroupType.Gesellschaft, null), Überreden(
			"Überreden", TalentGroupType.Gesellschaft, null), Überzeugen("Überzeugen", TalentGroupType.Gesellschaft,
			null), Galanterie("Galanterie", TalentGroupType.Gesellschaft, null), Fährtensuchen("Fährtensuchen",
			TalentGroupType.Natur, null), FallenStellen("Fallen stellen", TalentGroupType.Natur, null), FesselnEntfesseln(
			"Fesseln/Entfesseln", TalentGroupType.Natur, null), FischenAngeln("Fischen/Angeln", TalentGroupType.Natur,
			null), Orientierung("Orientierung", TalentGroupType.Natur, null), Wettervorhersage("Wettervorhersage",
			TalentGroupType.Natur, null), Seefischerei("Seefischerei", TalentGroupType.Natur, null), Wildnisleben(
			"Wildnisleben", TalentGroupType.Natur, null), LesenSchreiben("Lesen/Schreiben", TalentGroupType.Schriften,
			null), LesenSchreibenAltesAlaani("Lesen/Schreiben Altes Alaani", TalentGroupType.Schriften, null), LesenSchreibenAltesAmulashtra(
			"Lesen/Schreiben Altes Amulashtra", TalentGroupType.Schriften, null), LesenSchreibenAmulashtra(
			"Lesen/Schreiben Amulashtra", TalentGroupType.Schriften, null), LesenSchreibenAngram(
			"Lesen/Schreiben Angram", TalentGroupType.Schriften, null), LesenSchreibenArkanil(
			"Lesen/Schreiben Arkanil", TalentGroupType.Schriften, null), LesenSchreibenChrmk("Lesen/Schreiben Chrmk",
			TalentGroupType.Schriften, null), LesenSchreibenChuchas("Lesen/Schreiben Chuchas",
			TalentGroupType.Schriften, null), LesenSchreibenDrakhardZinken("Lesen/Schreiben Drakhard-Zinken",
			TalentGroupType.Schriften, null), LesenSchreibenDraknedGlyphen("Lesen/Schreiben Drakned-Glyphen",
			TalentGroupType.Schriften, null), LesenSchreibenGeheiligteGlyphenVonUnau(
			"Lesen/Schreiben Geheiligte Glyphen von Unau", TalentGroupType.Schriften, null), LesenSchreibenGimarilGlyphen(
			"Lesen/Schreiben Gimaril-Glyphen", TalentGroupType.Schriften, null), LesenSchreibenGjalskisch(
			"Lesen/Schreiben Gjalskisch", TalentGroupType.Schriften, null), LesenSchreibenHjaldingscheRunen(
			"Lesen/Schreiben Hjaldingsche Runen", TalentGroupType.Schriften, null), LesenSchreibenAltImperialeZeichen(
			"Lesen/Schreiben (Alt-)Imperiale Zeichen", TalentGroupType.Schriften, null), LesenSchreibenIsdiraAsdharia(
			"Lesen/Schreiben Isdira/Asdharia", TalentGroupType.Schriften, null), LesenSchreibenAltesKemi(
			"Lesen/Schreiben Altes Kemi", TalentGroupType.Schriften, null), LesenSchreibenKuslikerZeichen(
			"Lesen/Schreiben Kusliker Zeichen", TalentGroupType.Schriften, null), LesenSchreibenNanduria(
			"Lesen/Schreiben Nanduria", TalentGroupType.Schriften, null), LesenSchreibenRogolan(
			"Lesen/Schreiben Rogolan", TalentGroupType.Schriften, null), LesenSchreibenTrollischeRaumbilderschrift(
			"Lesen/Schreiben Trollische Raumbilderschrift", TalentGroupType.Schriften, null), LesenSchreibenTulamidya(
			"Lesen/Schreiben Tulamidya", TalentGroupType.Schriften, null), LesenSchreibenUrtulamidya(
			"Lesen/Schreiben Urtulamidya", TalentGroupType.Schriften, null), LesenSchreibenZhayad(
			"Lesen/Schreiben Zhayad", TalentGroupType.Schriften, null), LesenSchreibenMahrischeGlyphen(
			"Lesen/Schreiben Mahrische Glyphen", TalentGroupType.Schriften, null), LesenSchreibenWudu(
			"Lesen/Schreiben Wudu", TalentGroupType.Schriften, null), SprachenKennen("Sprachen kennen",
			TalentGroupType.Sprachen, null), SprachenKennenGarethi("Sprachen kennen Garethi", TalentGroupType.Sprachen,
			null), SprachenKennenBosparano("Sprachen kennen Bosparano", TalentGroupType.Sprachen, null), SprachenKennenAltImperialAureliani(
			"Sprachen kennen Alt-Imperial/Aureliani", TalentGroupType.Sprachen, null), SprachenKennenZyklopäisch(
			"Sprachen kennen Zyklopäisch", TalentGroupType.Sprachen, null), SprachenKennenTulamidya(
			"Sprachen kennen Tulamidya", TalentGroupType.Sprachen, null), SprachenKennenUrtulamidya(
			"Sprachen kennen Urtulamidya", TalentGroupType.Sprachen, null), SprachenKennenZelemja(
			"Sprachen kennen Zelemja", TalentGroupType.Sprachen, null), SprachenKennenAltesKemi(
			"Sprachen kennen Altes Kemi", TalentGroupType.Sprachen, null), SprachenKennenAlaani(
			"Sprachen kennen Alaani", TalentGroupType.Sprachen, null), SprachenKennenZhulchammaqra(
			"Sprachen kennen Zhulchammaqra", TalentGroupType.Sprachen, null), SprachenKennenFerkina(
			"Sprachen kennen Ferkina", TalentGroupType.Sprachen, null), SprachenKennenRuuz("Sprachen kennen Ruuz",
			TalentGroupType.Sprachen, null), SprachenKennenRabensprache("Sprachen kennen Rabensprache",
			TalentGroupType.Sprachen, null), SprachenKennenNujuka("Sprachen kennen Nujuka", TalentGroupType.Sprachen,
			null), SprachenKennenMohisch("Sprachen kennen Mohisch", TalentGroupType.Sprachen, null), SprachenKennenThorwalsch(
			"Sprachen kennen Thorwalsch", TalentGroupType.Sprachen, null), SprachenKennenHjaldingsch(
			"Sprachen kennen Hjaldingsch", TalentGroupType.Sprachen, null), SprachenKennenIsdira(
			"Sprachen kennen Isdira", TalentGroupType.Sprachen, null), SprachenKennenAsdharia(
			"Sprachen kennen Asdharia", TalentGroupType.Sprachen, null), SprachenKennenRogolan(
			"Sprachen kennen Rogolan", TalentGroupType.Sprachen, null), SprachenKennenAngram("Sprachen kennen Angram",
			TalentGroupType.Sprachen, null), SprachenKennenOloghaijan("Sprachen kennen Ologhaijan",
			TalentGroupType.Sprachen, null), SprachenKennenOloarkh("Sprachen kennen Oloarkh", TalentGroupType.Sprachen,
			null), SprachenKennenGoblinisch("Sprachen kennen Goblinisch", TalentGroupType.Sprachen, null), SprachenKennenTrollisch(
			"Sprachen kennen Trollisch", TalentGroupType.Sprachen, null), SprachenKennenRssahh(
			"Sprachen kennen Rssahh", TalentGroupType.Sprachen, null), SprachenKennenGrolmisch(
			"Sprachen kennen Grolmisch", TalentGroupType.Sprachen, null), SprachenKennenKoboldisch(
			"Sprachen kennen Koboldisch", TalentGroupType.Sprachen, null), SprachenKennenDrachisch(
			"Sprachen kennen Drachisch", TalentGroupType.Sprachen, null), SprachenKennenZhayad(
			"Sprachen kennen Zhayad", TalentGroupType.Sprachen, null), SprachenKennenAtak("Sprachen kennen Atak",
			TalentGroupType.Sprachen, null), SprachenKennenFüchsisch("Sprachen kennen Füchsisch",
			TalentGroupType.Sprachen, null), SprachenKennenMahrisch("Sprachen kennen Mahrisch",
			TalentGroupType.Sprachen, null), SprachenKennenRissoal("Sprachen kennen Rissoal", TalentGroupType.Sprachen,
			null), SprachenKennenMolochisch("Sprachen kennen Molochisch", TalentGroupType.Sprachen, null), SprachenKennenNeckergesang(
			"Sprachen kennen Neckergesang", TalentGroupType.Sprachen, null), SprachenKennenZLit(
			"Sprachen kennen Z'Lit", TalentGroupType.Sprachen, null), SprachenKennenWudu("Sprachen kennen Wudu",
			TalentGroupType.Sprachen, null), Anatomie("Anatomie", TalentGroupType.Wissen, null), Baukunst("Baukunst",
			TalentGroupType.Wissen, null), BrettKartenspiel("Brett-/Kartenspiel", TalentGroupType.Wissen, null), Geografie(
			"Geografie", TalentGroupType.Wissen, null), Geschichtswissen("Geschichtswissen", TalentGroupType.Wissen,
			null), Gesteinskunde("Gesteinskunde", TalentGroupType.Wissen, null), GötterUndKulte("Götter und Kulte",
			TalentGroupType.Wissen, null), Heraldik("Heraldik", TalentGroupType.Wissen, null), Hüttenkunde(
			"Hüttenkunde", TalentGroupType.Wissen, null), Schiffbau("Schiffbau", TalentGroupType.Wissen, null), Kriegskunst(
			"Kriegskunst", TalentGroupType.Wissen, null), Kryptographie("Kryptographie", TalentGroupType.Wissen, null), Magiekunde(
			"Magiekunde", TalentGroupType.Wissen, null), Mechanik("Mechanik", TalentGroupType.Wissen, null), Pflanzenkunde(
			"Pflanzenkunde", TalentGroupType.Wissen, null), Philosophie("Philosophie", TalentGroupType.Wissen, null), Rechnen(
			"Rechnen", TalentGroupType.Wissen, null), Rechtskunde("Rechtskunde", TalentGroupType.Wissen, null), SagenUndLegenden(
			"Sagen und Legenden", TalentGroupType.Wissen, null), Schätzen("Schätzen", TalentGroupType.Wissen, null), Sprachenkunde(
			"Sprachenkunde", TalentGroupType.Wissen, null), Staatskunst("Staatskunst", TalentGroupType.Wissen, null), Sternkunde(
			"Sternkunde", TalentGroupType.Wissen, null), Tierkunde("Tierkunde", TalentGroupType.Wissen, null), Abrichten(
			"Abrichten", TalentGroupType.Handwerk, null), Ackerbau("Ackerbau", TalentGroupType.Handwerk, null), Alchimie(
			"Alchimie", TalentGroupType.Handwerk, null), Bergbau("Bergbau", TalentGroupType.Handwerk, null), Bogenbau(
			"Bogenbau", TalentGroupType.Handwerk, null), BooteFahren("Boote fahren", TalentGroupType.Handwerk, null), Brauer(
			"Brauer", TalentGroupType.Handwerk, null), Drucker("Drucker", TalentGroupType.Handwerk, null), FahrzeugLenken(
			"Fahrzeug lenken", TalentGroupType.Handwerk, null), Falschspiel("Falschspiel", TalentGroupType.Handwerk,
			null), Feinmechanik("Feinmechanik", TalentGroupType.Handwerk, null), Feuersteinbearbeitung(
			"Feuersteinbearbeitung", TalentGroupType.Handwerk, null), Fleischer("Fleischer", TalentGroupType.Handwerk,
			null), GerberKürschner("Gerber/Kürschner", TalentGroupType.Handwerk, null), Glaskunst("Glaskunst",
			TalentGroupType.Handwerk, null), Grobschmied("Grobschmied", TalentGroupType.Handwerk, null), Handel(
			"Handel", TalentGroupType.Handwerk, null), Hauswirtschaft("Hauswirtschaft", TalentGroupType.Handwerk, null), HeilkundeGift(
			"Heilkunde: Gift", TalentGroupType.Handwerk, null), HeilkundeKrankheiten("Heilkunde: Krankheiten",
			TalentGroupType.Handwerk, null), HeilkundeSeele("Heilkunde: Seele", TalentGroupType.Handwerk, null), HeilkundeWunden(
			"Heilkunde: Wunden", TalentGroupType.Handwerk, null), Kartographie("Kartographie",
			TalentGroupType.Handwerk, null), HundeschlittenFahren("Hundeschlitten fahren", TalentGroupType.Handwerk,
			null), EisseglerFahren("Eissegler fahren", TalentGroupType.Handwerk, null), Kapellmeister("Kapellmeister",
			TalentGroupType.Handwerk, null), Steuermann("Steuermann", TalentGroupType.Handwerk, null), Holzbearbeitung(
			"Holzbearbeitung", TalentGroupType.Handwerk, null), Instrumentenbauer("Instrumentenbauer",
			TalentGroupType.Handwerk, null), Kartografie("Kartografie", TalentGroupType.Handwerk, null), Kochen(
			"Kochen", TalentGroupType.Handwerk, null), Kristallzucht("Kristallzucht", TalentGroupType.Handwerk, null), Lederarbeiten(
			"Lederarbeiten", TalentGroupType.Handwerk, null), MalenZeichnen("Malen/Zeichnen", TalentGroupType.Handwerk,
			null), Maurer("Maurer", TalentGroupType.Handwerk, null), Metallguss("Metallguss", TalentGroupType.Handwerk,
			null), Musizieren("Musizieren", TalentGroupType.Handwerk, null), SchlösserKnacken("Schlösser knacken",
			TalentGroupType.Handwerk, null), SchnapsBrennen("Schnaps brennen", TalentGroupType.Handwerk, null), Schneidern(
			"Schneidern", TalentGroupType.Handwerk, null), Seefahrt("Seefahrt", TalentGroupType.Handwerk, null), Seiler(
			"Seiler", TalentGroupType.Handwerk, null), Steinmetz("Steinmetz", TalentGroupType.Handwerk, null), SteinschneiderJuwelier(
			"Steinschneider/Juwelier", TalentGroupType.Handwerk, null), Stellmacher("Stellmacher",
			TalentGroupType.Handwerk, null), StoffeFärben("Stoffe färben", TalentGroupType.Handwerk, null), Tätowieren(
			"Tätowieren", TalentGroupType.Handwerk, null), Töpfern("Töpfern", TalentGroupType.Handwerk, null), Viehzucht(
			"Viehzucht", TalentGroupType.Handwerk, null), Webkunst("Webkunst", TalentGroupType.Handwerk, null), Winzer(
			"Winzer", TalentGroupType.Handwerk, null), Zimmermann("Zimmermann", TalentGroupType.Handwerk, null), Gefahreninstinkt(
			"Gefahreninstinkt", TalentGroupType.Gaben, null), Zwergennase("Zwergennase", TalentGroupType.Gaben, null), GeisterRufen(
			"Geister rufen", TalentGroupType.Gaben, null), GeisterBannen("Geister bannen", TalentGroupType.Gaben, null), GeisterBinden(
			"Geister binden", TalentGroupType.Gaben, null), GeisterAufnehmen("Geister aufnehmen",
			TalentGroupType.Gaben, null), PirschUndAnsitzjagd("Pirsch- und Ansitzjagd", TalentGroupType.Meta, null), NahrungSammeln(
			"Nahrung sammeln", TalentGroupType.Meta, null), Kräutersuchen("Kräutersuchen", TalentGroupType.Meta, null), WacheHalten(
			"Wache halten", TalentGroupType.Meta, null), Ritualkenntnis("Ritualkenntnis", TalentGroupType.Gaben, null), RitualkenntnisAchazSchamane(
			"Ritualkenntnis: Achaz-Schamane", TalentGroupType.Gaben, null), RitualkenntnisAlchimist(
			"Ritualkenntnis: Alchimist", TalentGroupType.Gaben, null), RitualkenntnisAlhanisch(
			"Ritualkenntnis: Alhanisch", TalentGroupType.Gaben, null), RitualkenntnisDerwisch(
			"Ritualkenntnis: Derwisch", TalentGroupType.Gaben, null), RitualkenntnisDruide("Ritualkenntnis: Druide",
			TalentGroupType.Gaben, null), RitualkenntnisDruidischGeodisch("Ritualkenntnis: Druidisch-Geodisch",
			TalentGroupType.Gaben, null), RitualkenntnisDurroDûn("Ritualkenntnis: Durro-Dûn", TalentGroupType.Gaben,
			null), RitualkenntnisFerkinaSchamane("Ritualkenntnis: Ferkina-Schamane", TalentGroupType.Gaben, null), RitualkenntnisGjalskerSchamane(
			"Ritualkenntnis: Gjalsker-Schamane", TalentGroupType.Gaben, null), RitualkenntnisGoblinSchamanin(
			"Ritualkenntnis: Goblin-Schamanin", TalentGroupType.Gaben, null), RitualkenntnisGeode(
			"Ritualkenntnis: Geode", TalentGroupType.Gaben, null), RitualkenntnisGildenmagie(
			"Ritualkenntnis: Gildenmagie", TalentGroupType.Gaben, null), RitualkenntnisGüldenländisch(
			"Ritualkenntnis: Güldenländisch", TalentGroupType.Gaben, null), RitualkenntnisGrolmisch(
			"Ritualkenntnis: Grolmisch", TalentGroupType.Gaben, null), RitualkenntnisHexe("Ritualkenntnis: Hexe",
			TalentGroupType.Gaben, null), RitualkenntnisKophtanisch("Ritualkenntnis: Kophtanisch",
			TalentGroupType.Gaben, null), RitualkenntnisKristallomantie("Ritualkenntnis: Kristallomantie",
			TalentGroupType.Gaben, null), RitualkenntnisMudramulisch("Ritualkenntnis: Mudramulisch",
			TalentGroupType.Gaben, null), RitualkenntnisNivesenSchamane("Ritualkenntnis: Nivesen-Schamane",
			TalentGroupType.Gaben, null), RitualkenntnisOrkSchamane("Ritualkenntnis: Ork-Schamane",
			TalentGroupType.Gaben, null), RitualkenntnisRunenzauberei("Ritualkenntnis: Runenzauberei",
			TalentGroupType.Gaben, null), RitualkenntnisSatuarisch("Ritualkenntnis: Satuarisch", TalentGroupType.Gaben,
			null), RitualkenntnisScharlatan("Ritualkenntnis: Scharlatan", TalentGroupType.Gaben, null), RitualkenntnisTapasuul(
			"Ritualkenntnis: Tapasuul", TalentGroupType.Gaben, null), RitualkenntnisTrollzackerSchamane(
			"Ritualkenntnis: Trollzacker-Schamane", TalentGroupType.Gaben, null), RitualkenntnisWaldmenschenSchamane(
			"Ritualkenntnis: Waldmenschen-Schamane", TalentGroupType.Gaben, null), RitualkenntnisWaldmenschenSchamaneUtulus(
			"Ritualkenntnis: Waldmenschen-Schamane (Utulus)", TalentGroupType.Gaben, null), RitualkenntnisWaldmenschenSchamaneTocamuyac(
			"Ritualkenntnis: Waldmenschen-Schamane (Tocamuyac)", TalentGroupType.Gaben, null), RitualkenntnisZaubertänzer(
			"Ritualkenntnis: Zaubertänzer", TalentGroupType.Gaben, null), RitualkenntnisZaubertänzerHazaqi(
			"Ritualkenntnis: Zaubertänzer (Hazaqi)", TalentGroupType.Gaben, null), RitualkenntnisZaubertänzerMajuna(
			"Ritualkenntnis: Zaubertänzer (Majuna)", TalentGroupType.Gaben, null), RitualkenntnisZaubertänzernovadischeSharisad(
			"Ritualkenntnis: Zaubertänzer (novadische Sharisad)", TalentGroupType.Gaben, null), RitualkenntnisZaubertänzertulamidischeSharisad(
			"Ritualkenntnis: Zaubertänzer (tulamidische Sharisad)", TalentGroupType.Gaben, null), RitualkenntnisZibilja(
			"Ritualkenntnis: Zibilja", TalentGroupType.Gaben, null), Liturgiekenntnis("Liturgiekenntnis",
			TalentGroupType.Gaben, null), LiturgiekenntnisAngrosch("Liturgiekenntnis (Angrosch)",
			TalentGroupType.Gaben, null), LiturgiekenntnisAves("Liturgiekenntnis (Aves)", TalentGroupType.Gaben, null), LiturgiekenntnisBoron(
			"Liturgiekenntnis (Boron)", TalentGroupType.Gaben, null), LiturgiekenntnisEfferd(
			"Liturgiekenntnis (Efferd)", TalentGroupType.Gaben, null), LiturgiekenntnisFirun(
			"Liturgiekenntnis (Firun)", TalentGroupType.Gaben, null), LiturgiekenntnisGravesh(
			"Liturgiekenntnis (Gravesh)", TalentGroupType.Gaben, null), LiturgiekenntnisHRanga(
			"Liturgiekenntnis (H'Ranga)", TalentGroupType.Gaben, null), LiturgiekenntnisHSzint(
			"Liturgiekenntnis (H'Szint)", TalentGroupType.Gaben, null), LiturgiekenntnisHesinde(
			"Liturgiekenntnis (Hesinde)", TalentGroupType.Gaben, null), LiturgiekenntnisHimmelswölfe(
			"Liturgiekenntnis (Himmelswölfe)", TalentGroupType.Gaben, null), LiturgiekenntnisIfirn(
			"Liturgiekenntnis (Ifirn)", TalentGroupType.Gaben, null), LiturgiekenntnisIngerimm(
			"Liturgiekenntnis (Ingerimm)", TalentGroupType.Gaben, null), LiturgiekenntnisKamaluq(
			"Liturgiekenntnis (Kamaluq)", TalentGroupType.Gaben, null), LiturgiekenntnisKor("Liturgiekenntnis (Kor)",
			TalentGroupType.Gaben, null), LiturgiekenntnisNandus("Liturgiekenntnis (Nandus)", TalentGroupType.Gaben,
			null), LiturgiekenntnisNamenloser("Liturgiekenntnis (Namenloser)", TalentGroupType.Gaben, null), LiturgiekenntnisPeraine(
			"Liturgiekenntnis (Peraine)", TalentGroupType.Gaben, null), LiturgiekenntnisPhex("Liturgiekenntnis (Phex)",
			TalentGroupType.Gaben, null), LiturgiekenntnisPraios("Liturgiekenntnis (Praios)", TalentGroupType.Gaben,
			null), LiturgiekenntnisRahja("Liturgiekenntnis (Rahja)", TalentGroupType.Gaben, null), LiturgiekenntnisRondra(
			"Liturgiekenntnis (Rondra)", TalentGroupType.Gaben, null), LiturgiekenntnisSwafnir(
			"Liturgiekenntnis (Swafnir)", TalentGroupType.Gaben, null), LiturgiekenntnisTairach(
			"Liturgiekenntnis (Tairach)", TalentGroupType.Gaben, null), LiturgiekenntnisTravia(
			"Liturgiekenntnis (Travia)", TalentGroupType.Gaben, null), LiturgiekenntnisTsa("Liturgiekenntnis (Tsa)",
			TalentGroupType.Gaben, null), LiturgiekenntnisZsahh("Liturgiekenntnis (Zsahh)", TalentGroupType.Gaben, null), Prophezeien(
			"Prophezeien", TalentGroupType.Gaben, null), Geräuschhexerei("Geräuschhexerei", TalentGroupType.Gaben, null), Magiegespür(
			"Magiegespür", TalentGroupType.Gaben, null), Tierempathiespeziell("Tierempathie (speziell)",
			TalentGroupType.Gaben, null), Tierempathiealle("Tierempathie (alle)", TalentGroupType.Gaben, null), Empathie(
			"Empathie", TalentGroupType.Gaben, null), Immanspiel("Immanspiel", TalentGroupType.Körperlich, null);

	private static final String DEPRECATED_WACHE_NAME = "Wache";
	private static final String DEPRECATED_KRÄUTERSUCHE_NAME1 = "Kräutersuchen";
	private static final String DEPRECATED_KRÄUTERSUCHE_NAME2 = "Kräuter Suchen";
	private static final String DEPRECATED_KRÄUTERSUCHE_NAME3 = "Kräutersuche";
	private static final String DEPRECATED_PIRSCH_ANSITZ_JAGD = "PirschAnsitzJagd ";

	private TalentGroupType groupType;
	private Integer be;

	private String xmlName;

	TalentType(String name, TalentGroupType type, Integer be) {
		this.be = be;
		this.xmlName = name;
		this.groupType = type;
	}

	public String xmlName() {
		if (xmlName != null)
			return xmlName;
		else
			return name();
	}

	public TalentGroupType type() {
		return groupType;
	}

	public Integer getBe() {
		return be;
	}

	public static TalentType byValue(String type) {
		if (DEPRECATED_KRÄUTERSUCHE_NAME1.equalsIgnoreCase(type)
				|| DEPRECATED_KRÄUTERSUCHE_NAME2.equalsIgnoreCase(type)
				|| DEPRECATED_KRÄUTERSUCHE_NAME3.equalsIgnoreCase(type)) {
			return TalentType.Kräutersuchen;
		} else if (DEPRECATED_WACHE_NAME.equalsIgnoreCase(type)) {
			return TalentType.WacheHalten;
		} else if (DEPRECATED_PIRSCH_ANSITZ_JAGD.equalsIgnoreCase(type)) {
			return TalentType.PirschUndAnsitzjagd;
		} else {
			return TalentType.valueOf(type);
		}

	}

	public static TalentType byXmlName(String code) {

		if (code == null)
			return null;

		for (TalentType attr : TalentType.values()) {
			if (attr.xmlName().equals(code)) {
				return attr;
			}
		}
		throw new TalentTypeUnknownException(code);
	}
}