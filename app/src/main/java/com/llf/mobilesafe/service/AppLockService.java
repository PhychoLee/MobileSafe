package com.llf.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.llf.mobilesafe.activity.EnterPasswordActivity;
import com.llf.mobilesafe.db.dao.AppLockDao;

import java.util.List;

public class AppLockService extends Service {

    private ActivityManager activityManager;
    private AppLockDao appLockDao;
    private boolean flag;
    private WatchDogReceiver receiver;

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

        receiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.llf.mobilesafe.stopprotect");
        //注册息屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver,filter);

        startWatchDog();
    }

    //临时停止保护的包名
    private String tempStopProtectPackageName;

    private class WatchDogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("com.llf.mobilesafe.stopprotect")){
                //获取到停止保护的对象

                tempStopProtectPackageName = intent.getStringExtra("packageName");
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                tempStopProtectPackageName = null;
                // 让狗休息
                flag = false;
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //让狗继续干活
                if(flag == false){
                    startWatchDog();
                }
            }
        }

    }


    private void startWatchDog() {
        new Thread(){
            @Override
            public void run() {
                //在后台一直运行，在子线程防止主线程阻塞
                flag = true;
                while(flag){
                    //获取正在运行的任务栈
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
                    //获取最上面的程序
                    ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningAppProcesses.get(0);
                    String processName = runningAppProcessInfo.processName;
                    if (appLockDao.query(processName)){
                        System.out.println(processName+"在程序锁数据库里");
                        //转跳到输入密码界面
                        Intent intent = new Intent(AppLockService.this, EnterPasswordActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("package", processName);
                        startActivity(intent);
                    }else {
                        System.out.println(processName+"不bubu在程序锁数据库里");
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;
    }
}
