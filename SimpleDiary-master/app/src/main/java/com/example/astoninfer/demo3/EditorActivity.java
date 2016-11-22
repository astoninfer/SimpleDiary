package com.example.astoninfer.demo3;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.astoninfer.demo3.popmenu.PopMenu_AddTag;
import com.example.astoninfer.demo3.popmenu.PopMenu_Background;
import com.example.astoninfer.demo3.popmenu.PopMenu_Save;
import com.example.astoninfer.demo3.richeditor.RichEditor;

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

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class EditorActivity extends SwipeBackActivity implements SwipeBackLayout.SwipeListener {
    public static final String DATABASE = "recordsaver";
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int PICK_PIC = 2;
    private final int REQUEST_EXTERNAL_STORAGE = 3;
    private final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private float mInsertedImgWidth,screenheight;
    String mCurrentPhotoPath;
    private LinearLayout editTextLayout = null;
    private RecordFile recordFile = new RecordFile();
    private TextWatcher editTextWatcher;
    private RelativeLayout editTextActivityLayout;
    private LinearLayout word_font;
    private TextView Choose_Font = null;
    private View buttons1,buttons2;
    private ArrayList<View> pageListBelow;
    private ViewPager viewPager;
    private String html = "";
    boolean wordfont_visiable = false;
    private Uri imageUri;
    private Map<Integer,Integer> mapcolor = new HashMap();
    private Map<Integer,Integer> mapsize = new HashMap<>();
    private ArrayList<String> tags = new ArrayList<String>();
    private ArrayList<String> newImgPaths = new ArrayList<String>();
    private ArrayList<String> initImgpaths = new ArrayList<String>();
    private DataBaseHelper dbhelper = new DataBaseHelper(this,DATABASE);
    View.OnClickListener choosecolor;
    View.OnClickListener choosesize;
    String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/down/";

    RichEditor richEditor;

    /********************************************************************
     *
     *
     *
     */
    private boolean userPreferenceSaveOnExit = false;

    public void onScrollStateChange(int state, float scrollPercent){

    }

    public void onEdgeTouch(int edgeFlag){
        //saveedit();
    }

    public void onScrollOverThreshold(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_activity_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        initricheditor();
        init();
        Intent intent = getIntent();
        String loadpath = intent.getStringExtra(MainActivity.PATH_MESSAGE);
        if(loadpath != "") {
            load(loadpath);
        }
        getSwipeBackLayout().addSwipeListener(this);
    }

    private void initricheditor() {
        richEditor = (RichEditor) findViewById(R.id.richEditor);
        richEditor.setPlaceholder("Yours...");
        richEditor.setBackgroundColor(0);
        richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                html = text;
            }
        });
    }

    public void WordSetting(View v) {
        switch (v.getId()) {
            case R.id.Btn_underline:
                richEditor.setUnderline();
                break;
            case R.id.Btn_strikethrough:
                richEditor.setStrikeThrough();
                break;
            case R.id.Btn_italic:
                richEditor.setItalic();
                break;
            case R.id.Btn_bold:
                richEditor.setBold();
                break;

        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ?
                        Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * note: this part is for initializing menu below
     * note: including fragments: camera, tag, etc.
     *
     */
    private void setMenuBelow(){
        final LayoutInflater inflater = getLayoutInflater();
        buttons1 = inflater.inflate(R.layout.buttons1,null);
        buttons2 = inflater.inflate(R.layout.buttons2,null);


        pageListBelow = new ArrayList<>();
        pageListBelow.add(buttons1);
        pageListBelow.add(buttons2);

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
                int mImgViewWidth;
                richEditor.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mImgViewWidth = richEditor.getWidth();
                screenheight = editTextActivityLayout.getHeight();
                mInsertedImgWidth = mImgViewWidth * .68f;
            }
        });

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float keyheight = screenheight / 2;
                int heightDiff = richEditor.getRootView().getHeight() - richEditor.getHeight();
                if(heightDiff > keyheight) {
                    viewPager.setVisibility(View.VISIBLE);
                    if(wordfont_visiable) {
                        word_font.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    viewPager.setVisibility(View.GONE);
                    word_font.setVisibility(View.GONE);
                }
            }
        });
    }

    public void initmap(){

        choosecolor = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                int color = mapcolor.get(id);
                setColor(color);
            }
        } ;

        choosesize = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                int size = mapsize.get(id);
                setSize(size);
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

        mapsize.put(R.id.Size1,7);
        mapsize.put(R.id.Size2,6);
        mapsize.put(R.id.Size3,5);
        mapsize.put(R.id.Size4,4);
        mapsize.put(R.id.Size5,3);
        mapsize.put(R.id.Size6,2);
        mapsize.put(R.id.Size7,1);


        //设置按钮监听
        for(int id : mapcolor.keySet()) {
            findViewById(id).setOnClickListener(choosecolor);
        }

        for (int id : mapsize.keySet()) {
            findViewById(id).setOnClickListener(choosesize);
        }

        //setBlur();
    }

    /**
     * note: this part is for global initialization
     * note: items list as below
     * note: 1 :: set status bar color
     * note: 2 :: ask for permission to write SD
     * note: 3 :: set menu above
     * note: 4 :: set global layout
     * note: 5 :: set edit view
     */
    private void init() {

        //note: set status bar color
        AuxUtil.setStatusBarColor(this, R.color.colorPrimaryDark);

        //note: ask permission to write to SD Card
        int permission_storage = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission_storage != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        editTextLayout = (LinearLayout)this.findViewById(R.id.edit_text_layout);
        word_font = (LinearLayout)this.findViewById(R.id.WordFont);
        richEditor =(RichEditor)this.findViewById(R.id.richEditor);
        //richEditor.setBackgroundColor(getResources().getColor(R.color.clay_blue));
        //richEditor.setB

        //note: initialize global layout
        editTextActivityLayout = (RelativeLayout)findViewById(R.id.edit_text_activity_layout);

        setBG(R.drawable.bj2);

        setMenuBelow();

        setEditView();

        initmap();


    }

    /**
     * 拍照按钮的触发事件，调用系统的拍照activity，将拍的照片存起来，之后进入onActivityResult
     *
     * @param view
     */
    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT);
            }
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
    public void choosepicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PIC);
    }

    /**
     * 选择字体按钮的触发事件，弹出选择菜单
     *
     * @param view
     */
    public void choosewordtype(View view) {
        if(Choose_Font == null) {
            Choose_Font = (TextView) view;
        }
        if(wordfont_visiable) {
            word_font.setVisibility(View.GONE);
            wordfont_visiable = false;
            Choose_Font.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            word_font.setVisibility(View.VISIBLE);
            wordfont_visiable = true;
            Choose_Font.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /**
     * 选择字体按钮的触发事件，弹出选择菜单
     *
     * @param view
     */
    public void chooseBG(View view) {
        PopMenu_Background popMenu_background = new PopMenu_Background(EditorActivity.this, this);
       // richEditor.setFocusable(false);
        editTextActivityLayout.requestFocus();
        InputMethodManager im = (InputMethodManager) richEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(richEditor.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        popMenu_background.showAtLocation(editTextActivityLayout, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
    }

    public void gotoleorri(View view) {
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

    /**
     * Save按钮的触发事件
     * @param
     */

    public void saveedit(View view) {
        PopMenu_Save popMenu_save = new PopMenu_Save(EditorActivity.this, this, getSwipeBackLayout());
        editTextActivityLayout.requestFocus();
        InputMethodManager im = (InputMethodManager) richEditor.getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(richEditor.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        popMenu_save.showAtLocation(editTextActivityLayout,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
    }


    public void savetodb(String title,String date,String address) {
        String path = savefile();
        String s = "";
        ArrayList<String> finalImgPaths = new ArrayList<String>();
        String getimgurl = "(?<=<img.src=\").*?(?=\")";
        Pattern pattern = Pattern.compile(getimgurl);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            finalImgPaths.add(matcher.group().toString());
        }
        for(int i = 0;i < newImgPaths.size();i ++){
            if(!finalImgPaths.contains(newImgPaths.get(i))) {
                File file1 = new File(newImgPaths.get(i));
                file1.delete();
            }
        }
        recordFile.settitle(title);
        recordFile.setAddress(address);
        recordFile.setDate(date);
        recordFile.setpath(path);
        for(int i = 0;i < finalImgPaths.size();i ++) {
            ImageInfo info = new ImageInfo(finalImgPaths.get(i),"","");
            recordFile.addimginfo(info);
        }
        dbhelper.addRecord(recordFile);
        richEditor.setHtml("");
        load(path);
    }


    public String savefile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String f;
        f = local_file + "/" + timeStamp + ".txt";
        File file = new File(f);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(html.getBytes());
            fos.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
//        richEditor.setHtml("");
//        load(f);
        return f;
    }

    public void load(String path) {
        File file = new File(path);
        byte[] bts = new byte[1000];
        String html = "";
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(file);
            while ( (fis.read(bts,0,1000)) != -1) {
                html += new String(bts);
            }
            fis.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        richEditor.setHtml(html);
//        String fileinfos = "";
//        ArrayList<RecordFile> recordFiles = dbhelper.getAllRecord();
//        for(int i = 0;i < recordFiles.size();i++) {
//            String s = "";
//            RecordFile rf = recordFiles.get(i);
//            s += "title: " + rf.gettitle() + "\n";
//            s += "date: " + rf.getDate() + " address: " + rf.getAddress() + "\n";
//            s += "path: " + rf.getpath() + "\n";
//            fileinfos += s;
//        }
//        richEditor.setHtml(fileinfos);
    }

    public void back(View view) {
        finish();
        //onBackPressed();
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_PIC) if (data == null) {
                Toast.makeText(this, "failedddddddddddd", Toast.LENGTH_SHORT).show();
            } else {
                Uri uri = geturi(data);
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                //copy to app area
                copyfile(new File(path));
                InsertImgintoView(mCurrentPhotoPath,"Picture");
                newImgPaths.add(mCurrentPhotoPath);
            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                File file = new File(mCurrentPhotoPath);
                if (!file.exists()) {
                    Toast.makeText(this, "failed to create photo", Toast.LENGTH_SHORT).show();
                }
                InsertImgintoView(mCurrentPhotoPath,"Photo");
                newImgPaths.add(mCurrentPhotoPath);
            }
        }
    }

    public Uri geturi(Intent intent) {
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

    private void InsertImgintoView(String url, String alt) {
        imageUri = Uri.fromFile(new File(url));
        Bitmap bitmap = getOriginalBitmap(imageUri);
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        if (imgWidth >= mInsertedImgWidth) {
            imgHeight = (int) (imgHeight * mInsertedImgWidth / imgWidth);
            imgWidth = (int) mInsertedImgWidth;
        }
        richEditor.insertImage(url,alt,imgWidth,imgHeight);
    }

    private SpannableString getBitmapMime(Bitmap pic, Uri uri) {
        int imgWidth = pic.getWidth();
        int imgHeight = pic.getHeight();
        // 只对大尺寸图片进行下面的压缩，小尺寸图片使用原图
        if (imgWidth >= mInsertedImgWidth) {
            float scale = (float) mInsertedImgWidth / imgWidth;
            Matrix mx = new Matrix();
            mx.setScale(scale, scale);
            pic = Bitmap.createBitmap(pic, 0, 0, imgWidth, imgHeight, mx, true);
        }
        String smile = uri.getPath();
        SpannableString ss = new SpannableString(smile);
        ImageSpan span = new ImageSpan(this, pic);
        ss.setSpan(span, 0, smile.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
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
    private File createImageFile() throws IOException {
        String filepath;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File f = new File(local_file, "/image/");
        if (!f.exists()) {
            f.mkdirs();
        }
        filepath = f.getAbsolutePath() + '/' + timeStamp + ".jpg";
        File image = new File(filepath);
        if (!image.exists()) {
            image.createNewFile();
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
        }
        return image;
    }

    /**
     * 在选择完图片后使用
     * 将图片复制到指定目录下
     *
     * @param file
     */
    private void copyfile(File file) {
        File aimfile = null;
        FileOutputStream fos = null;
        FileInputStream fis = null;
        byte[] buffer = new byte[4000];
        int count = 0;
        int onetime = 0;
        try {
            aimfile = createImageFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (aimfile != null) {
            try {
                fos = new FileOutputStream(aimfile);
                fis = new FileInputStream(file);
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

    public void setSize(int size) {
        richEditor.setFontSize(size);
    }

    public void setColor(int color) {
        richEditor.setTextColor(color);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setBG(final int id) {
        ViewTreeObserver vto = editTextActivityLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                editTextActivityLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int w = editTextActivityLayout.getWidth(),
                        h = editTextActivityLayout.getHeight();
                Bitmap bitmap = MemoryManager.loadBitmap(id, w, h, 10010);
                bitmap = AuxUtil.blurBitmapByView(getBaseContext(), bitmap, 5);
                editTextActivityLayout.setBackground(new BitmapDrawable(bitmap));
                //richEditor.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                //richEditor.getBackground().setAlpha(50);
            }
        });
        vto = richEditor.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                richEditor.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                richEditor.setBackgroundColor(getResources().getColor(R.color.glass_mask));
                //richEditor.getBackground().setAlpha(50);
            }
        });
    }

}
