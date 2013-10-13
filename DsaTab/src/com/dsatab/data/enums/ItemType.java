package com.dsatab.data.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bugsense.trace.BugSenseHandler;
import com.dsatab.exception.ItemTypeUnknownException;

public enum ItemType {
	Waffen('W', true), Fernwaffen('D', true), Rüstung('A', true), Schilde('S', true), Sonstiges('M');

	private static Map<ItemType, List<String>> categories;
	static {
		categories = new HashMap<ItemType, List<String>>(ItemType.values().length);

		categories.put(ItemType.Waffen, Arrays.asList("Schwerter", "Speere und Stäbe", "Säbel",
				"Zweihandschwerter und -säbel", "Anderthalbhänder", "Dolche", "Hiebwaffen", "Fechtwaffen", "Waffenlos",
				"Kettenstäbe", "Infanteriewaffen", "Wurfwaffen"));
		categories.put(ItemType.Schilde, Arrays.asList("Parierwaffe", "Schilde", "Dolche", "Waffenlos"));
		categories.put(ItemType.Fernwaffen, Arrays.asList("Schusswaffen", "Wurfwaffen", "Speere und Stäbe"));
		categories.put(ItemType.Rüstung, Arrays.asList("Komplettrüstung", "Arme", "Helm", "Beine", "Torso"));
		categories.put(ItemType.Sonstiges, Arrays.asList("Zubehör", "Illumination", "Werkzeuge", "Essutensilien",
				"Feinmechanik", "Instrumente", "Reisebedarf", "Körperpflege", "Bücher", "Freizeitbedarf", "Nahrung",
				"Schreibwaren", "Kräuter", "Oben", "Kopfbedeckung", "Torso", "Schuhe", "Mantel", "Beine", "Handschuhe",
				"Flaschen", "Taschen", "Köcher", "Kisten", "magische Artefakte", "Schmuck", "Sonstiges"));

	}

	private final char character;

	private final boolean equipable;

	private ItemType(char c, boolean equipable) {
		this.character = c;
		this.equipable = equipable;
	}

	private ItemType(char c) {
		this.character = c;
		this.equipable = false;
	}

	public List<String> getCategories() {
		if (categories.containsKey(this))
			return categories.get(this);
		else
			return Collections.emptyList();
	}

	public char character() {
		return character;
	}

	public boolean isEquipable() {
		return equipable;
	}

	public static ItemType fromCharacter(char c) {

		switch (c) {
		case 'W':
			return Waffen;
		case 'D':
			return Fernwaffen;
		case 'A':
			return Rüstung;
		case 'S':
			return Schilde;
		case 'M':
			return Sonstiges;
		default:
			BugSenseHandler.sendException(new ItemTypeUnknownException(c));
			return Sonstiges;
		}
	}
}
