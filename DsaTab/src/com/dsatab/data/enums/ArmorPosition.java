package com.dsatab.data.enums;

public enum ArmorPosition {

	Kopf("Kopf", 2), Brust("Brust", 4), Ruecken("Rücken", 4), Bauch("Bauch", 4), LinkerArm("Linker Arm", 1), RechterArm(
			"Rechter Arm", 1), LinkesBein("Linkes Bein", 2), RechtesBein("Rechtes Bein", 2);

	protected String name;
	protected int multiplier;

	private ArmorPosition(String name, int multi) {
		this.name = name;
		this.multiplier = multi;
	}

	public String getName() {
		return name;
	}

	public int getMultiplier() {
		return multiplier;
	}

}
