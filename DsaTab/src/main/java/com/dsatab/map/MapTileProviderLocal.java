package com.dsatab.map;

import android.content.Context;

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

public class MapTileProviderLocal extends MapTileProviderArray {

	/**
	 * @param pContext
	 */
	public MapTileProviderLocal(String basePath, Context pContext) {
		this(basePath, pContext, TileSourceFactory.DEFAULT_TILE_SOURCE);

	}

	/**
	 * @param pContext
	 * @param pTileSource
	 */
	public MapTileProviderLocal(String basePath, Context pContext, ITileSource pTileSource) {
		this(basePath, new SimpleRegisterReceiver(pContext), new NetworkAvailabliltyCheck(pContext), pTileSource);

	}

	/**
	 * @param pRegisterReceiver
	 * @param aNetworkAvailablityCheck
	 * @param pTileSource
	 */
	public MapTileProviderLocal(String basePath, IRegisterReceiver pRegisterReceiver,
			INetworkAvailablityCheck aNetworkAvailablityCheck, ITileSource pTileSource) {
		super(pTileSource, pRegisterReceiver);

		final MapTileAbsoluteFilesystemProvider fileSystemProvider = new MapTileAbsoluteFilesystemProvider(basePath,
				pRegisterReceiver, pTileSource);
		mTileProviderList.add(fileSystemProvider);
	}

}
