package com.dsatab.data;

import android.text.TextUtils;

import com.cloudrail.si.types.CloudMetaData;
import com.dsatab.DsaTabApplication;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.cloud.HeroExchange.StorageType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.xml.HeldenXmlParser;
import com.dsatab.xml.Xml;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

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

        try {
            storageType = StorageType.valueOf(json.optString(JSON_STORAGE_TYPE));
        } catch (IllegalArgumentException e) {
            storageType = null;
        }

        if (json.has(JSON_FILE))
			file = new File(json.optString(JSON_FILE));
		else
			file = new File(DsaTabApplication.getHeroDirectory(), getId() + HeroFileInfo.HERO_FILE_EXTENSION);

		if (json.has(JSON_FILE_CONFIG))
			fileConfig = new File(json.optString(JSON_FILE_CONFIG));
		else
			fileConfig = new File(file.getAbsolutePath().replace(HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION));

		remoteHeroId = json.optString(JSON_HERO_REMOTE);
		remoteConfigId = json.optString(JSON_CONFIG_REMOTE);

		portraitUri = json.optString(JSON_PORTRAIT_URI);

	}

	public HeroFileInfo(File internalHeroFile, HeroExchange exchange)
			throws IllegalArgumentException {

		storageType = null;
		file = internalHeroFile;

		if (file !=null) {
			fileConfig = new File(file.getParentFile(),file.getName().replace(HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION));
		}

		prepare(exchange);
	}

	public HeroFileInfo(CloudMetaData heroFile, StorageType storageType, HeroExchange exchange)
			throws IllegalArgumentException {

		this.storageType = storageType;

		remoteHeroId = heroFile.getPath();
        remoteConfigId = heroFile.getPath().replace(
                HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION);

		file = new File(DsaTabApplication.getHeroDirectory(), heroFile.getName());
		fileConfig = new File(DsaTabApplication.getHeroDirectory(), heroFile.getName().replace(
					HERO_FILE_EXTENSION, CONFIG_FILE_EXTENSION));

		prepare(exchange);
	}

    public static void merge(List<HeroFileInfo> heroes, HeroFileInfo heroFileInfo) {
        int index = heroes.indexOf(heroFileInfo);
        if (index >= 0) {
            HeroFileInfo info = heroes.get(index);
            info.merge(heroFileInfo);
        } else {
            heroes.add(heroFileInfo);
        }
    }
    public static void merge(List<HeroFileInfo> heroes, List<HeroFileInfo> heroesAdd) {
        for (HeroFileInfo heroFileInfo: heroesAdd) {
            merge(heroes,heroFileInfo);
        }
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
        if (fileConfig !=null && fileConfig.exists()) {
            try {
                HeroConfiguration configuration;
                FileInputStream configIn = new FileInputStream(fileConfig);

                String data = Util.slurp(configIn, 1024);
                if (!TextUtils.isEmpty(data)) {
                    JSONObject jsonObject = new JSONObject(new String(data));
                    configuration = new HeroConfiguration(null, jsonObject);
                    if (configuration.getStorageType()!=null) {
                        storageType = configuration.getStorageType();
                    }
                    if (storageType!=null) {
                        if (!TextUtils.isEmpty(configuration.getStorageHeroId())) {
                            remoteHeroId = configuration.getStorageHeroId();
                        } else {
                            remoteHeroId = HeroExchange.BASE_DIRECTORY + "/" + file.getName();
                        }
                        if (!TextUtils.isEmpty(configuration.getStorageConfigId())) {
                            remoteConfigId = configuration.getStorageConfigId();
                        } else {
                            remoteConfigId = HeroExchange.BASE_DIRECTORY + "/" + fileConfig.getName();
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                Debug.error(e);
            } catch (JSONException e) {
                Debug.error(e);
            }
        }

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

    public void setName(String name) {
        this.name = name;
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

	public void setPortraitUri(String portraitUri) {
		this.portraitUri = portraitUri;
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
		return getStorageType()!=null;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeroFileInfo that = (HeroFileInfo) o;

        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : super.hashCode();
    }

    @Override
	public String toString() {
		return "HeroFileInfo [name=" + name + ", key=" + key + ", id=" + id + ", version=" + version + ", storageType="
				+ storageType + ", file=" + file + ", fileConfig=" + fileConfig + ", portraitUri=" + portraitUri + "]";
	}

}
