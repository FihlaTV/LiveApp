package com.hnust.liveapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 创建数据库,保存错题
 */

public class DBHelper extends SQLiteOpenHelper {

    String sql = "create table category ( id integer primary key autoincrement," +
            " name varchar(20))";

    //构造函数，创建数据库 history ,版本号1
    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(sql);
            Log.e("sql_", "sql_+  " + sql);
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e("sql_", "SQLiteDatabase");
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS category");
        onCreate(db);
    }
}
