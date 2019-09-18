package com.hnust.liveapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hnust.liveapp.bean.CateInfo;
import com.hnust.liveapp.bean.HistoryInfo;
import com.hnust.liveapp.bean.RoomInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作类
 */

public class DBService {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    //构造方法
    public DBService(Context context) {
        dbHelper = new DBHelper(context);
        //连接数据库
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取所有分类
     *
     * @return
     */
    public List<CateInfo> getCateInfo() {
        List<CateInfo> list = new ArrayList<>();
        Cursor cursor = null;

        String sql = "select id,name from category order by id desc";
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    CateInfo cateInfo = new CateInfo(cursor.getInt(0),cursor.getString(1));
                    list.add(cateInfo);

                    Log.e("DDDDDDDDDDD", "Exception" + cateInfo.toString());
                }
            }
        } catch (Exception e) {
            Log.e("DDDDDDDDDDD", "Exception" + e.getMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return list;
    }


    /**
     * 获取所有历史
     *
     * @return
     */
    public List<RoomInfo> getHistorys() {
        List<RoomInfo> list = new ArrayList<>();
        Cursor cursor = null;

        String sql = "select * from history order by time desc";
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
//                    RoomInfo roomInfo = new RoomInfo(
//                            cursor.getInt(cursor.getColumnIndex("rid")),
//                            cursor.getString(cursor.getColumnIndex("address")),
//                            cursor.getString(cursor.getColumnIndex("title")),
//                            cursor.getString(cursor.getColumnIndex("name")), 1);
//                    Log.e("DDDDDDDDDDD", roomInfo.toString());
//                    list.add(roomInfo);
                }
            }
        } catch (Exception e) {
            Log.e("DDDDDDDDDDD", "Exception" + e.getMessage());
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return list;
    }

    //新增历史
    public void addHistory(RoomInfo roomInfo) {

        try {
            ContentValues values = new ContentValues();
//            values.put("rid", roomInfo.getRoomId());
//            values.put("title", roomInfo.getRoomName());
//            values.put("name", roomInfo.getNick());
//            values.put("address", roomInfo.getRoomSrc());
            values.put("category", "最新");
            values.put("time", getTime());
            db.insert("history", null, values);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DDDDDDDDDDD", "Exception" + e.getMessage());
        } finally {
            if (null != db) {
                db.close();
            }
        }

    }

    //插入数据库的数据
    public void deleteAll() {
        db.delete("history", null, null);
    }

    private String getTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

}