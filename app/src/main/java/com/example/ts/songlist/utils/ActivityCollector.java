package com.example.ts.songlist.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ts on 17-12-7.
 * 用于管理所有活动，可随时结束全部活动
 */

public class ActivityCollector {
    private static List<Activity> activityList=new ArrayList();

    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity :activityList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
    public static Activity getActivity(int n){
        return activityList.get(n);
    }
}
