package com.example.astoninfer.demo3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 *  note: this class is for welcome animation of this application
 *  note: entrance for this app, call main
 */

public class WelcomeActivity extends AppCompatActivity{

    private ImageView welcomeImg = null;
    private final long animationDuration = 2300;
    Bitmap bg;
    private LayoutInflater inflater;

    /**
     *  note: global initialization for MemManager and
     *  AuxUtil is placed here
     */

    private void init(){
        MemoryManager.init(this.getBaseContext());
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

        welcomeImg = (ImageView)findViewById(R.id.welcomeImgView);
        AlphaAnimation animation = new AlphaAnimation(1.0f, 1.0f);
        animation.setDuration(animationDuration);
        animation.setAnimationListener(new AnimationImpl());
        welcomeImg.startAnimation(animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setWindows();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        init();
        setAnimation();
    }

    private class AnimationImpl implements Animation.AnimationListener{

        /**
         * note: appoint image resource
         * @param animation
         *
         */

        @Override
        public void onAnimationStart(Animation animation){
            bg = MemoryManager.loadBitmap(R.drawable.welcome,
                    AuxUtil.getScreenWidth(WelcomeActivity.this), AuxUtil.getScreenHeight(WelcomeActivity.this), 1);
            welcomeImg.setBackground(new BitmapDrawable(bg));
        }

        @Override
        public void onAnimationEnd(Animation animation){
            skip();
        }

        @Override
        public void onAnimationRepeat(Animation animation){

        }
    }

    /**
     *  note: jump to MainActivity
     */

    private void skip(){
        startActivity(new Intent(this, MainActivity.class));
        if(bg != null && !bg.isRecycled()){
            bg.recycle();
            bg = null;
        }
        finish();
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
