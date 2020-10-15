package com.deanlib.plantern;

import android.content.Context;

/**
 *
 */
public class Plantern {

    private static Context mContext;
    private static boolean isDebug;

    public static void init(Context context){
        init(context, false);
    }
    public static void init(Context context, boolean debug){
        mContext = context;
        isDebug = debug;
    }

    public static Context getAppContext(){
        if (mContext == null){
            throw new NullPointerException("Plantern not init");
        }
        return mContext;
    }

    public static boolean isDebug(){
        return isDebug;
    }
}
