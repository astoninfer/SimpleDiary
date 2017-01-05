package com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

public class ImgdspHelper {

    private static Context context;
    private static LruCache<String, Bitmap> mMemoryCache = null;
    private static int maxByteMemory = 0;
    private static int maxByteCacheSize = 0;
    private static float maxImgWidth = 0f;
    private static float maxImgHeight = 0f;


    public static void init(Context mContext){

        maxByteMemory = (int)(Runtime.getRuntime().maxMemory());

        maxByteCacheSize= maxByteMemory / 6;

        mMemoryCache = new LruCache<String, Bitmap>(maxByteCacheSize){

            protected int sizeOf(String key, Bitmap bitmap){
                return bitmap.getRowBytes() * bitmap.getHeight();
            }

        };

        context = mContext;

        maxImgWidth = NumericHelper.getScreenWidth(context);

        maxImgHeight = NumericHelper.getScreenHeight(context);

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

        String mKey = pathName + "$$#$$##&" + append;

        Bitmap bitmap = getBitmapFromMemCache(mKey);

        if(bitmap != null && !bitmap.isRecycled()) return bitmap;

        else{

            Bitmap buf = decodeSampledBitmapFromResource(pathName, reqWidth, reqHeight);

            Object obj[] = new Object[]{mKey, buf};

            new BitmapWorkerTask().execute(obj);

            return buf;

        }
    }

    public static Bitmap loadBitmap(int srcId, int reqWidth, int reqHeight, String append){

        String mKey = srcId + "$$#$$##&" + append;

        Bitmap bitmap = getBitmapFromMemCache(mKey);
        Log.i("#debug", "prepare little map" + reqWidth + " " + reqHeight);

        Log.i("#debug", "is bitmap null ? " + (bitmap == null ? "yes" : "no"));

        if(bitmap != null && !bitmap.isRecycled()) return bitmap;

        else{

            Bitmap buf = decodeSampledBitmapFromResource(srcId, reqWidth, reqHeight);

            Object obj[] = new Object[]{mKey, buf};

            new BitmapWorkerTask().execute(obj);

            return buf;

        }
    }

    private static int calInSampleSize(BitmapFactory.Options options, float scale){

        int ratio = NumericHelper.dip2px(context, 1);

        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = Math.round(scale);
        int sampleSizeBit = 0;
        while(((inSampleSize >> (sampleSizeBit + 1)) & 1) != 0){
            sampleSizeBit++;
        }
        float S0 = maxImgHeight * maxImgWidth;
        float S1 = (float)width * height / (1 << (sampleSizeBit << 1));
        while(S1 > S0){
            sampleSizeBit++;
            S1 = (float)width * height / (1 << (sampleSizeBit << 1));
        }
        return 1 << sampleSizeBit;
    }


    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        //decode by file name
        options.inJustDecodeBounds = false;
        int w = options.outWidth, h = options.outHeight;
        float scaleX = Math.max(1, (float)w / reqWidth);
        float scaleY = Math.max(1, (float)h / reqHeight);

        float scale = Math.max(Math.max(scaleX, scaleY),
                (float)Math.sqrt(w * h / maxImgWidth / maxImgHeight));

        options.inSampleSize = calInSampleSize(options, scale);
        options.inDither = false;
        options.inPreferredConfig = null;
        Bitmap bmp = BitmapFactory.decodeFile(pathName, options);

        Bitmap bitmap = Bitmap.createScaledBitmap(bmp, (int)(w / scale), (int)(h / scale), false);
        if(bmp != bitmap && bmp != null && !bmp.isRecycled()){
            bmp.recycle();
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(int srcId, int reqWidth, int reqHeight){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), srcId, options);
        options.inJustDecodeBounds = false;
        int w = options.outWidth, h = options.outHeight;
        float scaleX = Math.max(1, (float)w / reqWidth);
        float scaleY = Math.max(1, (float)h / reqHeight);

        float scale = Math.max(Math.max(scaleX, scaleY),
                (float)Math.sqrt(w * h / maxImgWidth / maxImgHeight));

        options.inSampleSize = calInSampleSize(options, scale);
        options.inDither = false;
        options.inPreferredConfig = null;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), srcId, options);
        Bitmap bitmap = Bitmap.createScaledBitmap(bmp, (int)(w / scale), (int)(h / scale), false);
        if(bmp != bitmap && bmp != null && !bmp.isRecycled()){
            bmp.recycle();
        }
        return bitmap;
    }

}
