package com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils;

import android.content.Context;
import android.widget.Toast;


public final class DebugHelper {
    public static void printInfo(Context context, String string){
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }
}