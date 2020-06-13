package com.mogul.lib_common_ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mogul.lib_common_ui.utils.SystemUI;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentView());
        SystemUI.fitSystemUI(this);
        initView();
        initData();
    }

    protected abstract int setContentView();

    protected abstract void initView();

    protected abstract void initData();
}