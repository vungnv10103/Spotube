package com.chagnahnn.spotube.util;

import android.util.Log;

@SuppressWarnings("unused")
public class LogUtils {
    public static final String TAG = "SPOTUBE";
    public static final int DEBUG = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;

    public static void log(int type, Object message) {
        log(null, type, message);
    }

    public static void log(String tag, int type, Object message) {
        if (tag == null)
            tag = TAG;
        if (message != null) {
            switch (type) {
                case DEBUG:
                    Log.d(TAG, message.toString());
                    break;
                case WARNING:
                    Log.w(TAG, message.toString());
                    break;
                default:
                    Log.d(tag, message.toString());
            }
        }

    }

    public static void logD(Object message) {
        if (message != null)
            Log.d(TAG, message.toString());
    }

    public static void logI(Object message) {
        if (message != null)
            Log.i(TAG, message.toString());
    }

    public static void logW(Object message) {
        if (message != null)
            Log.w(TAG, message.toString());
    }

    public static void logE(Object message, Throwable t) {
        if (message != null)
            Log.e(TAG, message.toString(), t);
    }
}
