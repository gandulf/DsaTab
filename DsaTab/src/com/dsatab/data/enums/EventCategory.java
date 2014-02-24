package com.dsatab.data.enums;

import com.dsatab.R;

public enum EventCategory {
	Misc(R.drawable.m_icon_misc), Abenteuer(R.drawable.m_icon_abenteuer, true), Held(R.drawable.m_icon_held, true), Bekanntschaft(
			R.drawable.m_icon_bekanntschaft, true), Freundschaft(R.drawable.m_icon_freundschaft, true), Tat(
			R.drawable.m_icon_tat), Ort(R.drawable.m_icon_ort, true), Heldensoftware(R.drawable.m_icon_heldensoftware);

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
