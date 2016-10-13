package com.dsatab.cloud;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.TextUtils;

import com.cloudrail.si.exceptions.AuthenticationException;
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

import static com.dsatab.cloud.HeroExchange.StorageType.Drive;
import static com.dsatab.data.HeroFileInfo.CONFIG_FILE_EXTENSION;
import static com.dsatab.data.HeroFileInfo.HERO_FILE_EXTENSION;

public class HeroExchange {

    public static final String BASE_DIRECTORY = "/dsatab";

    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = 2;
    public static final int RESULT_CANCELED = 3;
    public static final int RESULT_EMPTY = 4;

    private static HeroExchange ourInstance = new HeroExchange();

    private final AtomicReference<CloudStorage> dropbox = new AtomicReference<>();
    private final AtomicReference<CloudStorage> box = new AtomicReference<>();
    private final AtomicReference<CloudStorage> googledrive = new AtomicReference<>();
    private final AtomicReference<CloudStorage> onedrive = new AtomicReference<>();

    public static StorageType[] storageTypes = new StorageType[]{Drive, StorageType.Dropbox};

    public interface CloudResult<T> {
        void onSuccess(T result);
    }

    public interface CloudTask<T> {
        T execute(CloudStorage storage) throws Exception;
    }

    private Activity context;

    private HeroExchange() {
    }

    public static HeroExchange getInstance() {
        return ourInstance;
    }

    private CloudStorage getProvider(StorageType type, boolean initialise) {
        if (type== null) {
            return null;
        }
        CloudStorage storage = null;
        try {
            String persistent = getCredentialsKey(type);
            if (initialise || persistent != null) {
                switch (type) {
                    case Box:
                        storage = box.get();
                        if (storage==null) {
                            storage = new Box(context, "", "");
                            box.set(storage);
                        }
                        break;
                    case Drive:
                        storage = googledrive.get();
                        if (storage==null) {
                            storage = new GoogleDrive(context, "184938105279-r3u3pn6g7ple46ig6kjeisrultakomgh.apps.googleusercontent.com", "6KeO4m7BCCM3TL739LHj2G0v");
                            googledrive.set(storage);
                        }
                        break;
                    case Dropbox:
                        storage = dropbox.get();
                        if (storage==null) {
                            storage = new Dropbox(context, "8tbps3js3ufundc", "h61le6e0dcbs13x");
                            dropbox.set(storage);
                        }
                        break;
                    case OneDrive:
                        storage = onedrive.get();
                        if (storage==null) {
                            storage = new OneDrive(context, "", "");
                            onedrive.set(storage);
                        }
                        break;
                }
                if (persistent != null) {
                    storage.loadAsString(persistent);
                }
            }
        } catch (ParseException e) {
        }

        return storage;
    }

    // --------- Public Methods -----------
    public void prepare(Activity context) {
        this.context = context;
    }

    public void storePersistent() {
        SharedPreferences sharedPreferences = DsaTabApplication.getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (dropbox.get() != null)
            editor.putString(StorageType.Dropbox.getCredentialsKey(), dropbox.get().saveAsString());
        if (box.get() != null)
            editor.putString(StorageType.Box.getCredentialsKey(), box.get().saveAsString());
        if (googledrive.get() != null)
            editor.putString(Drive.getCredentialsKey(), googledrive.get().saveAsString());
        if (onedrive.get() != null)
            editor.putString(StorageType.OneDrive.getCredentialsKey(), onedrive.get().saveAsString());
        editor.commit();
    }

    private CloudStorage getProvider(HeroFileInfo fileInfo, boolean initialise) {
        return getProvider(fileInfo.getStorageType(), initialise);
    }

    protected boolean isConnected(StorageType type) {
        if (type == null)
            return false;

        String persistent = getCredentialsKey(type);
        if (persistent != null) {
            return true;
        } else {
            CloudStorage provider = getProvider(type, false);
            if (provider == null)
                return false;
            else {
                return provider.getUserLogin() != null;
            }
        }
    }

    public void isConnected(final StorageType type, CloudResult<Boolean> cloudResult) {
        if (type == null) {
            cloudResult.onSuccess(false);
            return;
        }

        String persistent = getCredentialsKey(type);
        if (persistent != null) {
            cloudResult.onSuccess(true);
        } else {
            CloudStorage storage = getProvider(type, false);
            if (storage == null) {
                cloudResult.onSuccess(false);
            } else {
                execute(storage, new CloudTask<Boolean>() {
                    @Override
                    public Boolean execute(CloudStorage storage) throws Exception {
                        return storage.getUserLogin() != null;
                    }
                }, cloudResult);
            }
        }
    }

    private String getCredentialsKey(StorageType type) {
        SharedPreferences sharedPreferences = DsaTabApplication.getPreferences();
        String key = sharedPreferences.getString(type.getCredentialsKey(), null);
        if (TextUtils.isEmpty(key) || "[{}]".equals(key))
            return null;
        else
            return key;
    }

    private CloudMetaData getBasePath(CloudStorage provider) {
        if (!provider.exists(BASE_DIRECTORY))
            provider.createFolder(BASE_DIRECTORY);

        return provider.getMetadata(BASE_DIRECTORY);
    }

    public void delete(final HeroFileInfo fileInfo, CloudResult<Boolean> result) {
        boolean success = false;
        File localHero = fileInfo.getFile(FileType.Hero);
        if (localHero != null && localHero.exists()) {
            success = localHero.delete();
        }

        File localConfig = fileInfo.getFile(FileType.Config);
        if (localConfig != null && localConfig.exists()) {
            success |= localConfig.delete();
        }

        if (fileInfo.getStorageType() != null) {
            final CloudStorage storage = getProvider(fileInfo, false);
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
        } else {
            result.onSuccess(success);
        }

    }

    private <T> void execute(final CloudStorage storage, final CloudTask<T> action, final CloudResult<T> cloudResult) {
        if (storage == null) {
            Debug.warning("Skipping storage task since provider was null");
            cloudResult.onSuccess(null);
            return;
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    T result;
                    try {
                        result = action.execute(storage);
                    } catch (final Exception e) {
                        result = null;
                        Debug.error(e);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ViewUtils.snackbar(context, "Fehler: " + e.getLocalizedMessage());
                            }
                        });
                    }

                    final T finalResult = result;
                    if (cloudResult != null) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cloudResult.onSuccess(finalResult);
                            }
                        });
                    }

                }
            });
        } else {
            T result;
            try {
                result = action.execute(storage);
            } catch (final Exception e) {
                Debug.error(e);
                result = null;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtils.snackbar(context, "Fehler: " + e.getLocalizedMessage());
                    }
                });
            }
            final T finalResult = result;
            if (cloudResult != null) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        cloudResult.onSuccess(finalResult);
                    }
                });
            }
        }
    }

    public void upload(final HeroFileInfo heroInfo, CloudResult<Boolean> result) throws IOException {
        execute(getProvider(heroInfo, false), new CloudTask<Boolean>() {
            @Override
            public Boolean execute(CloudStorage storage) throws IOException {
                return upload(storage, heroInfo, (CloudMetaData) null);
            }
        }, result);
    }

    private boolean upload(CloudStorage storage, HeroFileInfo heroInfo, CloudMetaData cloudMetaData) throws IOException {
        boolean result = false;
        if (storage != null) {
            File local = heroInfo.getFile(FileType.Hero);
            if (local != null && local.exists()) {
                Debug.verbose("Uploading hero file to" + heroInfo.getRemoteHeroId());
                storage.upload(heroInfo.getRemoteHeroId(), new FileInputStream(local), local.length(), true);

                // keep lastmodified of cloud data in sync with local files
                if (cloudMetaData == null)
                    cloudMetaData = storage.getMetadata(heroInfo.getRemoteHeroId());

                local.setLastModified(cloudMetaData.getModifiedAt());
                result = true;
            }

            File localConfig = heroInfo.getFile(FileType.Config);
            if (localConfig != null && localConfig.exists()) {
                if (heroInfo.getRemoteConfigId() == null) {
                    heroInfo.setRemoteConfigId(heroInfo.getRemoteHeroId().replace(HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION));
                }
                Debug.verbose("Uploading heroconfig file to" + heroInfo.getRemoteConfigId());
                storage.upload(heroInfo.getRemoteConfigId(), new FileInputStream(localConfig), localConfig.length(), true);
                result = true;
            }
        }
        return result;
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

    protected void synchronize(CloudStorage storage, HeroFileInfo heroInfo, CloudMetaData cloudMetaData) throws IOException, JSONException {
        if (storage != null) {
            if (!TextUtils.isEmpty(heroInfo.getRemoteHeroId())) {
                File local = heroInfo.getFile(FileType.Hero);
                if (local != null && local.exists()) {
                    Long localModified = local.lastModified();
                    ViewUtils.snackbar(context, heroInfo.getName() + ": Status überprüfen");

                    if (cloudMetaData == null) {
                        cloudMetaData = storage.getMetadata(heroInfo.getRemoteHeroId());
                    }
                    Long cloudModified = cloudMetaData.getModifiedAt();
                    if (localModified.equals(cloudModified)) {
                        Debug.verbose("No changes - " + heroInfo);
                    } else if (cloudModified != null && localModified > cloudModified) {
                        ViewUtils.snackbar(context, heroInfo.getName() + ": Uploading...");
                        Debug.verbose("Upload - " + heroInfo);
                        upload(storage, heroInfo, cloudMetaData);
                    } else if (cloudModified != null && localModified < cloudModified) {
                        ViewUtils.snackbar(context, heroInfo.getName() + ": Downloading...");
                        Debug.verbose("Download - " + heroInfo);
                        download(storage, heroInfo, cloudMetaData);
                    } else {
                        Debug.verbose("Skipping no cloud modified date - " + cloudMetaData);
                    }
                } else {
                    ViewUtils.snackbar(context, heroInfo.getName() + ": Downloading...");
                    Debug.verbose("Download New - " + heroInfo);
                    download(storage, heroInfo, cloudMetaData);
                }
            }
        }
    }

    public void download(final HeroFileInfo heroInfo, CloudResult<Boolean> result) throws IOException {
        execute(getProvider(heroInfo, false), new CloudTask<Boolean>() {
            @Override
            public Boolean execute(CloudStorage storage) throws IOException, JSONException {
                return download(storage, heroInfo, null);
            }
        }, result);
    }

    private boolean download(CloudStorage storage, HeroFileInfo heroInfo, CloudMetaData cloudMetaData) throws IOException, JSONException {
        boolean result = false;
        if (storage != null) {
            if (!TextUtils.isEmpty(heroInfo.getRemoteHeroId())) {
                File local = heroInfo.getFile(FileType.Hero);
                if (local != null) {
                    InputStream data = storage.download(heroInfo.getRemoteHeroId());
                    pipe(data, local);
                    // keep lastmodified of cloud data in sync with local files
                    if (cloudMetaData == null) {
                        cloudMetaData = storage.getMetadata(heroInfo.getRemoteHeroId());
                    }
                    local.setLastModified(cloudMetaData.getModifiedAt());
                    result = true;
                }
            }

            if (!TextUtils.isEmpty(heroInfo.getRemoteConfigId())) {
                File localConfig = heroInfo.getFile(FileType.Config);
                if (localConfig != null) {
                    InputStream dataConfig = storage.download(heroInfo.getRemoteConfigId());

                    pipe(dataConfig, localConfig);
                    result = true;
                    if (localConfig.exists()) {
                        String data = Util.slurp(new FileInputStream(localConfig), 1024);
                        if (!TextUtils.isEmpty(data)) {
                            JSONObject jsonObject = new JSONObject(data);
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
        return result;
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
        execute(getProvider(storageType, true), new CloudTask<Boolean>() {
            @Override
            public Boolean execute(CloudStorage storage) {
                storage.login();
                storePersistent();
                return storage.getUserName() != null;
            }
        }, result);
    }

    public void disconnect(final StorageType storageType, CloudResult<Boolean> result) {
        SharedPreferences sharedPreferences = DsaTabApplication.getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(storageType.getCredentialsKey());
        editor.apply();

        CloudStorage storage = getProvider(storageType, false);
        if (storage != null) {
            execute(storage, new CloudTask<Boolean>() {
                @Override
                public Boolean execute(CloudStorage storage) {
                    switch (storageType) {
                        case Dropbox:
                            dropbox.set(null);
                            break;
                        case Drive:
                            googledrive.set(null);
                            break;
                        case Box:
                            box.set(null);
                            break;
                        case OneDrive:
                            onedrive.set(null);
                            break;
                    }
                    try {
                        storage.logout();
                    } catch (AuthenticationException e) {
                        // fine since we wanted to logout anyway
                        Debug.verbose(e.getLocalizedMessage());
                    }
                    return Boolean.TRUE;
                }
            }, result);
        } else {
            result.onSuccess(Boolean.TRUE);
        }
    }

    protected List<HeroFileInfo> getHeroesByType(StorageType storageType) throws Exception {
        List<HeroFileInfo> heroes = new ArrayList<HeroFileInfo>();

        CloudStorage storage = getProvider(storageType, false);

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
                    HeroFileInfo info = new HeroFileInfo(file, storageType, this);
                    synchronize(storage, info, file);
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
        Dropbox("dropboxPersistent"), Drive("googledrivePersistent"), Box("boxPersistent"), OneDrive("onedrivePersistent");

        private String credentialsKey;

        StorageType(String credentialsKey) {
            this.credentialsKey = credentialsKey;
        }

        public java.lang.String getCredentialsKey() {
            return credentialsKey;
        }
    }
}
