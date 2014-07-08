package com.dsatab.db;

import junit.framework.Assert;
import android.test.AndroidTestCase;

public class DataManagerTest extends AndroidTestCase {

	public DataManagerTest() {
	}

	public void testGetArtByName() {
		Assert.assertNotNull(DataManager.getArtLikeName("Lied des Trostes"));
		Assert.assertNotNull(DataManager.getArtLikeName("Haut des Odûn"));
	}

}
