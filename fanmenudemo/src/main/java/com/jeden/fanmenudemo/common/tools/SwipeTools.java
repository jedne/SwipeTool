package com.jeden.fanmenudemo.common.tools;

import android.content.Context;

import com.jeden.fanmenudemo.common.MyCustomMenuManager;

import java.lang.ref.WeakReference;

/**
 * Created by jeden on 2017/3/23.
 */

public abstract class SwipeTools {
    protected WeakReference<SwipeView> mSwipeView;
    protected boolean mCloseAfterChange = false;
    private boolean mBindOver = false;
    public void bindSwipeView(Context context, SwipeView view)
    {
        mSwipeView = new WeakReference<>(view);
        viewBinded(context);
        mBindOver = true;
    }
    public abstract void viewBinded(Context context);
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
            MyCustomMenuManager.showToast(context, content);
        }
    }
}
