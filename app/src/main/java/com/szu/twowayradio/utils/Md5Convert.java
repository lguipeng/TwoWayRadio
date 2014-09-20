package com.szu.twowayradio.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lgp on 2014/9/2.
 */
public class Md5Convert {

    public static byte[] md5(String string)
    {

        try{
           return   MessageDigest.getInstance("MD5").digest(string.getBytes("utf-8"));
        }catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
