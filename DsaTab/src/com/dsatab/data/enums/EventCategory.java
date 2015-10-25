package com.dsatab.data.enums;

import com.dsatab.R;

public enum EventCategory {
	Misc(R.drawable.vd_cubes), Abenteuer(R.drawable.vd_black_book, true), Held(R.drawable.vd_biceps, true), Bekanntschaft(
			R.drawable.vd_backup, true), Freundschaft(R.drawable.vd_broken_heart, true), Tat(R.drawable.vd_palm), Ort(
			R.drawable.vd_castle, true), Heldensoftware(R.drawable.vd_anatomy);

	private int iconId;

	private boolean hasName;

	EventCategory(int drawableId, boolean hasName) {
		this.iconId = drawableId;
		this.hasName = hasName;
	}

	EventCategory(int drawableId) {
		this(drawableId, false);
	}

	public int getDrawableId() {
		return iconId;
	}

	public boolean hasName() {
		return hasName;
	}

}
