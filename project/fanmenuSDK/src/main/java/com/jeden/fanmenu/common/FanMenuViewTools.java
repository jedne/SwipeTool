package com.jeden.fanmenu.common;

import android.content.Context;

/**
 * Created by jeden on 2017/3/13.
 */

public class FanMenuViewTools {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float getTwoPointDistance(float x1, float y1, float x2, float y2)
    {
        return  (float) Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
    }

    public static double getDegreeByPoint(float x, float y)
    {
        return Math.toDegrees(Math.atan(x / y));
    }
}
