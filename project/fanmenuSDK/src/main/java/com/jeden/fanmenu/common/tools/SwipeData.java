package com.jeden.fanmenu.common.tools;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;

import com.jeden.fanmenu.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeData extends SwipeTools {

    private volatile static SwipeData mInstance;
    private boolean mDataAvailable = false;

    private SwipeData(Context context) {
    }

    public static SwipeData getInstance(Context context) {
        if (mInstance == null) {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context) {
        mInstance = new SwipeData(context);
    }

    @Override
    public void viewBind(Context context) {
        changeViewState(context);
    }

    @Override
    public void changeState(Context context) {
        setMobileDataEnabled(context, !isMobileDataEnable(context));
        changeViewState(context);
    }

    private void changeViewState(Context context) {
        SwipeView view = mSwipeView.get();
        if (view == null) {
            return;
        }

        Resources rs = context.getResources();
        if (isMobileDataEnable(context)) {
            view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_data_on));
            showToast(context, rs.getString(R.string.fan_menu_toolbox_data_on));
        } else {
            view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_data_off));
            if (mDataAvailable)
                showToast(context, rs.getString(R.string.fan_menu_toolbox_data_off));
        }
    }

    private void setMobileDataEnabled(Context context, boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class
                    .forName(conman.getClass().getName());
            final Field connectivityManagerField = conmanClass
                    .getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager = connectivityManagerField
                    .get(conman);
            final Class connectivityManagerClass = Class
                    .forName(connectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = connectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
            mDataAvailable = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        }
    }

    private void tryToSetMobileDataEnabled(Context context, boolean arg0) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> mClass = mConnectivityManager.getClass();
        Class<?>[] mClass2 = new Class[1];
        mClass2[0] = boolean.class;
        try {
            Method method = mClass.getMethod("setMobileDataEnabled", mClass2);
            method.invoke(mConnectivityManager, arg0);
            mDataAvailable = true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private boolean isMobileDataEnable(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobileDataEnabled;
    }
}
