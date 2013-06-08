package com.dsatab.data.items;

import android.text.TextUtils;

import com.dsatab.R;
import com.dsatab.data.enums.ItemType;
import com.dsatab.util.Util;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "item_shield")
public class Shield extends CloseCombatItem {

	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField
	private boolean shield;
	@DatabaseField
	private boolean paradeWeapon;

	// transient cache for the info string
	private String info;

	/**
	 * no arg constructor for ormlite
	 */
	public Shield() {

	}

	public Shield(Item item) {
		super(item, ItemType.Schilde, 0);
	}

	public boolean isShield() {
		return shield;
	}

	public void setShield(boolean shield) {
		this.shield = shield;
	}

	public boolean isParadeWeapon() {
		return paradeWeapon;
	}

	public void setParadeWeapon(boolean paradeWeapon) {
		this.paradeWeapon = paradeWeapon;
	}

	@Override
	public int getResourceId() {
		if (isParadeWeapon() && !isShield())
			return R.drawable.icon_messer;
		else
			return R.drawable.icon_shield;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.items.ItemSpecification#getName()
	 */
	@Override
	public String getName() {
		if (isShield() && isParadeWeapon())
			return "Schild/Parierwaffe";
		else if (isShield())
			return "Schild";
		else if (isParadeWeapon())
			return "Parierwaffe";
		else
			return "NO SHIELD AND PARADES";
	}

	@Override
	public String getInfo() {
		if (info == null) {
			info = TextUtils.expandTemplate("^1/^2 Bf ^3 Ini ^4", Util.toString(getWmAt()), Util.toString(getWmPa()),
					Util.toString(getBf()), Util.toString(getIni())).toString();
		}
		return info;
	}
}
