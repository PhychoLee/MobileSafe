package com.llf.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Lee on 2016/5/16.
 */
public class AppInfo {
    //app图标
    private Drawable icon;
    private String appName;
    private long appSize;
    private String appPackageName;

    //是否为用户app
    //true表示为用户app
    //false表示为系统app
    private boolean userApp;

    //安装位置是否为Rom
    //true表示为Rom
    //false表示为SD
    private boolean isRom;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setRom(boolean rom) {
        isRom = rom;
    }
}
