package com.dsatab.data.items;

import com.dsatab.data.enums.ItemType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "item_misc")
public class MiscSpecification extends ItemSpecification {

	private static final long serialVersionUID = -3149075995866654756L;

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

	@Override
	public int getId() {
		return id;
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

}
