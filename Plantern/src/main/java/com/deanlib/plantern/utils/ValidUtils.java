package com.deanlib.plantern.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证相关
 * <p>
 * Created by dean on 2017/4/24.
 */

public class ValidUtils {

    /**
     * 验证邮箱地址是否正确
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {

        if (android.text.TextUtils.isEmpty(email))
            return false;

        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 验证手机号码
     *
     * @param mobileNum
     * @return
     */
    public static boolean isMobileNum(String mobileNum) {

        if (android.text.TextUtils.isEmpty(mobileNum))
            return false;

        if (mobileNum.length() > 11) {
            return false;
        }

        boolean result = false;

        try {

            Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(16[0-9]))\\d{8}$");

            Matcher m = p.matcher(mobileNum);

            result = m.matches();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }

    /**
     * 验证是否是完整的网址
     *
     * @param url
     * @return
     */
    public static boolean isHttpURL(String url) {

        if (android.text.TextUtils.isEmpty(url))
            return false;

        String str = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(url);

        return m.matches();

    }

    /**
     * 验证是否是整数
     * 包括正整数，负整数和0
     *
     * @param num
     * @return
     */
    public static boolean isInteger(String num) {

        if (android.text.TextUtils.isEmpty(num))
            return false;

        return Pattern.matches("[+-]?\\d+", num);

    }

    /**
     * 验证是否是小数
     *
     * @param num
     * @return
     */
    public static boolean isDecimals(String num) {

        if (android.text.TextUtils.isEmpty(num))
            return false;

        return Pattern.matches("(([+-]?\\d+)|(\\d*))\\.\\d+", num);
    }

    /**
     * 验证是否是身份证
     *
     * @param num
     * @return
     */
    public static boolean isIDCard(String num) {

        if (android.text.TextUtils.isEmpty(num))
            return false;

        //第二代身份证正则表达式(18位)
        if (Pattern.matches("^\\d{10}((0[1-9])|(1[0-2]))((0[1-9]|([1-2][0-9]))|(3[0-1]))\\d{3}[\\dXx]$", num)) {

            // 第二代身份证 国家规范
            int i = 0;
            String r = "error";
            String lastnumber = "";

            i += Integer.parseInt(num.substring(0, 1)) * 7;
            i += Integer.parseInt(num.substring(1, 2)) * 9;
            i += Integer.parseInt(num.substring(2, 3)) * 10;
            i += Integer.parseInt(num.substring(3, 4)) * 5;
            i += Integer.parseInt(num.substring(4, 5)) * 8;
            i += Integer.parseInt(num.substring(5, 6)) * 4;
            i += Integer.parseInt(num.substring(6, 7)) * 2;
            i += Integer.parseInt(num.substring(7, 8)) * 1;
            i += Integer.parseInt(num.substring(8, 9)) * 6;
            i += Integer.parseInt(num.substring(9, 10)) * 3;
            i += Integer.parseInt(num.substring(10, 11)) * 7;
            i += Integer.parseInt(num.substring(11, 12)) * 9;
            i += Integer.parseInt(num.substring(12, 13)) * 10;
            i += Integer.parseInt(num.substring(13, 14)) * 5;
            i += Integer.parseInt(num.substring(14, 15)) * 8;
            i += Integer.parseInt(num.substring(15, 16)) * 4;
            i += Integer.parseInt(num.substring(16, 17)) * 2;
            i = i % 11;
            lastnumber = num.substring(17, 18);
            if (i == 0) {
                r = "1";
            }
            if (i == 1) {
                r = "0";
            }
            if (i == 2) {
                r = "x";
            }
            if (i == 3) {
                r = "9";
            }
            if (i == 4) {
                r = "8";
            }
            if (i == 5) {
                r = "7";
            }
            if (i == 6) {
                r = "6";
            }
            if (i == 7) {
                r = "5";
            }
            if (i == 8) {
                r = "4";
            }
            if (i == 9) {
                r = "3";
            }
            if (i == 10) {
                r = "2";
            }
            if (r.equals(lastnumber.toLowerCase())) {
                return true;
            }
        } else if (Pattern.matches("^\\d{8}((0[1-9])|(1[0-2]))((0[1-9]|([1-2][0-9]))|(3[0-1]))\\d{3}$", num)) {
            //第一代身份证正则表达式(15位)
            return true;
        }

        return false;
    }

    /**
     * 验证是否是金钱金额
     *
     * @param num
     * @return
     */
    public static boolean isMoney(String num) {

        if (android.text.TextUtils.isEmpty(num))
            return false;

        return Pattern.matches("\\d+(\\.\\d{1,2})?", num);
    }


}
