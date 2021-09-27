package com.deanlib.plantern.widget;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;


/**
 * Dialog
 * 平滑的展示和隐藏
 * 避免 闪烁问题
 * @auther Dean
 * @create 2020/8/11
 */
public abstract class AbsLoadingDialog extends AlertDialog implements ILoadingDialog {

    private static final int MIN_SHOW_TIME = 500;
    private static final int MIN_DELAY = 100;

    private long mStartTime = -1;
    private boolean mPostedHide = false;
    private boolean mPostedShow = false;
    private boolean mDismissed = false;

    private Handler mHandler = new Handler();

    private final Runnable mDelayedHide = new Runnable() {

        @Override
        public void run() {
            mPostedHide = false;
            mStartTime = -1;
            dismiss();
        }
    };

    private final Runnable mDelayedShow = new Runnable() {

        @Override
        public void run() {
            mPostedShow = false;
            if (!mDismissed) {
                mStartTime = System.currentTimeMillis();
                show();
            }
        }
    };

    public AbsLoadingDialog(@NonNull Context context) {
        super(context);
//        this(context, R.style.Theme_AppCompat_Light_Dialog);
    }

    public AbsLoadingDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void showSmoothDialog() {
        mStartTime = -1;
        mDismissed = false;
        mHandler.removeCallbacks(mDelayedHide);
        mPostedHide = false;
        if (!mPostedShow) {
            mHandler.postDelayed(mDelayedShow, MIN_DELAY);
            mPostedShow = true;
        }
    }

    @Override
    public void dismissSmoothDialog() {
        mDismissed = true;
        mHandler.removeCallbacks(mDelayedShow);
        mPostedShow = false;
        long diff = System.currentTimeMillis() - mStartTime;
        if (diff >= MIN_SHOW_TIME || mStartTime == -1) {
            dismiss();
        } else {
            if (!mPostedHide) {
                mHandler.postDelayed(mDelayedHide, MIN_SHOW_TIME - diff);
                mPostedHide = true;
            }
        }
    }

    @Override
    public boolean isDialogShowing() {
        return isShowing();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mDelayedHide);
        mHandler.removeCallbacks(mDelayedShow);
    }

}
