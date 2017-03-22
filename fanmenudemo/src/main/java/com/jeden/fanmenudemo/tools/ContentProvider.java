package com.jeden.fanmenudemo.tools;

import com.jeden.fanmenudemo.bean.AppInfo;

import java.util.List;

/**
 * Created by jeden on 2017/3/15.
 */

public class ContentProvider {
    private static ContentProvider mInstance = new ContentProvider();
    private List<AppInfo> mFavorates;
    private List<AppInfo> mRecentlys;
    private List<AppInfo> mToolBoxs;

    private ContentProvider(){}
    public static ContentProvider getmInstance(){
        return mInstance;
    }

    public List<AppInfo> getmFavorates() {
        return mFavorates;
    }

    public void setmFavorates(List<AppInfo> mFavorates) {
        this.mFavorates = mFavorates;
    }

    public List<AppInfo> getmRecentlys() {
        return mRecentlys;
    }

    public void setmRecentlys(List<AppInfo> mRecentlys) {
        this.mRecentlys = mRecentlys;
    }

    public List<AppInfo> getmToolBoxs() {
        return mToolBoxs;
    }

    public void setmToolBoxs(List<AppInfo> mToolBoxs) {
        this.mToolBoxs = mToolBoxs;
    }
}
