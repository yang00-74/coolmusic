package com.example.ts.songlist;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SongProgressReceiver extends BroadcastReceiver {
    public static final String ACTION_SONG_PROGRESS_CHANGED
            = "com.example.ts.songlist.SongProgressReceiver.ACTION_SONG_PROGRESS_CHANGED";

    public static final String EXTRA_SONG = "song";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ACTION_SONG_PROGRESS_CHANGED.equals(intent.getAction())) {
            return;
        }

        if (intent.hasExtra(EXTRA_SONG)) {
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
