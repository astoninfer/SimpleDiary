package com.example.jeylnastoninfer.debug7;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.GetDataCallback;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.DrawerHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.ImgdspHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.NumericHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.networkUtils.NetworkHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.DateInfo;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.ImageInfo;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.LocalDataUtil;
import com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils.DebugHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils.RuntimeDataManager;
import com.example.jeylnastoninfer.debug7.daemonAct.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class PhotoAlbumActivity extends SwipeBackActivity{

    private ProgressDialog mProgressDialog;
    private File rootDir;
    private File tarFile;
    private String rootPath;
    private String tarPath;

    private LinearLayout rootLayout = null;

    private LayoutInflater inflater;

    private ImageInfo[] list = null;

    private LinearLayout topMenu = null;



    private void init(){

        topMenu = (LinearLayout)this.findViewById(R.id.photo_album_act_top_menu);

        topMenu.setPadding(0, NumericHelper.getStatusBarHeight(PhotoAlbumActivity.this), 0, 0);


    }


    protected void loadInfo(){

        AsyncTask<Void, Void, Void> loadTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                list = RuntimeDataManager.getDataBaseHelper().getAllImagesDownTime();

                return null;


            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                preDisplayData();
            }
        };

        loadTask.execute();
    }

    private void preDisplayData(){

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                displayData();
            }
        });

    }

    private void displayData(){


        int cnt = list.length;

        DateInfo __date = null;

        int i = 0;

        while(i < cnt) {

            int j = i;

            while (j < cnt && (__date == null || __date.year == list[j].date.year &&
                    __date.month == list[j].date.month)) {

                if (__date == null) {
                    __date = list[j].date;
                }

                j++;
            }

            int __year = __date.year, __month = __date.month;

            View v = View.inflate(PhotoAlbumActivity.this, R.layout.photo_album_header, null);

            ((TextView) v.findViewById(R.id.photo_album_text)).setText(__year + "年"
                    + __month + "月");


            rootLayout = (LinearLayout) this.findViewById(R.id.album_root);

            rootLayout.addView(v);

            while(i < j){


                View nv = View.inflate(PhotoAlbumActivity.this, R.layout.photo_album_row, null);

                int width = NumericHelper.getScreenWidth(PhotoAlbumActivity.this) / 3;

                int height = NumericHelper.dip2px(PhotoAlbumActivity.this, 120);


                if(i < j){

                    String path = LocalDataUtil.getUsrPhotoGalleryPath(MainActivity.usrPhone)
                            + list[i].name;

                    Bitmap bitmap = ImgdspHelper.decodeSampledBitmapFromResource(path, width, height);
                    ((ImageView)nv.findViewById(R.id.album_row_img1)).setImageBitmap(bitmap);

                    i++;
                }

                if(i < j){

                    String path = LocalDataUtil.getUsrPhotoGalleryPath(MainActivity.usrPhone)
                            + list[i].name;

                    Bitmap bitmap = ImgdspHelper.decodeSampledBitmapFromResource(path, width, height);
                    ((ImageView)nv.findViewById(R.id.album_row_img2)).setImageBitmap(bitmap);

                    i++;
                }

                if(i < j){

                    String path = LocalDataUtil.getUsrPhotoGalleryPath(MainActivity.usrPhone)
                            + list[i].name;

                    Bitmap bitmap = ImgdspHelper.decodeSampledBitmapFromResource(path, width, height);
                    ((ImageView)nv.findViewById(R.id.album_row_img3)).setImageBitmap(bitmap);

                    i++;
                }

                rootLayout.addView(nv);

            }



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceBundle){

        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.photo_album_activity);

        init();

        loadInfo();



    }



}
