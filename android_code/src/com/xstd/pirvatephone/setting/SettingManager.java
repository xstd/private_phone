package com.xstd.pirvatephone.setting;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-9-12
 * Time: AM11:31
 * To change this template use File | Settings | File Templates.
 */
public class SettingManager {

    private static final String SHARE_PREFERENCE_NAME = "setting_manager_share_pref";

    private static SettingManager gSettingManager;

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mEditor;

    public static SettingManager getInstance() {
        if (gSettingManager == null) {
            gSettingManager = new SettingManager();
        }

        return gSettingManager;
    }

    private SettingManager() {
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences(SHARE_PREFERENCE_NAME, 0);
        mEditor = mSharedPreferences.edit();
    }

    private static final String KEY_ENTER_PASSWORD = "enter_password";
    public String getKeyEnterPassword() {
        return mSharedPreferences.getString(KEY_ENTER_PASSWORD, null);
    }

    public void setKeyEnterPassword(String password) {
        mEditor.putString(KEY_ENTER_PASSWORD, password).commit();
    }
}
