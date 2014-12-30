package com.szu.twowayradio.ui.base;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;

import com.szu.twowayradio.App;

public class BaseFragment extends Fragment {

    protected final String TAG = getClass().getSimpleName();

    protected ActionBar actionBar;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActivity().getActionBar();
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected App getAppContext()
    {
        return (App)getActivity().getApplication();
    }
    protected void setTitle(CharSequence title)
    {
        actionBar.setTitle(title);
    }

    protected void setTitle(int title)
    {
        actionBar.setTitle(title);
    }
}
