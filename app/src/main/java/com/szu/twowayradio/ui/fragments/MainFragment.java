package com.szu.twowayradio.ui.fragments;

import android.app.DialogFragment;
import android.media.AudioRecord;
import android.media.AudioTrack;
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
import com.szu.twowayradio.domains.AdpcmState;
import com.szu.twowayradio.domains.User;
import com.szu.twowayradio.network.NetWorkService;
import com.szu.twowayradio.service.AudioService;
import com.szu.twowayradio.service.ConnectService;
import com.szu.twowayradio.ui.base.BaseFragment;
import com.szu.twowayradio.utils.Adpcm;
import com.szu.twowayradio.utils.AudioTrackHelper;
import com.szu.twowayradio.utils.ByteConvert;
import com.szu.twowayradio.utils.DebugLog;
import com.szu.twowayradio.utils.PreferenceUtil;
import com.szu.twowayradio.utils.RecordHelper;
import com.szu.twowayradio.utils.ToastUtil;
import com.szu.twowayradio.utils.UsetUtil;
import com.szu.twowayradio.views.SpeakButton;

import java.util.Timer;
import java.util.TimerTask;


/**
 * lgp on 2014/10/28.
 */
public class MainFragment extends BaseFragment implements RecordHelper.RecordListener, ConnectService.ConnectListener,
        AudioTrackHelper.PlayListener{

    private Timer timer = new Timer();
    private SpeakButton speaker;
    private ImageView speakerLight;
    private RecordHelper recordHelper;
    private AudioTrackHelper trackHelper;
    private AudioTrack track;
    private AdpcmState stateCoder = new AdpcmState();
    private AdpcmState stateDeCoder = new AdpcmState();
    private short[] recordBuf;
    private final int beatBreakTime = 21;
    public static MainFragment newInstance()
    {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToServer();
        recordHelper = new RecordHelper();
        recordHelper.setRecordListener(this);
        trackHelper = new AudioTrackHelper();
        trackHelper.setPlayListener(this);

        recordBuf = new short[AudioService.AUDIO_DATA_LENGTH - AudioService.AUDIO_DATA_HEAD_LENGTH];
        stateCoder.setIndex((byte) 0);
        stateCoder.setValprev((short) 0);
        stateDeCoder.setIndex((byte) 0);
        stateDeCoder.setValprev((short) 0);
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
        speaker.setEnabled(ConnectService.getInstance().isConnect());
    }

    private void speakButtonDown()
    {
        if (speaker.isEnabled())
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
    public void recording(final AudioRecord record) {
        //DebugLog.e("recording");
        ToastUtil.show(getActivity(), "recording");
        //do some network transfer
        NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
            @Override
            public void run() {
                while(recordHelper.isRecord())
                {
                    final int readSize = record.read(recordBuf, 0, recordBuf.length);

                    final byte[] out = new byte[recordBuf.length];
                    Adpcm.adpcmCoder(recordBuf, out, recordBuf.length, stateCoder);
                    NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            if(readSize != AudioRecord.ERROR_INVALID_OPERATION && readSize != AudioRecord.ERROR_BAD_VALUE)
                            {
                                //send byte[] out
                                AudioService.getInstance().sendAudio(out, stateCoder);
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public void connectSuccess(User user) {

        NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
            @Override
            public void run() {
                trackHelper.startPlay();
                while (ConnectService.getInstance().isConnect())
                {
                    byte [] receive = AudioService.getInstance().receiveAudio(stateDeCoder);
                    if (receive == null)
                        continue;
                    short [] recordShort = new short[receive.length];
                    byte [] audioBuf = new byte[2 * receive.length];

                    Adpcm.adpcmDecoder(receive, recordShort, receive.length, stateDeCoder);
                    for(int i=0; i<recordShort.length; i++)
                    {
                        ByteConvert.shortToBytes(audioBuf, recordShort[i], 2 * i);
                    }
                    DebugLog.e("receive audio length-->" + receive.length);
                    track.write(audioBuf, 0, audioBuf.length);
                }
            }
        });
        if (speaker == null)
            return;
        speaker.setEnabled(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        DebugLog.e("sendBeat-->");
                        ConnectService.getInstance().sendBeat();
                    }
                });
            }
        }, beatBreakTime , beatBreakTime);
    }

    @Override
    public void connectFail() {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(getActivity(), "无法链接服务器");
                if (speaker == null)
                    return;
                speaker.setEnabled(false);
            }
        });
    }

    @Override
    public void disconnectSuccess() {
        DebugLog.e("disconnectSuccess");
    }

    @Override
    public void disconnectFail() {
        DebugLog.e("disconnectFail");
    }

    private void connectToServer()
    {
        ConnectService.getInstance().setConnectListener(this);

        if (ConnectService.getInstance().isConnect())
        {
            NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                @Override
                public void run() {
                    DebugLog.d("re connect");
                    ConnectService.getInstance().disconnect();
                    ConnectService.getInstance().connect();
                }
            });
        }else{
            NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                @Override
                public void run() {
                    ConnectService.getInstance().connect();
                }
            });
        }
    }

    @Override
    public void playing(AudioTrack track) {
        this.track = track;
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
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }
}
