package com.jeden.fanmenudemo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.jeden.fanmenudemo.tools.ContentProvider;
import com.jeden.fanmenudemo.tools.DataBeans;
import com.jeden.fanmenudemo.tools.FanMenuConfig;
import com.jeden.fanmenudemo.tools.MyCustomMenuManager;

public class MyService extends Service {
    private static final String TAG = MyService.class.getSimpleName();

    private static final String ACTION_INIT = "FAN_MENU_SERVICE_INIT";
    private static final String ACTION_SHOW_FLOWING = "FAN_MENU_SERVICE_SHOW_FLOWING";
    private static final String ACTION_HIDE_FLOWING = "FAN_MENU_SERVICE_HIDE_FLOWING";

    private Looper mFanMenuLooper;
    private volatile FanMenuHandler mFanMenuHandler;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    private Object mWaitLock = new Object();

    private boolean mInited = false;

    private final class FanMenuHandler extends android.os.Handler{
        public FanMenuHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onHandleIntent((Intent)msg.obj);
        }
    }

    private void onHandleIntent(Intent intent)
    {
        if(intent == null)
        {
            return;
        }

        final String action = intent.getAction();
        if(ACTION_INIT.equals(action))
        {
            handleInit();
        }
        else if(ACTION_SHOW_FLOWING.equals(action))
        {
            handleShowFlowing();
        }
        else if(ACTION_HIDE_FLOWING.equals(action))
        {
            handleHideFlowing();
        }
    }

    private void handleInit()
    {
        long start = System.currentTimeMillis();
        DataBeans.getInstance().initDataBeans(this);
        FanMenuConfig.getMenuConfig();
        ContentProvider.initContentProvider(this);

        Log.v(TAG, "handle Init cost:" + (System.currentTimeMillis() - start));

        mInited = true;
        try {
            synchronized (mWaitLock) {
                mWaitLock.notifyAll();
            }
        } catch (Exception e) {
            Log.i(TAG, "handleInit notifyAll:" + e.getMessage());
        }
    }

    private void handleShowFlowing()
    {
        if(!mInited)
        {
            try {
                synchronized (mWaitLock) {
                    mWaitLock.wait();
                }
            } catch (Exception e) {
                Log.i(TAG, "handleShowFlowing waitForInit:" + e.getMessage());
            }
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                showFlowingView();
            }
        });
    }

    private void handleHideFlowing()
    {
        if(!mInited)
        {
            try {
                synchronized (mWaitLock) {
                    mWaitLock.wait();
                }
            } catch (Exception e) {
                Log.i(TAG, "handleHideFlowing waitForInit:" + e.getMessage());
            }
        }

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                removeFlowingView();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("FanMenuService", Thread.MIN_PRIORITY);
        thread.start();
        mFanMenuLooper = thread.getLooper();
        mFanMenuHandler = new FanMenuHandler(mFanMenuLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mFanMenuHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mFanMenuHandler.sendMessage(msg);
        return START_NOT_STICKY;
    }

    public void showFlowingView(){
        MyCustomMenuManager.showFlowingView(getApplicationContext());
    }

    public void removeFlowingView(){
        MyCustomMenuManager.removeFlowingView(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        removeFlowingView();
        FanMenuConfig.saveMenuConfig();
        mFanMenuLooper.quit();
        Log.v(TAG, "service onDestroy");
        super.onDestroy();
    }

    public static void initFanMenuSDK(Context context)
    {
        Intent intent = new Intent(context, MyService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    public static void showFlowing(Context context)
    {
        Intent intent = new Intent(context, MyService.class);
        intent.setAction(ACTION_SHOW_FLOWING);
        context.startService(intent);
    }

    public static void hideFlowing(Context context)
    {
        Intent intent = new Intent(context, MyService.class);
        intent.setAction(ACTION_HIDE_FLOWING);
        context.startService(intent);
    }
}
