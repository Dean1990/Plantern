package com.deanlib.plantern.utils;

import android.widget.Toast;

import com.deanlib.plantern.Plantern;


/**
 * 弹出框 dialog,toast,popwindow 等
 *
 * @author dean
 * @time 2018/6/28 下午2:57
 */
public class PopupUtils {

    public static void sendToast(int rid) {
        sendToast(Plantern.getAppContext().getString(rid));
    }

    public static void sendToast(String msg) {
        sendToast(msg, Toast.LENGTH_SHORT);
    }

    public static void sendToast(int rid, int duration) {
        sendToast(Plantern.getAppContext().getString(rid), duration);
    }

    public static void sendToast(String msg, int duration) {
        Toast.makeText(Plantern.getAppContext(), msg, duration).show();
    }
}
