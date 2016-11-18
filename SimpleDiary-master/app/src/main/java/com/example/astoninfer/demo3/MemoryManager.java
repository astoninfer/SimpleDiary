package com.example.astoninfer.demo3;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import java.io.ObjectStreamException;

public class MemoryManager {
    private static LruCache<String, Bitmap> mMemoryCache;
    private static int maxMemory, cacheSize;
    private static Context context;
    private static float maxWidth, maxHeight;
    public static void init(Context mContext){
        maxMemory = (int)(Runtime.getRuntime().maxMemory());
        cacheSize = maxMemory / 6;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            protected int sizeOf(String key, Bitmap bitmap){
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
        context = mContext;
        maxWidth = AuxUtil.getScreenWidth(context);
        maxHeight = AuxUtil.getScreenHeight(context);
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if(getBitmapFromMemCache(key) == null){
            mMemoryCache.put(key, bitmap);
        }
    }

    public static LruCache<String, Bitmap> getCache(){
        return mMemoryCache;
    }
    private static Bitmap getBitmapFromMemCache(String key){
        return mMemoryCache.get(key);
    }

    public static Bitmap loadBitmap(String pathName, int reqWidth, int reqHeight, String append){

        String key = pathName + append;
        Bitmap bitmap = getBitmapFromMemCache(key);
        if(bitmap != null && !bitmap.isRecycled()) return bitmap;
        else{
            Bitmap buf = decodeSampledBitmapFromResource(pathName, reqWidth, reqHeight);
            Object obj[] = new Object[]{key, buf};
            new BitmapWorkerTask().execute(obj);
            return buf;
        }
    }

    public static Bitmap loadBitmap(int resId, int frameWidth, int frameHeight, int compressLevel){
        String padding = compressLevel == 0 ? "free" :
                (String.valueOf(compressLevel) + "xcompress");
        String imageKey = String.valueOf(resId) + padding;
        Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if(bitmap != null && !bitmap.isRecycled()) return bitmap;
        else{
            Bitmap buf = compressLevel != 0 ? decodeSampledBitmapFromResource(
                    context.getResources(), resId, frameWidth, frameHeight) :
                    BitmapFactory.decodeResource(context.getResources(), resId);
            Object object[] = new Object[]{imageKey, buf};
            new BitmapWorkerTask().execute(object);
            return buf;
        }
    }


    private static int calInSampleSize(BitmapFactory.Options options, float scale){
        int ratio = AuxUtil.dip2px(context, 1);
        int width = options.outWidth;
        int height = options.outHeight;

        Log.i("origin", width + " " + height);
        int inSampleSize = (int)scale;
        int sampleSizeBit = 0;
        while(((inSampleSize >> (sampleSizeBit + 1)) & 1) != 0){
            sampleSizeBit++;
        }
        float S0 = maxWidth * maxHeight;
        float S1 = (float)width * height / (1 << (sampleSizeBit << 1));
        while(S1 > S0){
            sampleSizeBit++;
            S1 = (float)width * height / (1 << (sampleSizeBit << 1));
        }
        Log.d("returning inSampleSize", "" + Math.round(1 << sampleSizeBit));
        return 1 << sampleSizeBit;
    }


    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inJustDecodeBounds = false;
        float ratio = options.outWidth / (float)options.outHeight;
        float scaleX = reqWidth / (float)options.outWidth;
        float scaleY = reqHeight / (float)options.outHeight;
        float scale = Math.min(Math.max(scaleX, scaleY),
                (float)Math.sqrt(maxWidth * maxHeight / options.outWidth / options.outHeight));
        options.inSampleSize = calInSampleSize(options, scale);
        options.inDither = false;
        options.inPreferredConfig = null;
        reqWidth = (int)(options.outWidth * scale);
        reqHeight = (int)(options.outHeight * scale);

        Bitmap bmp = BitmapFactory.decodeFile(pathName, options);
        //Log.i("inSampleSize", options.inSampleSize + "");
//        Log.i("sizeofbmp", bmp.getWidth() + " " + bmp.getHeight());
        Bitmap bitmap = Bitmap.createScaledBitmap(bmp, reqWidth, reqHeight, false);
  //      Log.i("sieofbitmap", bitmap.getWidth() + " " + bitmap.getHeight());

        if(bmp != bitmap && bmp != null && !bmp.isRecycled()){
            bmp.recycle();
        }

        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inJustDecodeBounds = false;
        float ratio = options.outWidth / (float)options.outHeight;
        float scaleX = reqWidth / (float)options.outWidth;
        float scaleY = reqHeight / (float)options.outHeight;
        float scale = Math.min(Math.max(scaleX, scaleY),
                (float)Math.sqrt(maxWidth * maxHeight / options.outWidth / options.outHeight));
        options.inSampleSize = calInSampleSize(options, scale);
        options.inDither = false;
        options.inPreferredConfig = null;
        reqWidth = (int)(options.outWidth * scale);
        reqHeight = (int)(options.outHeight * scale);

        Bitmap bmp = BitmapFactory.decodeResource(res, resId, options);
        //Log.i("inSampleSize", options.inSampleSize + "");
        Log.i("sizeofbmp", bmp.getWidth() + " " + bmp.getHeight());
        Bitmap bitmap = Bitmap.createScaledBitmap(bmp, reqWidth, reqHeight, false);
        Log.i("sieofbitmap", bitmap.getWidth() + " " + bitmap.getHeight());

        if(bmp != bitmap && bmp != null && !bmp.isRecycled()){
            bmp.recycle();
        }

        return bitmap;
    }

}
