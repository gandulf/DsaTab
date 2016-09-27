package com.dsatab.util;

import android.widget.NumberPicker;

public class NumberPickerUtils {

	private NumberPicker numberPicker;

	private int minValue;

	public NumberPickerUtils(NumberPicker numberPicker) {
		this.numberPicker = numberPicker;
		this.numberPicker.setFormatter(new NumberPicker.Formatter() {
			@Override
			public String format(int index) {
				return Integer.toString(index + minValue);
			}
		});
	}

	public void setMinValue(int value) {
		if (value < 0) {
			minValue = value;
			numberPicker.setMinValue(0);
		} else {
			numberPicker.setMinValue(value);
		}
	}

	public void setMaxValue(int value) {
		numberPicker.setMaxValue(value - minValue);
	}

	public void setValue(int value) {
		numberPicker.setValue(value - minValue);
	}

	public int getValue() {
		return numberPicker.getValue() + minValue;
	}

}
