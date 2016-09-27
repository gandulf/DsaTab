package com.dsatab.data.items;

import com.dsatab.data.enums.ItemType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public abstract class ItemSpecification implements Cloneable, Serializable {

	private static final long serialVersionUID = -5119414460010671876L;

	public static final String ITEM_ID_FIELD_NAME = "item_id";

	@DatabaseField(foreign = true, foreignAutoRefresh = false, columnName = ITEM_ID_FIELD_NAME)
	protected Item item;

	@DatabaseField
	protected ItemType type;

	@DatabaseField
	protected String specificationLabel;

	@DatabaseField
	protected int version;

	/**
	 * 
	 */
	public ItemSpecification() {
		this.type = ItemType.Sonstiges;
	}

	public ItemSpecification(Item item, ItemType type, int version) {
		this.item = item;
		this.type = type;
		this.version = version;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public abstract int getId();

	public abstract String getInfo();

	public abstract String getName();

	public String getSpecificationLabel() {
		return specificationLabel;
	}

	public void setSpecificationLabel(String specificationLabel) {
		this.specificationLabel = specificationLabel;
	}

	public ItemType getType() {
		return type;
	}

	public int getVersion() {
		return version;
	}

	void setVersion(int version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected ItemSpecification clone() throws CloneNotSupportedException {
		return (ItemSpecification) super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ItemSpecification)) {
			return false; // different class
		}

		if (!getClass().equals(obj.getClass())) {
			return false; // different sub class
		}

		ItemSpecification other = (ItemSpecification) obj;

		if (this.getId() == other.getId()) {
			return true;
		}

		return super.equals(obj);
	}
}
