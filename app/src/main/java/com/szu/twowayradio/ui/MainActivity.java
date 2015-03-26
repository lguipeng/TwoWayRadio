package com.szu.twowayradio.ui;

import android.os.Bundle;
import android.view.KeyEvent;

import com.szu.twowayradio.R;
import com.szu.twowayradio.ui.base.BaseFragment;
import com.szu.twowayradio.ui.fragments.MainFragment;
import com.szu.twowayradio.ui.base.BaseActivity;

/**
 *  lgp on 2014/10/28.
 */
public class MainActivity  extends BaseActivity {
    MainFragment mainFragment = null;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_activity);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected BaseFragment getFragment() {
        mainFragment = MainFragment.newInstance();
        return mainFragment;
    }
}
