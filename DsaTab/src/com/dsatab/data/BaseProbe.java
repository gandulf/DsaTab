package com.dsatab.data;

import android.os.SystemClock;

public abstract class BaseProbe implements Probe {

	public static long cacheValidationDate = 0;

	protected ProbeInfo probeInfo;

	protected int modCache = Integer.MIN_VALUE;
	protected long cacheDate = 0;

	/**
	 * 
	 */
	public BaseProbe() {
		probeInfo = new ProbeInfo();
	}

	@Override
	public ProbeInfo getProbeInfo() {
		return probeInfo;
	}

	@Override
	public int getModCache() {
		if (cacheDate > cacheValidationDate)
			return modCache;
		else
			return Integer.MIN_VALUE;
	}

	@Override
	public void setModCache(int cacheValue) {
		this.modCache = cacheValue;
		this.cacheDate = SystemClock.uptimeMillis();
	}

	@Override
	public void clearCache() {
		this.modCache = Integer.MIN_VALUE;
	}

}
