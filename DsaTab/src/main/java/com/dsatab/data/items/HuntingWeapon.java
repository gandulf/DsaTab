package com.dsatab.data.items;

public class HuntingWeapon {

	private int number;
	private int set;

	/**
	 * 
	 */
	public HuntingWeapon(int set, int number) {
		this.set = set;
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSet() {
		return set;
	}

	public void setSet(int set) {
		this.set = set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " number=" + number + " set=" + set;
	}

}
