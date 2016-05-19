package com.llf.mobilesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Lee on 2016/5/19.
 */
public class UIUtils {

    /**
     * 吐司工具，在主线程和子线程都可用
     * @param context
     * @param text
     */
    public static void showToast(final Activity context, final String text){
        if ("main".equals(Thread.currentThread().getName())){
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
