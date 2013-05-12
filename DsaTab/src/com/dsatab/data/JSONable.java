package com.dsatab.data;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONable {
	public JSONObject toJSONObject() throws JSONException;
}
