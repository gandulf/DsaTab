package com.dsatab.fragment.preference;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.config.DsaTabConfiguration;
import com.dsatab.fragment.dialog.DirectoryChooserDialog;
import com.dsatab.util.Debug;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.view.PreferenceWithButton;
import com.gandulf.guilib.download.DownloaderGinger;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_ARMOR_TYPE;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_ALL;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_BACKGROUNDS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_ITEMS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_MAPS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_OSMMAPS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_WESNOTH_PORTRAITS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_PROBE_SHAKE_ROLL_DICE;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_SETUP_SDCARD_HERO_PATH;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_SETUP_SDCARD_PATH;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_STYLE_BG_DELETE;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_STYLE_BG_PATH;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_STYLE_BG_WOUNDS_DELETE;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_STYLE_BG_WOUNDS_PATH;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_THEME;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_TIP_TODAY_RESET;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_WOUND_TYPE;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.PATH_BACKGROUNDS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.PATH_OFFICIAL_MAP_PACK;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.PATH_OSM_MAP_PACK;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.PATH_WESNOTH_PORTRAITS;

public abstract class BasePreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    public interface DsaTabSettings {
         String KEY_PROBE_PROBABILITY = "probeProbability";
         String KEY_PROBE_SHAKE_ROLL_DICE = "shakeRollDice";
         String KEY_PROBE_ANIM_ROLL_DICE = "animRollDice";
         String KEY_PROBE_SOUND_ROLL_DICE = "soundRollDice";
         String KEY_PROBE_AUTO_ROLL_DICE = "autoRollDice";
         String KEY_PROBE_SOUND_RESULT_DICE = "soundResultDice";

         String KEY_HOUSE_RULES = "houseRules";
         String KEY_HOUSE_RULES_2_OF_3_DICE = "houseRules.2of3Dice";
         String KEY_HOUSE_RULES_EASIER_WOUNDS = "houseRules.easierWounds";
         String KEY_HOUSE_RULES_MORE_WOUND_ZONES = "houseRules.moreWoundZones";
         String KEY_HOUSE_RULES_MORE_TARGET_ZONES = "houseRules.moreTargetZones";

         String KEY_ARMOR_TYPE = "armorType";
         String KEY_WOUND_TYPE = "woundType";

        @Deprecated
         String KEY_SETUP_SDCARD_PATH = "sdcardPath";
         String KEY_CUSTOM_DIRECTORY = "sdcardPath";
         String DIR_PDFS = "pdfs";

         String KEY_SETUP_SDCARD_HERO_PATH = "sdcardHeroPath";

         String KEY_DOWNLOAD_ALL = "downloadAll";
         String KEY_DOWNLOAD_MAPS = "downloadMaps";
         String KEY_DOWNLOAD_BACKGROUNDS = "downloadBackgrounds";
         String KEY_DOWNLOAD_OSMMAPS = "downloadOSMMaps";
         String KEY_DOWNLOAD_WESNOTH_PORTRAITS = "downloadWesnothPortraits";
         String KEY_DOWNLOAD_ITEMS = "downloadItems";

         String KEY_THEME = "theme";

         String KEY_STYLE_BG_PATH = "theme.bg.path";
         String KEY_STYLE_BG_DELETE = "theme.bg.delete";

         String KEY_STYLE_BG_WOUNDS_PATH = "theme.wound.bg.path";
         String KEY_STYLE_BG_WOUNDS_DELETE = "theme.wound.bg.delete";

         String KEY_EXCHANGE_TOKEN = "exchange_token";

         String KEY_SCREEN_ORIENTATION = "screen_orientation";

         String KEY_TIP_TODAY = "tipToday";
         String KEY_TIP_TODAY_RESET = "tipTodayReset";

         String KEY_AUTO_SAVE = "hero_auto_save";

         String SCREEN_ORIENTATION_AUTO = "auto";
         String SCREEN_ORIENTATION_LANDSCAPE = "landscape";
         String SCREEN_ORIENTATION_PORTRAIT = "portrait";

         String DEFAULT_SCREEN_ORIENTATION = SCREEN_ORIENTATION_AUTO;
        // http://dl.dropbox.com/u/15750588/dsatab-wesnoth-portraits.zip
         String PATH_WESNOTH_PORTRAITS = "https://dl.dropboxusercontent.com/u/15750588/dsatab-wesnoth-portraits.zip";

         String PATH_OFFICIAL_MAP_PACK = "https://dl.dropboxusercontent.com/u/15750588/dsatab-maps-v1.zip";

         String PATH_OSM_MAP_PACK = "https://dl.dropboxusercontent.com/u/15750588/dsatab-osmmap-v1.zip";

         String PATH_BACKGROUNDS = "https://dl.dropboxusercontent.com/u/15750588/dsatab-backgrounds.zip";

    }

    static final int REQUEST_LINK_TO_DBX = 1190;
    public static final int ACTION_PICK_BG_PATH = 1001;
    public static final int ACTION_PICK_BG_WOUNDS_PATH = 1002;

	public abstract int getPreferenceResourceId();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceFragment#onPreferenceTreeClick(android .preference.PreferenceScreen,
	 * android.preference.Preference)
	 */
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (!TextUtils.isEmpty(preference.getFragment())) {
			try {
				BasePreferenceFragment fragment = (BasePreferenceFragment) Class.forName(preference.getFragment())
						.newInstance();
				((DsaTabPreferenceActivity) getActivity()).startPreferenceFragment(fragment, true);
				return true;
			} catch (Exception e) {
				Debug.error(e);
			}
			return false;
		} else {
			return handlePreferenceClick(this, preference, preference.getKey(), getPreferenceManager().getSharedPreferences());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(getPreferenceResourceId());

		initPreferences(getPreferenceManager(), getPreferenceScreen());

		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++)
			initSummary(getPreferenceScreen().getPreference(i));
	}

	private void initSummary(Preference pref) {
		if (pref instanceof PreferenceScreen) {
			final PreferenceScreen screen = (PreferenceScreen) pref;
			for (int i = 0; i < screen.getPreferenceCount(); i++)
				initSummary(screen.getPreference(i));
		}
		else if (pref instanceof PreferenceCategory) {
			final PreferenceCategory category = (PreferenceCategory) pref;
			for (int i = 0; i < category.getPreferenceCount(); i++)
				initSummary(category.getPreference(i));
		}
		else
			updatePrefSummary(pref);
	}

	private void updatePrefSummary(Preference pref) {
		if (pref instanceof ListPreference) {
			final ListPreference list = (ListPreference) pref;
			pref.setSummary(list.getEntry());
		}
		else if (pref instanceof EditTextPreference) {
			final EditTextPreference edit = (EditTextPreference) pref;
			if (!pref.getKey().equalsIgnoreCase("editKey"))
				pref.setSummary(edit.getText());
		}
	}

	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		handlePreferenceChange(findPreference(key), sharedPreferences, key);
		final Preference preference = findPreference(key);
		if (preference instanceof ListPreference) {
            preference.setSummary(((ListPreference) preference).getEntry());
        } else if (preference instanceof  CheckBoxPreference) {
            CheckBoxPreference checkPreference = (CheckBoxPreference) preference;
            checkPreference.setChecked(sharedPreferences.getBoolean(key,false));
        }
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ACTION_PICK_BG_PATH) {
                File bg = Util.handleImagePick(getActivity(), KEY_STYLE_BG_PATH, data);

                if (bg != null) {
                    SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString(KEY_STYLE_BG_PATH, bg.getAbsolutePath());
                    edit.apply();

                    ViewUtils.snackbar(this, R.string.message_background_image_changed, Snackbar.LENGTH_SHORT);
                }
            } else if (requestCode == ACTION_PICK_BG_WOUNDS_PATH) {
                File bg = Util.handleImagePick(getActivity(), KEY_STYLE_BG_WOUNDS_PATH, data);
                if (bg != null) {
                    SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString(KEY_STYLE_BG_WOUNDS_PATH, bg.getAbsolutePath());
                    edit.apply();

                    ViewUtils.snackbar(this, R.string.message_background_image_changed, Snackbar.LENGTH_SHORT);
                }
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

    public boolean handlePreferenceClick(final BasePreferenceFragment fragment, Preference preference, final String key,
                                                final SharedPreferences preferences) {
        DownloaderGinger downloader;
        Activity context = fragment.getActivity();
        if (KEY_DOWNLOAD_ALL.equals(key)) {
            cleanCardFiles();
            downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
            downloader.download(context.getString(R.string.path_items));
            downloader.download(PATH_WESNOTH_PORTRAITS);
            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_MAPS.equals(key)) {
            downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(DsaTabApplication.DIR_MAPS),
                    context);
            downloader.download(PATH_OFFICIAL_MAP_PACK);

            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_ITEMS.equals(key)) {
            cleanCardFiles();
            downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
            downloader.download(context.getString(R.string.path_items));
            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_WESNOTH_PORTRAITS.equals(key)) {
            downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
            downloader.download(PATH_WESNOTH_PORTRAITS);
            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_BACKGROUNDS.equals(key)) {
            downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
            downloader.download(PATH_BACKGROUNDS);
            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_OSMMAPS.equals(key)) {
            downloader = DownloaderGinger.getInstance(DsaTabApplication.getDirectory(), context);
            downloader.download(PATH_OSM_MAP_PACK);
            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_STYLE_BG_PATH.equals(key)) {
            Util.pickImage(this, ACTION_PICK_BG_PATH);
            return true;
        } else if (KEY_STYLE_BG_WOUNDS_PATH.equals(key)) {
            Util.pickImage(this, ACTION_PICK_BG_WOUNDS_PATH);
            return true;
        } else if (KEY_STYLE_BG_WOUNDS_DELETE.equals(key)) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.remove(KEY_STYLE_BG_WOUNDS_PATH);
            edit.apply();
            ViewUtils.snackbar(fragment, R.string.message_background_image_reset, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_STYLE_BG_DELETE.equals(key)) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.remove(KEY_STYLE_BG_PATH);
            edit.apply();
            ViewUtils.snackbar(fragment, R.string.message_background_image_reset, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_SETUP_SDCARD_PATH.equals(key)) {
            DirectoryChooserDialog.OnDirectoryChooserListener resultListener = new DirectoryChooserDialog.OnDirectoryChooserListener() {
                /*
                 * (non-Javadoc)
                 *
                 * @see com.dsatab.view.DirectoryChooserDialogHelper.Result# onChooseDirectory(java.lang.String)
                 */
                @Override
                public void onChooseDirectory(String dir) {

                    File directory = new File(dir);
                    if (directory.exists() && directory.canWrite()) {
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString(KEY_SETUP_SDCARD_PATH, dir);
                        edit.apply();
                    } else {
                        ViewUtils.snackbar(fragment, R.string.message_no_write_access_in_directory_choose_another,
                                Snackbar.LENGTH_LONG);
                    }
                }
            };

            DirectoryChooserDialog.show(null, fragment.getFragmentManager(), DsaTabApplication.getExternalHeroPath(),
                    resultListener, 0);

            return true;
        } else if (KEY_SETUP_SDCARD_HERO_PATH.equals(key)) {
            DirectoryChooserDialog.OnDirectoryChooserListener resultListener = new DirectoryChooserDialog.OnDirectoryChooserListener() {
                /*
                 * (non-Javadoc)
                 *
                 * @see com.dsatab.view.DirectoryChooserDialogHelper.Result# onChooseDirectory(java.lang.String)
                 */
                @Override
                public void onChooseDirectory(String dir) {
                    File directory = new File(dir);
                    if (directory.exists() && directory.canWrite()) {
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString(KEY_SETUP_SDCARD_HERO_PATH, dir);
                        edit.apply();
                    } else {
                        ViewUtils.snackbar(fragment, R.string.message_no_write_access_in_directory_choose_another,
                                Snackbar.LENGTH_LONG);
                    }
                }
            };
            DirectoryChooserDialog.show(null, fragment.getFragmentManager(), DsaTabApplication.getExternalHeroPath(),
                    resultListener, 0);
            return true;
        } else if (KEY_TIP_TODAY_RESET.equals(key)) {
            SharedPreferences.Editor edit = preferences.edit();
            for (String prefKey : preferences.getAll().keySet()) {
                if (prefKey.startsWith(Hint.PREF_PREFIX_HINT_STORAGE)) {
                    edit.remove(prefKey);
                }
            }
            edit.commit();
            ViewUtils.snackbar(fragment, R.string.message_tips_reset, Snackbar.LENGTH_SHORT);
            return true;
        }

        return false;

    }

    public void handlePreferenceChange(Preference preference, SharedPreferences sharedPreferences, String key) {

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
            }
        }
    }

    private void initPreferenceScreen(PreferenceGroup screen, SharedPreferences sharedPreferences) {

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

    public void initPreferences(final PreferenceManager mgr, final PreferenceScreen screen) {

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
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
                        handlePreferenceClick(BasePreferenceFragment.this, preference,
                                DsaTabPreferenceActivity.KEY_STYLE_BG_DELETE, mgr.getSharedPreferences());
                    } else if (preference.getKey().equals(KEY_STYLE_BG_WOUNDS_PATH)) {
                        handlePreferenceClick(BasePreferenceFragment.this, preference,
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

            for (DsaTabConfiguration.ArmorType themeValue : DsaTabConfiguration.ArmorType.values()) {
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

            for (DsaTabConfiguration.WoundType themeValue : DsaTabConfiguration.WoundType.values()) {
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
}