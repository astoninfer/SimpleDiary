package com.example.astoninfer.demo3;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class BitmapWorkerTask extends AsyncTask<Object, Void, Void> {
    protected Void doInBackground(Object... objects) {
        MemoryManager.addBitmapToMemoryCache((String)objects[0], (Bitmap)objects[1]);
        return null;
    }
}
