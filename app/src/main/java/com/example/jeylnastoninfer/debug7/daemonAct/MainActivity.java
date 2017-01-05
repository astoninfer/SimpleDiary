package com.example.jeylnastoninfer.debug7.daemonAct;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.jeylnastoninfer.debug7.ImageLoader;
import com.example.jeylnastoninfer.debug7.PhotoAlbumActivity;
import com.example.jeylnastoninfer.debug7.R;
import com.example.jeylnastoninfer.debug7.RecordListActivity;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.DrawerHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.GraphicsHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.ImgdspHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.NumericHelper;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import com.example.jeylnastoninfer.debug7.homeInterfaceDesign.HomeScreen;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private Animation myAnimation_Translate = null;
    private DrawerLayout drawerRightLayout = null;
    private ImageView main_figure = null;
    private ScrollView mainDisplayLayout = null;
    private ImageView mainAdd = null;
    private TextView mainAlbum = null;
    private TextView mainRecord = null;
    private LinearLayout mainMenuBelow = null;
    private LinearLayout mainMenuAbove = null;
    private LinearLayout calendarLayout = null;
    private LinearLayout mainBlurView = null;
    private RelativeLayout rootLayout = null;
    private volatile boolean startAnimationSet = false;
    private ArrayList<HashMap<String, Object>> drawerList = null;
    private HashMap<String, Object> drawerHashMap = null;

    private ImageView mainMap = null;

    private SlidingMenu slidingMenu = null;

    private ListView main_drawer_list_layout = null;

    public static String usrPhone = null;

    private LayoutInflater mLayoutInflator = null;

    private View drawerItem1 = null;
    private View drawerItem2 = null;
    private View drawerItem3 = null;
    private View drawerItem4 = null;

    private ImageView drawerItem1Img = null;
    private ImageView drawerItem2Img = null;
    private ImageView drawerItem3Img = null;
    private ImageView drawerItem4Img = null;

    private TextView drawerItem1Text = null;
    private TextView drawerItem2Text = null;
    private TextView drawerItem3Text = null;
    private TextView drawerItem4Text = null;

    private enum slideAnimAction {
        CLOSE,
        OPEN,
        REVERSE
    }

    private enum slideState {
        CLOSED,
        OPENING,
        OPEN,
        CLOSING
    }

    private enum animStarter{
        CALENDAR,
        LITTLE_MAP
    }

    private slideState curCalendarState;
    private slideState curLittleMapState;

    private MaterialCalendarView mcv = null;
    private final String[] monthNameChinese = {"一", "二", "三", "四", "五", "六", "七", "八",
            "九", "十", "十一", "十二"};


    private void setListenerCluster(){
        drawerItem1 = this.findViewById(R.id.main_drawer_item_1);
        drawerItem1Img = (ImageView)this.findViewById(R.id.main_drawer_item_1_img);
        drawerItem1Text = (TextView)this.findViewById(R.id.main_drawer_item_1_text);

        drawerItem1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i("#debug", "at okay " + motionEvent.toString());
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    drawerItem1.setBackgroundColor(getResources().getColor(R.color.colorPrimary7));
                    drawerItem1Img.setImageResource(R.drawable.ic_account_circle_white_24dp);
                    drawerItem1Text.setTextColor(getResources().getColor(R.color.pure_white));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                    drawerItem1.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    drawerItem1Img.setImageResource(R.drawable.ic_account_circle_black_24dp);
                    drawerItem1Text.setTextColor(getResources().getColor(R.color.proper_grey));
                }
                return true;
            }
        });

        drawerItem2 = this.findViewById(R.id.main_drawer_item_2);
        drawerItem2Img = (ImageView)this.findViewById(R.id.main_drawer_item_2_img);
        drawerItem2Text = (TextView)this.findViewById(R.id.main_drawer_item_2_text);

        drawerItem2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    drawerItem2.setBackgroundColor(getResources().getColor(R.color.colorPrimary7));
                    drawerItem2Img.setImageResource(R.drawable.ic_settings_white_24dp);
                    drawerItem2Text.setTextColor(getResources().getColor(R.color.pure_white));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                    drawerItem2.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    drawerItem2Img.setImageResource(R.drawable.ic_settings_black_24dp);
                    drawerItem2Text.setTextColor(getResources().getColor(R.color.proper_grey));
                }
                return true;
            }
        });


        drawerItem3 = this.findViewById(R.id.main_drawer_item_3);
        drawerItem3Img = (ImageView)this.findViewById(R.id.main_drawer_item_3_img);
        drawerItem3Text = (TextView)this.findViewById(R.id.main_drawer_item_3_text);

        drawerItem3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    drawerItem3.setBackgroundColor(getResources().getColor(R.color.colorPrimary7));
                    drawerItem3Img.setImageResource(R.drawable.ic_backup_white_24dp);
                    drawerItem3Text.setTextColor(getResources().getColor(R.color.pure_white));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                    drawerItem3.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    drawerItem3Img.setImageResource(R.drawable.ic_backup_black_24dp);
                    drawerItem3Text.setTextColor(getResources().getColor(R.color.proper_grey));
                }
                return true;
            }
        });

        drawerItem4 = this.findViewById(R.id.main_drawer_item_4);
        drawerItem4Img = (ImageView)this.findViewById(R.id.main_drawer_item_4_img);
        drawerItem4Text = (TextView)this.findViewById(R.id.main_drawer_item_4_text);

        drawerItem4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    drawerItem4.setBackgroundColor(getResources().getColor(R.color.colorPrimary7));
                    drawerItem4Img.setImageResource(R.drawable.ic_info_white_24dp);
                    drawerItem4Text.setTextColor(getResources().getColor(R.color.pure_white));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                    drawerItem4.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    drawerItem4Img.setImageResource(R.drawable.ic_info_black_24dp);
                    drawerItem4Text.setTextColor(getResources().getColor(R.color.proper_grey));
                }
                return true;
            }
        });
    }

    private void init() {

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("usrInfo");
        usrPhone = bundle.getString("usrPhone");
        Log.i("#debug", "usr phone at main : " + usrPhone);
        drawerRightLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        rootLayout = (RelativeLayout) findViewById(R.id.main_top_relative_layout);
        mainMenuAbove = (LinearLayout)this.findViewById(R.id.main_menu_above);
        mainMenuAbove.setPadding(0, NumericHelper.getStatusBarHeight(MainActivity.this), 0, 0);

        mainDisplayLayout = (ScrollView) this.findViewById(R.id.main_display_layout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View myCalendarView = inflater.inflate(R.layout.main_calendar_view, null);
        calendarLayout = (LinearLayout) myCalendarView
                .findViewById(R.id.calendarView_layout);

        setListenerCluster();

        /*
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.letterpaper003);
        resideMenu.attachToActivity(MainActivity.this);

        String titles[] = { "A", "B", "C", "D", "E" };
        int[] icons = {R.drawable.add3, R.drawable.add3, R.drawable.add3, R.drawable.add3, R.drawable.add3};

        for(int i = 0; i < titles.length; i++) {
            ResideMenuItem resideMenuItem = new ResideMenuItem(this, icons[i], titles[i]);
            resideMenuItem.setOnClickListener(null);
            resideMenu.addMenuItem(resideMenuItem, ResideMenu.DIRECTION_RIGHT);
        }

       */

        mainMap = (ImageView)this.findViewById(R.id.main_map);

        int w = NumericHelper.getScreenWidth(MainActivity.this);

        int h = NumericHelper.dip2px(MainActivity.this, 250);

        Bitmap bitmap = ImgdspHelper.loadBitmap(R.drawable.china_blank, w, h, "map");

        mainMap.setImageBitmap(bitmap);


    }

    private void setMenuAbove(){
        //set add
        mainAdd = (ImageView)this.findViewById(R.id.main_add);
        int height = NumericHelper.dip2px(MainActivity.this, 30);
        Bitmap icon = ImgdspHelper.loadBitmap(R.drawable.add_icon_revised, height, height, "1");
        mainAdd.setImageBitmap(icon);
        mainAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("tarPath", "");

                Log.i("at debug", "next: start editor activity");
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });


        //set album
        mainAlbum = (TextView)this.findViewById(R.id.main_album);
        mainAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PhotoAlbumActivity.class));
            }
        });


        //set record
        mainRecord = (TextView)this.findViewById(R.id.main_record);
        mainRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecordListActivity.class));
            }
        });
    }

    /**
     * note: set drawer in the right side
     * <p>
     * note: assume that background image for drawer is fixed and of square shape
     * (may be modified later)
     * <p>
     * note: status bar color is set in this function
     *
     * @// TODO: 2016.10.18 Note: better to open the drawer first
     * @// TODO: then use asynchronous thread to loadFileContentToEditor image and other time
     * @// TODO: -consuming components
     */



    private void setDrawerRight() {

        ImageView drawer_figure = (ImageView) findViewById(R.id.main_drawer_user_figure);
        Bitmap drawer_fig_bmp = ImgdspHelper.loadBitmap(R.drawable.user_icon_1,
                drawer_figure.getLayoutParams().width, drawer_figure.getLayoutParams().width, "1");
        drawer_figure.setImageBitmap(drawer_fig_bmp);
        final RelativeLayout drawer_upper_layout = (RelativeLayout) findViewById(R.id.main_drawer_upper_layout);

        Bitmap drawer_fig_bg_bmp = ImgdspHelper.loadBitmap(R.drawable.wallpaper_5,
                NumericHelper.dip2px(MainActivity.this, 250), NumericHelper.dip2px(MainActivity.this, 250), "1");

        drawer_upper_layout.setBackground(new BitmapDrawable(drawer_fig_bg_bmp));
    }

    /**
     * note: set user's figure image in the main layout
     * and register actions
     * <p>
     * note: assume that image to be displayed is fixed and of square shape
     * (may be modified later)
     */

    private void setFigureMain() {
        main_figure = (ImageView) findViewById(R.id.main_figure);
        Bitmap bmp = ImgdspHelper.loadBitmap(R.drawable.user_icon_1,
                main_figure.getLayoutParams().height,
                main_figure.getLayoutParams().height, "2");
        main_figure.setImageBitmap(bmp);
        main_figure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerRightLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    private slideState getState(animStarter starter){
        if(starter == animStarter.CALENDAR){
            return curCalendarState;
        }else{
            return curLittleMapState;
        }
    }

    private void updateState(animStarter starter, slideState state){
        if(starter == animStarter.CALENDAR){
            curCalendarState = state;
        }else{
            curLittleMapState = state;
        }
    }


    /**
     * note: set and register actions for calendar
     * <p>
     * note: calendar won't present until specific action is invoked
     *
     * @// TODO: 2016.10.18  note: calendar resize needs further consideration
     */


    /**
     * note: initialize display layout
     */

    private void setDisplay(){

        mainDisplayLayout = (ScrollView)this.findViewById(R.id.main_display_layout);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        init();
        setMenuAbove();
        setDrawerRight();
        setFigureMain();
        setDisplay();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
