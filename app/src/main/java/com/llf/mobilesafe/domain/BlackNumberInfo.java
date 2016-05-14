package com.llf.mobilesafe.domain;

/**
 * Created by Lee on 2016/5/13.
 */
public class BlackNumberInfo {
    private String number;

    /**
     * 1,全部拦截
     * 2,电话拦截
     * 3,短信拦截
     */
    private String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
