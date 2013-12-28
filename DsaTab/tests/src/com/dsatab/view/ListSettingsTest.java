package com.dsatab.view;

import android.os.Parcel;
import android.test.AndroidTestCase;

import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;

public class ListSettingsTest extends AndroidTestCase {

	public void testParcelable() {
		ListSettings settings = new ListSettings();
		settings.setShowFavorite(true);
		settings.addListItem(new ListItem(ListItemType.Attribute));

		Parcel parcel = Parcel.obtain();

		settings.writeToParcel(parcel, 0);

		parcel.setDataPosition(0);

		ListSettings newSettings = new ListSettings(parcel);

		assertEquals(settings.isShowFavorite(), newSettings.isShowFavorite());
		assertEquals(settings.getListItems().size(), newSettings.getListItems().size());
	}

}
