package com.szu.twowayradio.service;

import android.text.TextUtils;

import com.szu.twowayradio.domains.NmcpHead;
import com.szu.twowayradio.domains.User;
import com.szu.twowayradio.network.UdpHelper;
import com.szu.twowayradio.utils.ByteConvert;
import com.szu.twowayradio.utils.DebugLog;
import com.szu.twowayradio.utils.Logger;
import com.szu.twowayradio.utils.Md5Convert;

import java.net.DatagramPacket;
import java.util.Arrays;

/**
 *  lgp on 2014/9/2.
 */
public class ConnectService {
    public final String TAG = getClass().getSimpleName();
    private static ConnectService connectService = new ConnectService();
    private UdpHelper udpHelper;
    private boolean isConnect = false;
    private ConnectListener mConnectListener = null;
    public static int transactionID;
    private static final int MAX_RETRY_CONNECT_TIME = 3;
    private  int retryTime = 0;
    public static ConnectService getInstance()
    {
        return connectService;
    }

    private ConnectService() {
        udpHelper = UdpHelper.getInstance();
    }

    //must not run in main threads
    public void connect1(final User user)
    {
        NmcpHead head = new NmcpHead(NmcpHead.NMCP_SUBP_CONNECT, NmcpHead.CONN_CONNECT, 1);

        if(!udpHelper.isInit())
        {
            if (!udpHelper.initNetWork()){
                onConnectFailEvent();
                DebugLog.e("initNetWork error");
                return ;
            }
        }else{
            DebugLog.e("initNetWork already");
        }

        byte[] receive = receive();

        if(receive == null)
        {
            onConnectFailEvent();
            return;
        }

        user.setTransactionID(ByteConvert.bytesToInt(receive, 8));
        Logger.getInstance().debug(TAG, "TransactionID is "+user.getTransactionID());
        head.setTransactionID(user.getTransactionID());
        head.setSubpID(NmcpHead.NMCP_SUBP_OM);
        head.setDescrip( (byte)0 );
        head.setLength( (short)48 );
        byte[] type = new byte[12];
        ByteConvert.intToBytes(type, 0x00280001);
        ByteConvert.intToBytes(type, 0x00000001,4);
        ByteConvert.intToBytes(type, 0x00240001,8);
        byte[] loginMessage;
        if(user.getName().getBytes().length < User.USER_NAME_LEN)
        {
            byte[] temp = new byte[User.USER_NAME_LEN -user.getName().getBytes().length];
            loginMessage = ByteConvert.combineBytes(56, head.getBytes(), type,user.getName().getBytes(), temp,
                    Md5Convert.md5(user.getName()));
        }else{
            loginMessage = ByteConvert.combineBytes(56, head.getBytes(),type,user.getName().getBytes(),
                    Md5Convert.md5(user.getPassword()));
        }
        ByteConvert.print(loginMessage);
        udpHelper.send(loginMessage);

        receive = receive();
        if(receive == null)
        {
            onConnectFailEvent();
            return;
        }

        if(receive[0] == ByteConvert.combine2Bytes(NmcpHead.DEFAULT_VERSION,NmcpHead.NMCP_SUBP_OMACK))
        {
            receive = receive();
            if(receive == null)
            {
                onConnectFailEvent();
                return;
            }
            byte[] result = new byte[8];
            ByteConvert.copy(receive,result,8,16);
            if(ByteConvert.bytesToInt(result) == 0)
            {
                byte[] authority = new byte[4];
                ByteConvert.copy(receive,user.getmClass(),28,60);
                ByteConvert.copy(receive,user.getGroup(),4,88);
                ByteConvert.copy(receive,authority,4,92);
                user.setOAuthority(ByteConvert.bytesToInt(authority));
                ByteConvert.copy(receive,authority,4,96);
                user.setRAuthority(ByteConvert.bytesToInt(authority));
                ByteConvert.copy(receive,authority,4,100);
                user.setCAuthority(ByteConvert.bytesToInt(authority));
                ByteConvert.copy(receive,user.getNoUse(),48,104);

                onConnectSuccessEvent(user);
                head.setSubpID(NmcpHead.NMCP_SUBP_OMACK);
                head.setLength((short)8);
                byte [] om = new byte[8];
                ByteConvert.intToBytes(om,0x00010088);
                ByteConvert.intToBytes(om,0x00000001,4);
                byte [] b = ByteConvert.combineBytes(16,head.getBytes(),om);
                udpHelper.send(b);

                head.setSubpID(NmcpHead.NMCP_SUBP_OM);
                head.setLength((short)14);
                ByteConvert.intToBytes(om,0x00c8000c);
            }else
            {
                onConnectFailEvent();
            }
        }else
        {
            onConnectFailEvent();
        }

    }

    public void disconnect()
    {
        setConnect(false);
        byte data0[] ={0x51, 0x00, 0x14, 0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff), (byte) 0xC8, 0x00,
                0x0C, 0x00, 0x04, 0x00, 0x00, 0x00, (byte)0xC8, 0x00, 0x08, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        udpHelper.send(data0);

        byte data1[] ={0x51, 0x00, 0x14, 0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff), (byte) 0xC8,
                0x00, 0x0C, 0x00, 0x04, 0x00, 0x00, 0x00, (byte)0xC8, 0x00, 0x08, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        udpHelper.send(data1);

        byte data2[] ={0x51, 0x00, 0x14, 0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff), (byte) 0xC8,
                0x00, 0x0C, 0x00, 0x04, 0x00, 0x00, 0x00, (byte)0xC8, 0x00, 0x08, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        udpHelper.send(data2);
        try {
            receive();
            receive();
            receive();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            byte data3[] = {0x21, 0x09, 0x00, 0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                    (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff)};
            udpHelper.send(data3);
            onDisConnectSuccessEvent();
        }

    }

    public void connect(User user)
    {
        if (user == null || TextUtils.isEmpty(user.getName()) || TextUtils.isEmpty(user.getPassword())){
            onConnectFailEvent();
            return;
        }

        if (user.getName().length() > User.USER_NAME_LEN || user.getPassword().length() > User.USER_PASSWORD_LEN){
            onConnectFailEvent();
            return;
        }

        if(!udpHelper.isInit())
        {
            if (!udpHelper.initNetWork()){
                onConnectFailEvent();
                DebugLog.e("initNetWork error");
                return ;
            }
        }else{
            DebugLog.e("initNetWork already");
        }

        byte data0[] = {0x21 ,0x01 ,0x00 ,0x00, 0x01,0x00 ,0x00 ,0x00};
        udpHelper.send(data0);
        byte[] buf0 = receive();

        if (buf0 == null || buf0.length < 12){
            onConnectFailEvent();
            return;
        }
        DebugLog.printBytes(buf0);
        user.setTransactionID(ByteConvert.bytesToInt(buf0, 8));
        transactionID = ByteConvert.bytesToInt(buf0, 8);

        if(buf0[0] == 0x21){

            byte data1[] ={0x51, 0x00, 0x30, 0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                    (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff),
                    0x01, 0x00, 0x28, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00, 0x24, 0x00,
//                    0x61, 0x64, 0x6D, 0x69, 0x6E, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//
//                    0x21, 0x23, 0x2F,0x29, 0x7A, 0x57, (byte) 0xA5, (byte) 0xA7,0x43, (byte) 0x89, 0x4A,
//                    0x0E, 0x4A, (byte) 0x80, 0x1F, (byte) 0xC3
            };

            byte [] name = user.getName().getBytes();
            name = Arrays.copyOf(name, User.USER_NAME_LEN);
            byte [] password = Md5Convert.md5(user.getPassword());
            data1 = ByteConvert.combineBytes(68, data1, name, password);
            udpHelper.send(data1);

            byte[] buf1 = receive();
            DebugLog.printBytes(buf1);

            byte[] buf2 = receive();
            if (buf2 == null || buf2.length < 24){
                onConnectFailEvent();
                return;
            }
            DebugLog.printBytes(buf2);

            int ack = ByteConvert.bytesToInt(buf2, 20);
            if (ack != 0){
                onConnectFailEvent();
                return;
            }
            if(buf2[0] == 0x51){
                byte data2[] ={(byte) 0xB1, (byte)0x9D, 0x08, 0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                        (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff), 0x01, 0x00, (byte)0x88, 0x00, 0x01, 0x00, 0x00, 0x00};
                udpHelper.send(data2);

                byte data3[] ={0x51, 0x00 ,0x14 ,0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                        (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff), (byte)0xC8,
                        0x00 ,0x0C ,0x00, 0x02, 0x00, 0x00, 0x00, (byte)0xC8 ,0x00, 0x08,
                        0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 ,0x00 };
                udpHelper.send(data3);

                byte[] buf3 = receive();
                //DebugLog.e("buf3[0]="+buf3[0]);

                byte[] buf4 = receive();
                //DebugLog.e("buf4[0]="+buf4[0]);

                byte[] buf5 = receive();
                //DebugLog.e("buf5[0]=" + buf5[0]);
                if (buf5 == null){
                    onConnectFailEvent();
                    return;
                }
                if(buf5[0] == 81){
                    byte data4[] ={(byte) 0xB1, 0x00, 0x08, 0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                            (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff),
                            (byte)0xC8, 0x00, 0x08, 0x00, 0x02, 0x00, 0x00, 0x00 };
                    udpHelper.send(data4);

                    byte data5[] ={0x51, 0x00, 0x14, 0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                            (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff), 0x36,
                            0x00, 0x0C, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                            0x00, (byte)0xBC, (byte)0x82, (byte)0xF9, 0x54, 0x00, 0x00, 0x00, 0x00 };
                    udpHelper.send(data5);

                    byte[] buf6 = receive();
                    //DebugLog.e("buf6[0]="+buf6[0]);

                    byte[] buf7 = receive();
                    //DebugLog.e("buf7[0]="+buf7[0]);
                    if (buf7 == null){
                        onConnectFailEvent();
                        return;
                    }
                    if(buf7[0] == 81){
                        byte data6[] ={(byte) 0xB1, 0x00, 0x08, 0x00, (byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                                (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff),
                                0x36, 0x00, 0x08, 0x00, 0x03, 0x00, 0x00, 0x00 };
                        udpHelper.send(data6);
                        byte data7[] ={0x51 ,0x00 ,0x14 ,0x00 ,(byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                                (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff) ,(byte) 0xC8 ,
                                0x00 ,0x0C ,0x00 ,0x08 ,0x00 ,0x00 ,0x00 ,(byte)0xC8 ,0x00 ,0x08 ,
                                0x00 ,0x01 ,0x00 ,0x00 ,0x00 ,0x02 ,0x03 ,0x01,0x00 };
                        udpHelper.send(data7);

                        onConnectSuccessEvent(user);
                    }else{
                        onConnectFailEvent();
                    }
                }else{
                    onConnectFailEvent();
                }
            }else{
                onConnectFailEvent();
            }
        }else{
            disconnect();
            onConnectFailEvent();

        }
    }

    public void sendBeat()
    {
        byte beat[] = {0x21 ,0x07 ,0x00 ,0x00 ,(byte)(transactionID & 0x000000ff), (byte)((transactionID >> 8) & 0x000000ff),
                (byte)((transactionID >> 16 & 0x000000ff)), (byte)((transactionID >> 24) & 0x000000ff)};
        udpHelper.send(beat);
    }

    private void setConnect(boolean coon)
    {
        isConnect = coon;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.mConnectListener = connectListener;
    }

    private byte[] receive()
    {
        DatagramPacket packet;
        if((packet = udpHelper.receive()) == null)
            return null;
        int length = packet.getLength();
        return Arrays.copyOf(packet.getData(), length);
    }

    private void onConnectFailEvent()
    {
        setConnect(false);
        if(mConnectListener != null)
        {
            mConnectListener.connectFail();
        }
    }

    private void onConnectSuccessEvent(User user)
    {
        setConnect(true);
        if(mConnectListener != null)
        {
            mConnectListener.connectSuccess(user);
        }
    }

    private void onDisConnectFailEvent()
    {
        if(mConnectListener != null)
        {
            mConnectListener.disconnectFail();
        }
        setConnect(false);
        udpHelper.close();
    }

    private void onDisConnectSuccessEvent()
    {
        if(mConnectListener != null)
        {
            mConnectListener.disconnectSuccess();
        }
        setConnect(false);
        udpHelper.close();
    }

    public static interface ConnectListener{
        void connectSuccess(User user);
        void connectFail();
        void disconnectSuccess();
        void disconnectFail();
    }
}
