package com.dsatab.cloud;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Document;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.data.HeroFileInfo.StorageType;
import com.dsatab.util.Debug;

public class HeroExchange {

	public static final int RESULT_OK = 1;
	public static final int RESULT_ERROR = 2;
	public static final int RESULT_CANCELED = 3;
	public static final int RESULT_EMPTY = 4;

	private Activity context;

	// dropbox
	private DbxAccountManager mDbxAcctMgr;
	private DbxPath dsatabBasePath;
	private Map<String, DbxFile> dbxFileMap;

	public interface OnHeroExchangeListener {
		public void onHeroInfoLoaded(List<HeroFileInfo> info);
	};

	public HeroExchange(Activity context) {
		this.context = context;
		dbxFileMap = new HashMap<String, DbxFile>();

		mDbxAcctMgr = DbxAccountManager.getInstance(context.getApplicationContext(), DsaTabApplication.DROPBOX_API_KEY,
				DsaTabApplication.DROPBOX_API_SECRET);
	}

	public boolean isConnected(StorageType type) {
		switch (type) {
		case Dropbox:
			return mDbxAcctMgr.hasLinkedAccount();
		case FileSystem:
			return true;
		case HeldenAustausch:
			String token = DsaTabApplication.getPreferences()
					.getString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, "");

			return !TextUtils.isEmpty(token);
		default:
			return false;
		}
	}

	private boolean isConfigured() {
		final SharedPreferences preferences = DsaTabApplication.getPreferences();
		String token = preferences.getString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, "");
		return !TextUtils.isEmpty(token);
	}

	public boolean syncDropbox(int requestCode) {
		if (!mDbxAcctMgr.hasLinkedAccount()) {
			mDbxAcctMgr.startLink(context, requestCode);
			return true;
		} else {
			return false;
		}
	}

	private DbxPath getDropboxBasePath() {
		if (dsatabBasePath == null)
			dsatabBasePath = new DbxPath("dsatab");

		return dsatabBasePath;
	}

	public boolean delete(HeroFileInfo fileInfo) {
		boolean result = false;

		switch (fileInfo.getStorageType()) {

		case FileSystem:
			File heroFile = new File(fileInfo.getPath(FileType.Hero));
			result |= heroFile.delete();

			File configFile = new File(fileInfo.getPath(FileType.Config));
			result |= configFile.delete();
			break;

		case Dropbox:
			if (mDbxAcctMgr != null && mDbxAcctMgr.hasLinkedAccount()) {
				try {
					DbxPath heroPath = new DbxPath(fileInfo.getPath(FileType.Hero));
					DbxPath configPath = new DbxPath(fileInfo.getPath(FileType.Hero));

					DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());

					if (dbxFs.exists(heroPath))
						dbxFs.delete(heroPath);
					if (dbxFs.exists(configPath))
						dbxFs.delete(configPath);

					result = true;
				} catch (Exception e) {
					Debug.error(e);
				}
			}
			break;
		}

		return result;
	}

	public void upload(StorageType type, HeroFileInfo fileInfo) throws DbxException, IOException {
		switch (type) {
		case Dropbox:
			if (mDbxAcctMgr != null && mDbxAcctMgr.hasLinkedAccount()) {
				uploadToDropbox(fileInfo);
			}
			break;
		case FileSystem:
			break;
		}
	}

	protected HeroFileInfo uploadToDropbox(HeroFileInfo fileInfo) throws DbxException, IOException {
		HeroFileInfo result = null;

		if (fileInfo.getStorageType() != StorageType.Dropbox) {
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());

			DbxFile dbxFile = null, dbxFileConfig = null;
			DbxPath heroPath = null, configPath = null;

			InputStream heroIs = getInputStream(fileInfo, FileType.Hero);
			if (heroIs != null) {
				heroPath = new DbxPath(getDropboxBasePath(), fileInfo.getName() + HeroFileInfo.HERO_FILE_EXTENSION);
				if (dbxFs.exists(heroPath)) {
					dbxFile = dbxFs.open(heroPath);
				} else {
					dbxFile = dbxFs.create(heroPath);
				}
				writeToDbxFile(dbxFile, heroIs);
				closeStream(fileInfo, FileType.Hero);
			}

			InputStream configIs = getInputStream(fileInfo, FileType.Config);
			if (configIs != null) {
				configPath = new DbxPath(getDropboxBasePath(), fileInfo.getName() + HeroFileInfo.CONFIG_FILE_EXTENSION);
				if (dbxFs.exists(configPath)) {
					dbxFileConfig = dbxFs.open(configPath);
				} else {
					dbxFileConfig = dbxFs.create(configPath);
				}

				writeToDbxFile(dbxFileConfig, configIs);
				closeStream(fileInfo, FileType.Config);
			}

			if (heroPath != null) {
				// delete the old file
				if (fileInfo.getStorageType() == StorageType.FileSystem) {
					delete(fileInfo);
				}

				result = new HeroFileInfo(heroPath, configPath, this);
			} else {
				result = null;
			}
		} else {
			result = fileInfo;
		}
		return result;
	}

	private void writeToDbxFile(DbxFile dbxFile, InputStream is) throws IOException {
		byte[] buffer = new byte[1024];

		OutputStream os = dbxFile.getWriteStream();
		int length = 0;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
		os.close();
		is.close();
		dbxFile.close();
	}

	public void download(HeroFileInfo heroInfo, OnHeroExchangeListener listener) {

		if (!checkSettings())
			return;

		final SharedPreferences preferences = DsaTabApplication.getPreferences();

		ImportHeroTaskNew importFileTask = new ImportHeroTaskNew(context, heroInfo, preferences.getString(
				DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, ""));
		importFileTask.setOnHeroExchangeListener(listener);
		importFileTask.execute();
	}

	private boolean checkSettings() {
		if (!isConfigured()) {

			Toast.makeText(context, R.string.message_insert_login_token_first, Toast.LENGTH_LONG).show();

			DsaTabPreferenceActivity.startPreferenceActivity(context);
			return false;
		} else
			return true;
	}

	public InputStream getInputStream(HeroFileInfo fileInfo, FileType fileType) {
		try {
			switch (fileInfo.getStorageType()) {
			case FileSystem:
				File file = new File(fileInfo.getPath(fileType));
				if (file.exists() && file.canRead()) {
					FileInputStream fis = new FileInputStream(file);
					return fis;
				} else {
					return null;
				}
			case Dropbox:
				DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
				DbxPath path = new DbxPath(fileInfo.getPath(fileType));
				if (dbxFs.exists(path)) {
					DbxFile dbxFile = dbxFs.open(path);

					dbxFileMap.put(path.toString(), dbxFile);

					return dbxFile.getReadStream();
				} else {
					return null;
				}
			case HeldenAustausch:

				if (fileType == FileType.Hero) {
					// nur für die Testphase, bis ein gültiges Zertifikate vorhanden ist
					Helper.disableSSLCheck();
					try {

						String token = DsaTabApplication.getPreferences().getString(
								DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, "");

						String stringheld = Helper.postRequest(token, "action", "returnheld", "format", "heldenxml",
								"heldenid", fileInfo.getId());

						ByteArrayInputStream bis = new ByteArrayInputStream(stringheld.getBytes());

						return bis;

					} catch (Exception e) {
						Debug.error(e);
						return null;
					}
				} else {
					// no config file for no
					return null;
				}
			default:
				return null;
			}
		} catch (Exception e) {
			Debug.error(e);
			return null;
		}
	}

	public void closeStream(HeroFileInfo fileInfo, FileType fileType) {
		switch (fileInfo.getStorageType()) {
		case FileSystem:
			break;
		case Dropbox:
			DbxPath path = new DbxPath(fileInfo.getPath(fileType));
			DbxFile file = dbxFileMap.remove(path.toString());
			if (file != null) {
				file.close();
			}
		}
	}

	public OutputStream getOutputStream(HeroFileInfo fileInfo, FileType fileType) {
		try {
			switch (fileInfo.getStorageType()) {
			case FileSystem: {
				File file = new File(fileInfo.getPath(fileType));
				FileOutputStream fis = new FileOutputStream(file);
				return fis;
			}
			case Dropbox: {
				DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
				DbxPath path = new DbxPath(fileInfo.getPath(fileType));
				DbxFile dbxFile;
				if (dbxFs.exists(path)) {
					dbxFile = dbxFs.open(path);
				} else {
					dbxFile = dbxFs.create(path);
				}
				dbxFileMap.put(path.toString(), dbxFile);
				return dbxFile.getWriteStream();
			}
			case HeldenAustausch: {
				File file = new File(fileInfo.getPath(fileType));
				FileOutputStream fis = new FileOutputStream(file);
				return fis;
			}
			default:
				return null;
			}
		} catch (Exception e) {
			Debug.error(e);
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
		heroes.addAll(getHeroes(StorageType.FileSystem));
		heroes.addAll(getHeroes(StorageType.Dropbox));
		heroes.addAll(getHeroes(StorageType.HeldenAustausch));

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

	public List<HeroFileInfo> getHeroes(StorageType storageType) throws Exception {
		List<HeroFileInfo> heroes = new ArrayList<HeroFileInfo>();

		switch (storageType) {
		case FileSystem: {
			File heroesDir = DsaTabApplication.getDsaTabHeroDirectory();
			File[] files = heroesDir.listFiles();
			if (files != null && heroesDir.exists() && heroesDir.canRead()) {
				for (File file : files) {
					if (file.isFile()
							&& file.getName().toLowerCase(Locale.GERMAN).endsWith(HeroFileInfo.HERO_FILE_EXTENSION)) {
						HeroFileInfo info = new HeroFileInfo(file, null, this);
						if (info != null) {
							heroes.add(info);
						}
					}
				}
			} else {
				Debug.warning("Unable to read directory " + heroesDir.getAbsolutePath()
						+ ". Make sure the directory exists and contains your heros");
			}
			break;
		}
		case Dropbox: {

			if (mDbxAcctMgr.hasLinkedAccount()) {
				DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());

				List<DbxFileInfo> files = dbxFs.listFolder(getDropboxBasePath());

				for (DbxFileInfo fileInfo : files) {
					if (fileInfo.path.getName().endsWith(HeroFileInfo.HERO_FILE_EXTENSION)) {
						HeroFileInfo info = new HeroFileInfo(fileInfo.path, null, this);

						if (info != null) {
							heroes.add(info);
						}
					}
				}
			}

			break;
		}
		case HeldenAustausch:

			String token = DsaTabApplication.getPreferences()
					.getString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, "");

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
				fileInfo.setStorageType(StorageType.HeldenAustausch);
				heroes.add(fileInfo);
			}

		}
		return heroes;
	}

}
