package com.llf.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.llf.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee on 2016/5/11.
 */
public class BlackNumberDao {

    private BlackNumberOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberOpenHelper(context);
    }

    /**
     * 添加
     *
     * @param number
     * @param mode
     */
    public boolean add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long insert = db.insert("blacknumber", null, values);
        db.close();
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 删除
     *
     * @param number
     */
    public boolean delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int delete = db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
        if (delete == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 修改
     *
     * @param number
     */
    public boolean update(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        int update = db.update("blacknumber", values, "number=?", new String[]{number});
        db.close();
        if (update == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 查看
     *
     * @param number
     */
    public String find(String number) {
        String mode = "";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getString(0);
        }
        db.close();
        cursor.close();
        return mode;
    }

    /**
     * 查找所有
     *
     * @return
     */
    public List<BlackNumberInfo> findAll() {
        List<BlackNumberInfo> blackNumberList = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            blackNumberInfo.setNumber(number);
            blackNumberInfo.setMode(mode);
            blackNumberList.add(blackNumberInfo);
        }
        SystemClock.sleep(3000);
        cursor.close();
        db.close();
        return blackNumberList;
    }

    /**
     * 分页查找
     * @param size
     * @param page
     * @return
     *
     * limit 限制多少行
     * offset 跳到哪行开始
     */
    public List<BlackNumberInfo> findByPage(int size, int page) {
        List<BlackNumberInfo> blackNumberList = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{String.valueOf(size),
                String.valueOf((page-1) * size)});
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            blackNumberInfo.setNumber(number);
            blackNumberInfo.setMode(mode);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }
}
