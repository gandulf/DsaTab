package com.dsatab.util;

import android.util.Log;

import com.dsatab.DsaTabApplication;

/**
 * Functions and helpers to aid debugging. DebugMode can be toggled .
 */
public class Debug {

	public static final String CATEGORY_DATA = "characterData";

	public static final String CATEGORY_DATABASE = "database";

	private static String tag = DsaTabApplication.TAG;

	private static boolean TRACE = true;

	protected static boolean debugMode = com.dsatab.BuildConfig.DEBUG;

	public static void trace(String message) {
		if (debugMode && TRACE)
			Log.d(tag, "TRACE:" + message);
	}

	/**
	 * Prints a warning to LogCat with information
	 * 
	 * @param source
	 *            The source of the warning, such as function name
	 * @param message
	 *            The message to be passed on
	 */
	public static void warning(String source, String message) {
		if (debugMode) {
			Log.w(tag, source + " - " + message);
			Exception e = new Exception(source + " - " + message);
			e.printStackTrace();
		}
	}

	/**
	 * Prints a warning to LogCat with information
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void warning(String message) {
		if (debugMode)
			Log.w(tag, message);
	}

	/**
	 * Prints to the verbose stream of LogCat with information
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void print(String message) {
		if (debugMode) {
			Log.v(tag, message);
		}
	}

	/**
	 * Prints to the error stream of LogCat with information
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void error(String message) {
		Log.e(tag, message);
		Exception e = new Exception(message);
		e.printStackTrace();

	}

	public static void error(String message, Throwable e) {
		Log.e(tag, message);
		e.printStackTrace();
	}

	/**
	 * Prints to the error stream of LogCat with information from the engine
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void warning(Throwable t) {
		if (debugMode) {
			Log.w(tag, t.getMessage(), t);
		}
	}

	/**
	 * Prints to the error stream of LogCat with information
	 * 
	 * @param message
	 *            The message to be passed on
	 */
	public static void error(Throwable t) {
		Log.e(tag, t.getMessage(), t);
	}

	/**
	 * Prints to the verbose stream of LogCat, with information
	 * 
	 * 
	 * @param method
	 * @param message
	 */
	public static void verbose(String method, String message) {
		if (debugMode) {
			Log.v(tag, method + " - " + message);
		}
	}

	public static void heap() {
		Log.w("MEMORY",
				"HeapSize: " + (android.os.Debug.getNativeHeapSize() / (1024)) + "kb Used: "
						+ (android.os.Debug.getNativeHeapAllocatedSize() / (1024)) + "kb Free: "
						+ (android.os.Debug.getNativeHeapFreeSize() / (1024)) + "kb");
	}

	/**
	 * Prints to the verbose stream of LogCat, with information
	 * 
	 * 
	 * @param message
	 */
	public static void verbose(String message) {
		if (debugMode) {
			Log.v(tag, message);
		}
	}

}
