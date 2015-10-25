package com.dsatab.data.modifier;

import com.dsatab.util.Util;

import java.util.Collection;

public class Modifier {

	public static final String TITLE_MANUAL = "Manuell";

	public static final String PREF_PREFIX_ACTIVE = "modifierEnabled_";
	public static final String PREF_PREFIX_SPINNER_INDEX = "modifierSpinnerIndex_";

	private int modifier;

	private String title;

	private String description;

	private boolean active;

	private String[] spinnerOptions;
	private int[] spinnerValues;
	private int spinnerIndex;

	public Modifier(int modifier, String title) {
		this(modifier, title, null);
	}

	public Modifier(int modifier, String title, String description) {
		super();
		this.modifier = modifier;
		this.title = title;
		this.description = description;
		this.active = true;
	}

	@Override
	public String toString() {
		return title + " " + Util.toProbe(modifier) + " | " + description;
	}

	public static int sum(Collection<Modifier> mods) {
		int sum = 0;
		for (Modifier m : mods) {
			if (m.isActive()) {
				sum += m.modifier;
			}
		}
		return sum;
	}

	public int getModifier() {
		return modifier;
	}

	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean toggleActive() {
		this.active = !active;

		return this.active;
	}

	public String[] getSpinnerOptions() {
		return spinnerOptions;
	}

	public void setSpinnerOptions(String[] spinnerOptions) {
		this.spinnerOptions = spinnerOptions;
	}

	public int[] getSpinnerValues() {
		return spinnerValues;
	}

	public void setSpinnerValues(int[] spinnerValues) {
		this.spinnerValues = spinnerValues;
	}

	public int getSpinnerIndex() {
		return spinnerIndex;
	}

	public void setSpinnerIndex(int spinnerIndex) {
		this.spinnerIndex = spinnerIndex;
	}

}
