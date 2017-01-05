package com.example.jeylnastoninfer.debug7.auxUtils.networkUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.example.jeylnastoninfer.debug7.daemonAct.RegisterActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;


public final class NetworkHelper{

    public static boolean checkInternetAvailable(){
        String result = null;
        try{
            String ip = "www.baidu.com";
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while((content = in.readLine()) != null){
                Log.i("debug", "in loop");
                stringBuffer.append(content);
            }
            Log.d("------ping------", "result content : " + stringBuffer.toString());
            int status = p.waitFor();
            if(status == 0){
                result = "success";
                Log.i("debug", "network okay");
                return true;
            }else{
                Log.i("debug", "network not available");
                result = "failed";
            }
        }catch (IOException e){
            result = "IOException";
        }catch (InterruptedException e){
            result = "InterruptedException";
        }finally {
            Log.d("------result------", "result = " + result);
        }
        return false;
    }

    public static boolean isPhoneNumber(String number){
        String telRegx = "[1][34578]\\d{9}";
        if(TextUtils.isEmpty(number)) return false;
        return number.matches(telRegx);
    }
}
