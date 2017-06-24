package com.group.android.finalproject.notification.activity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;

import com.group.android.finalproject.R;
import com.group.android.finalproject.player.presenter.MusicPresenter;

/**
 * Created by root on 16-12-22.
 */

public class SettingMainActivity extends AppCompatActivity {

    private Switch notifySwitch;
    private LinearLayout linearLayout;
    private Button timeSet;
    private int mHour, mMinute;
    private boolean isOn;

    private MusicPresenter musicPresenter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        Bundle extras = getIntent().getExtras();
        setBackground((int)extras.get("color"));

        Toolbar toolbar = (Toolbar)findViewById(R.id.setting_toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        musicPresenter = MusicPresenter.getInstance(this);
        sharedPreferences = getSharedPreferences("Alarm_Time", MODE_PRIVATE);

        mHour = sharedPreferences.getInt("hour", 21);
        mMinute = sharedPreferences.getInt("minute", 00);
        isOn = sharedPreferences.getBoolean("is_alarm", true);

        notifySwitch = (Switch)findViewById(R.id.setting_switch);
        linearLayout = (LinearLayout)findViewById(R.id.setting_time_to_set);
        timeSet = (Button)findViewById(R.id.setting_time_picker);

        notifySwitch.setChecked(isOn);
        String time;
        if (mMinute/10 == 0) {
            time = String.valueOf(mHour) + ":0" + String.valueOf(mMinute);
        } else {
            time = String.valueOf(mHour) + ":" + String.valueOf(mMinute);
        }
        timeSet.setText(time);
        timeSetAvailable();

        timeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSetPicker();
            }
        });

        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOn = isChecked;
                timeSetAvailable();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_alarm", isOn);
                editor.commit();
            }
        });
    }

    private void showTimeSetPicker() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
            }
        };
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, listener, mHour, mMinute, true);
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    timePickerDialog.onClick(dialog, which);
                    String time;
                    if (mMinute/10 == 0) {
                        time = String.valueOf(mHour) + ":0" + String.valueOf(mMinute);
                    } else {
                        time = String.valueOf(mHour) + ":" + String.valueOf(mMinute);
                    }
                    timeSet.setText(time);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("hour", mHour);
                    editor.putInt("minute", mMinute);
                    editor.commit();
                    musicPresenter.setAlarmTime(mHour, mMinute);
                    dialog.dismiss();
                }
            }
        });
        timePickerDialog.show();
    }

    private void timeSetAvailable() {
        if (isOn) {
            linearLayout.setVisibility(View.VISIBLE);
            musicPresenter.setAlarmTime(mHour, mMinute);
        } else {
            linearLayout.setVisibility(View.GONE);
            musicPresenter.cancelAlarmManager();
        }
    }

    public void setBackground(int color) {
        LinearLayout background_setting = (LinearLayout)findViewById(R.id.setting_activity);
        switch(color) {
            case 0:
                background_setting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
            case 1:
                background_setting.setBackgroundColor(Color.parseColor("#ABABAB"));
                break;
        }
    }
}
