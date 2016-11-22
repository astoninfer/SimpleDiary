package com.example.astoninfer.demo3;

import android.app.Activity;
import android.content.Context;
import android.content.RestrictionEntry;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public final class AuxUtil {
    public static int dip2px(Context context, float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + .5f);
    }

    public static int px2dip(Context context, float px){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(px / scale);
    }

    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(pxValue / scale);
    }

    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(spValue * scale);
    }

    public static int getScreenWidth(Context context){
        Resources res = context.getResources();
        return res.getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context){
        Resources res = context.getResources();
        return res.getDisplayMetrics().heightPixels;
    }

    public static int resizeTextSize(int screenWidth, int screenHeight, int textSize){
        float ratio = 1;
        try{
            float ratioWidth = (float)screenWidth / 480;
            float ratioHeight = (float)screenHeight / 800;
            ratio = Math.min(ratioWidth, ratioHeight);
        }catch (Exception e){

        }
        return Math.round(textSize * ratio);
    }

    public static void printInfo(Context context, String string){
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    /**
     * note: this function is reserved to adjust statusBar color
     *
     * @param activity current activity's reference
     * @param colorId  color index in resources
     */

    public static void setStatusBarColor(Activity activity, int colorId) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity.getBaseContext(), colorId));
    }
    public static void setBackgroundWallpaperStill(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }


    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
     * more memory that there is already allocated.
     *
     * @param imgIn - Source image. It will be released, and should not be used more
     * @return a copy of imgIn, but muttable.
     */
    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }
    public static final boolean checkInternetAvailable(){
        String result = null;
        try{
            String ip = "www.baidu.com";
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while((content = in.readLine()) != null){
                stringBuffer.append(content);
            }
            Log.d("------ping------", "result content : " + stringBuffer.toString());
            int status = p.waitFor();
            if(status == 0){
                result = "success";
                return true;
            }else{
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

}
