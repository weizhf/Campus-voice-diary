package com.group.android.finalproject.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.group.android.finalproject.player.util.RecordItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 16-11-20.
 */
public class DBbase extends SQLiteOpenHelper {
    private static final String    DB_NAME = "FINALPROJECT";
    private static final String TABLE_NAME = "RECORDER";
    private static final int    DB_VERSION = 1;

    public DBbase(Context context) {
        // 第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
        super(context, DB_NAME, null, DB_VERSION);
        Log.e("TAG", "create db base");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                + TABLE_NAME
                + " (_id INTEGER PRIMARY KEY, title Text, date TEXT, feel TEXT, place TEXT, remark Text, storeUrl Text)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.e("TAG", "create a table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(String title, String date, String feel, String place, String remark, String storeUrl) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = getContentValuse(title, date, feel, place, remark, storeUrl);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public void update(String title, String date, String feel, String place, String remark, String storeUrl) {
        Log.e("TAG", "update");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = getContentValuse(title, date, feel, place, remark, storeUrl);
        String whereClause = "storeUrl=?";
        String [] whereArgs = {storeUrl};
        db.update(TABLE_NAME, cv, whereClause, whereArgs);
        db.close();
    }

    /**
    * 删除记录
    * @param {String} name 文件名
    */
    public void delete(String name) {
        Log.e("TAG", "delete");
        SQLiteDatabase db = getWritableDatabase();
        String DELETE = "DELETE FROM " + TABLE_NAME
                + " WHERE storeUrl='" + name + "'";
        db.execSQL(DELETE);
        db.close();
    }

//    // 根据文件名来查询
//    public Person queryDB(String name) {
//        SQLiteDatabase db = getReadableDatabase();
//        Person person = new Person();
//        String QUERY = "SELECT * FROM "
//                + TABLE_NAME
//                + " WHERE file_name='" + name + "'";
//        Cursor cursor = db.rawQuery(QUERY, null);
//
//        if (cursor.getCount() == 0) {
//            cursor.close();
//            return null;
//        }
//
//        while (cursor.moveToNext()) {
//            person.setName(cursor.getString(cursor.getColumnIndex("name")));
//            person.setName(cursor.getString(cursor.getColumnIndex("birth")));
//            person.setName(cursor.getString(cursor.getColumnIndex("gift")));
//            Log.e("TAG_query", person.getName());
//        }
//        cursor.close();
//        db.close();
//        return person;
//    }

    /**
    * 查询所有记录，返回所有字段
    * @param {null} null
    * @return {List<Map<String, String>>}  所有记录
    * */
    public List<RecordItem> queryAll() {
        SQLiteDatabase db = getReadableDatabase();
        List<RecordItem> list = new ArrayList<RecordItem>();
        String QUERY = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(QUERY, null);

        Log.e("TAG", "queryAll " + cursor.getCount());

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        while (cursor.moveToNext()) {
//            Map<String, String> map = new LinkedHashMap<>();
//            map.put("title", cursor.getString(cursor.getColumnIndex("title")));
//            map.put("date", cursor.getString(cursor.getColumnIndex("date")));
//            map.put("feel", cursor.getString(cursor.getColumnIndex("feel")));
//            map.put("place", cursor.getString(cursor.getColumnIndex("place")));
//            map.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
//            map.put("storeUrl", cursor.getString(cursor.getColumnIndex("storeUrl")));
//            list.add(map);
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String feel = cursor.getString(cursor.getColumnIndex("feel"));
            String place = cursor.getString(cursor.getColumnIndex("place"));
            String remark = cursor.getString(cursor.getColumnIndex("remark"));
            String storeUrl = cursor.getString(cursor.getColumnIndex("storeUrl"));
            list.add(new RecordItem(title, date, feel, place, remark, storeUrl));
        }
        db.close();
        return list;
    }

    /**
    * 自定义查询
    * @param {String} sql sql查询语句
    * @return {List<Map<String, String>>}  所有符合查询条件的记录
    * */
    public List<Map<String, String>> queryDB(String sql) {
        SQLiteDatabase db = getReadableDatabase();
        List<Map<String, String>> list = new LinkedList<>();
//        String QUERY = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        Log.e("TAG", "queryDB " + cursor.getCount());

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        while (cursor.moveToNext()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("file_name", cursor.getString(cursor.getColumnIndex("file_name")));
            map.put("title", cursor.getString(cursor.getColumnIndex("title")));
            map.put("date", cursor.getString(cursor.getColumnIndex("date")));
            map.put("place", cursor.getString(cursor.getColumnIndex("place")));
            map.put("weather", cursor.getString(cursor.getColumnIndex("weather")));
            map.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
            map.put("storeUrl", cursor.getString(cursor.getColumnIndex("storeUrl")));
            list.add(map);
            Log.e("TAG_query", map.get("file_name"));
        }
        db.close();
        return list;
    }

    /*
    * @return {String} 返回表格名
    * */
    public String getTableName() {
        return TABLE_NAME;
    }

    private ContentValues getContentValuse(String title, String date, String feel, String place, String remark, String storeUrl) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("date", date);
        cv.put("feel", feel);
        cv.put("place", place);
        cv.put("remark", remark);
        cv.put("storeUrl", storeUrl);
        return cv;
    }
}
