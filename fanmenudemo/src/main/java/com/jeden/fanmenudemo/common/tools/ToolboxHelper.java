package com.jeden.fanmenudemo.common.tools;

import android.content.Context;

import com.jeden.fanmenudemo.bean.AppInfo;
import com.jeden.fanmenudemo.common.model.AppInfoHelper;

/**
 * Created by jeden on 2017/3/23.
 */

public class ToolboxHelper
{
    public static SwipeTools checkSwipeTools(Context context, AppInfo appInfo)
    {
        if(appInfo.getIntent() != null)
        {
            return null;
        }

        String pkg = appInfo.getPkgName();
        if(pkg == null || !pkg.contains("#com.system."))
        {
            return null;
        }

        if(AppInfoHelper.TOOLBOX_PKG_BLUETOOTH.equals(pkg))
        {
            return SwipeBlueTooth.getInstance(context);
        }
        else if(AppInfoHelper.TOOLBOX_PKG_LOCKER.equals(pkg))
        {
            return SwipeAutoLocker.getInstance(context);
        }
        else if(AppInfoHelper.TOOLBOX_PKG_BRIGHT.equals(pkg))
        {
            return SwipeBrightness.getInstance(context);
        }
        else if(AppInfoHelper.TOOLBOX_PKG_WIFI.equals(pkg))
        {
            return SwipeWifi.getInstance(context);
        }
        else if(AppInfoHelper.TOOLBOX_PKG_SHAKE.equals(pkg))
        {
            return SwipeAudio.getInstance(context);
        }
        else if(AppInfoHelper.TOOLBOX_PKG_ROTATE.equals(pkg))
        {
            return SwipeRotate.getInstance(context);
        }
        else if(AppInfoHelper.TOOLBOX_PKG_LIGHTBULB.equals(pkg))
        {
            return SwipeFlashLight.getInstance(context);
        }
        else if(AppInfoHelper.TOOLBOX_PKG_DATA.equals(pkg))
        {
            return SwipeData.getInstance(context);
        }
        else if(AppInfoHelper.TOOLBOX_PKG_CALCULATOR.equals(pkg))
        {
            return SwipeCalculator.getInstance(context);
        }
        else if(AppInfoHelper.TOOLBOX_PKG_SCREENSHOT.equals(pkg))
        {
            return SwipeScreenShot.getInstance(context);
        }

        return null;
    }
}
