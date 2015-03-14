package com.szu.twowayradio.domains;

import com.szu.twowayradio.utils.ByteConvert;

import java.io.Serializable;


public class NmcpHead implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	
	public static final byte DEFAULT_VERSION =1;
	
	public static final byte NMCP_SUBP_STUN =1;
	public static final byte NMCP_SUBP_CONNECT	=2;
	public static final byte NMCP_SUBP_RTP =3;
	public static final byte NMCP_SUBP_RTCP =4;
	public static final byte NMCP_SUBP_OM =5;
	public static final byte NMCP_SUBP_QUERY =6;
	public static final byte NMCP_SUBP_QUERYACK =7;
	public static final byte NMCP_SUBP_ASSIST =8;
	public static final byte NMCP_SUBP_ASSISTACK =9;
	public static final byte NMCP_SUBP_RTPRETRY =10;
	public static final byte NMCP_SUBP_OMACK =11;
	public static final byte NMCP_SUBP_RTPRETRYACK =12;
	public static final byte NMCP_SUBP_RTCPACK	=13;
	
	
	public static final byte CONN_CONNECT		=1;
	public static final byte CONN_CONNECTACK		=2;
	/************
	public static final byte CONN_QUERY			=3;
	public static final byte CONN_QUERYACK		=4;
	public static final byte CONN_ASSIST			=5;
	public static final byte CONN_ASSISTACK		=6;
	*********/
	public static final byte CONN_PULSE			=7;
	public static final byte CONN_PULSEACK		=8;
	public static final byte CONN_DISCONNECT		=9;
	public static final byte CONN_CONNECT2		=10;
	public static final byte CONN_CONNECTACK2	=11;
	
	
	private byte version;
	private byte subpID;
	private byte descrip;
	private short length;
	private int transactionID;
    
    public NmcpHead(byte version,byte subpID,byte descrip,short length,int transactionID)
    {
    	this.version = version;
    	this.subpID = subpID;
    	this.descrip = descrip;
    	this.length = length;
    	this.transactionID = transactionID;
    }
    
    public NmcpHead(byte subpID,byte descrip,short length,int transactionID)
    {
    	this(DEFAULT_VERSION,subpID,descrip,length,transactionID);
    }
    
    public NmcpHead(byte subpID,byte descrip,int transactionID)
    {
    	this(subpID,descrip,(short)0,transactionID);
    }
    
    public byte[] getBytes()
    {
    	byte b[] = new byte[8];
    	b[0] = ByteConvert.combine2Bytes(version, subpID);
    	b[1] = descrip;
    	ByteConvert.shortToBytes(b, length, 2);
    	ByteConvert.intToBytes(b, transactionID, 4);
    	return b;
    }

    public void setSubpID(byte subpID) {
        this.subpID = subpID;
    }

    public void setDescrip(byte descrip) {
        this.descrip = descrip;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public void setVersion(byte version) {
        this.version = version;
    }
}
