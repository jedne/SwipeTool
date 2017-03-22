package com.jeden.fanmenudemo.tools;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.jeden.fanmenudemo.R;
import com.jeden.fanmenudemo.view.MyCustomFanRootView;
import com.jeden.fanmenudemo.view.MyCustomFlowingView;
import com.jeden.fanmenudemo.view.base.PositionState;


/**
 * Created by Administrator on 2017/3/12.
 */

public class MyCustomMenuManager {

    private static final String TAG = MyCustomMenuManager.class.getSimpleName();
    private Context mContext;
    private WindowManager mWindowManager;
    private LayoutInflater mLayoutInflater;
    private MyCustomFanRootView mFanRootView;
    private MyCustomFlowingView mFlowingView;
    private WindowManager.LayoutParams mFlowingViewLP;

    private static MyCustomMenuManager mInstance;

    private MyCustomMenuManager(Context context, WindowManager wm){
        this.mContext = context;
        this.mWindowManager = wm;

        this.mLayoutInflater = LayoutInflater.from(context);
    }

    private void initFlowingView(){
        if(mFlowingView != null)
        {
            return;
        }

        mFlowingView = new MyCustomFlowingView(mContext);
        mFlowingView.setBackgroundResource(R.drawable.fan_menu_whitedot_pressed);
    }

    private void initFanMenuView(){

        if(mFanRootView != null) {
            return;
        }
        mFanRootView = (MyCustomFanRootView)mLayoutInflater.inflate(R.layout.my_custom_fan_menu, null);
    }

    private WindowManager.LayoutParams generateFanMenuLP(){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
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

    public void showFlowing(){
        initFlowingView();
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

    public void updateFlowingPosition(WindowManager.LayoutParams params){
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

    public void showFanMenu(){
        initFanMenuView();
        if(mFanRootView.getParent() != null) {
            Log.w(TAG, "fan menu have showed");
            return;
        }
        mFanRootView.initFanMenuConfig();
        mWindowManager.addView(mFanRootView, generateFanMenuLP());
    }

    public void removeFlowing(){
        if(mWindowManager != null && mFlowingView != null && mFlowingView.getParent() != null){
            mWindowManager.removeView(mFlowingView);
        }
    }

    public void removeFanMenu(){
        if(mWindowManager != null && mFanRootView != null && mFanRootView.getParent() != null){
            mWindowManager.removeView(mFanRootView);
        }
    }

    public synchronized static MyCustomMenuManager getInstance(Context context){
        if(mInstance == null){
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mInstance = new MyCustomMenuManager(context, windowManager);
        }
        return mInstance;
    }

    public static void showFlowingView(Context context){
        getInstance(context).showFlowing();
    }

    public static void showFanMenuView(Context context) {
        getInstance(context).showFanMenu();
    }

    public static void removeFlowingView(Context context){
        getInstance(context).removeFlowing();
    }

    public static void removeFanMenuView(Context context){
        getInstance(context).removeFanMenu();
    }

    public static void updateFlowingPosition(Context context, WindowManager.LayoutParams params){
        getInstance(context).updateFlowingPosition(params);
    }
}
