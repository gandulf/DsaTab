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

import com.dsatab.R;
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
	}

}
