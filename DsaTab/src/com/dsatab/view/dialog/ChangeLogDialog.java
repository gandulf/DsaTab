package com.dsatab.view.dialog;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.gandulf.guilib.util.ResUtil;

/*
 * Class to show a changelog dialog
 * (c) 2012 Martin van Zuilekom (http://martin.cubeactive.com)
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class ChangeLogDialog {
	static final private String TAG = "ChangeLogDialog";

	public static final String KEY_NEWS_VERSION = "newsversion";

	private Activity context;

	private boolean newChangelogFound = false;
	private int lastSeenVersion = 0;

	public ChangeLogDialog(Activity context) {
		this.context = context;
		this.lastSeenVersion = getSeenVersion();
	}

	// Parse a the release tag and return html code
	private String parseReleaseTag(XmlResourceParser aXml) throws XmlPullParserException, IOException {
		int version = aXml.getAttributeIntValue(null, "versioncode", 0);
		if (version > lastSeenVersion)
			newChangelogFound = true;

		String _Result = "<h3>Release: " + aXml.getAttributeValue(null, "version") + "</h3>";

		StringBuilder fixes = new StringBuilder();
		StringBuilder changes = new StringBuilder();
		StringBuilder notes = new StringBuilder();

		int eventType = aXml.getEventType();
		while (eventType != XmlPullParser.END_TAG || !aXml.getName().equals("release")) {
			if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("note"))) {
				eventType = aXml.next();
				notes.append("<p>" + aXml.getText() + "</p>");
			}
			if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("change"))) {
				eventType = aXml.next();
				changes.append("<li>" + aXml.getText() + "</li>");
			}
			if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("fix"))) {
				eventType = aXml.next();
				fixes.append("<li>" + aXml.getText() + "</li>");
			}
			eventType = aXml.next();
		}

		_Result = _Result + notes.toString();
		if (changes.length() > 0)
			_Result = _Result + "<ul>" + changes.toString() + "</ul>";
		if (fixes.length() > 0)
			_Result = _Result + "<ul>" + fixes.toString() + "</ul>";

		return _Result;
	}

	// CSS style for the html
	private String getStyle() {
		return "<style type=\"text/css\">" + "h1 { margin-left: 0px; }" + "li { margin-left: 0px; }"
				+ "ul { padding-left: 30px;}" + "</style>";
	}

	// Get the changelog in html code, this will be shown in the dialog's
	// webview
	private String getHTMLChangelog(int aResourceId, Resources aResource) {
		String _Result = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
				+ getStyle() + "</head><body>";

		String summary = ResUtil.loadResToString(R.raw.donate, context);
		if (summary != null) {
			summary = summary.replace("{hs-version}", DsaTabApplication.HS_VERSION);
			_Result += summary;
		}

		XmlResourceParser _xml = aResource.getXml(aResourceId);
		try {
			int eventType = _xml.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if ((eventType == XmlPullParser.START_TAG) && (_xml.getName().equals("release"))) {
					_Result = _Result + parseReleaseTag(_xml);
				}
				eventType = _xml.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);

		} finally {
			_xml.close();
		}
		_Result = _Result + "</body></html>";
		return _Result;
	}

	public void show() {
		show(false);
	}

	// Call to show the changelog dialog
	public void show(boolean forceShow) {
		// Get resources
		Resources resource = context.getResources();

		// Get dialog title
		String title = resource.getString(R.string.app_name);
		title = title + " " + DsaTabApplication.getInstance().getPackageVersionName();

		// Create html change log
		String html = getHTMLChangelog(R.xml.changelog, resource);

		// Get button strings
		String close = resource.getString(R.string.label_ok);

		// Check for empty changelog
		if (html.equals("")) {
			// Could not load change log, message user and exit void
			Toast.makeText(context, "Could not load change log", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!forceShow && !newChangelogFound) {
			// nothing new found skipping changelog
			return;
		} else {
			// setting new lastseen version
			setSeenVersion(DsaTabApplication.getInstance().getPackageVersion());
		}

		// Create webview and load html
		WebView webView = new WebView(context);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setView(webView)
				.setPositiveButton(close, new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
		builder.create().show();
	}

	private int getSeenVersion() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getInt(KEY_NEWS_VERSION, 0);
	}

	private void setSeenVersion(int version) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		Editor editor = preferences.edit();
		editor.putInt(KEY_NEWS_VERSION, version);
		editor.commit();
	}

}
