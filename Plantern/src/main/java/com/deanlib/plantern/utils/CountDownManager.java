package com.deanlib.plantern.utils;

import android.os.CountDownTimer;

/**
 * 全局的倒计时
 */
public class CountDownManager {

    private static CountDownManager instance;

    private static CountDownTimer countDownTimer; //做一个全局倒计时

    private static long mMillisInFuture = 60000;
    private static long mCountDownInterval = 1000;

    CountDownTimerListener cdtListener;

    private CountDownManager(long millisInFuture, long countDownInterval){
        mMillisInFuture = millisInFuture;
        mCountDownInterval = countDownInterval;
        countDownTimer = new CountDownTimer(millisInFuture,countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (cdtListener != null)
                    cdtListener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (cdtListener != null)
                    cdtListener.onFinish();
            }
        };
    }

    public static CountDownManager getInstance(){
        return getInstance(mMillisInFuture, mCountDownInterval);
    }

    public static CountDownManager getInstance(long millisInFuture, long countDownInterval){
        if (instance == null){
            synchronized (CountDownManager.class){
                if (instance == null){
                    instance = new CountDownManager(millisInFuture, countDownInterval);
                }
            }
        }
        return instance;
    }

    public void startCountDown(){
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
    }

    public void cancelCountDown(){
        if (countDownTimer != null){
            countDownTimer.cancel();
            instance = null;
            countDownTimer = null;
            mMillisInFuture = 60000;
            mCountDownInterval = 1000;
        }
    }

    public void setCountDownTimerListener(CountDownTimerListener cdtListener){
        this.cdtListener = cdtListener;
    }

    public void removeCountDownTimerListener(){
        cdtListener = null;
    }

    public interface CountDownTimerListener{
        void onTick(long millisUntilFinished);
        void onFinish();
    }
}
