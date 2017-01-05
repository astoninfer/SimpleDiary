package com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils;

public class RecordItem {

    public static final String PHONE = "phone";

    public static final String NAME = "name";

    public static final String DATE = "DATE";

    public static final String LOCATION = "location";

    public static final String YEAR = "year";

    public static final String MONTH = "month";

    public static final String DAY = "day";

    public String phone = null;

    public String name = null;

    public DateInfo date = null;

    public String location = null;

    public RecordItem(String phone, String name, DateInfo date, String location){

        this.phone = phone;

        this.name = name;

        this.date = date;

        this.location = location;
    }

}
