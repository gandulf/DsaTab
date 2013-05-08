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

import java.util.Comparator;

import android.net.Uri;

import com.dsatab.data.ItemLocationInfo;

public interface ItemCard {

	public static final Comparator<ItemCard> CELL_NUMBER_COMPARATOR = new Comparator<ItemCard>() {
		@Override
		public int compare(ItemCard lhs, ItemCard rhs) {
			if (lhs.getItemInfo().getCellNumber() == -1 && rhs.getItemInfo().getCellNumber() >= 0)
				return 1;
			else if (lhs.getItemInfo().getCellNumber() == -1 && rhs.getItemInfo().getCellNumber() == -1)
				return 0;
			else if (lhs.getItemInfo().getCellNumber() > 0 && rhs.getItemInfo().getCellNumber() == -1)
				return -1;

			return lhs.getItemInfo().getCellNumber() - rhs.getItemInfo().getCellNumber();
		}
	};

	public ItemLocationInfo getItemInfo();

	public Uri getImageUri();

	public boolean hasImage();

	public boolean isImageTextOverlay();

	public String getTitle();

	public Item getItem();

}
