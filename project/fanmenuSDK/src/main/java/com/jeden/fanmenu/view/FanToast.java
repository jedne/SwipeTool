package com.jeden.fanmenu.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeden.fanmenu.R;
import com.jeden.fanmenu.common.FanMenuManager;

/**
 * Created by jeden on 2017/3/23.
 */

public class FanToast extends RelativeLayout {
    private static final long DEFAULT_TIME = 2500;
    private TextView mText;
    private RelativeLayout mLayout;
    private static final Handler mHandler = new Handler();
    private FanToastRunnable mRunnable = new FanToastRunnable();
    private boolean mProgressHidden = false;
    private int mOffsetY;

    public FanToast(Context context) {
        super(context);
    }

    public FanToast(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FanToast(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mText = (TextView) findViewById(R.id.fan_toast_text);
        mLayout = (RelativeLayout) findViewById(R.id.fan_toast_layout);
        mOffsetY = getResources().getDimensionPixelSize(R.dimen.fan_menu_toast_radius);
    }

    public void showToast(String content) {
        showToast(content, DEFAULT_TIME);
    }

    public void showToast(String content, long time) {
        if (content == null || content.length() <= 0) {
            hideToast();
            return;
        }

        mProgressHidden = false;
        mText.setText(content);
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, time);
    }

    public void showAnimator() {
        setTranslationY(mOffsetY);
        final ValueAnimator va = ValueAnimator.ofFloat(1.0f, 0f);
        va.setDuration(200);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mLayout.setAlpha((float) Math.cos(Math.PI / 2 * value));
                mLayout.setTranslationY(value * mOffsetY);
            }
        });
        va.start();
    }

    public void hideToast() {
        mProgressHidden = true;
        ValueAnimator va = ValueAnimator.ofFloat(0f, 1.0f);
        va.setDuration(200);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mLayout.setAlpha((float) Math.cos(Math.PI / 2 * value));
                mLayout.setTranslationY(value * mOffsetY);
            }
        });

        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mProgressHidden) {
                    FanMenuManager.removeToast(getContext());
                    mProgressHidden = false;
                } else {
                    showAnimator();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        va.start();
    }

    private class FanToastRunnable implements Runnable {
        @Override
        public void run() {
            hideToast();
        }
    }
}
