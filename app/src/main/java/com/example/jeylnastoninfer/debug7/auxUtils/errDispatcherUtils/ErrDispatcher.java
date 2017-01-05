package com.example.jeylnastoninfer.debug7.auxUtils.errDispatcherUtils;


import android.content.Context;
import android.widget.Toast;

public class ErrDispatcher {

    private static String lastMsg = null;
    private static long lastMsgTime = 0;

    private static final int miniUsrNameLen = 5;

    private static final int phoneNumberLen = 11;

    private static final int miniPwdLen = 6;

    public enum ErrCode {
        NETWORK_FAILED, USR_NAME_TOO_SHORT, PHONE_NUMBER_LEN_ILLEGAL, NOT_A_PHONE_NUMBER,
        PWD_TOO_SHORT, CODE_NOT_AVAILABLE, CODE_MISMATCH, NETWORK_NOT_AVAILABLE,
        PHONE_ALREADY_REGISTERED, PHONE_NOT_REGISTERED, PWD_MISMATCH
    };

    public static void errHandler(Context context, ErrCode errCode){
        switch (errCode){
            case NETWORK_FAILED:
                onNetworkFail(context);
                break;
            case USR_NAME_TOO_SHORT:
                onUserNameTooShort(context);
                break;
            case PHONE_NUMBER_LEN_ILLEGAL:
                onPhoneNumberLenIllegal(context);
                break;
            case NOT_A_PHONE_NUMBER:
                onNotAPhoneNumber(context);
                break;
            case PWD_TOO_SHORT:
                onPasswordTooShort(context);
                break;
            case CODE_NOT_AVAILABLE:
                onCodeNotAvailable(context);
                break;
            case CODE_MISMATCH:
                onCodeMismatch(context);
                break;
            case NETWORK_NOT_AVAILABLE:
                onNetworkNotAvailable(context);
                break;
            case PHONE_ALREADY_REGISTERED:
                onPhoneAlreadyRegisterd(context);
                break;
            case PHONE_NOT_REGISTERED:
                onPhoneAlreadyRegisterd(context);
                break;
            case PWD_MISMATCH:
                onPasswordMismatch(context);
                break;
        }
    }

    private static void onPasswordMismatch(Context context){
        toast(context, "您输入的密码不正确，请检查后再试");
    }

    private static void onNetworkFail(Context context){
        toast(context, "网络未连接，请检查后再试");
    }

    private static void onPhoneNotRegistered(Context context){
        toast(context, "此号码尚未注册，请注册后登录");
    }

    private static void onUserNameTooShort(Context context){
        toast(context, "用户名长度至少应为 " + miniUsrNameLen);
    }

    private static void onPhoneNumberLenIllegal(Context context){
        toast(context, "手机号码长度应为 " + phoneNumberLen);
    }

    private static void onNotAPhoneNumber(Context context){
        toast(context, "手机号码格式有误");
    }

    private static void onPasswordTooShort(Context context){
        toast(context, "密码长度至少应为 " + miniPwdLen);
    }

    private static void onCodeNotAvailable(Context context){
        toast(context, "验证码未发送或已过期");
    }

    private static void onCodeMismatch(Context context){
        toast(context, "验证码不正确");
    }

    private static void onNetworkNotAvailable(Context context){
        toast(context, "当前网络不可用，请检查后再试");
    }

    private static void onPhoneAlreadyRegisterd(Context context){
        toast(context, "此号码已注册，请直接登录");
    }

    private static void toast(Context context, String msg){
        //must be called in UI thread
        long curTime = System.currentTimeMillis();
        if(lastMsg != null && msg.equals(lastMsg) && curTime - lastMsgTime < 2000){
            lastMsgTime = curTime;
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        lastMsg = msg;
        lastMsgTime = curTime;
    }
}
