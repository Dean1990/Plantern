package com.deanlib.plantern.entity;

import android.graphics.drawable.Drawable;

/**
 * 应用信息
 * @auther Dean
 * @create 2020/9/26
 */
public class AppInfo {

    String name;
    String packageName;
    Drawable icon;
    boolean isRom;
    boolean isUser;
    String versionName;
    int versionCode;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setRom(boolean rom) {
        isRom = rom;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", icon=" + icon +
                ", isRom=" + isRom +
                ", isUser=" + isUser +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
