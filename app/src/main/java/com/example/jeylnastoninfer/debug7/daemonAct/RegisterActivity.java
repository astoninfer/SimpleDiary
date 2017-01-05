package com.example.jeylnastoninfer.debug7.daemonAct;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.R.transition;
import android.support.v7.app.AppCompatActivity;
import android.test.AndroidTestCase;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.jeylnastoninfer.debug7.auxUtils.errDispatcherUtils.ErrDispatcher;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.DrawerHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.NumericHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.networkUtils.NetworkHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.LocalDataUtil;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.RichEditor;
import com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils.RuntimeDataManager;
import com.example.jeylnastoninfer.debug7.R;

import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class RegisterActivity extends SwipeBackActivity{

    private EditText pwdLayout = null;
    private EditText usrNameLayout = null;
    private EditText phoneNumberLayout = null;
    private EditText verificationCodeLayout = null;

    private int textInfoOriginColor = 0;

    private final String mitos = new String("MiTOS");

    private Button codeSendBtn = null;
    private Button submitBtn = null;

    private final int miniUsrNameLen = 5;

    private final int phoneNumberLen = 11;

    private final int miniPwdLen = 6;

    private boolean registerClickable = true;
    private String usrName = null;
    private String phoneNumber = null;
    private String passwd = null;
    private String verificationCode = null;
    private String vCodeSent = null;

    private final int verificationCodeExpire = 60 * 1000;
    //verificationCode available for 60 seconds sine its last dispatch

    private boolean verificationCodeAvailable = false;

    private void init(){

        pwdLayout = (EditText)this.findViewById(R.id.act_register_pwd_fill_ly);
        usrNameLayout = (EditText)this.findViewById(R.id.act_register_usrname_fill_ly);
        phoneNumberLayout = (EditText)this.findViewById(R.id.act_register_phonenumber_fill_ly);
        verificationCodeLayout = (EditText)this.findViewById(R.id.act_register_code_fill_ly);

        codeSendBtn = (Button)this.findViewById(R.id.act_register_code_send_ly);
        submitBtn = (Button)this.findViewById(R.id.act_register_registe_ly);

        textInfoOriginColor = LoginActivity.textInfoOriginColor;

        final TextView usrNameInfo = (TextView)this.findViewById(R.id.act_register_usrname_info_ly);
        usrNameLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    usrNameLayout.setTypeface(null, Typeface.ITALIC);
                    usrNameInfo.setTextColor(getResources().getColor(R.color.colorPrimary));
                    usrNameLayout.setTextColor(getResources().getColor(R.color.pure_black));
                }else{
                    usrNameLayout.setTypeface(null, Typeface.NORMAL);
                    usrNameInfo.setTextColor(textInfoOriginColor);
                    usrNameLayout.setTextColor(textInfoOriginColor);
                }
            }
        });

        final TextView phoneInfo = (TextView)this.findViewById(R.id.act_register_phonenumber_info_ly);
        phoneNumberLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    phoneInfo.setTextColor(getResources().getColor(R.color.colorPrimary));
                    phoneNumberLayout.setTypeface(null, Typeface.ITALIC);
                    phoneNumberLayout.setTextColor(getResources().getColor(R.color.pure_black));
                }else{
                    phoneInfo.setTextColor(textInfoOriginColor);
                    phoneNumberLayout.setTypeface(null, Typeface.NORMAL);
                    phoneNumberLayout.setTextColor(textInfoOriginColor);
                }
            }
        });

        final TextView passwdInfo = (TextView)this.findViewById(R.id.act_register_pwd_info_ly);
        pwdLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    passwdInfo.setTextColor(getResources().getColor(R.color.colorPrimary));
                    pwdLayout.setTypeface(null, Typeface.ITALIC);
                    pwdLayout.setTextColor(getResources().getColor(R.color.pure_black));
                }else{
                    passwdInfo.setTextColor(textInfoOriginColor);
                    pwdLayout.setTypeface(null, Typeface.NORMAL);
                    pwdLayout.setTextColor(textInfoOriginColor);
                }
            }
        });

        verificationCodeLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    verificationCodeLayout.setTypeface(null, Typeface.ITALIC);
                    verificationCodeLayout.setTextColor(getResources().getColor(R.color.pure_black));
                }else{
                    verificationCodeLayout.setTypeface(null, Typeface.NORMAL);
                    verificationCodeLayout.setTextColor(textInfoOriginColor);
                }
            }
        });

        View firstView = this.findViewById(R.id.act_register_topmenu);

        firstView.setPadding(0, NumericHelper.getStatusBarHeight(RegisterActivity.this), 0, 0);

    }

    private boolean checkUsrName(){
        if(usrNameLayout.getText().length() < miniUsrNameLen){
            ErrDispatcher.errHandler(RegisterActivity.this, ErrDispatcher.ErrCode.USR_NAME_TOO_SHORT);
            return false;
        }
        usrName = usrNameLayout.getText().toString();
        return true;
    }

    private boolean checkPhoneNumber(){
        if(phoneNumberLayout.getText().length() != phoneNumberLen){
            ErrDispatcher.errHandler(RegisterActivity.this, ErrDispatcher.ErrCode.PHONE_NUMBER_LEN_ILLEGAL);
            return false;
        }
        phoneNumber = phoneNumberLayout.getText().toString();
        if(!NetworkHelper.isPhoneNumber(phoneNumber)){
            ErrDispatcher.errHandler(RegisterActivity.this, ErrDispatcher.ErrCode.NOT_A_PHONE_NUMBER);
            return false;
        }
        return true;
    }

    private boolean checkPwd(){
        if(pwdLayout.getText().length() < miniPwdLen){
            ErrDispatcher.errHandler(RegisterActivity.this, ErrDispatcher.ErrCode.PWD_TOO_SHORT);
            return false;
        }
        passwd = pwdLayout.getText().toString();
        return true;
    }

    private void checkCode(){
        if(verificationCodeAvailable == false){
            ErrDispatcher.errHandler(RegisterActivity.this, ErrDispatcher.ErrCode.CODE_NOT_AVAILABLE);
            return;
        }
        verificationCode = verificationCodeLayout.getText().toString();
        verifyCode(verificationCode);
    }

    private void setSubmitBtn(){
        submitBtn.setTextColor(textInfoOriginColor);
        submitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    submitBtn.setTextColor(getResources().getColor(R.color.pure_white));
                    submitBtn.setBackground(getDrawable(R.drawable.round_corner_btn_3));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                    submitBtn.setTextColor(textInfoOriginColor);
                    submitBtn.setBackground(getDrawable(R.drawable.round_corner_btn_1));
                }
                return false;
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!registerClickable) return;
                registerClickable = false;
                submitBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        registerClickable = true;
                    }
                }, 1000);
                if(!(checkUsrName() && checkPhoneNumber() && checkPwd())){
                    return;
                }
                checkCode();
            }
        });
    }

    private void setCodeSendBtn(){
        codeSendBtn.setTextColor(textInfoOriginColor);
        codeSendBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                    codeSendBtn.setTextColor(textInfoOriginColor);
                    codeSendBtn.setBackground(getDrawable(R.drawable.round_corner_btn_1));
                }else if(!verificationCodeAvailable && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    codeSendBtn.setTextColor(getResources().getColor(R.color.pure_white));
                    codeSendBtn.setBackground(getDrawable(R.drawable.round_corner_btn_3));
                }
                return false;
            }
        });
        codeSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verificationCodeAvailable) return;
                Log.i("#debug", "you clicked to sent code");
                if(checkPhoneNumber()){
                    sendCodeIfNotRegistered(phoneNumber);
                }
                Log.i("#debug", "network checked done");
            }
        });
    }


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        init();
        setSubmitBtn();
        setCodeSendBtn();
    }

    public void onCodeVerificationDone(){
        Log.i("#debug", "next: add new usr");
        RuntimeDataManager.getDataBaseHelper().addNewUser(RegisterActivity.this, usrName,
                phoneNumber, passwd);
    }

    public void onRegisterDone(){
        //cloud end responses
        //perhaps we can fill last logged-in usr's info auto
        LocalDataUtil.createLocalDirs(phoneNumber);

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("usrPhone", phoneNumber);
        intent.putExtra("usrInfo", bundle);
        startActivity(intent);
        finish();
    }

    public void onCodeSuccessfullySent(){
        verificationCodeAvailable = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(verificationCodeExpire);
                }catch (InterruptedException e){}
                verificationCodeAvailable = false;
            }
        }).start();
    }

    private void sendCodeIfNotRegistered(final String phone){

        final AsyncTask<Void, Void, Void> sendCodeTask = new AsyncTask<Void, Void, Void>(){
            boolean res;

            protected Void doInBackground(Void... params){
                try{
                    AVOSCloud.requestSMSCode(phone, mitos, "注册", 1);
                    res = true;
                }catch (AVException e){
                    e.printStackTrace();
                    res = false;
                }
                return null;
            }

            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                if(res){
                    Toast.makeText(RegisterActivity.this,
                            "验证码已发送，请在1分钟内完成验证", Toast.LENGTH_LONG).show();
                    onCodeSuccessfullySent();
                }else{
                    Toast.makeText(RegisterActivity.this,
                            "验证码发送失败", Toast.LENGTH_LONG).show();
                }
            }
        };

        AsyncTask<Void, Void, Void> checkDuplicateRegisterTask = new AsyncTask<Void, Void, Void>() {
            boolean res = true;
            @Override
            protected Void doInBackground(Void... voids) {
                AVQuery<AVObject> query = new AVQuery<AVObject>("usrInfo");
                query.whereEqualTo("phone", phoneNumber);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if(e != null){
                            ErrDispatcher.errHandler(RegisterActivity.this, ErrDispatcher.ErrCode.NETWORK_NOT_AVAILABLE);
                        }else if(!list.isEmpty()){
                            ErrDispatcher.errHandler(RegisterActivity.this, ErrDispatcher.ErrCode.PHONE_ALREADY_REGISTERED);
                        }else{
                            sendCodeTask.execute();
                        }
                    }
                });
                return null;
            }
        };
        checkDuplicateRegisterTask.execute();
    }

    private void verifyCode(String code){
        AVOSCloud.verifySMSCodeInBackground(code, phoneNumber, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    onCodeVerificationDone();
                }else{
                    ErrDispatcher.errHandler(RegisterActivity.this, ErrDispatcher.ErrCode.CODE_MISMATCH);
                }
            }
        });
    }

}
