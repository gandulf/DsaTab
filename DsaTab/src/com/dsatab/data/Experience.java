package com.dsatab.data;

public class Experience extends EditableValue {

	/**
	 * 
	 */
	public Experience(Hero hero, String name) {
		super(hero, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#setValue(java.lang.Integer)
	 */
	@Override
	public void setValue(Integer value) {
		Integer oldValue = getValue();

		super.setValue(value);

		int added = 0;
		if (oldValue != null && value != null)
			added = value - oldValue;
		else if (value != null)
			added = value;
		else if (oldValue != null)
			added = -oldValue;

		hero.getFreeExperience().addValue(added);
	}

}
