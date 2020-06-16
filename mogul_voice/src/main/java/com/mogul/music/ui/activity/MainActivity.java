package com.mogul.music.ui.activity;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.mogul.lib_common_ui.base.BaseActivity;
import com.mogul.music.R;
import com.mogul.music.adapter.HomePageAdapter;
import com.mogul.music.ui.fragment.FindFragment;
import com.mogul.music.ui.fragment.FriendFragment;
import com.mogul.music.ui.fragment.MineFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    //初始化view
    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private TextView mToggleView;
    private TextView mSearchView;
    private RadioGroup mRadioGroup;

    private HomePageAdapter mHomePageAdapter;

    private List<Fragment> mFragmentList;

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mViewPager = findViewById(R.id.view_pager);
        mToggleView = findViewById(R.id.toggle_view);
        mSearchView = findViewById(R.id.search_view);
        mRadioGroup = findViewById(R.id.radio_group);


        initFragments();
        initViewPager();
        initListener();

    }

    private void initFragments() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new MineFragment());
        mFragmentList.add(new FindFragment());
        mFragmentList.add(new FriendFragment());
    }

    /**
     * 设置Viewpager
     */
    private void initViewPager() {
        mHomePageAdapter = new HomePageAdapter(getSupportFragmentManager(), mFragmentList, 1);
        mViewPager.setAdapter(mHomePageAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mRadioGroup.check(R.id.find_tab);
    }

    /**
     * 设置Viewpager滑动监听
     */
    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(position);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 设置RadioGroup监听
     */
    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    };

    /**
     * 设置监听
     */
    private void initListener() {
        mToggleView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        ToggleButton button = new ToggleButton(this);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_view:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.search_view:
                break;
        }
    }
}