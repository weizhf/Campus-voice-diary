package com.group.android.finalproject.recoder;

import android.content.DialogInterface;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.group.android.finalproject.R;
import com.group.android.finalproject.common.DBbase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by root on 16-12-9.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // 控件
    private Button btn_start  = null;
    private Button btn_delete = null;
    private Button btn_ok     = null;
    private Button btn_play   = null;
    private RecordView mRecorfView = null;
    private boolean state = false;

    private MyLocation mLocation = null;
    private Recorder mRecorder   = null;
    private DBbase dBbase        = null;

    private LocationManager locationManager = null;

    private TimerTask timeTask = null;
    private Timer timeTimer = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int db = (int) (Math.random()*100);
            mRecorfView.setVolume(db);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_activity_main);
        getLocation();
        Bundle extras = getIntent().getExtras();
        if (extras != null) setBackground((int)extras.get("color"));
        getRecorder();
        findView();
        bindListener();
        setmRecorfView();
    }

    private void setBackground(int color) {
        RelativeLayout background_player = (RelativeLayout) findViewById(R.id.recorder_main_background);
        switch(color) {
            case 0:
                background_player.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
            case 1:
                background_player.setBackgroundColor(Color.parseColor("#ABABAB"));
                break;
        }
    }

    private void setmRecorfView() {
        mRecorfView = (RecordView) findViewById(R.id.recordView);
        mRecorfView.setCountdownTime(0);
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); //位置服务
        mLocation = new MyLocation(locationManager);
    }

    private void getRecorder() {
        mRecorder = new Recorder();
    }

    private void findView() {
        btn_start = (Button) findViewById(R.id.start);
        btn_ok    = (Button) findViewById(R.id.ok);
        btn_delete= (Button) findViewById(R.id.delete);
    }

    private void bindListener() {
        btn_start.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }

    boolean hasRecord = false;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                // 录音波形动画
                if(!hasRecord) {
                    mRecorfView.setModel(RecordView.MODEL_RECORD);
                    mRecorder.startRecord();

                    hasRecord = true;
                    btn_delete.setClickable(true);
                    btn_ok.setClickable(true);
                    startAnimate();
                    mRecorfView.start();
                }
//                if (mRecorder.isPause()) {
//                    btn_delete.setClickable(true);
//                    btn_ok.setClickable(true);
//                    stopAnimate();
//                    mRecorfView.cancel();
//                } else {
//                    hasRecord = true;
//                    btn_delete.setClickable(false);
//                    btn_ok.setClickable(false);
//                    startAnimate();
//                    mRecorfView.start();
//                }
                break;
            case R.id.ok:
                if (!hasRecord) {
                    showToast("正在录音或未录音，无法点击");
                    return;
                }
                mRecorfView.cancel();
                stopAnimate();
                mRecorder.stopRecord();
                view1 = view;
                showDialog();
                break;
            case R.id.delete:
                if (!hasRecord) {
                    showToast("正在录音或未录音，无法点击");
                    return;
                }
                hasRecord = false;
                if (mRecorder == null || mRecorfView == null) return;
                mRecorfView.cancel();
                stopAnimate();
                mRecorfView.cancel();
//                mRecorfView.setCountdownTime(0);
//                mRecorder.cancelRecord();
                quit();
                break;
        }
    }
    private View view1;

    private void showDialog() {
        dBbase = new DBbase(this);
        final MyLocation.LOCATION localInfo = mLocation.getLocation();

        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View v = factory.inflate(R.layout.recorder_activity_save_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(v);

        final TextView tv_date = (TextView) v.findViewById(R.id.tv_date);
        final EditText et_title = (EditText) v.findViewById(R.id.et_title);
        final EditText et_feel = (EditText) v.findViewById(R.id.et_feel);
        final EditText et_talk = (EditText) v.findViewById(R.id.et_talk);
        final EditText et_place = (EditText) v.findViewById(R.id.et_place);

        et_place.setText(localInfo.getAddress());

        final ToggleButton btn_play = (ToggleButton) v.findViewById(R.id.play);
        final boolean[] isFirst = {true};
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecorder.playRecord(mRecorder.getFileUrl());
                if (isFirst[0]) {
                    isFirst[0] = false;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mRecorder.isPlaying()) {
                                handler.postDelayed(this, 500);
                            } else {
                                isFirst[0] = true;
                                btn_play.setChecked(true);
                            }
                        }
                    }, 3000);
                }
            }
        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        final String date = formatter.format(c.getTime());
        tv_date.setText(date);

        builder.setTitle("个人语音日志");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = et_title.getText().toString();
                String feel = et_feel.getText().toString();
                String talk = et_talk.getText().toString();
                String fileUrl = mRecorder.getFileUrl();
                String place = et_place.getText().toString();

                Log.e("DATEINMAIN", date);
                if (title.isEmpty() || feel.isEmpty() || talk.isEmpty()) {
                    showToast("填空不能为空");
                } else {
                    setResult(RESULT_OK);
                    mRecorfView.setCountdownTime(0);
                    mRecorder.stopRecord();
                    fileUrl = mRecorder.getFileUrl();
                    dBbase.insert(title, date, feel, place, talk, fileUrl);
                    Log.e("list1",  fileUrl);
                    state = true;
                    Snackbar.make(view1, "保存成功", Snackbar.LENGTH_SHORT)
                            .setAction("确认", new View.OnClickListener() {
                                public void onClick(View view) {
                                    finish();
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                            .setDuration(3000)
                            .show();
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    };
                    handler.postDelayed(runnable, 3000);
                    // finish this activity, and turn back main activity
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });

        builder.show();
    }

    private void startAnimate() {
        if (timeTimer == null) {
            timeTimer = new Timer(true);
            timeTimer.schedule(timeTask = new TimerTask() {
                public void run() {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }, 20, 20);
        }
    }

    private void stopAnimate() {
        if (timeTimer == null) return;
        timeTimer.cancel();
        timeTask.cancel();
        timeTimer = null;
        timeTask = null;
    }

    private void quit() {
        new AlertDialog.Builder(this)
                .setTitle("确认删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mRecorfView.setCountdownTime(0);
                        mRecorder.stopRecord();
                        String fileUrl = mRecorder.getFileUrl();
                        File file = new File(fileUrl);
                        if (file.exists()) {
                            file.delete();
                        }
                        showToast("文件删除成功");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}