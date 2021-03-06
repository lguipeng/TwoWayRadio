package com.szu.twowayradio.utils;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioTrackHelper {

    private AudioTrack track;

    private PlayListener playListener = null;

    private int bufSize = 0;

    private boolean isPlay;

    public AudioTrackHelper() {
        init();
    }

    private void init()
    {
    	bufSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                RecordHelper.BufferElements2Rec, AudioTrack.MODE_STREAM);
    }

    public void startPlay()   
    {
    	if (track != null && !isPlay)
    	{
    		isPlay = true;
    		track.play();
    		if (playListener != null)
    		  playListener.playing(track);
    	}
    }
    public void stopPlay()
    {
    	if (track != null)
    	{
    		if (isPlay)
    		{
    			track.stop();
    			track.release();
    			track = null;
    			isPlay = false;
    		}
    	}
    }

    public void setPlayListener(PlayListener playListener) {
        this.playListener = playListener;
    }

    public interface PlayListener {
        void playing(AudioTrack track);
    }
}
