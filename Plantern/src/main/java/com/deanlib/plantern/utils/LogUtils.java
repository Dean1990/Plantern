package com.deanlib.plantern.utils;

import android.text.TextUtils;
import android.util.Log;


import com.deanlib.plantern.Plantern;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志类
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *
 * @author dean
 * @date 2017年3月16日
 */

public class LogUtils {

    public static String customTagPrefix = "plantern_log";

    private static boolean isDebug;

    private static LogUtils dlog;

    private static boolean writeable;

    private static File dirFile;

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

    static int logCount;

    static FileWriter writer;


    private LogUtils() {

        this.isDebug = Plantern.isDebug();

    }

    /**
     * 单例
     *
     * @return
     */
    public static LogUtils getInstance() {

        if (dlog == null) {

            dlog = new LogUtils();

        }

        return dlog;
    }

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    /**
     * 开启日志记录
     * <p>
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     *
     * @param dir
     * @throws Exception
     */
    public void logWriteable(String dir) throws Exception {

        writeable = true;

        dirFile = new File(dir);

        if (!dirFile.exists()) {

            if (!dirFile.mkdirs()) {

                throw new Exception("save log dir don't create");

            }

        }

    }

    /**
     * 关闭日志记录
     *
     * @throws IOException
     */
    public void logUnwriteable() throws IOException {

        writeable = false;

        if (writer != null) {

            writer.flush();

            writer.close();

        }

    }

    private static void writeLog(String log) {

        if (writeable) {

            writer = getLogFile();

            if (writer != null) {

                try {
                    writer.append(sdf.format(new Date(System.currentTimeMillis())) + " " + log + "\n\r");

                    writer.flush();

                    logCount++;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    private static FileWriter getLogFile() {

        if (logCount < 10000 && writer != null) {

            return writer;

        } else {

            logCount = 0;

            if (dirFile != null && dirFile.exists()) {

                try {

                    File file = new File(dirFile, sdf.format(new Date(System.currentTimeMillis())) + ".log");

                    return new FileWriter(file);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return null;

        }

    }

    public static void d(String content) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.d(tag, content);

        writeLog(tag + " -> " + content);
    }

    public static void d(String content, Throwable tr) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.d(tag, content, tr);
    }

    public static void e(String content) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.e(tag, content);

        writeLog(tag + " -> " + content);
    }

    public static void e(String content, Throwable tr) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.e(tag, content, tr);

        writeLog(tag + " -> " + content);
    }

    public static void i(String content) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.i(tag, content);

        writeLog(tag + " -> " + content);
    }

    public static void i(String content, Throwable tr) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.i(tag, content, tr);

        writeLog(tag + " -> " + content);

    }

    public static void v(String content) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.v(tag, content);

        writeLog(tag + " -> " + content);
    }

    public static void v(String content, Throwable tr) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.v(tag, content, tr);

        writeLog(tag + " -> " + content);
    }

    public static void w(String content) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.w(tag, content);

        writeLog(tag + " -> " + content);
    }

    public static void w(String content, Throwable tr) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.w(tag, content, tr);

        writeLog(tag + " -> " + content);
    }

    public static void w(Throwable tr) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.w(tag, tr);

        writeLog(tag + " -> " + tr);
    }

    public static void wtf(String content) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.wtf(tag, content);

        writeLog(tag + " -> " + content);
    }

    public static void wtf(String content, Throwable tr) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.wtf(tag, content, tr);

        writeLog(tag + " -> " + content + "\n\r" + tr);
    }

    public static void wtf(Throwable tr) {
        if (!isDebug)
            return;
        String tag = generateTag();

        Log.wtf(tag, tr);

        writeLog(tag + " -> " + tr);
    }

    public boolean isWrite() {
        return writeable;
    }
}
