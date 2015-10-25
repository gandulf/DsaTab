package com.dsatab.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bingzer.android.driven.DrivenException;
import com.bingzer.android.driven.Result;
import com.bingzer.android.driven.StorageProvider;
import com.bingzer.android.driven.contracts.Task;
import com.bingzer.android.driven.dropbox.Dropbox;
import com.bingzer.android.driven.dropbox.app.DropboxActivity;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.config.DsaTabConfiguration;
import com.dsatab.config.DsaTabConfiguration.ArmorType;
import com.dsatab.config.DsaTabConfiguration.WoundType;
import com.dsatab.fragment.BasePreferenceFragment;
import com.dsatab.fragment.dialog.DirectoryChooserDialog;
import com.dsatab.fragment.dialog.DirectoryChooserDialog.OnDirectoryChooserListener;
import com.dsatab.util.Debug;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;
import com.dsatab.view.PreferenceWithButton;
import com.gandulf.guilib.download.DownloaderGinger;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DsaTabPreferenceActivity extends AppCompatPreferenceActivity implements OnSharedPreferenceChangeListener {

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
	public static final String KEY_HOUSE_RULES_EASIER_WOUNDS = "houseRules.easierWounds";
	public static final String KEY_HOUSE_RULES_MORE_WOUND_ZONES = "houseRules.moreWoundZones";
	public static final String KEY_HOUSE_RULES_MORE_TARGET_ZONES = "houseRules.moreTargetZones";

	public static final String KEY_ARMOR_TYPE = "armorType";
	public static final String KEY_WOUND_TYPE = "woundType";

	@Deprecated
	public static final String KEY_SETUP_SDCARD_PATH = "sdcardPath";
	public static final String KEY_CUSTOM_DIRECTORY = "sdcardPath";
	public static final String DIR_PDFS = "pdfs";

	public static final String KEY_SETUP_SDCARD_HERO_PATH = "sdcardHeroPath";

	public static final String KEY_DOWNLOAD_SCREEN = "downloadMediaScreen";
	public static final String KEY_DOWNLOAD_ALL = "downloadAll";
	public static final String KEY_DOWNLOAD_MAPS = "downloadMaps";
	public static final String KEY_DOWNLOAD_BACKGROUNDS = "downloadBackgrounds";
	public static final String KEY_DOWNLOAD_OSMMAPS = "downloadOSMMaps";
	public static final String KEY_DOWNLOAD_WESNOTH_PORTRAITS = "downloadWesnothPortraits";
	public static final String KEY_DOWNLOAD_ITEMS = "downloadItems";

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

	private Toolbar toolbar;

	private ViewGroup container;

	public static void startPreferenceActivity(Activity context) {
		context.startActivity(new Intent(context, DsaTabPreferenceActivity.class));
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
						handlePreferenceClick((DsaTabPreferenceActivity) v.getContext(), preference,
								DsaTabPreferenceActivity.KEY_STYLE_BG_DELETE, mgr.getSharedPreferences());
					} else if (preference.getKey().equals(KEY_STYLE_BG_WOUNDS_PATH)) {
						handlePreferenceClick((DsaTabPreferenceActivity) v.getContext(), preference,
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
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = DsaTabApplication.getPreferences();
		preferences.registerOnSharedPreferenceChangeListener(this);

	}

	private static int getResIdFromAttribute(final Activity activity, final int attr) {
		if (attr == 0) {
			return 0;
		}
		final TypedValue typedvalueattr = new TypedValue();
		activity.getTheme().resolveAttribute(attr, typedvalueattr, true);
		return typedvalueattr.resourceId;
	}

	/**
	 * Populate the activity with the top-level headers.
	 */
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(com.dsatab.R.xml.preferences_headers, target);

		setContentView(R.layout.main_preferences);

		ActionBar bar = getSupportActionBar();
		bar.setHomeButtonEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		bar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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
			Snackbar.make(getListView(), R.string.message_changes_require_restart_of_app, Snackbar.LENGTH_LONG).show();
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

					Snackbar.make(getListView(), R.string.message_background_image_changed, Snackbar.LENGTH_SHORT).show();
				}
			} else if (requestCode == ACTION_PICK_BG_WOUNDS_PATH) {
				File bg = Util.handleImagePick(this, KEY_STYLE_BG_WOUNDS_PATH, data);
				if (bg != null) {
					SharedPreferences preferences = DsaTabApplication.getPreferences();
					Editor edit = preferences.edit();
					edit.putString(KEY_STYLE_BG_WOUNDS_PATH, bg.getAbsolutePath());
					edit.commit();

					Snackbar.make(getListView(), R.string.message_background_image_changed, Snackbar.LENGTH_SHORT).show();
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

	protected static void cleanCardFiles() {
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

	public static boolean handlePreferenceClick(final DsaTabPreferenceActivity context, Preference preference, final String key,
			final SharedPreferences preferences) {
		DownloaderGinger downloader;
		if (KEY_DOWNLOAD_ALL.equals(key)) {
			cleanCardFiles();
			downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
			downloader.download(context.getString(R.string.path_items));
			downloader.download(PATH_WESNOTH_PORTRAITS);
			Snackbar.make(context.getListView(), R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_MAPS.equals(key)) {
			downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(DsaTabApplication.DIR_MAPS),
					context);
			downloader.download(PATH_OFFICIAL_MAP_PACK);
			Snackbar.make(context.getListView(), R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_ITEMS.equals(key)) {
			cleanCardFiles();
			downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
			downloader.download(context.getString(R.string.path_items));
			Snackbar.make(context.getListView(), R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_WESNOTH_PORTRAITS.equals(key)) {
			downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
			downloader.download(PATH_WESNOTH_PORTRAITS);
			Snackbar.make(context.getListView(), R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_BACKGROUNDS.equals(key)) {
			downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
			downloader.download(PATH_BACKGROUNDS);
			Snackbar.make(context.getListView(), R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DOWNLOAD_OSMMAPS.equals(key)) {
			downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
			downloader.download(PATH_OSM_MAP_PACK);
			Snackbar.make(context.getListView(), R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT).show();
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

			Snackbar.make(context.getListView(), R.string.message_background_image_reset, Snackbar.LENGTH_SHORT).show();
			return true;
		} else if (KEY_STYLE_BG_DELETE.equals(key)) {
			Editor edit = preferences.edit();
			edit.remove(KEY_STYLE_BG_PATH);
			edit.commit();

			Snackbar.make(context.getListView(), R.string.message_background_image_reset, Snackbar.LENGTH_SHORT).show();
			return true;
		} else if (KEY_SETUP_SDCARD_PATH.equals(key)) {
			OnDirectoryChooserListener resultListener = new OnDirectoryChooserListener() {
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
						Snackbar.make(context.getListView(), R.string.message_no_write_access_in_directory_choose_another,
								Snackbar.LENGTH_LONG).show();
					}
				}
			};

			DirectoryChooserDialog.show(null, context.getFragmentManager(), DsaTabApplication.getExternalHeroPath(),
					resultListener, 0);

			return true;
		} else if (KEY_SETUP_SDCARD_HERO_PATH.equals(key)) {
			OnDirectoryChooserListener resultListener = new OnDirectoryChooserListener() {
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
						Snackbar.make(context.getListView(), R.string.message_no_write_access_in_directory_choose_another,
								Snackbar.LENGTH_LONG).show();
					}
				}
			};
			DirectoryChooserDialog.show(null, context.getFragmentManager(), DsaTabApplication.getExternalHeroPath(),
					resultListener, 0);
			return true;
		} else if (KEY_TIP_TODAY_RESET.equals(key)) {
			Editor edit = preferences.edit();
			for (String prefKey : preferences.getAll().keySet()) {
				if (prefKey.startsWith(Hint.PREF_PREFIX_HINT_STORAGE)) {
					edit.remove(prefKey);
				}
			}
			edit.commit();
			Snackbar.make(context.getListView(), R.string.message_tips_reset, Snackbar.LENGTH_SHORT).show();
			return true;
		} else if (KEY_DROPBOX.equals(key)) {
			StorageProvider dropbox = new Dropbox();
			dropbox.authenticate(context);
			CheckBoxPreference cb = (CheckBoxPreference) preference;

			if (!dropbox.isAuthenticated()) {
				DropboxActivity
						.launch(context, DsaTabApplication.DROPBOX_API_KEY, DsaTabApplication.DROPBOX_API_SECRET);
			} else {
				dropbox.clearSavedCredential(context);
			}
			if (cb.isChecked() && !dropbox.isAuthenticated()) {
				Editor edit = preferences.edit();
				edit.putBoolean(key, false);
				edit.commit();
				cb.setChecked(false);

				DropboxActivity
						.launch(context, DsaTabApplication.DROPBOX_API_KEY, DsaTabApplication.DROPBOX_API_SECRET);
			}
			if (!cb.isChecked() && dropbox.isAuthenticated()) {
				dropbox.clearSavedCredential(context);
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
			} else if (KEY_SETUP_SDCARD_HERO_PATH.equals(key)) {
				preference.setSummary(DsaTabApplication.getInstance().getString(
						R.string.pref_sdcardHeroPath_description)
						+ ": "
						+ sharedPreferences.getString(KEY_SETUP_SDCARD_HERO_PATH,
								DsaTabApplication.getExternalHeroPath()));
			} else if (KEY_DROPBOX.equals(key)) {
				CheckBoxPreference cb = (CheckBoxPreference) preference;
				Dropbox dropbox = new Dropbox();
				Task<Result<DrivenException>> task = new Task<Result<DrivenException>>() {
					@Override
					public void onCompleted(Result<DrivenException> result) {

					}
				};
				if (dropbox.hasSavedCredential(DsaTabApplication.getInstance())) {
					dropbox.authenticateAsync(DsaTabApplication.getInstance(), task);
				}
				if (sharedPreferences.getBoolean(KEY_DROPBOX, false) != dropbox.isAuthenticated()) {
					Editor edit = sharedPreferences.edit();
					edit.putBoolean(KEY_DROPBOX, dropbox.isAuthenticated());
					edit.commit();
				}
				cb.setChecked(sharedPreferences.getBoolean(key, dropbox.isAuthenticated()));
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
}