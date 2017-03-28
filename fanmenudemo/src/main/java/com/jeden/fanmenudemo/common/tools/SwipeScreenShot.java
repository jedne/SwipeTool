package com.jeden.fanmenudemo.common.tools;

import android.content.Context;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeScreenShot extends SwipeTools {
    private volatile static SwipeScreenShot mInstance;

    private SwipeScreenShot(Context context)
    {
    }
    public static SwipeScreenShot getInstance(Context context)
    {
        if(mInstance == null)
        {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context)
    {
        mInstance = new SwipeScreenShot(context);
    }

    @Override
    public void viewBinded(Context context) {

    }

    @Override
    public void changeState(Context context) {
    }

    private void changeViewState()
    {

    }
}
