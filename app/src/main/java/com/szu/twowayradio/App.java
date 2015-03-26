package com.szu.twowayradio;

import android.app.Application;

import com.szu.twowayradio.domains.User;
import com.szu.twowayradio.network.NetWorkConfig;
import com.szu.twowayradio.utils.PreferenceUtil;
import com.szu.twowayradio.utils.UsetUtil;

public class App extends Application{

    private User user;
	@Override
	public void onCreate() {
		super.onCreate();
        init();
	}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void init()
    {
        if (PreferenceUtil.getInstance(getApplicationContext()).getBooleanParam(PreferenceUtil.FIRST_USE_KEY))
        {
            PreferenceUtil.getInstance(getApplicationContext()).saveParam(PreferenceUtil.FIRST_USE_KEY, false);
            PreferenceUtil.getInstance(getApplicationContext()).saveParam(PreferenceUtil.USERNAME_KEY, "admin");
            PreferenceUtil.getInstance(getApplicationContext()).saveParam(PreferenceUtil.PASSWORD_KEY, "admin");
            PreferenceUtil.getInstance(getApplicationContext()).saveParam(PreferenceUtil.IP_KEY, NetWorkConfig.DEFAULT_SERVER_IP);
            PreferenceUtil.getInstance(getApplicationContext()).saveParam(PreferenceUtil.PORT_KEY, NetWorkConfig.DEFAULT_PORT + "");
        }
        NetWorkConfig.DEFAULT_SERVER_IP = PreferenceUtil.getInstance(getApplicationContext()).getStringParam(PreferenceUtil.IP_KEY);
        NetWorkConfig.DEFAULT_PORT = Integer.parseInt(PreferenceUtil.getInstance(getApplicationContext()).getStringParam(PreferenceUtil.PORT_KEY));
        user = UsetUtil.loadUserFromLocal(getApplicationContext());
    }
}
