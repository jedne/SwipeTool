package com.jeden.fanmenu.common.tools;

import android.content.Context;

import com.jeden.fanmenu.view.base.FanMenuManager;

import java.lang.ref.WeakReference;

/**
 * Created by jeden on 2017/3/23.
 */

public abstract class SwipeTools {
    protected WeakReference<SwipeView> mSwipeView;
    protected boolean mCloseAfterChange = false;
    protected boolean mBindOver = false;
    public void bindSwipeView(Context context, SwipeView view)
    {
        mBindOver = false;
        mSwipeView = new WeakReference<>(view);
        viewBind(context);
        mBindOver = true;
    }
    public abstract void viewBind(Context context);
    public abstract void changeState(Context context);

    public boolean changeStateWithResult(Context context)
    {
        changeState(context);
        return mCloseAfterChange;
    }

    protected void showToast(Context context, String content)
    {
        if(mBindOver)
        {
            FanMenuManager.showToast(context, content);
        }
    }
}
