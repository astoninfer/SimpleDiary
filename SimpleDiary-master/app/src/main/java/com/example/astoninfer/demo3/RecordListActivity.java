package com.example.astoninfer.demo3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordListActivity extends AppCompatActivity {
    private ListView listView;
    TheAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        adapter = new TheAdapter(RecordListActivity.this, this) ;
        adapter.freshdata();
        listView = (ListView) findViewById(R.id.recordlist);
        listView.setAdapter(adapter);
    }


    public void loadrecord(String path) {
        File file = new File(path);
        if(!file.exists()) {
            DataBaseHelper.deleteRecord(path);
            adapter.freshdata();
            adapter.notifyDataSetChanged();
            Toast.makeText(RecordListActivity.this,"文件不存在",Toast.LENGTH_SHORT).show();
            return ;
        }
        Intent intent = new Intent(this,EditorActivity.class);
        intent.putExtra(MainActivity.PATH_MESSAGE,path);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.freshdata();
    }

}

