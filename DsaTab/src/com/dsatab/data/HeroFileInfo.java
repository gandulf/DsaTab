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

import com.dropbox.sync.android.DbxPath;
import com.dsatab.DsaTabApplication;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.xml.HeldenXmlParser;
import com.dsatab.xml.Xml;

public class HeroFileInfo implements JSONable, Serializable {

	public static final String HERO_FILE_EXTENSION = ".xml";
	public static final String CONFIG_FILE_EXTENSION = ".json";

	private static final long serialVersionUID = 2900488398093511704L;

	public enum StorageType {
		FileSystem, Dropbox, HeldenAustausch
	}

	public enum FileType {
		Hero, Config
	}

	private static final String JSON_NAME = "name";
	private static final String JSON_KEY = "key";
	private static final String JSON_ID = "id";
	private static final String JSON_FILE = "file";
	private static final String JSON_FILE_CONFIG = "fileConfig";
	private static final String JSON_PORTRAIT_URI = "portraitUri";
	private static final String JSON_STORAGE_TYPE = "storageType";
	private static final String JSON_VERSION = "version";

	private String name;

	private String key;

	private String id;

	private String version;

	private StorageType storageType;

	private String file;
	private String fileConfig;

	private String portraitUri;

	public HeroFileInfo(JSONObject json) throws IllegalArgumentException {

		name = json.optString(JSON_NAME);
		key = json.optString(JSON_KEY);
		id = json.optString(JSON_ID);
		version = json.optString(JSON_VERSION);
		storageType = StorageType.valueOf(json.optString(JSON_STORAGE_TYPE, StorageType.FileSystem.name()));

		if (json.has(JSON_FILE))
			file = json.optString(JSON_FILE);
		else
			initHeroFile();

		if (json.has(JSON_FILE_CONFIG))
			fileConfig = json.optString(JSON_FILE_CONFIG);
		else
			initConfigFile();

		portraitUri = json.optString(JSON_PORTRAIT_URI);
	}

	public HeroFileInfo(DbxPath heroFile, DbxPath configFile, HeroExchange exchange) throws IllegalArgumentException {
		boolean valid = false;

		this.storageType = StorageType.Dropbox;
		this.file = heroFile.toString();
		if (configFile == null) {
			this.fileConfig = file.replace(HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION);
		} else {
			this.fileConfig = configFile.toString();
		}

		valid = readData(exchange);
		if (!valid) {
			throw new IllegalArgumentException("Given File " + heroFile.getName()
					+ " is no valid xml file. Does not contain a " + Xml.KEY_HELD + " tag");
		}
	}

	public HeroFileInfo(File heroFile, File configFile, HeroExchange exchange) throws IllegalArgumentException {
		boolean valid = false;

		storageType = StorageType.FileSystem;
		this.file = heroFile.getAbsolutePath();
		if (configFile == null) {
			initConfigFile();
		} else {
			fileConfig = configFile.getAbsolutePath();
		}

		valid = readData(exchange);
		if (!valid) {
			throw new IllegalArgumentException("Given File " + heroFile.getAbsolutePath()
					+ " is no valid xml file. Does not contain a " + Xml.KEY_HELD + " tag");
		}
	}

	public HeroFileInfo(String name, String id, String key) {
		super();
		this.name = name;
		this.id = id;
		this.key = key;
		this.storageType = StorageType.HeldenAustausch;

		initHeroFile();
		initConfigFile();
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
	}

	private void initHeroFile() {
		if (storageType == StorageType.FileSystem || storageType == StorageType.HeldenAustausch && file == null) {
			File file = new File(DsaTabApplication.getDsaTabPath(), getId() + HeroFileInfo.HERO_FILE_EXTENSION);
			this.file = file.getAbsolutePath();
		}
	}

	private void initConfigFile() {
		if (storageType == StorageType.FileSystem || storageType == StorageType.HeldenAustausch && fileConfig == null) {
			File oldFile = new File(getPath(FileType.Hero) + ".dsatab");
			File file = new File(getPath(FileType.Hero).replace(HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION));

			if (oldFile.exists() && oldFile.canRead() && oldFile.length() > 0) {
				if (!file.exists())
					oldFile.renameTo(file);
				else
					oldFile.delete();
			}

			this.fileConfig = file.getAbsolutePath();
		}
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public String getPath(FileType fileType) {
		switch (fileType) {
		case Hero:
			return file;
		case Config:
			return fileConfig;
		default:
			return null;
		}
	}

	protected boolean readData(HeroExchange exchange) {
		boolean valid = false;
		InputStream fis = null;
		try {
			fis = exchange.getInputStream(this, FileType.Hero);
			if (fis != null) {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
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
			exchange.closeStream(this, FileType.Hero);
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

		int version = Util.parseInt(versionNumbers, -1);

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
		return getKey().hashCode();
	}

}
