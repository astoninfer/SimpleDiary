package com.example.astoninfer.demo3;

import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class RecordFile {

    private String path;
    private String title;
    private ArrayList<String> tags;
    private String date;
    private String address;
    private ArrayList<ImageInfo> imginfos;

    public RecordFile() {
        tags = new ArrayList<String>();
        imginfos = new ArrayList<ImageInfo>();
        path = "";
        title = "";
        date = "";
        address = "";
    }

    public void setpath(String s) {
        path = s;
    }

    public String getpath() {
        return path;
    }

    public void settitle(String s) {
        title = s;
    }

    public String gettitle() {
        return title;
    }

    public void addTag(String s) {
        tags.add(s);
    }

    public void removetag(int i) {
        if(i < tags.size()) {
            tags.remove(i);
        }
    }

    public ArrayList<String> gettags() {
        ArrayList<String> ts = new ArrayList<>();
        for(int i = 0;i < tags.size();i ++) {
            ts.add(tags.get(i));
        }
        return ts;
    }

    public void setDate(String d) {
        date = d;
    }

    public String getDate() {
        return date;
    }

    public void setAddress(String ad) {
        address = ad;
    }

    public String getAddress() {
        return address;
    }

    public void addimginfo(ImageInfo info) {
        imginfos.add(info);
    }

    public ArrayList<ImageInfo> getimginfos() {
        ArrayList<ImageInfo> ts = new ArrayList<>();
        for(int i = 0;i < imginfos.size();i ++) {
            ts.add(imginfos.get(i));
        }
        return ts;
    }

}

class ImageInfo
{
    String path;
    String date;
    String address;
    public ImageInfo(String path,String date,String address) {
        this.path = path;
        this.date = date;
        this.address = address;
    }
}