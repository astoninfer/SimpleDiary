package com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils;

public class ImageRecordTableItem {

    public final String RECORD = "recordName";

    public final String PHONE = "phone";

    public final String IMAGE = "imageName";

    private String recordName = null;

    private String imageName = null;

    private String usrPhone = null;

    public ImageRecordTableItem(String usrPhone, String recordName, String imageName){

        this.usrPhone = usrPhone;

        this.recordName = recordName;

        this.imageName = imageName;

    }

    public String getRecordName(){
        return this.recordName;
    }

    public String getImageName(){
        return this.imageName;
    }

    public String getUsrPhone(){
        return this.usrPhone;
    }



}
