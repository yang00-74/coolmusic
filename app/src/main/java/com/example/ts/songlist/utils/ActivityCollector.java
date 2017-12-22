package com.example.ts.songlist.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ts on 17-12-7.
 * It is created to manage all the activities extends BasicActivity
 */

public class ActivityCollector {

    private static List<Activity> activityList = new ArrayList();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static Activity getActivity(int n) {
        if (n < 0) {
            throw new RuntimeException("Error:argument < 0");
        }
        return activityList.get(n);
    }
}
