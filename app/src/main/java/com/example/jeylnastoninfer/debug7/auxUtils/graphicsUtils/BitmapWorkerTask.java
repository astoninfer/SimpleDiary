package com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.ImgdspHelper;

/**
 * Created by jeylnastoninfer on 2016/12/22.
 */

public class BitmapWorkerTask extends AsyncTask<Object, Void, Void> {
    protected Void doInBackground(Object... objects) {
        ImgdspHelper.addBitmapToMemoryCache((String)objects[0], (Bitmap)objects[1]);
        return null;
    }
}
