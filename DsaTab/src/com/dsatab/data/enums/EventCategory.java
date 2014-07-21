package com.dsatab.data.enums;

import com.dsatab.R;

public enum EventCategory {
	Misc(R.drawable.dsa_cubes), Abenteuer(R.drawable.dsa_adventure, true), Held(R.drawable.dsa_talents, true), Bekanntschaft(
			R.drawable.dsa_group, true), Freundschaft(R.drawable.dsa_heart, true), Tat(R.drawable.dsa_fist), Ort(
			R.drawable.dsa_castle, true), Heldensoftware(R.drawable.dsa_character);

	private int iconId;

	private boolean hasName;

	private EventCategory(int drawableId, boolean hasName) {
		this.iconId = drawableId;
		this.hasName = hasName;
	}

	private EventCategory(int drawableId) {
		this(drawableId, false);
	}

	public int getDrawableId() {
		return iconId;
	}

	public boolean hasName() {
		return hasName;
	}

}
