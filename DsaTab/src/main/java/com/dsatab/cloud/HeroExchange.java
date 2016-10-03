package com.dsatab.cloud;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.TextUtils;

import com.cloudrail.si.exceptions.NotFoundException;
import com.cloudrail.si.exceptions.ParseException;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Box;
import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.services.GoogleDrive;
import com.cloudrail.si.services.OneDrive;
import com.cloudrail.si.types.CloudMetaData;
import com.dsatab.DsaTabApplication;
import com.dsatab.data.HeroConfiguration;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static com.dsatab.data.HeroFileInfo.CONFIG_FILE_EXTENSION;
import static com.dsatab.data.HeroFileInfo.HERO_FILE_EXTENSION;

public class HeroExchange {

    public static final String BASE_DIRECTORY = "/dsatab";

    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = 2;
    public static final int RESULT_CANCELED = 3;
    public static final int RESULT_EMPTY = 4;
    public static final String DROPBOX_PERSISTENT = "dropboxPersistent";
    public static final String BOX_PERSISTENT = "boxPersistent";
    public static final String GOOGLEDRIVE_PERSISTENT = "googledrivePersistent";
    public static final String ONEDRIVE_PERSISTENT = "onedrivePersistent";

    private static HeroExchange ourInstance = new HeroExchange();

    private final AtomicReference<CloudStorage> dropbox = new AtomicReference<>();
    private final AtomicReference<CloudStorage> box = new AtomicReference<>();
    private final AtomicReference<CloudStorage> googledrive = new AtomicReference<>();
    private final AtomicReference<CloudStorage> onedrive = new AtomicReference<>();

    public static StorageType[] storageTypes = new StorageType[]{StorageType.Drive, StorageType.Dropbox};

    public interface OnHeroExchangeListener {
        void onHeroInfoLoaded(List<HeroFileInfo> info);

        void onError(String errorMessage, Throwable exception);
    }

    public interface CloudResult<T> {
        void onSuccess(T result);
    }

    public interface CloudTask<T> {
        T execute(CloudStorage storage);
    }

    private Activity context;

    private HeroExchange() {

    }

    public static HeroExchange getInstance() {
        return ourInstance;
    }

    private void initDropbox() {
        dropbox.set(new Dropbox(context, "8tbps3js3ufundc", "h61le6e0dcbs13x"));

        try {
            SharedPreferences sharedPreferences = DsaTabApplication.getInstance().getPreferences();
            String persistent = sharedPreferences.getString(DROPBOX_PERSISTENT, null);
            if (persistent != null) {
                dropbox.get().loadAsString(persistent);
            }
        } catch (ParseException e) {
        }
    }

    private void initBox() {
        box.set(new Box(context, "", ""));

        try {
            SharedPreferences sharedPreferences = DsaTabApplication.getInstance().getPreferences();
            String persistent = sharedPreferences.getString(BOX_PERSISTENT, null);
            if (persistent != null) {
                box.get().loadAsString(persistent);
            }
        } catch (ParseException e) {
        }
    }

    private void initGoogleDrive() {
        googledrive.set(new GoogleDrive(context, "184938105279-r3u3pn6g7ple46ig6kjeisrultakomgh.apps.googleusercontent.com", "6KeO4m7BCCM3TL739LHj2G0v"));

        try {
            SharedPreferences sharedPreferences = DsaTabApplication.getInstance().getPreferences();
            String persistent = sharedPreferences.getString(GOOGLEDRIVE_PERSISTENT, null);
            if (persistent != null) {
                googledrive.get().loadAsString(persistent);
            }
        } catch (ParseException e) {
        }
    }

    private void initOneDrive() {

        onedrive.set(new OneDrive(context, "", ""));

        try {
            SharedPreferences sharedPreferences = DsaTabApplication.getInstance().getPreferences();
            String persistent = sharedPreferences.getString(ONEDRIVE_PERSISTENT, null);
            if (persistent != null) {
                onedrive.get().loadAsString(persistent);
            }
        } catch (ParseException e) {
        }
    }

    // --------- Public Methods -----------
    public void prepare(Activity context) {
        this.context = context;
    }

    public void storePersistent() {
        SharedPreferences sharedPreferences = DsaTabApplication.getInstance().getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (dropbox.get() != null)
            editor.putString(DROPBOX_PERSISTENT, dropbox.get().saveAsString());
        if (box.get() != null)
            editor.putString(BOX_PERSISTENT, box.get().saveAsString());
        if (googledrive.get() != null)
            editor.putString(GOOGLEDRIVE_PERSISTENT, googledrive.get().saveAsString());
        if (onedrive.get() != null)
            editor.putString(ONEDRIVE_PERSISTENT, onedrive.get().saveAsString());
        editor.commit();
    }

    private CloudStorage getProvider(HeroFileInfo fileInfo) {
        return getProvider(fileInfo.getStorageType());
    }

    private CloudStorage getProvider(StorageType type) {
        AtomicReference<CloudStorage> ret = new AtomicReference<>();

        if (type != null) {
            switch (type) {
                case Drive:
                    if (googledrive.get() == null)
                        initGoogleDrive();
                    ret = googledrive;
                    break;
                case Dropbox:
                    if (dropbox.get() == null)
                        initDropbox();
                    ret = dropbox;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown service!");
            }
        }

        return ret.get();
    }

    protected boolean isConnected(StorageType type) {
        SharedPreferences sharedPreferences = DsaTabApplication.getInstance().getPreferences();
        String persistent = null;
        switch (type) {
            case Dropbox:
                persistent = sharedPreferences.getString(DROPBOX_PERSISTENT, null);
                if (persistent != null)
                    return true;
                else
                    return (googledrive.get() != null);
            case Drive:
                persistent = sharedPreferences.getString(GOOGLEDRIVE_PERSISTENT, null);
                if (persistent != null)
                    return true;
                else
                    return (googledrive.get() != null);
            default:
                return false;
        }
    }

    public void isConnected(StorageType type, CloudResult<Boolean> cloudResult) {
        boolean result = isConnected(type);
        cloudResult.onSuccess(result);
    }

    private CloudMetaData getBasePath(CloudStorage provider) {
        if (!provider.exists(BASE_DIRECTORY))
            provider.createFolder(BASE_DIRECTORY);

        return provider.getMetadata(BASE_DIRECTORY);
    }

    public void delete(final HeroFileInfo fileInfo, CloudResult<Boolean> result) {

        File localHero = fileInfo.getFile(FileType.Hero);
        if (localHero != null && localHero.exists()) {
            localHero.delete();
        }

        File localConfig = fileInfo.getFile(FileType.Config);
        if (localConfig != null && localConfig.exists()) {
            localConfig.delete();
        }

        final CloudStorage storage = getProvider(fileInfo);

        execute(storage, new CloudTask<Boolean>() {
            @Override
            public Boolean execute(CloudStorage storage) {
                if (fileInfo.getRemoteHeroId() != null)
                    storage.delete(fileInfo.getRemoteHeroId());
                if (fileInfo.getRemoteConfigId() != null)
                    storage.delete(fileInfo.getRemoteConfigId());
                return Boolean.TRUE;
            }
        }, result);

    }

    private <T> void execute(final CloudStorage storage, final CloudTask<T> action, final CloudResult<T> cloudResult) {
        if (storage == null) {
            Debug.warning("Skipping storage task since provider was null");
            return;
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    final T result = action.execute(storage);
                    if (cloudResult != null) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                cloudResult.onSuccess(result);

                            }
                        });
                    }

                }
            });
        } else {
            final T result = action.execute(storage);
            if (cloudResult != null) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        cloudResult.onSuccess(result);
                    }
                });
            }
        }
    }

    public void upload(final HeroFileInfo heroInfo, CloudResult<Boolean> result) throws IOException {
        execute(getProvider(heroInfo), new CloudTask<Boolean>() {
            @Override
            public Boolean execute(CloudStorage storage) {
                try {
                    upload(heroInfo,(CloudMetaData) null);
                } catch (IOException e) {
                    Debug.error(e);
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
        },result);
    }

    private void upload(HeroFileInfo heroInfo,CloudMetaData cloudMetaData) throws IOException {
        CloudStorage storage = getProvider(heroInfo);
        if (storage != null) {
            File local = heroInfo.getFile(FileType.Hero);
            if (local != null && local.exists()) {
                Debug.verbose("Uploading hero file to"+heroInfo.getRemoteHeroId());
                storage.upload(heroInfo.getRemoteHeroId(), new FileInputStream(local), local.length(), true);

                // keep lastmodified of cloud data in sync with local files
                if (cloudMetaData == null)
                    cloudMetaData = storage.getMetadata(heroInfo.getRemoteHeroId());

                local.setLastModified(cloudMetaData.getModifiedAt());
            }

            File localConfig = heroInfo.getFile(FileType.Config);
            if (localConfig != null && localConfig.exists()) {
                if (heroInfo.getRemoteConfigId() == null) {
                    heroInfo.setRemoteConfigId(heroInfo.getRemoteHeroId().replace(HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION));
                }
                Debug.verbose("Uploading heroconfig file to"+heroInfo.getRemoteConfigId());
                storage.upload(heroInfo.getRemoteConfigId(), new FileInputStream(localConfig), localConfig.length(), true);
            }
        }
    }

    private void pipe(InputStream in, File file) throws IOException {
        OutputStream out = null;
        try {
            boolean result = file.getParentFile().mkdirs();

            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            Util.close(out);
            Util.close(in);
        }

    }

    protected void synchronize(HeroFileInfo heroInfo, CloudMetaData cloudMetaData) throws IOException, JSONException {
        CloudStorage storage = getProvider(heroInfo);

        if (storage != null) {
            if (!TextUtils.isEmpty(heroInfo.getRemoteHeroId())) {
                File local = heroInfo.getFile(FileType.Hero);
                if (local != null && local.exists()) {
                    Long localModified = local.lastModified();
                    ViewUtils.snackbar(context,heroInfo.getName()+": Status überprüfen");

                    if (cloudMetaData == null) {
                        cloudMetaData = storage.getMetadata(heroInfo.getRemoteHeroId());
                    }
                    Long cloudModified = cloudMetaData.getModifiedAt();
                    if (localModified.equals(cloudModified)) {
                        Debug.verbose("No changes - " + heroInfo);
                        return;
                    } else if (cloudModified!=null && localModified > cloudModified) {
                        ViewUtils.snackbar(context,heroInfo.getName()+": Uploading...");
                        Debug.verbose("Upload - " + heroInfo);
                        upload(heroInfo,cloudMetaData);
                    } else if (cloudModified!=null && localModified < cloudModified){
                        ViewUtils.snackbar(context,heroInfo.getName()+": Downloading...");
                        Debug.verbose("Download - " + heroInfo);
                        download(heroInfo,cloudMetaData);
                    }else {
                        Debug.verbose("Skipping no cloud modified date - " + cloudMetaData);
                    }
                } else {
                    ViewUtils.snackbar(context,heroInfo.getName()+": Downloading...");
                    Debug.verbose("Download New - " + heroInfo);
                    download(heroInfo,cloudMetaData);
                }
            }
        }
    }

    public void download(final HeroFileInfo heroInfo, CloudResult<Boolean> result) throws IOException {
        execute(getProvider(heroInfo), new CloudTask<Boolean>() {
            @Override
            public Boolean execute(CloudStorage storage) {
                try {
                    download(heroInfo,(CloudMetaData) null);
                } catch (IOException e) {
                    Debug.error(e);
                    return Boolean.FALSE;
                } catch (JSONException e) {
                    Debug.error(e);
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
        },result);
    }

    private void download(HeroFileInfo heroInfo,CloudMetaData cloudMetaData) throws IOException, JSONException {
        CloudStorage storage = getProvider(heroInfo);

        if (storage != null) {
            if (!TextUtils.isEmpty(heroInfo.getRemoteHeroId())) {
                File local = heroInfo.getFile(FileType.Hero);
                if (local != null) {
                    InputStream data = storage.download(heroInfo.getRemoteHeroId());
                    pipe(data, local);
                    // keep lastmodified of cloud data in sync with local files
                    if (cloudMetaData==null) {
                        cloudMetaData = storage.getMetadata(heroInfo.getRemoteHeroId());
                    }
                    local.setLastModified(cloudMetaData.getModifiedAt());
                }
            }

            if (!TextUtils.isEmpty(heroInfo.getRemoteConfigId())) {
                File localConfig = heroInfo.getFile(FileType.Config);
                if (localConfig != null) {
                    InputStream dataConfig = storage.download(heroInfo.getRemoteConfigId());

                    pipe(dataConfig, localConfig);

                    if (localConfig.exists()) {
                        String data = Util.slurp(new FileInputStream(localConfig), 1024);
                        if (!TextUtils.isEmpty(data)) {
                            JSONObject jsonObject = new JSONObject(new String(data));
                            if (HeroConfiguration.updateStorageInfo(jsonObject, heroInfo.getStorageType(), heroInfo.getRemoteHeroId(), heroInfo.getRemoteConfigId())) {
                                FileOutputStream fos = new FileOutputStream(localConfig);
                                try {
                                    fos.write(jsonObject.toString().getBytes());
                                } finally {
                                    Util.close(fos);
                                }
                                storage.upload(heroInfo.getRemoteConfigId(), new FileInputStream(localConfig), localConfig.length(), true);
                            }
                        }
                    }
                }
            }
        }

    }

    public InputStream getInputStream(HeroFileInfo fileInfo, FileType fileType) throws IOException {
        File file = fileInfo.getFile(fileType);
        if (file != null && file.exists() && file.canRead()) {
            return new FileInputStream(file);
        } else {
            return null;
        }
    }

    public OutputStream getOutputStream(HeroFileInfo fileInfo, FileType fileType) throws IOException {
        File file = fileInfo.getFile(fileType);
        if (file != null) {
            return new FileOutputStream(file);
        } else {
            return null;
        }
    }

    /**
     * Should not be called from UI thread
     *
     * @return
     * @throws Exception
     */
    public List<HeroFileInfo> getHeroes() throws Exception {
        List<HeroFileInfo> heroes = new ArrayList<HeroFileInfo>();
        for (StorageType type : storageTypes) {
            if (isConnected(type)) {
                HeroFileInfo.merge(heroes, getHeroesByType(type));
            }
        }
        return heroes;
    }

    public void connect(StorageType storageType, CloudResult<Boolean> result) {
        execute(getProvider(storageType), new CloudTask<Boolean>() {
            @Override
            public Boolean execute(CloudStorage storage) {
                storage.login();
                return storage.getUserName() != null;
            }
        }, result);
    }

    public void disconnect(final StorageType storageType) {
        execute(getProvider(storageType), new CloudTask<Void>() {
            @Override
            public Void execute(CloudStorage storage) {
                SharedPreferences sharedPreferences = DsaTabApplication.getInstance().getPreferences();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (storageType) {
                    case Dropbox:
                        dropbox.set(null);
                        editor.remove(DROPBOX_PERSISTENT);
                        break;
                    case Drive:
                        googledrive.set(null);
                        editor.remove(GOOGLEDRIVE_PERSISTENT);
                        break;
                }
                editor.commit();

                storage.logout();
                return null;
            }
        }, null);

    }

    protected List<HeroFileInfo> getHeroesByType(StorageType storageType) throws Exception {
        List<HeroFileInfo> heroes = new ArrayList<HeroFileInfo>();

        CloudStorage storage = getProvider(storageType);

        CloudMetaData basePath = getBasePath(storage);
        if (basePath == null) {
            Debug.warning("Couldn't create/read BasePath for storage type " + storageType
                    + ". Make sure the directory exists and contains your heroes");
            return heroes;
        }

        List<CloudMetaData> files = storage.getChildren(BASE_DIRECTORY);
        if (files != null) {
            for (CloudMetaData file : files) {
                if (file != null && file.getName().toLowerCase(Locale.GERMAN).endsWith(HERO_FILE_EXTENSION)) {

                    String configName = file.getName().replace(HERO_FILE_EXTENSION,
                            CONFIG_FILE_EXTENSION);

                    CloudMetaData remoteConfig = null;
                    try {
                        remoteConfig = storage.getMetadata(BASE_DIRECTORY + "/" + configName);
                    } catch (NotFoundException e) {
                        remoteConfig = null;
                    }

                    HeroFileInfo info = new HeroFileInfo(file, remoteConfig, storageType, this);
                    synchronize(info, file);
                    info.prepare(this);
                    heroes.add(info);
                }
            }
        } else {
            Debug.warning("Unable to read directory " + basePath.getName()
                    + ". Make sure the directory exists and contains your heroes");
        }


        return heroes;
    }

    public enum StorageType {
        FileSystem, Dropbox, Drive
    }
}
