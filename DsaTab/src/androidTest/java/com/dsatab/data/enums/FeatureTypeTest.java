package com.dsatab.data.enums;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FeatureTypeTest extends TestCase{

    @Test
	public void testTypeWithValue() {
		Assert.assertEquals(FeatureType.RüstungsgewöhnungI, FeatureType.byXmlName("Rüstungsgewöhnung I (Kettenmantel)"));
		Assert.assertEquals(FeatureType.Kulturkunde, FeatureType.byXmlName("Kulturkunde (Tulamidenlande)"));
		Assert.assertEquals(FeatureType.GöttergeschenkSchlange, FeatureType.byXmlName("Göttergeschenk: Schlange"));

	}

}
