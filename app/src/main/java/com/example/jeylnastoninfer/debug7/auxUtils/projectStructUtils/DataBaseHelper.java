package com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jeylnastoninfer.debug7.daemonAct.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * we need 3 tables
     * TABLE_RCD
     * table for record, with date, location, filepath
     * <p>
     * TABLE_IMG
     * table for images, with data, location, filepath
     * <p>
     * TABLE_R2I
     * table for image-record relations
     */

    public static final int version = 1;

    public static final String RCD_TABLE_NAME = "RECORD";
    public static final String IMG_TABLE_NAME = "IMAGE";
    public static final String R2I_TABLE_NAME = "RECORD_PHOTO";

    public static final String USERS_INFO_TABLE_NAME = "USERS_INFO";


    public static final String CREATE_RECORD_TABLE =
            "create table if not exists " + RCD_TABLE_NAME +
                    "(phone text, name text, year int, month int, day int, location text)";

    public static final String CREATE_IMG_TABLE =
            "create table if not exists " + IMG_TABLE_NAME +
                    "(phone text, name text, year int, month int, day int, location text)";

    public static final String CREATE_RI_TABLE =
            "create table if not exists " + R2I_TABLE_NAME +
                    "(phone text, recordName text, imageName text)";

    public static final String CREATE_USERS_INFO_TABLE =
            "create table if not exists " + USERS_INFO_TABLE_NAME + "(phone text," +
                    "pwd text, nickname text, year int, month int, day int)";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context, String name, int version) {
        super(context, name, null, 1);
    }

    public DataBaseHelper(Context context, String name) {
        super(context, name, null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_RECORD_TABLE);
        db.execSQL(CREATE_IMG_TABLE);
        db.execSQL(CREATE_RI_TABLE);
        db.execSQL(CREATE_USERS_INFO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addRecord(RecordFileInfo recordFileInfo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("name", recordFileInfo.name);
        cv.put("year", recordFileInfo.date.year);
        cv.put("month", recordFileInfo.date.month);
        cv.put("day", recordFileInfo.date.year);
        cv.put("location", recordFileInfo.location);


        db.insert(RCD_TABLE_NAME,null, cv);



        ArrayList<ImageInfo> imgInfoList = recordFileInfo.imgInfoList;

        for(ImageInfo imageInfo : imgInfoList){

            ContentValues icv = new ContentValues();

            ContentValues ricv = new ContentValues();

            icv.put("name", imageInfo.name);

            icv.put("year", imageInfo.date.year);

            icv.put("month", imageInfo.date.month);

            icv.put("day", imageInfo.date.day);

            icv.put("location", imageInfo.location);

            db.insert(IMG_TABLE_NAME, null, icv);

            ricv.put("recordName", recordFileInfo.name);

            ricv.put("imageName", imageInfo.name);

            db.insert(R2I_TABLE_NAME, null, ricv);

        }

    }

    public void deleteRecord(String path) {

        SQLiteDatabase db = this.getWritableDatabase();

        String ipath;

        Cursor ri_cursor = db.rawQuery("select * from " + R2I_TABLE_NAME + " where recordName = ?",new String[]{path});

        while (ri_cursor.moveToNext()) {
            ipath = ri_cursor.getString(ri_cursor.getColumnIndex("imageName"));
            db.delete(IMG_TABLE_NAME,"name=?",new String[]{ipath});
        }

        db.delete(R2I_TABLE_NAME,"recordName=?",new String[]{path});
        db.delete(RCD_TABLE_NAME,"name=?",new String[]{path});
        ri_cursor.close();
    }

    public int getrecordCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + RCD_TABLE_NAME, null);
        cursor.close();
        return cursor.getCount();
    }

    public ArrayList<RecordFileInfo> getAllRecords(){

        SQLiteDatabase db = this.getWritableDatabase();

        String recordName = null, recordLocation = null;

        DateInfo recordDate = null;
        Cursor cursor = db.rawQuery("select * from " + RCD_TABLE_NAME, null);
        cursor.getCount();
        Log.i("#debug", "sie of cur is " + cursor.getCount());

        ArrayList<RecordFileInfo> recordFileInfoList = new ArrayList<>();

        while(cursor.moveToNext()){

            RecordFileInfo recordFileInfo = new RecordFileInfo();

            recordName = cursor.getString(cursor.getColumnIndex("name"));

            int date_year = cursor.getInt(cursor.getColumnIndex("year"));

            int date_month = cursor.getInt(cursor.getColumnIndex("month"));

            int date_day = cursor.getInt(cursor.getColumnIndex("day"));

            recordDate = new DateInfo(date_year, date_month, date_day);

            recordLocation = cursor.getString(cursor.getColumnIndex("location"));

            recordFileInfo.name = recordName;

            recordFileInfo.location = recordLocation;

            recordFileInfo.date = recordDate;

            recordFileInfoList.add(recordFileInfo);

        }

        cursor.close();

        return recordFileInfoList;

    }


    public void addNewUser(Context context, String nickname, String phone, String pwd){
        //must be handled immediately

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("phone", phone);
        cv.put("pwd", pwd);
        cv.put("nickname", nickname);

        Calendar now = Calendar.getInstance();
        DateInfo dateInfo = new DateInfo(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1,
                now.get(Calendar.DAY_OF_MONTH));

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String formattedDate = dateFormat.format(date);

        cv.put("year", dateInfo.year);
        cv.put("month", dateInfo.month);
        cv.put("day", dateInfo.day);

        db.insert(USERS_INFO_TABLE_NAME, null, cv);

        Log.i("#debug", "next: register on line");

        DataSynchronizeHelper.addUsr(context, new UsrInfo(phone, nickname, pwd, dateInfo));

    }

    public void addImgRecordItem(ImageRecordTableItem item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(item.PHONE, item.getUsrPhone());
        cv.put(item.RECORD, item.getRecordName());
        cv.put(item.IMAGE, item.getImageName());
        db.insert(R2I_TABLE_NAME, null, cv);
    }

    public void addImgItem(ImageInfo item){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ImageInfo.PHONE, item.phone);
        cv.put(ImageInfo.NAME, item.name);
        cv.put(ImageInfo.YEAR, item.date.year);
        cv.put(ImageInfo.MONTH, item.date.month);
        cv.put(ImageInfo.DAY, item.date.day);
        cv.put(ImageInfo.LOCATION, item.location);

        db.insert(IMG_TABLE_NAME, null, cv);

    }

    public void addRecordItem(RecordItem item){

        Log.i("#debug", "at add record item");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(RecordItem.PHONE, item.phone);
        cv.put(RecordItem.NAME, item.name);
        cv.put(RecordItem.YEAR, item.date.year);
        cv.put(RecordItem.MONTH, item.date.month);
        cv.put(RecordItem.DAY, item.date.day);
        cv.put(RecordItem.LOCATION, item.location);

        long x = db.insert(RCD_TABLE_NAME, null, cv);
        Log.i("#debug", "insert rreturn is " + x);

        Cursor cur = db.rawQuery("select * from " + RCD_TABLE_NAME, null);

        int cnt = cur.getCount();

        Log.i("#debug", "cnt is " + cnt);

    }

    public String[] getImgListFromRecordName(String __phone, String __recordName){

        String[] ret = { null, null, null };

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + R2I_TABLE_NAME +
                " where phone = ? and recordName = ? order by imageName desc limit 3", new String[]{ __phone, __recordName});

        int cur = 0;

        while(cursor.moveToNext()){

            ret[cur] = cursor.getString(cursor.getColumnIndex("imageName"));

            cur++;

        }


        return ret;

    }

    public ImageInfo[] getAllImagesDownTime(){

        String sql = "select * from " + IMG_TABLE_NAME + " where phone = ? order by name desc";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{
            MainActivity.usrPhone});

        final int cnt = cursor.getCount();

        ImageInfo[] list = new ImageInfo[cnt];

        int cur = 0;

        while(cursor.moveToNext()){

            ImageInfo temInfo = new ImageInfo(MainActivity.usrPhone, "", null, "");
            temInfo.name = cursor.getString(cursor.getColumnIndex("name"));

            int _day = cursor.getInt(cursor.getColumnIndex("day"));

            int _month = cursor.getInt(cursor.getColumnIndex("month"));

            int _year = cursor.getInt(cursor.getColumnIndex("year"));

            DateInfo _date = new DateInfo(_year, _month, _day);

            temInfo.date = _date;

            temInfo.location = cursor.getString(cursor.getColumnIndex("location"));

            list[cur] = temInfo;
            cur++;

        }

        return list;
    }


}
