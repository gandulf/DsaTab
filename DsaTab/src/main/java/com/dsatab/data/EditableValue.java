package com.dsatab.data;

public class EditableValue implements Value {

	protected String name;

	protected Hero hero;

	protected Integer minimum, maximum, value;

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

		if (changed && hero != null)
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
