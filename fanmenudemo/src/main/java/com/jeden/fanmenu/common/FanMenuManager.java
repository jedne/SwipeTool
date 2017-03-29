package com.jeden.fanmenu.common;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.common.model.FanMenuConfig;
import com.jeden.fanmenu.view.FanToast;
import com.jeden.fanmenu.view.FanRootView;
import com.jeden.fanmenu.view.FanFlowingView;
import com.jeden.fanmenu.view.FanMenuDialog;
import com.jeden.fanmenu.view.base.PositionState;


/**
 * Created by Administrator on 2017/3/12.
 */

public class FanMenuManager {

    private static final String TAG = FanMenuManager.class.getSimpleName();
    private Context mContext;
    private WindowManager mWindowManager;
    private LayoutInflater mLayoutInflater;
    private FanRootView mFanRootView;
    private FanFlowingView mFlowingView;
    private FanMenuDialog mDialogView;
    private FanToast mFanToastView;
    private WindowManager.LayoutParams mFlowingViewLP;

    private static volatile FanMenuManager mInstance;

    private FanMenuManager(Context context, WindowManager wm){
        this.mContext = context;
        this.mWindowManager = wm;

        this.mLayoutInflater = LayoutInflater.from(context);

        initFlowingView();
        initFanMenuView();
        initDialogView();
        initToastView();
    }

    private void initFlowingView(){
        if(mFlowingView != null)
        {
            return;
        }

        mFlowingView = new FanFlowingView(mContext);
        mFlowingView.setBackgroundResource(R.drawable.fan_menu_whitedot_pressed);
    }

    private void initFanMenuView(){

        if(mFanRootView != null) {
            return;
        }
        mFanRootView = (FanRootView)mLayoutInflater.inflate(R.layout.fan_menu_root_layout, null);
    }

    private void initDialogView()
    {
        if(mDialogView != null)
        {
            return;
        }
        mDialogView = (FanMenuDialog) mLayoutInflater.inflate(R.layout.fan_dialog_layout, null);
    }

    private void initToastView()
    {
        if(mFanToastView != null)
        {
            return;
        }
        mFanToastView = (FanToast) mLayoutInflater.inflate(R.layout.fan_toast_layout, null);
    }

    private WindowManager.LayoutParams generateFanMenuLP(){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);
        return lp;
    }

    private WindowManager.LayoutParams generateFlowingLP(){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        lp.format = PixelFormat.RGBA_8888;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        FanMenuConfig config = FanMenuConfig.getMenuConfig();
        if(config.getFlowingY() == 0)
        {
            Point p = new Point();
            mWindowManager.getDefaultDisplay().getSize(p);
            lp.x = p.x;
            lp.y = (p.y / 2);
            config.setFlowingX(p.x);
            config.setFlowingY(p.y);
            config.setPositionState(PositionState.POSITION_STATE_RIGHT);
        }
        else
        {
            lp.x = config.getFlowingX();
            lp.y = config.getFlowingY();
        }
        return lp;
    }

    private WindowManager.LayoutParams generateToastLP(){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.x = 0;
        lp.y = 0;
        return lp;
    }

    private void showFlowing(){
        if(mFlowingView.getParent() != null) {
            Log.w(TAG, "flowing view have showed");
            return;
        }

        if(mFlowingViewLP == null){
            mFlowingViewLP = generateFlowingLP();
        }
        mFlowingView.setParams(mFlowingViewLP);
        mWindowManager.addView(mFlowingView, mFlowingViewLP);
    }

    private void updateFlowingPosition(WindowManager.LayoutParams params){
        if(mFlowingView != null && mFlowingView.getParent() != null)
        {
            mFlowingViewLP = params;
            FanMenuConfig.getMenuConfig().setFlowingX(mFlowingViewLP.x);
            FanMenuConfig.getMenuConfig().setFlowingY(mFlowingViewLP.y);
            FanMenuConfig.getMenuConfig().setPositionState(mFlowingViewLP.x == 0 ?
                    PositionState.POSITION_STATE_LEFT : PositionState.POSITION_STATE_RIGHT);
            mWindowManager.updateViewLayout(mFlowingView, params);
        }
    }

    private void showFanMenu(){
        if(mFanRootView.getParent() != null) {
            Log.w(TAG, "fan menu have showed");
            return;
        }
        mFanRootView.initFanMenuConfig();
        mWindowManager.addView(mFanRootView, generateFanMenuLP());
        mFanRootView.showFanMenu();
    }

    private void removeFlowing(){
        if(mWindowManager != null && mFlowingView != null && mFlowingView.getParent() != null){
            mWindowManager.removeView(mFlowingView);
        }
    }

    private void removeFanMenu(){
        if(mWindowManager != null && mFanRootView != null && mFanRootView.getParent() != null){
            mWindowManager.removeView(mFanRootView);
        }
    }

    private void showToast(String content)
    {
        if(mFanToastView.getParent() == null)
        {
            mWindowManager.addView(mFanToastView, generateToastLP());
            mFanToastView.showAnimator();
        }
        mFanToastView.showToast(content);
    }

    private void removeToast()
    {
        if(mWindowManager != null && mFanToastView != null && mFanToastView.getParent() != null){
            mWindowManager.removeView(mFanToastView);
        }
    }

    private void showDialog(int selectCard, FanMenuDialog.DialogSubmitListener listener)
    {
        if(mDialogView.getParent() != null) {
            Log.w(TAG, "dialog have showed");
            return;
        }
        mDialogView.initView(selectCard, listener);
        mWindowManager.addView(mDialogView, generateFanMenuLP());
        mDialogView.showDialog();
    }

    private void removeDialog()
    {
        if(mWindowManager != null && mDialogView != null && mDialogView.getParent() != null){
            mWindowManager.removeView(mDialogView);
        }
    }

    private static FanMenuManager getInstance(Context context){
        if(mInstance == null){
            synchronized (FanMenuManager.class)
            {
                if(mInstance == null)
                {
                    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    mInstance = new FanMenuManager(context, windowManager);
                }
            }
        }
        return mInstance;
    }

    public static void showFlowingView(Context context){
        getInstance(context).showFlowing();
    }

    public static void removeFlowingView(Context context){
        getInstance(context).removeFlowing();
    }

    public static void updateFlowingPosition(Context context, WindowManager.LayoutParams params){
        getInstance(context).updateFlowingPosition(params);
    }

    public static void closeFanMenu(Context context)
    {
        getInstance(context).removeFanMenu();
        getInstance(context).showFlowing();
    }

    public static void showFanMenu(Context context)
    {
        getInstance(context).showFanMenu();
        getInstance(context).removeFlowing();
    }

    public static void closeDialog(Context context)
    {
        getInstance(context).removeDialog();
    }

    public static void showDialog(Context context, int selectCard, FanMenuDialog.DialogSubmitListener listener)
    {
        getInstance(context).showDialog(selectCard, listener);
    }

    public static void showToast(Context context, String content)
    {
        getInstance(context).showToast(content);
    }

    public static void removeToast(Context context)
    {
        getInstance(context).removeToast();
    }
}
