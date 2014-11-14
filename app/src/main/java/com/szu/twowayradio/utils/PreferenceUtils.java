package com.szu.twowayradio.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lgp on 2014/10/30.
 */
public class PreferenceUtils {

    public final static String USERNAME_KEY = "USERNAME_KEY";

    public final static String PASSWORD_KEY = "PASSWORD_KEY";

    public final static String IP_KEY = "IP_KEY";

    public final static String PORT_KEY = "PORT_KEY";

    public final static String FIRST_USE_KEY = "FIRST_USE_KEY";

    private final String fileName = "settings";

    private final String DefaultValue = "null";

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor shareEditor;

    private static PreferenceUtils preferenceUtils = null;

    private PreferenceUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        shareEditor = sharedPreferences.edit();
    }

    public static PreferenceUtils getInstance(Context context)
    {
        if (preferenceUtils == null)
        {
            preferenceUtils = new PreferenceUtils(context);
        }
        return preferenceUtils;
    }

    public String getStringParam(String key)
    {
        return sharedPreferences.getString(key,"null");
    }

    public void putParam(String key,String value)
    {
        shareEditor.putString(key,value).commit();
    }

    public boolean getBooleanParam(String key)
    {
        return sharedPreferences.getBoolean(key,true);
    }

    public void putParam(String key,boolean value)
    {
        shareEditor.putBoolean(key,value).commit();
    }
}
