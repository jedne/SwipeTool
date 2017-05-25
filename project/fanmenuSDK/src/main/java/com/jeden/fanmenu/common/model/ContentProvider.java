package com.jeden.fanmenu.common.model;

import android.content.Context;

import com.jeden.fanmenu.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeden on 2017/3/15.
 */

public class ContentProvider {

    private static ContentProvider mInstance = new ContentProvider();
    private List<AppInfo> mAllApps = new ArrayList<>();
    private List<AppInfo> mAllToolBox = new ArrayList<>();
    private List<AppInfo> mFavorite = new ArrayList<>();
    private List<AppInfo> mRecently = new ArrayList<>();
    private List<AppInfo> mToolBox = new ArrayList<>();

    private ContentProvider() {
    }

    public static ContentProvider getInstance() {
        return mInstance;
    }

    public static void initContentProvider(Context context) {
        mInstance.initData(context);
    }

    private void initData(Context context) {
        AppInfoHelper.queryAppInfo(context, mAllApps);
        AppInfoHelper.initToolbox(context, mAllToolBox);

        String toolboxStr = DataBeans.getInstance().getToolbox();
        AppInfoHelper.initToolboxByCache(mAllToolBox, mToolBox, toolboxStr);

        String favoriteStr = DataBeans.getInstance().getFavorite();
        AppInfoHelper.initFavoriteByCache(mAllApps, mFavorite, favoriteStr);

        initRecentlyData(context);
    }

    private void initRecentlyData(Context context) {
        String recentlyStr = DataBeans.getInstance().getRecently();
        AppInfoHelper.queryRecently(context, mRecently, mAllApps);
        AppInfoHelper.initRecentlyByCache(mAllApps, mRecently, recentlyStr);
        saveRecently();
    }

    public List<AppInfo> getFavorite() {
        return mFavorite;
    }

    public List<AppInfo> getRecently() {
        return mRecently;
    }

    public List<AppInfo> getToolBox() {
        return mToolBox;
    }

    public List<AppInfo> getAllApps() {
        return mAllApps;
    }

    public List<AppInfo> getAllToolBox() {
        return mAllToolBox;
    }

    public void saveFavorite() {
        String str = AppInfoHelper.generatePkgStr(mFavorite);
        DataBeans.getInstance().saveFavorite(str);
    }

    public void saveToolbox() {
        String str = AppInfoHelper.generatePkgStr(mToolBox);
        DataBeans.getInstance().saveToolbox(str);
    }

    public void saveRecently() {
        String str = AppInfoHelper.generatePkgStr(mRecently);
        DataBeans.getInstance().saveRecently(str);
    }

    public void updateRecently(Context context) {
        mRecently.clear();
        initRecentlyData(context);
    }
}
