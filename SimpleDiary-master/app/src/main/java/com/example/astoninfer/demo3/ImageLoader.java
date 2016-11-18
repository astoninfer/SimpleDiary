package com.example.astoninfer.demo3;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;


import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.Semaphore;


public class ImageLoader {

    private Thread mPoolThread;
    private ExecutorService mThreadPool;
    private int mThreadCount = 1;
    private Type mType = Type.LIFO;
    private LinkedList<Runnable> mTasks;
    private Handler mPoolThreadHandler;
    private Handler mHandler;
    private volatile Semaphore mSemaphore = new Semaphore(1);
    private volatile Semaphore mPoolSemaphore;
    private static ImageLoader mInstance;
    private static Context mContext;

    public enum Type{
        FIFO, LIFO
    }

    public static void init(Context context){
        mContext = context;
    }

    public static ImageLoader getInstance(){
        if(mInstance == null){
            synchronized (ImageLoader.class){
                if(mInstance == null){
                    mInstance = new ImageLoader(1, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    public static ImageLoader getInstance(int threadCount, Type type){
        if(mInstance == null){
            synchronized (ImageLoader.class){
                if(mInstance == null){
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    private LruCache<String, Bitmap> mCache;

    private ImageLoader(int threadCount, Type type){
        init(threadCount, type);
    }

    private void init(int threadCount, Type type){
        //loop thread
        mPoolThread = new Thread(){
            @Override
            public void run(){
                try{
                    mSemaphore.acquire();
                }catch (InterruptedException e){

                }
                Looper.prepare();

                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg){

                        mThreadPool.execute(getTask());
                        try{
                            mPoolSemaphore.acquire();
                        }catch (InterruptedException e){

                        }
                    }
                };

                mSemaphore.release();
                Looper.loop();
            }
        };

        mPoolThread.start();
        mCache = MemoryManager.getCache();

        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<Runnable>();
        mType = type == null ? Type.LIFO : type;
    }


    public void loadImage(final ImageView imageView, final String imageTag, final int imageResId){

        imageView.setTag(imageTag);

        if(mHandler == null){

            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg){

                    ImgBeanHolder holder = (ImgBeanHolder)msg.obj;
                    ImageView imageView = holder.imageView;
                    Bitmap bm = holder.bitmap;
                    if(imageView.getTag().equals(holder.tag)){
                        imageView.setImageBitmap(bm);
                    }
                }
            };
        }

        Bitmap bm = getBitmapFromLruCache(imageTag);
        if(bm != null){
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.bitmap = bm;
            holder.imageView = imageView;
            holder.tag = imageTag;
            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        }else{
            addTask(new Runnable(){
                @Override
                public void run(){
                    ImageSize imageSize = getImageViewWidth(imageView);
                    int reqWidth = imageSize.width;
                    int reqHeight = imageSize.height;

                    Bitmap bm = MemoryManager.decodeSampledBitmapFromResource(
                            mContext.getResources(), imageResId, reqWidth, reqHeight);

                    addBitmapToLruCache(imageTag, bm);
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.bitmap = bm;
                    holder.imageView = imageView;
                    holder.tag = imageTag;

                    Message message = Message.obtain();
                    message.obj = holder;

                    mHandler.sendMessage(message);
                    mPoolSemaphore.release();
                }
            });
        }

    }

    private synchronized void addTask(Runnable runnable){
        try{
            if(mPoolThreadHandler == null){
                mSemaphore.acquire();
            }
        }catch (InterruptedException e){

        }

        mTasks.add(runnable);
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private synchronized Runnable getTask(){
        if(mType == Type.FIFO){
            return mTasks.removeFirst();
        }else if(mType == Type.LIFO){
            return mTasks.removeLast();
        }

        return null;
    }

    private ImageSize getImageViewWidth(ImageView imageView){
        ImageSize imageSize = new ImageSize();
        imageSize.width = imageView.getLayoutParams().width;
        imageSize.height = imageView.getLayoutParams().height;
        return imageSize;
    }

    private Bitmap getBitmapFromLruCache(String key){
        return mCache.get(key);
    }

    private void addBitmapToLruCache(String key, Bitmap bitmap){
        if(getBitmapFromLruCache(key) == null){
            if(bitmap != null){
                mCache.put(key, bitmap);
            }
        }
    }

    private class ImgBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String tag;
    }

    private class ImageSize{
        int width;
        int height;
    }

}
