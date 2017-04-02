package com.jeden.fanmenu.common.tools;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;

import com.jeden.fanmenu.R;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeWifi extends SwipeTools {
    private volatile static SwipeWifi mInstance;
    private WifiManager mWifi;

    private SwipeWifi(Context context) {
        mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static SwipeWifi getInstance(Context context) {
        if (mInstance == null) {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context) {
        mInstance = new SwipeWifi(context);
    }

    @Override
    public void viewBind(Context context) {
        changeViewState(context);
    }

    @Override
    public void changeState(Context context) {
        if (mWifi.isWifiEnabled()) {
            mWifi.setWifiEnabled(false);
            changeViewState(context, false);
        } else {
            mWifi.setWifiEnabled(true);
            changeViewState(context, true);
        }
    }

    private void changeViewState(Context context) {
        changeViewState(context, mWifi.isWifiEnabled());
    }

    private void changeViewState(Context context, boolean isEnable) {
        SwipeView view = mSwipeView.get();
        if (view == null) {
            return;
        }

        Resources rs = context.getResources();
        if (isEnable) {
            view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_wifi_on));
            showToast(context, rs.getString(R.string.fan_menu_toolbox_wifi_on));
        } else {
            view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_wifi_off));
            showToast(context, rs.getString(R.string.fan_menu_toolbox_wifi_off));
        }
    }
}
