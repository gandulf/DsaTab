package com.dsatab;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.config.DsaTabConfiguration;
import com.dsatab.data.Hero;
import com.dsatab.db.DatabaseHelper;
import com.dsatab.fragment.dialog.ChangeLogDialog;
import com.dsatab.map.BitmapTileSource;
import com.dsatab.util.Debug;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.splunk.mint.Mint;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import pl.tajchert.nammu.Nammu;

public class DsaTabApplication extends MultiDexApplication {

	public static final int HS_VERSION_INT = 5510;
	public static final String HS_VERSION = "5.5.1";

	public static final String TILESOURCE_AVENTURIEN = "AVENTURIEN";

	public static final String BUGSENSE_API_KEY = "4b4062da";

	public static final String DROPBOX_API_KEY = "fivprcmrt2bjm2c";
	public static final String DROPBOX_API_SECRET = "ezq611w6bmie4js";

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

	private HeroExchange exchange;

	private boolean firstRun;
	private boolean newsShown = false;

    public static RefWatcher getRefWatcher(Context context) {
        DsaTabApplication application = (DsaTabApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

	/**
	 * Convenient access, saves having to call and cast getApplicationContext()
	 */
	public static DsaTabApplication getInstance() {
		checkInstance();
		return instance;
	}

	public ContextThemeWrapper getContextWrapper(Context context) {
		if (isDarkTheme())
			return new ContextThemeWrapper(context, R.style.DsaTabTheme_Dark);
		else
			return new ContextThemeWrapper(context, R.style.DsaTabTheme_Light);
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

	public boolean isDarkTheme() {
		return R.style.DsaTabTheme_Dark == getCustomTheme();
	}

	public int getCustomTheme() {
		String theme = getPreferences().getString(DsaTabPreferenceActivity.KEY_THEME, THEME_DEFAULT);

		if (THEME_LIGHT_PLAIN.equals(theme)) {
			return R.style.DsaTabTheme_Light;
		} else if (THEME_DARK_PLAIN.equals(theme)) {
			return R.style.DsaTabTheme_Dark;
		} else {
			return R.style.DsaTabTheme_Light;
		}

	}

	public int getCustomPreferencesTheme() {
		String theme = getPreferences().getString(DsaTabPreferenceActivity.KEY_THEME, THEME_DEFAULT);

		if (THEME_LIGHT_PLAIN.equals(theme)) {
			return R.style.DsaTabTheme_Light;
		} else if (THEME_DARK_PLAIN.equals(theme)) {
			return R.style.DsaTabTheme_Dark;
		} else {
			return R.style.DsaTabTheme_Light;
		}
	}

	public String getCustomThemeValue() {
		String theme = getPreferences().getString(DsaTabPreferenceActivity.KEY_THEME, THEME_DEFAULT);

		List<String> themeValues = Arrays.asList(getResources().getStringArray(R.array.themesValues));
		int index = themeValues.indexOf(theme);
		if (index >= 0 && index < themeValues.size()) {
			return themeValues.get(index);
		} else
			return themeValues.get(0);

	}

	public String getCustomThemeName() {
		String theme = getPreferences().getString(DsaTabPreferenceActivity.KEY_THEME, THEME_DEFAULT);

		List<String> themeValues = Arrays.asList(getResources().getStringArray(R.array.themesValues));
		int index = themeValues.indexOf(theme);

		String[] themes = getResources().getStringArray(R.array.themes);
		if (index >= 0 && index < themes.length)
			return themes[index];
		else
			return themes[0];
	}

	@Override
	public void onCreate() {
		super.onCreate();
        refWatcher= LeakCanary.install(this);
		// provide an instance for our static accessors
        instance = this;

		cleanUp();

		setTheme(getCustomTheme());

		configuration = new DsaTabConfiguration(this);

		if (!BuildConfig.DEBUG)
			Mint.initAndStartSession(this, BUGSENSE_API_KEY);

		migrateDirectories();

		firstRun = getPreferences().getBoolean("dsaTabFirstRun", true);

		Editor edit = getPreferences().edit();
		edit.putBoolean("dsaTabFirstRun", false);

		TileSourceFactory.getTileSources().clear();
		final ITileSource tileSource = new BitmapTileSource(TILESOURCE_AVENTURIEN, null, 2, 5, 256, ".jpg");
		TileSourceFactory.addTileSource(tileSource);

		File token = new File(getDirectory(), "token.txt");
		if (token.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(token));
				String tokenValue = reader.readLine();
				reader.close();
				if (!TextUtils.isEmpty(tokenValue)) {
					edit.putString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, tokenValue.trim());
				}
			} catch (FileNotFoundException e) {
				Debug.error(e);
			} catch (IOException e) {
				Debug.error(e);
			}
		}

		edit.commit();

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

		exchange = new HeroExchange(getBaseContext());


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

	public HeroExchange getExchange() {
		return exchange;
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
			edit.commit();
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

	public void showNewsInfoPopup(Activity activity) {
		if (newsShown)
			return;

		ChangeLogDialog.show(activity, false, 0);
		newsShown = true;
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