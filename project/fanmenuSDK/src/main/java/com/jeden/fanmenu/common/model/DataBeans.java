package com.jeden.fanmenu.common.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.jeden.fanmenu.util.FanLog;
import com.jeden.fanmenu.view.base.CardState;
import com.jeden.fanmenu.view.base.PositionState;

/**
 * Created by jeden on 2017/3/15.
 */

public class DataBeans {
    public static final String TAG = DataBeans.class.getSimpleName();
    private static final String FAN_MENU_DATA_SP_NAME = "fan_menu_sp";
    private static final String FAN_MENU_CONFIG_FLOW_X = "fan_menu_config_flow_x";
    private static final String FAN_MENU_CONFIG_FLOW_Y = "fan_menu_config_flow_y";
    private static final String FAN_MENU_CONFIG_POSITION_STATE = "fan_menu_config_position_state";
    private static final String FAN_MENU_CONFIG_CARD_INDEX = "fan_menu_config_card_index";

    private static final String FAN_MENU_FAVORITE_LIST = "fan_menu_favorite_list";
    private static final String FAN_MENU_RECENTLY_LIST = "fan_menu_recently_list";
    private static final String FAN_MENU_TOOLBOX_LIST = "fan_menu_toolbox_list";

    private static DataBeans mInstance = new DataBeans();

    private SharedPreferences mSp;

    private DataBeans() {
    }

    public static DataBeans getInstance() {
        return mInstance;
    }

    public void initDataBeans(Context context) {
        if (mSp == null) {
            mSp = context.getSharedPreferences(FAN_MENU_DATA_SP_NAME, Context.MODE_APPEND);
        }
    }

    void saveConfig(FanMenuConfig config) {
        SharedPreferences.Editor ed = mSp.edit();
        ed.putInt(FAN_MENU_CONFIG_FLOW_X, config.getFlowingX());
        ed.putInt(FAN_MENU_CONFIG_FLOW_Y, config.getFlowingY());
        ed.putInt(FAN_MENU_CONFIG_POSITION_STATE, config.getPositionState());
        ed.putInt(FAN_MENU_CONFIG_CARD_INDEX, config.getCardIndex());
        ed.apply();

        FanLog.v(TAG, "saveConfig config" + config.toString());
    }

    void getConfig(FanMenuConfig config) {

        config.setFlowingX(mSp.getInt(FAN_MENU_CONFIG_FLOW_X, 0));
        config.setFlowingY(mSp.getInt(FAN_MENU_CONFIG_FLOW_Y, 0));
        config.setPositionState(mSp.getInt(FAN_MENU_CONFIG_POSITION_STATE, PositionState.POSITION_STATE_LEFT));
        config.setCardIndex(mSp.getInt(FAN_MENU_CONFIG_CARD_INDEX, CardState.CARD_STATE_RECENTLY));

        FanLog.v(TAG, "getConfig config:" + config.toString());
    }

    void saveFavorite(String favorite) {
        SharedPreferences.Editor ed = mSp.edit();
        ed.putString(FAN_MENU_FAVORITE_LIST, favorite);
        ed.apply();
    }

    String getFavorite() {
        return mSp.getString(FAN_MENU_FAVORITE_LIST, "");
    }

    void saveToolbox(String toolbox) {
        SharedPreferences.Editor ed = mSp.edit();
        ed.putString(FAN_MENU_TOOLBOX_LIST, toolbox);
        ed.apply();
    }

    String getToolbox() {
        return mSp.getString(FAN_MENU_TOOLBOX_LIST, "");
    }

    void saveRecently(String recently) {
        SharedPreferences.Editor ed = mSp.edit();
        ed.putString(FAN_MENU_RECENTLY_LIST, recently);
        ed.apply();
    }

    String getRecently() {
        return mSp.getString(FAN_MENU_RECENTLY_LIST, "");
    }
}
