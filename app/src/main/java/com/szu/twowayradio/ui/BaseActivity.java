package com.szu.twowayradio.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.szu.twowayradio.App;


public class BaseActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle bundle) {
		
		super.onCreate(bundle);
	}

    protected App getAppContext()
    {
        return (App)getApplication();
    }
}
