package com.dsatab.data;

import android.text.TextUtils;

import com.dsatab.data.enums.AttributeType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class ProbeInfo implements Cloneable, Serializable {

	private static final long serialVersionUID = 2396412014329540630L;

	/*
	 * e.g.: MU/IN/KL (+5) or (MU/IN/KL) or +5 or MU IN KL or 10/MU/9
	 */
	private static final String pattern_post = "\\(?([+-]\\d+)?\\)?"; // (-+5)

	private static final String pattern_mu = "([0-9A-Za-z-]+)\\s*[/ ]?\\s*"; // MU/

	private static final Pattern PROBE_PATTERN_ATTR_ = Pattern.compile(
			"(\\(?([0-9a-z-]{2})\\s*[/ ]\\s*([0-9a-z-]{2})\\s*[/ ]\\s*([0-9a-z-]{2})\\)?)?" + pattern_post,
			Pattern.CASE_INSENSITIVE);

	private static final Pattern PROBE_PATTERN_ATTR = Pattern.compile(
			"(\\(?(" + pattern_mu + ")*\\)?)?" + pattern_post, Pattern.CASE_INSENSITIVE);

	private List<Object> attributeValues;
	private String attributesString;

	/**
	 * Returns the probe modification positive values means the probe is more difficult, negative values simplifies the
	 * probe
	 * 
	 * @return
	 */
	private Integer erschwernis;

	private static final int BE_FLAG_MULTIPLY = 64;
	private static final int BE_FLAG_ADDITION = 128;
	private static final int BE_FLAG_SUBTRACTION = 256;
	private static final int BE_FLAG_NONE = -1;

	private int beFlag;

	private String bePattern;

	public ProbeInfo() {
		beFlag = BE_FLAG_NONE;
		attributeValues = new ArrayList<Object>(3);
	}

	public List<Object> getAttributeValues() {
		return attributeValues;
	}

	public void applyProbePattern(String s) {
		attributesString = null;
		attributeValues.clear();

		if (!TextUtils.isEmpty(s)) {
			// remove all whitespaces from string
			s = s.replace("/", " ");
			s = s.replace(",", " ");
			s = s.replace("\\", " ");
			s = s.replace("(", " ");
			s = s.replace(")", " ");
			s = s.replace("+", " +");
			s = s.replace("-", " -");

			StringTokenizer st = new StringTokenizer(s, " ");
			while (st.hasMoreTokens()) {

				String v = st.nextToken().trim();

				if (!TextUtils.isEmpty(v)) {

					if (v.startsWith("+") || v.startsWith("-")) {
						try {
							erschwernis = Util.parseInteger(v);
						} catch (NumberFormatException e) {
                            erschwernis = 0;

                            HashMap<String,String> customData = new HashMap<>(2);
                            customData.put("Erschwernis",v);
                            customData.put("ProbePattern", s);
                            Debug.logCustomEvent("ProbeInfo - Unparseable Erschwernis",customData);
						}
					} else {
						AttributeType type = AttributeType.byCode(v);
						if (type != null) {
							attributeValues.add(type);
						} else {
							try {
								attributeValues.add(Util.parseInteger(v));
							} catch (NumberFormatException e) {
                                attributeValues.add(0);

                                HashMap<String,String> customData = new HashMap<>(2);
                                customData.put("AttributeValue",v);
                                customData.put("ProbePattern",s);
                                Debug.logCustomEvent("ProbeInfo - Unparseable AttributeValue",customData);
							}
						}
					}
				}
			}
		}
	}

	public void applyBePattern(Integer beModifier) {
		if (beModifier == null) {
			beFlag = BE_FLAG_NONE;
			bePattern = null;
		} else if (beModifier > 0) {
			beFlag = BE_FLAG_ADDITION + beModifier;
			bePattern = "BE+" + beModifier;
		} else if (beModifier < 0) {
			beFlag = BE_FLAG_SUBTRACTION + Math.abs(beModifier);
			bePattern = "BE" + beModifier;
		} else {
			beFlag = BE_FLAG_ADDITION + 0;
			bePattern = "BE";
		}
	}

	public void applyBePattern(String beModifier) {
		if (beModifier != null) {
			bePattern = beModifier.toUpperCase(Locale.GERMAN);
			bePattern = bePattern.replace("BEX", "BEx");
		} else {
			bePattern = null;
		}
		if (TextUtils.isEmpty(beModifier)) {
			beFlag = BE_FLAG_NONE;
		} else {
			beModifier = beModifier.toUpperCase(Locale.GERMAN);
			if ("BE".equalsIgnoreCase(beModifier)) {
				beFlag = BE_FLAG_ADDITION;
			} else if (beModifier.startsWith("BE-")) {
				try {
					int beMinus = Util.parseInteger(beModifier.substring(3));
					beFlag = BE_FLAG_SUBTRACTION + Math.abs(beMinus);
				} catch (NumberFormatException e) {
					Debug.e(e);
				}
			} else if (beModifier.startsWith("BE+")) {
				try {
					int beAdd = Util.parseInteger(beModifier.substring(3));
					beFlag = BE_FLAG_ADDITION + Math.abs(beAdd);
				} catch (NumberFormatException e) {
					Debug.e(e);
				}
			} else if (beModifier.startsWith("BEX")) {
				try {
					int beMulti = Util.parseInteger(beModifier.substring(3));
					beFlag = BE_FLAG_MULTIPLY + beMulti;
				} catch (NumberFormatException e) {
					Debug.e(e);
				}
			} else if ("0->BE".equalsIgnoreCase(beModifier)) {
				beFlag = BE_FLAG_NONE;
			} else {
				Debug.w("Could not parse beModifier " + beModifier);
			}
		}
	}

	public String getBe() {
		return bePattern;
	}

	public boolean hasBe() {
		return beFlag != BE_FLAG_NONE;
	}

	public int getBe(int value, int be) {

		if (beFlag == BE_FLAG_NONE) {
			return value;
		} else if (beFlag >= BE_FLAG_SUBTRACTION) {
			return value - (Math.max(0, be - (beFlag - BE_FLAG_SUBTRACTION)));
		} else if (beFlag >= BE_FLAG_ADDITION) {
			return value - (be + (beFlag - BE_FLAG_ADDITION));
		} else if (beFlag >= BE_FLAG_MULTIPLY) {
			return value - (be * (beFlag - BE_FLAG_MULTIPLY));
		} else
			return 0;
	}

	/**
	 * Returns the probe modification positive values means the probe is more difficult, negative values simplifies the
	 * probe
	 * 
	 * @return
	 */
	public Integer getErschwernis() {
		return erschwernis;
	}

	public String getAttributesString() {
		if (attributeValues.isEmpty())
			return null;
		else {
			if (attributesString == null) {

				StringBuilder sb = new StringBuilder();
				sb.append("(");

				for (int i = 0; i < attributeValues.size(); i++) {
					if (i > 0)
						sb.append("/");

					Object o = attributeValues.get(i);

					if (o instanceof AttributeType) {
						sb.append(((AttributeType) o).code());
					} else if (o != null) {
						sb.append(o.toString());
					} else {
						sb.append("--");
					}
				}

				sb.append(")");

				attributesString = sb.toString();

			}
			return attributesString;
		}
	}

	public void clearAttributeValues() {
		this.attributesString = null;
		attributeValues.clear();
	}

	public void setErschwernis(Integer erschwernis) {
		this.erschwernis = erschwernis;
	}

	@Override
	public String toString() {

		if (!attributeValues.isEmpty() && erschwernis != null)
			return getAttributesString() + " " + Util.toProbe(erschwernis);

		if (!attributeValues.isEmpty() && erschwernis == null)
			return getAttributesString();

		if (attributeValues.isEmpty() && erschwernis != null)
			return Util.toProbe(erschwernis);

		return null;

	}

	public static ProbeInfo parse(String s) {
		ProbeInfo info = new ProbeInfo();
		info.applyProbePattern(s);
		return info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ProbeInfo clone() {
		try {
			return (ProbeInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			Debug.e(e);
			return null;
		}
	}
}
