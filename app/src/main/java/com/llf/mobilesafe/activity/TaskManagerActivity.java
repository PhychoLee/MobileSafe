package com.llf.mobilesafe.activity;

import android.app.ActivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

import com.llf.mobilesafe.R;

import java.util.List;

public class TaskManagerActivity extends AppCompatActivity {

    private TextView tv_task_count;
    private TextView tv_memory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        initUI();
        initData();
    }

    private void initData() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //得到应用进程
        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();

        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        //剩余内存
        long availMem = outInfo.availMem;
        //总内存
        long totalMem = outInfo.totalMem;

        tv_task_count.setText("运行中进程"+appTasks.size()+"个");
        tv_memory.setText("剩余/总内存："+ Formatter.formatFileSize(this, availMem) + "/" + Formatter.formatFileSize(this, totalMem));
    }

    private void initUI() {
        tv_task_count = (TextView) findViewById(R.id.tv_task_count);
        tv_memory = (TextView) findViewById(R.id.tv_memory);
    }
}
