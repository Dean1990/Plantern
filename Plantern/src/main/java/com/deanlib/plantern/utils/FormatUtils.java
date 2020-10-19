package com.deanlib.plantern.utils;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
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
    public static String convertDateTimestampToString(long timestamp, String format) {

        Date date = new Date(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(date);

    }

    /**
     * 时间戳转带描述的字符串
     *
     * @param timestamp 时间戳
     * @return 小于6小时有文字描述，否则 "yyyy-MM-dd HH:mm:ss" 格式的字符串
     */
    public static String convertDateTimestampToDescribeString(long timestamp) {
        return convertDateTimestampToDescribeString(timestamp, DATE_FORMAT_YMDHMS);
    }


    /**
     * 时间戳转带描述的字符串
     *
     * @param timestamp 时间戳
     * @param format    格式
     * @return 小于6小时有文字描述，否则指定格式的字符串
     */
    public static String convertDateTimestampToDescribeString(long timestamp, String format) {

        long l = System.currentTimeMillis() - timestamp;

        if (l < 1000 * 60)
            return "刚刚";
        else if (l < 1000 * 60 * 2)
            return "1分钟前";
        else if (l < 1000 * 60 * 3)
            return "2分钟前";
        else if (l < 1000 * 60 * 4)
            return "3分钟前";
        else if (l < 1000 * 60 * 5)
            return "4分钟前";
        else if (l < 1000 * 60 * 10)
            return "5分钟前";
        else if (l < 1000 * 60 * 20)
            return "10分钟前";
        else if (l < 1000 * 60 * 30)
            return "20分钟前";
        else if (l < 1000 * 60 * 60)
            return "30分钟前";
        else if (l < 1000 * 60 * 60 * 2)
            return "1小时前";
        else if (l < 1000 * 60 * 60 * 3)
            return "2小时前";
        else if (l < 1000 * 60 * 60 * 4)
            return "3小时前";
        else if (l < 1000 * 60 * 60 * 5)
            return "4小时前";
        else if (l < 1000 * 60 * 60 * 6)
            return "5小时前";
        else if (l < 1000 * 60 * 60 * 7)
            return "6小时前";
        else
            return convertDateTimestampToString(timestamp, format);


    }

    /**
     * 字符串转时间戳
     *
     * @param string 字符串
     * @param format 格式
     * @return 时间戳
     */
    public static long convertDateStringToTimestamp(String string, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        try {
            Date date = sdf.parse(string);

            return date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

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
            return (num / 1000) + "千";
        else if (num < 100000000)
            return (num / 10000) + "万";
        else
            return (num / 100000000) + "亿";


    }

    /**
     * 毫秒转时长
     *
     * @param l 毫秒
     * @return h:m:s
     */
    public static String convertNumLongToDuration(long l) {

        l = l / 1000;

        String h = addZero((int) (l / 3600));

        String m = addZero((int) (l % 3600 / 60));

        String s = addZero((int) (l % 3600 % 60));

        if ("00".equals(h)) {
            return h + ":" + m + ":" + s;
        }
        return m + ":" + s;
    }

    private static String addZero(int h) {

        return h < 10 ? "0" + h : h + "";
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
        StringBuffer sb = new StringBuffer();
        if (h>0){
            sb.append(h+"小时");
        }
        if (m>0){
            sb.append(m+"分钟");
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
        StringBuffer sb = new StringBuffer();
        if (h>0){
            sb.append(h+"°");
        }
        if (m>0){
            sb.append(m+"’");
        }
        if (s>0){
            sb.append(s+"”");
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
     * 将字符串转为16进制
     * @param str
     * @return
     */
    public static String convertStr2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }
    /**
     * 将16进制转为字符串
     * @param hexStr
     * @return
     */
    public static String convertHexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

}