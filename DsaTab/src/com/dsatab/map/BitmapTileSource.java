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
package com.dsatab.map;

import java.io.InputStream;

import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.dsatab.DsaTabApplication;
import com.dsatab.util.Debug;

/**
 * @author Ganymede
 * 
 */
public class BitmapTileSource extends BitmapTileSourceBase {

	/**
	 * @param aName
	 * @param aResourceId
	 * @param aZoomMinLevel
	 * @param aZoomMaxLevel
	 * @param aTileSizePixels
	 * @param aImageFilenameEnding
	 */

	public BitmapTileSource(String aName, string aResourceId, int aZoomMinLevel, int aZoomMaxLevel,
			int aTileSizePixels, String aImageFilenameEnding) {
		super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels, aImageFilenameEnding);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase#pathBase()
	 */
	@Override
	public String pathBase() {
		return "";
	}

	@Override
	public String getTileRelativeFilenameString(final MapTile tile) {
		final StringBuilder sb = new StringBuilder();
		sb.append(tile.getZoomLevel());
		sb.append('/');
		sb.append(tile.getX());
		sb.append('/');
		sb.append(tile.getY());
		sb.append(imageFilenameEnding());
		sb.append(TILE_PATH_EXTENSION);
		return sb.toString();
	}

	@Override
	public Drawable getDrawable(final InputStream aFileInputStream) {
		try {
			// default implementation will load the file as a bitmap and create
			// a BitmapDrawable from it
			final Bitmap bitmap = BitmapFactory.decodeStream(aFileInputStream);
			if (bitmap != null) {
				return new BitmapDrawable(DsaTabApplication.getInstance().getResources(), bitmap);
			}

		} catch (final OutOfMemoryError e) {
			Debug.error("OutOfMemoryError loading bitmap");
			System.gc();
		}
		return null;
	}

	@Override
	public Drawable getDrawable(final String aFilePath) {
		try {
			// default implementation will load the file as a bitmap and create
			// a BitmapDrawable from it
			final Bitmap bitmap = BitmapFactory.decodeFile(aFilePath);
			if (bitmap != null) {
				return new BitmapDrawable(DsaTabApplication.getInstance().getResources(), bitmap);
			} else {
				// if we couldn't load it then it's invalid - delete it
				Debug.error("Error loading bitmap " + aFilePath);
			}
		} catch (final OutOfMemoryError e) {
			Debug.error("OutOfMemoryError loading bitmap: " + aFilePath);
			System.gc();
		}
		return null;
	}

}
