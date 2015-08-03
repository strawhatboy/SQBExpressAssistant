package com.sqbnet.expressassistant.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sqbnet.expressassistant.MyApplication;

/**
 * Created by virgil on 15-8-3.
 */
public class SharedPreferenceHelper {
    private static SharedPreferenceHelper inst;
    private static String CRASH_SETTINGS = "crash_settings";

    private SharedPreferences mSharedPreferences;

    public static SharedPreferenceHelper getInst(){
        if(inst == null){
            synchronized (SharedPreferenceHelper.class){
                if(inst == null){
                    inst = new SharedPreferenceHelper();
                }
            }
        }
        return inst;
    }

    private SharedPreferenceHelper(){
        mSharedPreferences = MyApplication.getInst().getSharedPreferences(CRASH_SETTINGS, Context.MODE_PRIVATE);
    }

    public void setKey(String key, String value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setKey(String key, boolean value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getStringkey(String key, String defaultValue){
        return mSharedPreferences.getString(key, defaultValue);
    }

    public boolean getBooleanKey(String key, boolean defaultValue){
        return mSharedPreferences.getBoolean(key, defaultValue);
    }
}
