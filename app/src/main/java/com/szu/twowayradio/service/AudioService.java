package com.szu.twowayradio.service;

import com.szu.twowayradio.domains.AdpcmState;
import com.szu.twowayradio.network.UdpHelper;
import com.szu.twowayradio.utils.ByteConvert;
import com.szu.twowayradio.utils.DebugLog;

import java.net.DatagramPacket;

/**
 * lgp on 2015/3/14.
 */
public class AudioService {
    private static AudioService audioService = new AudioService();
    public static final int AUDIO_DATA_LENGTH = 1024;
    public static final int AUDIO_DATA_HEAD_LENGTH = 84;
    private UdpHelper udpHelper;
    public static AudioService getInstance()
    {
        return audioService;
    }

    private AudioService() {
        udpHelper = UdpHelper.getInstance();
    }

    public void sendAudio(byte[] data, AdpcmState state)
    {
        if (!udpHelper.isInit())
        {
            udpHelper.initNetWork();
        }
        int len = 20;
        int length = data.length;
        byte[] buffer = new byte[data.length + AUDIO_DATA_HEAD_LENGTH];

//        buffer[0] = 0x31;
//        buffer[1] = 0x00;
//        buffer[2] = 0x4c;
//        buffer[3] = 0x02;
//
//        buffer[4] = 0x01;
//        buffer[5] = 0x00;
//        buffer[6] = 0x00;
//        buffer[7] = 0x00;
//
//        buffer[8] = 0x1a;
//        buffer[9] = 0x04;
//        buffer[10] = (byte)0xb3;
//        buffer[11] = 0x00;
//
//        buffer[12] = 0x00;
//        buffer[13] = 0x00;
//        buffer[14] = 0x00;
//        buffer[15] = 0x00;
//
//        buffer[16] = 0x00;
//        buffer[17] = 0x00;
//        buffer[18] = 0x00;
//        buffer[19] = 0x00;



//        int utmp1 = 0x000001c0;//int

//        GenPesHeader(utmp1, 4, len, buffer);
//        len += 4;
//
//        utmp1 = (length/2 + (64 - 6) << 16) |   // PES_packet_length
//                (0x87  << 8 ) |
//                (0x3   << 6 );
//        GenPesHeader(utmp1, 4, len,buffer);
//        len += 4;
//
//        int dts = 0;
//        utmp1 = (	(32 - 9)                    << 24) |  //
//        (0x3                          << 20) |  //
//        (((dts >> 29) & 0x7)    << 17) |  //  PTS[32:30]
//                (0x1                          << 16)        |  //  marker_bit
//        (((dts >> 14) & 0x7FFF) << 1 ) |  //  PTS[29:15]
//                0x1                                  ;  //  marker_bit
//        GenPesHeader(utmp1, 4, len,buffer);
//        len += 4;
//
//        utmp1 = (	(dts & 0x3FFF)       << 18) |  //  PTS[14:1] << 1
//                (0x1                        << 16) |  //  marker_bit
//        (0x1                        << 12) |  //  0001
//        (((dts >> 29) & 0x7)  << 9 ) |  //  DTS[32:30]
//                (0x1                        << 8 ) |  //  marker_bit
//        ((dts >> 21) & 0xFF)         ;  //  DTS[29:22]
//        GenPesHeader(utmp1, 4, len,buffer);
//        len += 4;
//
//        utmp1 = (	((dts >> 14) & 0x7F) << 25) |  //  DTS[21:15]
//                (0x1                        << 24) |  //  marker_bit
//        ((dts & 0x3FFF)       << 10) |  //  DTS[14:1] << 1
//                (0x1                        << 8 ) |  //  marker_bit
//        (0x0F                       << 0 ) ;  //  PES_externtion_flag_2 = 1
//        GenPesHeader(utmp1, 4, len,buffer);
//        len += 4;
//
//        utmp1 = (0x1                   << 31) |  //  marker_bit
//        (11                     << 24) |  //  PES_extention_field_length
//        (0x1                    << 23) |  //  marker
//        (((length >> 11) & 0x7FF) << 12) |  //  prev_PES_packet_length[21:11]
//                (0x1                    << 11) |  //  marker
//        (((length >> 0 ) & 0x7FF) << 0 ) ;  //  prev_PES_packet_length[10:0]
//        GenPesHeader(utmp1, 4, len,buffer);
//        len += 4;
//
//
//        int utmp0 = ((length + 31) & (~(0x1F)));
//        utmp1 = (0x1                    << 31) |  //  marker
//        (((utmp0 >> 11) & 0x7FF) << 20) |  //  marker_bit
//                (0x1                    << 19) |  //  marker
//        (((utmp0 >> 0 ) & 0x7FF) << 8 ) |  //  marker_bit
//                (0x0                    << 5 ) |  //  app. specific(3bit)
//        0 << 2 | //((p_eso->status & 0x7)   << 2 ) |  //  {new_start_after_vloss, skip, new_start_after_skip}
//                0;	//	(p_eso->pictype          << 0 ) ;  //  picture type
//        GenPesHeader(utmp1, 4, len,buffer);
//        len += 4;
//
//        utmp1	= (	0xff<<24 |
//                0xffff<<16 |
//                0xff);
//        GenPesHeader(utmp1, 4, len,buffer);  // app. specific (32bit)
//        len += 4;
//
//        // 31~55 ==> 0xff
//        utmp1 = 0xffffffff;
//        GenPesHeader(utmp1, 24, len,buffer);
//        len += 24;
//
//        // mark the postion
//        utmp1 = len;
//        len += 3;
//
//        buffer[len] = (byte)0xff;
//        len ++;
//
//        GenPesHeader(0xffffffff, 4, len, buffer);
//        len += 4;

        byte[] head = {0x31,(byte)0xc6,0x4c,0x2,0x1,0x0,0x0,0x0,0x2,0x4,0xc,0x0,0x0,0x0,
                0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x1,(byte)0xc0,0x2,0x3a,(byte)0x87,(byte)0xc0,0x17,
                0x37,0x23,0x67,(byte)0xb3,0x61,0x17,0x23,0x67,(byte)0xb3,0x61,0xf,(byte)0x8b,(byte)0x80,0xa,0x0,(byte)0x80,0xa,
                0x0,0x0,(byte)0xff,(byte)0xff,0x0,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
                (byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
                (byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff
                ,0x50,0x1,0x20,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
        ByteConvert.copy(head, buffer, AUDIO_DATA_HEAD_LENGTH, 0);
        //audio data offset
        for(int i=0; i<length; i++)
            buffer[i + AUDIO_DATA_HEAD_LENGTH] = data[i];

        buffer[76] = (byte)(state.getValprev() & 0x00ff);
        buffer[77] = (byte)((state.getValprev() & 0xff00) >> 8);
        buffer[78] = (state.getIndex());

        udpHelper.send(buffer);
    }

    private void GenPesHeader(int ndata ,int nlength ,int npos, byte[] sendbuf)
    {
        int data = ndata;
        int length = nlength;
        int pos = npos;
        int i;
        for(i=0; i<length; i++)
        {
            sendbuf[i+pos]	= (byte)((data>>( (length-i-1)*8) ) & 0xff);
        }
    }

    public byte[] receiveAudio(AdpcmState state)
    {
        byte[] buf = receive();
        if (buf == null)
            return null;
        if (buf.length <= AUDIO_DATA_HEAD_LENGTH)
        {
            return null;
        }

        byte[] temp = new byte[buf.length - AUDIO_DATA_HEAD_LENGTH];
        for(int i=0; i<temp.length; i++){
            temp[i] = buf[i + AUDIO_DATA_HEAD_LENGTH];
        }

        short valprev = ByteConvert.bytesToShort(buf, 76);
        byte index = buf[78];
        if (state != null)
        {
            state.setValprev(valprev);
            state.setIndex(index);
        }
        DebugLog.printBytes(buf, 84);
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
