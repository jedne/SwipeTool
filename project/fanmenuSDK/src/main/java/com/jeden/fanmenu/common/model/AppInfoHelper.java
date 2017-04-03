package com.jeden.fanmenu.common.model;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.util.Log;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.bean.AppInfo;
import com.jeden.fanmenu.util.FanLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.List;

/**
 * Created by jeden on 2017/3/22.
 */

public class AppInfoHelper {
    public static final String TAG = AppInfoHelper.class.getSimpleName();

    public static final String TOOLBOX_PKG_COMMON_HEADER = "#com.system.";
    public static final String TOOLBOX_PKG_SCREENSHOT = TOOLBOX_PKG_COMMON_HEADER + "screenshot";
    public static final String TOOLBOX_PKG_ALARM = "com.system.alarm";
    public static final String TOOLBOX_PKG_BLUETOOTH = TOOLBOX_PKG_COMMON_HEADER + "bluetooth";
    public static final String TOOLBOX_PKG_CALCULATOR = TOOLBOX_PKG_COMMON_HEADER + "calculator";
    public static final String TOOLBOX_PKG_FLIGHT = "com.system.flight";
    public static final String TOOLBOX_PKG_LOCKER = TOOLBOX_PKG_COMMON_HEADER + "locker";
    public static final String TOOLBOX_PKG_ROTATE = TOOLBOX_PKG_COMMON_HEADER + "rotate";
    public static final String TOOLBOX_PKG_WIFI = TOOLBOX_PKG_COMMON_HEADER + "wifi";
    public static final String TOOLBOX_PKG_CAMERA = "android.media.action.STILL_IMAGE_CAMERA";
    public static final String TOOLBOX_PKG_AUDIO = TOOLBOX_PKG_COMMON_HEADER + "audio";
    public static final String TOOLBOX_PKG_SETTING = "com.system.setting";
    public static final String TOOLBOX_PKG_LIGHTBULB = TOOLBOX_PKG_COMMON_HEADER + "lightbulb";
    public static final String TOOLBOX_PKG_ISWIP = TOOLBOX_PKG_COMMON_HEADER + "iswip";
    public static final String TOOLBOX_PKG_DATA = TOOLBOX_PKG_COMMON_HEADER + "data";
    public static final String TOOLBOX_PKG_CALENDAR = "com.android.calendar.LaunchActivity";
    public static final String TOOLBOX_PKG_BRIGHT = TOOLBOX_PKG_COMMON_HEADER + "bright";

    public static String generatePkgStr(List<AppInfo> appInfos) {
        String result;
        JSONArray jsonArray = new JSONArray();
        for (AppInfo appInfo : appInfos) {
            jsonArray.put(appInfo.getPkgName());
        }

        result = jsonArray.toString();

        return result;
    }

    public static void initToolboxByCache(List<AppInfo> alltoolbox, List<AppInfo> toolbox, String cache) {
        boolean firstTime = false;
        if ("".equals(cache)) {
            firstTime = true;
        }

        if (firstTime) {
            initToolboxFirstTime(alltoolbox, toolbox);
        } else {
            initDataByCache(alltoolbox, toolbox, cache);
        }
    }

    public static void initFavoriteByCache(List<AppInfo> allApp, List<AppInfo> favorite, String cache) {
        boolean firstTime = false;
        if ("".equals(cache)) {
            firstTime = true;
        }

        if (firstTime) {
            initFavoriteFirstTime(allApp, favorite);
        } else {
            initDataByCache(allApp, favorite, cache);
        }
    }

    public static void initRecentlyByCache(List<AppInfo> allApp, List<AppInfo> recently, String cache) {
        boolean firstTime = false;
        if ("".equals(cache)) {
            firstTime = true;
        }

        if (firstTime) {
            initRecentlyFirstTime(allApp, recently);
        } else {
            initDataByCache(allApp, recently, cache);
        }
    }

    private static void initToolboxFirstTime(List<AppInfo> allApp, List<AppInfo> needApp) {
        for (int i = 0; i < allApp.size(); i++) {
            AppInfo appInfo = allApp.get(i);
            if (i < 8) {
                needApp.add(appInfo);
            }
        }
    }

    private static void initFavoriteFirstTime(List<AppInfo> allApp, List<AppInfo> needApp) {
        for (int i = 0; i < allApp.size(); i++) {
            AppInfo appInfo = allApp.get(i);
            if (i < 8) {
                needApp.add(appInfo);
            }
        }
    }

    private static void initRecentlyFirstTime(List<AppInfo> allApp, List<AppInfo> needApp) {
        for (int i = 0; i < allApp.size(); i++) {
            AppInfo appInfo = allApp.get(i);
            if (i < 9 && needApp.size() < 9) {
                needApp.add(appInfo);
            } else {
                return;
            }
        }
    }

    private static void initDataByCache(List<AppInfo> allApp, List<AppInfo> favorite, String cache) {
        JSONArray array = null;
        try {
            array = new JSONArray(cache);
        } catch (JSONException e) {
            FanLog.e(TAG, "initDataByCache exception cache:" + cache);
        }

        if (array == null || favorite.size() >= 9) {
            return;
        }

        for (int j = 0; j < array.length(); j++) {
            String pk = array.optString(j);
            for (int i = 0; i < allApp.size(); i++) {
                AppInfo appInfo = allApp.get(i);
                if (pk != null && appInfo.getPkgName().equals(pk)) {
                    favorite.add(appInfo);
                    if (favorite.size() >= 9) {
                        return;
                    }
                    break;
                }
            }
        }
    }

    public static void initToolbox(Context context, List<AppInfo> toolbox) {
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
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_AUDIO));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_SETTING));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_LIGHTBULB));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_ISWIP));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_DATA));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_CALENDAR));
        toolbox.add(generateToolbox(rs, TOOLBOX_PKG_BRIGHT));
    }

    public static AppInfo generateToolbox(Resources rs, String pkgName) {
        AppInfo appInfo = new AppInfo();
        if (TOOLBOX_PKG_SCREENSHOT.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_screenshot)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_screenshot_title));
        } else if (TOOLBOX_PKG_ALARM.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_alarm)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_alarm_title));

            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            appInfo.setIntent(intent);
        } else if (TOOLBOX_PKG_BLUETOOTH.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_bluetooth_on)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_bluetooth_title));
        } else if (TOOLBOX_PKG_CALCULATOR.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_calculator)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_calculator_title));
        } else if (TOOLBOX_PKG_FLIGHT.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_flight_on)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_flight_title));

            Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            appInfo.setIntent(intent);
        } else if (TOOLBOX_PKG_LOCKER.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_locker_0)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_locker_title));
        } else if (TOOLBOX_PKG_ROTATE.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_rotate_on)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_rotate_title));
        } else if (TOOLBOX_PKG_WIFI.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_wifi_on)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_wifi_title));
        } else if (TOOLBOX_PKG_CAMERA.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_camera)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_camera_title));

            Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            appInfo.setIntent(intent);
        } else if (TOOLBOX_PKG_AUDIO.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_audio_1)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_audio_title));
        } else if (TOOLBOX_PKG_SETTING.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_setting)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_setting_title));

            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            appInfo.setIntent(intent);
        } else if (TOOLBOX_PKG_LIGHTBULB.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_lightbulb_on)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_lightbulb_title));
        } else if (TOOLBOX_PKG_ISWIP.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_iswip)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_iswip_title));
        } else if (TOOLBOX_PKG_DATA.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_data_on)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_data_title));
        } else if (TOOLBOX_PKG_CALENDAR.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_calendar)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_calendar_title));

            Intent intent = new Intent();
            intent.setComponent((new ComponentName("com.android.calendar", pkgName)));
            appInfo.setIntent(intent);
        } else if (TOOLBOX_PKG_BRIGHT.equals(pkgName)) {
            appInfo.setAppIcon(compressDrawable(rs.getDrawable(R.drawable.fan_item_icon_bright_1)));
            appInfo.setPkgName(pkgName);
            appInfo.setAppLabel(rs.getString(R.string.fan_menu_toolbox_bright_title));
        }

        return appInfo;
    }

    public static void queryAppInfo(Context context, List<AppInfo> listAppInfo) {
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_UNINSTALLED_PACKAGES);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        if (listAppInfo != null) {
            listAppInfo.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name;
                String pkgName = reInfo.activityInfo.packageName;
                String appLabel = (String) reInfo.loadLabel(pm);
                Drawable icon = reInfo.loadIcon(pm);
                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName,
                        activityName));
                // 创建一个AppInfo对象，并赋值
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pkgName);
                appInfo.setAppIcon(compressDrawable(icon));
                appInfo.setIntent(launchIntent);
                listAppInfo.add(appInfo);
                FanLog.v(TAG, appLabel + " activityName---" + activityName
                        + " pkgName---" + pkgName);
            }
        }
    }

    public static void queryRecently(Context context, List<AppInfo> listAppInfo) {
        PackageManager pm = context.getPackageManager();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTaskInfo = manager.getRecentTasks(9, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        if (null != recentTaskInfo) {
            AppInfo appInfo;
            for (ActivityManager.RecentTaskInfo info : recentTaskInfo) {
                Intent intent = info.baseIntent;
                ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
                if (resolveInfo.activityInfo.packageName.contains(".launcher")) {
                    break;
                }
                appInfo = new AppInfo();
                appInfo.setAppLabel((String) resolveInfo.loadLabel(pm));
                appInfo.setPkgName(resolveInfo.activityInfo.packageName);
                appInfo.setAppIcon(compressDrawable(resolveInfo.loadIcon(pm)));
                appInfo.setIntent(intent);
                listAppInfo.add(appInfo);
                FanLog.v(TAG, "queryRecently activityName---" + appInfo.getAppLabel());
            }
        }
    }

    public static Drawable compressDrawable(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldBmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) 50 / width);
        float scaleHeight = ((float) 50 / height);
        FanLog.v(TAG, "compressDrawable scaleW:" + scaleWidth + " scaleHeight:" + scaleHeight);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(oldBmp, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(newBmp);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
