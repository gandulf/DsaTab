package com.dsatab.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.dsatab.DsaTabApplication;
import com.dsatab.util.Debug;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase;
import org.osmdroid.tileprovider.tilesource.ITileSource;

import java.io.InputStream;

import static org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.TILE_PATH_EXTENSION;

public class BitmapTileSource extends BitmapTileSourceBase {

    public static final String TILESOURCE_AVENTURIEN = "AVENTURIEN";

	/**
	 * @param aName
	 * @param aZoomMinLevel
	 * @param aZoomMaxLevel
	 * @param aTileSizePixels
	 * @param aImageFilenameEnding
	 */

    public static final ITileSource AVENTURIEN = new BitmapTileSource(TILESOURCE_AVENTURIEN, 2, 5, 256, ".jpg");

	public BitmapTileSource(String aName, int aZoomMinLevel, int aZoomMaxLevel,
			int aTileSizePixels, String aImageFilenameEnding) {
		super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels, aImageFilenameEnding);

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
			Debug.e("OutOfMemoryError loading bitmap");
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
				Debug.e("Error loading bitmap " + aFilePath);
			}
		} catch (final OutOfMemoryError e) {
			Debug.e("OutOfMemoryError loading bitmap: " + aFilePath);
			System.gc();
		}
		return null;
	}

}
