package com.jeden.fanmenu.common.model;

import com.jeden.fanmenu.view.base.PositionState;
import com.jeden.fanmenu.view.base.CardState;

/**
 * Created by jeden on 2017/3/15.
 */

public class FanMenuConfig {

    private volatile static FanMenuConfig mInstance;

    private int flowingX = 0;
    private int flowingY = 0;
    private int positionState = PositionState.POSITION_STATE_LEFT;
    private int cardIndex = CardState.CARD_STATE_RECENTLY;

    private FanMenuConfig() {
    }

    public static FanMenuConfig getMenuConfig() {
        if (mInstance == null) {
            newInstance();
        }
        return mInstance;
    }

    private static synchronized void newInstance() {
        mInstance = new FanMenuConfig();
        DataBeans.getInstance().getConfig(mInstance);
    }

    public static void saveMenuConfig() {
        if (mInstance == null) {
            return;
        }
        DataBeans.getInstance().saveConfig(mInstance);
    }

    public int getFlowingX() {
        return flowingX;
    }

    public void setFlowingX(int flowingX) {
        this.flowingX = flowingX;
    }

    public int getFlowingY() {
        return flowingY;
    }

    public void setFlowingY(int flowingY) {
        this.flowingY = flowingY;
    }

    public int getPositionState() {
        return positionState;
    }

    public void setPositionState(int positionState) {
        this.positionState = positionState;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }
}
