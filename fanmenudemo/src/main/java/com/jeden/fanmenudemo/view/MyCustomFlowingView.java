package com.jeden.fanmenudemo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jeden.fanmenudemo.tools.MyCustomMenuManager;

import java.lang.reflect.Field;

/**
 * Created by jeden on 2017/3/14.
 */

public class MyCustomFlowingView extends ImageView{

    private int mSreenWidth;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    public MyCustomFlowingView(Context context) {
        super(context);
        mSreenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public MyCustomFlowingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCustomFlowingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置

                mParams.x = (int) (xInScreen - xInView);
                mParams.y = (int) (yInScreen - yInView);
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    openFanMenu();
                }
                else
                {
                    slideToEdge();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params
     *            小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        MyCustomMenuManager.updateFlowingPosition(getContext(), mParams);
    }

    private void slideToEdge(){
        if(mParams.x != 0 || mParams.x != mSreenWidth)
        {
            int endX = mParams.x > mSreenWidth / 2 ? mSreenWidth : 0;
            ValueAnimator va = ValueAnimator.ofInt(mParams.x, endX);
            va.setDuration(150);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int values = (Integer) animation.getAnimatedValue();
                    mParams.x = values;
                    updateViewPosition();
                }
            });
            va.start();
        }
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void openFanMenu() {
        MyCustomMenuManager.showFanMenuView(getContext());
        MyCustomMenuManager.removeFlowingView(getContext());
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
