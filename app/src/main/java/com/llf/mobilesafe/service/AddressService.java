package com.llf.mobilesafe.service;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.db.dao.AddressDao;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressService extends Service {

	private OutCallReceiver receiver;
	private View view;
	private WindowManager mWM;
	private SharedPreferences mpref;

	private int startX;
	private int startY;
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mpref = getSharedPreferences("config", MODE_PRIVATE);
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		MyListener listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 动态注册去电广播
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String address = AddressDao.getAddress(incomingNumber);
				showToast(address);
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				if (mWM != null && view != null) {
					mWM.removeView(view);
					view = null;
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 监听去电广播
	 * 
	 * @author Lee
	 * 
	 */
	public class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获得去电号码
			String number = getResultData();
			String address = AddressDao.getAddress(number);
			showToast(address);
		}

	}

	/**
	 * 电话归属地悬浮窗
	 * 
	 * @param address
	 */
	private void showToast(String address) {
		mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

		// 获得屏幕宽高
		final int winWidth = mWM.getDefaultDisplay().getWidth();
		final int winHeight = mWM.getDefaultDisplay().getHeight();

		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;// 要对悬浮窗进行触摸操作要改成TYPE_PHONE
		// 设置重心为左上角
		params.gravity = Gravity.LEFT + Gravity.TOP;
		params.setTitle("Toast");

		int lastX = mpref.getInt("lastX", 0);
		int lastY = mpref.getInt("lastY", 0);

		// 根据用户设置，显示悬浮窗位置
		params.x = lastX;
		params.y = lastY;

		// view = new TextView(this);
		view = View.inflate(this, R.layout.toast_address, null);
		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		int style = mpref.getInt("address_style", 0);
		// 给View设置用户自定义的样式
		view.setBackgroundResource(bgs[style]);

		TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);
		tvNumber.setText(address);

		//移动悬浮窗
		view.setOnTouchListener(new OnTouchListener() {
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
					params.x += dx;
					params.y += dy;

					// 越过边界不处理
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > winWidth - view.getWidth()) {
						params.x = winWidth - view.getWidth();
					}
					if (params.y > winWidth - view.getHeight()) {
						params.y = winWidth - view.getHeight();
					}
					

					// 更新位置
					mWM.updateViewLayout(view, params);

					// 根据ivDrag位置改变提示框

					// 重新初始化坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// 记录位置
					Editor edit = mpref.edit();
					edit.putInt("lastX", params.x).commit();
					edit.putInt("lastY", params.y).commit();
					break;

				default:
					break;
				}
				return false;
			}
		});

		mWM.addView(view, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 解绑注册
		unregisterReceiver(receiver);
	}
}
