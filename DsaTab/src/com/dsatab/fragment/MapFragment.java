package com.dsatab.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.Hero;
import com.dsatab.map.MapTileProviderLocal;
import com.dsatab.util.Util;
import com.gandulf.guilib.download.AbstractDownloader;
import com.gandulf.guilib.download.DownloaderWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import uk.co.senab.photoview.PhotoViewAttacher;

public class MapFragment extends BaseFragment {

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
	private TextView emptyView;

	private ImageView imageMapView;
	private PhotoViewAttacher mAttacher;

	private MapView osmMapView;

	private String activeMap = null;

	private File mapDir;
	private List<String> mapFiles;
	private List<String> mapNames;
	private boolean osmMapLoaded = false;

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
		getMapFiles();
	}

	@Override
	public void hideActionBarItems() {
		super.hideActionBarItems();

		removeMapNavigation();
	}

	@Override
	public void showActionBarItems() {
		super.showActionBarItems();

		initMapNavigation();
	}

	private void initMapNavigation() {

		mapFiles = null;
		mapNames = null;

		if (!getMapNames().isEmpty()) {
			ActionBar actionBar = getActionBarActivity().getSupportActionBar();

			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
					android.R.layout.simple_spinner_item, getMapNames());
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {

				@Override
				public boolean onNavigationItemSelected(int itemPosition, long itemId) {
					loadMap(getMapFiles().get(itemPosition));
					return false;
				}
			});

			String lastMap = getPreferences().getString(PREF_KEY_LAST_MAP, null);
			int mapIndex = getMapFiles().indexOf(lastMap);
			if (mapIndex >= 0)
				actionBar.setSelectedNavigationItem(mapIndex);
			else
				actionBar.setSelectedNavigationItem(0);
		}
	}

	public List<String> getMapFiles() {
		if (mapFiles == null) {
			initMaps();
		}
		return mapFiles;
	}

	public List<String> getMapNames() {
		if (mapNames == null) {
			initMaps();
		}
		return mapNames;
	}

	private void initMaps() {
		mapFiles = new ArrayList<String>();
		mapNames = new ArrayList<String>();

		mapDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_MAPS);
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
			osmMapLoaded = true;
			mapFiles.add(OSM_AVENTURIEN);
			mapNames.add("Aventurien (GoogleMaps)");
		} else {
			osmMapLoaded = false;
		}
	}

	private void removeMapNavigation() {
		ActionBar actionBar = getActionBarActivity().getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setListNavigationCallbacks(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sheet_map, container, false);

		imageMapView = (ImageView) root.findViewById(R.id.imageView);

		progress = (ProgressBar) root.findViewById(R.id.map_progress);
		progressText = (TextView) root.findViewById(R.id.map_progress_text);
		osmMapView = null;

		emptyView = (TextView) root.findViewById(android.R.id.empty);
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

			osmMapView.getController().setZoom(getPreferences().getInt(PREF_KEY_OSM_ZOOM, DEFAULT_OSM_ZOOM));

			int latitude = getPreferences().getInt(PREF_KEY_OSM_LATITUDE, -1);
			int longitude = getPreferences().getInt(PREF_KEY_OSM_LONGITUDE, -1);
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
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		if (mapFiles.isEmpty()) {
			String path = mapDir.getAbsolutePath();
			emptyView.setVisibility(View.VISIBLE);
			imageMapView.setVisibility(View.GONE);
			if (osmMapView != null)
				osmMapView.setVisibility(View.GONE);

			emptyView.setText(Util.getText(R.string.message_map_empty, path));

		} else {
			emptyView.setVisibility(View.GONE);
		}

		mAttacher = new PhotoViewAttacher(imageMapView);

		if (!osmMapLoaded && getPreferences().getBoolean(PREF_KEY_OSM_ASK, true)) {
			AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity());
			builder.setTitle("Neue Aventurien Karte");
			builder.setMessage("Es gibt jetzt eine GoogleMaps ähnliche Karte von ganz Aventurien. Willst du dir das Kartenpaket (ca. 10MB) aus dem Internet herunterladen?");
			builder.setCancelable(true);
			builder.setPositiveButton("Herunterladen", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					AbstractDownloader downloader = DownloaderWrapper.getInstance(DsaTabApplication.getDirectory()
							.getAbsolutePath(), getActivity());
					downloader.addPath(DsaTabPreferenceActivity.PATH_OSM_MAP_PACK);
					downloader.downloadZip();
				}
			});
			builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					Editor edit = getPreferences().edit();
					edit.putBoolean(PREF_KEY_OSM_ASK, false);
					edit.commit();
					dialog.dismiss();

				}
			});
			builder.show();

		}

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

			String coords = getPreferences().getString(PREF_KEY_LAST_MAP_COORDINATES, null);
			if (coords != null) {
                // TODO load original settings
				// mAttacher.setSuppViewMatrix(Util.parseFloats(coords));
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

		@Override
		protected Bitmap doInBackground(String... urls) {
			File file = new File(DsaTabApplication.getDirectory(DsaTabApplication.DIR_MAPS), urls[0]);
			publishProgress(file.getName());

			ImageSize imageSize = new ImageSize(1000, 1000);
			String uri = Uri.fromFile(file).toString();

			return ImageLoader.getInstance().loadImageSync(Uri.decode(uri), imageSize);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(String... values) {

		}

		@Override
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
		@Override
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

		Editor edit = getPreferences().edit();
		edit.putString(PREF_KEY_LAST_MAP, activeMap);
        // TODO store settings
		//edit.putString(PREF_KEY_LAST_MAP_COORDINATES, Util.toString(mAttacher.getSuppViewMatrix()));
		if (osmMapView != null) {
			edit.putInt(PREF_KEY_OSM_ZOOM, osmMapView.getZoomLevel());
			IGeoPoint center = osmMapView.getMapCenter();
			edit.putInt(PREF_KEY_OSM_LATITUDE, center.getLatitudeE6());
			edit.putInt(PREF_KEY_OSM_LONGITUDE, center.getLongitudeE6());
		}
		edit.commit();

		// clear bitmap
		unloadMap();
		removeMapNavigation();

		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		initMapNavigation();

	}

}
