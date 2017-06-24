package com.group.android.finalproject.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.group.android.finalproject.R;
import com.group.android.finalproject.player.util.RecordItem;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by YZQ on 2016/12/20.
 */

public class RecordAdapter extends BaseAdapter {
    private Context context;
    private List<RecordItem> list;

    public RecordAdapter(Context context, List<RecordItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        if(list == null) {
            return null;
        }
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView;
        ViewHolder viewHolder;

        if (view == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.player_activity_main_recorder_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.recorder_name);
            viewHolder.date = (TextView)convertView.findViewById(R.id.recorder_date);
            convertView.setTag(viewHolder);
        } else {
            convertView = view;
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.name.setText(list.get(i).getTitle());
        viewHolder.date.setText(list.get(i).getDate());

        return convertView;
    }

    class ViewHolder{
        public TextView name;
        public TextView date;
    }
}
