package com.dsatab.view;

import java.io.Serializable;

import android.os.Parcelable;

import com.dsatab.data.JSONable;

public interface FilterSettings extends JSONable, Serializable, Parcelable {

	public enum FilterType {
		Talent, Spell, Art, Fight
	}
}
