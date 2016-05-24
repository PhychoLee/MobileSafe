package com.llf.mobilesafe.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.llf.mobilesafe.service.TaskWidgetService;

/**
 * Created by Lee on 2016/5/24.
 */
public class TaskAppWidget extends AppWidgetProvider {

    /**
     * 第一次创建时调用
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        //转跳到服务
        Intent intent = new Intent(context, TaskWidgetService.class);
        context.startService(intent);
    }

    /**
     * 最后一个销毁时调用
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        //关闭服务
        Intent intent = new Intent(context, TaskWidgetService.class);
        context.stopService(intent);
    }

}
