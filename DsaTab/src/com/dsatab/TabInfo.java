package com.dsatab;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.dsatab.data.JSONable;
import com.dsatab.fragment.ArtFragment;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.fragment.BaseListFragment;
import com.dsatab.fragment.FightFragment;
import com.dsatab.util.Util;
import com.dsatab.view.FightFilterSettings;
import com.dsatab.view.FilterSettings;
import com.dsatab.view.ListFilterSettings;

public class TabInfo implements Parcelable, JSONable, Cloneable {

	public static final int MAX_TABS_PER_PAGE = 2;

	private static final String FIELD_ACTIVITY_CLAZZ = "activityClazz";
	private static final String FIELD_TAB_RESOURCE_INDEX = "tabResourceId";
	private static final String FIELD_TAB_ICON_URI = "iconUri";
	private static final String FIELD_PRIMARY_ACTIVITY_CLAZZ = "activityClazz1";
	private static final String FIELD_SECONDARY_ACTIVITY_CLAZZ = "activityClazz2";
	private static final String FIELD_DICE_SLIDER = "diceSlider";
	private static final String FIELD_ATTRIBUTE_LIST = "attributeList";
	private static final String FIELD_FILTER_SETTINGS = "filterSettings";

	@SuppressWarnings("unchecked")
	private Class<? extends BaseFragment>[] activityClazz = new Class[MAX_TABS_PER_PAGE];

	private boolean diceSlider = true;
	private boolean attributeList = true;

	private transient UUID id;
	private Uri iconUri;

	private FilterSettings[] filterSettings = new FilterSettings[MAX_TABS_PER_PAGE];

	private static final int indexToResourceId(int index) {
		if (index < 0 || index >= DsaTabApplication.getInstance().getConfiguration().getTabIcons().size())
			index = 0;

		return DsaTabApplication.getInstance().getConfiguration().getTabIcons().get(index);
	}

	private static final int resourceIdToIndex(int id) {
		int index = DsaTabApplication.getInstance().getConfiguration().getTabIcons().indexOf(id);

		if (index < 0 || index >= DsaTabApplication.getInstance().getConfiguration().getTabIcons().size())
			return 0;
		else
			return index;
	}

	public TabInfo(Class<? extends BaseFragment> activityClazz1, Class<? extends BaseFragment> activityClazz2,
			int tabResourceId, boolean diceSlider) {
		super();
		this.activityClazz[0] = activityClazz1;
		this.activityClazz[1] = activityClazz2;

		this.iconUri = Util.getUriForResourceId(tabResourceId);
		this.diceSlider = diceSlider;
		this.id = UUID.randomUUID();

		updateFilterSettings();
	}

	public TabInfo(Class<? extends BaseFragment> activityClazz1, Class<? extends BaseFragment> activityClazz2,
			int tabResourceId) {
		this(activityClazz1, activityClazz2, tabResourceId, true);
	}

	public TabInfo(Class<? extends BaseFragment> activityClazz1, int tabResourceId, boolean diceSlider) {
		this(activityClazz1, null, tabResourceId, diceSlider);
	}

	public TabInfo(Class<? extends BaseFragment> activityClazz1, int tabResourceId) {
		this(activityClazz1, null, tabResourceId, true);
	}

	public TabInfo() {
		this(null, null, -1, true);
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
		this.id = UUID.randomUUID();
		this.filterSettings = (FilterSettings[]) in.readSerializable();
		this.attributeList = in.readInt() == 0 ? false : true;
		updateFilterSettings();
	}

	/**
	 * JSONObject constructor
	 * 
	 * @param in
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public TabInfo(JSONObject in) throws JSONException, ClassNotFoundException {
		// backwardcompat for resourceindex
		if (in.has(FIELD_TAB_RESOURCE_INDEX)) {
			int tabResourceIndex = in.getInt(FIELD_TAB_RESOURCE_INDEX);
			int resourceId = indexToResourceId(tabResourceIndex);
			iconUri = Util.getUriForResourceId(resourceId);
		}
		if (in.has(FIELD_TAB_ICON_URI)) {
			iconUri = Uri.parse(in.getString(FIELD_TAB_ICON_URI));
		}

		if (in.has(FIELD_DICE_SLIDER))
			diceSlider = in.getBoolean(FIELD_DICE_SLIDER);

		// old delegate version
		if (!in.isNull(FIELD_PRIMARY_ACTIVITY_CLAZZ)) {
			String className = in.getString(FIELD_PRIMARY_ACTIVITY_CLAZZ);
			if ("com.dsatab.fragment.LiturgieFragment".equals(className)) {
				className = ArtFragment.class.getName();
			}

			activityClazz[0] = (Class<? extends BaseFragment>) Class.forName(className, true,
					BaseFragment.class.getClassLoader());
		}
		// old delegate version
		if (!in.isNull(FIELD_SECONDARY_ACTIVITY_CLAZZ)) {

			String className = in.getString(FIELD_SECONDARY_ACTIVITY_CLAZZ);
			if ("com.dsatab.fragment.LiturgieFragment".equals(className)) {
				className = ArtFragment.class.getName();
			}

			activityClazz[1] = (Class<? extends BaseFragment>) Class.forName(className, true,
					BaseFragment.class.getClassLoader());
		}

		if (!in.isNull(FIELD_ACTIVITY_CLAZZ)) {

			JSONArray jsonArray = in.getJSONArray(FIELD_ACTIVITY_CLAZZ);
			activityClazz = new Class[MAX_TABS_PER_PAGE];
			for (int i = 0; i < jsonArray.length(); i++) {
				if (!jsonArray.isNull(i)) {
					String className = jsonArray.getString(i);
					if ("com.dsatab.fragment.LiturgieFragment".equals(className)) {
						className = ArtFragment.class.getName();
					}

					activityClazz[i] = (Class<? extends BaseFragment>) Class.forName(className, true,
							BaseFragment.class.getClassLoader());
				}
			}
		}

		this.id = UUID.randomUUID();

		if (in.has(FIELD_ATTRIBUTE_LIST)) {
			attributeList = in.optBoolean(FIELD_ATTRIBUTE_LIST, true);
		}
		if (!in.isNull(FIELD_FILTER_SETTINGS)) {
			JSONArray jsonArray = in.getJSONArray(FIELD_FILTER_SETTINGS);
			filterSettings = new FilterSettings[MAX_TABS_PER_PAGE];

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject filterJson = jsonArray.optJSONObject(i);

				if (filterJson != null) {
					if (filterJson.has(ListFilterSettings.class.getName())) {
						filterSettings[i] = new ListFilterSettings(filterJson.getJSONObject(ListFilterSettings.class
								.getName()));
					} else if (filterJson.has(FightFilterSettings.class.getName())) {
						filterSettings[i] = new FightFilterSettings(filterJson.getJSONObject(FightFilterSettings.class
								.getName()));
					}
				}
			}
		}

		updateFilterSettings();
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

	public BaseFragment getFragment() throws InstantiationException, IllegalAccessException {
		BaseFragment fragment = null;
		for (int i = 0; i < activityClazz.length; i++) {
			fragment = getFragment(i);
			if (fragment != null)
				break;
		}

		return fragment;
	}

	public BaseFragment getFragment(int pos) throws InstantiationException, IllegalAccessException {
		BaseFragment fragment = null;
		if (activityClazz[pos] != null) {
			fragment = activityClazz[pos].newInstance();
			Bundle args = new Bundle();
			args.putParcelable(BaseFragment.TAB_INFO, this);
			args.putInt(BaseFragment.TAB_POSITION, pos);
			fragment.setArguments(args);
		}
		return fragment;
	}

	public void setActivityClazz(int pos, Class<? extends BaseFragment> activityClazz) {
		this.activityClazz[pos] = activityClazz;
		updateFilterSettings(pos);
	}

	public Uri getIconUri() {
		return iconUri;
	}

	public String getTitle() {
		StringBuilder title = new StringBuilder();

		for (Class<? extends BaseFragment> clazz : getActivityClazzes()) {
			if (clazz != null) {
				if (title.length() > 0)
					title.append("/");

				title.append(BaseFragment.getFragmentTitle(clazz));
			}
		}
		return title.toString();
	}

	public void setIconUri(Uri uri) {
		this.iconUri = uri;
	}

	public boolean isDiceSlider() {
		return diceSlider;
	}

	public void setDiceSlider(boolean diceSlider) {
		this.diceSlider = diceSlider;
	}

	public boolean isAttributeList() {
		return attributeList;
	}

	public void setAttributeList(boolean attributeList) {
		this.attributeList = attributeList;
	}

	public int getTabCount() {
		int count = 0;
		for (int i = 0; i < activityClazz.length; i++) {
			if (activityClazz[i] != null)
				count++;
		}

		return count;

	}

	public void updateFilterSettings() {
		for (int i = 0; i < activityClazz.length; i++) {
			updateFilterSettings(i);
		}
	}

	private void updateFilterSettings(int i) {
		if (activityClazz[i] != null) {
			if (FightFragment.class.isAssignableFrom(activityClazz[i])) {
				if (!(filterSettings[i] instanceof FightFilterSettings)) {
					filterSettings[i] = new FightFilterSettings(true, true, true, true);
				}
			} else if (BaseListFragment.class.isAssignableFrom(activityClazz[i])) {
				if (!(filterSettings[i] instanceof ListFilterSettings)) {
					filterSettings[i] = new ListFilterSettings(true, true, true, true);
				}
			} else {
				filterSettings[i] = null;
			}
		} else {
			filterSettings[i] = null;
		}
	}

	public FilterSettings getFilterSettings(int pos) {
		if (filterSettings[pos] == null) {
			updateFilterSettings();
		}
		return filterSettings[pos];
	}

	public FilterSettings getFilterSettings(BaseFragment baseFragment) {
		for (int i = 0; i < activityClazz.length; i++) {
			if (activityClazz[i] == baseFragment.getClass()) {
				return getFilterSettings(i);
			}
		}
		return null;
	}

	public FilterSettings[] getFilterSettings() {
		return filterSettings;
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
		dest.writeSerializable(filterSettings);
		dest.writeInt(attributeList ? 1 : 0);
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

		out.put(FIELD_DICE_SLIDER, diceSlider);

		out.put(FIELD_ATTRIBUTE_LIST, attributeList);

		if (filterSettings != null) {

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < filterSettings.length; i++) {
				if (filterSettings[i] != null) {
					JSONObject json = new JSONObject();
					json.put(filterSettings[i].getClass().getName(), filterSettings[i].toJSONObject());
					jsonArray.put(i, json);
				}
			}

			out.put(FIELD_FILTER_SETTINGS, jsonArray);
		}
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
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public TabInfo clone() {
		try {
			return (TabInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
