package com.szu.twowayradio.fragment;

import android.app.DialogFragment;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.szu.twowayradio.R;
import com.szu.twowayradio.domain.User;
import com.szu.twowayradio.network.NetWorkService;
import com.szu.twowayradio.service.ConnectService;
import com.szu.twowayradio.utils.PreferenceUtils;
import com.szu.twowayradio.utils.RecordHelper;
import com.szu.twowayradio.utils.ToastUtils;
import com.szu.twowayradio.view.SpeakButton;


/**
 * Created by lgp on 2014/10/28.
 */
public class MainFragment extends BaseFragment implements RecordHelper.RecordListener{

    private SpeakButton speaker;
    private ImageView speakerLight;
    private RecordHelper recordHelper;
    private final int ERROR = 0x01;

    public static MainFragment newInstance()
    {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == ERROR)
            {
                ToastUtils.show(getActivity(),"无法连接服务器");
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToServer(getAppContext().getUser());
        recordHelper = new RecordHelper();
        recordHelper.setRecordListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.main_fragment,container,false);
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
    }

    private void speakButtonDown()
    {
        speakerLight.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.a3q));
        recordHelper.startRecord();
    }

    private void speakButtonUp()
    {
        speakerLight.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.a3p));
    }

    private void changeUserEvent()
    {
        final EditDialogFragment fragment = EditDialogFragment.newInstance(EditDialogFragment.DialogType.CHANGE_USER);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE,R.style.DialogFragment);
        fragment.show(getFragmentManager(),"change_user");
        fragment.setListener(new EditDialogFragment.ButtonListener() {
            @Override
            public void onSure(EditDialogFragment.DialogType type, String arg1, String arg2) {
                PreferenceUtils.getInstance(getActivity()).putParam(PreferenceUtils.USERNAME_KEY,arg1);
                PreferenceUtils.getInstance(getActivity()).putParam(PreferenceUtils.PASSWORD_KEY,arg2);
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
        fragment.show(getFragmentManager(),"change_address");
        fragment.setListener(new EditDialogFragment.ButtonListener() {
            @Override
            public void onSure(EditDialogFragment.DialogType type, String arg1, String arg2) {
                PreferenceUtils.getInstance(getActivity()).putParam(PreferenceUtils.IP_KEY,arg1);
                PreferenceUtils.getInstance(getActivity()).putParam(PreferenceUtils.PORT_KEY,arg2);
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

    private void connectToServer(final User user)
    {
        ConnectService.getInstance().setConnectListener(new ConnectService.ConnectListener() {
            @Override
            public void connectSuccess(User user) {

            }

            @Override
            public void connectFail() {
                Message message = handler.obtainMessage();
                message.what = ERROR;
                handler.sendMessage(message);
            }

            @Override
            public void disconnectSuccess() {

            }

            @Override
            public void disconnectFail() {

            }
        });
        NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
            @Override
            public void run() {
                ConnectService.getInstance().connect(user);
            }
        });

    }
}
