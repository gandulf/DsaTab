/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.dsatab.data.Markable;

/**
 * 
 * 
 */
public class FightFilterSettings implements FilterSettings, Cloneable {

	private static final long serialVersionUID = 2113054507904847696L;

	private static final String FIELD_INCLUDE_MODIFIERS = "includeModifier";

	private static final String FIELD_SHOW_ARMOR = "showArmor";

	private static final String FIELD_SHOW_EVADE = "evade";

	private static final String FIELD_SHOW_MODIFIER = "showModifier";

	private boolean showArmor, showModifier, showEvade;

	private boolean includeModifiers;

	/**
	 * Basic constructor
	 */
	public FightFilterSettings() {

	}

	public FightFilterSettings(boolean armor, boolean modifier, boolean evade, boolean incMod) {
		this.showModifier = modifier;
		this.showArmor = armor;
		this.showEvade = evade;
		this.includeModifiers = incMod;
	}

	public FightFilterSettings(JSONObject jsonObject) {
		this.showArmor = jsonObject.optBoolean(FIELD_SHOW_ARMOR);
		this.showEvade = jsonObject.optBoolean(FIELD_SHOW_EVADE);
		this.showModifier = jsonObject.optBoolean(FIELD_SHOW_MODIFIER);
		this.includeModifiers = jsonObject.optBoolean(FIELD_INCLUDE_MODIFIERS);
	}

	/**
	 * @param in
	 */
	public FightFilterSettings(Parcel in) {
		boolean[] booleans = new boolean[4];
		in.readBooleanArray(booleans);
		showArmor = booleans[0];
		showEvade = booleans[1];
		showModifier = booleans[2];
		includeModifiers = booleans[3];
	}

	/**
	 * Creator for the Parcelable
	 */
	public static final Parcelable.Creator<FightFilterSettings> CREATOR = new Parcelable.Creator<FightFilterSettings>() {
		public FightFilterSettings createFromParcel(Parcel in) {
			return new FightFilterSettings(in);
		}

		public FightFilterSettings[] newArray(int size) {
			return new FightFilterSettings[size];
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBooleanArray(new boolean[] { showArmor, showEvade, showModifier, includeModifiers });
	}

	public boolean isShowArmor() {
		return showArmor;
	}

	public void setShowArmor(boolean showNormal) {
		this.showArmor = showNormal;
	}

	public boolean isShowModifier() {
		return showModifier;
	}

	public void setShowModifiers(boolean showFavorite) {
		this.showModifier = showFavorite;
	}

	public boolean isShowEvade() {
		return showEvade;
	}

	public void setShowEvade(boolean showUnused) {
		this.showEvade = showUnused;
	}

	public boolean isIncludeModifiers() {
		return includeModifiers;
	}

	public void setIncludeModifiers(boolean includeModifiers) {
		this.includeModifiers = includeModifiers;
	}

	public boolean equals(FightFilterSettings settings) {
		return equals(settings.isShowArmor(), settings.isShowModifier(), settings.isShowEvade(),
				settings.isIncludeModifiers());
	}

	public boolean equals(boolean showArmor, boolean showModifier, boolean showEvade, boolean incModifier) {
		return this.showModifier == showModifier && this.showArmor == showArmor && this.showEvade == showEvade
				&& this.includeModifiers == incModifier;
	}

	public boolean isAllVisible() {
		return showModifier && showArmor && showEvade;
	}

	public boolean isVisible(Markable mark) {
		return (showModifier && mark.isFavorite()) || (showEvade && mark.isUnused())
				|| (showArmor && !mark.isFavorite() && !mark.isUnused());
	}

	public void set(FightFilterSettings settings) {
		set(settings.isShowArmor(), settings.isShowModifier(), settings.isShowEvade(), settings.isIncludeModifiers());
	}

	/**
	 * @param checked
	 * @param checked2
	 * @param checked3
	 */
	public void set(boolean armor, boolean modifer, boolean evade, boolean incMod) {
		this.showModifier = modifer;
		this.showArmor = armor;
		this.showEvade = evade;
		this.includeModifiers = incMod;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.JSONable#toJSONObject()
	 */
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(FIELD_INCLUDE_MODIFIERS, includeModifiers);
		jsonObject.put(FIELD_SHOW_ARMOR, showArmor);
		jsonObject.put(FIELD_SHOW_EVADE, showEvade);
		jsonObject.put(FIELD_SHOW_MODIFIER, showModifier);
		return jsonObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public FightFilterSettings clone() {
		return new FightFilterSettings(showArmor, showModifier, showEvade, includeModifiers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getName() + " " + showArmor + " " + showEvade + " " + showModifier + " " + includeModifiers;
	}

}
