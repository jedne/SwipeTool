package com.jeden.fanmenu.common.tools;

import android.content.Context;
import android.content.Intent;

import com.jeden.fanmenu.common.screenshot.ScreenShotActivity;

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
    public void viewBind(Context context) {

    }

    @Override
    public void changeState(Context context) {
        mCloseAfterChange = true;
        Intent intent = new Intent(context, ScreenShotActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void changeViewState()
    {

    }
}
