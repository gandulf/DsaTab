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
package com.dsatab.data.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.text.TextUtils;

import com.dsatab.data.adapter.OpenArrayAdapter;
import com.dsatab.data.adapter.OpenFilter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.ItemType;

/**
 * @author Ganymede
 * 
 */
public class ItemCardListFilter extends OpenFilter<ItemCard> {

	private List<ItemType> types;

	private String category;

	/**
	 * 
	 */
	public ItemCardListFilter(OpenArrayAdapter<ItemCard> list) {
		super(list);
	}

	public List<ItemType> getTypes() {
		return types;
	}

	public void setTypes(List<ItemType> type) {
		this.types = type;
	}

	public void setType(ItemType type) {
		if (type != null)
			this.types = Arrays.asList(type);
		else
			this.types = null;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	protected boolean isFilterSet() {
		return constraint != null || types != null || category != null;
	}

	public boolean filter(ItemCard m) {
		boolean valid = true;
		Item item = null;
		if (m instanceof Item)
			item = (Item) m;
		else if (m instanceof EquippedItem) {
			item = ((EquippedItem) m).getItem();
		}

		if (types != null) {
			boolean found = false;

			for (ItemSpecification spec : item.getSpecifications()) {
				if (types.contains(spec.getType())) {
					found = true;
					break;
				}
			}

			valid &= found;
		}

		if (!TextUtils.isEmpty(category)) {
			valid &= category.equals(item.getCategory());
		}

		if (constraint != null) {
			valid &= item.getName().toLowerCase(Locale.GERMAN).startsWith(constraint);
		}

		return valid;
	}

}
