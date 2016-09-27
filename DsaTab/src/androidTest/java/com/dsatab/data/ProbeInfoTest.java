package com.dsatab.data;

import android.support.test.runner.AndroidJUnit4;

import com.dsatab.data.enums.AttributeType;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProbeInfoTest extends TestCase{

    @Test
	public void testProbeInfoParsing() {

		ProbeInfo probeInfo = new ProbeInfo();
		probeInfo.applyProbePattern("MU/9");

        assertEquals(AttributeType.Mut, probeInfo.getAttributeValues().get(0));
        assertEquals(9,probeInfo.getAttributeValues().get(1));
	}

}
