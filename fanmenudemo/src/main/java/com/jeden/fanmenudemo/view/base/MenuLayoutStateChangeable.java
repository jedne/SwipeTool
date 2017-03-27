package com.jeden.fanmenudemo.view.base;

import android.view.View;

import com.jeden.fanmenudemo.bean.AppInfo;

/**
 * Created by jeden on 2017/3/16.
 */

public interface MenuLayoutStateChangeable {
    void selectCardChange(int selectState);
    void longClickStateChange(View view, boolean isEditMode);
    void dragViewAndRefresh(float x, float y, AppInfo appInfo, boolean hidden, boolean isToolbox);
    void addBtnClicked(View view, int selectCard);
    void menuItemClicked(View view, AppInfo appInfo);
}
