package com.group.android.finalproject.player.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.group.android.finalproject.player.presenter.MusicPresenter;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private final IBinder binder = new MyBinder();
    private boolean isStop = false;
    private MusicPresenter musicPresenter;

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public MusicService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        musicPresenter = MusicPresenter.getInstance(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public boolean isValid() {
        return (mediaPlayer != null && mediaPlayer.isPlaying());
    }

    public void load(String filePath) {
        try {
            if (mediaPlayer != null) {
                if(!isStop) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    musicPresenter.finishPlaying();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        isStop = false;
    }

    public void play(){
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            if (isStop) {
                try {
                    mediaPlayer.prepare();
                    isStop = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isStop = true;
        }
    }

    public void seek(int progress) {
        if (mediaPlayer != null && !isStop) {
            mediaPlayer.seekTo(progress);
        }
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }
}
