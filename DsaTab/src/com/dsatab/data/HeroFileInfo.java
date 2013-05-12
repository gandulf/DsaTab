package com.dsatab.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.dsatab.util.Debug;
import com.dsatab.xml.HeldenXmlParser;
import com.dsatab.xml.Xml;

/**
 * 
 * 
 */
public class HeroFileInfo {

	public String name;

	public String key;

	public String id;

	public File file;

	public String portraitUri;

	public HeroFileInfo(File file) throws IllegalArgumentException {
		boolean valid = false;
		this.file = file;

		FileInputStream fis = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			fis = new FileInputStream(file);
			xpp.setInput(fis, HeldenXmlParser.ENCODING);

			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (xpp.getName().equals(Xml.KEY_HELD)) {
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
		} catch (FileNotFoundException e) {
			Debug.error(e);
		} catch (XmlPullParserException e) {
			Debug.error(e);
		} catch (IOException e) {
			Debug.error(e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
			}
		}

		if (!valid) {
			throw new IllegalArgumentException("Given File is no valid xml file. Does not contain a " + Xml.KEY_HELD
					+ " tag");
		}
	}

	public HeroFileInfo(String name, String id, String key) {
		super();
		this.name = name;
		this.id = id;
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getPortraitUri() {
		return portraitUri;
	}

	public File getFile() {
		return file;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isOnline() {
		return id != null;
	}

}
