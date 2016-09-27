package com.dsatab.test;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.dsatab.config.TabInfo;
import com.dsatab.fragment.ListableFragment;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TabInfoTest extends TestCase {

    @Test
	public void testParcelable() {
		TabInfo tabInfo = new TabInfo();
		tabInfo.setTitle("test");
		tabInfo.setActivityClazz(0, ListableFragment.class);
		ListSettings settings = new ListSettings();
		settings.setShowFavorite(true);
		settings.addListItem(new ListItem(ListItemType.Attribute));

		tabInfo.getListSettings()[0] = settings;
		Parcel parcel = Parcel.obtain();

		tabInfo.writeToParcel(parcel, 0);

		parcel.setDataPosition(0);

		TabInfo newTabInfo = new TabInfo(parcel);
		ListSettings newSettings = newTabInfo.getListSettings()[0];

		assertEquals("test", tabInfo.getTitle());
		assertEquals(settings.isShowFavorite(), newSettings.isShowFavorite());
		assertEquals(settings.getListItems().size(), newSettings.getListItems().size());
	}

}
