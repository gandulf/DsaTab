package com.dsatab.data;

import java.io.Serializable;

public class ItemLocationInfo implements Serializable, Cloneable {

	private static final long serialVersionUID = -7504593992133518605L;

	public static final int INVALID_POSITION = -1;

	private int screen = INVALID_POSITION;

	/**
	 * Indicates the position of the associated cell.
	 */
	private int cellNumber = INVALID_POSITION;

	/**
	 * 
	 */
	public ItemLocationInfo() {

	}

	public int getScreen() {
		return screen;
	}

	public void setScreen(int screen) {
		this.screen = screen;
	}

	public int getCellNumber() {
		return cellNumber;
	}

	public void setCellNumber(int cellNumber) {
		this.cellNumber = cellNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return cellNumber + " on " + screen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (!o.getClass().equals(this.getClass()))
			return false;

		ItemLocationInfo i = (ItemLocationInfo) o;

		return i.cellNumber == cellNumber && i.screen == screen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
