package com.szu.twowayradio.utils;

public class ByteConvert {
	/**
	 * 
	 * @return byte[]
	 */
	 public static void longToBytes(byte[] b,long n,int offset) {
	       
	        b[0+offset] = (byte) (n & 0xff);
	        b[1+offset] = (byte) (n >> 8  &0xff);
	        b[2+offset] = (byte) (n >> 16 & 0xff);
	        b[3+offset] = (byte) (n >> 24 & 0xff);
	        b[4+offset] = (byte) (n >> 32 & 0xff);
	        b[5+offset] = (byte) (n >> 40 & 0xff);
	        b[6+offset] = (byte) (n >> 48 & 0xff);
	        b[7+offset] = (byte) (n >> 56 & 0xff);
	        return ;
	 }
	/**
	 * 
	 * @return byte[]
	 */
	 public static void longToBytes(byte[] b,long n) {
	        
	        longToBytes(b,n, 0);
	 }
	 /**
	  * 
	  * @return byte[]
	  */
	 public static void intToBytes(byte[] b,int n,int offset) {
	        
	        b[0+offset] = (byte) (n & 0xff);
	        b[1+offset] = (byte) (n >> 8 & 0xff);
	        b[2+offset] = (byte) (n >> 16 & 0xff);
	        b[3+offset] = (byte) (n >> 24 & 0xff);
	        return ;
	 }
	 
	 /**
	  * 
	  * @return byte[] offset=0
	  */
	 public static void intToBytes(byte[] b,int n) {
	        
	         intToBytes(b,n,0);
	 }
	 
	 /**
	  * 
	  * @return
	  */
	 public static void shortToBytes(byte[] b,short n,int offset) {
	        
	        b[0+offset] = (byte) (n & 0xff);
	        b[1+offset] = (byte) ((n >> 8) & 0xff);
	        return ;
	  }
	 
	 /**
	  * 
	  * @return byte[]
	  */
	 public static void shortToBytes(byte[] b,short n) {
	        
	        shortToBytes(b,n, 0);
	  }
	 
	 /**
	  * 
	  * @return long
	  */
	 public static long bytesToLong( byte[] array ){
	        return ((((long) array[ 0] & 0xff)<< 56)
	              | (((long)array[ 1] & 0xff) << 48)
	              | (((long)array[ 2] & 0xff) << 40)
	              | (((long)array[ 3] & 0xff) << 32)
	              | (((long)array[ 4] & 0xff) << 24)
	              | (((long)array[ 5] & 0xff) << 16)
	              | (((long)array[ 6] & 0xff) << 8) 
	              | (((long)array[ 7] & 0xff) << 0));        
	 }
	 /**
	  * 
	  * @return int
	  */
	 public static int bytesToInt(byte b[],int offset) {
	        return    b[0+offset] &0xff
	              | (b[1+offset] & 0xff) << 8
	              | (b[2+offset] & 0xff) << 16
	              | (b[3+offset] & 0xff) << 24;
	 }

    /**
     *
     * @return int
     */
    public static int bytesToInt(byte b[]) {
        return bytesToInt(b, 0);
    }
	 /**
	  * 
	  * @return short
	  */
	 public static short bytesToShort(byte[] b,int offset){
	        return (short)( b[0+offset] & 0xff
	                     |(b[1+offset] & 0xff) << 8 ); 
	  }    
	 
	 /**
	  * 
	  * @return short
	  */
	 public static short bytesToShort(byte[] b){
	        return bytesToShort(b,0); 
	  }    

	 public static byte combine2Bytes(byte src1,byte src2)
	 {
		 byte res=0;
		 
		 res|=(byte)(src1 & 0x0f);
		 
		 res|=(byte) (src2 << 4);
		 return res;
	 }

    public static byte[] convertNetLow2High(byte[] b, int offset, short length)
    {
        byte[] res = new byte[length];
        for(int i=0; i<length; i++)
        {
            res[i] = b[offset +length -i];
        }
        return res;
    }

    public static byte[] combineBytes(int totalLength, byte[]... b)
    {
        byte[] res = new byte[totalLength];
        int currentLength = 0;
        for(int i=0; i<b.length; i++)
        {
            for(int j=0; j<b[i].length; j++)
            {
                res[currentLength ++] = (b[i])[j];
                if(currentLength >= totalLength)
                {
                    return res;
                }
            }

        }
        return res;
    }

    public static void copy(byte[] src, byte[] dest, int length,int offset)
    {
        if(length > dest.length && ((length+offset) > src.length))
        {
            return ;
        }
        for(int i=0; i<length; i++)
        {
            dest[i] = src[i+offset];
        }
    }

    public static void copy(byte[] src, byte[] dest, int length)
    {
        copy(src, dest, length, 0);
    }

    public static void copy(byte[] src, byte[] dest)
    {
        copy(src, dest, src.length, 0);
    }

    public static void print(byte[] b)
    {
        for(int i=0;i<b.length;i++)
        {
            System.out.printf("%x ",b[i]);
        }
    }
}
