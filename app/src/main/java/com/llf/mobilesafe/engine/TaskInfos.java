package com.llf.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee on 2016/5/21.
 */
public class TaskInfos {

    public static List<TaskInfo> getTaskInfos(Context context) {
        List<TaskInfo> taskInfos = new ArrayList<>();

        PackageManager packageManager = context.getPackageManager();
        //进程管理
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取运行的所有程序
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            TaskInfo taskInfo = new TaskInfo();

            //进程名
            String processName = runningAppProcessInfo.processName;
            taskInfo.setPackageName(processName);

            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                //图标
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                //应用名
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();

                //内存占用
                Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
                long totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty() *1024;

                taskInfo.setIcon(icon);
                taskInfo.setAppName(appName);
                taskInfo.setMemory(totalPrivateDirty);

                //应用标记
                int flags = packageInfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //系统应用
                    taskInfo.setUserApp(false);
                } else {
                    //用户应用
                    taskInfo.setUserApp(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
                taskInfo.setAppName(processName);
                taskInfo.setIcon(context.getResources().getDrawable(
                        R.drawable.ic_launcher2));
            }

            taskInfos.add(taskInfo);
        }

        return taskInfos;
    }
}
