package com.szu.twowayradio.utils;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class RecordHelper {

    private AudioRecord audioRecord;
    private RecordListener recordListener = null;
    private int audioSource = MediaRecorder.AudioSource.MIC;
    private static int sampleRateInHz = 8000;
	private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private boolean isRecord = false;
    private int bufferSizeInBytes ;
    public static final int BufferElements2Rec = 2048; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int BytesPerElement = 2;
    
    public int getBufferSizeInBytes() {
		return bufferSizeInBytes;
	}

    public RecordHelper() {
    }

    private void init()
    {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,  
               channelConfig, audioFormat);  
        DebugLog.e("RecordHelper init " + bufferSizeInBytes);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz,  
                channelConfig, audioFormat, BufferElements2Rec);
    }

    public void startRecord()
    {
    	if (isRecord)
            return;
        init();
        audioRecord.startRecording();
    	isRecord = true;
    	if (recordListener != null)
    	{
    		recordListener.recording(audioRecord);
    	}
    }

    public void stopRecord()
    {
        if (null != audioRecord)
        {
        	isRecord = false;
        	audioRecord.stop();
        	audioRecord.release();
        	audioRecord = null;
        }
    }

    public boolean isRecord() {
        return isRecord;
    }

    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public interface RecordListener {
        void recording(AudioRecord record);
    }
}
