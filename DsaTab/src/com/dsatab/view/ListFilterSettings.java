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

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.dsatab.data.JSONable;
import com.dsatab.data.Markable;

/**
 * 
 * 
 */
public class ListFilterSettings implements FilterSettings, JSONable, Serializable {

	private static final long serialVersionUID = -128741208133727278L;

	private static final String FIELD_INCLUDE_MODIFIERS = "includeModifiers";
	private static final String FIELD_SHOW_NORMAL = "showNormal";
	private static final String FIELD_SHOW_FAVORITE = "showFavorite";
	private static final String FIELD_SHOW_UNUSED = "showUnused";

	private boolean showNormal, showFavorite, showUnused;

	private boolean includeModifiers;

	/**
	 * Basic constructor
	 */
	public ListFilterSettings() {

	}

	public ListFilterSettings(JSONObject jsonObject) {
		this.showFavorite = jsonObject.optBoolean(FIELD_SHOW_FAVORITE);
		this.showNormal = jsonObject.optBoolean(FIELD_SHOW_NORMAL);
		this.showUnused = jsonObject.optBoolean(FIELD_SHOW_UNUSED);
		this.includeModifiers = jsonObject.optBoolean(FIELD_INCLUDE_MODIFIERS);
	}

	public ListFilterSettings(boolean fav, boolean normal, boolean unused, boolean incMod) {
		this.showFavorite = fav;
		this.showNormal = normal;
		this.showUnused = unused;
		this.includeModifiers = incMod;
	}

	/**
	 * @param in
	 */
	public ListFilterSettings(Parcel in) {
		boolean[] booleans = new boolean[4];
		in.readBooleanArray(booleans);
		showFavorite = booleans[0];
		showNormal = booleans[1];
		showUnused = booleans[2];
		includeModifiers = booleans[3];
	}

	/**
	 * Creator for the Parcelable
	 */
	public static final Parcelable.Creator<ListFilterSettings> CREATOR = new Parcelable.Creator<ListFilterSettings>() {
		public ListFilterSettings createFromParcel(Parcel in) {
			return new ListFilterSettings(in);
		}

		public ListFilterSettings[] newArray(int size) {
			return new ListFilterSettings[size];
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
		dest.writeBooleanArray(new boolean[] { showFavorite, showNormal, showUnused, includeModifiers });
	}

	public boolean isShowNormal() {
		return showNormal;
	}

	public void setShowNormal(boolean showNormal) {
		this.showNormal = showNormal;
	}

	public boolean isShowFavorite() {
		return showFavorite;
	}

	public void setShowFavorite(boolean showFavorite) {
		this.showFavorite = showFavorite;
	}

	public boolean isShowUnused() {
		return showUnused;
	}

	public void setShowUnused(boolean showUnused) {
		this.showUnused = showUnused;
	}

	public boolean isIncludeModifiers() {
		return includeModifiers;
	}

	public void setIncludeModifiers(boolean includeModifiers) {
		this.includeModifiers = includeModifiers;
	}

	public boolean equals(ListFilterSettings settings) {
		return equals(settings.isShowFavorite(), settings.isShowNormal(), settings.isShowUnused(),
				settings.isIncludeModifiers());
	}

	public boolean equals(boolean showFavorite, boolean showNormal, boolean showUnused, boolean includeModifiers) {
		return this.showFavorite == showFavorite && this.showNormal == showNormal && this.showUnused == showUnused
				&& this.includeModifiers == includeModifiers;
	}

	public boolean isAllVisible() {
		return showFavorite && showNormal && showUnused;
	}

	public boolean isVisible(Markable mark) {
		return (showFavorite && mark.isFavorite()) || (showUnused && mark.isUnused())
				|| (showNormal && !mark.isFavorite() && !mark.isUnused());
	}

	public void set(ListFilterSettings settings) {
		if (settings != null) {
			set(settings.isShowFavorite(), settings.isShowNormal(), settings.isShowUnused(),
					settings.isIncludeModifiers());
		}
	}

	/**
	 * @param checked
	 * @param checked2
	 * @param checked3
	 */
	public void set(boolean fav, boolean normal, boolean unused, boolean incMod) {
		this.showFavorite = fav;
		this.showNormal = normal;
		this.showUnused = unused;
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
		jsonObject.put(FIELD_SHOW_FAVORITE, showFavorite);
		jsonObject.put(FIELD_SHOW_NORMAL, showNormal);
		jsonObject.put(FIELD_SHOW_UNUSED, showUnused);
		return jsonObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getName() + " " + showNormal + " " + showFavorite + " " + showUnused + " " + includeModifiers;
	}

}
