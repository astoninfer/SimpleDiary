package com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by jeylnastoninfer on 2016/12/22.
 */

public final class NumericHelper {
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

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
