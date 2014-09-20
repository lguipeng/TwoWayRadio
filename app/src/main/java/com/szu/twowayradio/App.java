package com.szu.twowayradio;

import android.app.Application;

import com.szu.twowayradio.domain.User;

public class App extends Application{

    private User user;
	@Override
	public void onCreate() {
		super.onCreate();
	}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
