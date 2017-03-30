package com.jeden.fanmenu.common.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.jeden.fanmenu.R;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeRotate extends SwipeTools {
    private static final String TAG = SwipeRotate.class.getSimpleName();

    private volatile static SwipeRotate mInstance;

    private SwipeRotate(Context context) {
    }

    public static SwipeRotate getInstance(Context context) {
        if (mInstance == null) {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context) {
        mInstance = new SwipeRotate(context);
    }

    @Override
    public void viewBind(Context context) {
        changeViewState(context);
    }

    @Override
    public void changeState(Context context) {
        setRotationStatus(context, getRotationStatus(context) == 1 ? 0 : 1);
        changeViewState(context);
    }

    private void changeViewState(Context context) {
        SwipeView view = mSwipeView.get();
        if (view == null) {
            return;
        }

        Resources rs = context.getResources();
        if (getRotationStatus(context) == 1) {
            view.getIconView().setBackgroundResource(R.drawable.fan_item_icon_rotate_on);
            showToast(context, rs.getString(R.string.fan_menu_toolbox_rotate_on));
        } else {
            view.getIconView().setBackgroundResource(R.drawable.fan_item_icon_rotate_off);
            showToast(context, rs.getString(R.string.fan_menu_toolbox_rotate_off));
        }
    }

    private int getRotationStatus(Context context) {
        int status = 0;
        try {
            status = android.provider.Settings.System.getInt(context.getContentResolver(),
                    android.provider.Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.w(TAG, "getRotationStatus: ", e);
        }
        return status;
    }

    /**
     * 设置
     *
     * @param status
     */
    private void setRotationStatus(Context context, int status) {
        ContentResolver resolver = context.getContentResolver();
        //得到uri
        Uri uri = android.provider.Settings.System.getUriFor("accelerometer_rotation");
        //沟通设置status的值改变屏幕旋转设置
        android.provider.Settings.System.putInt(resolver, "accelerometer_rotation", status);
        //通知改变
        resolver.notifyChange(uri, null);
    }
}
