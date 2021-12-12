package com.deanlib.plantern.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.view.WindowInsetsCompat;


import com.deanlib.plantern.Plantern;
import com.deanlib.plantern.R;
import com.deanlib.plantern.entity.AppInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 应用操作相关
 * 打开，分享等
 *
 * @author dean
 * @time 2018/6/28 下午3:29
 */
public class AppUtils {

    public static final int REQ_CODE_UNKNOWN_APP_SOURCES = 5100;

    private static long exitTime;

    public static boolean exit() {
        return exit(Plantern.getAppContext().getString(R.string.p_again_exit));
    }

    public static boolean exit(String msg) {
        return exit(msg, 2000);
    }

    /**
     * 按两次退出
     *
     * @param msg
     * @param interval
     * @return
     */
    public static boolean exit(String msg, long interval) {
        if (System.currentTimeMillis() - exitTime > interval) {
            PopupUtils.sendToast(msg);
            exitTime = System.currentTimeMillis();
            return false;
        }

        return true;
    }

    /**
     * 创建快捷方式
     * 需要声明权限 com.android.launcher.permission.INSTALL_SHORTCUT
     *
     * @param act          点击图标时打开的页面
     * @param iconResId    图标
     * @param appnameResId 名称
     */
    public static void createShortCut(Activity act, int iconResId,
                                      int appnameResId) {
        createShortCut(act, iconResId, act.getString(appnameResId));

    }

    /**
     * 创建快捷方式
     * 需要声明权限 com.android.launcher.permission.INSTALL_SHORTCUT
     *
     * @param act       点击图标时打开的页面
     * @param iconResId 图标
     * @param appname   名称
     */
    public static void createShortCut(Activity act, int iconResId,
                                      String appname) {

        // com.android.launcher.permission.INSTALL_SHORTCUT

        Intent shortcutintent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                appname);
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                act.getApplicationContext(), iconResId);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
                new Intent(act.getApplicationContext(), act.getClass()));
        // 发送广播
        act.sendBroadcast(shortcutintent);
    }

    public static void installApk(Uri uri) {
        installApk(Plantern.getAppContext(), uri);
    }

    /**
     * 安装APK
     *
     * @param context
     * @param uri     android 7.0 以下 可以使用 Uri.fromFile(file) 得到uri
     *                android 7.0及以上，需要配置 provider (自行百度) ，使用 FileProvider.getUriForFile(context, "provider的authorities", file)
     */
    public static void installApk(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 7.0+以上版本
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        context.startActivity(intent);
    }

    public static void callPhone(String phoneNum) {
        callPhone(Plantern.getAppContext(), phoneNum);
    }

    /**
     * 打电话
     * 权限 android.permission.CALL_PHONE
     *
     * @param context
     * @param phoneNum
     */
    public static void callPhone(Context context, String phoneNum) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNum));
        context.startActivity(intent);
    }

    public static void openBrowser(String url) {
        openBrowser(Plantern.getAppContext(), url);
    }

    /**
     * 打开第三方浏览器
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 打开设置中的未知来源安装（android 8.0及以上）
     * <p>
     * getPackageManager().canRequestPackageInstalls() 判断该设置是否打开
     * 如果设置被打开，返回时则会触发 onActivityResult ，否则不触发
     */
    public static void openSettingsUnknownAppSources(Activity activity) {
        Uri packageURI = Uri.parse("package:" + Plantern.getAppContext().getPackageName());//设置包名，可直接跳转当前软件的设置页面
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        activity.startActivityForResult(intent, REQ_CODE_UNKNOWN_APP_SOURCES);
    }

    public static boolean openMarket(String appPkg, String marketPkg) {
        return openMarket(Plantern.getAppContext(), appPkg, marketPkg);
    }

    /**
     * 跳转应用商店.
     *
     * @param context   {@link Context}
     * @param appPkg    包名
     * @param marketPkg 应用商店包名
     * @return {@code true} 跳转成功 <br> {@code false} 跳转失败
     */
    public static boolean openMarket(Context context, String appPkg, String marketPkg) {
        Uri uri = Uri.parse("market://details?id=" + appPkg);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (marketPkg != null) {// 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
            intent.setPackage(marketPkg);
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 跳转到指定应用的首页
     */
    public static void openAppLaunchPage(@NonNull String packageName) {
        Intent intent = Plantern.getAppContext().getPackageManager().getLaunchIntentForPackage(packageName);
        Plantern.getAppContext().startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    public static void openAppSpecificPage(@NonNull String packageName, @NonNull String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Plantern.getAppContext().startActivity(intent);
    }

    public static void shareImage(Uri uri, String dialogTitle) {
        shareImage(Plantern.getAppContext(), uri, dialogTitle);
    }

    /**
     * 分享图片
     */
    public static void shareImage(Context context, Uri uri, String dialogTitle) {
        if (uri != null) {
            Intent share_intent = new Intent();
            share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
            share_intent.setType("image/*");  //设置分享内容的类型
            share_intent.putExtra(Intent.EXTRA_STREAM, uri);
            //创建分享的Dialog
            share_intent = Intent.createChooser(share_intent, dialogTitle);
            context.startActivity(share_intent);
        }
    }

    public static void restart() {
        restart(Plantern.getAppContext());
    }

    /**
     * 重启
     *
     * @param context
     */
    public static void restart(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /**
     * 关闭 Android P 对使用非SDK API的提示框
     * 治标不治本，但是有些第三方类库也是我们不能左右的
     */
    private void closeAPICompatibilityDialog() {
        try {
            Class clazz = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = clazz.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class clazz2 = Class.forName("android.app.ActivityThread");
            Method declaredMethod = clazz2.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = clazz2.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(invoke, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取手机中所有的app
     * 耗时，建议开启线程
     *
     * @param userInstall 用户安装的（不包含系统的）
     * @return
     */
    public static List<AppInfo> getAllApp(boolean userInstall) {
        PackageManager pm = Plantern.getAppContext().getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);  // 获取所以已安装的包
        List<AppInfo> appInfos = new ArrayList<>();
        for (PackageInfo packageInfo : installedPackages) {
            AppInfo info = new AppInfo();
            String packageName = packageInfo.packageName;  // 包名
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;  // 应用信息
            String name = applicationInfo.loadLabel(pm).toString();  // 应用名称
            Drawable icon = applicationInfo.loadIcon(pm);  // 应用图标
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            info.setName(name);
            info.setPackageName(packageName);
            info.setIcon(icon);
            info.setVersionName(versionName);
            info.setVersionCode(versionCode);
            // 状态机,通过01状态来表示是否具备某些属性和功能
            int flags = applicationInfo.flags;  // 获取应用标记
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo
                    .FLAG_EXTERNAL_STORAGE) {
                //安装在sdcard
                info.setRom(false);

            } else {
                //安装在手机
                info.setRom(true);
            }

            if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo
                    .FLAG_SYSTEM) {
                //系统应用
                info.setUser(false);

            } else {
                //用户应用
                info.setUser(true);
            }

            if (userInstall) {
                if (info.isUser()) {
                    appInfos.add(info);
                }
            } else {
                appInfos.add(info);
            }
        }

        return appInfos;
    }

    /**
     * 获得桌面级应用
     *
     * @return
     */
    public static List<AppInfo> getAllAppOfDesktop() {
        PackageManager packageManager = Plantern.getAppContext().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                0);
        List<AppInfo> appInfos = new ArrayList<>();
        for (ResolveInfo ri : resolveInfo) {
            boolean exist = false;
            String packageName = ri.activityInfo.packageName;  // 包名
            //去重
            for (AppInfo info : appInfos) {
                if (info.getPackageName().equals(packageName)) {
                    exist = true;
                    break;
                }
            }
            if (exist) continue;

            AppInfo info = new AppInfo();
            ApplicationInfo applicationInfo = ri.activityInfo.applicationInfo;  // 应用信息
            String name = applicationInfo.loadLabel(packageManager).toString();  // 应用名称
            Drawable icon = applicationInfo.loadIcon(packageManager);  // 应用图标
//            String versionName = VersionUtils.getAppVersionName(packageName);
//            int versionCode = VersionUtils.getAppVersionCode(packageName);
            info.setName(name);
            info.setPackageName(packageName);
            info.setIcon(icon);
//            info.setVersionName(versionName);
//            info.setVersionCode(versionCode);
            appInfos.add(info);

        }
        return appInfos;
    }

    /**
     * 判断是否在 电池不优化名单
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations() {
        return isIgnoringBatteryOptimizations(Plantern.getAppContext().getPackageName());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(String packageName) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) Plantern.getAppContext().getSystemService(Context.POWER_SERVICE);

        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName);
        }

        return isIgnoring;
    }

    /**
     * 请求允许 不优化
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestIgnoreBatteryOptimizations() {

        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + Plantern.getAppContext().getPackageName()));
            Plantern.getAppContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求允许 不优化
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestIgnoreBatteryOptimizations(Activity context, int requstCode) {
        requestIgnoreBatteryOptimizations(context, context.getPackageName(), requstCode);
    }

    /**
     * 请求允许 不优化
     * 这里 并不能做到 调用基本应用的 不优化，只能调用 自己的设置，
     * 还没有找到原因，是因为权限问题，还是操作有误？
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestIgnoreBatteryOptimizations(Activity context, String packageName, int requstCode) {

        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivityForResult(intent, requstCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断服务是否在运行
     *
     * @param className
     * @return
     */
    public static boolean isServiceRunning(String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                Plantern.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 判断应用是否运行在前端
     *
     * @return
     */
    public static boolean isAppInForeground() {
        return isAppInForeground(Plantern.getAppContext().getPackageName());
    }

    /**
     * 判断应用是否运行在前端
     *
     * @param packageName
     * @return
     */
    public static boolean isAppInForeground(String packageName) {
        ActivityManager activityManager = (ActivityManager) Plantern.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(packageName)) {
                    return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                }
            }
        }
        return false;
    }


    /**
     * 获取 Activity 布局 View
     *
     * @param ac
     * @return
     */
    public static View getContentView(Activity ac) {
        ViewGroup view = (ViewGroup) ac.getWindow().getDecorView();
        FrameLayout content = (FrameLayout) view.findViewById(android.R.id.content);
        return content.getChildAt(0);
    }


    /**
     * 创建通知
     *
     * @param context
     * @param notifyId
     * @param channelId
     * @param channelName
     * @param largeIconRid
     * @param smallIconRid
     * @param ticker
     * @param contentTitle
     * @param contentText
     * @param bigText
     * @param pendingIntent
     * @return
     */
    public static Notification createNotification(
            Context context, int notifyId, String channelId, String channelName,
            int largeIconRid, int smallIconRid, String ticker, String contentTitle, String contentText,
            String bigText, PendingIntent pendingIntent
    ) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);
        notificationBuilder.setSmallIcon(smallIconRid);
        //builder.setSmallIcon(android.os.Build.VERSION.SDK_INT>20?R.drawable.ic_launcher_round:R.drawable.ic_launcher);
        //builder.setColor(context.getResources().getColor(R.color.icon_blue));
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIconRid));
        notificationBuilder.setAutoCancel(true);
//            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder.setTicker(ticker);
        notificationBuilder.setContentTitle(contentTitle);
        notificationBuilder.setContentText(contentText);
//            notificationBuilder.setProgress(0, 0, true);
        notificationBuilder.setWhen(System.currentTimeMillis());
        if (!TextUtils.isEmpty(bigText)) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(bigText));
        }
        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        } else {
            notificationBuilder.setSound(null);
        }

        return notificationBuilder.build();
    }

    /**
     * 展示通知
     *
     * @param context
     * @param notifyId
     * @param channelId
     * @param channelName
     * @param largeIconRid
     * @param smallIconRid
     * @param ticker
     * @param contentTitle
     * @param contentText
     * @param bigText
     * @param pendingIntent
     */
    public static void showNotification(
            Context context, int notifyId, String channelId, String channelName,
            int largeIconRid, int smallIconRid, String ticker, String contentTitle, String contentText,
            String bigText, PendingIntent pendingIntent
    ) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notifyId, createNotification(
                context, notifyId, channelId, channelName, largeIconRid, smallIconRid, ticker,
                contentTitle, contentText, bigText, pendingIntent
        ));
    }

    /**
     * 获取当前进程名
     *
     * @return
     */
    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void setSystemAppbarTranslucent(Activity activity){
        int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            attributes.flags |= flagTranslucentNavigation;
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            attributes.flags |= flagTranslucentStatus | flagTranslucentNavigation;
        }
        window.setAttributes(attributes);

        //系统弃用了
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            activity.getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Window window = activity.getWindow();
//            View decorView = window.getDecorView();
//
//            WindowInsetsControllerCompat wic = new WindowInsetsControllerCompat(window, decorView);
//
//            wic.setAppearanceLightStatusBars(light); // true or false as desired.
//
//            // And then you can set any background color to the status bar.
////            window.setStatusBarColor(Color.WHITE);
//        }


    }

}
