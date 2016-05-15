package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.service.AddressService;
import com.llf.mobilesafe.service.BlackNumberService;
import com.llf.mobilesafe.utils.ServiceStateUtils;
import com.llf.mobilesafe.view.SettingClickView;
import com.llf.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	private SettingItemView sivUpdate;
	private SharedPreferences mpref;
	private SettingItemView sivAddress;
	private String[] items = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	private SettingClickView scvAddressStyle;
	private SettingClickView scvAddressLocation;;
	private SettingItemView siv_black_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		// 保存设置自动更新的信息
		mpref = getSharedPreferences("config", MODE_PRIVATE);

		initUpdate();
		initAddress();
		initAddressStyle();
		initAddressLocation();
		initBlackNumber();
	}

	/**
	 * 自动更新设置
	 */
	private void initUpdate() {
		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		// siv.setTitle("自动更新设置");

		// 初始化信息
		boolean auto_update = mpref.getBoolean("auto_update", true);
		if (auto_update) {
			// siv.setDesc("自动更新已经开启");
			sivUpdate.setChecked(true);
		} else {
			// siv.setDesc("自动更新已经关闭");
			sivUpdate.setChecked(false);
		}

		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean checked = sivUpdate.isChecked();
				if (checked) {
					sivUpdate.setChecked(false);
					// siv.setDesc("自动更新已经关闭");

					// 更新SharedPreferences
					mpref.edit().putBoolean("auto_update", false).commit();
				} else {
					sivUpdate.setChecked(true);
					// siv.setDesc("自动更新已经开启");

					// 更新SharedPreferences
					mpref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
	}

	/**
	 * 来电归属地设置
	 */
	private void initAddress() {
		sivAddress = (SettingItemView) findViewById(R.id.siv_address);

		boolean isServiceRunning = ServiceStateUtils.isServiceRunning(this,
				"com.llf.mobilesafe.service.AddressService");
		if (isServiceRunning) {
			sivAddress.setChecked(true);
		}else {
			sivAddress.setChecked(false);
		}

		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivAddress.isChecked()) {
					sivAddress.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							AddressService.class));
				} else {
					sivAddress.setChecked(true);
					startService(new Intent(SettingActivity.this,
							AddressService.class));
				}
			}
		});
	}
	
	/**
	 * 归属地悬浮窗风格设置
	 */
	private void initAddressStyle(){
		scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
		scvAddressStyle.setTitle("归属地悬浮窗风格");
		
		int style = mpref.getInt("address_style", 0);
		scvAddressStyle.setDesc(items[style]);
		
		scvAddressStyle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击后显示单选风格对话框
				showSecletStyleDialog();
			}
		});
	}

	/**
	 * 单选风格对话框
	 */
	private void showSecletStyleDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("归属地悬浮窗风格");
		int style = mpref.getInt("address_style", 0);
		builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//将单选的风格存入
				mpref.edit().putInt("address_style", which).commit();
				scvAddressStyle.setDesc(items[which]);
				dialog.dismiss();
			}
		});
		
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 归属地显示位置
	 */
	private void initAddressLocation(){
		scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
		scvAddressLocation.setTitle("归属地悬浮框显示位置");
		scvAddressLocation.setDesc("设置归属地悬浮框显示位置");
		
		scvAddressLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this, DragActivity.class));
			}
			
		});
	}


	/**
	 * 黑名单拦截设置
	 */
	private void initBlackNumber() {
		siv_black_number = (SettingItemView) findViewById(R.id.siv_black_number);

		boolean isServiceRunning = ServiceStateUtils.isServiceRunning(this,
				"com.llf.mobilesafe.service.BlackNumberService");
		if (isServiceRunning) {
			siv_black_number.setChecked(true);
		}else {
			siv_black_number.setChecked(false);
		}

		siv_black_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_black_number.isChecked()) {
					siv_black_number.setChecked(false);
					stopService(new Intent(SettingActivity.this,
							BlackNumberService.class));
				} else {
					siv_black_number.setChecked(true);
					startService(new Intent(SettingActivity.this,
							BlackNumberService.class));
				}
			}
		});
	}
}
