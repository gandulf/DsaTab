package com.dsatab.cloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.bingzer.android.driven.Credential;
import com.bingzer.android.driven.DrivenException;
import com.bingzer.android.driven.LocalFile;
import com.bingzer.android.driven.RemoteFile;
import com.bingzer.android.driven.Result;
import com.bingzer.android.driven.StorageProvider;
import com.bingzer.android.driven.contracts.Task;
import com.bingzer.android.driven.dropbox.Dropbox;
import com.bingzer.android.driven.gdrive.GoogleDrive;
import com.bingzer.android.driven.local.ExternalDrive;
import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.util.Debug;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HeroExchange {

	public static final int RESULT_OK = 1;
	public static final int RESULT_ERROR = 2;
	public static final int RESULT_CANCELED = 3;
	public static final int RESULT_EMPTY = 4;

	private Context context;

	private StorageProvider dropBox = new Dropbox();
	private StorageProvider drive = new GoogleDrive();
	private StorageProvider external = new ExternalDrive();

	public static StorageType[] storageTypes = new StorageType[] { StorageType.Drive, StorageType.Dropbox,
			StorageType.HeldenAustausch };

	public interface OnHeroExchangeListener {
		public void onHeroInfoLoaded(List<HeroFileInfo> info);

		public void onError(String errorMessage, Throwable exception);

	};

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
				provider = null;
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

	public boolean isConnected(StorageType type) {
		switch (type) {
		case HeldenAustausch:
			return isConfigured();
		default:
			return getProvider(type).isAuthenticated();
		}
	}

	private boolean isConfigured() {
		final SharedPreferences preferences = DsaTabApplication.getPreferences();
		String token = preferences.getString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, "");
		return !TextUtils.isEmpty(token);
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

		switch (fileInfo.getStorageType()) {

		case HeldenAustausch:
			break;
		default:
			getProvider(fileInfo).delete(fileInfo.getPath(FileType.Hero));
			getProvider(fileInfo).delete(fileInfo.getPath(FileType.Config));
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
				remoteConfig.download(localConfig);
			}
		} else {
			if (isConfigured()) {
				final SharedPreferences preferences = DsaTabApplication.getPreferences();
				ImportHeroTask importFileTask = new ImportHeroTask(context, heroInfo, preferences.getString(
						DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, ""));
				importFileTask.setOnHeroExchangeListener(listener);
				importFileTask.execute();
			} else {
				Debug.warning("Heldenaustausch: Not configured skipping download");
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

		switch (storageType) {
		case HeldenAustausch:

			String token = DsaTabApplication.getPreferences()
					.getString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, "");

			if (!TextUtils.isEmpty(token)) {
				// HeldenListe anfordern
				String stringHeldenliste = Helper.postRequest(token, "action", "listhelden");

				Document d = Helper.string2Doc(stringHeldenliste);

				// Anzahl der Helden bestimmen
				int anzahl = Helper.getDaten(d, "/helden/held").getLength();
				// Die Namen der Helden anzeigen

				for (int i = 1; i <= anzahl; i++) {
					String name = Helper.getDatenAsString(d, "/helden/held[" + i + "]/name");
					String heldenid = Helper.getDatenAsString(d, "/helden/held[" + i + "]/heldenid");
					String heldenKey = Helper.getDatenAsString(d, "/helden/held[" + i + "]/heldenkey");
					String lastChange = Helper.getDatenAsString(d, "/helden/held[" + i + "]/heldlastchange");

					HeroFileInfo fileInfo = new HeroFileInfo(name, heldenid, heldenKey);
					heroes.add(fileInfo);
				}
			}
			break;
		default:
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
			break;
		}
		return heroes;
	}

	public enum StorageType {
		FileSystem, Dropbox, Drive, HeldenAustausch
	}
}
