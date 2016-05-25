package com.llf.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

/**
 * 杀死所有进程
 * Created by Lee on 2016/5/25.
 */
public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo: runningAppProcesses) {
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }

        Toast.makeText(context, "清理完成！", Toast.LENGTH_SHORT).show();
    }
}
