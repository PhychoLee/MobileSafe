package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 第4个页面
 * 
 * @author Lee
 * 
 */
public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cbProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avtivity_setup4);
		
		cbProtect = (CheckBox) findViewById(R.id.cb_protect);
		boolean protect = mpref.getBoolean("protect", false);
		if (protect) {
			cbProtect.setText("防盗保护已经开启");
			cbProtect.setChecked(true);
		}else {
			cbProtect.setText("防盗保护没有开启");
			cbProtect.setChecked(false);
		}
		
		cbProtect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cbProtect.setText("防盗保护已经开启");
					mpref.edit().putBoolean("protect", true).commit();
				}else {
					cbProtect.setText("防盗保护没有开启");
					mpref.edit().putBoolean("protect", false).commit();
				}
			}
		});
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		// 界面切换动画
		overridePendingTransition(R.anim.trans__prev_in, R.anim.trans__prev_out);
	}

	@Override
	public void showNextPage() {
		// 设置页面只在第一次出现
		mpref.edit().putBoolean("configed", true).commit();
		startActivity(new Intent(this, LostFindActivity.class));
		finish();

		// 界面切换动画
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}
}
