package com.example.ts.songlist;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

import com.example.ts.songlist.adapter.FragmentAdapter;
import com.example.ts.songlist.views.ArtistListFragment;
import com.example.ts.songlist.views.MusicListFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BasicActivity {

    private ViewPager mPager;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();
    private PagerAdapter mAdapter;

    private PagerTabStrip pagerTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mPager = findViewById(R.id.view_pager);

        pagerTitle = findViewById(R.id.pager_title);

        //为标题设置属性，比如背景，颜色线等
        pagerTitle.setBackgroundColor(Color.WHITE);//设置背景颜色
        pagerTitle.setTextColor(Color.GRAY);//设置标题文字的颜色
        pagerTitle.setDrawFullUnderline(false);//将标题下的长分割线去掉
        pagerTitle.setTabIndicatorColor(Color.GREEN);//设置标题下粗一点的短分割线的颜色

        mTitleList.add("歌曲");
        mTitleList.add("歌手");
        Fragment musicListFragment = new MusicListFragment();
        Fragment artistListFragment = new ArtistListFragment();

        mFragmentList.add(musicListFragment);
        mFragmentList.add(artistListFragment);

        mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList, mTitleList);

        mPager.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
