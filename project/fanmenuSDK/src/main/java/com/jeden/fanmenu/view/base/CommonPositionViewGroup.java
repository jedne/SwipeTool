package com.jeden.fanmenu.view.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by jeden on 2017/3/14.
 */

public class CommonPositionViewGroup extends ViewGroup {

    private boolean mIsLeft = false;

    public MenuLayoutStateChangeable mStateChangeable = null;

    public boolean mDisableTouchEvent = false;

    public CommonPositionViewGroup(Context context) {
        super(context);
    }

    public CommonPositionViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonPositionViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void setPositionState(int state) {
        this.mIsLeft = state == PositionState.POSITION_STATE_LEFT;
//        requestLayout();
    }

    public void setMenuStateChangeListener(MenuLayoutStateChangeable changeable)
    {
        this.mStateChangeable = changeable;
    }

    public void setDisableTouchEvent(boolean disable)
    {
        this.mDisableTouchEvent = disable;
    }

    public boolean isLeft() {
        return mIsLeft;
    }
}
