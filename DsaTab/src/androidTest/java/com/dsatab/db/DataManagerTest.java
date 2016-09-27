package com.dsatab.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DataManagerTest extends InstrumentationTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Injecting the Instrumentation instance is required for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }
    @Test
	public void testGetArtByName() {
		Assert.assertNotNull(DataManager.getArtLikeName("Lied des Trostes"));
		Assert.assertNotNull(DataManager.getArtLikeName("Haut des Odûn"));
	}

}
