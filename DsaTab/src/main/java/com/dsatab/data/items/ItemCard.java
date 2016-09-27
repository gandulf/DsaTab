package com.dsatab.data.items;

import android.net.Uri;

public interface ItemCard {

	public static final int INVALID_POSITION = -1;

	public int getContainerId();

	public Uri getImageUri();

	public boolean hasImage();

	public boolean isImageTextOverlay();

	public String getTitle();

	public Item getItem();

}
