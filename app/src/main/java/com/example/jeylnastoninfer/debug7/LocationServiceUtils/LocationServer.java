package com.example.jeylnastoninfer.debug7.LocationServiceUtils;

import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.DataSynchronizeHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.RecordFileInfo;
import com.example.jeylnastoninfer.debug7.daemonAct.EditorActivity;


public class LocationServer {

    public static void getLocation(EditorActivity activity, RecordFileInfo info){

        activity.increateUploadBy(30);

        DataSynchronizeHelper.lastLocation = "defaultAddr:Haidian, Beijing";

        DataSynchronizeHelper.onLocationReceived(activity, info);

    }

}
