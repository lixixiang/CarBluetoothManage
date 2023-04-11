package com.shenzhen.honpe.carbluetoothmanage.util;

import android.text.TextUtils;

/**
 * FileName: StringUtils
 * Author: asus
 * Date: 2021/5/26 13:59
 * Description:
 */
public class StringUtils {


    /**
     * 字符串转数组
     * @param str
     * @return
     */
    public static byte[] strToBytes(String str){
        return str.getBytes();
    }

    /**
     * 数组转字符串
     * @param bytes
     * @return
     */
    public static String bytesToStr(byte[] bytes){
        return new String(bytes);
    }
    /**
     * 为了方便字符的加减操作，通常以16进制字符替代普通字符与byte数组进行相互转换
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString
     *            16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] toByteArr(String hexString){
        if (TextUtils.isEmpty(hexString)) {
            throw new IllegalArgumentException("this hexString must not be empty");
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }
    /** 为了方便字符的加减操作，通常以16进制字符替代普通字符与byte数组进行相互转换
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray
     *            需要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }


}






















