package com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.okhttp.Dispatcher;
import com.example.jeylnastoninfer.debug7.LocationServiceUtils.LocationServer;
import com.example.jeylnastoninfer.debug7.auxUtils.errDispatcherUtils.ErrDispatcher;
import com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils.RuntimeDataManager;
import com.example.jeylnastoninfer.debug7.daemonAct.EditorActivity;
import com.example.jeylnastoninfer.debug7.daemonAct.LoginActivity;
import com.example.jeylnastoninfer.debug7.daemonAct.MainActivity;
import com.example.jeylnastoninfer.debug7.daemonAct.RegisterActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataSynchronizeHelper {

    public static String lastLocation = null;

    private static String localPhoneNumber = null;

    private static String recordUrl = null;

    private static String photoUrl = null;

    private final static String USRS_TABLE_NAME = "usrInfo";
    private final static String USRS_FIELD_PHONE = "phone";
    private final static String USRS_FIELD_NICKNAME = "nickname";
    private final static String USRS_FIELD_PASSWORD = "pwd";

    private final static String USRS_FIELD_REGISTER_DAY = "day";

    private final static String USRS_FIELD_REGISTER_MONTH = "month";

    private final static String USRS_FIELD_REGISTER_YEAR = "year";

    private static int cnt = 0, cur = 0;

    public static void addUsr(final Context context, UsrInfo newUsr){
        AVObject usr = new AVObject(USRS_TABLE_NAME);

        usr.put(USRS_FIELD_PHONE, newUsr.phone);

        usr.put(USRS_FIELD_NICKNAME, newUsr.nickname);

        usr.put(USRS_FIELD_PASSWORD, newUsr.pwd);

        usr.put(USRS_FIELD_REGISTER_YEAR, newUsr.registerDate.year);

        usr.put(USRS_FIELD_REGISTER_MONTH, newUsr.registerDate.month);

        usr.put(USRS_FIELD_REGISTER_DAY, newUsr.registerDate.day);

        usr.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Log.i("#debug", "on register deone");
                    //register done
                    ((RegisterActivity)context).onRegisterDone();

                }else{

                    ErrDispatcher.errHandler(context, ErrDispatcher.ErrCode.NETWORK_FAILED);

                }
            }
        });
    }

    public static void fetchDataIfNeeded(final LoginActivity context, String phone){
        Log.i("#debug", "next: check is usr exists");
        localPhoneNumber = phone;
        boolean existence = LocalDataUtil.checkIfUserExists(phone);
        if(!existence){
            LocalDataUtil.createLocalDirs(phone);
            Log.i("#debug", "prepare to fetch data online");
            //get all data from AVOSCloud, UI thread will be stumped here
            //until data fetch done
            AVQuery<AVObject> query = new AVQuery<>("usrInfo");
            query.whereEqualTo("phone", phone);
            Log.i("#debug", "next: query usr Info where phone is specified");
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if(e != null){
                        Log.i("#debug", "this: error not null in query usr info with phone");
                        LoginActivity.onDataSynchronized(context, false);
                    }else{
                        context.increaseDialogBy(10);
                        Log.i("#debug", "next: syn usr data");
                        synchronizeUsrData(context, list.get(0));
                    }
                }
            });
        }else{
            Log.i("#debug", "already exists");
            context.increaseDialogBy(100);
            LoginActivity.onDataSynchronized(context, true);
        }
    }

    private static void synchronizeUsrImgTable(final LoginActivity context, final AVObject usr){
        context.increaseDialogBy(10);
        //80% done
        Log.i("#debug", "at syn ust img table");
        AVQuery<AVObject> query = new AVQuery<>("imgTable");
        query.whereEqualTo("phone", usr.get("phone").toString());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e != null){
                    LoginActivity.onDataSynchronized(context, false);
                }else if(!list.isEmpty()){
                    final String phone = usr.get("phone").toString();

                    for(AVObject imgItem : list){

                        final String name = imgItem.get(ImageInfo.NAME).toString();

                        final int date_year = (int)imgItem.get(ImageInfo.YEAR);

                        final int date_month = (int)imgItem.get(ImageInfo.MONTH);

                        final int date_day = (int)imgItem.get(ImageInfo.DAY);

                        final DateInfo date = new DateInfo(date_year, date_month, date_day);

                        final String location = imgItem.get(ImageInfo.LOCATION).toString();



                        ImageInfo item = new ImageInfo(phone, name, date, location);

                        LocalDataUtil.writeImgItemToLocalDB(item);

                    }

                    synchronizeUsrRcdTable(context, usr);
                }else{
                    Log.i("debug", "next: syn ysr record table");
                    synchronizeUsrRcdTable(context, usr);
                }
            }
        });
    }

    private static void synchronizeUsrRcdTable(final LoginActivity context, final AVObject usr){
        context.increaseDialogBy(10);
        //90% done
        AVQuery<AVObject> query = new AVQuery<>("recordTable");
        query.whereEqualTo("phone", usr.get("phone").toString());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e != null){
                    LoginActivity.onDataSynchronized(context, false);
                }else if(!list.isEmpty()){
                    final String phone = usr.get("phone").toString();
                    Log.i("#debug", "rec nt is " + list.size());
                    for(AVObject recordItem : list){

                        final String name = recordItem.get(RecordItem.NAME).toString();

                        final int date_year = (int)recordItem.get(RecordItem.YEAR);

                        final int date_month = (int)recordItem.get(RecordItem.MONTH);

                        final int date_day = (int)recordItem.get(RecordItem.DAY);

                        final DateInfo date = new DateInfo(date_year, date_month, date_day);

                        final String location = recordItem.get(RecordItem.LOCATION).toString();

                        RecordItem item = new RecordItem(phone, name, date, location);

                        LocalDataUtil.writeRecordItemToLocalDB(item);

                    }
                    context.increaseDialogBy(10);
                    LoginActivity.onDataSynchronized(context, true);
                }else{

                    Log.i("#debug", "next: on syn done");
                    context.increaseDialogBy(10);
                    LoginActivity.onDataSynchronized(context, true);
                }
            }
        });
    }

    private static void synchronizeUsrImgRecordTable(final LoginActivity context, final AVObject usr){
        context.increaseDialogBy(30);
        //70% done
        AVQuery<AVObject> query = new AVQuery<>("relateTable");
        Log.i("#debug", "next: query urs relateTable");
        query.whereEqualTo("phone", usr.get("phone").toString());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e != null){
                    Log.i("#debug", "error not null in relateTable query");
                    LoginActivity.onDataSynchronized(context, false);
                }else if(!list.isEmpty()){

                    final String phone = usr.get("phone").toString();
                    for(AVObject relateItem : list){
                        final String recordName = relateItem.get("recordName").toString();
                        final String imageName = relateItem.get("imageName").toString();
                        ImageRecordTableItem item = new ImageRecordTableItem(phone, recordName,
                                imageName);
                        LocalDataUtil.writeRelateRecordToLocalDB(item);
                    }
                    synchronizeUsrImgTable(context, usr);
                }else{
                    Log.i("#debug", "next: syn img table");
                    synchronizeUsrImgTable(context, usr);
                }
            }
        });
    }

    private static void synchronizeUsrRecords(final LoginActivity context, final AVObject usr){
        context.increaseDialogBy(30);
        //40% done
        AVQuery<AVObject> query = new AVQuery<>("recordTable");
        query.whereEqualTo("phone", usr.get("phone").toString());
        Log.i("#debug", "next: query usr records");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e != null) {
                    Log.i("#debug", "error not null in records query");
                    LoginActivity.onDataSynchronized(context, false);
                }else if(!list.isEmpty()){
                    cnt = list.size();
                    cur = 0;

                    for(AVObject recordInfo : list){
                        final String recordFileName = localPhoneNumber + "_" +
                                recordInfo.get("name").toString();
                        final String recordName = recordInfo.get("name").toString();
                        Log.i("#debug", "record name: " + recordName);
                        recordUrl = "";
                        Log.i("#debug", "s1");
                        AVQuery<AVObject> query = new AVQuery<AVObject>("_File");
                        Log.i("#debug", "s2");
                        query.whereEqualTo("name", recordFileName);
                        Log.i("#debug", "s3");
                        Log.i("#debug", "s7");
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if(e != null){
                                    e.printStackTrace();
                                    LoginActivity.onDataSynchronized(context, false);
                                }else{
                                    recordUrl = (list.get(0)).get("url").toString();
                                    Log.i("#debug", "record Url act: " + recordUrl);
                                }
                                onRecordUrlGot(recordName, context, usr);
                            }
                        });
                        Log.i("#debug", "s4");
                        Log.i("#debug", "record url: " + recordUrl);

                    }

                }else{
                    Log.i("#debug", "next: syn usr img-record table");
                    synchronizeUsrImgRecordTable(context, usr);
                }
            }
        });
    }

    private static void onRecordUrlGot(final String recordName, final LoginActivity context, final AVObject usr){
        AVFile recordFile = new AVFile(recordName, recordUrl, null);

        recordFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                if(e != null){
                    e.printStackTrace();
                    Log.i("#debug", "error happens");
                    LoginActivity.onDataSynchronized(context, false);
                }else{
                    ++cur;
                    String phone = usr.get("phone").toString();
                    String str = new String(bytes);
                    Log.i("#debug", "content \n" + str);
                    LocalDataUtil.writeUsrRcd(bytes, phone, recordName);
                    if(cur == cnt){
                        synchronizeUsrImgRecordTable(context, usr);
                    }
                }
            }
        });
    }

    public static void
    uploadUsrRecordDateAndWriteLocally(final EditorActivity editorActivity,
                                       final String phone, final String recordContent,
                                       ArrayList<String> imgPathList) {

        Long curTimer = System.currentTimeMillis();

        Calendar now = Calendar.getInstance();

        DateInfo dateInfo = new DateInfo(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1,
                now.get(Calendar.DAY_OF_MONTH));


        String fileName = curTimer + ".txt";


        //write item 1 : local record
        LocalDataUtil.writeUsrRcd(recordContent.getBytes(), phone, fileName);

        //write item2 : cloud record
        AVFile recordFile = new AVFile(phone + "_" + fileName, recordContent.getBytes());

        recordFile.saveInBackground();

        RecordFileInfo recordFileInfo = new RecordFileInfo();

        recordFileInfo.name = fileName;

        recordFileInfo.date = new DateInfo(dateInfo.year, dateInfo.month, dateInfo.day);

        recordFileInfo.phone = phone;

        cnt = 0;

        for(String imgPath : imgPathList){

            File file = new File(imgPath);

            String imgName = file.getName();

            recordFileInfo.imgInfoList.add(new ImageInfo(phone, imgName, recordFileInfo.date, ""));

            cnt++;

        }

        LocationServer.getLocation(editorActivity, recordFileInfo);

    }

    public static void onLocationReceived(final EditorActivity editorActivity, final RecordFileInfo recordFileInfo) {

        recordFileInfo.location = lastLocation;

        //write item3 : local record table item
        RuntimeDataManager.getDataBaseHelper().addRecordItem(new RecordItem(
                recordFileInfo.phone, recordFileInfo.name, recordFileInfo.date,
                recordFileInfo.location));

        //write item4: cloud record table item
        AVObject recordTableItem = new AVObject("recordTable");

        recordTableItem.put("name", recordFileInfo.name);

        recordTableItem.put("location", recordFileInfo.location);

        recordTableItem.put("phone", recordFileInfo.phone);

        recordTableItem.put("year", recordFileInfo.date.year);

        recordTableItem.put("month", recordFileInfo.date.month);

        recordTableItem.put("day", recordFileInfo.date.day);

        recordTableItem.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e != null){
                    editorActivity.onRecordSaved(false);
                }else{
                    editorActivity.increateUploadBy(20);
                    if(recordFileInfo.imgInfoList.size() == 0){
                        editorActivity.onRecordSaved(true);
                    }
                    Log.i("#debug", "record table item upload successfully");
                }
            }
        });

        cur = 0;


        for(ImageInfo imageInfo : recordFileInfo.imgInfoList) {

            AVFile imgFile = new AVFile(localPhoneNumber + "_" + imageInfo.name,
                    LocalDataUtil.getUsrImg(recordFileInfo.phone, imageInfo.name));

            imageInfo.location = recordFileInfo.location;

            //write item5: local image table item
            RuntimeDataManager.getDataBaseHelper().addImgItem(imageInfo);

            //write item 8: local image-record table item
            RuntimeDataManager.getDataBaseHelper().addImgRecordItem(new ImageRecordTableItem(
                    imageInfo.phone, recordFileInfo.name, imageInfo.name
            ));

            //write item9: cloud image-record table item

            AVObject relateTaleItem = new AVObject("relateTable");

            relateTaleItem.put("imageName", imageInfo.name);

            relateTaleItem.put("recordName", recordFileInfo.name);

            relateTaleItem.put("phone", recordFileInfo.phone);

            relateTaleItem.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if(e != null){
                        editorActivity.onRecordSaved(false);
                    }else{
                        editorActivity.increateUploadBy(20);
                        Log.i("#debug", "relateTableItem upload successfully");
                    }
                }
            });

            //write item6: cloud image table item

            final AVObject imageTableItem = new AVObject("imgTable");

            imageTableItem.put("name", imageInfo.name);

            imageTableItem.put("phone", imageInfo.phone);

            imageTableItem.put("location", imageInfo.location);

            imageTableItem.put("year", imageInfo.date.year);

            imageTableItem.put("month", imageInfo.date.month);
            imageTableItem.put("day", imageInfo.date.day);

            imageTableItem.saveInBackground();
            //write item7: cloud image file
            imgFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if(e != null) {
                        editorActivity.onRecordSaved(false);
                    }else {
                        ++cur;
                        if(cur == cnt){
                            editorActivity.increateUploadBy(20);
                            editorActivity.onRecordSaved(true);
                        }
                    }
                }
            });

        }

    }


    private static void synchronizeUsrData(final LoginActivity context, final AVObject usr){

        AVQuery<AVObject> query = new AVQuery<>("imgTable");
        query.whereEqualTo("phone", usr.get("phone").toString());
        Log.i("#debug", "next: query usr photoGallery");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e != null){
                    Log.i("#debug", "error not null in photoGallery query");
                    LoginActivity.onDataSynchronized(context, false);
                }else if(!list.isEmpty()){
                    cnt = list.size();
                    cur = 0;
                    for(final AVObject photoInfo : list){
                        final String imgFileName = localPhoneNumber + "_" + photoInfo.get("name")
                                .toString();
                        final String imgName = photoInfo.get("name").toString();
                        Log.i("#debug", "phtot anme is " + imgName);
                        AVQuery<AVObject> query = new AVQuery<AVObject>("_File");
                        query.whereEqualTo("name", imgFileName);
                        photoUrl = "";
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if(e != null){
                                    LoginActivity.onDataSynchronized(context, false);
                                }else{
                                    photoUrl = (list.get(0)).get("url").toString();
                                }
                                onImageUrlGot(imgName, context, usr);
                            }
                        });
                    }
                }else{
                    Log.i("#debug", "next: syn usr records");
                    synchronizeUsrRecords(context, usr);
                }
            }
        });
    }

    private static void onImageUrlGot(final String imgName, final LoginActivity context, final AVObject usr){
        AVFile photoFile = new AVFile(imgName, photoUrl, null);
        photoFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                if(e != null){
                    LoginActivity.onDataSynchronized(context, false);
                }else{
                    ++cur;
                    String phone = usr.get("phone").toString();
                    LocalDataUtil.writeUsrImg(bytes, phone, imgName);
                    if(cur == cnt){
                        synchronizeUsrRecords(context, usr);
                    }
                }
            }
        });
    }

}
