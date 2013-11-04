package com.xstd.pirvatephone.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.xstd.pirvatephone.R;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-9-9
 * Time: PM3:08
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends Activity {

    protected static int count = -1;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ddz);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (count == -1) {
            mediaPlayer.start();
        }
        count++;
        Log.w("TAG", "多少个Activity" + count);
    }

    @Override
    protected void onStop() {
        super.onStop();
        count--;
        if (count == -1) {
            mediaPlayer.pause();
        }
        Log.w("TAG", "多少个Activity" + count);
    }

}


