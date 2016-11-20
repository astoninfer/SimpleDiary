package com.example.astoninfer.demo3.popmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.astoninfer.demo3.FlowLayout;
import com.example.astoninfer.demo3.R;
import com.example.astoninfer.demo3.RecordFile;

import java.util.ArrayList;
import java.util.LinkedList;


public class PopMenu_AddTag extends PopupWindow {
    private View mMenuView;
    private Button btn_add;
    private EditText edit_tag;
    private FlowLayout layout_tags;
    private Activity context;
    ArrayList<String> taglist = new ArrayList<>();
    private int max_tag_num = 5;
    RecordFile recordFile;
    int i = 0;
    public PopMenu_AddTag(final Activity context, final RecordFile recordFile) {
        super(context);
        this.context = context;
        this.recordFile = recordFile;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.add_tag, null);
        layout_tags = (FlowLayout) mMenuView.findViewById(R.id.layout_tags);
        btn_add = (Button) mMenuView.findViewById(R.id.btn_add);
        edit_tag = (EditText) mMenuView.findViewById(R.id.edit_tags);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                //layoutParams.setMargins(0, 20, 40, 20);
                String s = edit_tag.getText().toString();
                if(s.length()!=0) {
                    if(taglist.size() == max_tag_num) {
                        Toast.makeText(context,"max 5 tag",Toast.LENGTH_SHORT);
                    }
                    else {
                        taglist.add(s);
                        recordFile.addTag(s);
                        edit_tag.setSelection(0,edit_tag.getText().toString().length());
                        initLayout();
                        edit_tag.setFocusable(true);
                        edit_tag.setFocusableInTouchMode(true);
                        edit_tag.requestFocus();
                    }
                }

            }
        });
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.tag_panel).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        taglist = recordFile.gettags();
        initLayout();
    }


    private void initLayout() {
        layout_tags.removeAllViewsInLayout();

        final  TextView[] textViews = new TextView[taglist.size()];
        final  TextView[] icons = new TextView[taglist.size()];

        for(int i = 0;i < taglist.size();i ++) {
            final View view = (View) ((LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.tag, layout_tags, false);
            final TextView text = (TextView) view.findViewById(R.id.text);  //查找  到当前     textView
            final TextView icon = (TextView) view.findViewById(R.id.delete_icon);  //查找  到当前  删除小图标

            // 将     已有标签设置成      可选标签
            text.setText(taglist.get(i));
            /**
             * 将当前  textView  赋值给    textView数组
             */
            textViews[i] = text;
            icons[i] = icon;

            //设置    单击事件：
            icon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //遍历  图标  删除 当前  被点击项
                    for(int j = 0; j < icons.length;j++){
                        if(icon.equals(icons[j])){  //获取   当前  点击删除图标的位置：
                            layout_tags.removeViewAt(j);
                            taglist.remove(j);
                            recordFile.removetag(j);
                            initLayout();
                        }
                    }
                }
            });

            text.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    text.setActivated(!text.isActivated()); // true是激活的

                    if (text.isActivated()) {
//                        boolean bResult = doAddText(taglist.get(pos), false, pos);
//                        text.setActivated(bResult);
                        //遍历   数据    将图标设置为可见：
                        for(int j = 0;j< textViews.length;j++){
                            if(text.equals(textViews[j])){//非当前  textView
                                icons[j].setVisibility(View.VISIBLE);
                                text.setText(taglist.get(j) + "       ");
                            }
                        }
                    }else{
                        for(int j = 0;j< textViews.length;j++){
                            icons[j].setVisibility(View.GONE);
                            text.setText(taglist.get(j));
                        }
                    }

                    /**
                     * 遍历  textView  满足   已经被选中     并且不是   当前对象的textView   则置为  不选
                     */
                    for(int j = 0;j< textViews.length;j++){
                        if(!text.equals(textViews[j])){//非当前  textView
                            textViews[j].setActivated(false); // true是激活的
                            icons[j].setVisibility(View.GONE);
                            textViews[j].setText(taglist.get(j));
                        }
                    }
                }
            });
            layout_tags.addView(view);

        }
    }

}
