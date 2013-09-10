package com.xstd.privatephone.view;

import com.xstd.privatephone.tools.Tools;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyScrollView extends ViewGroup {

	// public MyScrollView(Context context, AttributeSet attrs) {
	// super(context, attrs);
	// // TODO Auto-generated constructor stub
	// }

	
	private Context ctx;
	
//	private MyScroller myScroller;
	
	private Scroller myScroller;
	
	private boolean isFling =false;
	
	private IMyScrollListener myScrollListener;
	
	public MyScrollView(Context context) {
		super(context);
		ctx = context;
		init();
	}

	private void init() {
		
//		myScroller = new MyScroller(ctx);
		myScroller = new Scroller(ctx);
		
		detector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener(){
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				scrollBy((int)distanceX, 0);
				return true;
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				isFling = true;
				
				if(velocityX>0 && curIndex>0){
					moveToDest(curIndex-1);
				}else if (velocityX<0 && curIndex<getChildCount()){
					moveToDest(curIndex+1);
				}else{
					moveToDest();
				}
				return true;
			}
			
		});
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		MeasureSpec.getSize(widthMeasureSpec);
		MeasureSpec.getMode(widthMeasureSpec);
		
		
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			view.measure(widthMeasureSpec, heightMeasureSpec);
			
			
//			view.getMeasuredWidth()
		}
	}
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			view.layout(l+i*getWidth(), t, r+i*getWidth(), b);
			
//			view.getWidth();
		}
	}
	
	private GestureDetector detector;

	private int curIndex;
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}
	
	private int startX;
	private int startY;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = false;
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = (int) ev.getX();
			startY = (int) ev.getY();
			
			break;
		case MotionEvent.ACTION_MOVE:
			
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			
			int distanceX = Math.abs(x -startX);
			int distanceY = Math.abs(y -startY);
			
			if(distanceX > distanceY+15){
				result = true;
			}else{
				result = false;
			}
			
			
			break;
		case MotionEvent.ACTION_UP:
			startX = 0;
			startY = 0;
			result = false;
			break;

		default:
			break;
		}
		
		return result;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if(!isFling){
				moveToDest();
			}
			isFling = false;
			break;

		default:
			break;
		}
		
		return true;
	}

	private void moveToDest() {
		
		int destId = (getScrollX()+getWidth()/2)/getWidth();
		
		moveToDest(destId);
		
	}

	public void moveToDest(int destId) {
		
		int maxId = getChildCount()-1;
		
		destId = destId>=maxId?maxId:destId;
		
		int distance = destId*getWidth()-getScrollX();
		
		curIndex = destId;
		
		if(myScrollListener!=null){
			myScrollListener.moveToDest(destId);
		}
		
//		scrollBy(distance, 0);
		myScroller.startScroll(getScrollX(), getScrollY(), distance, 0,Math.abs(distance));
		
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		if(myScroller.computeScrollOffset()){
			int curX = myScroller.getCurrX();
			Tools.logSh("curX::"+curX);
			scrollTo(curX, 0);
			invalidate();
		}
	}
	
	
	public IMyScrollListener getMyScrollListener() {
		return myScrollListener;
	}

	public void setMyScrollListener(IMyScrollListener myScrollListener) {
		this.myScrollListener = myScrollListener;
	}


	public interface IMyScrollListener{
		void moveToDest(int destId);
	}
	

}
