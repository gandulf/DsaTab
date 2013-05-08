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


/**
 * 
 * 
 */
public class EditableValue implements Value {

	private String name;

	protected Hero hero;

	private Integer minimum, maximum, value;

	public EditableValue(Hero hero, String name) {
		this.hero = hero;

		this.name = name;

		this.minimum = 0;
		this.maximum = Integer.MAX_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#getValue()
	 */
	@Override
	public Integer getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#setValue(java.lang.Integer)
	 */
	@Override
	public void setValue(Integer value) {

		boolean changed = getValue() == null || !getValue().equals(value);

		if (value != null) {
			if (minimum != null && value < minimum)
				value = minimum;

			if (maximum != null && value > maximum)
				value = maximum;

			this.value = value;
		} else
			this.value = null;

		if (changed)
			hero.fireValueChangedEvent(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#reset()
	 */
	@Override
	public void reset() {
		setValue(getReferenceValue());
	}

	public void addValue(Integer value) {

		if (getValue() != null && value != null)
			setValue(getValue() + value);
		else
			setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#getMinimum()
	 */
	@Override
	public int getMinimum() {
		return minimum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#getMaximum()
	 */
	@Override
	public int getMaximum() {
		return maximum;
	}

	public void setMinimum(Integer minimum) {
		this.minimum = minimum;
	}

	public void setMaximum(Integer maximum) {
		this.maximum = maximum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#getReferenceValue()
	 */
	@Override
	public Integer getReferenceValue() {
		return null;
	}

}
