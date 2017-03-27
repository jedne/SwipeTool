package com.jeden.fanmenudemo.services;

import android.content.Context;

/**
 * Created by jeden on 2017/3/22.
 */

public class FanMenuSDK {

    private static Context mContext;

    public static void initSDK(Context context)
    {
        mContext = context;
        MyService.initFanMenuSDK(context);
    }

    public static void showFlowing()
    {
        if(mContext == null)
        {
            return;
        }
        MyService.showFlowing(mContext);
    }

    public static void hideFlowing()
    {
        if(mContext == null)
        {
            return;
        }
        MyService.hideFlowing(mContext);
    }
}
