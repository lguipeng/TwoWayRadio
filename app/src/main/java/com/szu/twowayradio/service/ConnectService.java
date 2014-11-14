package com.szu.twowayradio.service;

import com.szu.twowayradio.domain.NmcpHead;
import com.szu.twowayradio.domain.User;
import com.szu.twowayradio.network.NetWorkService;
import com.szu.twowayradio.network.UdpHelper;
import com.szu.twowayradio.utils.ByteConvert;
import com.szu.twowayradio.utils.Logger;
import com.szu.twowayradio.utils.Md5Convert;

import java.net.DatagramPacket;

/**
 * Created by lgp on 2014/9/2.
 */
public class ConnectService {
    public final String TAG = getClass().getSimpleName();
    private static ConnectService connectService = new ConnectService();
    private UdpHelper udpHelper;
    private boolean isConnect = false;
    private ConnectListener mConnectListener = null;

    public static ConnectService getInstance()
    {
        return connectService;
    }

    private ConnectService() {
        udpHelper = new UdpHelper();
    }

    //must not run in main threads
    public void connect(final User user)
    {
        NmcpHead head = new NmcpHead(NmcpHead.NMCP_SUBP_CONNECT, NmcpHead.CONN_CONNECT, 1);
        if(udpHelper.initNetWork())
        {
            setConnect(true);
        }else
        {
            onConnectFailEvent();
            return ;
        }
        if(!udpHelper.send(head.getBytes()))
            return;
        byte[] receive = receive();

        if(receive == null)
        {
            onConnectFailEvent();
            return;
        }

        user.setTransactionID(ByteConvert.bytesToInt(receive, 8));
        Logger.getInstance().debug(TAG,"TransactionID is "+user.getTransactionID());
        head.setTransactionID(user.getTransactionID());
        head.setSubpID(NmcpHead.NMCP_SUBP_OM);
        head.setDescrip( (byte)0 );
        head.setLength( (short)48 );
        byte[] type = new byte[12];
        ByteConvert.intToBytes(type, 0x00280001);
        ByteConvert.intToBytes(type,0x00000001,4);
        ByteConvert.intToBytes(type,0x00240001,8);
        byte[] loginMessage;
        if(user.getName().getBytes().length < User.USER_NAME_LEN)
        {
            byte[] temp = new byte[User.USER_NAME_LEN -user.getName().getBytes().length];
            loginMessage = ByteConvert.combineBytes(56, head.getBytes(), type,user.getName().getBytes(), temp,
                    Md5Convert.md5(user.getName()));
        }else{
            loginMessage = ByteConvert.combineBytes(56,head.getBytes(),type,user.getName().getBytes(),
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
        if(udpHelper.receive() == null)
            return null;
        return udpHelper.receive().getData();
    }

    private void onConnectFailEvent()
    {
        if(mConnectListener != null)
        {
            mConnectListener.connectFail();
        }
        setConnect(false);
    }

    private void onConnectSuccessEvent(User user)
    {
        if(mConnectListener != null)
        {
            mConnectListener.connectSuccess(user);
        }
        setConnect(true);
    }

    public static interface ConnectListener{
        void connectSuccess(User user);
        void connectFail();
        void disconnectSuccess();
        void disconnectFail();
    }
}
