/*
 * Copyright (C) 2010 Gandulf Kohlweiss
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.dsatab.fragment;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.Hero;
import com.dsatab.map.MapTileProviderLocal;
import com.dsatab.util.Util;
import com.gandulf.guilib.download.AbstractDownloader;
import com.gandulf.guilib.download.DownloaderWrapper;

public class MapFragment extends BaseFragment {

	/**
	 * 
	 */
	private static final String OSM_AVENTURIEN = "OSM_AVENTURIEN";

	private static final String PREF_KEY_LAST_MAP_COORDINATES = "lastMapCoordinates";
	private static final String PREF_KEY_LAST_MAP = "lastMap";

	private static final String PREF_KEY_OSM_ASK = "osm.askdownload";
	private static final String PREF_KEY_OSM_ZOOM = "osm.zoomLevel";
	private static final String PREF_KEY_OSM_LATITUDE = "osm.lat";
	private static final String PREF_KEY_OSM_LONGITUDE = "osm.lon";
	private static final int DEFAULT_OSM_ZOOM = 2;

	public enum TouchMode {
		None, Drag, Zoom;
	}

	private ProgressBar progress;
	private TextView progressText;

	private ImageView imageMapView;
	private PhotoViewAttacher mAttacher;

	private MapView osmMapView;

	private String activeMap = null;

	private String[] mapFiles;
	private String[] mapNames;

	private BitmapDrawable bitmap;

	private LoadImageTask loadImageTask;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.
	 * actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.map_menu, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.
	 * actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.option_map_choose) {
			showMapChooser();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sheet_map, container, false);

		imageMapView = (ImageView) root.findViewById(R.id.imageView);

		progress = (ProgressBar) root.findViewById(R.id.map_progress);
		progressText = (TextView) root.findViewById(R.id.map_progress_text);
		osmMapView = null;
		return configureContainerView(root);
	}

	private void initOSMMapView() {
		if (osmMapView == null) {
			File osmMapDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_OSM_MAPS);
			ITileSource tileSource = TileSourceFactory.getTileSource(DsaTabApplication.TILESOURCE_AVENTURIEN);
			MapTileProviderLocal tileProvider = new MapTileProviderLocal(osmMapDir.getAbsolutePath(), getActivity(),
					tileSource);
			osmMapView = new MapView(getActivity(), 256, new DefaultResourceProxyImpl(getActivity()), tileProvider);
			osmMapView.setUseDataConnection(false);
			osmMapView.setBuiltInZoomControls(true);
			osmMapView.setMultiTouchControls(true);

			osmMapView.getController().setZoom(preferences.getInt(PREF_KEY_OSM_ZOOM, DEFAULT_OSM_ZOOM));

			int latitude = preferences.getInt(PREF_KEY_OSM_LATITUDE, -1);
			int longitude = preferences.getInt(PREF_KEY_OSM_LONGITUDE, -1);
			if (latitude != -1 && longitude != -1) {
				IGeoPoint center = new GeoPoint(latitude, longitude);
				osmMapView.getController().setCenter(center);
			}

		}
		if (osmMapView.getParent() == null) {
			((ViewGroup) getView()).addView(osmMapView, 0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (loadImageTask != null) {
			loadImageTask.cancel(true);
			loadImageTask = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		// imageMapView.setOnTouchListener(this);

		List<String> mapFiles = new ArrayList<String>();
		List<String> mapNames = new ArrayList<String>();

		File mapDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_MAPS);
		File osmMapDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_OSM_MAPS);

		if (!mapDir.exists())
			mapDir.mkdirs();

		File[] files = mapDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {

				filename = filename.toLowerCase(Locale.GERMAN);

				return filename.endsWith(".jpg") || filename.endsWith(".gif") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg") || filename.endsWith(".bmp");
			}
		});
		if (files != null) {
			Arrays.sort(files, new Util.FileNameComparator());
			for (File file : files) {
				if (file.isFile()) {
					mapFiles.add(file.getName());
					mapNames.add(file.getName().replace("-", " ").substring(0, file.getName().length() - 4));
				}
			}
		}

		files = osmMapDir.listFiles();
		if (files != null && files.length > 0) {
			mapFiles.add(OSM_AVENTURIEN);
			mapNames.add("Aventurien (GoogleMaps)");
		} else {
			if (preferences.getBoolean(PREF_KEY_OSM_ASK, true)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Neue Aventurien Karte");
				builder.setMessage("Es gibt jetzt eine GoogleMaps ähnliche Karte von ganz Aventurien. Willst du dir das Kartenpaket (ca. 10MB) aus dem Internet herunterladen?");
				builder.setCancelable(true);
				builder.setPositiveButton("Herunterladen", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						AbstractDownloader downloader = DownloaderWrapper.getInstance(
								DsaTabApplication.getDsaTabPath(), getActivity());
						downloader.addPath(DsaTabPreferenceActivity.PATH_OSM_MAP_PACK);
						downloader.downloadZip();
					}
				});
				builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Editor edit = preferences.edit();
						edit.putBoolean(PREF_KEY_OSM_ASK, false);
						edit.commit();
						dialog.dismiss();

					}
				});
				builder.show();
			}

		}

		TextView empty = (TextView) findViewById(android.R.id.empty);

		if (mapFiles.isEmpty()) {
			String path = mapDir.getAbsolutePath();
			empty.setVisibility(View.VISIBLE);
			imageMapView.setVisibility(View.GONE);
			if (osmMapView != null)
				osmMapView.setVisibility(View.GONE);

			empty.setText(Util.getText(R.string.message_map_empty, path));

			this.mapFiles = null;
			this.mapNames = null;

		} else {

			empty.setVisibility(View.GONE);

			this.mapFiles = mapFiles.toArray(new String[0]);
			this.mapNames = mapNames.toArray(new String[0]);

		}

		mAttacher = new PhotoViewAttacher(imageMapView);

		super.onActivityCreated(savedInstanceState);
	}

	private void loadMap(String filePath) {

		if (activeMap != null && activeMap.equals(filePath)) {
			return;
		} else {
			unloadMap();
			activeMap = filePath;
		}

		if (OSM_AVENTURIEN.equals(filePath)) {
			imageMapView.setVisibility(View.GONE);
			initOSMMapView();
			osmMapView.setVisibility(View.VISIBLE);
		} else {
			if (osmMapView != null)
				osmMapView.setVisibility(View.GONE);
			imageMapView.setVisibility(View.VISIBLE);

			progress.setVisibility(View.VISIBLE);
			progressText.setVisibility(View.VISIBLE);
			progress.setIndeterminate(true);

			loadImageTask = new LoadImageTask(this);
			loadImageTask.execute(filePath);

		}
	}

	private void mapLoaded(BitmapDrawable bitmap) {

		progress.setVisibility(View.GONE);
		progressText.setVisibility(View.GONE);

		if (bitmap != null) {
			this.bitmap = bitmap;

			imageMapView.setImageDrawable(bitmap);
			// Attach a PhotoViewAttacher, which takes care of all of the
			// zooming functionality.
			mAttacher.update();

			String coords = preferences.getString(PREF_KEY_LAST_MAP_COORDINATES, null);
			if (coords != null) {
				mAttacher.setSuppViewMatrix(Util.parseFloats(coords));
			}

		}

		loadImageTask = null;
	}

	private static class LoadImageTask extends AsyncTask<String, String, Bitmap> {

		private WeakReference<MapFragment> mapFragmentRef;

		/**
		 * 
		 */
		public LoadImageTask(MapFragment mapFragment) {
			mapFragmentRef = new WeakReference<MapFragment>(mapFragment);
		}

		protected Bitmap doInBackground(String... urls) {
			File file = new File(DsaTabApplication.getDirectory(DsaTabApplication.DIR_MAPS), urls[0]);
			publishProgress(file.getName());
			return Util.decodeBitmap(file, 1000);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(String... values) {

		}

		protected void onPostExecute(Bitmap result) {
			if (isCancelled()) {
				onCancelled(result);
				return;
			}

			MapFragment mapFragment = mapFragmentRef.get();
			if (mapFragment != null && mapFragment.getActivity() != null) {

				if (result != null) {
					Toast.makeText(mapFragment.getActivity(), "Karte geladen.", Toast.LENGTH_SHORT).show();
					mapFragment.mapLoaded(new BitmapDrawable(mapFragment.getResources(), result));
				} else {
					Toast.makeText(mapFragment.getActivity(), "Konnte Karte nicht laden.", Toast.LENGTH_SHORT).show();
					mapFragment.mapLoaded(null);
				}

			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
		 */
		protected void onCancelled(Bitmap result) {
			MapFragment mapFragment = mapFragmentRef.get();
			if (mapFragment != null && mapFragment.getActivity() != null) {
				mapFragment.mapLoaded(null);
			}
		}
	}

	private void unloadMap() {

		if (loadImageTask != null) {
			loadImageTask.cancel(true);
			loadImageTask = null;
		}

		if (bitmap != null) {
			imageMapView.setImageDrawable(null);
			bitmap.setCallback(null);
			if (bitmap.getBitmap() != null)
				bitmap.getBitmap().recycle();
			bitmap = null;
		}

		activeMap = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {

		Editor edit = preferences.edit();
		edit.putString(PREF_KEY_LAST_MAP_COORDINATES, Util.toString(mAttacher.getSuppViewMatrix()));
		if (osmMapView != null) {
			edit.putInt(PREF_KEY_OSM_ZOOM, osmMapView.getZoomLevel());
			IGeoPoint center = osmMapView.getMapCenter();
			edit.putInt(PREF_KEY_OSM_LATITUDE, center.getLatitudeE6());
			edit.putInt(PREF_KEY_OSM_LATITUDE, center.getLongitudeE6());
		}
		edit.commit();

		// clear bitmap
		unloadMap();

		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		String lastMap = preferences.getString(PREF_KEY_LAST_MAP, null);

		if (osmMapView != null) {
			osmMapView.getController().setZoom(preferences.getInt(PREF_KEY_OSM_ZOOM, DEFAULT_OSM_ZOOM));

			int latitude = preferences.getInt(PREF_KEY_OSM_LATITUDE, -1);
			int longitude = preferences.getInt(PREF_KEY_OSM_LONGITUDE, -1);
			if (latitude != -1 && longitude != -1) {
				IGeoPoint center = new GeoPoint(latitude, longitude);
				osmMapView.getController().setCenter(center);
			}
		}

		if (!TextUtils.isEmpty(lastMap)) {
			loadMap(lastMap);
		}

	}

	private void showMapChooser() {
		if (this.mapFiles == null || this.mapFiles.length == 0) {
			Toast.makeText(getActivity(), "Keine Karten gefunden", Toast.LENGTH_SHORT).show();
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Karte auswählen");
		builder.setItems(mapNames, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor edit = preferences.edit();
				edit.putString(PREF_KEY_LAST_MAP, mapFiles[which]);
				edit.commit();

				loadMap(mapFiles[which]);

				dialog.dismiss();
			}
		});

		builder.show();
	}

}
