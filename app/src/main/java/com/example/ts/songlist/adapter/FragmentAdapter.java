package com.example.ts.songlist.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by ts on 17-12-20.
 * A fragmentAdapter used to adapter fragment
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private List<String> titleList;

    public FragmentAdapter(FragmentManager fm, List<Fragment> mFragments, List<String> titleList) {
        super(fm);
        this.titleList = titleList;
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {//必须实现
        return mFragments.get(position);
    }

    @Override
    public int getCount() {//必须实现
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position > 0 && position < titleList.size()) {
            return titleList.get(position);
        }
        return titleList.get(0);
    }

}
