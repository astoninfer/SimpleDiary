package com.example.astoninfer.demo3.popmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.astoninfer.demo3.EditorActivity;
import com.example.astoninfer.demo3.R;

import me.imid.swipebacklayout.lib.SwipeBackLayout;


public class PopMenu_Save extends PopupWindow {
    private Activity context;
    private EditorActivity editorActivity;
    private View mMenuView;
    private EditText save_edit;
    private TextView datetext,addresstext,savetext,canceltext;
    public PopMenu_Save(Activity context, final EditorActivity editorActivity, final SwipeBackLayout swipeBackLayout) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.editorActivity = editorActivity;
        this.context = context;

        mMenuView = inflater.inflate(R.layout.edit_save, null);

        save_edit = (EditText) mMenuView.findViewById(R.id.save_edit);
        datetext = (TextView) mMenuView.findViewById(R.id.save_date);
        addresstext = (TextView) mMenuView.findViewById(R.id.save_address);
        savetext = (TextView) mMenuView.findViewById(R.id.save_save);
        canceltext = (TextView) mMenuView.findViewById(R.id.save_cancle);

        savetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = save_edit.getText().toString();
                String date = datetext.getText().toString();
                String address = addresstext.getText().toString();
                editorActivity.savetodb(s,date,address);
                swipeBackLayout.setEnableGesture(true);
            }
        });

        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                swipeBackLayout.setEnableGesture(true);
            }
        });

        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.edit_save_window).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public static void saveImmediate(EditorActivity activity){
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mMenuView = inflater.inflate(R.layout.edit_save, null);
        EditText save_edit = (EditText) mMenuView.findViewById(R.id.save_edit);
        String s = save_edit.getText().toString();
        String date = "DATE";
        String addr = "ADDRESS";
        activity.savetodb(s, date, addr);
    }
}
