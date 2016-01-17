package com.example.buging.graffcity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ScreenSlidePagerAdapterFil extends FragmentStatePagerAdapter {
    private List<String> picList = new ArrayList<>();

    public ScreenSlidePagerAdapterFil(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        return ScreenSlidePageFragmentFil.newInstance(picList.get(i));
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    public void addAll(List<String> picList) {
        this.picList = picList;
    }
}
