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

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

import android.content.Context;

/**
 * @author Ganymede
 * 
 */
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
