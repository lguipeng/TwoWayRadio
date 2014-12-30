package com.szu.twowayradio.domains;


/**
 * Created by lgp on 2014/9/2.
 */
public class User implements java.io.Serializable{

    public static final int USER_NAME_LEN = 20;
    public static final int USER_PASSWORD_LEN = 16;
    private String name;
    private String password;
    private int transactionID;
    private byte[]	mClass;
    private byte[]	group;
    private int	OAuthority;
    private int	RAuthority;
    private int	CAuthority;
    private byte[] 	noUse;

    public User() {
    }

    public User(String name, String password) {
        this(name,password,1);
    }

    public User(String name, String password, int transactionID) {
        this.name = name;
        this.password = password;
        this.transactionID = transactionID;
        this.mClass = new byte[28];
        this.group = new byte[4];
        this.noUse = new byte[48];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public byte[] getGroup() {
        return group;
    }

    public byte[] getmClass() {
        return mClass;
    }

    public int getOAuthority() {
        return OAuthority;
    }

    public int getRAuthority() {
        return RAuthority;
    }

    public int getCAuthority() {
        return CAuthority;
    }

    public byte[] getNoUse() {
        return noUse;
    }

    public void setOAuthority(int OAuthority) {
        this.OAuthority = OAuthority;
    }

    public void setRAuthority(int RAuthority) {
        this.RAuthority = RAuthority;
    }

    public void setCAuthority(int CAuthority) {
        this.CAuthority = CAuthority;
    }
}
