package com.szu.twowayradio.utils;

import android.content.Context;

import com.szu.twowayradio.domains.User;

/**
 *  lgp on 2015/3/3.
 */
public class UsetUtil {

    public static User loadUserFromLocal(Context context){
        return  new User(PreferenceUtil.getInstance(context).getStringParam(PreferenceUtil.USERNAME_KEY),
                PreferenceUtil.getInstance(context).getStringParam(PreferenceUtil.PASSWORD_KEY));
    }

    public static void saveUserToLocal(User user, Context context)
    {
        String name = user.getName();
        String password = user.getPassword();
        PreferenceUtil.getInstance(context).saveParam(PreferenceUtil.USERNAME_KEY, name);
        PreferenceUtil.getInstance(context).saveParam(PreferenceUtil.PASSWORD_KEY, password);
    }
}
