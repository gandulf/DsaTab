package com.dsatab.data;

import com.dsatab.data.enums.Position;
import com.dsatab.data.modifier.WoundModificator;

import org.json.JSONException;
import org.json.JSONObject;

public class WoundAttribute extends EditableValue implements JSONable {

	private static final String FIELD_POSITION = "position";
	private static final String FIELD_VALUE = "value";
	private static final String FIELD_ACTIVE = "active";

	private Position position;

	private WoundModificator modificator;

	public WoundAttribute(Hero hero, Position position) {
		super(hero, position.getName());
		this.position = position;
		this.modificator = new WoundModificator(hero, this, true);
		setMinimum(0);
		setMaximum(3);
		setValue(0);
	}

	public WoundAttribute(Hero hero, JSONObject json) throws JSONException {
		super(hero, json.getString(FIELD_POSITION));
		position = Position.valueOf(json.getString(FIELD_POSITION));

		// migrate old deprecated positions to new ones
		if (position == Position.LeftLowerArm)
			position = Position.LinkerArm;
		if (position == Position.RightLowerArm)
			position = Position.RechterArm;
		if (position == Position.UpperLeg)
			position = Position.LinkesBein;
		if (position == Position.LowerLeg)
			position = Position.RechtesBein;

		boolean active = true;
		if (json.has(FIELD_ACTIVE))
			active = json.getBoolean(FIELD_ACTIVE);
		this.modificator = new WoundModificator(hero, this, active);
		setMinimum(0);
		setMaximum(3);
		setValue(json.getInt(FIELD_VALUE));
	}

	@Override
	public String getName() {
		return position.getName();
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public boolean isActive() {
		return modificator.isActive();
	}

	public void setActive(boolean active) {
		this.modificator.setActive(active);
	}

	public WoundModificator getModificator() {
		return modificator;
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
		out.put(FIELD_VALUE, getValue());
		out.put(FIELD_ACTIVE, isActive());

		return out;
	}

}
