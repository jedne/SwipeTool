package com.jeden.fanmenudemo.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jeden.fanmenudemo.view.base.PositionState;
import com.jeden.fanmenudemo.view.base.SelectCardState;

/**
 * Created by jeden on 2017/3/15.
 */

public class DataBeans {
    public static final String TAG = DataBeans.class.getSimpleName();
    private static final String FANMENU_DATA_SP_NAME = "fan_menu_sp";
    private static final String FANMENU_CONFIG_FLOW_X = "fan_menu_config_flow_x";
    private static final String FANMENU_CONFIG_FLOW_Y = "fan_menu_config_flow_y";
    private static final String FANMENU_CONFIG_POSITION_STATE = "fan_menu_config_position_stage";
    private static final String FANMENU_CONFIG_SELECTTEXT_INDEX = "fan_menu_config_selecttext_index";
    private static DataBeans mInstance = new DataBeans();

    private SharedPreferences mSp;

    private DataBeans(){}

    public static DataBeans getInstance(){
        return mInstance;
    }

    public void initDataBeans(Context context){
        if(mSp == null)
        {
            mSp = context.getSharedPreferences(FANMENU_DATA_SP_NAME, Context.MODE_APPEND);
        }
    }

    public void saveConfig(FanMenuConfig config){
        SharedPreferences.Editor ed = mSp.edit();
        ed.putInt(FANMENU_CONFIG_FLOW_X, config.getFlowingX());
        ed.putInt(FANMENU_CONFIG_FLOW_Y, config.getFlowingY());
        ed.putInt(FANMENU_CONFIG_POSITION_STATE, config.getPositionState());
        ed.putInt(FANMENU_CONFIG_SELECTTEXT_INDEX, config.getSelectTextIndex());
        ed.commit();

        Log.v(TAG, "saveConfig config" + config);
    }

    public void getConfig(FanMenuConfig config){

        config.setFlowingX(mSp.getInt(FANMENU_CONFIG_FLOW_X, 0));
        config.setFlowingY(mSp.getInt(FANMENU_CONFIG_FLOW_Y, 0));
        config.setPositionState(mSp.getInt(FANMENU_CONFIG_POSITION_STATE, PositionState.POSITION_STATE_LEFT));
        config.setSelectTextIndex(mSp.getInt(FANMENU_CONFIG_SELECTTEXT_INDEX, SelectCardState.CARD_STATE_RECENTLY));

        Log.v(TAG, "getConfig config:" + config);
    }
}
