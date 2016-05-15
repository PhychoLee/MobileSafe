package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.llf.mobilesafe.R;

public class DragActivity extends Activity {

	private TextView tvTop;
	private TextView tvButton;

	private int startX;
	private int startY;
	private SharedPreferences mPref;
	long[] mHits = new long[2];// 表示双击事件
	private LinearLayout ivDrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		tvTop = (TextView) findViewById(R.id.tv_top);
		tvButton = (TextView) findViewById(R.id.tv_button);
		ivDrag = (LinearLayout) findViewById(R.id.ll_drag);

		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);
		
		//给ivDrag设置用户选择的颜色
		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		int style = mPref.getInt("address_style", 0);
		// 给View设置用户自定义的样式
		ivDrag.setBackgroundResource(bgs[style]);

		// 获取屏幕宽高
		final int width = getWindowManager().getDefaultDisplay().getWidth();
		final int height = getWindowManager().getDefaultDisplay().getHeight();

		// 根据ivDrag位置改变提示框
		if (lastY < height / 2) {
			tvTop.setVisibility(View.INVISIBLE);
			tvButton.setVisibility(View.VISIBLE);
		} else {
			tvTop.setVisibility(View.VISIBLE);
			tvButton.setVisibility(View.INVISIBLE);
		}

		// 给ivDrag重新设置LayoutParams，将ivDrag移动到用户上次设置的位置
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag
				.getLayoutParams();
		layoutParams.leftMargin = lastX;
		layoutParams.topMargin = lastY;
		ivDrag.setLayoutParams(layoutParams);

		ivDrag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机时间
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {// 500毫秒内完成2次点击
					// 将ivDrag居中
					ivDrag.layout(width / 2 - ivDrag.getWidth() / 2,
							ivDrag.getTop(), width / 2 + ivDrag.getWidth() / 2,
							ivDrag.getBottom());
					//将居中位置存入mPref
					Editor edit = mPref.edit();
					edit.putInt("lastX", ivDrag.getLeft()).commit();
					edit.putInt("lastY", ivDrag.getTop()).commit();
				}
			}
		});

		ivDrag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 初始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// 结束位置
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 位移距离
					int dx = endX - startX;
					int dy = endY - startY;

					// 移动后位置
					int l = ivDrag.getLeft() + dx;
					int r = ivDrag.getRight() + dx;
					int t = ivDrag.getTop() + dy;
					int b = ivDrag.getBottom() + dy;

					// 越过边界不处理
					if (l < 0 | r > width | t < 0 | b > height - 40) {
						break;
					}

					// 更新位置
					ivDrag.layout(l, t, r, b);

					// 根据ivDrag位置改变提示框
					if (t < height / 2) {
						tvTop.setVisibility(View.INVISIBLE);
						tvButton.setVisibility(View.VISIBLE);
					} else {
						tvTop.setVisibility(View.VISIBLE);
						tvButton.setVisibility(View.INVISIBLE);
					}

					// 重新初始化坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// 记录位置
					Editor edit = mPref.edit();
					edit.putInt("lastX", ivDrag.getLeft()).commit();
					edit.putInt("lastY", ivDrag.getTop()).commit();
					break;

				default:
					break;
				}
				return false;//传递事件
			}
		});

	}
}
