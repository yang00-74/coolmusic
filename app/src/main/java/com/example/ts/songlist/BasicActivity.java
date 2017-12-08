package com.example.ts.songlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ts.songlist.utils.ActivityCollector;
import com.example.ts.songlist.utils.LogUtil;

/**
 * Created by ts on 17-12-7.
 * 所有活动的基类，可用于追踪界面对应的活动,提供管理所有活动的ActivityCollector
 */

public class BasicActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        LogUtil.d("BaseActivity",getClass().getSimpleName());

        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
