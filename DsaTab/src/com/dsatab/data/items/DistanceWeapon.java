package com.dsatab.data.items;

import android.text.TextUtils;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.Dice;
import com.dsatab.data.enums.TalentType;
import com.dsatab.db.TalentTypeWrapper;
import com.dsatab.util.Util;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "item_distance_weapon")
public class DistanceWeapon extends ItemSpecification {

	public static final int DISTANCE_COUNT = 5;

	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField
	private String tp;
	@DatabaseField
	private String distances;
	@DatabaseField
	private String tpDistances;
	@DatabaseField(foreign = true)
	private TalentTypeWrapper combatTalentTypeWrapper;

	// cache for split tpDistances string
	private String tpDistance[];
	// cache for split distances string
	private String distance[];

	/**
	 * no arg constructor for ormlite
	 */
	public DistanceWeapon() {
		super(null, ItemType.Fernwaffen, 0);
	}

	public DistanceWeapon(Item item) {
		super(item, ItemType.Fernwaffen, 0);
	}

	public String getTp() {
		return tp;
	}

	public void setTp(String tp) {
		this.tp = tp;
	}

	public String getDistances() {
		initDistances();
		return distances;
	}

	public int getMaxDistance() {
		int count = getDistanceCount();

		if (count >= 0)
			return Util.parseInteger(getDistance(count - 1));
		else
			return 0;

	}

	protected int getDistanceCount() {
		initDistances();
		if (distance != null)
			return distance.length;
		else
			return 0;
	}

	public String getDistance(int index) {
		initDistances();
		if (distance != null && distance.length > index)
			return distance[index];
		else
			return null;
	}

	public void setDistances(int index, String value) {
		if (distance == null) {
			distance = new String[DISTANCE_COUNT];
		}
		this.distance[index] = value;
		this.distances = null;
		initDistances();
	}

	public void setDistances(String[] distances) {
		this.distance = distances;
		this.distances = null;
		initDistances();
	}

	public void setDistances(String distances) {
		this.distances = distances;
		this.distance = null;
		initDistances();
	}

	private void initDistances() {
		if (distance != null && distances != null)
			return;

		if (distances != null)
			this.distance = Util.splitDistanceString(distances);
		else if (distance != null) {
			distances = "(" + TextUtils.join("/", distance) + ")";
		}
	}

	private void initTpDistances() {
		if (tpDistance != null && tpDistances != null)
			return;

		if (tpDistances != null)
			this.tpDistance = Util.splitDistanceString(tpDistances);
		else if (distance != null) {
			tpDistances = "(" + TextUtils.join("/", tpDistance) + ")";
		}
	}

	public String getTpDistances() {
		initTpDistances();
		return tpDistances;
	}

	public void setTpDistances(String tpDistances) {
		this.tpDistances = tpDistances;
		this.tpDistance = null;
		initTpDistances();
	}

	public void setTpDistances(int index, String value) {
		if (tpDistance == null) {
			tpDistance = new String[DISTANCE_COUNT];
		}
		this.tpDistance[index] = value;
		this.tpDistances = null;
		initTpDistances();
	}

	public TalentType getTalentType() {
		if (combatTalentTypeWrapper != null)
			return combatTalentTypeWrapper.get();
		else
			return null;
	}

	public void setTalentType(TalentType combatTalentType) {
		if (combatTalentType != null)
			this.combatTalentTypeWrapper = new TalentTypeWrapper(combatTalentType);
		else
			this.combatTalentTypeWrapper = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.items.ItemSpecification#getName()
	 */
	@Override
	public String getName() {
		return "Fernkampf";
	}

	public String getInfo() {
		return getTp() + " " + getDistances() + " " + getTpDistances();
	}

	public CharSequence getInfo(int modifier) {

		StyleableSpannableStringBuilder info = new StyleableSpannableStringBuilder();

		if (modifier != 0) {
			Dice dice = Dice.parseDice(tp);
			if (dice != null) {

				dice.constant += modifier;
				if (modifier > 0) {
					info.appendColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueGreen),
							dice.toString());
				} else {
					info.appendColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueRed),
							dice.toString());
				}
			} else {
				info.append(tp);
				if (modifier > 0) {
					info.appendColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueGreen),
							Util.toProbe(modifier));
				} else {
					info.appendColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueRed),
							Util.toProbe(modifier));
				}
			}
		} else {
			info.append(tp);
		}

		info.append(" ");
		info.append(getDistances());
		info.append(" ");
		info.append(getTpDistances());

		return info;
	}

	public int getResourceId() {
		switch (getTalentType()) {
		case Wurfmesser:
			return R.drawable.icon_wurfdolch;
		case Armbrust:
			return R.drawable.icon_crossbow;
		case Wurfbeile:
			return R.drawable.icon_wurfbeil;
		case Wurfspeere:
			return R.drawable.icon_speer;
		case Diskus:
			return R.drawable.icon_diskus;
		case Schleuder:
			return R.drawable.icon_sling;
		default:
			return R.drawable.icon_bow;

		}
	}

}
