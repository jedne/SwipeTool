package com.jeden.fanmenu.common.tools;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;

import com.jeden.fanmenu.R;

/**
 * Created by jeden on 2017/3/23.
 */

public class SwipeAudio extends SwipeTools {
    private volatile static SwipeAudio mInstance;
    private AudioManager mAudioManager;

    private SwipeAudio(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static SwipeAudio getInstance(Context context) {
        if (mInstance == null) {
            newInstance(context);
        }
        return mInstance;
    }

    private static synchronized void newInstance(Context context) {
        mInstance = new SwipeAudio(context);
    }

    @Override
    public void viewBind(Context context) {
        changeViewState(context);
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

        changeViewState(context);
    }

    private void changeViewState(Context context) {
        SwipeView view = mSwipeView.get();
        if (view == null) {
            return;
        }

        Resources rs = context.getResources();

        switch (getState()) {
            case AudioManager.RINGER_MODE_SILENT:
                view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_audio_3));
                view.setTitle(rs.getString(R.string.fan_menu_toolbox_audio_silent));
                showToast(context, rs.getString(R.string.fan_menu_toolbox_audio_silent_toast));
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_audio_2));
                view.setTitle(rs.getString(R.string.fan_menu_toolbox_audio_normal));
                showToast(context, rs.getString(R.string.fan_menu_toolbox_audio_normal_toast));
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                view.setItemIcon(rs.getDrawable(R.drawable.fan_item_icon_audio_1));
                view.setTitle(rs.getString(R.string.fan_menu_toolbox_audio_vibrate));
                showToast(context, rs.getString(R.string.fan_menu_toolbox_audio_vibrate_toast));
                break;
        }
    }

    private int getState() {
        return mAudioManager.getRingerMode();
    }
}
