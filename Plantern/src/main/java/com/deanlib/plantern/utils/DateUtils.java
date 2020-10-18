package com.deanlib.plantern.utils;

import android.os.CountDownTimer;

import java.util.Calendar;

public class DateUtils {

    /**
     * 是否是闰年
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year) {
        return year % 400 == 0 || (year % 100 != 0 && year % 4 == 0);
    }

    /**
     * 得到月的最后一天
     * @param year
     * @param month
     * @return
     */
    public static int getEndDayOfMonth(int year,int month){
        int end = 30;
        switch (month){
            case 2:
                //判断2月 计算闰年
                if (DateUtils.isLeapYear(year)){
                    end = 29;
                }else {
                    end = 28;
                }
                break;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                end = 31;
                break;
        }
        return end;
    }

    /**
     * 四舍五入取月份
     * @return
     */
    public static int getMonthFloor(long v){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(v);
        int m = calendar.get(Calendar.MONTH);
        if (calendar.get(Calendar.DATE)>15){//粗略计算
            m++;
        }
        return m;
    }

    /**
     * 四舍五入取 天
     * @return
     */
    public static long getDayFloor(long v){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(v);
        if (calendar.get(Calendar.HOUR_OF_DAY)>=12){
            calendar.add(Calendar.DATE,1);
        }
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar.getTimeInMillis();
    }

    /**
     * 得到这一天的 0点0分0秒
     * @param time
     * @return
     */
    public static long getDayInitTime(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();//归零点
    }

    /**
     * 得到明天的 0点0分0秒
     * @param time
     * @return
     */
    public static long getTomorrowDayInitTime(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, 1);//明天
        return calendar.getTimeInMillis();//归零点
    }

    /**
     * 返回这个时间的初始小时时间
     * @param time
     * @return
     */
    public static long getHourInit(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }

    /**
     * 是否是同一天
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isOneDay(long time1,long time2){
        //由于时间戳 0 是1970-01-01 08:00:00 ，所以要减掉8小时再除以 天
        return (time1-28800000L)/86400000L == (time2-28800000L)/86400000L;
    }

    /**
     * 是否是同一周
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isOneWeek(long time1, long time2){
        return getWeekDesc(time1).equals(getWeekDesc(time2));
    }

    /**
     * 算个周出来
     * @param time
     * @return 3/20-3/26
     */
    public static String getWeekDesc(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //如果time是周日 , calendar 默认周日是第一天，直接取周一，会取到下一周去
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            calendar.add(Calendar.WEEK_OF_YEAR,-1);
        }
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        StringBuffer sb = new StringBuffer();
        sb.append(FormatUtils.convertDateTimestampToString(calendar.getTimeInMillis(),"M/d"));
        sb.append("-");
        calendar.add(Calendar.WEEK_OF_YEAR,1);
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        sb.append(FormatUtils.convertDateTimestampToString(calendar.getTimeInMillis(),"M/d"));
        return sb.toString();
    }

    /**
     * 获取这周的周一
     * @param time
     * @return
     */
    public static long getOneWeekMonday(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //如果time是周日 , calendar 默认周日是第一天，直接取周一，会取到下一周去
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            calendar.add(Calendar.WEEK_OF_YEAR,-1);
        }
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取这周的周日
     * @param time
     * @return
     */
    public static long getOneWeekSunday(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
            calendar.add(Calendar.WEEK_OF_YEAR,1);//因为周日下一周的第一天，如果不加一周，取到的是上一周的周日
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        }

        return calendar.getTimeInMillis();
    }

}
