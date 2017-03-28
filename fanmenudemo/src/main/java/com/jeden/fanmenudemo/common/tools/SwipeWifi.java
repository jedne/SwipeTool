package com.jeden.fanmenudemo.common.tools;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeWifi extends SwipeTools {
    private volatile static SwipeWifi mInstance;
    private WifiManager mWifi;
    private SwipeWifi(Context context)
    {
        mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    public static SwipeWifi getInstance(Context context)
    {
        if(mInstance == null)
        {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context)
    {
        mInstance = new SwipeWifi(context);
    }

    @Override
    public void viewBinded(Context context) {
        changeViewState();
    }

    @Override
    public void changeState(Context context) {
        if(mWifi.isWifiEnabled())
        {
            mWifi.setWifiEnabled(false);
        }
        else
        {
            mWifi.setWifiEnabled(true);
        }
        changeViewState();
    }

    private void changeViewState(){
        if(mWifi.isWifiEnabled())
        {
            //TODO
        }
        else
        {
            //TODO
        }
    }
}
