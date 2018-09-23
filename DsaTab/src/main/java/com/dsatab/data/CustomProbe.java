package com.dsatab.data;

import android.net.Uri;

import com.dsatab.R;
import com.dsatab.data.listable.Listable;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;
import com.dsatab.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class CustomProbe extends BaseProbe implements JSONable, Listable {

	private static final String FIELD_NAME = "name";
	private static final String FIELD_MODIFIER_TYPE = "modType";
	private static final String FIELD_PROBE_TYPE = "probeType";
	private static final String FIELD_VALUE = "value";
	private static final String FIELD_BE_PATTERN = "bePattern";
	private static final String FIELD_PROBE_PATTERN = "probePattern";
	private static final String FIELD_DESCRIPTION = "description";
	private static final String FIELD_FOOTER = "footer";
	private static final String FIELD_ICON = "icon";
	private static final String FIELD_ID = "id";

	private UUID id;
	private ProbeType probeType;
	private ModificatorType modificatorType;
	private String name, description, footer;
	private Integer value;
	private Uri iconUri;

	public CustomProbe(AbstractBeing being) {
		super(being);
		this.id = UUID.randomUUID();
		this.iconUri = Util.getUriForResourceId(R.drawable.vd_dice_six_faces_two);
	}

	public CustomProbe(AbstractBeing being, String name, ProbeType probeType, ModificatorType modificatorType,
			Integer value, String probePattern, String bePattern) {
		super(being);

		this.id = UUID.randomUUID();
		this.name = name;
		this.probeType = probeType;
		this.modificatorType = modificatorType;
		this.value = value;

		getProbeInfo().applyBePattern(bePattern);
		getProbeInfo().applyProbePattern(probePattern);

	}

	public CustomProbe(AbstractBeing being, JSONObject json) {
		super(being);

		this.id = UUID.fromString(json.optString(FIELD_ID));
		this.name = json.optString(FIELD_NAME);
		this.description = json.optString(FIELD_DESCRIPTION);
		this.footer = json.optString(FIELD_FOOTER);
		this.probeType = ProbeType.valueOf(json.optString(FIELD_PROBE_TYPE, ProbeType.ThreeOfThree.name()));
		this.modificatorType = ModificatorType.valueOf(json.optString(FIELD_MODIFIER_TYPE, ModificatorType.ALL.name()));
		if (json.has(FIELD_ICON)) {
			this.iconUri = Uri.parse(json.optString(FIELD_ICON));
		}
		this.value = (Integer) json.opt(FIELD_VALUE);
		getProbeInfo().applyBePattern(json.optString(FIELD_BE_PATTERN));
		getProbeInfo().applyProbePattern(json.optString(FIELD_PROBE_PATTERN));

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public void setProbeType(ProbeType probeType) {
		this.probeType = probeType;
	}

	public void setModificatorType(ModificatorType modificatorType) {
		this.modificatorType = modificatorType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public ProbeType getProbeType() {
		return probeType;
	}

	@Override
	public ModificatorType getModificatorType() {
		return modificatorType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public Integer getProbeBonus() {
		return value;
	}

	public Uri getIconUri() {
		return iconUri;
	}

	public void setIconUri(Uri iconUri) {
		this.iconUri = iconUri;
	}

	public long getId() {
		return id.hashCode();
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		json.putOpt(FIELD_ID, id.toString());
		json.putOpt(FIELD_NAME, name);
		json.putOpt(FIELD_DESCRIPTION, description);
		json.putOpt(FIELD_FOOTER, footer);
		json.putOpt(FIELD_MODIFIER_TYPE, modificatorType.name());
		json.putOpt(FIELD_PROBE_TYPE, probeType.name());
		json.putOpt(FIELD_VALUE, value);
		json.putOpt(FIELD_BE_PATTERN, probeInfo.getBe());
		json.putOpt(FIELD_PROBE_PATTERN, probeInfo.getAttributesString());
		if (iconUri != null) {
			json.putOpt(FIELD_ICON, iconUri.toString());
		}
		return json;
	}

}
