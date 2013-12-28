package com.dsatab.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.dsatab.data.Art;
import com.dsatab.data.Attribute;
import com.dsatab.data.JSONable;
import com.dsatab.data.Markable;
import com.dsatab.data.Spell;
import com.dsatab.data.Talent;
import com.dsatab.data.Value;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.listable.FileListable;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.Util;

public class ListSettings implements JSONable, Serializable, Parcelable {

	private static final long serialVersionUID = -128741208133727278L;

	private static final String FIELD_INCLUDE_MODIFIERS = "includeModifiers";
	private static final String FIELD_SHOW_NORMAL = "showNormal";
	private static final String FIELD_SHOW_FAVORITE = "showFavorite";
	private static final String FIELD_SHOW_UNUSED = "showUnused";
	private static final String FIELD_ITEMS = "items";

	public enum FilterType {
		Talent, Spell, Art, Fight
	}

	private boolean showNormal, showFavorite, showUnused;

	private boolean includeModifiers;

	private List<ListItem> listItems;

	/**
	 * Basic constructor
	 */
	public ListSettings() {
		this(true, true, true, true);
	}

	public ListSettings(JSONObject jsonObject) throws JSONException {
		this(jsonObject.optBoolean(FIELD_SHOW_FAVORITE), jsonObject.optBoolean(FIELD_SHOW_NORMAL), jsonObject
				.optBoolean(FIELD_SHOW_UNUSED), jsonObject.optBoolean(FIELD_INCLUDE_MODIFIERS));

		if (jsonObject.has(FIELD_ITEMS)) {
			JSONArray array = jsonObject.getJSONArray(FIELD_ITEMS);
			listItems = new ArrayList<ListItem>(array.length());
			for (int i = 0; i < array.length(); i++) {
				JSONObject tab = array.getJSONObject(i);
				ListItem info = new ListItem(tab);
				listItems.add(info);
			}
		} else {
			listItems = new ArrayList<ListItem>();
		}
	}

	public ListSettings(ListSettings settings) {
		this(settings.showFavorite, settings.showNormal, settings.showUnused, settings.includeModifiers);
	}

	public ListSettings(boolean fav, boolean normal, boolean unused, boolean incMod) {
		this.showFavorite = fav;
		this.showNormal = normal;
		this.showUnused = unused;
		this.includeModifiers = incMod;

		listItems = new ArrayList<ListItem>();
	}

	/**
	 * @param in
	 */
	public ListSettings(Parcel in) {
		boolean[] booleans = new boolean[4];
		in.readBooleanArray(booleans);
		showFavorite = booleans[0];
		showNormal = booleans[1];
		showUnused = booleans[2];
		includeModifiers = booleans[3];

		listItems = new ArrayList<ListItem>();
		in.readTypedList(listItems, ListItem.CREATOR);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Creator for the Parcelable
	 */
	public static final Parcelable.Creator<ListSettings> CREATOR = new Parcelable.Creator<ListSettings>() {
		@Override
		public ListSettings createFromParcel(Parcel in) {
			return new ListSettings(in);
		}

		@Override
		public ListSettings[] newArray(int size) {
			return new ListSettings[size];
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeBooleanArray(new boolean[] { showFavorite, showNormal, showUnused, includeModifiers });
		dest.writeTypedList(listItems);
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

	public boolean equals(ListSettings settings) {
		return settings != null
				&& equals(settings.isShowFavorite(), settings.isShowNormal(), settings.isShowUnused(),
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

	public void set(ListSettings settings) {
		if (settings != null) {
			set(settings.isShowFavorite(), settings.isShowNormal(), settings.isShowUnused(),
					settings.isIncludeModifiers());
		}
	}

	public void addListItem(ListItem item) {
		listItems.add(item);
	}

	public void removeListItem(ListItem item) {
		listItems.remove(item);
	}

	public List<ListItem> getListItems() {
		return listItems;
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

	public boolean isAffected(Value value) {

		ListItemType affectedType = ListSettings.getListItemType(value);
		String affectedName = ListSettings.getListItemName(value);

		if (affectedType != null) {
			return hasListItem(affectedType, affectedName);
		} else {
			return false;
		}
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

		Util.putArray(jsonObject, listItems, FIELD_ITEMS);
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

	public enum ListItemType {
		Header, Talent, Spell, Art, Attribute, EquippedItem, Modificator, Document
	}

	public static class ListItem implements JSONable, Parcelable {

		private static final String JSON_TYPE = "type";
		private static final String JSON_NAME = "name";

		private ListItemType type;
		private String name;

		public ListItem(AttributeType type) {
			this.type = ListItemType.Attribute;
			this.name = type.name();
		}

		public ListItem(ListItemType type) {
			this.type = type;
		}

		public ListItem(ListItemType type, String name) {
			this.type = type;
			this.name = name;
		}

		public ListItem(JSONObject json) {
			this.type = ListItemType.valueOf(json.optString(JSON_TYPE));
			this.name = json.optString(JSON_NAME);
		}

		public ListItem(Parcel in) {
			this.type = ListItemType.valueOf(in.readString());
			this.name = in.readString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#describeContents()
		 */
		@Override
		public int describeContents() {
			return 0;
		}

		/**
		 * Creator for the Parcelable
		 */
		public static final Parcelable.Creator<ListItem> CREATOR = new Parcelable.Creator<ListItem>() {
			@Override
			public ListItem createFromParcel(Parcel in) {
				return new ListItem(in);
			}

			@Override
			public ListItem[] newArray(int size) {
				return new ListItem[size];
			}
		};

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
		 */
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(type.name());
			dest.writeString(name);
		}

		@Override
		public JSONObject toJSONObject() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(JSON_TYPE, type.name());
			json.put(JSON_NAME, name);
			return json;
		}

		public ListItemType getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public void setType(ListItemType type) {
			this.type = type;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static ListItemType getListItemType(Object o) {
		if (o instanceof Attribute)
			return ListItemType.Attribute;
		else if (o instanceof Art)
			return ListItemType.Art;
		else if (o instanceof Spell) {
			return ListItemType.Spell;
		} else if (o instanceof Talent) {
			return ListItemType.Talent;
		} else if (o instanceof EquippedItem) {
			return ListItemType.EquippedItem;
		} else if (o instanceof Modificator) {
			return ListItemType.Modificator;
		} else if (o instanceof FileListable) {
			return ListItemType.Document;
		} else {
			return null;
		}

	}

	public static String getListItemName(Object o) {
		if (o instanceof Attribute)
			return ((Attribute) o).getType().name();
		else if (o instanceof Art)
			return ((Art) o).getName();
		else if (o instanceof Spell) {
			return ((Spell) o).getName();
		} else if (o instanceof Talent) {
			return ((Talent) o).getName();
		} else if (o instanceof EquippedItem) {
			return ((EquippedItem) o).getName();
		} else if (o instanceof Modificator) {
			return ((Modificator) o).getModificatorName();
		} else if (o instanceof FileListable) {
			return ((FileListable) o).getFile().getName();
		} else {
			return null;
		}

	}

	public boolean hasListItem(ListItemType type, String name) {
		for (ListItem listItem : listItems) {
			if (listItem.getType() == type && (listItem.getName() == null || listItem.getName().equals(name)))
				return true;
		}
		return false;
	}

	public boolean hasListItem(ListItemType type) {
		for (ListItem listItem : listItems) {
			if (listItem.getType() == type)
				return true;
		}
		return false;
	}

}
