package com.llf.mobilesafe.db.dao;

import android.content.Context;

/**
 * Created by Lee on 2016/5/11.
 */
public class BlackNumberDao {
    public BlackNumberDao(Context context){
        BlackNumberOpenHelper openHelper = new BlackNumberOpenHelper(context);
    }

    /**
     * 添加
     * @param number
     * @param mode
     */
    public void add(String number, String mode){

    }

    /**
     * 删除
     * @param number
     */
    public void delete(String number){

    }

    /**
     * 修改
     * @param number
     */
    public void find(String number){

    }

    /**
     * 查看
     * @param number
     */
    public void query(String number){

    }
}
