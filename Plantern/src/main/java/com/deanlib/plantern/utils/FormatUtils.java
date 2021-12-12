package com.deanlib.plantern.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;

import com.deanlib.plantern.Plantern;
import com.deanlib.plantern.R;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 格式化类
 * 区别于TextUtils更偏向于文字操作和编码相关
 * <p>
 * DATE:时间
 * NUM:数值
 * <p>
 * Created by dean on 16/5/10.
 */
public class FormatUtils {

    /**
     * format:yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * format:yyyy-MM-dd HH:mm
     */
    public static final String DATE_FORMAT_YMDHM = "yyyy-MM-dd HH:mm";

    /**
     * format:yyyy-MM-dd
     */
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";

    /**
     * format:MM-dd
     */
    public static final String DATE_FORMAT_MD = "MM-dd";


    /**
     * 时间戳转字符串
     *
     * @param timestamp 时间戳
     * @param format    格式
     * @return 指定格式的字符串
     */
    public static String convertTimestampToString(long timestamp, String format) {

        Date date = new Date(timestamp);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(date);

    }


    /**
     * 时间戳转带描述的字符串
     * @param timestamp
     * @return yyyy-MM-dd
     */
    public static String convertTimestampToDescribe(long timestamp){
        return convertTimestampToDescribe(timestamp, FormatUtils.DATE_FORMAT_YMD);
    }


    /**
     * 时间戳转带描述的字符串
     *
     * @param timestamp 时间戳
     * @param format    格式
     * @return
     */
    public static String convertTimestampToDescribe(long timestamp, String format) {

        String formatStr = "";
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 1000 * 60){ //1分钟以内
            formatStr = Plantern.getAppContext().getString(R.string.p_just_now);
        }else if (diff < 1000 * 3600){ //1小时以内
            formatStr = Plantern.getAppContext().getString(R.string.p_minute, diff/(1000*60));
        }else if (diff < 1000 * 3600 * 24) { //1天以内
            formatStr = Plantern.getAppContext().getString(R.string.p_hour, diff/(1000*3600));
        }else if (diff < 1000 * 3600 * 48){ //昨天
            formatStr = Plantern.getAppContext().getString(R.string.p_yesterday);
        }else {
            formatStr = convertTimestampToString(timestamp, format);
        }

        return formatStr;


    }

    /**
     * 时间戳转带描述的字符串 精确的
     * @param timestamp
     * @return
     * 18:20
     * 昨天 19:23
     * 周一 06:10
     * 2-18 02:46
     * 2019-4-8 11:04
     */
    public static String convertTimestampToExactDescribe(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);//周一为一周的开始
        calendar.setMinimalDaysInFirstWeek(7);//一周至少7天,解决，跨年获取的周出现的问题

        //当前年 月 周
        int cYear = calendar.get(Calendar.YEAR);
        int cWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        //获取昨天的时间范围
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayStart = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
        long yesterdayStart = calendar.getTimeInMillis();

        //给定的时间的年 月 周
        calendar.setTimeInMillis(timestamp);
        int year = calendar.get(Calendar.YEAR);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

        StringBuilder sb = new StringBuilder();
        if (cWeekOfYear == weekOfYear){
            //同一周
            if (timestamp > todayStart){
                //今天 什么也不加
            } else if (timestamp < todayStart && timestamp >= yesterdayStart ){
                //昨天
                sb.append(Plantern.getAppContext().getString(R.string.p_yesterday)).append(" ");
            }else {
                //其他的显示 周一， 周二 ...
                String[] weeks = Plantern.getAppContext().getResources().getStringArray(R.array.p_weeks);
                //周的取值是 1-7
                sb.append(weeks[calendar.get(Calendar.DAY_OF_WEEK)]).append(" ");
            }

        }else{
            if (cYear != year){
                //不是同一年
                sb.append(FormatUtils.convertTimestampToString(timestamp,FormatUtils.DATE_FORMAT_YMD)).append(" ");
            }
            sb.append(FormatUtils.convertTimestampToString(timestamp,FormatUtils.DATE_FORMAT_MD)).append(" ");
        }

        sb.append(FormatUtils.convertTimestampToString(timestamp,"HH:mm"));

        return sb.toString();
    }

    /**
     * 格式化 年月日，对小于10的数字补0
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String formatYMD(int year, int month, int day){
        return String.valueOf(year) + addZeroFront(month, 2) + addZeroFront(day, 2);
    }

    /**
     * 字符串转时间戳
     *
     * @param string 字符串
     * @param format 格式
     * @return 时间戳
     */
    public static long convertStringToTimestamp(String string, String format) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        long l = 0;
        try {
            Date date = sdf.parse(string);
            if (date != null) {
                l = date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * 格式化数值转带单位的字符串
     *
     * @param num 数值
     * @return 大于1000时带单位
     */
    public static String formatNum(long num) {

        if (num < 1000)
            return num + "";
        else if (num < 10000)
            return Plantern.getAppContext().getString(R.string.p_thousand, num / 1000);
        else if (num < 100000000)
            return Plantern.getAppContext().getString(R.string.p_ten_thousand, num / 10000);
        else
            return Plantern.getAppContext().getString(R.string.p_a_hundred_million, num / 100000000);

    }

    /**
     * 毫秒转时长
     *
     * @param timestamp 毫秒
     * @return h:m:s
     */
    public static String convertTimestampToDuration(long timestamp) {

        long second = timestamp / 1000;
        String h = addZeroFront((int) (second / 3600), 2);
        String m = addZeroFront((int) (second % 3600 / 60), 2);
        String s = addZeroFront((int) (second % 3600 % 60), 2);
        if ("00".equals(h)) {
            return h + ":" + m + ":" + s;
        }
        return m + ":" + s;
    }

    /**
     * 在前面补0
     * @param n
     * @param length 总长度 包括n
     * @return
     */
    public static String addZeroFront(int n, int length) {
        StringBuilder str = new StringBuilder(String.valueOf(n));
        for(int i = str.length();i<length;i++){
            str.insert(0, "0");
        }
        return str.toString();
    }

    /**
     * 格式化计算机容量单位
     * 可参考
     *
     * @param size 单位byte
     * @return
     * @see android.text.format.Formatter#formatFileSize(Context, long)
     */
    public static String formatFileSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 格式化重量单位
     *
     * @param weight 单位g
     * @return
     */
    public static String formatWeight(float weight) {
        //按g换算
        if (weight >= 1000) {
            return (weight / 1000) + "kg";
        } else {
            return weight + "g";
        }
    }

    /**
     * 格式化人民币
     *
     * @param money
     * @return
     */
    public static String formatRMB(float money) {
        BigDecimal decimal = new BigDecimal(money);
        return formatRMB(decimal.setScale(2, BigDecimal.ROUND_DOWN).toString());
    }

    /**
     * 格式化人民币
     * @param money
     * @return
     */
    public static String formatRMB(String money) {
        return "¥ " + money;
    }

    /**
     * 隐藏手机号中间部分
     *
     * @param num
     * @return
     */
    public static String hidePhoneNum(String num) {

        if (ValidUtils.isMobileNum(num)) {

            return num.substring(0, 3) + "****" + num.substring(num.length() - 4);
        } else return num;
    }

    /**
     * 格式 删除线
     *
     * @param str
     * @return
     */
    public static SpannableString formatStrike(String str) {
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new StrikethroughSpan(), 0, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 字符串转数值
     *
     * @param str
     * @return
     */
    public static float convertStringToNum(String str) {
        if (str != null) {
            if (Pattern.matches("\\d+\\.?\\d*", str)) {
                return Float.parseFloat(str);
            }
        }
        return 0;
    }

    /**
     * 格式化时间
     * @param l
     * @param emptyVal
     * @return 3小时23分钟
     */
    public static String formatTime(long l, String emptyVal){
        l = l/1000;
        int h = (int) (l / 3600);
        int m = (int)(l % 3600 / 60);
//        int s = (int) (l % 3600 % 60);
        StringBuilder sb = new StringBuilder();
        if (h>0){
            sb.append(Plantern.getAppContext().getString(R.string.p_hour, h));
        }
        if (m>0){
            sb.append(Plantern.getAppContext().getString(R.string.p_minute, m));
        }
        if (sb.toString().trim().isEmpty()){
            return emptyVal;
        }else {
            return sb.toString();
        }

    }

    /**
     * 用撇标示的速度
     * 小时不用撇（°），分用一撇 （’），秒用两撇（”）
     * 1°=60′，1′=60″ ，1°=3600″。
     * @param l 单位 秒
     * @return
     */
    public static String formatSpeed(long l){
        int h = (int) (l / 3600);
        int m = (int)(l % 3600 / 60);
        int s = (int) (l % 3600 % 60);
        StringBuilder sb = new StringBuilder();
        if (h>0){
            sb.append(h).append("°");
        }
        if (m>0){
            sb.append(m).append("’");
        }
        if (s>0){
            sb.append(s).append("”");
        }
        if (sb.toString().trim().isEmpty()){
            return "0";
        }else {
            return sb.toString();
        }

    }

    /**
     * 对小数取舍
     * @param value
     * @param scale
     * @return
     */
    public static String formatFloat(float value, int scale){
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(scale, BigDecimal.ROUND_DOWN).toString();
    }

    /**
     * 格式化 大于 limit 追加 "+"
     * @param num
     * @param limit
     * @return
     */
    public static String formatNumToPlus(long num, long limit){
        StringBuilder sb = new StringBuilder();
        if (num > limit){
            sb.append(limit).append("+");
        }else {
            sb.append(num);
        }
        return sb.toString();
    }

}