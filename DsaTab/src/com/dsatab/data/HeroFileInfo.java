package com.dsatab.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.bingzer.android.driven.LocalFile;
import com.bingzer.android.driven.RemoteFile;
import com.dsatab.DsaTabApplication;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.cloud.HeroExchange.StorageType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.xml.HeldenXmlParser;
import com.dsatab.xml.Xml;

public class HeroFileInfo implements JSONable, Serializable {

	public static final String HERO_FILE_EXTENSION = ".xml";
	public static final String CONFIG_FILE_EXTENSION = ".json";

	private static final long serialVersionUID = 2900488398093511704L;

	public enum FileType {
		Hero, Config
	}

	private static final String JSON_NAME = "name";
	private static final String JSON_KEY = "key";
	private static final String JSON_ID = "id";
	private static final String JSON_FILE = "file";
	private static final String JSON_FILE_CONFIG = "fileConfig";
	private static final String JSON_HERO_REMOTE = "heroRemote";
	private static final String JSON_CONFIG_REMOTE = "configRemote";
	private static final String JSON_PORTRAIT_URI = "portraitUri";
	private static final String JSON_STORAGE_TYPE = "storageType";
	private static final String JSON_VERSION = "version";

	private String name;

	private String key;

	private String id;

	private String version;

	private StorageType storageType;

	private String remoteHeroId;
	private String remoteConfigId;

	private File file;
	private File fileConfig;

	private String portraitUri;

	public HeroFileInfo(JSONObject json) throws IllegalArgumentException {

		name = json.optString(JSON_NAME);
		key = json.optString(JSON_KEY);
		id = json.optString(JSON_ID);
		version = json.optString(JSON_VERSION);
		storageType = StorageType.valueOf(json.optString(JSON_STORAGE_TYPE, StorageType.FileSystem.name()));

		if (json.has(JSON_FILE))
			file = new File(json.optString(JSON_FILE));
		else
			file = new File(DsaTabApplication.getInternalHeroDirectory(), getId() + HeroFileInfo.HERO_FILE_EXTENSION);

		if (json.has(JSON_FILE_CONFIG))
			fileConfig = new File(json.optString(JSON_FILE_CONFIG));
		else
			fileConfig = new File(file.getAbsolutePath().replace(HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION));

		remoteHeroId = json.optString(JSON_HERO_REMOTE);
		remoteConfigId = json.optString(JSON_CONFIG_REMOTE);

		portraitUri = json.optString(JSON_PORTRAIT_URI);

	}

	public HeroFileInfo(File internalHeroFile, File internalConfigFile, HeroExchange exchange)
			throws IllegalArgumentException {

		storageType = null;
		file = internalHeroFile;
		fileConfig = internalConfigFile;

		if (fileConfig == null) {
			fileConfig = new File(file.getAbsolutePath().replace(HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION));
		}

		prepare(exchange);
	}

	public HeroFileInfo(RemoteFile heroFile, RemoteFile configFile, StorageType storageType, HeroExchange exchange)
			throws IllegalArgumentException {

		this.storageType = storageType;

		remoteHeroId = heroFile.getId();

		if (configFile != null) {
			remoteConfigId = configFile.getId();
		}

		file = new File(DsaTabApplication.getInternalHeroDirectory(), heroFile.getName());
		if (configFile != null) {
			fileConfig = new File(DsaTabApplication.getInternalHeroDirectory(), configFile.getName());
		} else {
			fileConfig = new File(DsaTabApplication.getInternalHeroDirectory(), heroFile.getName().replace(
					HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION));
		}

		prepare(exchange);
	}

	public HeroFileInfo(String name, String id, String key) {
		super();
		this.name = name;
		this.id = id;
		this.key = key;
		this.storageType = StorageType.HeldenAustausch;

		file = new File(DsaTabApplication.getInternalHeroDirectory(), getId() + HeroFileInfo.HERO_FILE_EXTENSION);
		fileConfig = new File(DsaTabApplication.getInternalHeroDirectory(), getId()
				+ HeroFileInfo.CONFIG_FILE_EXTENSION);
	}

	public String getRemoteHeroId() {
		return remoteHeroId;
	}

	public String getRemoteConfigId() {
		return remoteConfigId;
	}

	public void merge(HeroFileInfo fileInfo) {
		if (id == null)
			id = fileInfo.getId();
		if (key == null)
			key = fileInfo.getKey();
		if (storageType == null)
			storageType = fileInfo.getStorageType();
		if (file == null)
			file = fileInfo.file;
		if (fileConfig == null)
			fileConfig = fileInfo.fileConfig;
		if (portraitUri == null)
			portraitUri = fileInfo.portraitUri;
		if (remoteConfigId == null)
			remoteConfigId = fileInfo.remoteConfigId;
		if (remoteHeroId == null)
			remoteHeroId = fileInfo.remoteHeroId;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public void setRemoteHeroId(String remoteHeroId) {
		this.remoteHeroId = remoteHeroId;
	}

	public void setRemoteConfigId(String remoteConfigId) {
		this.remoteConfigId = remoteConfigId;
	}

	public String getPath(FileType fileType) {
		switch (fileType) {
		case Hero:
			return file.getAbsolutePath();
		case Config:
			return fileConfig.getAbsolutePath();
		default:
			return null;
		}
	}

	public LocalFile getLocalFile(FileType fileType) {
		switch (fileType) {
		case Hero:
			if (file != null)
				return new LocalFile(file);
			else
				return null;
		case Config:
			if (fileConfig != null)
				return new LocalFile(fileConfig);
			else
				return null;
		default:
			return null;
		}
	}

	public File getFile(FileType fileType) {
		switch (fileType) {
		case Hero:
			return file;
		case Config:
			return fileConfig;
		default:
			return null;
		}
	}

	public boolean prepare(HeroExchange exchange) {
		boolean valid = false;
		InputStream fis = null;

		try {
			fis = exchange.getInputStream(this, FileType.Hero);
			if (fis != null) {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setValidating(false);
				factory.setNamespaceAware(true);

				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(fis, HeldenXmlParser.ENCODING);

				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG) {
						if (xpp.getName().equals(Xml.KEY_HELDEN)) {
							version = xpp.getAttributeValue(null, "Version");
						} else if (xpp.getName().equals(Xml.KEY_HELD)) {
							portraitUri = xpp.getAttributeValue(null, Xml.KEY_PORTRAIT_PATH);
							name = xpp.getAttributeValue(null, Xml.KEY_NAME);
							key = xpp.getAttributeValue(null, Xml.KEY_KEY);
							id = xpp.getAttributeValue(null, Xml.KEY_ID);
							valid = true;
							break;
						}
					}
					eventType = xpp.next();
				}
			}
		} catch (XmlPullParserException e) {
			Debug.error(e);
		} catch (IOException e) {
			Debug.error(e);
		} finally {
			Util.close(fis);
		}
		return valid;

	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPortraitUri() {
		return portraitUri;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getVersion() {
		return version;
	}

	public int getVersionInt() {
		String versionNumbers = version.replace(".", "");
		while (versionNumbers.length() < 4) {
			versionNumbers = versionNumbers.concat("0");
		}

		int version;
		try {
			version = Util.parseInt(versionNumbers, -1);
		} catch (NumberFormatException e) {
			version = -1;
		}

		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isDeletable() {
		return file != null || fileConfig != null;
	}

	public boolean isOnline() {
		return id != null;
	}

	public boolean isInternal() {
		return file != null && file.exists();
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.putOpt(JSON_NAME, name);
		jsonObject.putOpt(JSON_KEY, key);
		jsonObject.putOpt(JSON_ID, id);
		jsonObject.putOpt(JSON_VERSION, version);
		jsonObject.putOpt(JSON_FILE, file);
		jsonObject.putOpt(JSON_FILE_CONFIG, fileConfig);
		if (storageType != null) {
			jsonObject.put(JSON_STORAGE_TYPE, storageType.name());
		}
		jsonObject.putOpt(JSON_PORTRAIT_URI, portraitUri);
		return jsonObject;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (o == this)
			return true;

		if (!(o instanceof HeroFileInfo))
			return false;

		HeroFileInfo info = (HeroFileInfo) o;

		return info.getKey() != null && info.getKey().equals(getKey());
	}

	@Override
	public int hashCode() {
		if (getKey() != null)
			return getKey().hashCode();
		else
			return super.hashCode();
	}

	@Override
	public String toString() {
		return "HeroFileInfo [name=" + name + ", key=" + key + ", id=" + id + ", version=" + version + ", storageType="
				+ storageType + ", file=" + file + ", fileConfig=" + fileConfig + ", portraitUri=" + portraitUri + "]";
	}

}
