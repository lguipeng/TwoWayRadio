package com.szu.twowayradio.ui.fragments;

import android.app.DialogFragment;
import android.media.AudioRecord;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.szu.twowayradio.R;
import com.szu.twowayradio.domains.User;
import com.szu.twowayradio.network.NetWorkService;
import com.szu.twowayradio.service.ConnectService;
import com.szu.twowayradio.ui.base.BaseFragment;
import com.szu.twowayradio.utils.PreferenceUtil;
import com.szu.twowayradio.utils.RecordHelper;
import com.szu.twowayradio.utils.ToastUtil;
import com.szu.twowayradio.utils.UsetUtil;
import com.szu.twowayradio.views.SpeakButton;


/**
 * lgp on 2014/10/28.
 */
public class MainFragment extends BaseFragment implements RecordHelper.RecordListener, ConnectService.ConnectListener{

    private SpeakButton speaker;
    private ImageView speakerLight;
    private RecordHelper recordHelper;

    public static MainFragment newInstance()
    {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToServer(getAppContext().getUser());
        recordHelper = new RecordHelper();
        recordHelper.setRecordListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.main_fragment, container, false);
        speaker = (SpeakButton)view.findViewById(R.id.speaker);
        speakerLight = (ImageView)view.findViewById(R.id.speaker_light);
        init();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_user:
                changeUserEvent();
                return true;
            case R.id.change_address:
                changeAddressEvent();
                return true;
            case R.id.exit:
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init()
    {
        speaker.setListener(new SpeakButton.SpeakButtonListener() {
            @Override
            public void onDown(MotionEvent event) {
                speakButtonDown();
            }
            @Override
            public void onUp(MotionEvent event) {
                speakButtonUp();
            }
        });
        speaker.setEnabled(false);
    }

    private void speakButtonDown()
    {
        if (speaker.isPressed())
        {
            speakerLight.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.a3q));
            recordHelper.startRecord();
        }
    }

    private void speakButtonUp()
    {
        speakerLight.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.a3p));
        recordHelper.stopRecord();
    }

    private void changeUserEvent()
    {
        final EditDialogFragment fragment = EditDialogFragment.newInstance(EditDialogFragment.DialogType.CHANGE_USER);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE,R.style.DialogFragment);
        fragment.show(getFragmentManager(),"change_user");
        fragment.setListener(new EditDialogFragment.ButtonListener() {
            @Override
            public void onSure(EditDialogFragment.DialogType type, String arg1, String arg2) {
                getAppContext().getUser().setName(arg1);
                getAppContext().getUser().setPassword(arg2);
                UsetUtil.saveUserToLocal(getAppContext().getUser(), getActivity());
                fragment.dismiss();
            }

            @Override
            public void onCancel() {
                fragment.dismiss();
            }
        });
    }

    private void changeAddressEvent()
    {
        final EditDialogFragment fragment = EditDialogFragment.newInstance(EditDialogFragment.DialogType.CHANGE_ADDRESS);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE,R.style.DialogFragment);
        fragment.show(getFragmentManager(), "change_address");
        fragment.setListener(new EditDialogFragment.ButtonListener() {
            @Override
            public void onSure(EditDialogFragment.DialogType type, String arg1, String arg2) {
                PreferenceUtil.getInstance(getActivity()).saveParam(PreferenceUtil.IP_KEY, arg1);
                PreferenceUtil.getInstance(getActivity()).saveParam(PreferenceUtil.PORT_KEY, arg2);
                fragment.dismiss();
            }

            @Override
            public void onCancel() {
                fragment.dismiss();
            }
        });
    }

    @Override
    public void recording(AudioRecord record) {
        //do some network transfer
        NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void connectSuccess(User user) {
        speaker.setEnabled(true);
    }

    @Override
    public void connectFail() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(getActivity(), "无法链接服务器");
                speaker.setEnabled(false);
            }
        });
    }

    @Override
    public void disconnectSuccess() {

    }

    @Override
    public void disconnectFail() {

    }

    private void connectToServer(final User user)
    {
        ConnectService.getInstance().setConnectListener(this);

        if (ConnectService.getInstance().isConnect())
        {
            NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                @Override
                public void run() {
                    ConnectService.getInstance().disconnect();
                    ConnectService.getInstance().connect(user);
                }
            });
        }else{
            NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                @Override
                public void run() {
                    ConnectService.getInstance().connect(user);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
            @Override
            public void run() {
                ConnectService.getInstance().disconnect();
            }
        });
    }
}
