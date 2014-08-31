package com.szu.twowayradio.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;


public class BaseActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle bundle) {
		
		super.onCreate(bundle);
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		return super.onKeyDown(keyCode, event);
	}

	
}
