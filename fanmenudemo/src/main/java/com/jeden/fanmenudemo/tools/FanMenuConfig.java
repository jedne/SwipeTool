package com.jeden.fanmenudemo.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.jeden.fanmenudemo.view.base.PositionState;
import com.jeden.fanmenudemo.view.base.SelectCardState;

/**
 * Created by jeden on 2017/3/15.
 */

public class FanMenuConfig implements Parcelable {

    private static FanMenuConfig mInstance;

    private int flowingX = 0;
    private int flowingY = 0;
    private int positionState = PositionState.POSITION_STATE_LEFT;
    private int selectTextIndex = SelectCardState.CARD_STATE_RECENTLY;

    private FanMenuConfig(){}

    public static FanMenuConfig getMenuConfig()
    {
        if(mInstance == null) {
            mInstance = new FanMenuConfig();
            DataBeans.getInstance().getConfig(mInstance);
        }
        return mInstance;
    }

    public static void saveMenuConfig()
    {
        if(mInstance == null)
        {
            return;
        }
        DataBeans.getInstance().saveConfig(mInstance);
    }

    public static final Creator<FanMenuConfig> CREATOR = new Creator<FanMenuConfig>() {
        @Override
        public FanMenuConfig createFromParcel(Parcel source) {
            FanMenuConfig fmc = new FanMenuConfig();
            fmc.flowingX = source.readInt();
            fmc.flowingY = source.readInt();
            fmc.positionState = source.readInt();
            fmc.selectTextIndex = source.readInt();
            return fmc;
        }

        @Override
        public FanMenuConfig[] newArray(int size) {
            return new FanMenuConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(flowingX);
        dest.writeInt(flowingY);
        dest.writeInt(positionState);
        dest.writeInt(selectTextIndex);
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

    public int getSelectTextIndex() {
        return selectTextIndex;
    }

    public void setSelectTextIndex(int selectTextIndex) {
        this.selectTextIndex = selectTextIndex;
    }

}
