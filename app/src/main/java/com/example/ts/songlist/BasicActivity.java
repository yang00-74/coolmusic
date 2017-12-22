package com.example.ts.songlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ts.songlist.utils.ActivityCollector;
import com.example.ts.songlist.utils.LogUtil;

/**
 * Created by ts on 17-12-7.
 * It is the superclass of all the activity ,used to check the activity which mapping given UI
 * besides it provider the ActivityCollector to manage all the activities
 *
 */

@SuppressLint("Registered")
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
