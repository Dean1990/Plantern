package com.deanlib.plantern.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import com.deanlib.plantern.Plantern;
import com.deanlib.plantern.R;
import com.deanlib.plantern.utils.network.NetworkManager;

import java.io.File;

/**
 * 设备相关 是全局的东西与 AppUtils区分开，后者是针对某个应用的
 * <p>
 * Created by dean on 2017/4/24.
 */

public class DeviceUtils {

    /**
     * 屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {

        WindowManager wm = (WindowManager) Plantern.getAppContext().getSystemService(Context.WINDOW_SERVICE);

        Display defaultDisplay = wm.getDefaultDisplay();

        return defaultDisplay.getWidth();

    }

    /**
     * 屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {

        WindowManager wm = (WindowManager) Plantern.getAppContext().getSystemService(Context.WINDOW_SERVICE);

        Display defaultDisplay = wm.getDefaultDisplay();

        return defaultDisplay.getHeight();

    }

    /**
     * convert px to its equivalent dp
     * <p>
     * 将px转换为与之相等的dp
     */
    public static int px2dp(float pxValue) {
        final float scale = Plantern.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * convert dp to its equivalent px
     * <p>
     * 将dp转换为与之相等的px
     */
    public static int dp2px(float dipValue) {
        final float scale = Plantern.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * convert px to its equivalent sp
     * <p>
     * 将px转换为sp
     */
    public static int px2sp(float pxValue) {
        final float fontScale = Plantern.getAppContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * convert sp to its equivalent px
     * <p>
     * 将sp转换为px
     */
    public static int sp2px(float spValue) {
        final float fontScale = Plantern.getAppContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 打开或关闭软键盘
     */
    public static void toggleKeyboard() {

        InputMethodManager imm = (InputMethodManager) Plantern.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }

    /**
     * 隐藏软键盘
     *
     * @param act
     */
    public static void hideKeyboard(Activity act) {

        View view = act.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    /**
     * 弹出软键盘
     * 不能在onCreate,onStart,onResume等函数中调用，需要界面绘制完成才能调用
     *
     * @param act
     */
    public static void showKeyboard(Activity act) {
        View view = act.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) Plantern.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }


    /**
     * 模拟发送按键事件
     *
     * @param KeyCode
     */
    public static void sendKeyEvent(final int KeyCode) {
        new Thread() { // 不可在主线程中调用
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param act
     * @param bgAlpha 0.0-1.0
     */
    public static void backgroundAlpha(Activity act, float bgAlpha) {
        WindowManager.LayoutParams lp = act.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        act.getWindow().setAttributes(lp);
    }

    /**
     * 获取本地MAC地址
     * <p>
     * android.permission.ACCESS_WIFI_STATE
     *
     * @return
     */
    public static String getLocalMacAddress() {
        @SuppressLint("WifiManagerLeak") WifiManager wifi = (WifiManager) Plantern.getAppContext().getSystemService(Context.WIFI_SERVICE);
        @SuppressLint("MissingPermission") WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 网络是否可用
     * <p>
     * android.permission.ACCESS_NETWORK_STATE
     *
     * @return
     */
    public static boolean isNetworkConnected() {
        return NetworkManager.isNetworkConn();
    }


    /**
     * 获取当前网络类型
     * <p>
     * android.permission.ACCESS_NETWORK_STATE
     *
     * @return 0：没有网络 1：WIFI网络   2：WAP网络    3：NET网络
     */
    public static int getNetworkType() {
        return NetworkManager.getAPNType();
    }

    /**
     * 是否是WIFI网络
     *
     * @return
     */
    public static boolean isWifiConn() {

        return NetworkManager.isWifiConn();
    }

    /**
     * 获取基本的设备及应用信息
     *
     * @return
     */
    public static String getHandSetInfo() {
        String handSetInfo = String.format(Plantern.getAppContext().getString(R.string.p_handset_info),
                Build.MANUFACTURER, Build.MODEL, Build.VERSION.SDK_INT, Build.VERSION.RELEASE
                , VersionUtils.getAppVersionName(), VersionUtils.getAppVersionCode());
        return handSetInfo;
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private long getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    private long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    private long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    private long getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    /**
     * 只针对 字号的转换
     * @param sp
     * @return
     */
    public static float sp2pix(float sp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, sp,
                Plantern.getAppContext().getResources().getDisplayMetrics());
    }

    public static String getDeviceId(){
        DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(Plantern.getAppContext());
        String deviceId = deviceUuidFactory.getDeviceUuid().toString();
        return deviceId;
    }

}
