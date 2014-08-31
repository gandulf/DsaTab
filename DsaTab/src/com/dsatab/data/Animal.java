package com.dsatab.data;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.dsatab.data.Hero.CombatStyle;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.modifier.Modifier;

public class Animal extends AbstractBeing {

	private Hero hero;

	private String title;
	private String species;

	private String family;

	private int height;
	private float weight;
	private int price;

	private int count;
	private String slot;

	private Dice iniDice;

	private List<AnimalAttack> animalAttacks;

	public Animal(Hero hero) {
		this.hero = hero;
		this.configuration = hero.getHeroConfiguration();
		animalAttacks = new ArrayList<AnimalAttack>();
	}

	@Override
	protected String getId() {
		return getName() + getSlot();
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getTitle() {
		if (TextUtils.isEmpty(title))
			return getName();
		else
			return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getModifiedValue(AttributeType type, boolean includeBe, boolean includeLeAu) {
		Attribute attr = getAttribute(type);
		if (attr == null || attr.getValue() == null)
			return null;

		return attr.getValue();
	}

	public int getModifier(Probe probe, boolean includeBe, boolean includeLeAu) {
		int result = 0;
		return result;
	}

	@Override
	public List<Modifier> getModifiers(Probe probe, boolean includeBe, boolean includeLeAu) {
		return new ArrayList<Modifier>(2);
	}

	@Override
	public int getModifierTP(EquippedItem weapon) {
		return 0;
	}

	@Override
	public int getArmorBe() {
		return 0;
	}

	public Dice getIniDice() {
		return iniDice;
	}

	public void setIniDice(Dice iniDice) {
		this.iniDice = iniDice;
	}

	public void addAnimalAttack(AnimalAttack attack) {
		animalAttacks.add(attack);
	}

	public List<AnimalAttack> getAnimalAttacks() {
		return animalAttacks;
	}

	@Override
	public CombatStyle getCombatStyle() {
		return CombatStyle.Offensive;
	}

	@Override
	public void setCombatStyle(CombatStyle style) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setBeCalculation(boolean auto) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isBeCalculation() {
		return false;
	}

}
