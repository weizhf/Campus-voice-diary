package com.group.android.finalproject.player.activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.group.android.finalproject.R;
import com.group.android.finalproject.player.presenter.MusicPresenter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerPlayActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView date, place, feel, remark;
    private TextView current, duration;
    private SeekBar seekBar;
    private ImageButton play, play_return;
    private MusicPresenter musicPresenter;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");

    private MHandler mHandler;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    mHandler.sendMessage(new Message());
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };




    class MHandler extends Handler {
        public void handleMessage(Message msg) {
            if (musicPresenter.isPlaying()) {
                Date durationTime = new Date(musicPresenter.getDuration());
                Date currentTime = new Date(musicPresenter.getCurrentPosition());
                seekBar.setMax(musicPresenter.getDuration());
                seekBar.setProgress(musicPresenter.getCurrentPosition());
                current.setText(time.format(currentTime));
                duration.setText(time.format(durationTime));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_play_activity_main);

        Bundle extras = getIntent().getExtras();

        Toolbar toolbar = (Toolbar)findViewById(R.id.player_play_toolbar);
        toolbar.setTitle((String)extras.get("title"));
        setSupportActionBar(toolbar);

        setBackground((int)extras.get("color"));

        date = (TextView)findViewById(R.id.player_play_date);
        place = (TextView)findViewById(R.id.player_play_place);
        feel = (TextView)findViewById(R.id.player_play_feel);
        remark = (TextView)findViewById(R.id.player_play_remark);
        current = (TextView)findViewById(R.id.player_play_current);
        duration = (TextView)findViewById(R.id.player_play_duration);
        seekBar = (SeekBar)findViewById(R.id.player_play_seek);
        play = (ImageButton)findViewById(R.id.player_play_music_play);
        play_return = (ImageButton)findViewById(R.id.player_play_return);

        date.setText((String)extras.get("date"));
        place.setText((String)extras.get("place"));
        feel.setText((String)extras.get("feel"));
        remark.setText((String)extras.get("remark"));

        musicPresenter = MusicPresenter.getInstance(this);
        musicPresenter.setPlayView(this);
        mHandler = new MHandler();
        new Thread(mRunnable).start();

        if (musicPresenter.isPlaying()) {
            play.setImageResource(R.mipmap.player_activity_pause);
        }

        play.setOnClickListener(this);
        play_return.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    current.setText(time.format(new Date(progress)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPresenter.seek(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.player_play_music_play:
                musicPresenter.musicPlay();
                break;
            case R.id.player_play_return:
                finish();
                break;
        }
    }

    public void changePlayImage(boolean isPlaying) {
        if (isPlaying) {
            play.setImageResource(R.mipmap.player_activity_pause);
        } else {
            play.setImageResource(R.mipmap.player_activity_play);
        }
    }

    public void setBackground(int color) {
        LinearLayout background_player = (LinearLayout)findViewById(R.id.player_play_activity_main);
        switch(color) {
            case 0:
                background_player.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
            case 1:
                background_player.setBackgroundColor(Color.parseColor("#ABABAB"));
                break;
        }
    }
}
