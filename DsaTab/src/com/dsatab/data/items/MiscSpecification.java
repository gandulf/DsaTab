package com.dsatab.data.items;

import com.dsatab.R;
import com.dsatab.data.enums.ItemType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "item_misc")
public class MiscSpecification extends ItemSpecification {

	@DatabaseField(generatedId = true)
	protected int id;

	/**
	 * no arg constructor for ormlite
	 */
	public MiscSpecification() {
		super(null, ItemType.Sonstiges, 0);
	}

	/**
	 * @param item
	 */
	public MiscSpecification(Item item, ItemType type) {
		super(item, type != null ? type : ItemType.Sonstiges, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.items.ItemSpecification#getName()
	 */
	@Override
	public String getName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.items.ItemSpecification#getInfo()
	 */
	@Override
	public String getInfo() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.items.ItemSpecification#getResourceId()
	 */
	@Override
	public int getResourceId() {
		if (type != null) {
			switch (type) {
			case Behälter:
				return R.drawable.icon_bags;
			case Schmuck:
				return R.drawable.icon_special;
			case Kleidung:
				return R.drawable.icon_armor_cloth;
			case Sonstiges:
				return R.drawable.icon_misc;
			default:
				return R.drawable.icon_other;
			}
		} else {
			return R.drawable.icon_other;
		}
	}

}
