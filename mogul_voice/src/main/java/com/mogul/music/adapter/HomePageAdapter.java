package com.mogul.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;


public class HomePageAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;

    public HomePageAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList, int behavior) {
        super(fm, behavior);
        this.mFragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}