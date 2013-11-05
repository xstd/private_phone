package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.xstd.pirvatephone.R;

/**
 * Created by chrain on 13-11-5.
 */
public class BackgroundSoundManager {

    private static BackgroundSoundManager instance;

    private static Object mObject = new Object();
    private static Context mContext;
    private SharedPreferences sp;
    private MediaPlayer mMediaPlayer;

    private BackgroundSoundManager() {
        sp = mContext.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    public static BackgroundSoundManager getInstance(Context context) {
        if (instance == null) {
            synchronized (mObject) {
                if (instance == null) {
                    mContext = context;
                    instance = new BackgroundSoundManager();
                }
            }
        }
        return instance;
    }

    public void playBackgroundSound() {
        int resID = sp.getInt("background_sound_resid", R.raw.ddz);
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(mContext,resID);
            mMediaPlayer.setLooping(true);
        }
        mMediaPlayer.start();
    }

    public void pauseBackgroundSound() {
        if(mMediaPlayer !=null && mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

    public void stopBackGroundSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
