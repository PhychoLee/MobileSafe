package com.llf.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Lee on 2016/5/28.
 */
public class AppLockDao {

    private final AppLockOpenHelper openHelper;

    public AppLockDao(Context context){
        openHelper = new AppLockOpenHelper(context);
    }

    public void add(String packageName){
        SQLiteDatabase database = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        database.insert("applock", null, values);
        database.close();
    }

    public void delete(String packageName){
        SQLiteDatabase database = openHelper.getWritableDatabase();
        database.delete("applock", "packagename = ?", new String[]{packageName});
        database.close();
    }

    /**
     * 查询包名
     * @param packageName
     * @return
     */
    public boolean query(String packageName){
        boolean locked = false;
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query("applock", null, "packagename = ?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()){
            locked = true;
        }
        return locked;
    }
}
