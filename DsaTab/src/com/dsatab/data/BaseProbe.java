package com.dsatab.data;

import android.os.SystemClock;

import com.dsatab.data.enums.AttributeType;

public abstract class BaseProbe implements Probe {

	private static final long serialVersionUID = 7786696276206361948L;

	public static long cacheValidationDate = 0;

	protected ProbeInfo probeInfo;

	private int modCache = Integer.MIN_VALUE;
	private long cacheDate = 0;

	protected transient AbstractBeing being;

	public BaseProbe(AbstractBeing being) {
		this.being = being;
		this.probeInfo = new ProbeInfo();
	}

	@Override
	public ProbeInfo getProbeInfo() {
		return probeInfo;
	}

	@Override
	public Integer getProbeValue(int i) {
		if (being != null) {

			if (!probeInfo.getAttributeValues().isEmpty()) {
				Object o = null;
				if (i < probeInfo.getAttributeValues().size()) {
					o = probeInfo.getAttributeValues().get(i);
				}
				if (o instanceof AttributeType) {
					AttributeType type = (AttributeType) o;
					return being.getModifiedValue(type, false, false);
				} else if (o instanceof Number) {
					return ((Number) o).intValue();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public int getModifierCache() {
		if (cacheDate > cacheValidationDate)
			return modCache;
		else
			return Integer.MIN_VALUE;
	}

	@Override
	public void setModifierCache(int cacheValue) {
		this.modCache = cacheValue;
		this.cacheDate = SystemClock.uptimeMillis();
	}

	@Override
	public void clearModifierCache() {
		this.modCache = Integer.MIN_VALUE;
	}

}
