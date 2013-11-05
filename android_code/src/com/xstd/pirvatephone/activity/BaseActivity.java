package com.xstd.pirvatephone.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.BackgroundSoundManager;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-9-9
 * Time: PM3:08
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends Activity {

    protected static int count = -1;

    @Override
    protected void onStart() {
        super.onStart();
        if (count == -1) {
            BackgroundSoundManager.getInstance(getApplicationContext()).playBackgroundSound();
        }
        count++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        count--;
        if (count == -1) {
            BackgroundSoundManager.getInstance(getApplicationContext()).pauseBackgroundSound();
        }
    }

}


