package com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by jeylnastoninfer on 2016/12/22.
 */

public class DrawerHelper {
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


    public static void hideNavigationBar(final Activity context){
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public static void enterImmersiveMode(final Handler handler, final Activity context) {

        final Runnable mHideRunnable = new Runnable() {
            @Override
            public void run() {
                int flags;
                int curApiVersion = android.os.Build.VERSION.SDK_INT;
                // This work only for android 4.4+
                if (curApiVersion >= Build.VERSION_CODES.KITKAT) {
                    // This work only for android 4.4+
                    // hide navigation bar permanently in android activity
                    // touch the screen, the navigation bar will not show
                    flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN;

                } else {
                    // touch the screen, the navigation bar will show
                    flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                }

                // must be executed in main thread :)
                context.getWindow().getDecorView().setSystemUiVisibility(flags);
            }

        };

        handler.post(mHideRunnable);
        context.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int i) {
                        handler.post(mHideRunnable);
                    }
                }
        );
    }

}
