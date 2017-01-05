package com.example.jeylnastoninfer.debug7.daemonAct;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.avos.avoscloud.LogUtil;
import com.example.jeylnastoninfer.debug7.R;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.DrawerHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.ImgdspHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.graphicsUtils.NumericHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.*;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.ImageInfo;
import com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils.DebugHelper;
import com.example.jeylnastoninfer.debug7.auxUtils.runtimeUtils.RuntimeDataManager;
import com.example.jeylnastoninfer.debug7.popmenu.PopMenu_AddTag;
import com.example.jeylnastoninfer.debug7.popmenu.PopMenu_Background;
import com.example.jeylnastoninfer.debug7.auxUtils.projectStructUtils.RichEditor;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorActivity extends AppCompatActivity{

    private final int REQUEST_IMAGE_CAPTURE = 1;

    private final int PICK_PIC = 2;

    private final int REQUEST_EXTERNAL_STORAGE = 3;

    private final String[] PERMISSIONS_STORAGE = {

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean isUnderline = false;
    private boolean isStrikethrough = false;
    private boolean isBold = false;
    private boolean isItalic = false;
    private int fontSz = 4;
    private int fontColor = Color.rgb(0x0,0,0);

    private TextView abortWorkBtn = null;

    private float mInsertedImgWidth, screenHeight;

    String mCurrentPhotoPath;

    private LinearLayout editTextLayout = null;

    private RecordFileInfo recordFile = new RecordFileInfo();

    private TextWatcher editTextWatcher;

    private RelativeLayout editTextActivityLayout;

    private boolean saveBtnClickable = true;

    private LinearLayout word_font;

    private TextView Choose_Font = null;

    private View buttonArray1 = null;

    private View buttonArray2 = null;

    private ArrayList<View> pageListBelow = null;

    private ViewPager viewPager = null;

    private TextView saveBtn = null;


    private String htmlContent = "";

    boolean wordfont_visiable = false;

    private Uri imageUri;

    private Map<Integer,Integer> mapcolor = new HashMap();

    private Map<Integer,Integer> mapsize = new HashMap<>();

    private ArrayList<String> tags = new ArrayList<String>();

    private ArrayList<String> newImgPaths = new ArrayList<String>();

    private ArrayList<String> initImgpaths = new ArrayList<String>();

    private ImageView boldShow = null;
    private ImageView italicShow = null;
    private ImageView strikethroughShow = null;
    private ImageView underlineShow = null;
    private TextView fontSzShow = null;
    private TextView locationShow = null;
    private TextView fontColorShow = null;

    private ProgressDialog dialog = null;

    View.OnClickListener choosecolor;

    View.OnClickListener choosesize;

    String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/down/";

    private RelativeLayout editViewMenuBar = null;

    private RichEditor richEditor = null;

    private void initShowCluster(){

        boldShow = (ImageView)this.findViewById(R.id.editor_act_show_bold);
        italicShow = (ImageView)this.findViewById(R.id.editor_act_show_italic);
        strikethroughShow = (ImageView)this.findViewById(R.id.editor_act_show_strikethrough);
        underlineShow = (ImageView)this.findViewById(R.id.editor_act_show_underline);
        fontSzShow = (TextView)this.findViewById(R.id.editor_act_show_font_sz);
        locationShow = (TextView)this.findViewById(R.id.editor_act_show_location);
        fontColorShow = (TextView)this.findViewById(R.id.editor_act_show_font_color);

        fontSzShow.setTextColor(getResources().getColor(R.color.pure_white));
        fontSzShow.setText(Integer.toString(jsFontSize2Px(4)));
        fontColorShow.setBackgroundTintList(ColorStateList.valueOf(fontColor));

        abortWorkBtn = (TextView)this.findViewById(R.id.editor_act_abort);
        abortWorkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorActivity.this).setTitle("丢弃")
                        .setMessage("确定要放弃保存吗").setPositiveButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                alertDialog.show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    public void setBackground(final int backgroundResId){


        ViewTreeObserver vto = editTextActivityLayout.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                editTextActivityLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int w = editTextActivityLayout.getWidth(), h = editTextActivityLayout.getHeight();

                Bitmap bitmap = ImgdspHelper.loadBitmap(backgroundResId, w, h, "editText");



                editTextActivityLayout.setBackground(new BitmapDrawable(bitmap));

            }
        });


        vto = richEditor.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                richEditor.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }


    private void setRichEditor(){

        richEditor.setPlaceholder("");

        richEditor.setBackgroundColor(0);

        richEditor.setFontSize(fontSz);

        richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                Log.i("debug", "this: on Text Changed");
                Log.i("debug", "text: " + text);
                Log.i("debug", "htmlContent: " + htmlContent);
                htmlContent = text;
            }
        });

    }

    private void setSaveButton(){

        if(!saveBtnClickable) return;
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveBtnClickable = false;

                InputMethodManager im = (InputMethodManager) richEditor.getContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                im.hideSoftInputFromWindow(richEditor.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorActivity.this).setTitle("保存")
                        .setMessage("确定要保存吗").setPositiveButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        saveBtnClickable = true;
                                        dialogInterface.dismiss();
                                    }
                                }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                onRecordToSave();
                            }
                        });
                alertDialog.show();

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.editor_activity);

        Log.i("at debug", "next: init");

        init();

        Log.i("at debug", "next: set rich editor");

        setRichEditor();

        Log.i("at debug", "next: set background");

        setBackground(R.drawable.letterpaper001);

        Log.i("at debug", "next: set menu below");

        setMenuBelow();

        Log.i("at debug", "next: set edit view");

        setEditView();

        Log.i("at debug", "next: init map");

        initMap();

        Log.i("at debug", "next: set save btn");

        setSaveButton();

        Log.i("at debug", "next: get intent");

        //TODO: delete the following test code

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String tarPath = bundle.get("tarPath").toString();

        Log.i("#debug", "tarpath is " + tarPath);

        if(!tarPath.equals("")){

            Log.i("#debug", "next: " + "loadfile content to editor");

            loadFileContentToEditor(tarPath);

        }

    }


    private void setMenuBelow(){

        final LayoutInflater inflater = getLayoutInflater();

        buttonArray1 = inflater.inflate(R.layout.editor_btn_array_1, null);

        buttonArray2 = inflater.inflate(R.layout.editor_btn_array_2, null);

        pageListBelow = new ArrayList<>();

        pageListBelow.add(buttonArray1);

        pageListBelow.add(buttonArray2);

        viewPager = (ViewPager) findViewById(R.id.ViewPager_btns);

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pageListBelow.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(pageListBelow.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(pageListBelow.get(position), 0);
                return pageListBelow.get(position);
            }

        });
    }


    private void setEditView() {

        ViewTreeObserver vto = editTextActivityLayout.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {

                richEditor.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int mImgViewWidth = richEditor.getWidth();

                screenHeight = editTextActivityLayout.getHeight();

                mInsertedImgWidth = mImgViewWidth * .75f;

            }
        });

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                float IMethodHeight = screenHeight / 2;

                int heightDiff = richEditor.getRootView().getHeight() - richEditor.getHeight();

                Log.i("#debugv", "root view is : " + richEditor.getRootView().toString());
                Log.i("#debugv", "root view h: " + richEditor.getRootView().getHeight());
                Log.i("#debugv", "rich edit h: " + richEditor.getHeight());

                if(heightDiff > IMethodHeight) {

                    viewPager.setVisibility(View.VISIBLE);

                    if(wordfont_visiable) {

                        word_font.setVisibility(View.VISIBLE);
                    }

                } else {

                    viewPager.setVisibility(View.GONE);

                    word_font.setVisibility(View.GONE);

                }
            }
        });

    }

    private int jsFontSize2Px(int fontSize){
        int ret = 18;
        switch (fontSize){
            case(1):
                ret = 10;
                break;
            case(2):
                ret = 14;
                break;
            case(3):
                ret = 16;
                break;
            case(4):
                ret = 18;
                break;
            case(5):
                ret = 24;
                break;
            case(6):
                ret = 32;
                break;
            case(7):
                ret = 48;
                break;
        }
        return ret;
    }

    public void initMap(){

        choosecolor = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int id = view.getId();
                int color = mapcolor.get(id);
                richEditor.setEditorFontColor(color);
                fontColor = color;
                fontColorShow.setBackgroundTintList(ColorStateList.valueOf(fontColor));
            }

        };

        choosesize = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
                int size = mapsize.get(id);
                richEditor.setFontSize(size);
                fontSz = jsFontSize2Px(size);
                fontSzShow.setText(Integer.toString(fontSz));
                fontSzShow.setTextColor(getResources().getColor(R.color.pure_white));
            }
        };

        mapcolor.put(R.id.black, Color.rgb(0,0,0));
        mapcolor.put(R.id.blue,Color.rgb(0,0,0xff));
        mapcolor.put(R.id.red,Color.rgb(0xff,0,0));
        mapcolor.put(R.id.yellow,Color.rgb(0xff,0xff,0));
        mapcolor.put(R.id.green,Color.rgb(0,0xff,0));
        mapcolor.put(R.id.white,Color.rgb(0xff,0xff,0xff));
        mapcolor.put(R.id.gray,Color.rgb(0x6,0x6,0x6));
        mapcolor.put(R.id.purple,Color.rgb(0xff,0x34,0xb3));
        mapcolor.put(R.id.brown,Color.rgb(0xcd,0x66,0x1d));
        mapcolor.put(R.id.lightblue,Color.rgb(0x63,0xb8,0xff));

        mapsize.put(R.id.Size1,1);
        mapsize.put(R.id.Size2,2);
        mapsize.put(R.id.Size3,3);
        mapsize.put(R.id.Size4,4);
        mapsize.put(R.id.Size5,5);
        mapsize.put(R.id.Size6,6);
        mapsize.put(R.id.Size7,7);


        //设置按钮监听
        for(int id : mapcolor.keySet()) {

            findViewById(id).setOnClickListener(choosecolor);

        }

        for (int id : mapsize.keySet()) {

            findViewById(id).setOnClickListener(choosesize);

        }

    }


    private void init() {

        File file = new File(local_file);
        if(!file.exists()){
            file.mkdir();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Window window = getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            DrawerHelper.setStatusBarColor(EditorActivity.this, R.color.pseduo_trans);
        }


        richEditor = (RichEditor) findViewById(R.id.richEditor);

        editTextLayout = (LinearLayout)this.findViewById(R.id.edit_text_layout);

        word_font = (LinearLayout)this.findViewById(R.id.WordFont);


        //note: initialize global layout
        editTextActivityLayout = (RelativeLayout)findViewById(R.id.edit_text_activity_layout);

        editViewMenuBar = (RelativeLayout)this.findViewById(R.id.edit_view_menu_bar);

        saveBtn = (TextView)this.findViewById(R.id.editor_act_save);


        initShowCluster();
    }

    /**
     * 拍照按钮的触发事件，调用系统的拍照activity，将拍的照片存起来，之后进入onActivityResult
     *
     * @param view
     */

    public void requestCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile("jpg");
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    /**
     * 选择图片的书法事件
     *
     * @param view
     */
    public void insertImg(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        Log.i("#debug", "start act for result :: pick_pic");

        startActivityForResult(intent, PICK_PIC);


    }

    /**
     * 选择字体按钮的触发事件，弹出选择菜单
     *
     * @param view
     */
    public void changeFontSizeOrColor(View view) {
        if(Choose_Font == null) {
            Choose_Font = (TextView) view;
        }
        if(wordfont_visiable) {
            word_font.setVisibility(View.GONE);
            wordfont_visiable = false;
            Choose_Font.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            word_font.setVisibility(View.VISIBLE);
            wordfont_visiable = true;
            Choose_Font.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public final void changeFontStyle(View v) {
        switch (v.getId()) {
            case R.id.Btn_underline:
                richEditor.setUnderline();
                isUnderline = !isUnderline;
                underlineShow.setVisibility(isUnderline ? View.VISIBLE : View.INVISIBLE);
                Log.i("#debugs", "underline");
                break;
            case R.id.Btn_strikethrough:
                richEditor.setStrikeThrough();
                isStrikethrough = !isStrikethrough;
                strikethroughShow.setVisibility(isStrikethrough ? View.VISIBLE : View.INVISIBLE);
                break;
            case R.id.Btn_italic:
                richEditor.setItalic();
                isItalic = !isItalic;
                italicShow.setVisibility(isItalic ? View.VISIBLE : View.INVISIBLE);
                break;
            case R.id.Btn_bold:
                richEditor.setBold();
                isBold = !isBold;
                boldShow.setVisibility(isBold ? View.VISIBLE : View.INVISIBLE);
                break;
        }
    }


    /**
     * 选择字体按钮的触发事件，弹出选择菜单
     *
     * @param view
     */
    public void changeBackground(View view) {
        PopMenu_Background popMenu_background = new PopMenu_Background(EditorActivity.this, this);
        // richEditor.setFocusable(false);
        editTextActivityLayout.requestFocus();
        InputMethodManager im = (InputMethodManager) richEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(richEditor.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        popMenu_background.showAtLocation(editTextActivityLayout, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
    }

    public void switchToNext(View view) {
        if(view.getId() == R.id.Btn_GotoLeft) {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            word_font.setVisibility(View.GONE);
            wordfont_visiable = false;
            if(Choose_Font != null) {
                Choose_Font.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        }
        else if(view.getId() == R.id.Btn_GotoRight) {
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
    }

    /**
     * @param view
     */
    public void addTag(View view) {
        PopMenu_AddTag popMenu_AddTag = new PopMenu_AddTag(EditorActivity.this, recordFile);
        //richEditor.setFocusable(false);
        editTextActivityLayout.requestFocus();
        InputMethodManager im = (InputMethodManager) richEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(richEditor.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        popMenu_AddTag.showAtLocation(editTextActivityLayout, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
    }

    public final void onRecordToSave(){

        Log.i("#debug", "next: save file");

        dialog = new ProgressDialog(EditorActivity.this);

        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        dialog.setCancelable(false);

        dialog.setCanceledOnTouchOutside(false);

        dialog.setIcon(R.drawable.ic_cloud_upload_black_24dp);

        dialog.setTitle("上传记录中...");

        dialog.setMax(100);

        dialog.setMessage("上传进度");

        dialog.show();





        ArrayList<String> finalImgPaths = new ArrayList<>();

        String getImgUrl = "(?<=<img.src=\").*?(?=\")";

        Pattern pattern = Pattern.compile(getImgUrl);

        Matcher matcher = pattern.matcher(htmlContent);

        while (matcher.find()) {

            finalImgPaths.add(matcher.group());

        }

        for(String imgPath : newImgPaths){
            if(!finalImgPaths.contains(imgPath)){
                boolean ok = new File(imgPath).delete();
                if(!ok){
                    Log.i("debug", "file delete failed");
                }
            }
        }

        increateUploadBy(10);

        DataSynchronizeHelper.uploadUsrRecordDateAndWriteLocally(EditorActivity.this,
                MainActivity.usrPhone, htmlContent, finalImgPaths);


    }

    public void increateUploadBy(int amount){
        dialog.incrementProgressBy(amount);
    }

    public final void onRecordSaved(boolean isOk){

        if(isOk){
            dialog.setMessage("记录同步完成");
        }else{
            dialog.setMessage("记录同步失败，请检查后再试");
            saveBtnClickable = true;
        }

        dialog.dismiss();

        dialog = null;

        if(isOk){
            finish();
        }

    }

    public void loadFileContentToEditor(String filePath) {

        Log.i("debug", "this: loadFileContentToEditor");

        Log.i("debug", "path: " + filePath);

        File file = new File(filePath);

        final int byteSize = 1024;

        byte[] bytes = new byte[byteSize];

        String htmlContent = "";

        try {

            if(!file.exists()) {

                boolean ok = file.createNewFile();

                if(!ok){
                    Log.i("debug", "create file failed");
                }

            }

            FileInputStream fis = new FileInputStream(file);

            while ( (fis.read(bytes, 0, byteSize)) != -1) {

                htmlContent += new String(bytes);

            }

            fis.close();

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        Log.i("#debug", "html content is " + htmlContent);
        richEditor.setHtml(htmlContent);
    }

    /**
     * 将图片添加到edittext
     * 若为选择图片事件的返回，还将选择的图片存起来
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("#debug", "next: on act result");

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_PIC) if (data == null) {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "not failed", Toast.LENGTH_SHORT).show();
                Log.i("#debug", "date fetch okay");
                Uri uri = getUri(data);
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                //copy to app area
                copyfile(new File(path));
                Log.i("#debug", "path: " + path);

                insertImgIntoView(mCurrentPhotoPath, "Picture");

                newImgPaths.add(mCurrentPhotoPath);
            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                File file = new File(mCurrentPhotoPath);
                if (!file.exists()) {
                    Toast.makeText(this, "failed to create photo", Toast.LENGTH_SHORT).show();
                }
                insertImgIntoView(mCurrentPhotoPath,"Photo");
                newImgPaths.add(mCurrentPhotoPath);
            }
        }
    }

    public Uri getUri(Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    private void insertImgIntoView(String url, String alt) {


        Log.i("#debug", "url" + url.toString());

        imageUri = Uri.fromFile(new File(url));

        Log.i("#debug", "is image uri null ? " + ((imageUri == null) ? "true" : "false"));

        Bitmap bitmap = getOriginalBitmap(imageUri);

        int imgWidth = bitmap.getWidth();

        int imgHeight = bitmap.getHeight();

        if (imgWidth >= mInsertedImgWidth) {
            imgHeight = (int) (imgHeight * mInsertedImgWidth / imgWidth);
            imgWidth = (int) mInsertedImgWidth;
        }

        richEditor.insertImage(url,alt,imgWidth,imgHeight);

    }

    private Bitmap getOriginalBitmap(Uri photoUri) {

        if (photoUri == null) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            ContentResolver conReslv = getContentResolver();
            // 得到选择图片的Bitmap对象
            bitmap = MediaStore.Images.Media.getBitmap(conReslv, photoUri);
        } catch (Exception e) {
            //Log.e( , "Media.getBitmap failed", e);
        }
        return bitmap;
    }

    /**
     * 根据时间创造一个file并返回
     *
     * @return file
     * @throws IOException
     */

    private File createImageFile(String extension){

        String filePath = LocalDataUtil.getUsrPhotoGalleryPath(MainActivity.usrPhone);

        filePath += System.currentTimeMillis() + "."
         + extension;

        File imageFile = new File(filePath);

        Log.i("#debug", "imageFile is null ? " + (imageFile == null ? "yes" : "no"));

        Log.i("#debug", "at create image file, filePath is " + filePath);

        Log.i("#debug", "image file absoulte path is " + imageFile.getAbsolutePath());

        try{

            if(!imageFile.exists()){

                boolean ok = imageFile.createNewFile();

                if(!ok){

                    Log.i("#debug", "image file create failed");

                }else{

                    Log.i("#debug", "image file newly created");
                }

            }else{

                Log.i("#debug", "image file already exists");
            }

            mCurrentPhotoPath = imageFile.getAbsolutePath();

            Log.i("#debug", "mCurrentPhotoPath is " + imageFile.getAbsolutePath());

        }catch (IOException e){

            e.printStackTrace();

            Log.i("#debug", "ioexception");

        }
        return imageFile;
    }

    /**
     * 在选择完图片后使用
     * 将图片复制到指定目录下
     *
     * @param srcFile
     */

    private String getFileExtension(File srcFile){

        String fileName = srcFile.getName();

        String extension = "";

        int i = fileName.lastIndexOf(".");

        if(i > -1 && i < fileName.length()){

            extension = fileName.substring(i+1);

        }

        return  extension;
    }

    private void copyfile(File srcFile) {

        File destFile = null;

        FileOutputStream fos = null;

        FileInputStream fis = null;

        byte[] buffer = new byte[4000];

        int count = 0;

        int onetime = 0;

        try {

            destFile = createImageFile(getFileExtension(srcFile));

        } catch (Exception e) {

            e.printStackTrace();

        }

        if (destFile != null) {

            try {

                fos = new FileOutputStream(destFile);

                fis = new FileInputStream(srcFile);

                while ((onetime = fis.read(buffer)) > 0) {

                    count += onetime;

                    fos.write(buffer, 0, onetime);

                }

                fis.close();

                fos.close();

            } catch (FileNotFoundException e) {

                Toast.makeText(this, "image file not find", Toast.LENGTH_SHORT);

                e.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}