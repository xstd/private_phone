package com.xstd.privatephone.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class MyViewPager extends ViewPager {
	private GestureDetector mGestureDetector;
	View.OnTouchListener mGestureListener;

	private boolean isSingleTap = false;// 单击事件.

	public MyViewPager(Context context) {
		super(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 捕获单击事件向下传递
		mGestureDetector = new GestureDetector(new MySimpleGesture());

		return super.onTouchEvent(event);
	}

	private class MySimpleGesture extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			
			isSingleTap = true;
			
			return true;
			
		}

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(!isSingleTap){
			isSingleTap = false;
			return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
		}
		
	}

}
