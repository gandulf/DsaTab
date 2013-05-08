/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.data;

import android.os.SystemClock;

/**
 * @author Ganymede
 * 
 */
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

	public ProbeInfo getProbeInfo() {
		return probeInfo;
	}

	public int getModCache() {
		if (cacheDate > cacheValidationDate)
			return modCache;
		else
			return Integer.MIN_VALUE;
	}

	public void setModCache(int cacheValue) {
		this.modCache = cacheValue;
		this.cacheDate = SystemClock.uptimeMillis();
	}

	public void clearCache() {
		this.modCache = Integer.MIN_VALUE;
	}

}
