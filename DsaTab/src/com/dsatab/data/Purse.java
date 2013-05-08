package com.dsatab.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Purse {

	public enum Currency {
		AlAnfa("Al'Anfa", PurseUnit.Doublone, PurseUnit.Oreal, PurseUnit.KleinerOreal, PurseUnit.Dirham), Vallusa(
				PurseUnit.Witten, PurseUnit.Stüber, PurseUnit.Flindrich), Trahelien(PurseUnit.Suvar, PurseUnit.Hedsch,
				PurseUnit.Chryskl), Xeranien(PurseUnit.Borbaradstaler, PurseUnit.Zholvari, PurseUnit.Splitter), Bornland(
				PurseUnit.Batzen, PurseUnit.Groschen, PurseUnit.Deut), Mittelreich(PurseUnit.Dukat,
				PurseUnit.Silbertaler, PurseUnit.Heller, PurseUnit.Kreuzer), Aranien(PurseUnit.Dinar,
				PurseUnit.Schekel, PurseUnit.Hallah, PurseUnit.Kurush), Zwerge(PurseUnit.Zwergentaler), Kalifat(
				PurseUnit.Marawedi, PurseUnit.Zechine, PurseUnit.Muwlat), Horasreich(PurseUnit.Horasdor), Amazonen(
				PurseUnit.Amazonenkronen);

		private String name;

		private List<PurseUnit> purseUnits;

		private Currency(PurseUnit... purseUnits) {
			this(null, purseUnits);
		}

		public String xmlName() {
			return name;
		}

		private Currency(String name, PurseUnit... purseUnits) {
			if (name == null)
				name = name();

			this.name = name;
			this.purseUnits = Arrays.asList(purseUnits);
		}

		public List<PurseUnit> units() {
			return purseUnits;
		}

		public static Currency getByXmlName(String name) {
			for (Currency c : Currency.values()) {
				if (c.name.equals(name))
					return c;
			}
			return null;
		}
	}

	public enum PurseUnit {
		Dukat, Silbertaler, Heller, Kreuzer, Doublone, Flindrich, Hedsch, Dirham, Splitter, Zholvari, Batzen, Dinar, Stüber, KleinerOreal(
				"Kleiner Oreal"), Deut, Hallah, Borbaradstaler, Groschen, Zwergentaler, Kurush, Muwlat, Horasdor, Witten, Marawedi, Amazonenkronen, Schekel, Oreal, Suvar, Zechine, Chryskl(
				"Ch'ryskl");

		private String name;

		private PurseUnit() {
			this(null);
		}

		private PurseUnit(String name) {
			if (name == null)
				name = name();

			this.name = name;
		}

		public Currency currency() {
			for (Currency cur : Currency.values()) {
				if (cur.units().contains(this))
					return cur;
			}
			return null;
		}

		public String xmlName() {
			return name;
		}

		public static PurseUnit getByXmlName(String name) {
			for (PurseUnit c : PurseUnit.values()) {
				if (c.name.equals(name))
					return c;
			}
			return null;
		}
	}

	private Currency activeCurrency;

	private Map<PurseUnit, Integer> coins;

	public Purse() {
		coins = new HashMap<PurseUnit, Integer>(4);
	}

	public Currency getActiveCurrency() {
		return activeCurrency;
	}

	public void setActiveCurrency(Currency activeCurrency) {
		this.activeCurrency = activeCurrency;
	}

	public void setCoins(PurseUnit w, int value) {
		coins.put(w, value);
	}

	public Map<PurseUnit, Integer> getCoins() {
		return coins;
	}

	public int getCoins(PurseUnit w) {
		Integer m = coins.get(w);
		if (m != null)
			return m;
		else
			return 0;
	}

}
