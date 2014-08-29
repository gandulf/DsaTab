package com.dsatab.activity;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.saik0.android.unifiedpreference.UnifiedPreferenceActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dsatab.DsaTabApplication;
import com.dsatab.DsaTabConfiguration;
import com.dsatab.DsaTabConfiguration.ArmorType;
import com.dsatab.DsaTabConfiguration.WoundType;
import com.dsatab.R;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.data.HeroFileInfo.StorageType;
import com.dsatab.fragment.BasePreferenceFragment;
import com.dsatab.util.Debug;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;
import com.dsatab.view.PreferenceWithButton;
import com.dsatab.view.dialog.ChangeLogDialog;
import com.dsatab.view.dialog.DirectoryChooserDialogHelper;
import com.dsatab.view.dialog.DirectoryChooserDialogHelper.Result;
import com.gandulf.guilib.download.AbstractDownloader;
import com.gandulf.guilib.download.DownloaderWrapper;
import com.gandulf.guilib.util.ResUtil;

public class DsaTabPreferenceActivity extends UnifiedPreferenceActivity implements OnSharedPreferenceChangeListener {

	static final int REQUEST_LINK_TO_DBX = 1190;
	public static final int ACTION_PICK_BG_PATH = 1001;
	public static final int ACTION_PICK_BG_WOUNDS_PATH = 1002;

	public static final String KEY_PROBE_PROBABILITY = "probeProbability";
	public static final String KEY_NOTES_VISIBILITY = "showNotes";
	public static final String KEY_PROBE_SHAKE_ROLL_DICE = "shakeRollDice";
	public static final String KEY_PROBE_ANIM_ROLL_DICE = "animRollDice";
	public static final String KEY_PROBE_SOUND_ROLL_DICE = "soundRollDice";
	public static final String KEY_PROBE_AUTO_ROLL_DICE = "autoRollDice";
	public static final String KEY_PROBE_SOUND_RESULT_DICE = "soundResultDice";
	public static final String KEY_PROBE_SHOW_MODIFIKATORS = "probeShowModificators";

	public static final String KEY_HOUSE_RULES = "houseRules";
	public static final String KEY_HOUSE_RULES_2_OF_3_DICE = "houseRules.2of3Dice";
	public static final String KEY_HOUSE_RULES_LE_MODIFIER = "houseRules.leModifier";
	public static final String KEY_HOUSE_RULES_AU_MODIFIER = "houseRules.auModifier";
	public static final String KEY_HOUSE_RULES_EASIER_WOUNDS = "houseRules.easierWounds";
	public static final String KEY_HOUSE_RULES_MORE_WOUND_ZONES = "houseRules.moreWoundZones";
	public static final String KEY_HOUSE_RULES_MORE_TARGET_ZONES = "houseRules.moreTargetZones";

	public static final String KEY_ARMOR_TYPE = "armorType";
	public static final String KEY_WOUND_TYPE = "woundType";
	public static final String KEY_FULLSCREEN = "fullscreen";

	public static final String KEY_SETUP_SDCARD_PATH = "sdcardPath";

	public static final String KEY_SETUP_SDCARD_PATH_PREFIX = KEY_SETUP_SDCARD_PATH + ".";

	public static final String KEY_SETUP_SDCARD_HERO_PATH = "sdcardHeroPath";

	public static final String KEY_DOWNLOAD_SCREEN = "downloadMediaScreen";
	public static final String KEY_DOWNLOAD_ALL = "downloadAll";
	public static final String KEY_DOWNLOAD_MAPS = "downloadMaps";
	public static final String KEY_DOWNLOAD_BACKGROUNDS = "downloadBackgrounds";
	public static final String KEY_DOWNLOAD_OSMMAPS = "downloadOSMMaps";
	public static final String KEY_DOWNLOAD_WESNOTH_PORTRAITS = "downloadWesnothPortraits";
	public static final String KEY_DOWNLOAD_ITEMS = "downloadItems";

	public static final String KEY_CREDITS = "credits";

	public static final String KEY_INFOS = "infos";
	public static final String KEY_DONATE = "donate";
	public static final String KEY_THEME = "theme";

	public static final String KEY_STYLE_BG_PATH = "theme.bg.path";
	public static final String KEY_STYLE_BG_DELETE = "theme.bg.delete";

	public static final String KEY_STYLE_BG_WOUNDS_PATH = "theme.wound.bg.path";
	public static final String KEY_STYLE_BG_WOUNDS_DELETE = "theme.wound.bg.delete";

	public static final String KEY_EXCHANGE = "heldenAustauschScreen";
	public static final String KEY_EXCHANGE_TOKEN = "exchange_token";

	public static final String KEY_SCREEN_ORIENTATION = "screen_orientation";

	public static final String KEY_TIP_TODAY = "tipToday";
	public static final String KEY_TIP_TODAY_RESET = "tipTodayReset";

	public static final String KEY_DSA_LICENSE = "dsa_license";

	public static final String KEY_HEADER_NAME = "header_name";
	public static final String KEY_HEADER_LE = "header_le";
	public static final String KEY_HEADER_AU = "header_au";
	public static final String KEY_HEADER_KE = "header_ke";
	public static final String KEY_HEADER_AE = "header_ae";
	public static final String KEY_HEADER_BE = "header_be";
	public static final String KEY_HEADER_MR = "header_mr";
	public static final String KEY_HEADER_GS = "header_gs";
	public static final String KEY_HEADER_WS = "header_ws";

	public static final String KEY_AUTO_SAVE = "hero_auto_save";
	public static final String KEY_DROPBOX = "dropbox";

	public static final String SCREEN_ORIENTATION_AUTO = "auto";
	public static final String SCREEN_ORIENTATION_LANDSCAPE = "landscape";
	public static final String SCREEN_ORIENTATION_PORTRAIT = "portrait";

	public static final String DEFAULT_SCREEN_ORIENTATION = SCREEN_ORIENTATION_AUTO;
	// http://dl.dropbox.com/u/15750588/dsatab-wesnoth-portraits.zip
	public static final String PATH_WESNOTH_PORTRAITS = "http://dl.dropboxusercontent.com/u/15750588/dsatab-wesnoth-portraits.zip";

	public static final String PATH_OFFICIAL_MAP_PACK = "http://dl.dropboxusercontent.com/u/15750588/dsatab-maps-v1.zip";

	public static final String PATH_OSM_MAP_PACK = "http://dl.dropboxusercontent.com/u/15750588/dsatab-osmmap-v1.zip";

	public static final String PATH_BACKGROUNDS = "http://dl.dropboxusercontent.com/u/15750588/dsatab-backgrounds.zip";

	private static final String[] RESTART_KEYS = { KEY_THEME };
	static {
		Arrays.sort(RESTART_KEYS);
	}

	private boolean restartRequired = false;

	public static void startPreferenceActivity(Activity context) {
		context.startActivityForResult(new Intent(context, DsaTabPreferenceActivity.class),
				DsaTabActivity.ACTION_PREFERENCES);

	}

	public static void initPreferences(final PreferenceManager mgr, final PreferenceScreen screen) {

		OnClickListener buttonClickListener = new OnClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {

				Preference preference = (Preference) v.getTag();

				if (preference != null) {
					if (preference.getKey().equals(KEY_STYLE_BG_PATH)) {
						handlePreferenceClick((Activity) v.getContext(), preference,
								DsaTabPreferenceActivity.KEY_STYLE_BG_DELETE, mgr.getSharedPreferences());
					} else if (preference.getKey().equals(KEY_STYLE_BG_WOUNDS_PATH)) {
						handlePreferenceClick((Activity) v.getContext(), preference,
								DsaTabPreferenceActivity.KEY_STYLE_BG_WOUNDS_DELETE, mgr.getSharedPreferences());
					}
				}
			}
		};

		PreferenceWithButton pref = (PreferenceWithButton) mgr.findPreference(KEY_STYLE_BG_PATH);
		if (pref != null) {
			pref.setButtonClickListener(buttonClickListener);
		}

		pref = (PreferenceWithButton) mgr.findPreference(KEY_STYLE_BG_WOUNDS_PATH);
		if (pref != null) {
			pref.setButtonClickListener(buttonClickListener);
		}

		ListPreference listPreference = (ListPreference) mgr.findPreference(KEY_ARMOR_TYPE);
		if (listPreference != null) {
			List<String> themeNames = new LinkedList<String>();
			List<String> themeValues = new LinkedList<String>();

			for (ArmorType themeValue : DsaTabConfiguration.ArmorType.values()) {
				themeNames.add(themeValue.title());
				themeValues.add(themeValue.name());
			}

			listPreference.setEntries(themeNames.toArray(new String[0]));
			listPreference.setEntryValues(themeValues.toArray(new String[0]));
		}

		listPreference = (ListPreference) mgr.findPreference(KEY_WOUND_TYPE);
		if (listPreference != null) {
			List<String> armorNames = new LinkedList<String>();
			List<String> armorValues = new LinkedList<String>();

			for (WoundType themeValue : DsaTabConfiguration.WoundType.values()) {
				armorNames.add(themeValue.title());
				armorValues.add(themeValue.name());
			}

			listPreference.setEntries(armorNames.toArray(new String[0]));
			listPreference.setEntryValues(armorValues.toArray(new String[0]));
		}

		Preference shakeDice = mgr.findPreference(KEY_PROBE_SHAKE_ROLL_DICE);
		if (shakeDice != null) {
			shakeDice.setEnabled(DsaTabApplication.getInstance().getPackageManager()
					.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER));
			if (!shakeDice.isEnabled()) {
				shakeDice.setSummary(R.string.message_sensor_not_supported_by_device);
			}
		}

		SharedPreferences sharedPreferences = mgr.getSharedPreferences();
		if (screen != null) {
			initPreferenceScreen(screen, sharedPreferences);
		}

	}

	private static void initPreferenceScreen(PreferenceGroup screen, SharedPreferences sharedPreferences) {

		final int count = screen.getPreferenceCount();

		for (int i = 0; i < count; i++) {
			Preference preference = screen.getPreference(i);

			if (preference instanceof PreferenceGroup) {
				initPreferenceScreen((PreferenceGroup) preference, sharedPreferences);
			} else {
				handlePreferenceChange(preference, sharedPreferences, preference.getKey());
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomPreferencesTheme());
		setHeaderRes(com.dsatab.R.xml.preferences_headers);

		super.onCreate(savedInstanceState);

		// TODO getSupportActionBar().setDisplayShowHomeEnabled(true);
		// TODO getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		SharedPreferences preferences = DsaTabApplication.getPreferences();
		preferences.registerOnSharedPreferenceChangeListener(this);

		updateFullscreenStatus(getWindow(), preferences.getBoolean(DsaTabPreferenceActivity.KEY_FULLSCREEN, true));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.saik0.android.unifiedpreference.UnifiedPreferenceActivity #onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (isSinglePane()) {
			initPreferences(getPreferenceManager(), getPreferenceScreen());
			onBindPreferenceSummariesToValues();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_OK);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.DsaTabPreferenceActivity#onSharedPreferenceChanged( android.content.SharedPreferences,
	 * java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		handlePreferenceChange(findPreference(key), sharedPreferences, key);

		if (KEY_FULLSCREEN.equals(key)) {
			updateFullscreenStatus(getWindow(), sharedPreferences.getBoolean(KEY_FULLSCREEN, true));
		}

		if (!restartRequired && Arrays.binarySearch(RESTART_KEYS, key) >= 0)
			restartRequired = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onPreferenceTreeClick(android. preference.PreferenceScreen,
	 * android.preference.Preference)
	 */
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		return handlePreferenceClick(this, preference, preference.getKey(), preference.getPreferenceManager()
				.getSharedPreferences());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		SharedPreferences preferences = DsaTabApplication.getPreferences();
		preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onStop() {
		if (restartRequired) {
			Toast.makeText(this, R.string.message_changes_require_restart_of_app, Toast.LENGTH_LONG).show();
		}
		super.onStop();
	}

	@Override
	protected boolean isValidFragment(String fragmentName) {
		return fragmentName.startsWith(DsaTabApplication.getInstance().getPackageName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == ACTION_PICK_BG_PATH) {
				File bg = Util.handleImagePick(this, KEY_STYLE_BG_PATH, data);

				if (bg != null) {
					SharedPreferences preferences = DsaTabApplication.getPreferences();
					Editor edit = preferences.edit();
					edit.putString(KEY_STYLE_BG_PATH, bg.getAbsolutePath());
					edit.commit();

					Toast.makeText(this, R.string.message_background_image_changed, Toast.LENGTH_SHORT).show();
				}
			} else if (requestCode == ACTION_PICK_BG_WOUNDS_PATH) {
				File bg = Util.handleImagePick(this, KEY_STYLE_BG_WOUNDS_PATH, data);
				if (bg != null) {
					SharedPreferences preferences = DsaTabApplication.getPreferences();
					Editor edit = preferences.edit();
					edit.putString(KEY_STYLE_BG_WOUNDS_PATH, bg.getAbsolutePath());
					edit.commit();

					Toast.makeText(this, R.string.message_background_image_changed, Toast.LENGTH_SHORT).show();
				}
			} else if (requestCode == REQUEST_LINK_TO_DBX) {
				SharedPreferences preferences = DsaTabApplication.getPreferences();
				Editor edit = preferences.edit();
				edit.putBoolean(KEY_DROPBOX, Boolean.TRUE);
				edit.commit();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected static void cleanOldFiles() {
		File cardsDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_CARDS);

		File[] dirs = cardsDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		if (dirs != null) {
			for (File f : dirs) {

				File[] children = f.listFiles();
				if (children != null) {
					for (File child : children) {
						child.delete();
					}
				}

				f.delete();
				Debug.verbose("Deleting " + f.getAbsolutePath());
			}
		}
	}

	protected static void updateFullscreenStatus(Window window, boolean bUseFullscreen) {
		if (bUseFullscreen) {
			window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		window.getDecorView().requestLayout();
	}

	public static boolean handlePreferenceClick(final Activity context, Preference preference, final String key,
			final SharedPreferences preferences) {
		AbstractDownloader downloader;
		if (KEY_DOWNLOAD_ALL.equals(key)) {
			cleanOldFiles();
			downloader = DownloaderWrapper.getInstance(DsaTabApplication.getDsaTabPath(), context);
			downloader.addPath(context.getString(R.string.path_items));
			downloader.addPath(PATH_WESNOTH_PORTRAITS);
			downloader.downloadZip();
			Toast.makeText(context, R.string.message_download_started_in_background, Toast.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_MAPS.equals(key)) {
			downloader = DownloaderWrapper.getInstance(DsaTabApplication.getDsaTabPath() + DsaTabApplication.DIR_MAPS,
					context);
			downloader.addPath(PATH_OFFICIAL_MAP_PACK);
			downloader.downloadZip();
			Toast.makeText(context, R.string.message_download_started_in_background, Toast.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_ITEMS.equals(key)) {
			cleanOldFiles();
			downloader = DownloaderWrapper.getInstance(DsaTabApplication.getDsaTabPath(), context);
			downloader.addPath(context.getString(R.string.path_items));
			downloader.downloadZip();
			Toast.makeText(context, R.string.message_download_started_in_background, Toast.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_WESNOTH_PORTRAITS.equals(key)) {
			downloader = DownloaderWrapper.getInstance(DsaTabApplication.getDsaTabPath(), context);
			downloader.addPath(PATH_WESNOTH_PORTRAITS);
			downloader.downloadZip();
			Toast.makeText(context, R.string.message_download_started_in_background, Toast.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_BACKGROUNDS.equals(key)) {
			downloader = DownloaderWrapper.getInstance(DsaTabApplication.getDsaTabPath(), context);
			downloader.addPath(PATH_BACKGROUNDS);
			downloader.downloadZip();
			Toast.makeText(context, R.string.message_download_started_in_background, Toast.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_OSMMAPS.equals(key)) {
			downloader = DownloaderWrapper.getInstance(DsaTabApplication.getDsaTabPath(), context);
			downloader.addPath(PATH_OSM_MAP_PACK);
			downloader.downloadZip();
			Toast.makeText(context, R.string.message_download_started_in_background, Toast.LENGTH_SHORT).show();
			return true;
		} else if (KEY_CREDITS.equals(key)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.title_credits);
			builder.setCancelable(true);
			WebView webView = new WebView(context);
			webView.getSettings().setDefaultTextEncodingName("utf-8");

			String summary = ResUtil.loadResToString(R.raw.credits, context);
			summary = summary.replace("{hs-version}", DsaTabApplication.HS_VERSION);
			webView.loadDataWithBaseURL(null, summary, "text/html", "utf-8", null);
			builder.setView(webView);
			builder.setNeutralButton(R.string.label_ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			return true;
		} else if (KEY_INFOS.equals(key)) {
			ChangeLogDialog logDialog = new ChangeLogDialog(context);
			logDialog.show(true);
			return true;
		} else if (KEY_DONATE.equals(key)) {
			Uri uriUrl = Uri.parse(DsaTabApplication.PAYPAL_DONATION_URL);
			final Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
			context.startActivity(launchBrowser);
			return true;
		} else if (KEY_DSA_LICENSE.equals(key)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.title_credits);
			builder.setCancelable(true);
			WebView webView = new WebView(context);
			webView.getSettings().setDefaultTextEncodingName("utf-8");
			String summary = ResUtil.loadResToString(R.raw.ulisses_license, context);
			webView.loadDataWithBaseURL(null, summary, "text/html", "utf-8", null);
			builder.setView(webView);
			builder.setNeutralButton(R.string.label_ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			return true;
		} else if (KEY_STYLE_BG_PATH.equals(key)) {
			Util.pickImage(context, ACTION_PICK_BG_PATH);
			return true;
		} else if (KEY_STYLE_BG_WOUNDS_PATH.equals(key)) {
			Util.pickImage(context, ACTION_PICK_BG_WOUNDS_PATH);
			return true;
		} else if (KEY_STYLE_BG_WOUNDS_DELETE.equals(key)) {
			Editor edit = preferences.edit();
			edit.remove(KEY_STYLE_BG_WOUNDS_PATH);
			edit.commit();

			Toast.makeText(context, R.string.message_background_image_reset, Toast.LENGTH_SHORT).show();
			return true;
		} else if (KEY_STYLE_BG_DELETE.equals(key)) {
			Editor edit = preferences.edit();
			edit.remove(KEY_STYLE_BG_PATH);
			edit.commit();

			Toast.makeText(context, R.string.message_background_image_reset, Toast.LENGTH_SHORT).show();
			return true;
		} else if (KEY_SETUP_SDCARD_PATH.equals(key)) {
			Result resultListener = new Result() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see com.dsatab.view.DirectoryChooserDialogHelper.Result# onChooseDirectory(java.lang.String)
				 */
				@Override
				public void onChooseDirectory(String dir) {

					File directory = new File(dir);
					if (directory.exists() && directory.canWrite()) {
						Editor edit = preferences.edit();
						edit.putString(KEY_SETUP_SDCARD_PATH, dir);
						edit.commit();
					} else {
						Toast.makeText(context, R.string.message_no_write_access_in_directory_choose_another,
								Toast.LENGTH_LONG).show();
					}
				}
			};
			new DirectoryChooserDialogHelper(context, resultListener, DsaTabApplication.getDsaTabHeroPath());
			return true;
		} else if (KEY_SETUP_SDCARD_HERO_PATH.equals(key)) {
			Result resultListener = new Result() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see com.dsatab.view.DirectoryChooserDialogHelper.Result# onChooseDirectory(java.lang.String)
				 */
				@Override
				public void onChooseDirectory(String dir) {
					File directory = new File(dir);
					if (directory.exists() && directory.canWrite()) {
						Editor edit = preferences.edit();
						edit.putString(KEY_SETUP_SDCARD_HERO_PATH, dir);
						edit.commit();
					} else {
						Toast.makeText(context, R.string.message_no_write_access_in_directory_choose_another,
								Toast.LENGTH_LONG).show();
					}
				}
			};
			new DirectoryChooserDialogHelper(context, resultListener, DsaTabApplication.getDsaTabHeroPath());
			return true;
		} else if (KEY_TIP_TODAY_RESET.equals(key)) {
			Editor edit = preferences.edit();
			for (String prefKey : preferences.getAll().keySet()) {
				if (prefKey.startsWith(Hint.PREF_PREFIX_HINT_STORAGE)) {
					edit.remove(prefKey);
				}
			}
			edit.commit();
			Toast.makeText(context, R.string.message_tips_reset, Toast.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DROPBOX.equals(key)) {
			DbxAccountManager mDbxAcctMgr = DbxAccountManager.getInstance(context.getApplicationContext(),
					DsaTabApplication.DROPBOX_API_KEY, DsaTabApplication.DROPBOX_API_SECRET);

			CheckBoxPreference cb = (CheckBoxPreference) preference;

			if (cb.isChecked() && !mDbxAcctMgr.hasLinkedAccount()) {
				Editor edit = preferences.edit();
				edit.putBoolean(key, false);
				edit.commit();
				cb.setChecked(false);
				mDbxAcctMgr.startLink(context, REQUEST_LINK_TO_DBX);
			}
			if (!cb.isChecked() && mDbxAcctMgr.hasLinkedAccount()) {
				mDbxAcctMgr.unlink();
			}

		}

		return false;

	}

	public static void handlePreferenceChange(Preference preference, SharedPreferences sharedPreferences, String key) {

		if (preference != null) {
			if (KEY_THEME.equals(key)) {

				String theme = DsaTabApplication.getInstance().getString(R.string.current_theme,
						DsaTabApplication.getInstance().getCustomThemeName());
				preference.setSummary(theme);
			} else if (KEY_STYLE_BG_PATH.equals(key)) {
				((PreferenceWithButton) preference)
						.setWidgetVisibility(sharedPreferences.contains(KEY_STYLE_BG_PATH) ? View.VISIBLE : View.GONE);
			} else if (KEY_STYLE_BG_WOUNDS_PATH.equals(key)) {
				((PreferenceWithButton) preference).setWidgetVisibility(sharedPreferences
						.contains(KEY_STYLE_BG_WOUNDS_PATH) ? View.VISIBLE : View.GONE);
			} else if (KEY_SETUP_SDCARD_PATH.equals(key)) {
				preference.setSummary(DsaTabApplication.getInstance().getString(R.string.pref_sdcardPath_description)
						+ ": " + sharedPreferences.getString(KEY_SETUP_SDCARD_PATH, DsaTabApplication.getDsaTabPath()));
			} else if (KEY_SETUP_SDCARD_HERO_PATH.equals(key)) {
				preference
						.setSummary(DsaTabApplication.getInstance().getString(R.string.pref_sdcardHeroPath_description)
								+ ": "
								+ sharedPreferences.getString(KEY_SETUP_SDCARD_HERO_PATH,
										DsaTabApplication.getDsaTabHeroPath()));
			} else if (KEY_DROPBOX.equals(key)) {
				CheckBoxPreference cb = (CheckBoxPreference) preference;
				boolean connected = HeroExchange.isConnected(DsaTabApplication.getInstance(), StorageType.Dropbox);
				if (sharedPreferences.getBoolean(KEY_DROPBOX, false) != connected) {
					Editor edit = sharedPreferences.edit();
					edit.putBoolean(KEY_DROPBOX, connected);
					edit.commit();
				}
				cb.setChecked(sharedPreferences.getBoolean(key, connected));
			}
		}
	}

	public static class PrefsSetupFragment extends BasePreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.dsatab.activity.DsaTabPreferenceActivity.BasePreferenceFragment #getPreferenceResourceId()
		 */
		@Override
		public int getPreferenceResourceId() {
			return R.xml.preferences_hc_setup;
		}
	}

	public static class PrefsDisplayFragment extends BasePreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.dsatab.activity.DsaTabPreferenceActivity.BasePreferenceFragment #getPreferenceResourceId()
		 */
		@Override
		public int getPreferenceResourceId() {
			return R.xml.preferences_hc_display;
		}
	}

	public static class PrefsHeaderFragment extends BasePreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.dsatab.activity.DsaTabPreferenceActivity.BasePreferenceFragment #getPreferenceResourceId()
		 */
		@Override
		public int getPreferenceResourceId() {
			return R.xml.preferences_hc_header;
		}
	}

	public static class PrefsDiceSliderFragment extends BasePreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.dsatab.activity.DsaTabPreferenceActivity.BasePreferenceFragment #getPreferenceResourceId()
		 */
		@Override
		public int getPreferenceResourceId() {
			return R.xml.preferences_hc_diceslider;
		}
	}

	public static class PrefsRulesFragment extends BasePreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.dsatab.activity.DsaTabPreferenceActivity.BasePreferenceFragment #getPreferenceResourceId()
		 */
		@Override
		public int getPreferenceResourceId() {
			return R.xml.preferences_hc_rules;
		}
	}

	public static class PrefsInfoFragment extends BasePreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.dsatab.activity.DsaTabPreferenceActivity.BasePreferenceFragment #getPreferenceResourceId()
		 */
		@Override
		public int getPreferenceResourceId() {
			return R.xml.preferences_hc_info;
		}
	}
}