package com.szu.twowayradio.service;

import com.szu.twowayradio.network.UdpHelper;

import java.net.DatagramPacket;

/**
 * lgp on 2015/3/14.
 */
public class AudioService {
    private static AudioService audioService = new AudioService();
    private UdpHelper udpHelper;
    public static AudioService getInstance()
    {
        return audioService;
    }

    private AudioService() {
        udpHelper = UdpHelper.getInstance();
    }

    public void sendAudio(byte[] data)
    {
        if (!udpHelper.isInit())
        {
            udpHelper.initNetWork();
        }
        int len = 20;
        byte[] buffer=new byte[data.length + len];
        for(int i=0; i<data.length; i++)
            buffer[i + len]=data[i];

        buffer[0] = 0x31;
        buffer[1] = 0x00;
        buffer[2] = 0x4c;
        buffer[3] = 0x02;

        buffer[4] = 0x01;
        buffer[5] = 0x00;
        buffer[6] = 0x00;
        buffer[7] = 0x00;

        buffer[8] = 0x1a;
        buffer[9] = 0x04;
        buffer[10] = (byte)0xb3;
        buffer[11] = 0x00;

        buffer[12] = 0x00;
        buffer[13] = 0x00;
        buffer[14] = 0x00;
        buffer[15] = 0x00;

        buffer[16] = 0x00;
        buffer[17] = 0x00;
        buffer[18] = 0x00;
        buffer[19] = 0x00;
        udpHelper.send(buffer);
    }

    public byte[] receiveAudio()
    {
        int len = 100;//According to the addtion data of "Send_Inf.java"
        byte[] buf = receive();
        if (buf == null)
            return null;
        byte[] temp = new byte[buf.length - len];
        for(int i=0;i<temp.length;i++){
            temp[i] = buf[i+len];
        }
        return temp;
    }

    private byte[] receive()
    {
        DatagramPacket packet;
        if((packet = udpHelper.receive()) == null)
            return null;
        return packet.getData();
    }
}
