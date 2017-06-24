package com.group.android.finalproject.notification.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.group.android.finalproject.R;
import com.group.android.finalproject.player.activity.PlayerMainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager manager;
    public static final String ACTION = "com.group.android.finalproject.notificate_setting";

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION.equals(intent.getAction())) {
            manager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            Intent playIntent = new Intent(context, PlayerMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("回忆44")
                    .setContentText("记得今天要进行语音日志哦")
                    .setSmallIcon(R.drawable.mp_play)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.mp_play))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis());
            manager.notify(1, builder.build());
        }
    }
}
