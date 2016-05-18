package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.utils.SMSUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AToolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	/**
	 * 转跳到归属地查询页面
	 * @param v
	 */
	public void numberAddressQuery(View v){
		startActivity(new Intent(this, AddressActivity.class));
	}

	/**
	 * 短信备份
	 * @param v
     */
	public void smsBackup(View v){
		SMSUtils.smsBackup(this);
	}
}
