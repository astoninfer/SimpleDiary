package com.example.astoninfer.demo3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;


/**
 * note: this class is for main interface of this application
 * note: this activity called by welcomeActivity
 */

public class MainActivity extends FragmentActivity{

    private Animation myAnimation_Translate;
    private DrawerLayout drawerRightLayout;
    private ImageView main_figure;
    private RelativeLayout rootLayout;
    private LinearLayout displayFragment;
    private ImageView mainAdd = null;
    private TextView mainAlbum = null;
    private TextView mainRecord = null;
    private volatile boolean startAnimationSet = false;

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

    /**
     * note: this is function is called on create of base activity to initialize
     * some tools-class for this application including
     * MemoryManager
     * AuxUtil
     * <p>
     * note: layout for root is also set in this function
     */

    private void init() {

        drawerRightLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        rootLayout = (RelativeLayout) findViewById(R.id.main_top_relative_layout);
        //displayFragment = (LinearLayout) findViewById(R.id.main_display_fragment);
        ImageLoader.init(MainActivity.this);

        int w = AuxUtil.getScreenWidth(this), h = AuxUtil.getScreenHeight(this);

        //Bitmap bmp = MemoryManager.loadBitmap(R.drawable.img016, w, h, 2);
        drawerRightLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.transparent));
        //drawerRightLayout.setBackground(new BitmapDrawable(bmp));
        drawerRightLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //drawerRightLayout.getBackground().setAlpha(50);
    }

    /**
     * note: this part for initialize buttons/texts/images in above menu
     * note: and register events
     *
     */

    private void setMenuAbove(){
        //set add
        mainAdd = (ImageView)this.findViewById(R.id.main_add);
        int height = AuxUtil.dip2px(MainActivity.this, 30);
        Bitmap icon = MemoryManager.loadBitmap(R.drawable.add_icon_revised, height, height, 1);
        mainAdd.setImageBitmap(icon);
        mainAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
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
     * @// TODO: then use asynchronous thread to load image and other time
     * @// TODO: -consuming components
     */

    private void setDrawerRight() {

        ImageView drawer_figure = (ImageView) findViewById(R.id.main_drawer_user_figure);
        Bitmap drawer_fig_bmp = MemoryManager.loadBitmap(R.drawable.user_icon_1, drawer_figure.getLayoutParams().width, drawer_figure.getLayoutParams().width, 1);
        drawer_figure.setImageBitmap(drawer_fig_bmp);
        final RelativeLayout drawer_upper_layout = (RelativeLayout) findViewById(R.id.main_drawer_upper_layout);

        Bitmap drawer_fig_bg_bmp = MemoryManager.loadBitmap(R.drawable.wallpaper_5, AuxUtil.dip2px(MainActivity.this, 250), AuxUtil.dip2px(MainActivity.this, 250), 1);
        drawer_upper_layout.setBackground(new BitmapDrawable(drawer_fig_bg_bmp));
        //following are configuration for drawer items contained
        ListView main_drawer_list_layout = (ListView) findViewById(R.id.main_drawer_list_layout);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ctype, android.R.layout.simple_list_item_1);
        main_drawer_list_layout.setAdapter(adapter);

        //color the status bar

        //disable gesture slide
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
        Bitmap bmp = MemoryManager.loadBitmap(R.drawable.user_icon_1,
                main_figure.getLayoutParams().height,
                main_figure.getLayoutParams().height, 2);
        main_figure.setImageBitmap(bmp);
        main_figure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerRightLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    /**
     * note: this is a function to control calendar animation
     *
     * action to do, CLOSE for TRY to close, OPEN for TRY to open
     *               <p>
     *               state rep diagram
     *               CalenderState       dateCombinedResetValue     ResponseForOpen :: Action       ResponseForClose :: Action
     *               Closed                 true                 approve :: invalidate btn         reject
     *               Opening                true                        reject                     reject
     *               OpeningDone          true->false                    reject                     reject (validate btn)
     *               Open                   false                       reject                     approve :: invalidate btn
     *               Closing                false                       reject                     reject
     *               ClosingDone         false->true                    reject                     reject (validate btn)
     */

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

    private void preAnimation(){
        main_figure.setClickable(false);
    }

    private void postAnimation(){
        startAnimationSet = false;
        main_figure.setClickable(true);
    }

    private void postAnimation(slideState state){
        startAnimationSet = false;
        if(state == slideState.CLOSED){
            main_figure.setClickable(true);
        }
    }

    private void startAnim(final animStarter starter, slideAnimAction action){


        if(startAnimationSet) return;
        //cannot enter until the previous one exit
        startAnimationSet = true;
        preAnimation();


        slideAnimAction nextAction = action;
        final LinearLayout starterLayout;
        final LinearLayout triggerLayout;
        //only use synchronization when write

        slideState state = getState(starter);
        if(starter == animStarter.CALENDAR){
            starterLayout = (LinearLayout)findViewById(R.id.calendarView_layout);
            if(curLittleMapState != slideState.CLOSED){
                postAnimation();
                return;
            }
        }else{
            starterLayout = (LinearLayout)findViewById(R.id.littleMapViewLayout);
            if(curCalendarState != slideState.CLOSED){
                postAnimation();
                return;
            }
        }

        if (state == slideState.OPENING ||
                state == slideState.CLOSING ||
                state == slideState.CLOSED &&
                        action == slideAnimAction.CLOSE ||
                state == slideState.OPEN &&
                        action == slideAnimAction.OPEN) {
            postAnimation();
            return;
        }

        if (action == slideAnimAction.REVERSE) {
            nextAction = state == slideState.OPEN ? slideAnimAction.CLOSE :
                    slideAnimAction.OPEN;
            if (nextAction == slideAnimAction.OPEN) updateState(starter, slideState.OPENING);
            else updateState(starter, slideState.CLOSING);
        }

        if(starter == animStarter.CALENDAR){
            triggerLayout = (LinearLayout)findViewById(R.id.main_date_layout);
        }else{
            triggerLayout = (LinearLayout)findViewById(R.id.main_location_layout);
        }

        myAnimation_Translate = nextAction == slideAnimAction.OPEN ?
                new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 1f,
                        Animation.RELATIVE_TO_SELF, 0f
                ) : new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f
        );
        myAnimation_Translate.setDuration(400);
        myAnimation_Translate.setInterpolator(AnimationUtils.loadInterpolator(
                MainActivity.this,
                android.R.anim.accelerate_decelerate_interpolator
        ));

        final LinearLayout menu_below = (LinearLayout) findViewById(R.id.main_menu_below);
        final RelativeLayout _root_layout = (RelativeLayout) findViewById(R.id.main_top_relative_layout);
        //curCalendarState should be safely visited here


        if(getState(starter) == slideState.OPENING){
            starterLayout.setVisibility(View.VISIBLE);
        }


        myAnimation_Translate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                _root_layout.bringChildToFront(menu_below);

                if(getState(starter) == slideState.OPENING) {
                    //triggerLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                    triggerLayout.setBackgroundResource(R.color.pure_white);
                    triggerLayout.getBackground().setAlpha(90);
                }else{
                    triggerLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                /*
                if(getState(starter) == slideState.OPENING){
                    starterLayout.setVisibility(View.VISIBLE);
                }
                */
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                slideState state = getState(starter);

                if (state == slideState.OPENING) {
                    state = slideState.OPEN;
                } else {
                    state = slideState.CLOSED;
                    starterLayout.setVisibility(View.GONE);
                }
                if(state == slideState.CLOSED){
                    //triggerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    //triggerLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                }

                updateState(starter, state);
                postAnimation(state);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        starterLayout.startAnimation(myAnimation_Translate);
    }


    private void startCalendarAnim(final slideAnimAction action) {
        final LinearLayout calendarLayout = (LinearLayout)findViewById(R.id.calendarView_layout);

        slideAnimAction nextAction = action;

        synchronized (curCalendarState) {
            if (curCalendarState == slideState.OPENING ||
                    curCalendarState == slideState.CLOSING ||
                    curCalendarState == slideState.CLOSED &&
                            action == slideAnimAction.CLOSE ||
                    curCalendarState == slideState.OPEN &&
                    action == slideAnimAction.OPEN) {
                return;
            }
            if (action == slideAnimAction.REVERSE) {
                nextAction = curCalendarState == slideState.OPEN ? slideAnimAction.CLOSE :
                        slideAnimAction.OPEN;
                if (nextAction == slideAnimAction.OPEN) curCalendarState = slideState.OPENING;
                else curCalendarState = slideState.CLOSING;
            }
        }
        final LinearLayout dateLayout = (LinearLayout)findViewById(R.id.main_date_layout);


        myAnimation_Translate = nextAction == slideAnimAction.OPEN ?
                new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 1f,
                        Animation.RELATIVE_TO_SELF, 0f
                ) : new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f
        );
        myAnimation_Translate.setDuration(400);
        myAnimation_Translate.setInterpolator(AnimationUtils.loadInterpolator(
                MainActivity.this,
                android.R.anim.accelerate_decelerate_interpolator
        ));
        final LinearLayout menu_below = (LinearLayout) findViewById(R.id.main_menu_below);
        final RelativeLayout _root_layout = (RelativeLayout) findViewById(R.id.main_top_relative_layout);
        //curCalendarState should be safely visited here
        if(curCalendarState == slideState.OPENING){
             calendarLayout.setVisibility(View.VISIBLE);
        }
        myAnimation_Translate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                _root_layout.bringChildToFront(menu_below);
                if(curCalendarState == slideState.OPENING) {
                    dateLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (curCalendarState == slideState.OPENING) {
                    curCalendarState = slideState.OPEN;
                } else {
                    curCalendarState = slideState.CLOSED;
                    calendarLayout.setVisibility(View.GONE);
                }
                if(curCalendarState == slideState.CLOSED){
                    dateLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        calendarLayout.startAnimation(myAnimation_Translate);
    }


    /**
     * note: set and register actions for calendar
     * <p>
     * note: calendar won't present until specific action is invoked
     *
     * @// TODO: 2016.10.18  note: calendar resize needs further consideration
     */

    private void setCalendar() {
        curCalendarState = slideState.CLOSED;
        final LinearLayout dateLayout = (LinearLayout) findViewById(R.id.main_date_layout);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View myCalendarView = inflater.inflate(R.layout.main_calendar_view, null);
        final LinearLayout calendarLayout = (LinearLayout) myCalendarView
                .findViewById(R.id.calendarView_layout);
        float width = AuxUtil.getScreenWidth(MainActivity.this), height = AuxUtil.getScreenWidth(MainActivity.this) * .87f;
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
                (int)width, (int)height
        );
        lps.addRule(RelativeLayout.ABOVE, R.id.main_menu_below);
        calendarLayout.setLayoutParams(lps);
        calendarLayout.setBackgroundColor(ContextCompat.getColor(
                getBaseContext(),
                R.color.transparent
        ));
        calendarLayout.setVisibility(View.GONE);
        rootLayout.addView(calendarLayout);


        //register actions
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try to reverse calendar state
                startAnim(animStarter.CALENDAR, slideAnimAction.REVERSE);
            }
        });

        mcv = (MaterialCalendarView)findViewById(R.id.calendarView);
        mcv.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        mcv.setTileWidth((int)(height / 8));
        mcv.setTileWidth((int)(width / 7));
        mcv.setPadding(5, 5, 5, 5);
        mcv.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return day.getYear() + "年 " + monthNameChinese[day.getMonth()] + "月";
            }
        });
        mcv.setArrowColor(getResources().getColor(R.color.transparent));
        //mcv.setArrowColor(getResources().getColor(R.color.colorPrimaryDark));
        mcv.setBackgroundResource(R.color.pure_white);
        mcv.getBackground().setAlpha(90);
    }

    /**
     * note: initialize display layout
     */

    private void setDisplay(){
        /*
        displayFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    startAnim(animStarter.CALENDAR, slideAnimAction.CLOSE);
            //    startAnim(animStarter.LITTLE_MAP, slideAnimAction.CLOSE);
            }
        });

    */
/*
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.main_display_fragment);

        if(fragment == null){
            fragment = new ImageGridFragment();
            fm.beginTransaction()
                    .add(R.id.main_display_fragment, fragment)
                    .commit();
        }
        */
    }

    private void setLittleMap(){

        final LinearLayout locationLayout = (LinearLayout)this
                .findViewById(R.id.main_location_layout);


        curLittleMapState = slideState.CLOSED;

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View myLittleMapView = inflater.inflate(R.layout.main_little_map_view, null);

        float heightWidthRatio = 939.6f / 1080.0f;
        float width = AuxUtil.getScreenWidth(MainActivity.this);
        float height = width * heightWidthRatio;

        Bitmap bmp = MemoryManager
                .loadBitmap(R.drawable.china_blank, (int)width, (int)height, 1);

        Bitmap bitmap = AuxUtil.convertToMutable(bmp);

        final LinearLayout myLittleMapLayout = (LinearLayout)myLittleMapView
                .findViewById(R.id.littleMapViewLayout);

        RelativeLayout.LayoutParams lps = new RelativeLayout
                .LayoutParams((int)width, (int)height);

        lps.addRule(RelativeLayout.ABOVE, R.id.main_menu_below);

        myLittleMapLayout.setLayoutParams(lps);
        //TODO: do we need to set background color here?
        myLittleMapLayout.setBackgroundColor(ContextCompat.getColor(
                getBaseContext(), R.color.transparent));


        //-----------------------LITTLE MAP LOGIC BG--------------------------
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorPrimary));

        //origin map size(C1600, R1280)
        //dest size(width, height)
        //coordinate of Beijing: (C1095, R520)
        //location of Beijing in dest: (C1095 * width / 1600, R520 * height / 1280)

        int bitmapW = bitmap.getWidth(), bitmapH = bitmap.getHeight();
        float dx = 1095f / 1600f * bitmapW, dy = 520f / 1280f * bitmapH;

        //canvas.drawCircle(bitmapW, bitmapH, 50, paint);
        canvas.drawCircle(dx, dy, 5, paint);

        canvas.save(Canvas.ALL_SAVE_FLAG);



        ImageView myLittleMap = (ImageView)myLittleMapView.findViewById(R.id.littleMapView);
        myLittleMap.setImageBitmap(bitmap);
        //-----------------------LITTLE MAP LOGIC ED--------------------------

        myLittleMapLayout.setVisibility(View.GONE);
        rootLayout.addView(myLittleMapLayout);

        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try to reverse littleMap state
                startAnim(animStarter.LITTLE_MAP, slideAnimAction.REVERSE);
            }
        });


    }

    private void setMenuBelow(){
        setLittleMap();
        setCalendar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setMenuAbove();
        setDrawerRight();
        setFigureMain();
        setMenuBelow();
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
