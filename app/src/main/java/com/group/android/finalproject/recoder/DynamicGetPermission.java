package com.group.android.finalproject.recoder;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

/**
 * Created by root on 16-12-5.
 */
public class DynamicGetPermission extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = getSharedPreferences("Alarm_Time", MODE_PRIVATE);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            //Toast.makeText(DynamicGetPermission.this, "Granted", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("permission_check", true);
                            editor.commit();
                            DynamicGetPermission.this.finish();
                        } else {
                            Toast.makeText(DynamicGetPermission.this, "Permission deny. Something will run with error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
