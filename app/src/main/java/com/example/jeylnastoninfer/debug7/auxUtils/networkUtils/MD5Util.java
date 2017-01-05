package com.example.jeylnastoninfer.debug7.auxUtils.networkUtils;

import java.security.MessageDigest;


public class MD5Util {

    public static String getMD5String(String key){
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F'};
        try{
            byte[] input = key.getBytes();
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            md5Digest.update(input);
            byte[] md5byte = md5Digest.digest();

            int j = md5byte.length;
            char md5char[] = new char[j << 1];

            int k = 0;

            for(byte b: md5byte){
                md5char[k++] = hexDigits[b >>> 4 & 0xf];
                md5char[k++] = hexDigits[b & 0xf];
            }

            return new String(md5char);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String bytes2hexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();

        for(byte aByte : bytes){

            String hex = Integer.toHexString(((1 << 16) - 1) & aByte);
            if(hex.length() == 1){
                sb.append('0');
            }
            sb.append(hex);
        }

        return sb.toString();
    }

}
