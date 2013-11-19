package com.xstd.privatephone.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by chrain on 13-11-6.
 */
public class JumpSoftwareWindowView extends LinearLayout {

	private static final int BACK_GROUND_STATE_NONE = 0;
	private static final int BACK_GROUND_STATE_LEFT = 1;
	private static final int BACK_GROUND_STATE_RIGHT = 2;

	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private float mPreDownX;
	private float mPreDownY;
	private float mDensity;
	private int screenWidth;

	SharedPreferences sp;

	private WindowManager mWM = (WindowManager) getContext().getSystemService(
			Context.WINDOW_SERVICE);
	android.view.WindowManager.LayoutParams mWMParams;
	private static JumpSoftwareWindowView instance;
	private static Object mObject = new Object();

	private JumpSoftwareWindowView(Context context) {
		super(context);
		init();
	}

	public static JumpSoftwareWindowView getInstance(Context context) {
		if (instance == null) {
			synchronized (mObject) {
				if (instance == null) {
					instance = new JumpSoftwareWindowView(context);
				}
			}
		}
		return instance;
	}

	public void dismiss() {
		mWM.removeView(instance);
	}

	public void show() {
		mWM.addView(this, mWMParams);
	}

	private void setBackGround(int mode) {

		int density20 = (int) (20 * mDensity);
		int density10 = (int) (10 * mDensity);
		float[] outerRadii = new float[] { 0, 0, density20, density20,
				density20, density20, 0, 0 };
		ShapeDrawable leftBg = new ShapeDrawable(new RoundRectShape(outerRadii,
				null, null));
		leftBg.setBounds(0, 0, (int) (70 * mDensity), (int) (35 * mDensity));
		leftBg.getPaint().setColor(0x55000000);

		float[] outerRadiiNone = new float[] { density10, density10, density10,
				density10, density10, density10, density10, density10 };
		ShapeDrawable noneBg = new ShapeDrawable(new RoundRectShape(
				outerRadiiNone, null, null));
		noneBg.setBounds(0, 0, (int) (70 * mDensity), (int) (35 * mDensity));
		noneBg.getPaint().setColor(0x55000000);

		float[] outerRadiiRight = new float[] { density20, density20, 0, 0, 0,
				0, density20, density20 };
		ShapeDrawable rightBg = new ShapeDrawable(new RoundRectShape(
				outerRadiiRight, null, null));
		rightBg.setBounds(0, 0, (int) (70 * mDensity), (int) (35 * mDensity));
		rightBg.getPaint().setColor(0x55000000);

		switch (mode) {
		case BACK_GROUND_STATE_LEFT:
			this.setBackgroundDrawable(leftBg);
			break;
		case BACK_GROUND_STATE_RIGHT:
			this.setBackgroundDrawable(rightBg);
			break;
		case BACK_GROUND_STATE_NONE:
			this.setBackgroundDrawable(noneBg);
			break;

		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	private void init() {

		sp = getContext().getSharedPreferences("setting", Context.MODE_PRIVATE);

		DisplayMetrics dm = new DisplayMetrics();
		mWM.getDefaultDisplay().getMetrics(dm);
		mDensity = dm.density;
		screenWidth = dm.widthPixels;
		mWMParams = new WindowManager.LayoutParams();

		mWMParams.type = android.view.WindowManager.LayoutParams.TYPE_PHONE;
		mWMParams.format = PixelFormat.RGBA_8888;
		mWMParams.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		mWMParams.gravity = Gravity.LEFT | Gravity.TOP;

		mWMParams.x = 0;
		mWMParams.y = (int) (150 * mDensity);

		setBackGround(BACK_GROUND_STATE_LEFT);
		if (Build.VERSION.SDK_INT >= 11)
			setAlpha(0.7f);
		setOrientation(HORIZONTAL);

		TextView tv = new TextView(getContext());
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		tv.setLayoutParams(params);
		tv.setTextColor(Color.DKGRAY);
		tv.setTextSize(8 * mDensity);
		tv.setGravity(Gravity.CENTER);
		tv.setText("一键切出");
		addView(tv);

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String packageName = sp.getString("jump_software_package_name",
						getContext().getPackageName());
				Intent intent = getContext().getPackageManager()
						.getLaunchIntentForPackage(packageName);
				getContext().startActivity(intent);
			}
		});

		mWMParams.width = (int) (70 * mDensity);
		mWMParams.height = (int) (35 * mDensity);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		x = event.getRawX();
		y = event.getRawY() - (25 * mDensity);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			mPreDownX = event.getRawX();
			mPreDownY = event.getRawY();
			return true;
		case MotionEvent.ACTION_MOVE:
			updateViewPosition();
			return true;
		case MotionEvent.ACTION_UP:

			int distance = (int) (5 * mDensity);
			if (Math.abs(mPreDownX - event.getRawX()) < distance
					&& Math.abs(mPreDownY - event.getRawY()) < distance) {
				this.performClick();
			}

			updateViewPosition();
			mTouchStartX = mTouchStartY = 0;
			snapToBorder();
			return true;

		default:
			break;
		}
		return false;
	}

	private void updateViewPosition() {

		mWMParams.x = (int) (x - mTouchStartX);
		mWMParams.y = (int) (y - mTouchStartY);

		if (mWMParams.x <= 5 * mDensity) {
			setBackGround(BACK_GROUND_STATE_LEFT);
		} else if (mWMParams.x >= screenWidth - getWidth() - 5 * mDensity) {
			setBackGround(BACK_GROUND_STATE_RIGHT);
		} else {
			setBackGround(BACK_GROUND_STATE_NONE);
		}

		mWM.updateViewLayout(this, mWMParams);

	}

	private void snapToBorder() {

		if (mWMParams.x <= 5 * mDensity) {
			mWMParams.x = 0;
			setBackGround(BACK_GROUND_STATE_LEFT);
		} else if (mWMParams.x >= screenWidth - getWidth() - 5 * mDensity) {
			mWMParams.x = screenWidth - getWidth();
			setBackGround(BACK_GROUND_STATE_RIGHT);
		} else {
			setBackGround(BACK_GROUND_STATE_NONE);
		}

		// mWMParams.x = (mWMParams.x >= (screenWidth >> 1) ? (screenWidth -
		// getWidth()): 0);
		mWM.updateViewLayout(this, mWMParams);
		// setBackGround(mWMParams.x == 0 ? BACK_GROUND_STATE_LEFT
		// : BACK_GROUND_STATE_RIGHT);
	}

}
