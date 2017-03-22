package com.jeden.fanmenudemo.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;

import com.jeden.fanmenudemo.bean.AppInfo;
import com.jeden.fanmenudemo.tools.ContentProvider;
import com.jeden.fanmenudemo.tools.DataBeans;
import com.jeden.fanmenudemo.tools.FanMenuConfig;
import com.jeden.fanmenudemo.tools.MyCustomMenuManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyService extends Service {
    private static final String TAG = MyService.class.getSimpleName();
    private List<AppInfo> mlistAppInfo = new ArrayList<>();
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DataBeans.getInstance().initDataBeans(this);
        FanMenuConfig.getMenuConfig();

        queryAppInfo();

        List<AppInfo> favorates = new ArrayList<>();
        List<AppInfo> recentlys = new ArrayList<>();
        List<AppInfo> toolbox = new ArrayList<>();

        for(int i = 0; i < mlistAppInfo.size(); i++)
        {
            if(i < 9)
            {
                favorates.add(mlistAppInfo.get(i));
            }
            else if(i < 18)
            {
                recentlys.add(mlistAppInfo.get(i));
            }
            else if(i < 27)
            {
                toolbox.add(mlistAppInfo.get(i));
            }
        }

        ContentProvider.getmInstance().setmFavorates(favorates);
        ContentProvider.getmInstance().setmRecentlys(recentlys);
        ContentProvider.getmInstance().setmToolBoxs(toolbox);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFlowingView();
        return super.onStartCommand(intent, flags, startId);
    }

    public void showFlowingView(){
        MyCustomMenuManager.showFlowingView(getApplicationContext());
    }

    public void removeFlowingView(){
        MyCustomMenuManager.removeFlowingView(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        removeFlowingView();
        FanMenuConfig.saveMenuConfig();
        Log.v(TAG, "service onDestroy");
        super.onDestroy();
    }

    public void queryAppInfo() {
        PackageManager pm = this.getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_UNINSTALLED_PACKAGES);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
        if (mlistAppInfo != null) {
            mlistAppInfo.clear();
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
                mlistAppInfo.add(appInfo); // 添加至列表中
                System.out.println(appLabel + " activityName---" + activityName
                        + " pkgName---" + pkgName);
            }
        }
    }
}
