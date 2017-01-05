package com.example.jeylnastoninfer.debug7;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.NumericHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.LocalDataUtil;
import com.example.jeylnastoninfer.debug7.daemonAct.EditorActivity;
import com.example.jeylnastoninfer.debug7.daemonAct.MainActivity;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class RecordListActivity extends SwipeBackActivity{

    private SwipeMenuListView listView = null;

    private RecordListAdapter adapter = null;

    private LinearLayout topMenu = null;

    private SwipeMenuCreator swipeMenuCreator = null;

    private void init(){

        adapter = new RecordListAdapter(RecordListActivity.this) ;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                adapter.freshdata();
            }
        });

        //adapter.freshdata();

        listView = (SwipeMenuListView) findViewById(R.id.recordlist);

        listView.setAdapter(adapter);

        topMenu = (LinearLayout)this.findViewById(R.id.record_list_act_top_menu);

        topMenu.setPadding(0, NumericHelper.getStatusBarHeight(RecordListActivity.this), 0, 0);

        swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                //set item background
                openItem.setBackground(R.drawable.round_corner_btn_6);
                //openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.clay_blue)));
                openItem.setIcon(R.drawable.ic_open_in_new_white_24dp);
                openItem.setWidth(NumericHelper.dip2px(RecordListActivity.this, 55));
                menu.addMenuItem(openItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(R.drawable.round_corner_btn_7);
                //deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
                deleteItem.setIcon(R.drawable.ic_delete_forever_white_24dp);
                deleteItem.setWidth(NumericHelper.dip2px(RecordListActivity.this, 55));
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(swipeMenuCreator);

        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:
                        //open
                        adapter.onItemToOpen(position);
                        break;
                    case 1:
                        //delete
                        adapter.onItemToDelete(RecordListActivity.this, position);
                        break;
                }
                return false;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_list_activity);

        init();

    }


    public void loadrecord(String recordName) {

        Log.i("debug", "this: loadrecord");


        Intent intent = new Intent(this, EditorActivity.class);

        Bundle bundle = new Bundle();

        bundle.putString("tarPath", LocalDataUtil.getUsrRecordPath(MainActivity.usrPhone) + recordName);

        intent.putExtras(bundle);

        startActivity(intent);

    }


}

