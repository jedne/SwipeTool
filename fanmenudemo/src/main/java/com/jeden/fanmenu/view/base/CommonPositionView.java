package com.jeden.fanmenu.view.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jeden on 2017/3/14.
 */

public class CommonPositionView extends View {

    private int mPositionState = PositionState.POSITION_STATE_RIGHT;

    public MenuLayoutStateChangeable mCardChangeable = null;

    public CommonPositionView(Context context) {
        super(context);
    }

    public CommonPositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonPositionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPositionState(int state) {
        this.mPositionState = state;
//        requestLayout();
    }

    public void setSelectCardChangeListener(MenuLayoutStateChangeable changeable)
    {
        this.mCardChangeable = changeable;
    }

    public int getPositionState() {
        return mPositionState;
    }

    public boolean isLeft() {
        return mPositionState == PositionState.POSITION_STATE_LEFT;
    }

    public boolean isRight() {
        return mPositionState == PositionState.POSITION_STATE_RIGHT;
    }
}
