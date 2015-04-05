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
import com.szu.twowayradio.network.UdpHelper;
import com.szu.twowayradio.service.AudioService;
import com.szu.twowayradio.service.ConnectService;
import com.szu.twowayradio.ui.base.BaseFragment;
import com.szu.twowayradio.utils.Adpcm;
import com.szu.twowayradio.utils.AudioTrackHelper;
import com.szu.twowayradio.utils.DebugLog;
import com.szu.twowayradio.utils.PreferenceUtil;
import com.szu.twowayradio.utils.RecordHelper;
import com.szu.twowayradio.utils.ToastUtil;
import com.szu.twowayradio.utils.UsetUtil;
import com.szu.twowayradio.views.SpeakButton;


/**
 * lgp on 2014/10/28.
 */
public class MainFragment extends BaseFragment implements RecordHelper.RecordListener, ConnectService.ConnectListener,
        AudioTrackHelper.PlayListener{

    private SpeakButton speaker;
    private ImageView speakerLight;
    private RecordHelper recordHelper;
    private AudioTrackHelper trackHelper;
    private AudioTrack track;
    private AdpcmState stateCoder = new AdpcmState();
    private AdpcmState stateDeCoder = new AdpcmState();
    private final int beatBreakTime = 21000;
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
        trackHelper = new AudioTrackHelper();
        trackHelper.setPlayListener(this);


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
            case R.id.change_address:
                changeAddressEvent();
                return true;
            case R.id.change_user:
                changeUserEvent();
                return true;
            case R.id.exit:
                NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(getActivity(), "即将退出");
                            }
                        });
                        ConnectService.getInstance().disconnect();
                        //NetWorkService.getDefaultInstance().shutDown();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

            case android.R.id.home:
                if (getActivity() != null)
                {
                    getActivity().finish();
                }
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
            trackHelper.startPlay();
        }
    }

    private void speakButtonUp()
    {
        speakerLight.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.a3p));
        recordHelper.stopRecord();
        trackHelper.startPlay();
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
                NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (ConnectService.getInstance().isConnect()){
                            DebugLog.e("disconnect");
                            ConnectService.getInstance().disconnect();
                        }

                        connectToServer(getAppContext().getUser());
                    }
                });

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
            public void onSure(EditDialogFragment.DialogType type, final String arg1, final String arg2) {
                PreferenceUtil.getInstance(getActivity()).saveParam(PreferenceUtil.IP_KEY, arg1);
                PreferenceUtil.getInstance(getActivity()).saveParam(PreferenceUtil.PORT_KEY, arg2);
                NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        UdpHelper.getInstance().setServerIp(arg1);
                        UdpHelper.getInstance().setServerPort(Integer.parseInt(arg2));
                        if (ConnectService.getInstance().isConnect())
                            ConnectService.getInstance().disconnect();
                        UdpHelper.getInstance().initNetWork();
                        connectToServer(getAppContext().getUser());
                    }
                });
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
        ToastUtil.show(getActivity(), "recording");
        //do some network transfer
        NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
            @Override
            public void run() {
                while(recordHelper.isRecord())
                {
                    final short[] recordBuf = new short[RecordHelper.BufferElements2Rec / 2];
                    final int readSize = record.read(recordBuf, 0, recordBuf.length);
                    //calc1(recordBuf, 0, recordBuf.length);
                    NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            final byte[] out  = new byte[recordBuf.length / 2];
                            if(readSize != AudioRecord.ERROR_INVALID_OPERATION && readSize != AudioRecord.ERROR_BAD_VALUE){
                                //send byte[] out
                                //AudioService.getInstance().sendAudio(out, stateCoder);
                                //ByteConvert.htos(recordBuf, recordBuf.length);
                                //short[] left = new short[recordBuf.length / 2];
                                //ByteConvert.getOshort(recordBuf, left, recordBuf.length);
                                //Adpcm.code(left, out, left.length, stateCoder);
                                //Adpcm.decode(out, left, out.length, stateCoder);
                                AdpcmState state = new AdpcmState();
                                state.setIndex(stateCoder.getIndex());
                                state.setValprev(stateCoder.getValprev());
                                Adpcm.adpcmCoder(recordBuf, out, recordBuf.length, stateCoder);
                                AudioService.getInstance().sendAudio(out, state);
                                //Adpcm.adpcmDecoder(out, recordBuf, out.length, state);
                                //calc1(left, 0, left.length);
                                //ByteConvert.putOshort(recordBuf, left, recordBuf.length);
                                //calc1(recordBuf, 0, recordBuf.length);
                                //int  size = speex.encode(recordBuf, 0, out, recordBuf.length);
                                //int srcsize = speex.decode(out, recordBuf, size);
                                //track.write(recordBuf, 0, recordBuf.length);
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
                    //DebugLog.d("receiveAudio ing --->");
                    byte [] receive = AudioService.getInstance().receiveAudio(stateDeCoder);
                    if (receive == null)
                        continue;
                    short [] audioBufShort = new short[ 2 * receive.length];
                    Adpcm.adpcmDecoder(receive, audioBufShort, receive.length * 2, stateDeCoder);
                    //DebugLog.d("receive audio length-->" + audioBufShort.length);
                    track.write(audioBufShort, 0, audioBufShort.length);
                }
            }
        });
        NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (ConnectService.getInstance().isConnect()){
                        Thread.sleep(beatBreakTime);
                        ConnectService.getInstance().sendBeat();
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(getActivity(), "成功链接服务器");
                if (speaker == null)
                    return;
                speaker.setEnabled(true);
            }
        });
    }

    @Override
    public void connectFail() {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(getActivity(), "非法用户或者是网络不好");
                if (speaker == null)
                    return;
                speaker.setEnabled(false);
            }
        });
    }

    @Override
    public void disconnectSuccess() {
        DebugLog.d("disconnectSuccess");
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (speaker != null)
                {
                    speaker.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void disconnectFail() {
        DebugLog.d("disconnectFail");
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (speaker != null)
                {
                    speaker.setEnabled(false);
                }
            }
        });
    }

    private void connectToServer(final User user)
    {
        ConnectService.getInstance().setConnectListener(this);
        NetWorkService.getDefaultInstance().getPool().execute(new Runnable() {
            @Override
            public void run() {
                DebugLog.e("connect--->");
                ConnectService.getInstance().connect(user);
            }
        });

    }

    @Override
    public void playing(AudioTrack track) {
        this.track = track;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
