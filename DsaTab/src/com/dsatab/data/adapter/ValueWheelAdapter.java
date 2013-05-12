package com.dsatab.data.adapter;

import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.content.Context;

import com.dsatab.R;
import com.dsatab.data.Attribute;
import com.dsatab.data.modifier.AuModificator;
import com.dsatab.data.modifier.LeModificator;
import com.gandulf.guilib.util.MathUtil;

public class ValueWheelAdapter extends NumericWheelAdapter {

	private Attribute attr;

	public ValueWheelAdapter(Context context) {
		super(context);

		setTextColor(STATE_NEGATIVE_VALUE, context.getResources().getColor(R.color.ValueRed));
		setTextColor(STATE_DEFAULT_VALUE, context.getResources().getColor(android.R.color.black));
		setTextColor(STATE_POSITIVE_VALUE, context.getResources().getColor(R.color.ValueGreen));
	}

	public ValueWheelAdapter(Context context, Attribute value) {
		super(context, value.getMinimum(), value.getMaximum());
		this.attr = value;

		setTextColor(STATE_NEGATIVE_VALUE, context.getResources().getColor(R.color.ValueRed));
		setTextColor(STATE_DEFAULT_VALUE, context.getResources().getColor(android.R.color.black));
		setTextColor(STATE_POSITIVE_VALUE, context.getResources().getColor(R.color.ValueGreen));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kankan.wheel.widget.adapters.NumericWheelAdapter#getStateForValue(int)
	 */
	@Override
	protected int getStateForValue(int index) {
		if (attr != null) {
			int value = getItem(index);
			float ratio = MathUtil.getRatio(value, attr.getReferenceValue());

			switch (attr.getType()) {
			case Lebensenergie_Aktuell:
			case Lebensenergie:
				if (ratio < LeModificator.LEVEL_2) {
					return STATE_NEGATIVE_VALUE;
				} else if (ratio < LeModificator.LEVEL_1) {
					return STATE_DEFAULT_VALUE;
				} else {
					return STATE_POSITIVE_VALUE;
				}

			case Ausdauer_Aktuell:
			case Ausdauer:
				if (ratio < AuModificator.LEVEL_2) {
					return STATE_NEGATIVE_VALUE;
				} else if (ratio < AuModificator.LEVEL_1) {
					return STATE_DEFAULT_VALUE;
				} else {
					return STATE_POSITIVE_VALUE;
				}
			default:
				break;
			}
		}
		return super.getStateForValue(index);
	}

	/**
	 * @param value
	 */
	public void setAttribute(Attribute value) {
		this.attr = value;

		setRange(value.getMinimum(), value.getMaximum());

	}

}
