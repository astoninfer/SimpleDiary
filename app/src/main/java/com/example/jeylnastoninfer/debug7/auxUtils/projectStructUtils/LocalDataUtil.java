package com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils.RuntimeDataManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalDataUtil {

    private static Context mContext;

    private static String rootDir = null;

    private static String photoGalleryName = "photoGallery";

    private static String recordName = "records";

    public static void init(Context context){

        mContext = context;
        rootDir =  Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getUsrRootDir(String phone){
        return rootDir + "/" + phone + "/";
    }

    public static String getUsrPhotoGalleryPath(String phone){
        return getUsrRootDir(phone) + photoGalleryName + "/";
    }

    public static String getUsrRecordPath(String phone){
        return getUsrRootDir(phone) + recordName + "/";
    }

    private static void createUsrRootDir(String phone){
        Log.i("#debug", "at createUsrRootDir");
        File usrFile = new File(getUsrRootDir(phone));
        boolean ok = usrFile.mkdirs();
        Log.i("#debug", "create path at " + usrFile.getAbsolutePath());
        Log.i("#debug", "create result " + usrFile.exists());
        Log.i("#debug", "create a dir ? " + usrFile.isDirectory());
    }

    public static boolean checkIfUserExists(String phone){

        File usrFile = new File(getUsrRootDir(phone));
        String path = getUsrRootDir(phone);
        Log.i("#debug", "at check path: " + path);
        Log.i("#debug", "is file on ? " + (usrFile.exists() == false ? "no" : "yes"));
        File usrFile1 = new File(getUsrRootDir("13020082688"));
        Log.i("#debug", "at check path: " + usrFile1.getAbsolutePath());
        Log.i("#debug", "is file on ? " + (usrFile1.exists() == false ? "no" : "yes"));

        return usrFile.exists() && usrFile.isDirectory();

    }

    public static void createLocalDirs(String phone){
        createUsrRootDir(phone);
        File photGalleryFile = new File(getUsrPhotoGalleryPath(phone));
        if(!photGalleryFile.exists()){
            boolean ok = photGalleryFile.mkdirs();
            if(!ok){
                //TODO: handle error here
            }
        }

        File recordFile = new File(getUsrRecordPath(phone));
        if(!recordFile.exists()){
            boolean ok = recordFile.mkdirs();
            if(!ok){
                //TODO: handle error here
            }
        }

    }

    public static void writeUsrRcd(byte[] bytes, String phone, String rcdName){

        String tarPath = getUsrRecordPath(phone) + rcdName;

        File file = new File(tarPath);

        try{
            if(!file.exists()){
                boolean ok = file.createNewFile();
                if(!ok){
                    Log.i("debug", "create file fail");
                }else{
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.close();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            Log.i("debug", "write record error");
        }

    }

    public static byte[] getUsrImg(String phone, String imgName){

        String tarPath = getUsrPhotoGalleryPath(phone) + imgName;

        File file = new File(tarPath);

        byte[] imgBytes = new byte[(int)file.length()];

        try{
            if(!file.exists()){
                Log.i("#debug", "tar file does not exist");
            }else{
                FileInputStream fis = new FileInputStream(file);
                fis.read(imgBytes);
                fis.close();
            }
        }catch (IOException e){
            e.printStackTrace();
            Log.i("#degbu", "ioexception");
        }
        return imgBytes;
    }


    public static void writeUsrImg(byte[] bytes, String phone, String imgName){

        String tarPath = getUsrPhotoGalleryPath(phone) + imgName;

        File file = new File(tarPath);

        try{
            if(!file.exists()){
                boolean ok = file.createNewFile();
                if(!ok){
                    Log.i("debug", "create file fail");
                }else{
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes, 0, bytes.length);
                    fos.close();
                }
            }

        }catch (IOException e){
            e.printStackTrace();
            Log.i("debug", "write image error");
        }
    }

    public static void writeImgItemToLocalDB(ImageInfo item){
        RuntimeDataManager.getDataBaseHelper().addImgItem(item);
    }

    public static void writeRecordItemToLocalDB(RecordItem item){
        Log.i("#debug", "at write record item to local datacase");
        RuntimeDataManager.getDataBaseHelper().addRecordItem(item);
    }

    public static void writeRelateRecordToLocalDB(ImageRecordTableItem item){

        RuntimeDataManager.getDataBaseHelper().addImgRecordItem(item);

    }

}
