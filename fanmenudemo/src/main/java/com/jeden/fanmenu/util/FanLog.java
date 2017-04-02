package com.jeden.fanmenu.util;

import android.util.Log;

/**
 * Created by jeden on 2017/3/29.
 */

public class FanLog {
    private boolean mDebug = true;
    private String mTag = "FanLog";
    private static FanLog mInstance = new FanLog();

    private FanLog(){}
    public static void setDebug(boolean debug) {
        mInstance.mDebug = debug;
    }

    public static void setTag(String tag) {
        mInstance.mTag = tag;
    }

    public static void v (String tag, String content) {
        mInstance.logV(tag, content);
    }

    public static void i (String tag, String content) {
        mInstance.logI(tag, content);
    }

    public static void w (String tag, String content) {
        mInstance.logW(tag, content);
    }

    public static void e (String tag, String content) {
        mInstance.logE(tag, content);
    }

    public static void v (String content) {
        v(mInstance.mTag, content);
    }

    public static void i (String content) {
        i(mInstance.mTag, content);
    }

    public static void w (String content) {
        w(mInstance.mTag, content);
    }

    public static void e (String content) {
        e(mInstance.mTag, content);
    }

    private void logV(String tag, String content) {
        if(!mDebug) {
            return;
        }
        Log.v(tag, content);
    }

    private void logI(String tag, String content) {
        if(!mDebug) {
            return;
        }
        Log.i(tag, content);
    }

    private void logW(String tag, String content) {
        Log.w(tag, content);
    }

    private void logE(String tag, String content) {
        Log.e(tag, content);
    }
}
