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

import org.json.JSONException;
import org.json.JSONObject;

import com.dsatab.data.enums.Position;

public class ArmorAttribute implements Value, JSONable {

	private static final String FIELD_POSITION = "position";
	private static final String FIELD_VALUE = "value";
	private static final String FIELD_MANUAL = "manual";

	private Hero hero;

	private Position position;

	private int value;

	private boolean manual = false;

	public ArmorAttribute(Hero hero, Position position) {
		this.hero = hero;
		this.position = position;
	}

	public ArmorAttribute(Hero hero, JSONObject json) throws JSONException {
		this.hero = hero;

		this.position = Position.valueOf(json.getString(FIELD_POSITION));
		this.value = json.getInt(FIELD_VALUE);
		this.manual = json.getBoolean(FIELD_MANUAL);
	}

	public String getName() {
		return position.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.Value#reset()
	 */
	@Override
	public void reset() {

		int newValue = getReferenceValue();

		if (manual) {
			manual = false;

			// still fire a value changed since the isManual value has changed
			if (newValue == getValue()) {
				hero.fireValueChangedEvent(this);
			}
		}
		setValue(newValue);
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

	public Integer getValue() {
		return value;
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

	public void setValue(Integer value) {
		int oldValue = this.value;

		if (value == null)
			this.value = 0;
		else
			this.value = value;

		if (oldValue != this.value)
			hero.fireValueChangedEvent(this);
	}

	public Integer getReferenceValue() {
		return hero.getArmorRs(getPosition());
	}

	public int getMinimum() {
		return 0;
	}

	public int getMaximum() {
		return 20;
	}

	/**
	 * Constructs a json object with the current data
	 * 
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSONObject() throws JSONException {
		JSONObject out = new JSONObject();

		out.put(FIELD_POSITION, position.name());
		out.put(FIELD_VALUE, value);
		out.put(FIELD_MANUAL, manual);

		return out;
	}

}
