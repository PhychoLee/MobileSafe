package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	
	private SharedPreferences mpref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mpref = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = mpref.getBoolean("configed", false);
		if (configed) {
			setContentView(R.layout.activity_lost_find);
			
			String phone = mpref.getString("safe_phone", "");
			TextView tvSafePhone =(TextView) findViewById(R.id.tv_safe_phone);
			tvSafePhone.setText(phone);
			
			boolean protect = mpref.getBoolean("protect", false);
			ImageView ivLock = (ImageView) findViewById(R.id.iv_lock);
			if (protect) {
				ivLock.setImageResource(R.drawable.lock);
			}else {
				ivLock.setImageResource(R.drawable.unlock);
			}
			
			
		}else {
			startActivity(new Intent(this, Setup1Activity.class));
		}
		
	}
	
	/**
	 * 重新进入到设置向导页面
	 * @param v
	 */
	public void reEnter(View v){
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}
}
