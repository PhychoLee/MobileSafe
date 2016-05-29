package com.llf.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.llf.mobilesafe.db.dao.AppLockDao;

import java.util.List;

public class AppLockService extends Service {

    private ActivityManager activityManager;
    private AppLockDao appLockDao;

    public AppLockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        appLockDao = new AppLockDao(this);
        
        startWatchDog();
    }

    private void startWatchDog() {
        new Thread(){
            @Override
            public void run() {
                //在后台一直运行，在子线程防止主线程阻塞

                //获取正在运行的任务栈
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
                //获取最上面的程序
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningAppProcesses.get(0);
                String processName = runningAppProcessInfo.processName;
                if (appLockDao.query(processName)){
                    System.out.println(processName+"在程序锁数据库里");
                }else {
                    System.out.println(processName+"不bubu在程序锁数据库里");
                }

            }
        }.start();
    }
}
