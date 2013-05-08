/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.data.items;

import com.j256.ormlite.field.DatabaseField;

public abstract class ItemSpecification implements Cloneable {

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

	public abstract String getInfo();

	public abstract String getName();

	public String getSpecificationLabel() {
		return specificationLabel;
	}

	public void setSpecificationLabel(String specificationLabel) {
		this.specificationLabel = specificationLabel;
	}

	public abstract int getResourceId();

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
}
