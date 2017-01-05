package com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils;


import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.jeylnastoninfer.debug7.R;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.ImgdspHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.DataBaseHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.LocalDataUtil;
import com.example.jeylnastoninfer.debug7.daemonAct.RegisterActivity;

import java.util.List;

public class RuntimeDataManager {

    public static Context mContext;

    private static DataBaseHelper dataBaseHelper = null;

    private static void initDatabase(){
        dataBaseHelper = new DataBaseHelper(mContext, "database");
    }

    public static DataBaseHelper getDataBaseHelper(){
        return dataBaseHelper;
    }


    private static void initLocalStorage(){
        LocalDataUtil.init(mContext);
    }

    public static void initImgLoader(Context context){
        ImgdspHelper.init(context);
    }

    public static void initialize(Context context){
        mContext = context;
        initDatabase();
        initLocalStorage();
    }

}
