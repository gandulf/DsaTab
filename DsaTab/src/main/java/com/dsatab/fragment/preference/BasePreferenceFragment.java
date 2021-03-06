package com.dsatab.fragment.preference;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.view.View;

import com.cloudrail.si.types.CloudMetaData;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.BaseActivity;
import com.dsatab.activity.DsaTabIntro;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.config.DsaTabConfiguration;
import com.dsatab.fragment.dialog.CloudDirectoryChooserDialog;
import com.dsatab.fragment.dialog.DirectoryChooserDialog;
import com.dsatab.util.Debug;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.util.download.Downloader;
import com.dsatab.view.PreferenceWithButton;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.dsatab.cloud.HeroExchange.StorageType.Drive;
import static com.dsatab.cloud.HeroExchange.StorageType.Dropbox;
import static com.dsatab.cloud.HeroExchange.StorageType.OneDrive;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_ARMOR_TYPE;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_ALL;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_BACKGROUNDS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_ITEMS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_MAPS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_OSMMAPS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DOWNLOAD_WESNOTH_PORTRAITS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DROPBOX_CHOOSE;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_DROPBOX_SYNC;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_GOOGLE_DRIVE_CHOOSE;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_GOOGLE_DRIVE_SYNC;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_INTRO;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_ONE_DRIVE_CHOOSE;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.KEY_ONE_DRIVE_SYNC;
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
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.PATH_ITEMS;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.PATH_OFFICIAL_MAP_PACK;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.PATH_OSM_MAP_PACK;
import static com.dsatab.fragment.preference.BasePreferenceFragment.DsaTabSettings.PATH_WESNOTH_PORTRAITS;

public class BasePreferenceFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener {

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

        String KEY_SCREEN_ORIENTATION = "screen_orientation";

        String KEY_TIP_TODAY = "tipToday";
        String KEY_TIP_TODAY_RESET = "tipTodayReset";

        String KEY_AUTO_SAVE = "hero_auto_save";

        String KEY_INTRO = "intro";

        String KEY_DROPBOX_SYNC = "dropbox_sync";
        String KEY_DROPBOX_CHOOSE = "dropbox_choose";

        String KEY_GOOGLE_DRIVE_SYNC = "google_drive_sync";
        String KEY_GOOGLE_DRIVE_CHOOSE = "google_drive_choose";

        String KEY_ONE_DRIVE_SYNC = "one_drive_sync";
        String KEY_ONE_DRIVE_CHOOSE = "one_drive_choose";

        String SCREEN_ORIENTATION_AUTO = "auto";
        String SCREEN_ORIENTATION_LANDSCAPE = "landscape";
        String SCREEN_ORIENTATION_PORTRAIT = "portrait";

        String DEFAULT_SCREEN_ORIENTATION = SCREEN_ORIENTATION_AUTO;

        String PATH_WESNOTH_PORTRAITS = "https://www.dropbox.com/s/qmn1adsajmlh88p/dsatab-wesnoth-portraits.zip?dl=1";
        String PATH_OFFICIAL_MAP_PACK = "https://www.dropbox.com/s/kgwtyd30btzna7h/dsatab-maps-v1.zip?dl=1";
        String PATH_OSM_MAP_PACK = "https://www.dropbox.com/s/xuf34kv2kzihpyt/dsatab-osmmap-v1.zip?dl=1";
        String PATH_BACKGROUNDS = "https://www.dropbox.com/s/0ecw8qe3444tmeq/dsatab-backgrounds.zip?dl=1";
        String PATH_ITEMS = "https://www.dropbox.com/s/8asis65zsf3lnki/dsatab-items3.zip?dl=1";

    }

    public static final int ACTION_PICK_BG_PATH = 1001;
    public static final int ACTION_PICK_BG_WOUNDS_PATH = 1002;

    private AsyncTask<Void,Void,Boolean> connectTask;

    public int getPreferenceResourceId() {
        return R.xml.preferences;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return handlePreferenceClick(this, preference, preference.getKey(), getPreferenceManager().getSharedPreferences());
    }

    public void connectCloud(final TwoStatePreference preference, final HeroExchange.StorageType storageType) {
        preference.setChecked(!preference.isChecked());
        HeroExchange.getInstance().isConnected(storageType, new HeroExchange.CloudResult<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result != null && result) {
                    if (getActivity()!=null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(storageType.toString());
                        builder.setMessage(storageType.toString() + " Synchronisation aufheben?");
                        builder.setPositiveButton("Aufheben", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HeroExchange.getInstance().disconnect(storageType, new HeroExchange.CloudResult<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        if (result != null && result) {
                                            ViewUtils.snackbar(getActivity(), storageType.toString() + " Verbindung getrennt");
                                            if (preference != null) {
                                                preference.setChecked(false);
                                            }
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                } else {
                    setToolbarRefreshing(true);
                    if (connectTask!=null && connectTask.getStatus() != AsyncTask.Status.FINISHED) {
                        connectTask.cancel(true);
                    }
                    connectTask = HeroExchange.getInstance().connect(storageType, new HeroExchange.CloudResult<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            setToolbarRefreshing(false);
                            if (result != null && result) {
                                ViewUtils.snackbar(getActivity(), storageType.toString() + " erfolgreich verbunden.");
                                if (preference != null) {
                                    preference.setChecked(true);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    protected  void setToolbarRefreshing(boolean value) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).setToolbarRefreshing(value);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(getPreferenceResourceId(), rootKey);
        initPreferences(getPreferenceManager(), getPreferenceScreen());

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++)
            initSummary(getPreferenceScreen().getPreference(i));
    }

    private void initSummary(Preference pref) {
        if (pref instanceof PreferenceScreen) {
            final PreferenceScreen screen = (PreferenceScreen) pref;
            for (int i = 0; i < screen.getPreferenceCount(); i++)
                initSummary(screen.getPreference(i));
        } else if (pref instanceof PreferenceCategory) {
            final PreferenceCategory category = (PreferenceCategory) pref;
            for (int i = 0; i < category.getPreferenceCount(); i++)
                initSummary(category.getPreference(i));
        } else
            updatePrefSummary(pref);
    }

    private void updatePrefSummary(Preference pref) {
        if (pref instanceof ListPreference) {
            final ListPreference list = (ListPreference) pref;
            pref.setSummary(list.getEntry());
        } else if (pref instanceof EditTextPreference) {
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
        } else if (preference instanceof TwoStatePreference) {
            TwoStatePreference checkPreference = (TwoStatePreference) preference;
            checkPreference.setChecked(sharedPreferences.getBoolean(key, false));
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
                Debug.v("Deleting " + f.getAbsolutePath());
            }
        }
    }

    public boolean handlePreferenceClick(final BasePreferenceFragment fragment, final Preference preference, final String key,
                                         final SharedPreferences preferences) {
        Downloader downloader;
        Activity context = fragment.getActivity();
        if (KEY_DOWNLOAD_ALL.equals(key)) {
            cleanCardFiles();
            downloader = Downloader.getInstance(DsaTabApplication.getDirectory(), context);
            downloader.download(PATH_ITEMS);
            downloader.download(PATH_WESNOTH_PORTRAITS);
            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_MAPS.equals(key)) {
            downloader = Downloader.getInstance(DsaTabApplication.getDirectory(DsaTabApplication.DIR_MAPS),
                    context);
            downloader.download(PATH_OFFICIAL_MAP_PACK);

            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_ITEMS.equals(key)) {
            cleanCardFiles();
            downloader = Downloader.getInstance(DsaTabApplication.getDirectory(), context);
            downloader.download(PATH_ITEMS);
            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_WESNOTH_PORTRAITS.equals(key)) {
            downloader = Downloader.getInstance(DsaTabApplication.getDirectory(), context);
            downloader.download(PATH_WESNOTH_PORTRAITS);
            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_BACKGROUNDS.equals(key)) {
            downloader = Downloader.getInstance(DsaTabApplication.getDirectory(), context);
            downloader.download(PATH_BACKGROUNDS);
            ViewUtils.snackbar(fragment, R.string.message_download_started_in_background, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_DOWNLOAD_OSMMAPS.equals(key)) {
            downloader = Downloader.getInstance(DsaTabApplication.getDirectory(), context);
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
                    if (TextUtils.isEmpty(dir)) {
                        ViewUtils.snackbar(fragment, R.string.message_no_directory_chosen,
                                Snackbar.LENGTH_LONG);
                    } else {
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
                    if (TextUtils.isEmpty(dir)) {
                        ViewUtils.snackbar(fragment, R.string.message_no_directory_chosen,
                                Snackbar.LENGTH_LONG);
                    } else {
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
            edit.apply();
            ViewUtils.snackbar(fragment, R.string.message_tips_reset, Snackbar.LENGTH_SHORT);
            return true;
        } else if (KEY_INTRO.equals(key)) {
            startActivity(new Intent(context, DsaTabIntro.class));
            return true;
        } else if (KEY_DROPBOX_SYNC.equals(key)) {
            connectCloud((TwoStatePreference) preference, HeroExchange.StorageType.Dropbox);
            return true;
        } else if (KEY_DROPBOX_CHOOSE.equals(key)) {
            CloudDirectoryChooserDialog.OnDirectoryChooserListener listener = new CloudDirectoryChooserDialog.OnDirectoryChooserListener() {
                @Override
                public void onChooseDirectory(CloudMetaData dirMetaData) {
                    HeroExchange.getInstance().setBaseDirectory(HeroExchange.StorageType.Dropbox, dirMetaData.getPath());
                    handlePreferenceChange(preference, preferences, KEY_DROPBOX_CHOOSE);
                }
            };
            CloudDirectoryChooserDialog.show(this, HeroExchange.getInstance().getBaseDirectory(HeroExchange.StorageType.Dropbox), HeroExchange.StorageType.Dropbox, listener, 0);
            return true;
        } else if (KEY_GOOGLE_DRIVE_SYNC.equals(key)) {
            connectCloud((TwoStatePreference) preference, HeroExchange.StorageType.Drive);
            return true;
        } else if (KEY_GOOGLE_DRIVE_CHOOSE.equals(key)) {
            CloudDirectoryChooserDialog.OnDirectoryChooserListener listener = new CloudDirectoryChooserDialog.OnDirectoryChooserListener() {
                @Override
                public void onChooseDirectory(CloudMetaData dirMetaData) {
                    HeroExchange.getInstance().setBaseDirectory(HeroExchange.StorageType.Drive, dirMetaData.getPath());
                    handlePreferenceChange(preference, preferences, KEY_GOOGLE_DRIVE_CHOOSE);
                }
            };
            CloudDirectoryChooserDialog.show(this, HeroExchange.getInstance().getBaseDirectory(HeroExchange.StorageType.Drive), HeroExchange.StorageType.Drive, listener, 0);
            return true;
        } else if (KEY_ONE_DRIVE_SYNC.equals(key)) {
            connectCloud((TwoStatePreference) preference, HeroExchange.StorageType.OneDrive);
            return true;
        } else if (KEY_ONE_DRIVE_CHOOSE.equals(key)) {
            CloudDirectoryChooserDialog.OnDirectoryChooserListener listener = new CloudDirectoryChooserDialog.OnDirectoryChooserListener() {
                @Override
                public void onChooseDirectory(CloudMetaData dirMetaData) {
                    HeroExchange.getInstance().setBaseDirectory(HeroExchange.StorageType.OneDrive, dirMetaData.getPath());
                    handlePreferenceChange(preference, preferences, KEY_ONE_DRIVE_CHOOSE);
                }
            };
            CloudDirectoryChooserDialog.show(this, HeroExchange.getInstance().getBaseDirectory(HeroExchange.StorageType.OneDrive), HeroExchange.StorageType.OneDrive, listener, 0);
            return true;
        }

        return false;

    }

    public String getCustomThemeName() {
        String theme = DsaTabApplication.getPreferences().getString(DsaTabPreferenceActivity.KEY_THEME, DsaTabApplication.THEME_DEFAULT);

        List<String> themeValues = Arrays.asList(getResources().getStringArray(R.array.themesValues));
        int index = themeValues.indexOf(theme);

        String[] themes = getResources().getStringArray(R.array.themes);
        if (index >= 0 && index < themes.length)
            return themes[index];
        else
            return themes[0];
    }

    public void handlePreferenceChange(final Preference preference, SharedPreferences sharedPreferences, String key) {

        if (preference != null) {
            if (KEY_THEME.equals(key)) {
                String theme = DsaTabApplication.getInstance().getString(R.string.current_theme,
                        getCustomThemeName());
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
            } else if (KEY_DROPBOX_SYNC.equals(key)) {
                HeroExchange.getInstance().isConnected(Dropbox, new HeroExchange.CloudResult<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result != null) {
                            ((TwoStatePreference) preference).setChecked(result);
                            findPreference(KEY_DROPBOX_CHOOSE).setEnabled(result);
                        }
                    }
                });
            } else if (KEY_DROPBOX_CHOOSE.equals(key)) {
                preference.setSummary(getString(R.string.pref_cloud_directory_choose, Dropbox.toString(), HeroExchange.getInstance().getBaseDirectory(HeroExchange.StorageType.Dropbox)));
            } else if (KEY_GOOGLE_DRIVE_SYNC.equals(key)) {
                HeroExchange.getInstance().isConnected(Drive, new HeroExchange.CloudResult<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result != null) {
                            ((TwoStatePreference) preference).setChecked(result);
                            findPreference(KEY_GOOGLE_DRIVE_CHOOSE).setEnabled(result);
                        }
                    }
                });
            } else if (KEY_GOOGLE_DRIVE_CHOOSE.equals(key)) {
                preference.setSummary(getString(R.string.pref_cloud_directory_choose, Drive.toString(), HeroExchange.getInstance().getBaseDirectory(HeroExchange.StorageType.Drive)));
            } else if (KEY_ONE_DRIVE_SYNC.equals(key)) {
                HeroExchange.getInstance().isConnected(OneDrive, new HeroExchange.CloudResult<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result != null) {
                            ((TwoStatePreference) preference).setChecked(result);
                            findPreference(KEY_ONE_DRIVE_CHOOSE).setEnabled(result);
                        }
                    }
                });
            } else if (KEY_ONE_DRIVE_CHOOSE.equals(key)) {
                preference.setSummary(getString(R.string.pref_cloud_directory_choose, OneDrive.toString(), HeroExchange.getInstance().getBaseDirectory(HeroExchange.StorageType.OneDrive)));
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