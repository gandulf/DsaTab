package com.dsatab;

import android.content.Context;

/**
 * 
 * 
 */
public class AnalyticsManager {

	private static boolean enabled = true;

	public static void setEnabled(boolean b) {
		enabled = b;
		// FlurryAgent.setLogEnabled(b);
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void startSession(Context context) {
		// if (enabled)
		// FlurryAgent.onStartSession(context, DSATabApplication.FLURRY_APP_ID);
	}

	public static void endSession(Context context) {
		// if (enabled)
		// FlurryAgent.onEndSession(context);
	}

	public static void onEvent(String event) {
		// if (enabled)
		// FlurryAgent.onEvent(event);
	}
}
