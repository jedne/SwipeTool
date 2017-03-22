package com.jeden.fanmenudemo.view.base;

import android.view.View;

/**
 * Created by jeden on 2017/3/16.
 */

public interface MenuLayoutStateChangeable {
    void selectCardChange(int selectState);
    void longClickStateChange(View view);
}
