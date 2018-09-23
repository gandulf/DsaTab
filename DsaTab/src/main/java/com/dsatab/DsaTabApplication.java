package com.dsatab;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;

import com.cloudrail.si.CloudRail;
import com.crashlytics.android.Crashlytics;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.config.DsaTabConfiguration;
import com.dsatab.data.Hero;
import com.dsatab.db.DatabaseHelper;
import com.dsatab.map.BitmapTileSource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import pl.tajchert.nammu.Nammu;

public class DsaTabApplication extends MultiDexApplication {

	public static final int HS_VERSION_INT = 5520;
	public static final String HS_VERSION = "5.5.2";

	public static final String SD_CARD_PATH_PREFIX = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator;

	public static final String DEFAULT_SD_CARD = "dsatab/";

	public static final String DIR_MAPS = "maps";

	public static final String DIR_OSM_MAPS = "osm_map";

	public static final String DIR_PORTRAITS = "portraits";

	public static final String DIR_CARDS = "cards";

	public static final String DIR_RECORDINGS = "recordings";

	public static final String PAYPAL_DONATION_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=gandulf%2ek%40gmx%2enet&lc=DE&item_name=Gandulf&item_number=DsaTab&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted";

	public static final String TAG = "DSATab";

	public static final String THEME_LIGHT_PLAIN = "light_plain";
	public static final String THEME_DARK_PLAIN = "dark_plain";
	public static final String THEME_DEFAULT = THEME_LIGHT_PLAIN;

	// instance
	private static DsaTabApplication instance = null;

	/**
	 * Cache for corrected path
	 */
    private static File baseFile;

	public Hero hero = null;

	private DsaTabConfiguration configuration;

	private DatabaseHelper databaseHelper = null;

	private boolean firstRun;

	/**
	 * Convenient access, saves having to call and cast getApplicationContext()
	 */
	public static DsaTabApplication getInstance() {
		checkInstance();
		return instance;
	}

	public static File getDirectory(String name) {
        File dirFile;
        if (name != null) {
            dirFile = new File(getDsaTabDirectory(), name);
        } else {
            dirFile = getDsaTabDirectory();
        }

		if (!dirFile.exists())
			dirFile.mkdirs();

		return dirFile;
	}



	private static File getDsaTabDirectory() {
		if (baseFile == null) {
			String basePath = getPreferences().getString(DsaTabPreferenceActivity.KEY_SETUP_SDCARD_PATH, DEFAULT_SD_CARD);

			if (!basePath.endsWith("/"))
				basePath += "/";

			if (!basePath.startsWith("/"))
				basePath = SD_CARD_PATH_PREFIX + basePath;

            baseFile = new File(basePath);
		}

		return baseFile;
	}

	public static File getHeroDirectory() {
		return new File(getExternalHeroPath());
	}

	public static File getDirectory() {
		return getDirectory(null);
	}

	public static String getExternalHeroPath() {

		String	heroPath = getPreferences().getString(DsaTabPreferenceActivity.KEY_SETUP_SDCARD_HERO_PATH, DEFAULT_SD_CARD);

        if (!heroPath.endsWith("/"))
            heroPath += "/";

        if (!heroPath.startsWith("/")) {
            heroPath = SD_CARD_PATH_PREFIX + heroPath;
        }

		return heroPath;
	}

	public static SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(getInstance());
	}

	public DsaTabConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Accessor for some resource that depends on a context
	 */

	private static void checkInstance() {
		if (instance == null)
			throw new IllegalStateException("Application not created yet!");
	}



	@Override
	public void onCreate() {
		super.onCreate();

		// provide an instance for our static accessors
        instance = this;

        // disable strict mode for file uri exposure
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //

		cleanUp();

		setTheme(DsaTabPreferenceActivity.getCustomTheme());

        CloudRail.setAppKey(BuildConfig.cloudrail_api_key);

		configuration = new DsaTabConfiguration(this);

		Fabric.with(this, new Crashlytics());

		migrateDirectories();

		firstRun = getPreferences().getBoolean("dsaTabFirstRun", true);

		Editor edit = getPreferences().edit();
		edit.putBoolean("dsaTabFirstRun", false);

		TileSourceFactory.getTileSources().clear();
		TileSourceFactory.addTileSource(BitmapTileSource.AVENTURIEN);

		edit.apply();

		// Create global configuration and initialize ImageLoader with this config
		// Create default options which will be used for every
		// displayImage(...) call if no options will be passed to this method

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.defaultDisplayImageOptions(defaultOptions).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

        Nammu.init(getApplicationContext());

    }

	private void migrateDirectories() {
        File[] oldDirs = ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
        if (oldDirs!=null) {
            for (File oldDir : oldDirs) {
                if (oldDir !=null && oldDir.isDirectory()) {
                    File[] files = oldDir.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            File newFile = new File(getDsaTabDirectory(), f.getName());
                            f.renameTo(newFile);
                        }
                    }
                }
            }
        }
	}

	public boolean isFirstRun() {
		return firstRun;
	}

	private void cleanUp() {

		// make sure we have a valid theme
		SharedPreferences preferences = getPreferences();
		String theme = preferences.getString(DsaTabPreferenceActivity.KEY_THEME, THEME_DEFAULT);
		List<String> themeValues = Arrays.asList(getResources().getStringArray(R.array.themesValues));
		int index = themeValues.indexOf(theme);
		if (index < 0) {
			Editor edit = preferences.edit();
			edit.putString(DsaTabPreferenceActivity.KEY_THEME, THEME_DEFAULT);
			edit.apply();
		}

	}

	public Hero getHero() {
		if (hero != null)
			return hero;
		else
			return null;
	}

	public void setHero(Hero hero) {
		this.hero = hero;
	}

	/**
	 * 
	 */
	public void release() {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	public DatabaseHelper getDBHelper() {
		if (databaseHelper == null) {

			// AssetsDatabaseHelper assetsDatabaseHelper = new AssetsDatabaseHelper(getApplicationContext());
			// assetsDatabaseHelper.getReadableDatabase();

			databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
		}
		return databaseHelper;
	}

	public int getPackageVersion() {
		int version = -1;
		try {
			PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = manager.versionCode;
		} catch (NameNotFoundException e) {
			// Handle exception
		}
		return version;
	}

	public String getPackageVersionName() {
		String version = null;
		try {
			PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = manager.versionName;
		} catch (NameNotFoundException e) {
			// Handle exception
		}
		return version;
	}

}