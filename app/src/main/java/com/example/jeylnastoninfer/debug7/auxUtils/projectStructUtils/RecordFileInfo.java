package com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils;

import java.util.ArrayList;

public class RecordFileInfo {

    public String phone = null;

    public String name = null;

    public DateInfo date = null;

    public String location = null;



    public ArrayList<ImageInfo> imgInfoList = null;


    public RecordFileInfo() {

        imgInfoList = new ArrayList<ImageInfo>();

        name = "";

        date = new DateInfo();

        location = "";
    }

    public void addImgInfo(ImageInfo imageInfo){

        imgInfoList.add(imageInfo);

    }

}
