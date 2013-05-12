
package com.dsatab.test;

import android.test.AndroidTestCase;

import com.dsatab.data.Feature;
import com.dsatab.data.enums.FeatureType;

public class AdvantageTest extends AndroidTestCase {

	public AdvantageTest() {
		super();
	}

	// Advantages without a value consist of only the name
	public void testToStringWithNoValue() {
		Feature advantage = new Feature(FeatureType.Beidh�ndig);
		assertEquals(FeatureType.Beidh�ndig.xmlName(), advantage.toString());
	}

	// Advantages with exactly one value consist of the name and the value
	public void testToStringWithOneValue() {
		Feature advantage = new Feature(FeatureType.Neugier);
		advantage.addValue("7");
		assertEquals("Neugier (7)", advantage.toString());
	}

	// Multiple values in advantages are shown in brackets
	public void testToStringWithMultipleValues() {
		Feature advantage = new Feature(FeatureType.HerausragenderSinn);
		advantage.addValue("Geh�r");
		advantage.addValue("Geruch");
		assertEquals("Herausragender Sinn (Geh�r, Geruch)", advantage.toString());
	}

	// Empty values will be omitted
	public void testToStringWithEmptyValue() {
		Feature advantage = new Feature(FeatureType.Beidh�ndig);
		advantage.addValue("");
		assertEquals(FeatureType.Beidh�ndig.xmlName(), advantage.toString());
	}
}
