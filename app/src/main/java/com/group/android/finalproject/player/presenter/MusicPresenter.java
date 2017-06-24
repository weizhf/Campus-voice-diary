package com.group.android.finalproject.player.presenter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.group.android.finalproject.notification.receiver.AlarmReceiver;
import com.group.android.finalproject.player.activity.PlayerMainActivity;
import com.group.android.finalproject.player.activity.PlayerPlayActivity;
import com.group.android.finalproject.player.services.MusicService;

import java.util.Calendar;

/**
 * Created by YZQ on 2016/12/21.
 */

public class MusicPresenter {
    private static MusicPresenter mPresenter;
    private MusicService musicService;
    private PlayerMainActivity mainView;
    private PlayerPlayActivity playView;
    private Context context;
    private AlarmManager alarmManager;
    private int colorSet = 0;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder)(iBinder)).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };

    private MusicPresenter(Context context) {
        this.context = context;
        Intent intent = new Intent(context, MusicService.class);
        context.bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    public static MusicPresenter getInstance(Context context) {
        if (mPresenter == null) {
            mPresenter = new MusicPresenter(context);
        }
        return mPresenter;
    }

    public void setMainView(PlayerMainActivity v) {
        mainView = v;
    }

    public void setPlayView(PlayerPlayActivity v) {
        playView = v;
    }

    public void loadMusic(String filepath) {
        musicService.load(filepath);
    }

    public void musicPlay() {
        if (isPlaying()) {
            musicService.pause();
            if (mainView != null) mainView.changePlayImage(false);
            if (playView != null) playView.changePlayImage(false);
        } else {
            musicService.play();
            if (mainView != null) mainView.changePlayImage(true);
            if (playView != null) playView.changePlayImage(true);
        }
    }

    public void musicStop() {
        musicService.stop();
    }

    public int getCurrentPosition() {
        return musicService.getCurrentPosition();
    }

    public int getDuration() {
        return musicService.getDuration();
    }

    public boolean isPlaying() {
        return musicService.isValid();
    }

    public void seek(int progress) {
        musicService.seek(progress);
    }

    public void finishPlaying() {
        if (mainView != null) mainView.changePlayImage(false);
        if (playView != null) playView.changePlayImage(false);
    }

    public void unBindService() {
        context.unbindService(sc);
    }

    public void changeBackgroundColor() {
        colorSet = 1- colorSet;
        if (mainView != null) mainView.setBackground(colorSet);
    }

    public void cancelAlarmManager() {
        Intent intent = new Intent(AlarmReceiver.ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mainView, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public void setAlarmTime(int mHour, int mMinute) {
        Intent intent = new Intent(AlarmReceiver.ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mainView, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) alarmManager.cancel(pendingIntent);

        alarmManager = (AlarmManager)mainView.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Calendar sys_time = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (sys_time.getTimeInMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
