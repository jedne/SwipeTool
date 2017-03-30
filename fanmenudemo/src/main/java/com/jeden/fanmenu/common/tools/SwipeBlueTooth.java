package com.jeden.fanmenu.common.tools;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Resources;

import com.jeden.fanmenu.R;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeBlueTooth extends SwipeTools {
    private volatile static SwipeBlueTooth mInstance;
    private BluetoothAdapter ba;

    private SwipeBlueTooth(Context context) {
        ba = BluetoothAdapter.getDefaultAdapter();
    }

    public static SwipeBlueTooth getInstance(Context context) {
        if (mInstance == null) {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context) {
        mInstance = new SwipeBlueTooth(context);
    }

    @Override
    public void viewBind(Context context) {
        changeViewState(context);
    }

    @Override
    public void changeState(Context context) {
        if (ba.isEnabled()) {
            ba.disable();
            changeViewState(context, false);
        } else {
            ba.enable();
            changeViewState(context, true);
        }

    }

    private void changeViewState(Context context) {
        changeViewState(context, ba.isEnabled());
    }

    private void changeViewState(Context context, boolean isEnable) {
        SwipeView view = mSwipeView.get();
        if (view == null) {
            return;
        }

        Resources rs = context.getResources();
        if (isEnable) {
            showToast(context, rs.getString(R.string.fan_menu_toolbox_bluetooth_on));
            view.getIconView().setBackgroundResource(R.drawable.fan_item_icon_bluetooth_on);
        } else {
            showToast(context, rs.getString(R.string.fan_menu_toolbox_bluetooth_off));
            view.getIconView().setBackgroundResource(R.drawable.fan_item_icon_bluetooth_off);
        }
    }
}
