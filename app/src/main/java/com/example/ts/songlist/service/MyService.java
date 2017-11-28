package com.example.ts.songlist.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ts.songlist.MusicActivity;
import com.example.ts.songlist.R;

public class MyService extends Service {
    private MyBinder mBinder = new MyBinder();

    class MyBinder extends Binder {
        public void start() {
            Log.d("MyBinder", "被调用了");
        }
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Myservice", "服务创建了");

        //前台服务
        Intent intent = new Intent(this, MusicActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("这是个标题")
                .setContentText("这是内容")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Myservice", "服务启动了");

        //使用多线程处理服务逻辑
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开始服务的逻辑

                //自行终止服务
                stopSelf();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Myservice", "服务销毁了");
        super.onDestroy();
    }
}
