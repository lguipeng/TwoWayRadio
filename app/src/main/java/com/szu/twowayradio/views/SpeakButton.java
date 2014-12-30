package com.szu.twowayradio.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by lgp on 2014/10/29.
 */
public class SpeakButton extends Button{
    private SpeakButtonListener mListener = null;
    public SpeakButton(Context context) {
        super(context);
    }

    public SpeakButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN: onDown(event);break;
            case MotionEvent.ACTION_UP: onUp(event);break;
            default:break;
        }
        return super.onTouchEvent(event);
    }

    private void onDown(MotionEvent event)
    {
        if(mListener != null)
        {
            mListener.onDown(event);
        }
    }

    private void onUp(MotionEvent event)
    {
        if(mListener != null)
        {
            mListener.onUp(event);
        }
    }

    public void setListener(SpeakButtonListener listener) {
        this.mListener = listener;
    }

    public interface SpeakButtonListener{
        void onDown(MotionEvent event);
        void onUp(MotionEvent event);
    }
}
