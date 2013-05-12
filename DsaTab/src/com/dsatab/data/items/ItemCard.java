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
