package com.dsatab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.db.DatabaseHelper;
import com.dsatab.map.BitmapTileSource;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.xml.HeldenXmlParser;
import com.dsatab.xml.Xml;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DsaTabApplication extends Application implements OnSharedPreferenceChangeListener {

	public static final int HS_VERSION_INT = 5330;
	public static final String HS_VERSION = "5.3.3";

	public static final String TILESOURCE_AVENTURIEN = "AVENTURIEN";

	public static final String BUGSENSE_API_KEY = "4b4062da";

	public static final String DROPBOX_API_KEY = "fivprcmrt2bjm2c";
	public static final String DROPBOX_API_SECRET = "ezq611w6bmie4js";

	public static final String SD_CARD_PATH_PREFIX = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator;

	public static final String DEFAULT_SD_CARD = "dsatab/";

	public static final String DIR_MAPS = "maps";

	public static final String DIR_OSM_MAPS = "osm_map";

	public static final String DIR_PDFS = "pdfs";

	public static final String DIR_PORTRAITS = "portraits";

	public static final String DIR_CARDS = "cards";

	public static final String DIR_BACKGROUNDS = "backgrounds";

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
	private static String basePath, heroPath;
	private static File baseDir, heroDir;

	public Hero hero = null;

	private DsaTabConfiguration configuration;

	private DatabaseHelper databaseHelper = null;

	private Typeface poorRichFont;

	private boolean firstRun;

	/**
	 * Convenient access, saves having to call and cast getApplicationContext()
	 */
	public static DsaTabApplication getInstance() {
		checkInstance();
		return instance;
	}

	public static File getDsaTabDirectory() {
		if (baseDir == null) {
			baseDir = new File(getDsaTabPath());
			if (!baseDir.exists())
				baseDir.mkdirs();
		}
		return baseDir;
	}

	public static File getDsaTabHeroDirectory() {
		if (heroDir == null) {
			heroDir = new File(getDsaTabHeroPath());
			if (!heroDir.exists())
				heroDir.mkdirs();
		}
		return heroDir;
	}

	public static void setDirectory(String name, File dir) {
		Editor edit = getPreferences().edit();
		edit.putString(DsaTabPreferenceActivity.KEY_SETUP_SDCARD_PATH_PREFIX + name, dir.getAbsolutePath());
		edit.commit();

	}

	public static File getDirectory(String name) {
		File dirFile = null;
		if (getPreferences().contains(DsaTabPreferenceActivity.KEY_SETUP_SDCARD_PATH_PREFIX + name)) {
			File dir = new File(getPreferences().getString(
					DsaTabPreferenceActivity.KEY_SETUP_SDCARD_PATH_PREFIX + name, null));

			if (dir.exists() && dir.isDirectory()) {
				dirFile = dir;
			}
		}
		if (dirFile == null) {
			dirFile = new File(getDsaTabDirectory(), name);
		}
		if (!dirFile.exists())
			dirFile.mkdirs();
		return dirFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#
	 * onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(DsaTabPreferenceActivity.KEY_SETUP_SDCARD_PATH)) {
			basePath = null;
			baseDir = null;
			checkDirectories();
		} else if (key.equals(DsaTabPreferenceActivity.KEY_SETUP_SDCARD_HERO_PATH)) {
			heroPath = null;
			heroDir = null;
			checkDirectories();
		}

	}

	public static String getDsaTabPath() {
		if (basePath == null) {
			basePath = getPreferences().getString(DsaTabPreferenceActivity.KEY_SETUP_SDCARD_PATH, DEFAULT_SD_CARD);

			if (!basePath.endsWith("/"))
				basePath += "/";

			if (!basePath.startsWith("/"))
				basePath = SD_CARD_PATH_PREFIX + basePath;

		}

		return basePath;
	}

	public static String getDsaTabHeroPath() {
		if (heroPath == null) {
			heroPath = getPreferences().getString(DsaTabPreferenceActivity.KEY_SETUP_SDCARD_HERO_PATH, DEFAULT_SD_CARD);

			if (!heroPath.endsWith("/"))
				heroPath += "/";

			if (!heroPath.startsWith("/")) {

				heroPath = SD_CARD_PATH_PREFIX + heroPath;
			}

		}

		return heroPath;
	}

	private static void checkDirectories() {

		Debug.verbose("Checking dsatab dir " + getDsaTabPath() + " for subdirs");
		File base = getDsaTabDirectory();

		File recordingsDir = getDirectory(DIR_RECORDINGS);
		File mapsDir = getDirectory(DIR_MAPS);
		File osmmapsDir = getDirectory(DIR_OSM_MAPS);
		File cardsDir = getDirectory(DIR_CARDS);
		File portraitsDir = getDirectory(DIR_PORTRAITS);
		File pdfsDir = getDirectory(DIR_PDFS);
		File bgDir = getDirectory(DIR_BACKGROUNDS);

		Debug.verbose("Checking dsatab hero dir " + getDsaTabHeroPath());
		File heroes = getDsaTabHeroDirectory();
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
		// provide an instance for our static accessors
		instance = this;

		cleanUp();

		setTheme(getCustomTheme());

		configuration = new DsaTabConfiguration(this);

		poorRichFont = Typeface.createFromAsset(this.getAssets(), "fonts/poorich.ttf");

		if (!BuildConfig.DEBUG)
			BugSenseHandler.initAndStartSession(this, BUGSENSE_API_KEY);

		checkDirectories();

		getPreferences().registerOnSharedPreferenceChangeListener(this);

		firstRun = getPreferences().getBoolean("dsaTabFirstRun", true);

		Editor edit = getPreferences().edit();
		edit.putBoolean("dsaTabFirstRun", false);

		TileSourceFactory.getTileSources().clear();
		final ITileSource tileSource = new BitmapTileSource(TILESOURCE_AVENTURIEN, null, 2, 5, 256, ".jpg");
		TileSourceFactory.addTileSource(tileSource);

		File token = new File(getDsaTabDirectory(), "token.txt");
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
			edit.commit();
		}

	}

	public Typeface getPoorRichardFont() {
		return poorRichFont;
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

	public void saveHero(Activity activity) {
		if (hero == null || activity == null) {
			return;
		}

		try {
			HeroExchange exchange = new HeroExchange(activity);

			InputStream fis = exchange.getInputStream(hero.getFileInfo(), FileType.Hero);
			if (fis == null) {
				Debug.warning("Unable to read hero from input stream: " + hero.getFileInfo());
				Toast.makeText(this, R.string.message_save_hero_failed, Toast.LENGTH_LONG).show();
				return;
			}

			Document dom = null;
			try {
				dom = HeldenXmlParser.readDocument(fis);
			} finally {
				exchange.closeStream(hero.getFileInfo(), FileType.Hero);
				Util.close(fis);
			}

			Element heroElement = dom.getRootElement().getChild(Xml.KEY_HELD);
			HeldenXmlParser.onPreHeroSaved(hero, heroElement);

			OutputStream out = exchange.getOutputStream(hero.getFileInfo(), FileType.Hero);
			if (out == null) {
				Debug.warning("Unable to write hero to output stream: " + hero.getFileInfo());
				Toast.makeText(this, R.string.message_save_hero_failed, Toast.LENGTH_LONG).show();
				return;
			}

			try {
				HeldenXmlParser.writeHero(hero, dom, out);
				hero.onPostHeroSaved();
			} finally {
				exchange.closeStream(hero.getFileInfo(), FileType.Hero);
				Util.close(out);
			}

			OutputStream outConfig = exchange.getOutputStream(hero.getFileInfo(), FileType.Config);
			if (outConfig == null) {
				Debug.warning("Unable to write config file for hero: " + hero.getFileInfo());
				Toast.makeText(this, R.string.message_save_hero_failed, Toast.LENGTH_LONG).show();
				return;
			}

			try {
				outConfig.write(hero.getHeroConfiguration().toJSONObject().toString().getBytes());
			} finally {
				exchange.closeStream(hero.getFileInfo(), FileType.Config);
				Util.close(outConfig);
			}

			if (!getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_AUTO_SAVE, true)) {
				Toast.makeText(this, getString(R.string.hero_saved, hero.getName()), Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			Toast.makeText(this, R.string.message_save_hero_failed, Toast.LENGTH_LONG).show();
			Debug.error(e);
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