package com.xstd.pirvatephone.app;

import android.app.Application;

import com.xstd.pirvatephone.setting.SettingManager;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-9-12
 * Time: AM11:37
 * To change this template use File | Settings | File Templates.
 */
public class PrivatePhoneApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SettingManager.getInstance().init(this);
    }

}
