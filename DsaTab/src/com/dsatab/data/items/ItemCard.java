package com.dsatab.data.items;

import java.util.Comparator;

import android.net.Uri;

public interface ItemCard {

	public static final int INVALID_POSITION = -1;

	public static final Comparator<ItemCard> CELL_NUMBER_COMPARATOR = new Comparator<ItemCard>() {
		@Override
		public int compare(ItemCard lhs, ItemCard rhs) {
			if (lhs.getCellNumber() == -1 && rhs.getCellNumber() >= 0)
				return 1;
			else if (lhs.getCellNumber() == -1 && rhs.getCellNumber() == -1)
				return 0;
			else if (lhs.getCellNumber() > 0 && rhs.getCellNumber() == -1)
				return -1;

			return lhs.getCellNumber() - rhs.getCellNumber();
		}
	};

	public int getCellNumber();

	public void setCellNumber(int cell);

	public int getContainerId();

	public Uri getImageUri();

	public boolean hasImage();

	public boolean isImageTextOverlay();

	public String getTitle();

	public Item getItem();

}
