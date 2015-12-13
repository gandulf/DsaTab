package com.dsatab.cloud;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.bingzer.android.driven.Credential;
import com.bingzer.android.driven.DrivenException;
import com.bingzer.android.driven.LocalFile;
import com.bingzer.android.driven.RemoteFile;
import com.bingzer.android.driven.Result;
import com.bingzer.android.driven.StorageProvider;
import com.bingzer.android.driven.contracts.Delegate;
import com.bingzer.android.driven.contracts.Task;
import com.bingzer.android.driven.dropbox.Dropbox;
import com.bingzer.android.driven.gdrive.GoogleDrive;
import com.bingzer.android.driven.local.ExternalDrive;
import com.bingzer.android.driven.utils.IOUtils;
import com.dsatab.DsaTabApplication;
import com.dsatab.data.HeroConfiguration;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.bingzer.android.driven.utils.AsyncUtils.doAsync;

public class HeroExchange {

    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = 2;
    public static final int RESULT_CANCELED = 3;
    public static final int RESULT_EMPTY = 4;

    private Context context;

    private StorageProvider dropBox = new Dropbox();
    private StorageProvider drive = new GoogleDrive();
    private StorageProvider external = new ExternalDrive();
    private HeldenAustauschProvider heldenAustausch = new HeldenAustauschProvider();


    private Task<Result<DrivenException>> EMPTY_TASK = new Task<Result<DrivenException>>() {
        @Override
        public void onCompleted(Result<DrivenException> result) {

        }
    };

    private Task<Boolean> EMPTY_BOOLEAN_TASK = new Task<Boolean>() {
        @Override
        public void onCompleted(Boolean result) {

        }
    };

    public static StorageType[] storageTypes = new StorageType[]{StorageType.Drive, StorageType.Dropbox,
            StorageType.HeldenAustausch};

    public interface OnHeroExchangeListener {
        void onHeroInfoLoaded(List<HeroFileInfo> info);

        void onError(String errorMessage, Throwable exception);
    }

    public HeroExchange(Context context) {
        this.context = context;


        if (dropBox.hasSavedCredential(context)) {
            dropBox.authenticateAsync(context, EMPTY_TASK);
        }
        if (drive.hasSavedCredential(context)) {
            drive.authenticateAsync(context, EMPTY_TASK);
        }

        heldenAustausch.syncAuthentication(context);
        if (heldenAustausch.hasSavedCredential(context)) {
            heldenAustausch.authenticateAsync(context, EMPTY_TASK);
        }

        if (!TextUtils.isEmpty(DsaTabApplication.getExternalHeroPath())) {
            Credential credential = new Credential(context, DsaTabApplication.getExternalHeroPath());
            external.authenticateAsync(credential, EMPTY_TASK);
        }

    }

    private StorageProvider getProvider(HeroFileInfo fileInfo) {
        return getProvider(fileInfo.getStorageType());
    }

    private StorageProvider getProvider(StorageType type) {
        StorageProvider provider = null;
        if (type != null) {
            switch (type) {
                case Drive:
                    provider = drive;
                    break;
                case Dropbox:
                    provider = dropBox;
                    break;
                case FileSystem:
                    provider = external;
                    break;
                case HeldenAustausch:
                    provider = heldenAustausch;
                    break;
            }
        }

        if (provider != null && !provider.isAuthenticated() && provider.hasSavedCredential(context)) {
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                Task<Result<DrivenException>> task = new Task<Result<DrivenException>>() {
                    @Override
                    public void onCompleted(Result<DrivenException> result) {

                    }
                };
                provider.authenticateAsync(context, task);
            } else {
                provider.authenticate(context);
            }
        }

        return provider;
    }

    public boolean isConnected(Context context, StorageType type) {
        return getProvider(type).hasSavedCredential(context);
    }

    private RemoteFile getBasePath(StorageProvider provider) {
        RemoteFile base = provider.get("dsatab");
        if (base == null || !base.isDirectory()) {
            base = provider.create("dsatab");
        }

        return base;
    }

    public boolean delete(HeroFileInfo fileInfo) {
        boolean result = true;

        File localHero = fileInfo.getFile(FileType.Hero);
        if (localHero != null && localHero.exists()) {
            result &= localHero.delete();
        }

        File localConfig = fileInfo.getFile(FileType.Config);
        if (localConfig != null && localConfig.exists()) {
            result &= localConfig.delete();
        }
        StorageProvider storage = getProvider(fileInfo);
        if (storage != null) {
            storage.deleteAsync(fileInfo.getPath(FileType.Hero), EMPTY_BOOLEAN_TASK);
            storage.deleteAsync(fileInfo.getPath(FileType.Config), EMPTY_BOOLEAN_TASK);
        }
        return result;
    }

    private void autoAuthenticate(StorageProvider storage) {
        if (!storage.isAuthenticated() && storage.hasSavedCredential(context)) {
            storage.authenticate(context);
        }
    }

    public void upload(HeroFileInfo heroInfo) throws IOException {
        StorageProvider storage = getProvider(heroInfo);
        if (storage != null) {
            LocalFile local = heroInfo.getLocalFile(FileType.Hero);
            RemoteFile remote;
            if (TextUtils.isEmpty(heroInfo.getRemoteHeroId())) {
                remote = storage.create(getBasePath(storage), local);
                if (remote != null) {
                    heroInfo.setRemoteHeroId(remote.getId());
                } else {
                    throw new IOException("Unable to upload hero to storage provider");
                }
            } else {
                remote = storage.id(heroInfo.getRemoteHeroId());
                if (remote != null) {
                    remote.upload(local);
                } else {
                    throw new IOException("Unable to find hero with id '" + heroInfo.getRemoteHeroId() + "' on storage provider");
                }
            }

            LocalFile localConfig = heroInfo.getLocalFile(FileType.Config);
            RemoteFile remoteConfig;
            if (TextUtils.isEmpty(heroInfo.getRemoteConfigId())) {
                remoteConfig = storage.create(getBasePath(storage), localConfig);
                if (remoteConfig != null) {
                    heroInfo.setRemoteConfigId(remoteConfig.getId());
                } else {
                    throw new IOException("Unable to upload hero config to storage provider");
                }
            } else {
                remoteConfig = storage.id(heroInfo.getRemoteConfigId());
                if (remoteConfig != null) {
                    remoteConfig.upload(localConfig);
                } else {
                    throw new IOException("Unable to find hero config with id '" + heroInfo.getRemoteConfigId() + "' on storage provider");
                }
            }

        }
    }

    public void download(HeroFileInfo heroInfo) {

        StorageProvider storage = getProvider(heroInfo);
        try {
            if (storage != null) {
                autoAuthenticate(storage);
                if (!TextUtils.isEmpty(heroInfo.getRemoteHeroId())) {
                    LocalFile local = heroInfo.getLocalFile(FileType.Hero);
                    if (local != null && local.getFile() != null) {
                        IOUtils.safeCreateDir(local.getFile().getParentFile());
                        RemoteFile remote = storage.id(heroInfo.getRemoteHeroId());
                        if (remote != null) {
                            remote.download(local);
                        } else {
                            throw new IOException("Unable to find hero with id '" + heroInfo.getRemoteHeroId() + "' on storage provider");
                        }
                    }
                }

                if (!TextUtils.isEmpty(heroInfo.getRemoteConfigId())) {
                    LocalFile localConfig = heroInfo.getLocalFile(FileType.Config);
                    if (localConfig != null && localConfig.getFile() != null) {
                        File localConfigFile = localConfig.getFile();
                        IOUtils.safeCreateDir(localConfigFile.getParentFile());
                        RemoteFile remoteConfig = storage.id(heroInfo.getRemoteConfigId());
                        if (remoteConfig == null) {
                            throw new IOException("Unable to find hero config with id '" + heroInfo.getRemoteConfigId() + "' on storage provider");
                        }
                        if (remoteConfig.download(localConfig)) {

                            if (localConfigFile.exists()) {
                                String data = Util.slurp(new FileInputStream(localConfigFile), 1024);
                                if (!TextUtils.isEmpty(data)) {
                                    JSONObject jsonObject = new JSONObject(new String(data));
                                    if (HeroConfiguration.updateStorageInfo(jsonObject, heroInfo.getStorageType(), heroInfo.getRemoteHeroId(), heroInfo.getRemoteConfigId())) {
                                        FileOutputStream fos = new FileOutputStream(localConfigFile);
                                        try {
                                            fos.write(jsonObject.toString().getBytes());
                                        } finally {
                                            Util.close(fos);
                                        }
                                        storage.update(remoteConfig, localConfig);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        } catch (JSONException e) {
            Debug.error(e);
        } catch (IOException e) {
            Debug.error(e);
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
        return getHeroes(storageTypes);
    }

    private static class ConnectionTask implements Task<Result<DrivenException>> {

        private List<StorageType> types;
        private int calls;

        private boolean allSuccess = true;
        private DrivenException exception;

        private Task<Result<DrivenException>> delegate;

        public ConnectionTask(List<StorageType> types, Task<Result<DrivenException>> task) {
            this.types = types;
            this.delegate = task;
        }

        @Override
        public void onCompleted(Result<DrivenException> result) {

            allSuccess &= result.isSuccess();
            if (result.getException() != null) {
                exception = result.getException();
            }
            calls++;

            if (calls >= types.size()) {
                Result<DrivenException> allResult = new Result<>(allSuccess, exception);
                delegate.onCompleted(allResult);
            }
        }
    }

    public void connect(Context context, Task<Result<DrivenException>> task) {

        ConnectionTask allTasks = new ConnectionTask(Arrays.asList(storageTypes), task);

        for (StorageType type : storageTypes) {
            StorageProvider provider = getProvider(type);
            if (provider != null && provider.hasSavedCredential(context)) {

                if (!provider.isAuthenticated()) {
                    provider.authenticateAsync(context, allTasks);
                } else {
                    doAsync(allTasks, new Delegate<Result<DrivenException>>() {
                        @Override
                        public Result<DrivenException> invoke() {
                            return new Result<>(true, null);
                        }
                    });
                }
            } else {
                doAsync(allTasks, new Delegate<Result<DrivenException>>() {
                    @Override
                    public Result<DrivenException> invoke() {
                        return new Result<>(true, null);
                    }
                });
            }
        }
    }

    public List<HeroFileInfo> getHeroes(StorageType... storageTypes) throws Exception {
        List<HeroFileInfo> heroes = new ArrayList<HeroFileInfo>();
        for (StorageType type : storageTypes) {
            HeroFileInfo.merge(heroes, getHeroesByType(type));
        }
        return heroes;
    }

    protected List<HeroFileInfo> getHeroesByType(StorageType storageType) throws Exception {
        List<HeroFileInfo> heroes = new ArrayList<HeroFileInfo>();

        StorageProvider storage = getProvider(storageType);
        if (storage != null && storage.isAuthenticated()) {
            RemoteFile basePath = getBasePath(storage);
            if (basePath == null) {
                Debug.warning("Couldn't create/read BasePath for storage type " + storageType
                        + ". Make sure the directory exists and contains your heroes");
                return heroes;
            }
            List<RemoteFile> files = storage.list(basePath);
            if (files != null) {
                for (RemoteFile file : files) {
                    if (file != null && file.getName().toLowerCase(Locale.GERMAN).endsWith(HeroFileInfo.HERO_FILE_EXTENSION)) {

                        String configName = file.getName().replace(HeroFileInfo.HERO_FILE_EXTENSION,
                                HeroFileInfo.CONFIG_FILE_EXTENSION);
                        RemoteFile remoteConfig = storage.get(getBasePath(storage), configName);

                        HeroFileInfo info = new HeroFileInfo(file, remoteConfig, storageType, this);
                        download(info);
                        info.prepare(this);
                        heroes.add(info);
                    }
                }
            } else {
                Debug.warning("Unable to read directory " + basePath.getName()
                        + ". Make sure the directory exists and contains your heroes");
            }
        }


        return heroes;
    }

    public enum StorageType {
        FileSystem, Dropbox, Drive, HeldenAustausch
    }
}
