package com.dsatab.data.items;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bugsense.trace.BugSenseHandler;
import com.dsatab.exception.ItemTypeUnknownException;

public enum ItemType {
	Waffen('W', "weapons", true), Fernwaffen('D', "weapons", true), Rüstung('A', "weapons", true), Schilde('S',
			"weapons", true), Sonstiges('M', "misc"), Behälter('B', "bags"), Kleidung('C', "cloths"), Schmuck('X',
			"special");

	private static Map<ItemType, List<String>> categories;
	static {
		categories = new HashMap<ItemType, List<String>>(ItemType.values().length);

		categories.put(ItemType.Waffen, Arrays.asList("Schwerter", "Speere und Stäbe", "Säbel",
				"Zweihandschwerter und -säbel", "Anderthalbhänder", "Dolche", "Hiebwaffen", "Fechtwaffen", "Waffenlos",
				"Kettenstäbe", "Infanteriewaffen", "Wurfwaffen"));
		categories.put(ItemType.Schilde, Arrays.asList("Parierwaffe", "Schilde", "Dolche", "Waffenlos"));
		categories.put(ItemType.Fernwaffen, Arrays.asList("Schusswaffen", "Wurfwaffen", "Speere und Stäbe"));
		categories.put(ItemType.Rüstung, Arrays.asList("Komplettrüstung", "Arme", "Helm", "Beine", "Torso"));
		categories.put(ItemType.Kleidung, Arrays.asList("Oben", "Sonstiges Grau", "Kopfbedeckung", "Torso", "Schuhe",
				"Mantel", "Beine", "Handschuhe"));
		categories.put(ItemType.Behälter, Arrays.asList("Flaschen", "Taschen", "Sonstiges Grün", "Köcher", "Kisten"));
		categories.put(ItemType.Sonstiges, Arrays.asList("Zubehör", "Illumination", "Werkzeuge", "Essutensilien",
				"Feinmechanik", "Instrumente", "Reisebedarf", "Körperpflege", "Bücher", "Freizeitbedarf", "Nahrung",
				"Schreibwaren", "Sonstiges Rot", "Kräuter"));
		categories.put(ItemType.Schmuck, Arrays.asList("magische Artefakte", "Schmuck", "Sonstiges Blau"));
	}

	private final String path;

	private final char character;

	private final boolean equipable;

	private ItemType(char c, String path, boolean equipable) {
		this.path = path;
		this.character = c;
		this.equipable = equipable;
	}

	private ItemType(char c, String path) {
		this.path = path;
		this.character = c;
		this.equipable = false;
	}

	public List<String> getCategories() {
		if (categories.containsKey(this))
			return categories.get(this);
		else
			return Collections.emptyList();
	}

	public String getPath() {
		return path;
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
		case 'B':
			return Behälter;
		case 'C':
			return Kleidung;
		case 'X':
			return Schmuck;
		default:
			BugSenseHandler.sendException(new ItemTypeUnknownException(c));
			return Sonstiges;
		}
	}
}
