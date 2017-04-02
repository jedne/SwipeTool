package com.jeden.fanmenu.common.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.PowerManager;
import android.provider.Settings;

import com.jeden.fanmenu.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeBrightness extends SwipeTools {
    private static final String TAG = SwipeBrightness.class.getSimpleName();

    private static final int LIGHT_NORMAL = 64;
    private static final int LIGHT_50_PERCENT = 127;
    private static final int LIGHT_75_PERCENT = 191;
    private static final int LIGHT_100_PERCENT = 255;
    private static final int LIGHT_AUTO = 0;
    private static final int LIGHT_ERR = -1;

    private volatile static SwipeBrightness mInstance;

    private PowerManager mPowerManager;

    private SwipeBrightness(Context context) {
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    public static SwipeBrightness getInstance(Context context) {
        if (mInstance == null) {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context) {
        mInstance = new SwipeBrightness(context);
    }

    @Override
    public void viewBind(Context context) {
        changeViewState(context);
    }

    @Override
    public void changeState(Context context) {

        setBrightStatus(context);

        changeViewState(context);
    }

    private void changeViewState(Context context) {
        SwipeView view = mSwipeView.get();
        if (view == null) {
            return;
        }
        Resources rs = context.getResources();

        int bright = getBrightStatus(context);
        switch (bright) {
            case LIGHT_NORMAL:
                view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_bright_3));
                break;
            case LIGHT_50_PERCENT:
            case LIGHT_75_PERCENT:
            case LIGHT_100_PERCENT:
                view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_bright_2));
                break;
            case LIGHT_AUTO:
                view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_bright_1));
                break;
            case LIGHT_ERR:
                break;
            default:
                break;
        }
    }

    private int getBrightStatus(Context context) {

        int light = 0;
        boolean auto = false;
        try {
            auto = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            if (!auto) {
                light = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
                if (light > 0 && light <= LIGHT_NORMAL) {
                    return LIGHT_NORMAL;
                } else if (light > LIGHT_NORMAL && light <= LIGHT_50_PERCENT) {
                    return LIGHT_50_PERCENT;
                } else if (light > LIGHT_50_PERCENT && light <= LIGHT_75_PERCENT) {
                    return LIGHT_75_PERCENT;
                } else if (light > LIGHT_75_PERCENT && light <= LIGHT_100_PERCENT) {
                    return LIGHT_100_PERCENT;
                }
            } else {
                return LIGHT_AUTO;
            }
        } catch (Settings.SettingNotFoundException e1) {
            e1.printStackTrace();
        }
        return LIGHT_ERR;

    }

    private void setBrightStatus(Context context) {
        int light = 0;

        switch (getBrightStatus(context)) {
            case LIGHT_NORMAL:
                light = LIGHT_50_PERCENT - 1;
                break;
            case LIGHT_50_PERCENT:
                light = LIGHT_75_PERCENT - 1;
                break;
            case LIGHT_75_PERCENT:
                light = LIGHT_100_PERCENT - 1;
                break;
            case LIGHT_100_PERCENT:
                startAutoBrightness(context.getContentResolver());
                break;
            case LIGHT_AUTO:
                light = LIGHT_NORMAL - 1;
                stopAutoBrightness(context.getContentResolver());
                break;
            case LIGHT_ERR:
                light = LIGHT_NORMAL - 1;
                break;

        }

        setLight(light);
        setScreenLightValue(context.getContentResolver(), light);
    }

    private void setLight(int light) {
        try {
            /**
             * 得到PowerManager类对应的Class对象
             */
            Class<?> pmClass = Class.forName(mPowerManager.getClass().getName());
            /**
             * 得到PowerManager类中的成员mService（mService为PowerManagerService类型）
             */
            Field field = pmClass.getDeclaredField("mService");
            field.setAccessible(true);
            /**
             * 实例化mService
             */
            Object iPM = field.get(mPowerManager);
            /**
             * 得到PowerManagerService对应的Class对象
             */
            Class<?> iPMClass = Class.forName(iPM.getClass().getName());
            /**
             * 得到PowerManagerService的函数setBacklightBrightness对应的Method对象，
             * PowerManager的函数setBacklightBrightness实现在PowerManagerService中
             */
            Method method = iPMClass.getDeclaredMethod("setBacklightBrightness", int.class);
            method.setAccessible(true);
            /**
             * 调用实现PowerManagerService的setBacklightBrightness
             */
            method.invoke(iPM, light);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * 启动自动调节亮度
     *
     * @param cr
     */
    private void startAutoBrightness(ContentResolver cr) {
        Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 关闭自动调节亮度
     *
     * @param cr
     */
    private void stopAutoBrightness(ContentResolver cr) {
        Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 设置改变亮度值
     *
     * @param resolver
     * @param value
     */
    private void setScreenLightValue(ContentResolver resolver, int value) {
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS,
                value);
    }
}
