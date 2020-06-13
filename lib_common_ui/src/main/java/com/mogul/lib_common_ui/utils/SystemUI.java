package com.mogul.lib_common_ui.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;


public class SystemUI {

    public static void fitSystemUI(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取顶层view
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}