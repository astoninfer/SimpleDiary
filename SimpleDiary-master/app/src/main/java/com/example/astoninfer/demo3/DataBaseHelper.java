package com.example.astoninfer.demo3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by woi on 2016/11/19.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static DataBaseHelper dataBaseHelper;
    public static final int version = 1;
    public static final String DATABASE = "recordsaver";
    public static final String RECORD_TABLE_NAME = "RECORD";
    public static final String IMG_TABLE_NAME = "IMAGE";
    public static final String RI_TABLE_NAME = "RECORD_PHOTO";
    public static final String CREATE_RECORD_TABLE = "create table if not exists " + RECORD_TABLE_NAME + "(path text primary key,date text," +
            "address text,title text,tags text)";
    public static final String CREATE_IMG_TABLE = "create table if not exists " + IMG_TABLE_NAME + "(path text primary key,date text," +
            "address text)";
    public static final String CREATE_RI_TABLE = "create table if not exists " + RI_TABLE_NAME + "(rpath text,ipath text)";
    private DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private DataBaseHelper(Context context, String name, int version) {
        super(context, name, null, 1);
    }

    private DataBaseHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    public static void init(Context context) {
        dataBaseHelper = new DataBaseHelper(context,DATABASE);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORD_TABLE);
        db.execSQL(CREATE_IMG_TABLE);
        db.execSQL(CREATE_RI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void updateRecord(RecordFile rf) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        String tag = "";
        ContentValues recordvalue = new ContentValues();
        ArrayList<String> tags = rf.gettags();
        for(int i = 0;i < tags.size();i ++) {
            tag += tags.get(i);
        }
        recordvalue.put("title",rf.gettitle());
        recordvalue.put("date",rf.getDate());
        recordvalue.put("address",rf.getAddress());
        recordvalue.put("tags",tag);
        db.update(RECORD_TABLE_NAME,recordvalue,"path=?",new String[]{rf.getpath()});
        ArrayList<ImageInfo> imginfos = rf.getimginfos();
        for(int i = 0;i < imginfos.size();i ++) {
            ContentValues imgvalue = new ContentValues();
            ContentValues rivalue = new ContentValues();
            ImageInfo imageInfo = imginfos.get(i);
            imgvalue.put("path",imageInfo.path);
            imgvalue.put("address",imageInfo.address);
            imgvalue.put("date",imageInfo.date);
            db.insert(IMG_TABLE_NAME,null,imgvalue);
            rivalue.put("rpath",rf.getpath());
            rivalue.put("ipath",imageInfo.path);
            db.insert(RI_TABLE_NAME,null,rivalue);
        }
    }

    public static void addRecord(RecordFile rf) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues recordvalue = new ContentValues();
        String tag = "";
        ArrayList<String> tags = rf.gettags();
        for(int i = 0;i < tags.size();i ++) {
            tag += tags.get(i);
        }
        recordvalue.put("title",rf.gettitle());
        recordvalue.put("path",rf.getpath());
        recordvalue.put("date",rf.getDate());
        recordvalue.put("address",rf.getAddress());
        recordvalue.put("tags",tag);
        db.insert(RECORD_TABLE_NAME,null,recordvalue);
        ArrayList<ImageInfo> imginfos = rf.getimginfos();
        for(int i = 0;i < imginfos.size();i ++) {
            ContentValues imgvalue = new ContentValues();
            ContentValues rivalue = new ContentValues();
            ImageInfo imageInfo = imginfos.get(i);
            imgvalue.put("path",imageInfo.path);
            imgvalue.put("address",imageInfo.address);
            imgvalue.put("date",imageInfo.date);
            db.insert(IMG_TABLE_NAME,null,imgvalue);
            rivalue.put("rpath",rf.getpath());
            rivalue.put("ipath",imageInfo.path);
            db.insert(RI_TABLE_NAME,null,rivalue);
        }

    }

    public static void deleteRecord(String path) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        String ipath;
        Cursor ri_cursor = db.rawQuery("select * from " + RI_TABLE_NAME + " where rpath = ?",new String[]{path});
        while (ri_cursor.moveToNext()) {
            ipath = ri_cursor.getString(ri_cursor.getColumnIndex("ipath"));
            db.delete(IMG_TABLE_NAME,"path=?",new String[]{ipath});
            new File(ipath).delete();
        }
        db.delete(RI_TABLE_NAME,"rpath=?",new String[]{path});
        db.delete(RECORD_TABLE_NAME,"path=?",new String[]{path});
        ri_cursor.close();
        new File(path).delete();
    }

    public static void deleteimg(String path) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.delete(RI_TABLE_NAME,"rpath=?",new String[]{path});
        db.delete(IMG_TABLE_NAME,"path=?",new String[]{path});
    }

    public static RecordFile queryrecord(String path) {
        RecordFile rf = new RecordFile();
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + RECORD_TABLE_NAME + " where path=?",new String[]{path});
        cursor.moveToNext();
        rf.settitle(cursor.getString(cursor.getColumnIndex("title")));
        rf.setDate(cursor.getString(cursor.getColumnIndex("date")));
        rf.setAddress(cursor.getString(cursor.getColumnIndex("address")));
        cursor.close();
        return rf;
    }

    public static int getrecordCount() {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + RECORD_TABLE_NAME,null);
        cursor.close();
        return cursor.getCount();
    }

    public static ArrayList<RecordFile> getAllRecord() {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        String path,date,address,title,tags;
        Cursor cursor = db.rawQuery("select * from " + RECORD_TABLE_NAME,null);
        ArrayList<RecordFile> recordFiles = new ArrayList<RecordFile>();
        while (cursor.moveToNext()) {
            RecordFile recordFile = new RecordFile();
            path = cursor.getString(cursor.getColumnIndex("path"));
            date = cursor.getString(cursor.getColumnIndex("date"));
            address = cursor.getString(cursor.getColumnIndex("address"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            tags = cursor.getString(cursor.getColumnIndex("tags"));
            recordFile.settitle(title);
            recordFile.setAddress(address);
            recordFile.setDate(date);
            recordFile.setpath(path);
            recordFiles.add(recordFile);
        }
        cursor.close();
        return  recordFiles;
    }


}
