package com.dsatab.map;

import android.graphics.drawable.Drawable;

import com.dsatab.util.Debug;

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileRequestState;
import org.osmdroid.tileprovider.modules.MapTileFileStorageProviderBase;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase.LowMemoryException;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.io.File;

import static org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.DEFAULT_MAXIMUM_CACHED_FILE_AGE;
import static org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.NUMBER_OF_TILE_FILESYSTEM_THREADS;
import static org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.TILE_FILESYSTEM_MAXIMUM_QUEUE_SIZE;

public class MapTileAbsoluteFilesystemProvider extends MapTileFileStorageProviderBase {

	// ===========================================================
	// Constants
	// ===========================================================
	public static final int MAXIMUM_ZOOMLEVEL = 12;
    public static final int MINIMUM_ZOOMLEVEL = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	private final long mMaximumCachedFileAge;

	private ITileSource mTileSource;

	private String basePath;

	// ===========================================================
	// Constructors
	// ===========================================================

	public MapTileAbsoluteFilesystemProvider(String basePath, final IRegisterReceiver pRegisterReceiver) {
		this(basePath, pRegisterReceiver, TileSourceFactory.DEFAULT_TILE_SOURCE);
	}

	public MapTileAbsoluteFilesystemProvider(String basePath, final IRegisterReceiver pRegisterReceiver,
			final ITileSource aTileSource) {
		this(pRegisterReceiver, aTileSource, DEFAULT_MAXIMUM_CACHED_FILE_AGE);
		this.basePath = basePath;
	}

	/**
	 * Provides a file system based cache tile provider. Other providers can register and store data in the cache.
	 * 
	 * @param pRegisterReceiver
	 */
	public MapTileAbsoluteFilesystemProvider(final IRegisterReceiver pRegisterReceiver, final ITileSource pTileSource,
			final long pMaximumCachedFileAge) {
		super(pRegisterReceiver, NUMBER_OF_TILE_FILESYSTEM_THREADS, TILE_FILESYSTEM_MAXIMUM_QUEUE_SIZE);
		mTileSource = pTileSource;

		mMaximumCachedFileAge = pMaximumCachedFileAge;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean getUsesDataConnection() {
		return false;
	}

	@Override
	protected String getName() {
		return "File System Cache Provider";
	}

	@Override
	protected String getThreadGroupName() {
		return "filesystem";
	}

	@Override
	protected Runnable getTileLoader() {
		return new TileLoader();
	}

	@Override
	public int getMinimumZoomLevel() {
		return mTileSource != null ? mTileSource.getMinimumZoomLevel() : MINIMUM_ZOOMLEVEL;
	}

	@Override
	public int getMaximumZoomLevel() {
		return mTileSource != null ? mTileSource.getMaximumZoomLevel() : MAXIMUM_ZOOMLEVEL;
	}

	@Override
	public void setTileSource(final ITileSource pTileSource) {
		mTileSource = pTileSource;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class TileLoader extends MapTileModuleProviderBase.TileLoader {

		@Override
		public Drawable loadTile(final MapTileRequestState pState) throws CantContinueException {

			if (mTileSource == null) {
				return null;
			}

			final MapTile tile = pState.getMapTile();

			// if there's no sdcard then don't do anything
			if (!isSdCardAvailable()) {
				Debug.v("No sdcard - do nothing for tile: " + tile);
				return null;
			}

			// Check the tile source to see if its file is available and if so,
			// then render the
			// drawable and return the tile
			final File file = new File(basePath, mTileSource.getTileRelativeFilenameString(tile));
			if (file.exists()) {

				// Check to see if file has expired
				final long now = System.currentTimeMillis();
				final long lastModified = file.lastModified();
				final boolean fileExpired = lastModified < now - mMaximumCachedFileAge;

				if (!fileExpired) {
					// If the file has not expired, then render it and return
					// it!
					try {
						Drawable drawable;
						drawable = mTileSource.getDrawable(file.getPath());

						return drawable;
					} catch (LowMemoryException e) {
						// low memory so empty the queue
						Debug.w("LowMemoryException downloading MapTile: " + tile + " : " + e);
						throw new CantContinueException(e);
					}
				} else {
					// If the file has expired then we render it, but we return
					// it as a candidate
					// and then fail on the request. This allows the tile to be
					// loaded, but also
					// allows other tile providers to do a better job.
					try {
						final Drawable drawable = mTileSource.getDrawable(file.getPath());
						tileLoaded(pState, drawable);
						return null;
					} catch (LowMemoryException e) {
						// low memory so empty the queue
						Debug.w("LowMemoryException downloading MapTile: " + tile + " : " + e);
						throw new CantContinueException(e);
					}
				}
			}

			// If we get here then there is no file in the file cache
			return null;
		}
	}
}
