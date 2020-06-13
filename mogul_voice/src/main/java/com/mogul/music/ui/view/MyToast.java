package com.mogul.music.ui.view;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mogul.music.R;
import com.mogul.music.base.App;


public class MyToast {

    public static void showToast(String msg) {
        Toast toast = new Toast(App.context);
        //设置Toast显示位置，居中，向 X、Y轴偏移量均为0
        toast.setGravity(Gravity.CENTER, 0, 0);
        //获取自定义视图
        View view = LayoutInflater.from(App.context).inflate(R.layout.toast, null);
        TextView text = view.findViewById(R.id.tv_toast);
        //设置文本
        text.setText(msg);
        //设置视图
        toast.setView(view);
        //设置显示时长
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示
        toast.show();
    }
}
