package com.jeden.fanmenudemo.common.tools;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeAudio extends SwipeTools {
    private volatile static SwipeAudio mInstance;
    private AudioManager mAudioManager;

    private SwipeAudio(Context context)
    {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
    public static SwipeAudio getInstance(Context context)
    {
        if(mInstance == null)
        {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context)
    {
        mInstance = new SwipeAudio(context);
    }

    @Override
    public void viewBinded(Context context) {
        changeViewState();
    }

    @Override
    public void changeState(Context context) {
        switch (getState()) {
            case AudioManager.RINGER_MODE_SILENT:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
        }

        changeViewState();
    }

    private void changeViewState()
    {
        SwipeView view = mSwipeView.get();
        if(view == null)
        {
            return;
        }

        switch (getState()) {
            case AudioManager.RINGER_MODE_SILENT:
                // TODO
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                // TODO
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                // TODO
                break;
        }
    }

    private int getState() {
        return mAudioManager.getRingerMode();
    }
}
