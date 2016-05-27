package com.llf.mobilesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 查询病毒数据库
 * Created by Lee on 2016/5/26.
 */
public class AntivirusDao {
    private static final String PATH = "data/data/com.llf.mobilesafe/files/antivirus.db";

    public static String getDesc(String md5){
        String desc = null;
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select desc from datable where md5 = ?", new String[]{md5});
        if (cursor.moveToNext()){
            desc = cursor.getString(0);
        }
        return  desc;
    }

    public static void updateVirus(String md5, String desc){
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READWRITE);

        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("desc", desc);
        database.insert("datable", null, values);
    }
}
