package com.deanlib.plantern.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.deanlib.plantern.Plantern;


/**
 * 版本相关
 * <p>
 * Created by dean on 2017/4/24.
 */

public class VersionUtils {

    /**
     * APP版本名（显示用）
     *
     * @return
     */
    public static String getAppVersionName() {
        return getAppVersionName(Plantern.getAppContext().getPackageName());
    }

    /**
     * APP版本名（显示用）
     *
     * @return
     */
    public static String getAppVersionName(String packageName) {

        String versionName = "";

        try {
            PackageManager packageManager = Plantern.getAppContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return versionName;
        }
        return versionName;
    }

    /**
     * APP版本号（开发用）
     *
     * @return
     */
    public static int getAppVersionCode() {
        return getAppVersionCode(Plantern.getAppContext().getPackageName());
    }

    /**
     * APP版本号（开发用）
     *
     * @return
     */
    public static int getAppVersionCode(String packageName) {
        int versionCode = 0;

        try {
            PackageManager packageManager = Plantern.getAppContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;

    }

}
