package com.deanlib.plantern.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 当viewpager中每页的高度一致时，尤其是第一页高度很小时（小于屏幕高度），可能会出现后面页与第一页高度相等的问题，撑不开的问题
 * FixedHeightViewPager 用于解决以上问题
 */
public class FixedHeightViewPager extends ViewPager {

	public FixedHeightViewPager(Context context) {
		super(context);
	}

	public FixedHeightViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = 0 ;
		// 下面遍历所有child的高度
		for ( int i = 0 ; i < getChildCount ( ) ; i ++ ) {
			View child = getChildAt(i) ;
			child. measure ( widthMeasureSpec, MeasureSpec. makeMeasureSpec ( 0 , MeasureSpec. UNSPECIFIED ) ) ;
			int h = child. getMeasuredHeight ( ) ;
			if ( h > height ) // 采用最大的view的高度。
				height = h ;
		}
		heightMeasureSpec = MeasureSpec. makeMeasureSpec ( height,
				MeasureSpec. EXACTLY ) ;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev){
		try{
			return super.onTouchEvent(ev);
		} catch (IllegalArgumentException ex){
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
