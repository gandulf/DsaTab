package com.dsatab.data.enums;

import junit.framework.Assert;
import android.test.AndroidTestCase;

public class FeatureTypeTest extends AndroidTestCase {

	public FeatureTypeTest() {
		super();
	}

	public void testTypeWithValue() {
		Assert.assertEquals(FeatureType.RüstungsgewöhnungI, FeatureType.byXmlName("Rüstungsgewöhnung I (Kettenmantel)"));
		Assert.assertEquals(FeatureType.Kulturkunde, FeatureType.byXmlName("Kulturkunde (Tulamidenlande)"));
		Assert.assertEquals(FeatureType.GöttergeschenkSchlange, FeatureType.byXmlName("Göttergeschenk: Schlange"));

	}

}
