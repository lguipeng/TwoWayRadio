package com.szu.twowayradio;

import android.app.Application;

import com.szu.twowayradio.domains.User;
import com.szu.twowayradio.network.NetWorkConfig;
import com.szu.twowayradio.utils.PreferenceUtils;

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
        if (PreferenceUtils.getInstance(getApplicationContext()).getBooleanParam(PreferenceUtils.FIRST_USE_KEY))
        {
            PreferenceUtils.getInstance(getApplicationContext()).putParam(PreferenceUtils.FIRST_USE_KEY,false);
            PreferenceUtils.getInstance(getApplicationContext()).putParam(PreferenceUtils.USERNAME_KEY,"admin");
            PreferenceUtils.getInstance(getApplicationContext()).putParam(PreferenceUtils.PASSWORD_KEY,"admin");
            PreferenceUtils.getInstance(getApplicationContext()).putParam(PreferenceUtils.IP_KEY, NetWorkConfig.DEFAULT_SERVER_IP);
            PreferenceUtils.getInstance(getApplicationContext()).putParam(PreferenceUtils.PORT_KEY, NetWorkConfig.DEFAULT_PORT+"");
        }
        user = new User(PreferenceUtils.getInstance(getApplicationContext()).getStringParam(PreferenceUtils.USERNAME_KEY),
                PreferenceUtils.getInstance(getApplicationContext()).getStringParam(PreferenceUtils.PASSWORD_KEY));

    }
}
