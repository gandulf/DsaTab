package com.dsatab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.dsatab.data.JSONable;
import com.dsatab.fragment.AnimalFragment;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.fragment.BaseListFragment;
import com.dsatab.fragment.CharacterFragment;
import com.dsatab.fragment.ItemsFragment;
import com.dsatab.fragment.ListableFragment;
import com.dsatab.fragment.MapFragment;
import com.dsatab.util.Util;
import com.dsatab.view.ListSettings;
import com.gandulf.guilib.util.Debug;

public class TabInfo implements Parcelable, JSONable, Cloneable {

	public static final int MAX_TABS_PER_PAGE = 2;

	private static final String FIELD_ACTIVITY_CLAZZ = "activityClazz";
	private static final String FIELD_TAB_ICON_URI = "iconUri";
	private static final String FIELD_PRIMARY_ACTIVITY_CLAZZ = "activityClazz1";
	private static final String FIELD_SECONDARY_ACTIVITY_CLAZZ = "activityClazz2";
	private static final String FIELD_DICE_SLIDER = "diceSlider";
	private static final String FIELD_FILTER_SETTINGS = "filterSettings";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_ID = "id";

	@SuppressWarnings("unchecked")
	private Class<? extends BaseFragment>[] activityClazz = new Class[MAX_TABS_PER_PAGE];

	private String title;
	private boolean diceSlider = true;

	private transient UUID id;
	private Uri iconUri;
	private int color;

	private ListSettings[] listSettings;

	public TabInfo(Class<? extends BaseFragment> activityClazz1, Class<? extends BaseFragment> activityClazz2,
			int tabResourceId, boolean diceSlider, boolean attributeList) {
		super();
		this.activityClazz[0] = activityClazz1;
		this.activityClazz[1] = activityClazz2;

		this.listSettings = new ListSettings[MAX_TABS_PER_PAGE];
		this.iconUri = Util.getUriForResourceId(tabResourceId);
		this.diceSlider = diceSlider;
		this.id = UUID.randomUUID();

		updateListSettings();
	}

	public TabInfo(Class<? extends BaseFragment> activityClazz1, Class<? extends BaseFragment> activityClazz2,
			int tabResourceId) {
		this(activityClazz1, activityClazz2, tabResourceId, true, true);
	}

	public TabInfo(Class<? extends BaseFragment> activityClazz1, int tabResourceId, boolean diceSlider,
			boolean attributeList) {
		this(activityClazz1, null, tabResourceId, diceSlider, attributeList);
	}

	public TabInfo(Class<? extends BaseFragment> activityClazz1, int tabResourceId) {
		this(activityClazz1, null, tabResourceId, true, true);
	}

	public TabInfo() {
		this(null, null, -1, true, true);
	}

	/**
	 * 
	 */
	public TabInfo(Parcel in) {
		this.activityClazz = (Class<? extends BaseFragment>[]) in.readSerializable();
		String uriString = in.readString();
		if (uriString != null) {
			this.iconUri = Uri.parse(uriString);
		}
		this.diceSlider = in.readInt() == 0 ? false : true;
		this.id = UUID.fromString(in.readString());

		List<ListSettings> list = new ArrayList<ListSettings>(MAX_TABS_PER_PAGE);
		in.readTypedList(list, ListSettings.CREATOR);
		this.listSettings = new ListSettings[list.size()];
		this.listSettings = list.toArray(listSettings);

		this.title = in.readString();
		updateListSettings();
	}

	/**
	 * JSONObject constructor
	 * 
	 * @param in
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public TabInfo(JSONObject in) throws JSONException, ClassNotFoundException {

		if (in.has(FIELD_TAB_ICON_URI)) {
			iconUri = Uri.parse(in.getString(FIELD_TAB_ICON_URI));
		}

		if (in.has(FIELD_ID))
			this.id = UUID.fromString(in.optString(FIELD_ID));
		else
			this.id = UUID.randomUUID();

		if (in.has(FIELD_DICE_SLIDER))
			diceSlider = in.getBoolean(FIELD_DICE_SLIDER);

		// old delegate version
		if (!in.isNull(FIELD_PRIMARY_ACTIVITY_CLAZZ)) {
			String className = compatibleFragmentName(in.getString(FIELD_PRIMARY_ACTIVITY_CLAZZ));

			activityClazz[0] = (Class<? extends BaseFragment>) Class.forName(className, true,
					BaseFragment.class.getClassLoader());
		}
		// old delegate version
		if (!in.isNull(FIELD_SECONDARY_ACTIVITY_CLAZZ)) {
			String className = compatibleFragmentName(in.getString(FIELD_SECONDARY_ACTIVITY_CLAZZ));

			activityClazz[1] = (Class<? extends BaseFragment>) Class.forName(className, true,
					BaseFragment.class.getClassLoader());
		}

		if (!in.isNull(FIELD_ACTIVITY_CLAZZ)) {

			JSONArray jsonArray = in.getJSONArray(FIELD_ACTIVITY_CLAZZ);
			activityClazz = new Class[MAX_TABS_PER_PAGE];
			for (int i = 0; i < jsonArray.length(); i++) {
				if (!jsonArray.isNull(i)) {
					String className = compatibleFragmentName(jsonArray.getString(i));

					activityClazz[i] = (Class<? extends BaseFragment>) Class.forName(className, true,
							BaseFragment.class.getClassLoader());
				}
			}
		}

		this.id = UUID.randomUUID();

		listSettings = new ListSettings[MAX_TABS_PER_PAGE];
		if (!in.isNull(FIELD_FILTER_SETTINGS)) {
			JSONArray jsonArray = in.getJSONArray(FIELD_FILTER_SETTINGS);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject filterJson = jsonArray.optJSONObject(i);

				if (filterJson != null) {
					if (filterJson.has(ListSettings.class.getName())) {
						listSettings[i] = new ListSettings(filterJson.getJSONObject(ListSettings.class.getName()));
					}
				}
			}
		}

		if (in.has(FIELD_TITLE)) {
			title = in.optString(FIELD_TITLE);
		}

		updateListSettings();
	}

	private String compatibleFragmentName(String className) {
		if (className == null)
			return null;

		if ("com.dsatab.fragment.LiturgieFragment".equals(className)
				|| "com.dsatab.fragment.ArtFragment".equals(className)) {
			className = ListableFragment.class.getName();
		}

		if ("com.dsatab.fragment.SpellFragment".equals(className)) {
			className = ListableFragment.class.getName();
		}

		if ("com.dsatab.fragment.TalentFragment".equals(className)) {
			className = ListableFragment.class.getName();
		}

		if ("com.dsatab.fragment.NotesFragment".equals(className)) {
			className = ListableFragment.class.getName();
		}

		if ("com.dsatab.fragment.DocumentsFragment".equals(className)) {
			className = ListableFragment.class.getName();
		}

		if ("com.dsatab.fragment.ItemsListFragment".equals(className)) {
			className = ItemsFragment.class.getName();
		}

		if ("com.dsatab.fragment.PurseFragment".equals(className)) {
			className = ItemsFragment.class.getName();
		}

		return className;
	}

	public UUID getId() {
		return id;
	}

	public Class<? extends BaseFragment> getActivityClazz(int pos) {
		return activityClazz[pos];
	}

	public Class<? extends BaseFragment>[] getActivityClazzes() {
		return activityClazz;
	}

	public void setActivityClazz(int pos, Class<? extends BaseFragment> activityClazz) {
		this.activityClazz[pos] = activityClazz;
		updateListSettings(pos);
	}

	public Uri getIconUri() {
		return iconUri;
	}

	public int getColor() {
		// color = Color.RED;
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getTitle() {
		if (title == null) {
			StringBuilder title = new StringBuilder();

			for (Class<? extends BaseFragment> clazz : getActivityClazzes()) {
				if (clazz != null) {
					if (title.length() > 0)
						title.append("/");

					title.append(BaseFragment.getFragmentTitle(clazz));
				}
			}
			return title.toString();
		} else {
			return title;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIconUri(Uri uri) {
		this.iconUri = uri;
	}

	public boolean isDiceSlider() {
		return diceSlider;
	}

	public boolean isActionbarTranslucent() {
		boolean translucent = true;

		for (Class clazz : activityClazz) {
			translucent &= clazz == null || CharacterFragment.class.isAssignableFrom(clazz)
					|| AnimalFragment.class.isAssignableFrom(clazz) || MapFragment.class.isAssignableFrom(clazz);
		}
		return translucent;
	}

	public boolean isEmpty() {
		boolean empty = true;
		for (Class<?> clazz : activityClazz) {
			empty &= clazz == null;
		}
		return empty;
	}

	public void setDiceSlider(boolean diceSlider) {
		this.diceSlider = diceSlider;
	}

	public int getTabCount() {
		int count = 0;
		for (int i = 0; i < activityClazz.length; i++) {
			if (activityClazz[i] != null)
				count++;
		}

		return count;

	}

	private void updateListSettings() {
		for (int i = 0; i < activityClazz.length; i++) {
			updateListSettings(i);
		}
	}

	private void updateListSettings(int i) {
		if (activityClazz[i] != null) {
			if (BaseListFragment.class.isAssignableFrom(activityClazz[i])) {
				if (!(listSettings[i] instanceof ListSettings)) {
					listSettings[i] = new ListSettings();
				}
			} else {
				listSettings[i] = null;
			}
		} else {
			listSettings[i] = null;
		}
	}

	public ListSettings getListSettings(int pos) {
		if (listSettings[pos] == null) {
			updateListSettings();
		}
		return listSettings[pos];
	}

	public ListSettings[] getListSettings() {
		return listSettings;
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
	public static final Parcelable.Creator<TabInfo> CREATOR = new Parcelable.Creator<TabInfo>() {
		@Override
		public TabInfo createFromParcel(Parcel in) {
			return new TabInfo(in);
		}

		@Override
		public TabInfo[] newArray(int size) {
			return new TabInfo[size];
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(activityClazz);
		if (iconUri != null)
			dest.writeString(iconUri.toString());
		else
			dest.writeString(null);
		dest.writeInt(diceSlider ? 1 : 0);
		dest.writeString(id.toString());
		dest.writeTypedList(Arrays.asList(listSettings));
		dest.writeString(getTitle());
	}

	/**
	 * Constructs a json object with the current data
	 * 
	 * @return
	 * @throws JSONException
	 */
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject out = new JSONObject();

		if (activityClazz != null) {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < activityClazz.length; i++) {
				if (activityClazz[i] != null) {
					jsonArray.put(i, activityClazz[i].getName());
				}
			}
			out.put(FIELD_ACTIVITY_CLAZZ, jsonArray);
		}
		if (iconUri != null) {
			out.put(FIELD_TAB_ICON_URI, iconUri.toString());
		}

		out.put(FIELD_ID, id.toString());

		out.put(FIELD_DICE_SLIDER, diceSlider);

		if (listSettings != null) {

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < listSettings.length; i++) {
				if (listSettings[i] != null) {
					JSONObject json = new JSONObject();
					json.put(listSettings[i].getClass().getName(), listSettings[i].toJSONObject());
					jsonArray.put(i, json);
				}
			}

			out.put(FIELD_FILTER_SETTINGS, jsonArray);
		}

		out.put(FIELD_TITLE, getTitle());
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TabInfo :" + activityClazz;
	}

	/*
	 * @Override public boolean equals(Object o) { if (o == null) return false; if (o == this) return true; if (!(o
	 * instanceof TabInfo)) return false;
	 * 
	 * TabInfo tabinfo = (TabInfo) o;
	 * 
	 * return tabinfo.id.equals(id); }
	 * 
	 * @Override public int hashCode() { return id.hashCode(); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public TabInfo clone() {
		try {
			return (TabInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			Debug.error(e);
			return null;
		}
	}

}
