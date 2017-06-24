package com.group.android.finalproject.player.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.group.android.finalproject.R;
import com.group.android.finalproject.common.DBbase;
import com.group.android.finalproject.common.EditTextWithDate;
import com.group.android.finalproject.notification.activity.SettingMainActivity;
import com.group.android.finalproject.player.adapter.RecordAdapter;
import com.group.android.finalproject.player.presenter.MusicPresenter;
import com.group.android.finalproject.player.util.RecordItem;
import com.group.android.finalproject.recoder.DynamicGetPermission;
import com.group.android.finalproject.recoder.MainActivity;
import com.group.android.finalproject.recoder.Recorder;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private SwipeMenuListView swipeMenuListView;
    private DBbase dBbase;
    private List<RecordItem> recordItemList;
    private RecordAdapter recordAdapter;

    private LinearLayout player_play_system;
    private TextView title;
    private ImageButton play_pause, play_next;

    private MusicPresenter musicPresenter;
    private SharedPreferences sharedPreferences;
    private int current;
    private int colorSet = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity_main);

        player_play_system = (LinearLayout)findViewById(R.id.player_play_system);
        title = (TextView)findViewById(R.id.player_current_title);
        play_pause = (ImageButton)findViewById(R.id.player_play);
        play_next = (ImageButton)findViewById(R.id.player_next);

        Toolbar toolbar = (Toolbar)findViewById(R.id.player_toolbar);
        toolbar.setTitle("Recorder List");
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.player_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.player_navigation);
        navigationView.setNavigationItemSelectedListener(this);

        swipeMenuListView = (SwipeMenuListView)findViewById(R.id.player_recorder_list);
        dBbase = new DBbase(this);

        musicPresenter = MusicPresenter.getInstance(this);
        musicPresenter.setMainView(this);
        sharedPreferences = getSharedPreferences("Alarm_Time", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("is_alarm", true)) {
            musicPresenter.setAlarmTime(sharedPreferences.getInt("hour", 21), sharedPreferences.getInt("minute", 0));
        }

        recordItemList = new ArrayList<RecordItem>();
        if (dBbase.queryAll() != null) recordItemList.addAll(dBbase.queryAll());
        recordAdapter = new RecordAdapter(this, recordItemList);
        swipeMenuListView.setAdapter(recordAdapter);
        setSwipeList();
        setListClick();

        player_play_system.setOnClickListener(this);
        play_pause.setOnClickListener(this);
        play_next.setOnClickListener(this);

        if (!sharedPreferences.getBoolean("permission_check", false)) {
            startActivity(new Intent(this, DynamicGetPermission.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPresenter.unBindService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent intent = new Intent();
            intent.setClass(PlayerMainActivity.this, MainActivity.class);
            intent.putExtra("color", colorSet);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    recordItemList.clear();
                    recordItemList.addAll(dBbase.queryAll());
                    recordAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void setSwipeList() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem detailItem = new SwipeMenuItem(
                        getApplicationContext());
                detailItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                detailItem.setWidth(dp2px(90));
                detailItem.setTitle("详情");
                detailItem.setTitleSize(18);
                detailItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(detailItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.mipmap.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch(index) {
                    case 0:     // detail
                        showDetailDialog(position);
                        break;
                    case 1:     // delete
                        showConfirmDeleteDialog(position);
                        break;
                }
                return true;
            }
        });
    }

    private void setListClick() {
        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                player_play_system.setVisibility(View.VISIBLE);
                current = i;
                musicPresenter.loadMusic(recordItemList.get(i).getStoreUrl());
                messageSet(current);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_all_list:
                recordItemList.clear();
                if (dBbase.queryAll() != null) recordItemList.addAll(dBbase.queryAll());
                recordAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_search_by_time:
                showQueryDialog();
                break;
            case R.id.menu_setting:
                Intent intent = new Intent(PlayerMainActivity.this, SettingMainActivity.class);
                intent.putExtra("color", colorSet);
                startActivityForResult(intent, 3);
                break;
            case R.id.menu_background_change:
                musicPresenter.changeBackgroundColor();
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.player_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDetailDialog(final int i) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PlayerMainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(PlayerMainActivity.this);
        View dialogView = inflater.inflate(R.layout.player_detail_dialog, null);
        builder.setView(dialogView);

        final TextView detail_date = (TextView) dialogView.findViewById(R.id.detail_date);
        final EditText detail_title = (EditText) dialogView.findViewById(R.id.detail_title);
        final EditText detail_feel = (EditText) dialogView.findViewById(R.id.detail_feel);
        final EditText detail_talk = (EditText) dialogView.findViewById(R.id.detail_talk);
        final EditText detail_place = (EditText) dialogView.findViewById(R.id.detail_place);

        detail_title.setText(recordItemList.get(i).getTitle());
        detail_feel.setText(recordItemList.get(i).getFeel());
        detail_place.setText(recordItemList.get(i).getPlace());
        detail_talk.setText(recordItemList.get(i).getRemark());
        detail_date.setText(recordItemList.get(i).getDate());

        builder.setTitle("个人语音日志");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = detail_title.getText().toString();
                String feel = detail_feel.getText().toString();
                String talk = detail_talk.getText().toString();
                String place = detail_place.getText().toString();
                String date = detail_date.getText().toString();
                if (title.isEmpty() || feel.isEmpty() || talk.isEmpty()) {
                    Toast.makeText(PlayerMainActivity.this, "填空不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    recordItemList.get(i).update(title, date, feel, place, talk);
                    recordAdapter.notifyDataSetChanged();
                    dBbase.update(title, date, feel, place, talk, recordItemList.get(i).getStoreUrl());
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void showConfirmDeleteDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerMainActivity.this);

        builder.setTitle("是否删除");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (current == i) {
                    musicPresenter.musicStop();
                    player_play_system.setVisibility(View.GONE);
                } else if (current > i) {
                    current--;
                }
                File file = new File(recordItemList.get(i).getStoreUrl());
                boolean deleted = file.delete();
                dBbase.delete(recordItemList.get(i).getStoreUrl());
                recordItemList.remove(i);
                recordAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void showQueryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerMainActivity.this);
        LayoutInflater inflater = PlayerMainActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.player_query_by_time_dialog, null);
        builder.setView(dialogView);

        final EditTextWithDate start_date = (EditTextWithDate) dialogView.findViewById(R.id.search_start);
        final EditTextWithDate end_date = (EditTextWithDate) dialogView.findViewById(R.id.search_end);
        final EditText loc = (EditText) dialogView.findViewById(R.id.search_location);

        builder.setTitle("查询");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recordItemList.clear();
                if (dBbase.queryAll() != null) recordItemList.addAll(dBbase.queryAll());
                String start = start_date.getText().toString() + " 00:00:00";
                String end = end_date.getText().toString() + " 23:59:59";
                String location = loc.getText().toString();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date start_time = new Date(System.currentTimeMillis());
                Date end_time = new Date(System.currentTimeMillis());

                try {
                    start_time = formatter.parse(start);
                    end_time = formatter.parse(end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < recordItemList.size();) {
                    Date d = new Date();
                    try {
                        d = formatter.parse(recordItemList.get(i).getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (d.after(start_time) && d.before(end_time) && recordItemList.get(i).getPlace().contains(location)) {
                        i++;
                    } else {
                        recordItemList.remove(i);
                    }
                }
                player_play_system.setVisibility(View.GONE);
                recordAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.player_play_system:
                Intent intent = new Intent(PlayerMainActivity.this, PlayerPlayActivity.class);
                intent.putExtra("title", recordItemList.get(current).getTitle());
                intent.putExtra("date", recordItemList.get(current).getDate());
                intent.putExtra("place", recordItemList.get(current).getPlace());
                intent.putExtra("feel", recordItemList.get(current).getFeel());
                intent.putExtra("remark", recordItemList.get(current).getRemark());
                intent.putExtra("color", colorSet);
                startActivityForResult(intent, 1);
                break;
            case R.id.player_play:
                musicPresenter.musicPlay();
                break;
            case R.id.player_next:
                current = (current + 1)%(recordItemList.size());
                musicPresenter.loadMusic(recordItemList.get(current).getStoreUrl());
                messageSet(current);
                break;
        }
    }

    public void changePlayImage(boolean isPlaying) {
        if (isPlaying) {
            play_pause.setImageResource(R.mipmap.player_stop);
        } else {
            play_pause.setImageResource(R.mipmap.player_play);
        }
    }

    private void messageSet(int i) {
        title.setText(recordItemList.get(i).getTitle());
    }

    public void setBackground(int color) {
        colorSet = color;
        LinearLayout background_player = (LinearLayout)findViewById(R.id.player_main_background);
        NavigationView navigationView = (NavigationView)findViewById(R.id.player_navigation);
        switch(color) {
            case 0:
                background_player.setBackgroundColor(Color.parseColor("#FFFFFF"));
                navigationView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                break;
            case 1:
                background_player.setBackgroundColor(Color.parseColor("#ABABAB"));
                navigationView.setBackgroundColor(Color.parseColor("#ABABAB"));
                break;
        }
    }
}
