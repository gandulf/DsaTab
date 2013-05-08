package com.dsatab.data;

import com.dsatab.data.modifier.RulesModificator.ModificatorType;

public interface Probe {

	public enum ProbeType {
		ThreeOfThree, TwoOfThree, One
	};

	public ProbeType getProbeType();

	public ModificatorType getModificatorType();

	public String getName();

	public Integer getValue();

	public ProbeInfo getProbeInfo();

	public Integer getProbeValue(int i);

	/**
	 * Returns the value that can be used to counter negative dice rolls
	 * (talent, spell value)
	 * 
	 * @return Integer
	 */
	public Integer getProbeBonus();

	public int getModCache();

	public void setModCache(int cacheValue);

	public void clearCache();

}
