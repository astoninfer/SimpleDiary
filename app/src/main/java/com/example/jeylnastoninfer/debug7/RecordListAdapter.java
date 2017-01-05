package com.example.jeylnastoninfer.debug7;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.BaseMenuPresenter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.ImgdspHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.NumericHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.LocalDataUtil;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.RecordFileInfo;
import com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils.RuntimeDataManager;
import com.example.jeylnastoninfer.debug7.daemonAct.EditorActivity;
import com.example.jeylnastoninfer.debug7.daemonAct.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecordListAdapter extends BaseAdapter {
    private List<Map<String,Object>> data = null;
    private LayoutInflater layoutInflater = null;
    private RecordListActivity recordListActivity = null;

    public RecordListAdapter(RecordListActivity recordListActivity) {

        data = new ArrayList<>();
        this.recordListActivity = recordListActivity;
        this.layoutInflater = LayoutInflater.from(recordListActivity);

    }

    public final class Content{

        TextView recordDate;
        TextView recordLocation;
        LinearLayout recordInfo;
        ImageView img1;
        ImageView img2;
        ImageView img3;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Content content = null;
        if(convertView == null) {

            content = new Content();

            convertView = layoutInflater.inflate(R.layout.record_list_item,null);

            content.recordDate = (TextView) convertView.findViewById(R.id.record_date);

            content.recordLocation = (TextView) convertView.findViewById(R.id.record_location);

            content.recordInfo = (LinearLayout) convertView.findViewById(R.id.record_info);

            content.img1 = content.img2 = content.img3 = null;

            content.img1 = (ImageView)convertView.findViewById(R.id.record_item_img1);

            content.img2 = (ImageView)convertView.findViewById(R.id.record_item_img2);

            content.img3 = (ImageView)convertView.findViewById(R.id.record_item_img3);

            convertView.setTag(content);
        }
        else {
            content = (Content) convertView.getTag();
        }


        int _year = (int)data.get(position).get("year");

        int _month = (int)data.get(position).get("month");

        int _day = (int)data.get(position).get("day");

        content.recordDate.setText(_year + "年" + _month + "月" + _day + "日");

        content.recordLocation.setText(data.get(position).get("location").toString());

        content.img1.setImageBitmap((Bitmap)data.get(position).get("img1"));

        content.img2.setImageBitmap((Bitmap)data.get(position).get("img2"));

        content.img3.setImageBitmap((Bitmap)data.get(position).get("img3"));

        /*
        content.recordInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = data.get(position).get("name").toString();
                Log.i("debug", "position: " + position);
                Log.i("debug", "name: " + data.get(position).get("name"));
                recordListActivity.loadrecord(path);
            }
        });
        */

        /*
        content.recordRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = data.get(position).get("name").toString();
                RuntimeDataManager.getDataBaseHelper().deleteRecord(path);
                freshdata();
            }
        });
        */

        return convertView;
    }

    public void onItemToDelete(RecordListActivity activity, final int position){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity).setTitle("删除记录")
                .setMessage("确定要删除该项记录吗, 此操作不能恢复").setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        String path = data.get(position).get("name").toString();
                        RuntimeDataManager.getDataBaseHelper().deleteRecord(path);
                        freshdata();

                    }
                });
        alertDialog.show();
    }

    public void onItemToOpen(int position){

        String path = data.get(position).get("name").toString();

        Log.i("debug", "position: " + position);

        Log.i("debug", "name: " + data.get(position).get("name"));

        recordListActivity.loadrecord(path);


    }

    public void freshdata() {

        data.clear();

        ArrayList<RecordFileInfo> recordFiles = RuntimeDataManager.getDataBaseHelper().getAllRecords();

        RecordFileInfo rf = null;

        Log.i("#debug", "recordFiles cout is " + recordFiles.size());

        for(int i = 0;i < recordFiles.size();i++) {

            rf = recordFiles.get(i);

            Map<String,Object> map = new HashMap<>();

            map.put("name", rf.name);

            map.put("year", rf.date.year);

            map.put("month", rf.date.month);

            map.put("day", rf.date.day);

            map.put("location", rf.location);

            String recordName = rf.name;

            String[] imgs = RuntimeDataManager.getDataBaseHelper().getImgListFromRecordName(
                    MainActivity.usrPhone, recordName);

            int len = imgs.length;

            int width = (NumericHelper.getScreenWidth(recordListActivity) / 5);

            int height = width * 6 / 5;


            for(int j = 0; j < len; j++){

                String imgPath = LocalDataUtil.getUsrPhotoGalleryPath(MainActivity.usrPhone)
                        + imgs[j];

                File imgFile = new File(imgPath);

                if(imgFile.exists()){
                    Bitmap bmp = ImgdspHelper.decodeSampledBitmapFromResource(imgPath, width, height);

                    if(j == 0){
                        map.put("img1", bmp);
                        //content.img1.setImageBitmap(bmp);
                    }else if(j == 1){
                        map.put("img2", bmp);
                        //content.img2.setImageBitmap(bmp);
                    }else if(j == 2){
                        map.put("img3", bmp);
                        //content.img3.setImageBitmap(bmp);
                    }
                }

            }


            data.add(map);

        }

        notifyDataSetChanged();

    }
}
