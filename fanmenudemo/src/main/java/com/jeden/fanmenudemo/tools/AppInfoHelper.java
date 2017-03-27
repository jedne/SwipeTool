package com.jeden.fanmenudemo.tools;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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
        toolbox.add(generateToolbox(rs, "screenshot"));
        toolbox.add(generateToolbox(rs, "alarm"));
        toolbox.add(generateToolbox(rs, "bluetooth"));
        toolbox.add(generateToolbox(rs, "calculator"));
        toolbox.add(generateToolbox(rs, "flight"));
        toolbox.add(generateToolbox(rs, "locker"));
        toolbox.add(generateToolbox(rs, "rotate"));
        toolbox.add(generateToolbox(rs, "wifi"));
        toolbox.add(generateToolbox(rs, "camera"));
        toolbox.add(generateToolbox(rs, "shake"));
        toolbox.add(generateToolbox(rs, "setting"));
        toolbox.add(generateToolbox(rs, "lightbulb"));
        toolbox.add(generateToolbox(rs, "iswip"));
        toolbox.add(generateToolbox(rs, "data"));
        toolbox.add(generateToolbox(rs, "calendar"));
        toolbox.add(generateToolbox(rs, "bright"));
    }

    public static AppInfo generateToolbox(Resources rs, String name)
    {
        AppInfo appInfo = new AppInfo();
        if("screenshot".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_screenshot));
            appInfo.setPkgName("com.system.screenshot");
            appInfo.setAppLabel("截屏");
        }
        else if("alarm".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_alarm));
            appInfo.setPkgName("com.system.alarm");
            appInfo.setAppLabel("闹钟");
        }
        else if("bluetooth".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_bluetooth));
            appInfo.setPkgName("com.system.bluetooth");
            appInfo.setAppLabel("蓝牙");
        }
        else if("calculator".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_calculator));
            appInfo.setPkgName("com.system.calculator");
            appInfo.setAppLabel("计算器");
        }
        else if("flight".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_flight));
            appInfo.setPkgName("com.system.flight");
            appInfo.setAppLabel("飞行模式");
        }
        else if("locker".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_locker));
            appInfo.setPkgName("com.system.locker");
            appInfo.setAppLabel("锁屏");
        }
        else if("rotate".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_rotate));
            appInfo.setPkgName("com.system.rotate");
            appInfo.setAppLabel("自动旋转");
        }
        else if("wifi".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_wifi));
            appInfo.setPkgName("com.system.wifi");
            appInfo.setAppLabel("WIFI");
        }
        else if("camera".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_camera));
            appInfo.setPkgName("com.system.camera");
            appInfo.setAppLabel("照相机");
        }
        else if("shake".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_shake));
            appInfo.setPkgName("com.system.shake");
            appInfo.setAppLabel("震动");
        }
        else if("setting".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_setting));
            appInfo.setPkgName("com.system.setting");
            appInfo.setAppLabel("设置");
        }
        else if("lightbulb".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_lightbulb));
            appInfo.setPkgName("com.system.lightbulb");
            appInfo.setAppLabel("手电筒");
        }
        else if("iswip".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_iswip));
            appInfo.setPkgName("com.system.iswip");
            appInfo.setAppLabel("菜单设置");
        }
        else if("data".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_data));
            appInfo.setPkgName("com.system.data");
            appInfo.setAppLabel("移动数据");
        }
        else if("calendar".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_calendar));
            appInfo.setPkgName("com.system.calendar");
            appInfo.setAppLabel("日历");
        }
        else if("bright".equals(name))
        {
            appInfo.setAppIcon(rs.getDrawable(R.drawable.fan_item_icon_bright));
            appInfo.setPkgName("com.system.bright");
            appInfo.setAppLabel("屏幕亮度");
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
