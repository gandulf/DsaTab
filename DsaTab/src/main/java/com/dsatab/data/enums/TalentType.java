package com.dsatab.data.enums;

import com.dsatab.exception.TalentTypeUnknownException;

public enum TalentType {
    Anderthalbhänder(
            "Anderthalbhänder",
            TalentGroupType.Nahkampf
            ,-2)
    ,
    Armbrust(
            "Armbrust",
            TalentGroupType.Fernkampf
            ,-5)
    ,
    Bastardstäbe(
            "Bastardstäbe",
            TalentGroupType.Nahkampf
            ,-2)
    ,
    Belagerungswaffen(
            "Belagerungswaffen",
            TalentGroupType.Fernkampf
    )
    ,
    Blasrohr(
            "Blasrohr",
            TalentGroupType.Fernkampf
            ,-5)
    ,
    Bogen(
            "Bogen",
            TalentGroupType.Fernkampf
            ,-3)
    ,
    Diskus(
            "Diskus",
            TalentGroupType.Fernkampf
            ,-3)
    ,
    Dolche(
            "Dolche",
            TalentGroupType.Nahkampf
            ,-1)
    ,
    Fechtwaffen(
            "Fechtwaffen",
            TalentGroupType.Nahkampf
            ,-1)
    ,
    Hiebwaffen(
            "Hiebwaffen",
            TalentGroupType.Nahkampf
            ,-4)
    ,
    Infanteriewaffen(
            "Infanteriewaffen",
            TalentGroupType.Nahkampf
            ,-3)
    ,
    Kettenstäbe(
            "Kettenstäbe",
            TalentGroupType.Nahkampf
            ,-1)
    ,
    Kettenwaffen(
            "Kettenwaffen",
            TalentGroupType.Nahkampf
            ,-3)
    ,
    Lanzenreiten(
            "Lanzenreiten",
            TalentGroupType.Fernkampf
    )
    ,
    Peitsche(
            "Peitsche",
            TalentGroupType.Nahkampf
            ,-1)
    ,
    Raufen(
            "Raufen",
            TalentGroupType.Nahkampf
            ,0)
    ,
    Ringen(
            "Ringen",
            TalentGroupType.Nahkampf
            ,0)
    ,
    Säbel(
            "Säbel",
            TalentGroupType.Nahkampf
            ,-2)
    ,
    Schleuder(
            "Schleuder",
            TalentGroupType.Fernkampf
            ,-2)
    ,
    Schwerter(
            "Schwerter",
            TalentGroupType.Nahkampf
            ,-2)
    ,
    Speere(
            "Speere",
            TalentGroupType.Nahkampf
            ,-3)
    ,
    Stäbe(
            "Stäbe",
            TalentGroupType.Nahkampf
            ,-2)
    ,
    Wurfbeile(
            "Wurfbeile",
            TalentGroupType.Fernkampf
            ,-2)
    ,
    Wurfmesser(
            "Wurfmesser",
            TalentGroupType.Fernkampf
            ,-3)
    ,
    Wurfspeere(
            "Wurfspeere",
            TalentGroupType.Fernkampf
            ,-2)
    ,
    Zweihandflegel(
            "Zweihandflegel",
            TalentGroupType.Nahkampf
            ,-3)
    ,
    Zweihandhiebwaffen(
            "Zweihandhiebwaffen",
            TalentGroupType.Nahkampf
            ,-3)
    ,
    Zweihandschwertersäbel(
            "Zweihandschwerter/-säbel",
            TalentGroupType.Nahkampf
            ,-2)
    ,
    Akrobatik(
            "Akrobatik",
            TalentGroupType.Körperlich
    )
    ,
    Athletik(
            "Athletik",
            TalentGroupType.Körperlich
    )
    ,
    Fliegen(
            "Fliegen",
            TalentGroupType.Körperlich
    )
    ,
    Gaukeleien(
            "Gaukeleien",
            TalentGroupType.Körperlich
    )
    ,
    Klettern(
            "Klettern",
            TalentGroupType.Körperlich
    )
    ,
    Körperbeherrschung(
            "Körperbeherrschung",
            TalentGroupType.Körperlich
    )
    ,
    Reiten(
            "Reiten",
            TalentGroupType.Körperlich
    )
    ,
    Schleichen(
            "Schleichen",
            TalentGroupType.Körperlich
    )
    ,
    Schwimmen(
            "Schwimmen",
            TalentGroupType.Körperlich
    )
    ,
    Selbstbeherrschung(
            "Selbstbeherrschung",
            TalentGroupType.Körperlich
    )
    ,
    SichVerstecken(
            "Sich verstecken",
            TalentGroupType.Körperlich
    )
    ,
    Singen(
            "Singen",
            TalentGroupType.Körperlich
    )
    ,
    Sinnenschärfe(
            "Sinnenschärfe",
            TalentGroupType.Körperlich
    )
    ,
    Skifahren(
            "Skifahren",
            TalentGroupType.Körperlich
    )
    ,
    StimmenImitieren(
            "Stimmen imitieren",
            TalentGroupType.Körperlich
    )
    ,
    Tanzen(
            "Tanzen",
            TalentGroupType.Körperlich
    )
    ,
    Taschendiebstahl(
            "Taschendiebstahl",
            TalentGroupType.Körperlich
    )
    ,
    Zechen(
            "Zechen",
            TalentGroupType.Körperlich
    )
    ,
    Betören(
            "Betören",
            TalentGroupType.Gesellschaft
    )
    ,
    Etikette(
            "Etikette",
            TalentGroupType.Gesellschaft
    )
    ,
    Gassenwissen(
            "Gassenwissen",
            TalentGroupType.Gesellschaft
    )
    ,
    Lehren(
            "Lehren",
            TalentGroupType.Gesellschaft
    )
    ,
    Menschenkenntnis(
            "Menschenkenntnis",
            TalentGroupType.Gesellschaft
    )
    ,
    Schauspielerei(
            "Schauspielerei",
            TalentGroupType.Gesellschaft
    )
    ,
    SchriftlicherAusdruck(
            "Schriftlicher Ausdruck",
            TalentGroupType.Gesellschaft
    )
    ,
    SichVerkleiden(
            "Sich verkleiden",
            TalentGroupType.Gesellschaft
    )
    ,
    Überreden(
            "Überreden",
            TalentGroupType.Gesellschaft
    )
    ,
    Überzeugen(
            "Überzeugen",
            TalentGroupType.Gesellschaft
    )
    ,
    Galanterie(
            "Galanterie",
            TalentGroupType.Gesellschaft
    )
    ,
    Fährtensuchen(
            "Fährtensuchen",
            TalentGroupType.Natur
    )
    ,
    FallenStellen(
            "Fallen stellen",
            TalentGroupType.Natur
    )
    ,
    FesselnEntfesseln(
            "Fesseln/Entfesseln",
            TalentGroupType.Natur
    )
    ,
    FischenAngeln(
            "Fischen/Angeln",
            TalentGroupType.Natur
    )
    ,
    Orientierung(
            "Orientierung",
            TalentGroupType.Natur
    )
    ,
    Wettervorhersage(
            "Wettervorhersage",
            TalentGroupType.Natur
    )
    ,
    Seefischerei(
            "Seefischerei",
            TalentGroupType.Natur
    )
    ,
    Wildnisleben(
            "Wildnisleben",
            TalentGroupType.Natur
    )
    ,
    LesenSchreiben(
            "Lesen/Schreiben",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenAltesAlaani(
            "Lesen/Schreiben Altes Alaani",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenAltesAmulashtra(
            "Lesen/Schreiben Altes Amulashtra",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenAmulashtra(
            "Lesen/Schreiben Amulashtra",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenAngram(
            "Lesen/Schreiben Angram",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenArkanil(
            "Lesen/Schreiben Arkanil",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenAsdharia(
            "Lesen/Schreiben Asdharia",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenChrmk(
            "Lesen/Schreiben Chrmk",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenChuchas(
            "Lesen/Schreiben Chuchas",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenDrakhardZinken(
            "Lesen/Schreiben Drakhard-Zinken",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenDraknedGlyphen(
            "Lesen/Schreiben Drakned-Glyphen",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenGeheiligteGlyphenVonUnau(
            "Lesen/Schreiben Geheiligte Glyphen von Unau",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenGimarilGlyphen(
            "Lesen/Schreiben Gimaril-Glyphen",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenGjalskisch(
            "Lesen/Schreiben Gjalskisch",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenHjaldingscheRunen(
            "Lesen/Schreiben Hjaldingsche Runen",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenAltImperialeZeichen(
            "Lesen/Schreiben (Alt-)Imperiale Zeichen",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenIsdira(
            "Lesen/Schreiben Isdira",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenIsdiraAsdharia(
            "Lesen/Schreiben Isdira/Asdharia",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenAltesKemi(
            "Lesen/Schreiben Altes Kemi",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenKuslikerZeichen(
            "Lesen/Schreiben Kusliker Zeichen",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenNanduria(
            "Lesen/Schreiben Nanduria",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenRogolan(
            "Lesen/Schreiben Rogolan",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenTrollischeRaumbilderschrift(
            "Lesen/Schreiben Trollische Raumbilderschrift",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenTulamidya(
            "Lesen/Schreiben Tulamidya",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenUrtulamidya(
            "Lesen/Schreiben Urtulamidya",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenZhayad(
            "Lesen/Schreiben Zhayad",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenMahrischeGlyphen(
            "Lesen/Schreiben Mahrische Glyphen",
            TalentGroupType.Schriften
    )
    ,
    LesenSchreibenWudu(
            "Lesen/Schreiben Wudu",
            TalentGroupType.Schriften
    )
    ,
    SprachenKennen(
            "Sprachen kennen",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenGarethi(
            "Sprachen kennen Garethi",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenBosparano(
            "Sprachen kennen Bosparano",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenAltImperialAureliani(
            "Sprachen kennen Alt-Imperial/Aureliani",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenZyklopäisch(
            "Sprachen kennen Zyklopäisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenTulamidya(
            "Sprachen kennen Tulamidya",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenUrtulamidya(
            "Sprachen kennen Urtulamidya",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenZelemja(
            "Sprachen kennen Zelemja",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenAltesKemi(
            "Sprachen kennen Altes Kemi",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenAlaani(
            "Sprachen kennen Alaani",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenZhulchammaqra(
            "Sprachen kennen Zhulchammaqra",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenFerkina(
            "Sprachen kennen Ferkina",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenRuuz(
            "Sprachen kennen Ruuz",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenRabensprache(
            "Sprachen kennen Rabensprache",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenNujuka(
            "Sprachen kennen Nujuka",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenMohisch(
            "Sprachen kennen Mohisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenThorwalsch(
            "Sprachen kennen Thorwalsch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenHjaldingsch(
            "Sprachen kennen Hjaldingsch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenIsdira(
            "Sprachen kennen Isdira",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenAsdharia(
            "Sprachen kennen Asdharia",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenRogolan(
            "Sprachen kennen Rogolan",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenAngram(
            "Sprachen kennen Angram",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenOloghaijan(
            "Sprachen kennen Ologhaijan",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenOloarkh(
            "Sprachen kennen Oloarkh",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenGoblinisch(
            "Sprachen kennen Goblinisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenTrollisch(
            "Sprachen kennen Trollisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenRssahh(
            "Sprachen kennen Rssahh",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenGrolmisch(
            "Sprachen kennen Grolmisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenKoboldisch(
            "Sprachen kennen Koboldisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenDrachisch(
            "Sprachen kennen Drachisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenZhayad(
            "Sprachen kennen Zhayad",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenAtak(
            "Sprachen kennen Atak",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenFüchsisch(
            "Sprachen kennen Füchsisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenMahrisch(
            "Sprachen kennen Mahrisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenRissoal(
            "Sprachen kennen Rissoal",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenMolochisch(
            "Sprachen kennen Molochisch",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenNeckergesang(
            "Sprachen kennen Neckergesang",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenZLit(
            "Sprachen kennen Z'Lit",
            TalentGroupType.Sprachen
    )
    ,
    SprachenKennenWudu(
            "Sprachen kennen Wudu",
            TalentGroupType.Sprachen
    )
    ,
    Anatomie(
            "Anatomie",
            TalentGroupType.Wissen
    )
    ,
    Baukunst(
            "Baukunst",
            TalentGroupType.Wissen
    )
    ,
    BrettKartenspiel(
            "Brett-/Kartenspiel",
            TalentGroupType.Wissen
    )
    ,
    Geografie(
            "Geografie",
            TalentGroupType.Wissen
    )
    ,
    Geschichtswissen(
            "Geschichtswissen",
            TalentGroupType.Wissen
    )
    ,
    Gesteinskunde(
            "Gesteinskunde",
            TalentGroupType.Wissen
    )
    ,
    GötterUndKulte(
            "Götter und Kulte",
            TalentGroupType.Wissen
    )
    ,
    Heraldik(
            "Heraldik",
            TalentGroupType.Wissen
    )
    ,
    Hüttenkunde(
            "Hüttenkunde",
            TalentGroupType.Wissen
    )
    ,
    Schiffbau(
            "Schiffbau",
            TalentGroupType.Wissen
    )
    ,
    Kriegskunst(
            "Kriegskunst",
            TalentGroupType.Wissen
    )
    ,
    Kryptographie(
            "Kryptographie",
            TalentGroupType.Wissen
    )
    ,
    Magiekunde(
            "Magiekunde",
            TalentGroupType.Wissen
    )
    ,
    Mechanik(
            "Mechanik",
            TalentGroupType.Wissen
    )
    ,
    Pflanzenkunde(
            "Pflanzenkunde",
            TalentGroupType.Wissen
    )
    ,
    Philosophie(
            "Philosophie",
            TalentGroupType.Wissen
    )
    ,
    Rechnen(
            "Rechnen",
            TalentGroupType.Wissen
    )
    ,
    Rechtskunde(
            "Rechtskunde",
            TalentGroupType.Wissen
    )
    ,
    SagenUndLegenden(
            "Sagen und Legenden",
            TalentGroupType.Wissen
    )
    ,
    Schätzen(
            "Schätzen",
            TalentGroupType.Wissen
    )
    ,
    Sprachenkunde(
            "Sprachenkunde",
            TalentGroupType.Wissen
    )
    ,
    Staatskunst(
            "Staatskunst",
            TalentGroupType.Wissen
    )
    ,
    Sternkunde(
            "Sternkunde",
            TalentGroupType.Wissen
    )
    ,
    Tierkunde(
            "Tierkunde",
            TalentGroupType.Wissen
    )
    ,
    Abrichten(
            "Abrichten",
            TalentGroupType.Handwerk
    )
    ,
    Ackerbau(
            "Ackerbau",
            TalentGroupType.Handwerk
    )
    ,
    Alchimie(
            "Alchimie",
            TalentGroupType.Handwerk
    )
    ,
    Bergbau(
            "Bergbau",
            TalentGroupType.Handwerk
    )
    ,
    Bogenbau(
            "Bogenbau",
            TalentGroupType.Handwerk
    )
    ,
    BooteFahren(
            "Boote fahren",
            TalentGroupType.Handwerk
    )
    ,
    Brauer(
            "Brauer",
            TalentGroupType.Handwerk
    )
    ,
    Drucker(
            "Drucker",
            TalentGroupType.Handwerk
    )
    ,
    FahrzeugLenken(
            "Fahrzeug lenken",
            TalentGroupType.Handwerk
    )
    ,
    Falschspiel(
            "Falschspiel",
            TalentGroupType.Handwerk
    )
    ,
    Feinmechanik(
            "Feinmechanik",
            TalentGroupType.Handwerk
    )
    ,
    Feuersteinbearbeitung(
            "Feuersteinbearbeitung",
            TalentGroupType.Handwerk
    )
    ,
    Fleischer(
            "Fleischer",
            TalentGroupType.Handwerk
    )
    ,
    GerberKürschner(
            "Gerber/Kürschner",
            TalentGroupType.Handwerk
    )
    ,
    Glaskunst(
            "Glaskunst",
            TalentGroupType.Handwerk
    )
    ,
    Grobschmied(
            "Grobschmied",
            TalentGroupType.Handwerk
    )
    ,
    Handel(
            "Handel",
            TalentGroupType.Handwerk
    )
    ,
    Hauswirtschaft(
            "Hauswirtschaft",
            TalentGroupType.Handwerk
    )
    ,
    HeilkundeGift(
            "Heilkunde: Gift",
            TalentGroupType.Handwerk
    )
    ,
    HeilkundeKrankheiten(
            "Heilkunde: Krankheiten",
            TalentGroupType.Handwerk
    )
    ,
    HeilkundeSeele(
            "Heilkunde: Seele",
            TalentGroupType.Handwerk
    )
    ,
    HeilkundeWunden(
            "Heilkunde: Wunden",
            TalentGroupType.Handwerk
    )
    ,
    Kartographie(
            "Kartographie",
            TalentGroupType.Handwerk
    )
    ,
    HundeschlittenFahren(
            "Hundeschlitten fahren",
            TalentGroupType.Handwerk
    )
    ,
    EisseglerFahren(
            "Eissegler fahren",
            TalentGroupType.Handwerk
    )
    ,
    Kapellmeister(
            "Kapellmeister",
            TalentGroupType.Handwerk
    )
    ,
    Steuermann(
            "Steuermann",
            TalentGroupType.Handwerk
    )
    ,
    Holzbearbeitung(
            "Holzbearbeitung",
            TalentGroupType.Handwerk
    )
    ,
    Instrumentenbauer(
            "Instrumentenbauer",
            TalentGroupType.Handwerk
    )
    ,
    Kartografie(
            "Kartografie",
            TalentGroupType.Handwerk
    )
    ,
    Kochen(
            "Kochen",
            TalentGroupType.Handwerk
    )
    ,
    Kristallzucht(
            "Kristallzucht",
            TalentGroupType.Handwerk
    )
    ,
    Lederarbeiten(
            "Lederarbeiten",
            TalentGroupType.Handwerk
    )
    ,
    MalenZeichnen(
            "Malen/Zeichnen",
            TalentGroupType.Handwerk
    )
    ,
    Maurer(
            "Maurer",
            TalentGroupType.Handwerk
    )
    ,
    Metallguss(
            "Metallguss",
            TalentGroupType.Handwerk
    )
    ,
    Musizieren(
            "Musizieren",
            TalentGroupType.Handwerk
    )
    ,
    SchlösserKnacken(
            "Schlösser knacken",
            TalentGroupType.Handwerk
    )
    ,
    SchnapsBrennen(
            "Schnaps brennen",
            TalentGroupType.Handwerk
    )
    ,
    Schneidern(
            "Schneidern",
            TalentGroupType.Handwerk
    )
    ,
    Seefahrt(
            "Seefahrt",
            TalentGroupType.Handwerk
    )
    ,
    Seiler(
            "Seiler",
            TalentGroupType.Handwerk
    )
    ,
    Steinmetz(
            "Steinmetz",
            TalentGroupType.Handwerk
    )
    ,
    SteinschneiderJuwelier(
            "Steinschneider/Juwelier",
            TalentGroupType.Handwerk
    )
    ,
    Stellmacher(
            "Stellmacher",
            TalentGroupType.Handwerk
    )
    ,
    StoffeFärben(
            "Stoffe färben",
            TalentGroupType.Handwerk
    )
    ,
    Tätowieren(
            "Tätowieren",
            TalentGroupType.Handwerk
    )
    ,
    Töpfern(
            "Töpfern",
            TalentGroupType.Handwerk
    )
    ,
    Viehzucht(
            "Viehzucht",
            TalentGroupType.Handwerk
    )
    ,
    Webkunst(
            "Webkunst",
            TalentGroupType.Handwerk
    )
    ,
    Winzer(
            "Winzer",
            TalentGroupType.Handwerk
    )
    ,
    Zimmermann(
            "Zimmermann",
            TalentGroupType.Handwerk
    )
    ,
    Gefahreninstinkt(
            "Gefahreninstinkt",
            TalentGroupType.Gaben
    )
    ,
    Zwergennase(
            "Zwergennase",
            TalentGroupType.Gaben
    )
    ,
    GeisterRufen(
            "Geister rufen",
            TalentGroupType.Gaben
    )
    ,
    GeisterBannen(
            "Geister bannen",
            TalentGroupType.Gaben
    )
    ,
    GeisterBinden(
            "Geister binden",
            TalentGroupType.Gaben
    )
    ,
    GeisterAufnehmen(
            "Geister aufnehmen",
            TalentGroupType.Gaben
    )
    ,
    PirschUndAnsitzjagd(
            "Pirsch- und Ansitzjagd",
            TalentGroupType.Meta
    )
    ,
    NahrungSammeln(
            "Nahrung sammeln",
            TalentGroupType.Meta
    )
    ,
    Kräutersuchen(
            "Kräutersuchen",
            TalentGroupType.Meta
    )
    ,
    WacheHalten(
            "Wache halten",
            TalentGroupType.Meta
    )
    ,
    Ritualkenntnis(
            "Ritualkenntnis",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisAchazSchamane(
            "Ritualkenntnis: Achaz-Schamane",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisAlchimist(
            "Ritualkenntnis: Alchimist",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisAlhanisch(
            "Ritualkenntnis: Alhanisch",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisDerwisch(
            "Ritualkenntnis: Derwisch",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisDruide(
            "Ritualkenntnis: Druide",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisDruidischGeodisch(
            "Ritualkenntnis: Druidisch-Geodisch",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisDurroDûn(
            "Ritualkenntnis: Durro-Dûn",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisFerkinaSchamane(
            "Ritualkenntnis: Ferkina-Schamane",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisGjalskerSchamane(
            "Ritualkenntnis: Gjalsker-Schamane",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisGoblinSchamanin(
            "Ritualkenntnis: Goblin-Schamanin",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisGeode(
            "Ritualkenntnis: Geode",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisGildenmagie(
            "Ritualkenntnis: Gildenmagie",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisGüldenländisch(
            "Ritualkenntnis: Güldenländisch",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisGrolmisch(
            "Ritualkenntnis: Grolmisch",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisHexe(
            "Ritualkenntnis: Hexe",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisKophtanisch(
            "Ritualkenntnis: Kophtanisch",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisKristallomantie(
            "Ritualkenntnis: Kristallomantie",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisMudramulisch(
            "Ritualkenntnis: Mudramulisch",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisNivesenSchamane(
            "Ritualkenntnis: Nivesen-Schamane",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisOrkSchamane(
            "Ritualkenntnis: Ork-Schamane",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisRunenzauberei(
            "Ritualkenntnis: Runenzauberei",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisSatuarisch(
            "Ritualkenntnis: Satuarisch",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisScharlatan(
            "Ritualkenntnis: Scharlatan",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisTapasuul(
            "Ritualkenntnis: Tapasuul",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisTrollzackerSchamane(
            "Ritualkenntnis: Trollzacker-Schamane",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisWaldmenschenSchamane(
            "Ritualkenntnis: Waldmenschen-Schamane",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisWaldmenschenSchamaneUtulus(
            "Ritualkenntnis: Waldmenschen-Schamane (Utulus)",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisWaldmenschenSchamaneTocamuyac(
            "Ritualkenntnis: Waldmenschen-Schamane (Tocamuyac)",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisZaubertänzer(
            "Ritualkenntnis: Zaubertänzer",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisZaubertänzerHazaqi(
            "Ritualkenntnis: Zaubertänzer (Hazaqi)",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisZaubertänzerMajuna(
            "Ritualkenntnis: Zaubertänzer (Majuna)",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisZaubertänzernovadischeSharisad(
            "Ritualkenntnis: Zaubertänzer (novadische Sharisad)",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisZaubertänzertulamidischeSharisad(
            "Ritualkenntnis: Zaubertänzer (tulamidische Sharisad)",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisZibilja(
            "Ritualkenntnis: Zibilja",
            TalentGroupType.Gaben
    )
    ,
    RitualkenntnisLeonir(
            "Ritualkenntnis: Leonir",
            TalentGroupType.Gaben
    )
    ,
    Liturgiekenntnis(
            "Liturgiekenntnis",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisAngrosch(
            "Liturgiekenntnis (Angrosch)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisAves(
            "Liturgiekenntnis (Aves)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisBoron(
            "Liturgiekenntnis (Boron)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisEfferd(
            "Liturgiekenntnis (Efferd)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisFirun(
            "Liturgiekenntnis (Firun)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisGravesh(
            "Liturgiekenntnis (Gravesh)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisHRanga(
            "Liturgiekenntnis (H'Ranga)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisHSzint(
            "Liturgiekenntnis (H'Szint)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisHesinde(
            "Liturgiekenntnis (Hesinde)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisHimmelswölfe(
            "Liturgiekenntnis (Himmelswölfe)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisIfirn(
            "Liturgiekenntnis (Ifirn)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisIngerimm(
            "Liturgiekenntnis (Ingerimm)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisKamaluq(
            "Liturgiekenntnis (Kamaluq)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisKor(
            "Liturgiekenntnis (Kor)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisNandus(
            "Liturgiekenntnis (Nandus)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisNamenloser(
            "Liturgiekenntnis (Namenloser)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisPeraine(
            "Liturgiekenntnis (Peraine)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisPhex(
            "Liturgiekenntnis (Phex)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisPraios(
            "Liturgiekenntnis (Praios)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisRahja(
            "Liturgiekenntnis (Rahja)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisRondra(
            "Liturgiekenntnis (Rondra)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisSwafnir(
            "Liturgiekenntnis (Swafnir)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisTairach(
            "Liturgiekenntnis (Tairach)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisTravia(
            "Liturgiekenntnis (Travia)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisTsa(
            "Liturgiekenntnis (Tsa)",
            TalentGroupType.Gaben
    )
    ,
    LiturgiekenntnisZsahh(
            "Liturgiekenntnis (Zsahh)",
            TalentGroupType.Gaben
    )
    ,
    Prophezeien(
            "Prophezeien",
            TalentGroupType.Gaben
    )
    ,
    Geräuschhexerei(
            "Geräuschhexerei",
            TalentGroupType.Gaben
    )
    ,
    Magiegespür(
            "Magiegespür",
            TalentGroupType.Gaben
    )
    ,
    Tierempathiespeziell(
            "Tierempathie (speziell)",
            TalentGroupType.Gaben
    )
    ,
    Tierempathiealle(
            "Tierempathie (alle)",
            TalentGroupType.Gaben
    )
    ,
    Empathie(
            "Empathie",
            TalentGroupType.Gaben
    )
    ,
    Immanspiel(
            "Immanspiel",
            TalentGroupType.Körperlich
    )

    ;

    private static final String DEPRECATED_WACHE_NAME = "Wache";
    private static final String DEPRECATED_KRÄUTERSUCHE_NAME1 = "Kräutersuchen";
    private static final String DEPRECATED_KRÄUTERSUCHE_NAME2 = "Kräuter Suchen";
    private static final String DEPRECATED_KRÄUTERSUCHE_NAME3 = "Kräutersuche";
    private static final String DEPRECATED_PIRSCH_ANSITZ_JAGD = "PirschAnsitzJagd ";

    private TalentGroupType groupType;
    private Integer be;

    private String xmlName;

    TalentType(String name, TalentGroupType type) {
        this(name,type,null);
    }
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