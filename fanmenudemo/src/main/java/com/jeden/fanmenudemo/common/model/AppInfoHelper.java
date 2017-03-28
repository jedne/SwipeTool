package com.jeden.fanmenudemo.common.model;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.Log;

import com.jeden.fanmenudemo.R;
import com.jeden.fanmenudemo.bean.AppInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.List;

/**
 * Created by jeden on 2017/3/22.
 */

public class AppInfoHelper
{
    public static final String TAG = AppInfoHelper.class.getSimpleName();

    public static final String TOOLBOX_PKG_SCREENSHOT = "#com.system.screenshot";
    public static final String TOOLBOX_PKG_ALARM = "com.system.alarm";
    public static final String TOOLBOX_PKG_BLUETOOTH = "#com.system.bluetooth";
    public static final String TOOLBOX_PKG_CALCULATOR = "#com.system.calculator";
    public static final String TOOLBOX_PKG_FLIGHT = "com.system.flight";
    public static final String TOOLBOX_PKG_LOCKER = "#com.system.locker";
    public static final String TOOLBOX_PKG_ROTATE = "#com.system.rotate";
    public static final String TOOLBOX_PKG_WIFI = "#com.system.wifi";
    public static final String TOOLBOX_PKG_CAMERA = "android.media.action.STILL_IMAGE_CAMERA";
    public static final String TOOLBOX_PKG_SHAKE = "#com.system.shake";
    public static final String TOOLBOX_PKG_SETTING = "com.system.setting";
    public static final String TOOLBOX_PKG_LIGHTBULB = "#com.system.lightbulb";
    public static final String TOOLBOX_PKG_ISWIP = "#com.system.iswip";
    public static final String TOOLBOX_PKG_DATA = "#com.system.data";
    public static final String TOOLBOX_PKG_CALENDAR = "com.android.calendar.LaunchActivity";
    public static final String TOOLBOX_PKG_BRIGHT= "#com.system.bright";

    public static String generateAppInfoToStr(List<AppInfo> appInfos)
    {
        String result;
        JSONArray jsonArray = new JSONArray();
        for(AppInfo appInfo : appInfos)
        {
            jsonArray.put(appInfo.getPkgName());
        }

        result = jsonArray.toString();

        return result;
    }

    public static void initToolboxFromCache(List<AppInfo> alltoolbox, List<AppInfo> toolbox, String cache){
        boolean firstTime = false;
        if("".equals(cache))
        {
            firstTime = true;
        }

        if(firstTime)
        {
            initToolboxFirstTime(alltoolbox, toolbox);
        }
        else
        {
            initDataFromCache(alltoolbox, toolbox, cache);
        }
    }

    public static void initFavoriteFromCache(List<AppInfo> allApp, List<AppInfo> favorite, String cache){
        boolean firstTime = false;
        if("".equals(cache))
        {
            firstTime = true;
        }

        if(firstTime)
        {
            initFavoriteFirstTime(allApp, favorite);
        }
        else
        {
            initDataFromCache(allApp, favorite, cache);
        }
    }

    public static void initRecentlyFromCache(List<AppInfo> allApp, List<AppInfo> recently, String cache){
        boolean firstTime = false;
        if("".equals(cache))
        {
            firstTime = true;
        }

        if(firstTime)
        {
            initRecentlyFirstTime(allApp, recently);
        }
        else
        {
            initDataFromCache(allApp, recently, cache);
        }
    }

    private static void initToolboxFirstTime(List<AppInfo> allApp, List<AppInfo> needApp)
    {
        for(int i = 0; i < allApp.size(); i++) {
            AppInfo appInfo = allApp.get(i);
            if (i < 9) {
                needApp.add(appInfo);
            }
        }
    }

    private static void initFavoriteFirstTime(List<AppInfo> allApp, List<AppInfo> needApp)
    {
        for(int i = 0; i < allApp.size(); i++) {
            AppInfo appInfo = allApp.get(i);
            if (i < 9) {
                needApp.add(appInfo);
            }
        }
    }

    private static void initRecentlyFirstTime(List<AppInfo> allApp, List<AppInfo> needApp)
    {
        for(int i = 0; i < allApp.size(); i++) {
            AppInfo appInfo = allApp.get(i);
            if (i < 9 && needApp.size() < 9) {
                needApp.add(appInfo);
            }
            else
            {
                return;
            }
        }
    }

    private static void initDataFromCache(List<AppInfo> allApp, List<AppInfo> favorite, String cache)
    {
        JSONArray array = null;
        try {
            array = new JSONArray(cache);
        }
        catch (JSONException e)
        {
            Log.w(TAG, "initDataFromCache exception cache:" + cache);
        }

        if(array == null || favorite.size() >= 9)
        {
            return;
        }

        for(int j = 0; j < array.length(); j++)
        {
            String pk = array.optString(j);
            for(int i = 0; i < allApp.size(); i++)
            {
                AppInfo appInfo = allApp.get(i);
                if(pk != null && appInfo.getPkgName().equals(pk))
                {
                    favorite.add(appInfo);
                    if(favorite.size() >= 9)
                    {
                        return;
                    }
                    break;
                }
            }
        }
    }

    public static void initToolbox(Context context, List<AppInfo> toolbox)
    {
        Resources rs = context.getResources();
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_SCREENSHOT));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_ALARM));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_BLUETOOTH));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_CALCULATOR));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_FLIGHT));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_LOCKER));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_ROTATE));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_WIFI));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_CAMERA));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_SHAKE));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_SETTING));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_LIGHTBULB));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_ISWIP));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_DATA));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_CALENDAR));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_BRIGHT));
    }

    public static AppInfo generateToolbox(Resources rs, String pkgName)
    {
        AppInfo appInfo = new AppInfo();
        if(TOOLBOX_PKG_SCREENSHOT.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_screenshot));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_screenshot_title));
        }
        else if(TOOLBOX_PKG_ALARM.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_alarm));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_alarm_title));
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            appInfo.setIntent(intent);
        }
        else if(TOOLBOX_PKG_BLUETOOTH.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_bluetooth));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_bluetooth_title));
        }
        else if(TOOLBOX_PKG_CALCULATOR.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_calculator));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_calculator_title));
        }
        else if(TOOLBOX_PKG_FLIGHT.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_flight));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_flight_title));
            Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            appInfo.setIntent(intent);
        }
        else if(TOOLBOX_PKG_LOCKER.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_locker));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_locker_title));
        }
        else if(TOOLBOX_PKG_ROTATE.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_rotate));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_rotate_title));
        }
        else if(TOOLBOX_PKG_WIFI.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_wifi));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_wifi_title));
        }
        else if(TOOLBOX_PKG_CAMERA.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_camera));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_camera_title));

            Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            appInfo.setIntent(intent);
        }
        else if(TOOLBOX_PKG_SHAKE.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_shake));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_shake_title));
        }
        else if(TOOLBOX_PKG_SETTING.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_setting));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_setting_title));

            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            appInfo.setIntent(intent);
        }
        else if(TOOLBOX_PKG_LIGHTBULB.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_lightbulb));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_lightbulb_title));
        }
        else if(TOOLBOX_PKG_ISWIP.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_iswip));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_iswip_title));
        }
        else if(TOOLBOX_PKG_DATA.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_data));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_data_title));
        }
        else if(TOOLBOX_PKG_CALENDAR.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_calendar));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_calendar_title));

            Intent intent = new Intent();
            intent.setComponent((new ComponentName("com.android.calendar", pkgName)));
            appInfo.setIntent(intent);
        }
        else if(TOOLBOX_PKG_BRIGHT.equals(pkgName))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_bright));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fanmenu_toolbox_bright_title));
        }

        return appInfo;
    }

    public static void queryAppInfo(Context context, List<AppInfo> listAppInfo) {
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_UNINSTALLED_PACKAGES);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
        if (listAppInfo != null) {
            listAppInfo.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
                // 为应用程序的启动Activity 准备Intent
                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName,
                        activityName));
                // 创建一个AppInfo对象，并赋值
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pkgName);
                appInfo.setAppIcon(icon);
                appInfo.setIntent(launchIntent);
                listAppInfo.add(appInfo); // 添加至列表中
                System.out.println(appLabel + " activityName---" + activityName
                        + " pkgName---" + pkgName);
            }
        }
    }

    public static void queryRecently(Context context, List<AppInfo> listAppInfo)
    {
        PackageManager pm = context.getPackageManager();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTaskInfo = manager.getRecentTasks(9, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        if (null != recentTaskInfo)
        {
            AppInfo appInfo;
            for(ActivityManager.RecentTaskInfo info : recentTaskInfo)
            {
                Intent intent = info.baseIntent;
                ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
                if(resolveInfo.activityInfo.packageName.contains(".launcher"))
                {
                    break;
                }
                appInfo = new AppInfo();
                appInfo.setAppLabel((String) resolveInfo.loadLabel(pm));
                appInfo.setPkgName(resolveInfo.activityInfo.packageName);
                appInfo.setAppIcon(resolveInfo.loadIcon(pm));
                appInfo.setIntent(intent);
                listAppInfo.add(appInfo);
                System.out.println("queryRecently activityName---" + appInfo.getAppLabel());
            }
        }

    }
}
