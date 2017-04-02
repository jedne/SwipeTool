package com.jeden.fanmenu.common.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.util.FanLog;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeFlashLight extends SwipeTools {
    private static final String TAG = SwipeFlashLight.class.getSimpleName();
    private volatile static SwipeFlashLight mInstance;
    private Camera mCamera;
    private Parameters mCameraParameters;
    private static String previousFlashMode = null;
    private boolean mIsOpen;

    private SwipeFlashLight(Context context) {
    }

    public static SwipeFlashLight getInstance(Context context) {
        if (mInstance == null) {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context) {
        mInstance = new SwipeFlashLight(context);
    }

    @Override
    public void viewBind(Context context) {
        changeViewState(context);
    }

    @Override
    public void changeState(Context context) {
        onAndOff(context);
        changeViewState(context);
    }

    private void changeViewState(Context context) {
        SwipeView view = mSwipeView.get();
        if (view == null) {
            return;
        }
        Resources rs = context.getResources();
        if (mIsOpen) {
            view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_lightbulb_on));
        } else {
            view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_lightbulb_off));
        }
    }

    public synchronized void openCamera(Context context) {
        try {
            mCamera = Camera.open();
            mCamera.setPreviewTexture(new SurfaceTexture(0));
        } catch (Exception e) {
            close();

            Log.w(TAG, "openCamera: ", e);

            //这里是打不开的情况，比如别人正在使用的灯泡，打不开用一个close关一次，下次就能打开了。
        }
        if (mCamera != null) {
            mCameraParameters = mCamera.getParameters();
            previousFlashMode = mCameraParameters.getFlashMode();
        }
        if (previousFlashMode == null) {
            // could be null if no flash, i.e. emulator
            previousFlashMode = Camera.Parameters.FLASH_MODE_OFF;
        }
    }

    public synchronized void close() {
        //关灯，就是用完之后清除一下camera对象，不然会影响其他设备的正常使用
        if (mCamera != null) {
            mCameraParameters.setFlashMode(previousFlashMode);
            mCamera.setParameters(mCameraParameters);
            mCamera.release();
            mCamera = null;
            mIsOpen = false;
        }
    }

    public synchronized void onAndOff(Context context) {

        try {
            if (mIsOpen) {
                off();
            } else if (!mIsOpen) {
                on(context);
            }
            // send broadcast for widget
            //调完之后可以通知界面更新
        } catch (RuntimeException e) {
            FanLog.e(TAG, "onAndOff e:" + e);
        }
    }

    private synchronized void on(Context context) {
        if (mCamera == null) {
            openCamera(context);
        }
        if (mCamera != null) {
            mIsOpen = true;
            mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mCameraParameters);
            mCamera.startPreview();
        }
    }

    private synchronized void off() {
        if (mCamera != null) {
            mIsOpen = false;
            mCamera.stopPreview();
            mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mCameraParameters);
        }
        close();
    }
}
