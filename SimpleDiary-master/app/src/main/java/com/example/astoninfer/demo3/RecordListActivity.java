package com.example.astoninfer.demo3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class RecordListActivity extends SwipeBackActivity{
    private ListView listView;
    TheAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        AuxUtil.setStatusBarColor(this, R.color.colorPrimaryDark);
        adapter = new TheAdapter(RecordListActivity.this, this) ;
        adapter.freshdata();
        listView = (ListView) findViewById(R.id.recordlist);
        listView.setAdapter(adapter);
    }


    public void loadrecord(String path) {
        Intent intent = new Intent(this,EditorActivity.class);
        intent.putExtra(MainActivity.PATH_MESSAGE,path);
        startActivity(intent);
    }


}

