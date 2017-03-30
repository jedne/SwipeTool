package com.jeden.fanmenu.common.tools;

import android.content.Context;
import android.content.Intent;

import com.jeden.fanmenu.activitys.FanMenuSettingActivity;

/**
 * Created by jeden on 2017/3/27.
 */

public class SwipeSetting extends SwipeTools {
    private volatile static SwipeSetting mInstance;

    private SwipeSetting(Context context)
    {
    }
    public static SwipeSetting getInstance(Context context)
    {
        if(mInstance == null)
        {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context)
    {
        mInstance = new SwipeSetting(context);
    }

    @Override
    public void viewBind(Context context) {

    }

    @Override
    public void changeState(Context context) {
        mCloseAfterChange = true;
        Intent intent = new Intent(context, FanMenuSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
