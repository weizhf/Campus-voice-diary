package com.group.android.finalproject.recoder;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

public class Recorder {
    // 语音操作对象
    private MediaPlayer mPlayer     = null;
    private MediaRecorder mRecorder = null;
    // 文件保存路径和名字
    private String fileName = null;

    private static boolean isFirst = false;
    private static boolean isPause = false;
    private static boolean isRecording = false;

    public Recorder() {
        isPause = false;
        isRecording = false;
        isFirst = true;
        setFileName();
    }

    private void setFileName() {
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        int fileSum = Environment.getExternalStorageDirectory().listFiles().length;
        fileName += "/record_" + fileSum + ".3gp";
        Log.e("FileName", fileName);
    }

    public String getFileUrl() {
        return fileName;
    }

// http://blog.csdn.net/cxf7394373/article/details/8313980
    // 开始录音
    @TargetApi(Build.VERSION_CODES.N)
    public void startRecord() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        if (isFirst) {
            // 设置声音来源——麦克风
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置文件输出格式——gpp
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置文件保存路径和名字, TODO
            mRecorder.setOutputFile(fileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Tag", "prepare() failed");
            }
            // 开始录音
            isFirst = false;
            isRecording = true;
            isPause = false;
        } else {
//            if (isPause) {
//                mRecorder.resume();
//                isPause = false;
//            } else {
//                mRecorder.pause();
//                isPause = true;
//            }
        }
    }

    // 停止录音
    public void stopRecord() {
        if (mRecorder == null) return;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        isFirst = true;
        isPause = false;
        isRecording = false;
    }

    // 重新录音
    public void cancelRecord() {
        if (mRecorder != null) {
            mRecorder.reset();
            isFirst = true;
        }
    }

    public int getDuration(String fileUrl) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(fileUrl);
                return mPlayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // 播放录音
    public void playRecord(String fileUrl) {
        try {
            Log.e("Here-----------", fileUrl);
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(fileUrl);
                mPlayer.prepare();
            }
            if (isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.start();
            }
        } catch(IOException e) {
            Log.e("Tag", "播放失败");
        }
    }

    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    public boolean isPause() {
        return isPause;
    }
}
