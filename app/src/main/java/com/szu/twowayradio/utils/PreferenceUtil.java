package com.szu.twowayradio.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lgp on 2014/10/30.
 */
public class PreferenceUtil {

    public final static String USERNAME_KEY = "USERNAME_KEY";

    public final static String PASSWORD_KEY = "PASSWORD_KEY";

    public final static String IP_KEY = "IP_KEY";

    public final static String PORT_KEY = "PORT_KEY";

    public final static String FIRST_USE_KEY = "FIRST_USE_KEY";

    private final String fileName = "settings";

    private final String DefaultValue = "null";

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor shareEditor;

    private static PreferenceUtil preferenceUtils = null;

    private PreferenceUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        shareEditor = sharedPreferences.edit();
    }

    public static PreferenceUtil getInstance(Context context)
    {
        if (preferenceUtils == null)
        {
            preferenceUtils = new PreferenceUtil(context);
        }
        return preferenceUtils;
    }

    public String getStringParam(String key)
    {
        return sharedPreferences.getString(key,"null");
    }

    public void saveParam(String key, String value)
    {
        shareEditor.putString(key,value).commit();
    }

    public boolean getBooleanParam(String key)
    {
        return sharedPreferences.getBoolean(key,true);
    }

    public void saveParam(String key, boolean value)
    {
        shareEditor.putBoolean(key,value).commit();
    }
}
