package com.example.jeylnastoninfer.debug7.daemonAct;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.avos.avoscloud.AVOSCloud;
import com.example.jeylnastoninfer.debug7.ImageLoader;
import com.example.jeylnastoninfer.debug7.R;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.DrawerHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.ImgdspHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.NumericHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils.RuntimeDataManager;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  note: this class is for welcome animation of this application
 *  note: entrance for this app, call main
 */

public class WelcomeActivity extends AppCompatActivity{

    private final int REQUEST_PERMISSIONS = 3;

    private static String APP_ID = null, APP_KEY = null;

    private final Lock lock = new ReentrantLock();

    private static WelcomeActivity activityInstance = null;

    private final static Handler nagivateBarHandler = new Handler();

    private final String[] PERMISSIONS = {

            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    private ImageView welcomeImgView = null;

    private final long animationDuration = 2300;

    Bitmap bg;

    /**
     *  note: global initialization for MemManager and
     *  AuxUtil is placed here
     */

    private void init(){
        ImgdspHelper.init(WelcomeActivity.this.getBaseContext());
    }

    /**
     *  note: deprive status bar and title here
     *  note: background image will be displayed only
     */

    private void setWindows(){

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    /**
     *  note: this part is for animation attributes set
     */

    private void setAnimation(){

        welcomeImgView = (ImageView)findViewById(R.id.welcomeImgView);

        AlphaAnimation animation = new AlphaAnimation(1.0f, 1.0f);

        animation.setDuration(animationDuration);

        animation.setAnimationListener(new AnimationImpl());

        welcomeImgView.startAnimation(animation);
    }

    private static void initAVOS(){
        APP_ID = activityInstance.getResources().getString(R.string.app_id);
        APP_KEY = activityInstance.getResources().getString(R.string.app_key);
        AVOSCloud.initialize(activityInstance, APP_ID, APP_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        activityInstance = WelcomeActivity.this;

        setWindows();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_activity);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                lock.lock();
                RuntimeDataManager.initialize(WelcomeActivity.this);
                ImageLoader.init(WelcomeActivity.this);
                init();
                lock.unlock();
                return null;
            }
        }.execute();
        RuntimeDataManager.initImgLoader(WelcomeActivity.this);
        setAnimation();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int res : grantResults){
            if(res == PackageManager.PERMISSION_DENIED){
                Log.i("#debug", "you denied");
                finish();
            }
        }
        postAnimation();
    }

    private class AnimationImpl implements Animation.AnimationListener{

        /**
         * note: appoint image resource
         * @param animation
         *
         */

        @Override
        public void onAnimationStart(Animation animation){
            bg = ImgdspHelper.loadBitmap(R.drawable.welcome,
                    NumericHelper.getScreenWidth(WelcomeActivity.this),
                    NumericHelper.getScreenHeight(WelcomeActivity.this), "1");
            welcomeImgView.setBackground(new BitmapDrawable(bg));
        }

        @Override
        public void onAnimationEnd(Animation animation){
            boolean hasPermission = true;
            for(String permissionItem : PERMISSIONS){
                hasPermission &= PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(WelcomeActivity.this,
                                permissionItem);
            }
            if (!hasPermission) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        WelcomeActivity.this,
                        PERMISSIONS,
                        REQUEST_PERMISSIONS
                );
            }else postAnimation();
        }

        @Override
        public void onAnimationRepeat(Animation animation){

        }
    }

    /**
     *  note: jump to MainActivity
     */

    private void postAnimation(){

        initAVOS();

        lock.lock();
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        //testSkip();
        if(bg != null && !bg.isRecycled()){
            bg.recycle();
            bg = null;
        }
        finish();

    }

    private void testSkip(){
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("usrPhone", "13020082679");
        intent.putExtra("usrInfo", bundle);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }
}
