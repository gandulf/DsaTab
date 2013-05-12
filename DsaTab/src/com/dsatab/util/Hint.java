package com.dsatab.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.XmlResourceParser;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.espian.showcaseview.ShowcaseView;

public class Hint {

	public static final String PREF_PREFIX_HINT_STORAGE = "dsatab_hint_";
	public static final String VIEW_ID_PREFIX = "@id/";

	public String id, viewId, description, title;

	private static Map<String, List<Hint>> hints;
	private static Random rnd = new Random();

	public static boolean showRandomHint(String fragmentName, Activity activity) {

		if (DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_TIP_TODAY, true)) {
			Hint hint = getRandomHint(fragmentName);
			if (hint != null)
				return hint.show(activity);
			else
				return false;
		} else
			return false;
	}

	public static Hint getRandomHint(String fragmentName) {
		if (hints == null)
			loadHints();

		if (hints.containsKey(fragmentName)) {
			SharedPreferences pref = DsaTabApplication.getPreferences();
			List<Hint> hintList = new ArrayList<Hint>(hints.get(fragmentName));

			while (!hintList.isEmpty()) {
				Hint hint = hintList.get(rnd.nextInt(hintList.size()));
				hintList.remove(hint);

				if (!pref.getBoolean(Hint.PREF_PREFIX_HINT_STORAGE + hint.id, false)) {
					return hint;
				}
			}
		}
		return null;
	}

	protected static void loadHints() {
		hints = new HashMap<String, List<Hint>>();
		XmlResourceParser xpp = DsaTabApplication.getInstance().getResources().getXml(R.xml.showcase_hints);
		try {
			xpp.next();
			int eventType = xpp.getEventType();

			String fragmentName = null;
			List<Hint> hintList = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG && "fragment".equals(xpp.getName())) {
					fragmentName = xpp.getAttributeValue(null, "name");
					hintList = new ArrayList<Hint>();
					hints.put(fragmentName, hintList);
				}

				if (fragmentName != null && eventType == XmlPullParser.START_TAG && "hint".equals(xpp.getName())) {
					String id = xpp.getAttributeValue(null, "id");

					Hint hint = new Hint();
					hint.id = id;
					hint.viewId = xpp.getAttributeValue(null, "viewId");
					if (hint.viewId.startsWith(Hint.VIEW_ID_PREFIX)) {
						hint.viewId = hint.viewId.substring(Hint.VIEW_ID_PREFIX.length());
					}
					hint.title = xpp.getAttributeValue(null, "title");
					hint.description = xpp.getAttributeValue(null, "description");
					hintList.add(hint);
				}

				if (eventType == XmlPullParser.END_TAG && "fragment".equals(xpp.getName())) {
					fragmentName = null;
					hintList = null;
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			Debug.error(e);
		} catch (IOException e) {
			Debug.error(e);
		}
	}

	/**
	 * 
	 */
	public boolean show(Activity activity) {
		int viewIdInt = activity.getResources().getIdentifier(this.viewId, "id",
				DsaTabApplication.getInstance().getPackageName());
		if (viewIdInt != 0) {
			ShowcaseView.insertShowcaseView(viewIdInt, activity, title, description, null);

			Editor edit = DsaTabApplication.getPreferences().edit();
			edit.putBoolean(Hint.PREF_PREFIX_HINT_STORAGE + id, true);
			edit.commit();
			return true;
		}
		return false;
	}
}