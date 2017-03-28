package com.jeden.fanmenudemo.common.tools;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeBlueTooth extends SwipeTools {
    private volatile static SwipeBlueTooth mInstance;
    private BluetoothAdapter ba;
    private SwipeBlueTooth(Context context)
    {
        ba = BluetoothAdapter.getDefaultAdapter();
    }
    public static SwipeBlueTooth getInstance(Context context)
    {
        if(mInstance == null)
        {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context)
    {
        mInstance = new SwipeBlueTooth(context);
    }

    @Override
    public void viewBinded(Context context) {
        changeViewState();
    }

    @Override
    public void changeState(Context context) {
        if(ba.isEnabled())
        {
            ba.disable();
        }
        else
        {
            ba.enable();
        }

        changeViewState();
    }

    private void changeViewState(){
        if(ba.isEnabled())
        {
            //TODO
        }
        else
        {
            //TODO
        }
    }
}
