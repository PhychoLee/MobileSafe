package com.llf.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class SystemInfoUtils {

	/**
	 * 检测service是否运行工具
	 * @param context
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String serviceName){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			String className = runningServiceInfo.service.getClassName();
//			System.out.println(className);
			if (className.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 获得进程数
	 * @param context
	 * @return
     */
	public static int getTaskCount(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//得到应用进程
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

		return runningAppProcesses.size();
	}

	/**
	 * 获得剩余内存
	 * @param context
	 * @return
     */
	public static long getAvailMen(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//得到应用进程
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(outInfo);
		//剩余内存
		return outInfo.availMem;
	}

	/**
	 * 获取总内存
	 * @param context
	 * @return
     */
	public static long getTotalMen(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//得到应用进程
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(outInfo);
		//总内存,适配低版本
		try {
			FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String readLine = reader.readLine();

			StringBuffer sb = new StringBuffer();
			for (char c: readLine.toCharArray()) {
				if (c >= '0' && c<= '9'){
					sb.append(c);
				}
			}
			long totalMen = Long.parseLong(sb.toString()) * 1024;
			return totalMen;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
