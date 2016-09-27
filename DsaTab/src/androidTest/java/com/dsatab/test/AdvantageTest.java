package com.dsatab.test;

import android.support.test.runner.AndroidJUnit4;

import com.dsatab.data.Feature;
import com.dsatab.data.enums.FeatureType;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdvantageTest extends TestCase {



	// Advantages without a value consist of only the name
    @Test
	public void testToStringWithNoValue() {
		Feature advantage = new Feature(FeatureType.Beidhändig);
		assertEquals(FeatureType.Beidhändig.xmlName(), advantage.toString());
	}

	// Advantages with exactly one value consist of the name and the value
    @Test
	public void testToStringWithOneValue() {
		Feature advantage = new Feature(FeatureType.Neugier);
		advantage.addValue("7");
		assertEquals("Neugier (7)", advantage.toString());
	}

	// Multiple values in advantages are shown in brackets
    @Test
	public void testToStringWithMultipleValues() {
		Feature advantage = new Feature(FeatureType.HerausragenderSinn);
		advantage.addValue("Gehör");
		advantage.addValue("Geruch");
		assertEquals("Herausragender Sinn (Gehör, Geruch)", advantage.toString());
	}

	// Empty values will be omitted
    @Test
	public void testToStringWithEmptyValue() {
		Feature advantage = new Feature(FeatureType.Beidhändig);
		advantage.addValue("");
		assertEquals(FeatureType.Beidhändig.xmlName(), advantage.toString());
	}
}
