package com.llf.mobilesafe.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.receiver.TaskAppWidget;
import com.llf.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

public class TaskWidgetService extends Service {

    private AppWidgetManager appWidgetManager;

    public TaskWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appWidgetManager = AppWidgetManager.getInstance(this);

        //定时刷新应用进程信息
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //刷新信息

                ComponentName provider = new ComponentName(getApplicationContext(), TaskAppWidget.class);

                //远程View对象
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);

                //应用进程数量
                int taskCount = SystemInfoUtils.getTaskCount(TaskWidgetService.this);
                views.setTextViewText(R.id.process_count, "正在运行软件："+String.valueOf(taskCount));

                long availMen = SystemInfoUtils.getAvailMen(TaskWidgetService.this);
                views.setTextViewText(R.id.process_memory, "剩余内存："+ Formatter.formatFileSize(TaskWidgetService.this, availMen));

                //更新桌面
                appWidgetManager.updateAppWidget(provider, views);
            }
        };
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
