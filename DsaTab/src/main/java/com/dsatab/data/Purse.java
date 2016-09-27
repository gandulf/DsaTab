package com.dsatab.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Purse {

    public static class PurseValue implements Value {

        private PurseUnit unit;
        private Purse purse;

        public PurseValue(Purse purse,PurseUnit unit) {
            this.unit = unit;
            this.purse = purse;
        }

        public PurseUnit getUnit() {
            return unit;
        }

        public Purse getPurse() {
            return purse;
        }

        @Override
        public int getMinimum() {
            return 0;
        }

        @Override
        public int getMaximum() {
            return 999;
        }

        @Override
        public String getName() {
            return unit.xmlName();
        }

        @Override
        public Integer getReferenceValue() {
            return null;
        }

        @Override
        public Integer getValue() {
            return purse.getCoins(unit);
        }

        @Override
        public void reset() {
            setValue(0);
        }

        @Override
        public void setValue(Integer value) {
            int oldValue = purse.getCoins(unit);
            int newValue = value == null ? 0 : value;

            if (oldValue!=newValue) {
                purse.setCoins(unit, newValue);
                purse.being.fireValueChangedEvent(this);
            }
        }
    }

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

		Currency(PurseUnit... purseUnits) {
			this(null, purseUnits);
		}

		Currency(String name, PurseUnit... purseUnits) {
			if (name == null)
				name = name();

			this.name = name;
			this.purseUnits = Arrays.asList(purseUnits);
		}

        public String xmlName() {
            return name;
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

		PurseUnit() {
			this(null);
		}

		PurseUnit(String name) {
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

	private Map<PurseUnit, Integer> coins;

    private AbstractBeing being;

	public Purse(AbstractBeing being) {
        this.being = being;
        coins = new HashMap<PurseUnit, Integer>(4);
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
