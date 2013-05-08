package com.dsatab.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

import com.dsatab.data.enums.FeatureType;
import com.dsatab.util.Util;

public class Feature {

	public static final String TALENTSPEZIALISIERUNG_PREFIX = "Talentspezialisierung ";
	public static final String ZAUBERSPEZIALISIERUNG_PREFIX = "Zauberspezialisierung ";
	public static final String RITUAL_KENNTNIS_PREFIX = "Ritualkenntnis:";

	private FeatureType type;
	private String comment;
	private List<String> values;

	public Feature(FeatureType type) {
		this.type = type;
	}

	public FeatureType getType() {
		return type;
	}

	public String getComment() {
		return comment;
	}

	public List<String> getValues() {
		if (values == null)
			return Collections.emptyList();
		else
			return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getValue() {
		if (values != null && values.size() == 1)
			return Util.parseInteger(values.get(0));
		else
			return null;
	}

	public String getValueAsString() {
		if (values != null && values.size() == 1)
			return values.get(0);
		else
			return null;
	}

	@Override
	public String toString() {
		if (values == null || values.isEmpty()) {
			return type.xmlName();
		} else if (values.size() == 1) {
			return type.xmlName() + " (" + values.get(0) + ")";
		} else {
			return type.xmlName() + " (" + TextUtils.join(", ", values) + ")";
		}
	}

	public void addValue(String value) {
		if (!TextUtils.isEmpty(value)) {
			if (values == null) {
				values = new ArrayList<String>(3);
			}
			this.values.add(value);
		}
	}
}
