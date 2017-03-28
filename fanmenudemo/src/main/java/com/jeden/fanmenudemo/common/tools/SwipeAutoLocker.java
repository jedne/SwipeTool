package com.jeden.fanmenudemo.common.tools;

import android.content.Context;
import android.util.Log;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeAutoLocker extends SwipeTools {
    private static final String TAG = SwipeAutoLocker.class.getSimpleName();

    private volatile static SwipeAutoLocker mInstance;

    private SwipeAutoLocker(Context context)
    {
    }
    public static SwipeAutoLocker getInstance(Context context)
    {
        if(mInstance == null)
        {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context)
    {
        mInstance = new SwipeAutoLocker(context);
    }

    @Override
    public void viewBinded(Context context) {
        changeViewState(context);
    }

    @Override
    public void changeState(Context context) {

        int level = getScreenOffTimeoutLevel(context);
        level = getScreenOffTimeoutNextLevel(level);
        setScreenOffTimeoutLevel(context, level);
        changeViewState(context);
    }

    private void changeViewState(Context context)
    {
        int level = getScreenOffTimeoutLevel(context);
        switch (level)
        {
            case 0:
                //TODO
                showToast(context, "无操作30s后锁屏");
                break;
            case 1:
                showToast(context, "无操作1m后锁屏");
                break;
            case 2:
                showToast(context, "无操作2m后锁屏");
                break;
            case 3:
                showToast(context, "无操作5m后锁屏");
                break;
            case 4:
                showToast(context, "无操作10m后锁屏");
                break;
            case 5:
                showToast(context, "不锁屏");
                break;
            default:
                break;
        }
    }

    private long getScreenOffTimeout(Context context) {
        try {
            return android.provider.Settings.System.getLong(context.getContentResolver(), "screen_off_timeout", 0L);
        } catch (Exception var2) {
            Log.w(TAG, "getScreenOffTimeout: ", var2);
            return 0L;
        }
    }

    private void setScreenOffTimeout(Context context, long timeout) {
        try {
            android.provider.Settings.System.putLong(context.getContentResolver(), "screen_off_timeout", timeout);
        } catch (Exception var4) {
            Log.w(TAG, "setScreenOffTimeout: ", var4);
        }

    }

    private int getScreenOffTimeoutLevel(Context context) {
        long timeout = getScreenOffTimeout(context);
        return timeout <= 30000L?0:(timeout <= 60000L?1:(timeout <= 120000L?2:(timeout <= 300000L?3:(timeout <= 600000L?4:5))));
    }

    private int getScreenOffTimeoutNextLevel(int level) {
        switch(level) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 0;
            default:
                return 0;
        }
    }

    private void setScreenOffTimeoutLevel(Context context, int level) {
        switch(level) {
            case 0:
                setScreenOffTimeout(context, 30000L);
                break;
            case 1:
                setScreenOffTimeout(context, 60000L);
                break;
            case 2:
                setScreenOffTimeout(context, 120000L);
                break;
            case 3:
                setScreenOffTimeout(context, 300000L);
                break;
            case 4:
                setScreenOffTimeout(context, 600000L);
                break;
            case 5:
                setScreenOffTimeout(context, 86400000L);
        }
    }
}
