package com.example.astoninfer.demo3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by woi on 2016/11/20.
 */

public class TheAdapter extends BaseAdapter {
    private List<Map<String,Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private DataBaseHelper dbhelper;
    private RecordListActivity recordListActivity;
    public TheAdapter(Context context, RecordListActivity recordListActivity) {
        data = new ArrayList<>();
        this.context = context;
        dbhelper = new DataBaseHelper(context,EditorActivity.DATABASE);
        this.recordListActivity = recordListActivity;
        this.layoutInflater = LayoutInflater.from(context);

    }

    public final class Content{
        TextView recordtitle;
        TextView recorddate;
        TextView recordaddress;
        LinearLayout recordinfo;
        Button recorddelete;
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
            convertView = layoutInflater.inflate(R.layout.onerecord,null);
            content.recordtitle = (TextView) convertView.findViewById(R.id.record_title);
            content.recorddate = (TextView) convertView.findViewById(R.id.record_date);
            content.recordaddress = (TextView) convertView.findViewById(R.id.record_address);
            content.recordinfo = (LinearLayout) convertView.findViewById(R.id.record_info);
            content.recorddelete = (Button) convertView.findViewById(R.id.record_delete);
            convertView.setTag(content);
        }
        else {
            content = (Content) convertView.getTag();
        }
        content.recordtitle.setText(data.get(position).get("title").toString());
        content.recorddate.setText(data.get(position).get("date").toString());
        content.recordaddress.setText(data.get(position).get("address").toString());
        content.recordinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = data.get(position).get("path").toString();
                recordListActivity.loadrecord(path);
            }
        });
        content.recorddelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = data.get(position).get("path").toString();
                dbhelper.deleteRecord(path);
                freshdata();
            }
        });
        return convertView;
    }

    public void freshdata() {
        data.clear();
        ArrayList<RecordFile> recordFiles = dbhelper.getAllRecord();
        RecordFile rf = null;
        for(int i = 0;i < recordFiles.size();i++) {
            rf = recordFiles.get(i);
            Map<String,Object> map = new HashMap<>();
            map.put("title",rf.gettitle());
            map.put("date",rf.getDate());
            map.put("address",rf.getAddress());
            map.put("path",rf.getpath());
            data.add(map);
        }
        notifyDataSetChanged();
    }
}
