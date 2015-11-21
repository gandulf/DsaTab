package com.dsatab.cloud;

import android.content.Context;
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

    public static StorageType[] storageTypes = new StorageType[]{StorageType.Drive, StorageType.Dropbox,
            StorageType.HeldenAustausch};

    public interface OnHeroExchangeListener {
        void onHeroInfoLoaded(List<HeroFileInfo> info);

        void onError(String errorMessage, Throwable exception);
    }

    public HeroExchange(Context context) {
        this.context = context;

        Task<Result<DrivenException>> task = new Task<Result<DrivenException>>() {
            @Override
            public void onCompleted(Result<DrivenException> result) {

            }
        };

        if (dropBox.hasSavedCredential(context)) {
            dropBox.authenticateAsync(context, task);
        }
        if (drive.hasSavedCredential(context)) {
            drive.authenticateAsync(context, task);
        }

        heldenAustausch.syncAuthentication(context);
        if (heldenAustausch.hasSavedCredential(context)) {
            heldenAustausch.authenticateAsync(context, task);
        }

        if (!TextUtils.isEmpty(DsaTabApplication.getExternalHeroPath())) {
            Credential credential = new Credential(context, DsaTabApplication.getExternalHeroPath());
            external.authenticateAsync(credential, task);
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

            Task<Result<DrivenException>> task = new Task<Result<DrivenException>>() {
                @Override
                public void onCompleted(Result<DrivenException> result) {

                }
            };
            provider.authenticateAsync(context, task);
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
        boolean result = false;
        StorageProvider storage = getProvider(fileInfo);
        if (storage!=null) {
            storage.delete(fileInfo.getPath(FileType.Hero));
            storage.delete(fileInfo.getPath(FileType.Config));
        }
        return result;
    }

    public void upload(HeroFileInfo heroInfo) throws IOException {
        StorageProvider storage = getProvider(heroInfo);
        if (storage != null) {
            LocalFile local = heroInfo.getLocalFile(FileType.Hero);
            RemoteFile remote = null;
            if (TextUtils.isEmpty(heroInfo.getRemoteHeroId())) {
                remote = storage.create(getBasePath(storage), local);
                heroInfo.setRemoteHeroId(remote.getId());
            } else {
                remote = storage.id(heroInfo.getRemoteHeroId());
                remote.upload(local);
            }

            LocalFile localConfig = heroInfo.getLocalFile(FileType.Config);
            RemoteFile remoteConfig = null;
            if (TextUtils.isEmpty(heroInfo.getRemoteConfigId())) {
                remoteConfig = storage.create(getBasePath(storage), localConfig);
                heroInfo.setRemoteConfigId(remoteConfig.getId());
            } else {
                remoteConfig = storage.id(heroInfo.getRemoteConfigId());
                remoteConfig.upload(localConfig);
            }

        }
    }

    public void download(HeroFileInfo heroInfo, OnHeroExchangeListener listener) {

        StorageProvider storage = getProvider(heroInfo);

        if (storage != null) {
            if (!TextUtils.isEmpty(heroInfo.getRemoteHeroId())) {
                LocalFile local = heroInfo.getLocalFile(FileType.Hero);
                RemoteFile remote = storage.id(heroInfo.getRemoteHeroId());
                remote.download(local);
            }

            if (!TextUtils.isEmpty(heroInfo.getRemoteConfigId())) {
                LocalFile localConfig = heroInfo.getLocalFile(FileType.Config);
                RemoteFile remoteConfig = storage.id(heroInfo.getRemoteConfigId());
                if (remoteConfig.download(localConfig)) {
                    File localConfigFile = localConfig.getFile();
                    try {
                        if (localConfigFile != null && localConfigFile.exists()) {
                            String data = Util.slurp(new FileInputStream(localConfigFile), 1024);
                            if (!TextUtils.isEmpty(data)) {
                                JSONObject jsonObject = new JSONObject(new String(data));
                                if (HeroConfiguration.updateStorageInfo(jsonObject,heroInfo.getStorageType(),heroInfo.getRemoteHeroId(),heroInfo.getRemoteConfigId())) {
                                    FileOutputStream fos = new FileOutputStream(localConfigFile);
                                    try {
                                        fos.write(jsonObject.toString().getBytes());
                                    } finally {
                                        Util.close(fos);
                                    }
                                    storage.update(remoteConfig,localConfig);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Debug.error(e);
                    } catch (IOException e) {
                        Debug.error(e);
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

            allSuccess &=result.isSuccess();
            if (result.getException()!=null) {
                exception = result.getException();
            }
            calls++;

            if (calls >= types.size()) {
                Result<DrivenException> allResult = new Result<>(allSuccess,exception);
                delegate.onCompleted(allResult);
            }
        }
    }

    public void connect(Context context, Task<Result<DrivenException>> task) {

        ConnectionTask allTasks = new ConnectionTask(Arrays.asList(storageTypes), task);

        for (StorageType type: storageTypes) {
            StorageProvider provider = getProvider(type);
            if (provider!=null && provider.hasSavedCredential(context)) {

                if (!provider.isAuthenticated()) {
                    provider.authenticateAsync(context, allTasks);
                } else {
                    doAsync(allTasks, new Delegate<Result<DrivenException>>() {
                        @Override
                        public Result<DrivenException> invoke() {
                            return new Result<>(true,null);
                        }
                    });
                }
            } else {
                doAsync(allTasks, new Delegate<Result<DrivenException>>() {
                    @Override
                    public Result<DrivenException> invoke() {
                        return new Result<>(true,null);
                    }
                });
            }
        }
    }

    public List<HeroFileInfo> getHeroes(StorageType... storageTypes) throws Exception {
        List<HeroFileInfo> heroes = new ArrayList<HeroFileInfo>();
        for (StorageType type : storageTypes) {
            heroes.addAll(getHeroesByType(type));
        }

        List<HeroFileInfo> result = new ArrayList<HeroFileInfo>();
        for (HeroFileInfo fileInfo : heroes) {
            int index = result.indexOf(fileInfo);
            if (index >= 0) {
                HeroFileInfo info = result.get(index);
                info.merge(fileInfo);
            } else {
                result.add(fileInfo);
            }
        }

        return result;
    }

    protected List<HeroFileInfo> getHeroesByType(StorageType storageType) throws Exception {
        List<HeroFileInfo> heroes = new ArrayList<HeroFileInfo>();

        StorageProvider storage = getProvider(storageType);
        if (storage.isAuthenticated()) {
            List<RemoteFile> files = storage.list(getBasePath(storage));

            if (files != null) {
                for (RemoteFile file : files) {
                    if (file.getName().toLowerCase(Locale.GERMAN).endsWith(HeroFileInfo.HERO_FILE_EXTENSION)) {

                        String configName = file.getName().replace(HeroFileInfo.HERO_FILE_EXTENSION,
                                HeroFileInfo.CONFIG_FILE_EXTENSION);
                        RemoteFile remoteConfig = storage.get(getBasePath(storage), configName);

                        HeroFileInfo info = new HeroFileInfo(file, remoteConfig, storageType, this);
                        download(info, null);
                        info.prepare(this);
                        heroes.add(info);
                    }
                }
            } else {
                Debug.warning("Unable to read directory " + getBasePath(storage).getName()
                        + ". Make sure the directory exists and contains your heroes");
            }
        }


        return heroes;
    }

    public enum StorageType {
        FileSystem, Dropbox, Drive, HeldenAustausch
    }
}
