package com.llf.mobilesafe;

import android.content.Context;
import android.test.AndroidTestCase;

import com.llf.mobilesafe.db.dao.BlackNumberDao;

import java.util.Random;

/**
 * Created by Lee on 2016/5/13.
 */
public class TestBlackNumberDao extends AndroidTestCase{

    private Context mContext;
    @Override
    protected void setUp() throws Exception {
        mContext = getContext();
        super.setUp();
    }

    public void testAdd(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        Random random = new Random();
        for (int i = 0; i < 200 ; i++) {
            long number = 18813973700l + i;
            dao.add(number + "", String.valueOf(random.nextInt(3) + 1));
        }
    }

    public void testDelete(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        boolean delete = dao.delete("18813973722");
//        assertEquals(true,delete);
    }

    public void testUpdate(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        boolean update = dao.update("18813933702","2");
        assertEquals(true,update);
    }

    public void testFind(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        String number = "18813973700";
        String s = dao.find(number);
        System.out.println(s);
    }
    public void testFindAll(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        dao.findAll();
    }
}
