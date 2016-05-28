package com.llf.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lee on 2016/5/28.
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {

    public AppLockOpenHelper(Context context) {
        super(context, "applock", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applock (_id integer primary key autoincrement, packagename varchar(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
