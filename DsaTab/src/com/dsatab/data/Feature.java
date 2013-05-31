package com.dsatab.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.TextUtils;

import com.dsatab.data.enums.FeatureType;

public class Feature {

	public static final String TALENTSPEZIALISIERUNG_PREFIX = "Talentspezialisierung ";
	public static final String ZAUBERSPEZIALISIERUNG_PREFIX = "Zauberspezialisierung ";
	public static final String RITUAL_KENNTNIS_PREFIX = "Ritualkenntnis:";
	public static final String LITURGIE_KENNTNIS_PREFIX = "Liturgiekenntnis ";

	private static final String DELIM = "|";
	private static final String DELIM_REG_EXP = "\\|";

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

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getValue() {
		return getValue(0);
	}

	public String getValue(int index) {
		if (values != null && values.size() > index)
			return values.get(index);
		else
			return null;
	}

	public String[] getValues(int index) {
		String value = getValue(index);
		if (value != null)
			return TextUtils.split(value, DELIM_REG_EXP);
		else
			return null;
	}

	public void addValues(String... values) {
		if (values != null) {
			addValue(TextUtils.join(DELIM, values));
		}
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

	public void addAllValues(List<String> values) {
		if (values != null) {
			for (String value : values) {
				addValue(value);
			}
		}
	}
}
