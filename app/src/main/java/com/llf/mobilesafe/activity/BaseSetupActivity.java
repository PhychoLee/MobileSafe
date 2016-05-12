package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

public abstract class BaseSetupActivity extends Activity {
	
	private GestureDetector mDetector;
	public SharedPreferences mpref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mpref = getSharedPreferences("config", MODE_PRIVATE);
		
		mDetector = new GestureDetector(this, new SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//竖直距离过大，不响应
				if (Math.abs(e1.getRawY() - e2.getRawY()) > 150) {
					return true;
				}
				
				//向右滑动，上一页
				if (e2.getRawX() - e1.getRawX() >150) {
					showPreviousPage();
				}
				//向左滑动，下一页
				if (e1.getRawX() - e2.getRawX() >150) {
					showNextPage();
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	
	public abstract void showPreviousPage();
	public abstract void showNextPage();

	/**
	 * 下一步
	 * 
	 * @param v
	 */
	public void next(View v) {
		showNextPage();
	}
	
	/**
	 * 上一步
	 * 
	 * @param v
	 */
	public void previous(View v) {
		showPreviousPage();
	}
	
	/**
	 * 将触摸事件传给GestureDetector
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
