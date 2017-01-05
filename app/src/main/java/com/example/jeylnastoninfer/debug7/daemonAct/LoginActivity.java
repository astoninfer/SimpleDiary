package com.example.jeylnastoninfer.debug7.daemonAct;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.NativeActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.example.jeylnastoninfer.debug7.R;
import com.example.jeylnastoninfer.debug7.auxUtils.errDispatcherUtils.ErrDispatcher;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.DrawerHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.NumericHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.networkUtils.NetworkHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.DataSynchronizeHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.LocalDataUtil;

import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class LoginActivity extends AppCompatActivity{

    private EditText phoneNumberLayout = null;

    private EditText passwordLayout = null;

    private Button registerBtn = null;

    private Button submitBtn = null;

    private String phoneNumber = null;

    private String password = null;

    public static int textInfoOriginColor = 0;

    private final int phoneNumberLen = 11;

    private boolean subBtnClickable = true;

    private ProgressDialog dialog = null;

    private boolean checkPhoneNumber(){
        if(phoneNumberLayout.getText().length() != phoneNumberLen){
            ErrDispatcher.errHandler(LoginActivity.this, ErrDispatcher.ErrCode.PHONE_NUMBER_LEN_ILLEGAL);
            return false;
        }
        phoneNumber = phoneNumberLayout.getText().toString();
        if(!NetworkHelper.isPhoneNumber(phoneNumber)){
            ErrDispatcher.errHandler(LoginActivity.this, ErrDispatcher.ErrCode.NOT_A_PHONE_NUMBER);
            return false;
        }
        return true;
    }

    void init(){


        phoneNumberLayout = (EditText)this.findViewById(R.id.act_login_phonenumber_fill_ly);

        final TextView phoneInfo = (TextView)this.findViewById(R.id.act_login_phonenumber_info_ly);

        textInfoOriginColor = phoneInfo.getTextColors().getDefaultColor();

        phoneNumberLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    phoneInfo.setTextColor(getResources().getColor(R.color.colorPrimary));
                    phoneNumberLayout.setTypeface(null, Typeface.ITALIC);
                    phoneNumberLayout.setTextColor(getResources().getColor(R.color.pure_black));
                }else{
                    phoneInfo.setTextColor(textInfoOriginColor);
                    phoneNumberLayout.setTextColor(textInfoOriginColor);
                    phoneNumberLayout.setTypeface(null, Typeface.NORMAL);
                }
            }
        });
        passwordLayout = (EditText)this.findViewById(R.id.act_login_pwd_fill_ly);

        final TextView passwdInfo = (TextView)this.findViewById(R.id.act_login_pwd_info_ly);

        passwordLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    passwdInfo.setTextColor(getResources().getColor(R.color.colorPrimary));
                    passwordLayout.setTypeface(null, Typeface.ITALIC);
                    passwordLayout.setTextColor(getResources().getColor(R.color.pure_black));
                }else{
                    passwdInfo.setTextColor(textInfoOriginColor);
                    passwordLayout.setTypeface(null, Typeface.NORMAL);
                    passwordLayout.setTextColor(textInfoOriginColor);
                }
            }
        });

        submitBtn = (Button)this.findViewById(R.id.act_login_login_ly);

        registerBtn = (Button)this.findViewById(R.id.act_login_register_ly);

        View firstView = this.findViewById(R.id.act_login_topmenu_ly);

        firstView.setPadding(0, NumericHelper.getStatusBarHeight(LoginActivity.this), 0, 0);


        submitBtn.setTextColor(textInfoOriginColor);
        submitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    submitBtn.setTextColor(getResources().getColor(R.color.pure_white));
                    submitBtn.setBackground(getDrawable(R.drawable.round_corner_btn_2));
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

                Log.i("#debug", "is clock ? " + (subBtnClickable ? "true": "false"));

                if(!subBtnClickable){
                    return;
                }else{
                    subBtnClickable = false;
                }
                submitBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        subBtnClickable = true;
                    }
                }, 1000);

                if(!checkPhoneNumber()){
                    return;
                }

                password = passwordLayout.getText().toString();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        AVQuery<AVObject> usr = new AVQuery<AVObject>("usrInfo");
                        usr.whereEqualTo("phone", phoneNumber);
                        usr.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if(e != null){
                                    e.printStackTrace();
                                    Log.i("debug", "exception in phone query");
                                    ErrDispatcher.errHandler(LoginActivity.this,
                                            ErrDispatcher.ErrCode.NETWORK_FAILED);
                                }else if(list.isEmpty()){
                                    ErrDispatcher.errHandler(LoginActivity.this,
                                            ErrDispatcher.ErrCode.PHONE_NOT_REGISTERED);
                                }else{
                                    Log.i("debug", "next: on confirmed");
                                    String __pwd = (String)list.get(0).get("pwd");
                                    if(!__pwd.equals(password)){
                                        ErrDispatcher.errHandler(LoginActivity.this,
                                                ErrDispatcher.ErrCode.PWD_MISMATCH);
                                    }else{
                                        onConfirmed();
                                    }
                                }
                            }
                        });
                        return null;
                    }
                }.execute();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("usrPhone", phoneNumber);
                intent.putExtra("usrInfo", bundle);
                startActivity(intent);
                //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
                //overridePendingTransition(R.anim.act_swt_slide, 0);
                //finish();
            }
        });

    }

    public static void onDataSynchronized(LoginActivity context, boolean okay){
        if(!okay){
            context.dialog.setMessage("数据更新失败，请检查后再试");
            context.dialog.dismiss();
        }else{
            context.dialog.setMessage("数据更新成功");
            context.dialog.dismiss();
            Intent intent = new Intent(context, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("usrPhone", context.phoneNumber);
            intent.putExtra("usrInfo", bundle);
            context.startActivity(intent);
            context.finish();
        }

    }



    private void onConfirmed(){

        dialog = new ProgressDialog(LoginActivity.this);

        dialog.setMax(100);

        dialog.setTitle("数据同步检查...");

        dialog.setIcon(R.drawable.ic_cloud_download_black_24dp);

        dialog.setMessage("更新进度");

        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        dialog.setCancelable(false);

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();


        DataSynchronizeHelper.fetchDataIfNeeded(LoginActivity.this, phoneNumber);

    }

    public void increaseDialogBy(int amount){
        dialog.incrementProgressBy(amount);
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        init();
    }

}
