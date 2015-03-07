package com.dsatab.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.XmlResourceParser;
import android.graphics.Point;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Hint {

	private static long hintShown = System.currentTimeMillis();

	public static final String PREF_PREFIX_HINT_STORAGE = "dsatab_hint_";
	public static final String VIEW_ID_PREFIX = "@id/";

	public String id, viewId, description, title;
	public float x, y;

	private static Map<String, List<Hint>> hints;
	private static Random rnd = new Random();

	public static boolean showHint(String fragmentName, String hintId, Activity activity) {
		if (hintDelayCheck()
				&& DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_TIP_TODAY, true)) {
			Hint hint = getHint(fragmentName, hintId);
			if (hint != null) {
				hintShown = System.currentTimeMillis();
				return hint.show(activity);
			} else {
				return false;
			}
		} else
			return false;

	}

	protected static boolean hintDelayCheck() {
		if (hintShown != 0 && System.currentTimeMillis() - hintShown < 1000 * 30) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean showRandomHint(String fragmentName, Activity activity) {

		if (hintDelayCheck()
				&& DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_TIP_TODAY, true)) {
			Hint hint = getRandomHint(fragmentName);
			if (hint != null) {
				hintShown = System.currentTimeMillis();
				return hint.show(activity);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	protected static Hint getRandomHint(String fragmentName) {
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

	protected static Hint getHint(String fragmentName, String hintId) {
		if (hints == null)
			loadHints();

		if (hints.containsKey(fragmentName)) {
			SharedPreferences pref = DsaTabApplication.getPreferences();
			List<Hint> hintList = new ArrayList<Hint>(hints.get(fragmentName));
			for (Hint hint : hintList) {
				if (hint.id.equals(hintId)) {
					if (!pref.getBoolean(Hint.PREF_PREFIX_HINT_STORAGE + hint.id, false)) {
						return hint;
					}
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
					if (hint.viewId != null && hint.viewId.startsWith(Hint.VIEW_ID_PREFIX)) {
						hint.viewId = hint.viewId.substring(Hint.VIEW_ID_PREFIX.length());
					}

					hint.x = xpp.getAttributeFloatValue(null, "x", -1.0f);
					hint.y = xpp.getAttributeFloatValue(null, "y", -1.0f);

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
		boolean shown = false;
		if (this.viewId != null) {
			int viewIdInt = activity.getResources().getIdentifier(this.viewId, "id",
					DsaTabApplication.getInstance().getPackageName());
			if (viewIdInt != 0) {
				ViewTarget target = new ViewTarget(viewIdInt, activity);

				// check if view is onscreen
				Point p = target.getPoint();
				if (p.x >= 0 && p.y >= 0 && p.x < Util.getWidth(activity) && p.y < Util.getHeight(activity)) {
					new ShowcaseView.Builder(activity).setTarget(target).setContentTitle(title)
							.setContentText(description).build().show();
					shown = true;
				}
			}
		} else if (this.x >= 0 && this.y >= 0) {
			int x = (int) (Util.getWidth(activity) * (this.x / 100.0f));
			int y = (int) (Util.getHeight(activity) * (this.y / 100.0f));
			PointTarget pointTarget = new PointTarget(x, y);
			new ShowcaseView.Builder(activity).setTarget(pointTarget).setContentTitle(title)
					.setContentText(description).build().show();
			shown = true;
		}

		if (shown) {
			Editor edit = DsaTabApplication.getPreferences().edit();
			edit.putBoolean(Hint.PREF_PREFIX_HINT_STORAGE + id, true);
			edit.commit();
			return true;
		}
		return false;
	}
}