package com.deanlib.plantern.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符相关
 * <p>
 * Created by dean on 2017/4/26.
 */

public class TextUtils {

    /**
     * 屏幕自适应网页的CSS
     **/
    public static final String CSS_FIT = "<head>\n" +
            "        <meta charset=\"utf-8\">\n" +
            "            <meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no\" />\n" +
            "                <meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />\n" +

            "                <meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\" />\n" +
            "                <meta name=\"format-detection\" content=\"telephone=no\" />\n" +
            "                <meta name=\"format-detection\" content=\"email=no\" />\n" +
            "                <style>img{height: auto;width:100%; }</style>\n" +
            "                <style>video{height: auto;width:100%; }</style>\n" +
            "                \n" +
            " </head>";

    /**
     * 剔除字符串中的html标签
     *
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {

        if (android.text.TextUtils.isEmpty(htmlStr)) {

            return "";
        }

        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

    /**
     * 获取字符串的长度，对双字符（包括汉字）按两位计数
     *
     * @param value
     * @return
     */
    public static int getStrLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 字符串转UNICODE码
     *
     * @param str
     * @return
     */
    public static String stringToUnicode(String str) {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            sb.append("\\u");
            j = (c >>> 8);
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF);
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }

    /**
     * UNICODE码转字符串
     *
     * @param str
     * @return
     */
    public static String unicodeToString(String str) {
        str = (str == null ? "" : str);
        if (!str.contains("\\u"))
            return str;

        StringBuilder sb = new StringBuilder(1000);

        for (int i = 0; i <= str.length() - 6; ) {
            String strTemp = str.substring(i, i + 6);
            String value = strTemp.substring(2);
            int c = 0;
            for (int j = 0; j < value.length(); j++) {
                char tempChar = value.charAt(j);
                int t = 0;
                switch (tempChar) {
                    case 'a':
                        t = 10;
                        break;
                    case 'b':
                        t = 11;
                        break;
                    case 'c':
                        t = 12;
                        break;
                    case 'd':
                        t = 13;
                        break;
                    case 'e':
                        t = 14;
                        break;
                    case 'f':
                        t = 15;
                        break;
                    default:
                        t = tempChar - 48;
                        break;
                }

                c += t * ((int) Math.pow(16, (value.length() - j - 1)));
            }
            sb.append((char) c);
            i = i + 6;
        }
        return sb.toString();
    }

    /**
     * URL汉字解码
     *
     * @param src 需要进行解码的字符串
     * @return
     * @desc java实现javascript中的unescape解码函数;多用于URL编码与解码
     */
    public static String unescape(String src) {
        if (src == null || src.equals("")) return null;

        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(
                            src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(
                            src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * 将字符串转为16进制
     * @param str
     * @return
     */
    public static String stringToHexStr(String str) {
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
    public static String hexStrToString(String hexStr) {
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

    /**
     * //剔除HTML空格 和 空格
     *
     * @param s
     * @return
     */
    public static String trim(String s) {

        int l = s.length();
        int a = 0, b = 0;
        boolean isA = false;
        boolean isB = false;
        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (!isA && chars[i] != 160 && chars[i] != 32) {
                a = i;
                isA = true;
            }
            if (!isB && chars[chars.length - i - 1] != 160 && chars[chars.length - i - 1] != 32) {
                b = chars.length - i;
                isB = true;
            }
            if (isA && isB) {
                break;
            }

        }
        return s.substring(a, b);
    }

    /**
     * 标记 人民币
     * @param text
     * @param markColor Color.parseColor("#ffff5c2a")
     * @return
     */
    public static SpannableString markTextRMB(String text, int markColor, boolean markBold){
        return markText(text, "¥\\d+.?\\d*", markColor, markBold);
    }

    /**
     * 标记 被 格式化的数字
     * @see FormatUtils#formatNum(long)
     * @param text
     * @param markColor
     * @param markBold
     * @return
     */
    public static SpannableString markTextFormatNum(String text, int markColor, boolean markBold){
        return markText(text, "[\\da-zA-Z+]+", markColor, markBold);
    }

    /**
     * 标记文本
     * @param text 文本
     * @param regex 正则查找要被标记的部分
     * @param markColor  标记部分的颜色
     * @param markBold   标记部分是否加粗
     * @return
     */
    public static SpannableString markText(String text, String regex ,int markColor, boolean markBold){
        SpannableString ss = new SpannableString(text);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
//            System.out.println("group" +matcher.group() + "  start:" + start + "   end:" + end);
            ss.setSpan(new ForegroundColorSpan(markColor), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            if (markBold) {
                ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        return ss;
    }

}
