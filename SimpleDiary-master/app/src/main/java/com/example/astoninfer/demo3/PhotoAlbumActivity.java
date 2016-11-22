package com.example.astoninfer.demo3;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetDataCallback;

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

    private LayoutInflater inflater;

    private void getImages(){
        int w = AuxUtil.dip2px(this, 300);
        Bitmap bmp = MemoryManager.loadBitmap(tarPath + "/test.jpg", w, w, "try");
        ImageView imageView = (ImageView)findViewById(R.id.imgInAlbum);
        imageView.setImageBitmap(bmp);
        try{
            ExifInterface exifInterface = new ExifInterface(tarPath + "/test.jpg");
            String FDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            AuxUtil.printInfo(this, "date is " + FDateTime);
        }catch (IOException e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.photo_album_activity);

        AVOSCloud.initialize(this, "CCArlD3TG7tC435zoOi1m4hO-gzGzoHsz", "hDFQb8gQmVkdDFj7jxoNWa8e");

        rootDir = Environment.getExternalStorageDirectory();
        rootPath = rootDir.getAbsolutePath();
        tarPath = rootPath + "/photoDirTest";
        tarFile = new File(tarPath);

        if(!tarFile.exists()){
            tarFile.mkdir();
            //suppose we won't fail
            download();
        }else{
            Log.i("okay", "already exists");
            Log.i("file path is", tarFile.getPath());
            Log.i("file isDirectory", tarFile.isDirectory() ? "true" : "false");
            try {
                FileInputStream fis = new FileInputStream(tarPath + "/test.jpg");
                Log.i("file size is ", fis.available() + "");
            }catch (FileNotFoundException e){

            }catch (IOException E){

            }
            AuxUtil.printInfo(this, "already exists");
            getImages();
        }
    }

    private void download(){

        if(AuxUtil.checkInternetAvailable()){
            AVFile temFile = new AVFile(
                    "test.jpg",
                    "http://ac-ccarld3t.clouddn.com/2d18f82e21095a5d032e.JPG",
                    new HashMap<String, Object>()
            );

            temFile.getDataInBackground(new GetDataCallback(){
                @Override
                public void done(byte[] bytes, AVException e) {
                    AuxUtil.printInfo(PhotoAlbumActivity.this, "DONE!");
                    try{
                        File dest = new File(tarPath + "/test.jpg");
                        if(!dest.exists()){
                            dest.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(tarPath + "/test.jpg");
                        fos.write(bytes);
                        fos.close();
                        AuxUtil.printInfo(PhotoAlbumActivity.this, "write okay");
                    }catch (IOException ioe){
                        //TODO: handle exception
                        AuxUtil.printInfo(PhotoAlbumActivity.this, "IOException here");
                    }
                    getImages();
                }
            });
        }else{
            AuxUtil.printInfo(this, "check network later");
        }

    }

}
