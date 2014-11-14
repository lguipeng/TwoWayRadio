package com.szu.twowayradio.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.szu.twowayradio.App;
import com.szu.twowayradio.R;
import com.szu.twowayradio.fragment.BaseFragment;


public abstract  class BaseActivity extends FragmentActivity{

    protected FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
        addFragment(getFragment());
	}

    protected App getAppContext()
    {
        return (App)getApplication();
    }

    protected void addFragment(Fragment fragment)
    {
        fragmentManager = getFragmentManager();
        if(fragment == null)
            return;
        fragmentManager.beginTransaction().add(R.id.main,fragment,"main").commit();
    }
    protected abstract BaseFragment getFragment();
}
