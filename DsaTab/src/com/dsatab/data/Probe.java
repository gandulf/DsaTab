package com.dsatab.data;

import com.dsatab.data.modifier.RulesModificator.ModificatorType;

public interface Probe {

	public enum ProbeType {
		/**
		 * The probe consists of three dices which all are taken into consideration for the probe
		 */
		ThreeOfThree,
		/**
		 * The probe consists of three dices but only the best two are taken into account
		 */
		TwoOfThree,
		/**
		 * The probe consists of one dice which counts
		 */
		One
	}

	public ProbeType getProbeType();

	public ModificatorType getModificatorType();

	public String getName();

	public Integer getValue();

	public ProbeInfo getProbeInfo();

	public Integer getProbeValue(int i);

	/**
	 * Returns the value that can be used to counter negative dice rolls (talent, spell value)
	 * 
	 * @return Integer
	 */
	public Integer getProbeBonus();

	public int getModifierCache();

	public void setModifierCache(int cacheValue);

	public void clearModifierCache();

}
