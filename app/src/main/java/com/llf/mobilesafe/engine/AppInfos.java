package com.llf.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.llf.mobilesafe.domain.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee on 2016/5/16.
 */
public class AppInfos {

    /**
     * 获得所有app数据
     * @param context
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context){
        List<AppInfo> appInfos = new ArrayList<>();

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo: installedPackages) {
            AppInfo appInfo = new AppInfo();

            Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);

            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();

            String packageName = packageInfo.packageName;

            //获取到资源目录
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            //将此资源目录封装到File对象中
            File file = new File(sourceDir);
            //应用大小
            long appSize = file.length();

            appInfo.setIcon(icon);
            appInfo.setAppName(appName);
            appInfo.setAppPackageName(packageName);
            appInfo.setAppSize(appSize);

            //应用标记
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                //系统应用
                appInfo.setUserApp(false);
            }else {
                //用户应用
                appInfo.setUserApp(true);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
                //SD
                appInfo.setRom(false);
            }else {
                //ROM
                appInfo.setRom(true);
            }

            appInfos.add(appInfo);
        }

        return appInfos;
    }
}
