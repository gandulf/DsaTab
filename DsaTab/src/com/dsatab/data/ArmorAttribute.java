package com.dsatab.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsatab.data.enums.Position;

public class ArmorAttribute extends EditableValue implements JSONable {

	private static final String FIELD_POSITION = "position";
	private static final String FIELD_VALUE = "value";
	private static final String FIELD_MANUAL = "manual";

	private Position position;

	private boolean manual = false;

	public ArmorAttribute(Hero hero, Position position) {
		super(hero, position.getName());
		this.position = position;
		this.minimum = 0;
		this.maximum = 20;

	}

	public ArmorAttribute(Hero hero, JSONObject json) throws JSONException {
		this(hero, Position.valueOf(json.getString(FIELD_POSITION)));

		manual = json.getBoolean(FIELD_MANUAL);
		value = json.getInt(FIELD_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#reset()
	 */
	@Override
	public void reset() {

		int refValue = getReferenceValue();

		if (manual) {
			manual = false;

			// still fire a value changed since the isManual value has changed
			if (refValue == getValue()) {
				hero.fireValueChangedEvent(this);
			}
		}
		setValue(refValue);
	}

	public void recalcValue() {
		if (manual)
			return;
		else
			setValue(getReferenceValue());
	}

	public Position getPosition() {
		return position;
	}

	public boolean isManual() {
		return manual;
	}

	public void setManual(boolean manual) {
		this.manual = manual;
	}

	public void setValue(Integer value, boolean manual) {

		if (manual) {

			if (this.manual == false) {
				this.manual = true;

				// still fire a value changed since the isManual value has
				// changed
				if (value == getValue()) {
					hero.fireValueChangedEvent(this);
				}
			}
		}
		setValue(value);
	}

	@Override
	public Integer getReferenceValue() {
		return hero.getArmorRs(getPosition());
	}

	/**
	 * Constructs a json object with the current data
	 * 
	 * @return
	 * @throws JSONException
	 */
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject out = new JSONObject();

		out.put(FIELD_POSITION, position.name());
		out.put(FIELD_VALUE, value);
		out.put(FIELD_MANUAL, manual);

		return out;
	}

}
