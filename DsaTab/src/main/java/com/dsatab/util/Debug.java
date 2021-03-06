package com.dsatab.util;

import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.dsatab.BuildConfig;
import com.dsatab.DsaTabApplication;

import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Functions and helpers to aid debugging. DebugMode can be toggled .
 */
public class Debug {

    private static String tag = DsaTabApplication.TAG;

    public static boolean TRACE = true;

    protected static boolean debugMode = com.dsatab.BuildConfig.DEBUG;

    public static void logCustomEvent(String name) {
        CustomEvent event = new CustomEvent(name);
        Answers.getInstance().logCustom(event);
    }
    public static void logCustomEvent(String name,String key,String value) {
        CustomEvent event = new CustomEvent(name);
        event.putCustomAttribute(key,value);
        Answers.getInstance().logCustom(event);
    }
    public static void logCustomEvent(String name,Map<String,String> data) {
        CustomEvent event = new CustomEvent(name);
        for (Map.Entry<String,String> entry : data.entrySet()) {
            event.putCustomAttribute(entry.getKey(),entry.getValue());
        }
        Answers.getInstance().logCustom(event);
    }

    public static void d(String message) {
        if (debugMode && TRACE)
            Log.d(tag, "TRACE:" + message);
    }

    /**
     * Prints a w to LogCat with information
     *
     * @param source  The source of the w, such as function name
     * @param message The message to be passed on
     */
    public static void w(String source, String message) {
        if (debugMode) {
            Log.w(tag, source + " - " + message);
            Exception e = new Exception(source + " - " + message);
            e.printStackTrace();
        }
    }

    /**
     * Prints a w to LogCat with information
     *
     * @param message The message to be passed on
     */
    public static void w(String message) {
        if (debugMode)
            Log.w(tag, message);
    }

    /**
     * Prints a w to LogCat with information
     *
     * @param message The message to be passed on
     */
    public static void w(String message, Throwable throwable) {
        if (debugMode)
            Log.w(tag, message, throwable);
    }

    /**
     * Prints to the e stream of LogCat with information
     *
     * @param message The message to be passed on
     */
    public static void e(String message) {
        Log.e(tag, message);
        Exception e = new Exception(message);
        e.printStackTrace();

    }

    public static void e(String message, Throwable e) {
        if (e instanceof Exception && !BuildConfig.DEBUG) {
            Fabric.getLogger().e(tag,e.getLocalizedMessage(),e);
        }
        Log.e(tag, message);
        e.printStackTrace();
    }

    /**
     * Prints to the e stream of LogCat with information from the engine
     *
     * @param t The throwable to be passed on
     */
    public static void w(Throwable t) {
        if (debugMode) {
            Log.w(tag, t.getMessage(), t);
        }
    }

    /**
     * Prints to the e stream of LogCat with information
     *
     * @param t The throwable to be passed on
     */
    public static void e(Throwable t) {
        if (t instanceof Exception && !BuildConfig.DEBUG) {
            Fabric.getLogger().e(tag,t.getLocalizedMessage(),t);
        }
        Log.e(tag, t.getMessage(), t);
    }

    /**
     * Prints to the v stream of LogCat, with information
     *
     * @param method
     * @param message
     */
    public static void v(String method, String message) {
        if (debugMode) {
            Log.v(tag, method + " - " + message);
        }
    }


    /**
     * Prints to the v stream of LogCat, with information
     *
     * @param message
     */
    public static void v(String message) {
        if (debugMode) {
            Log.v(tag, message);
        }
    }

    public static void heap() {
        Log.w("MEMORY",
                "HeapSize: " + (android.os.Debug.getNativeHeapSize() / (1024)) + "kb Used: "
                        + (android.os.Debug.getNativeHeapAllocatedSize() / (1024)) + "kb Free: "
                        + (android.os.Debug.getNativeHeapFreeSize() / (1024)) + "kb");
    }
}
