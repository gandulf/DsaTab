package com.dsatab.data;

import android.test.AndroidTestCase;

public class ProbeInfoTest extends AndroidTestCase {

	public void testProbeInfoParsing() {

		ProbeInfo probeInfo = new ProbeInfo();
		probeInfo.applyProbePattern("MU/9");
	}

}
