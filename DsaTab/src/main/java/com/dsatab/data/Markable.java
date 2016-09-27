package com.dsatab.data;

/**
 * 
 * 
 */
public interface Markable {

	public String getName();

	public boolean isFavorite();

	public boolean isUnused();

	public void setFavorite(boolean value);

	public void setUnused(boolean value);

}
