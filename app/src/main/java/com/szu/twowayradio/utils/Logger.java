package com.szu.twowayradio.utils;

import android.util.Log;

/**
 * Created by lgp on 2014/7/26.
 */
public class Logger {

    private static Logger mLogger = new Logger();

    private Logger() {
    }

    public static Logger getInstance()
    {
        return mLogger;
    }
    public void debug (String tag, String message)
    {
        Log.d(tag, message);
    }

    public void info (String tag, String message)
    {
        Log.i(tag, message);
    }

    public void error (String tag, String message)
    {
        Log.e(tag, message);
    }

    public void verbose (String tag, String message)
    {
        Log.v(tag, message);
    }
}
