package rogne.ntnu.no.cardroid.Utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Kristoffer on 2017-11-10.
 */

public class ByteUtils {
    public static byte[] getSeperatorBytes(){
        return "JPEG".getBytes();
    }
    public static byte[] concat(byte[]... a){
        if(a.length == 0){
            return null;
        }
        byte[] returnByte = a[0];
        for(int i = 1; i < a.length; i++){
            returnByte = concat(returnByte, a[i]);
        }
        return returnByte;
    }
    public static byte[] intToByte(int i){
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(i);
        return buf.array();
    }

    public static int byteToInt(byte[] bytes){
        if(bytes.length != 4){
            return -1;
        }
        return ByteBuffer.wrap(bytes).getInt();
    }
    public static byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c= new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
