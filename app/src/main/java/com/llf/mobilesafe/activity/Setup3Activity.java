package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 第3个页面
 * 
 * @author Lee
 * 
 */
public class Setup3Activity extends BaseSetupActivity {

	private EditText etPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avtivity_setup3);
		etPhone = (EditText) findViewById(R.id.et_phone);
		
		String phone = mpref.getString("safe_phone", "");
		etPhone.setText(phone);
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();

		// 界面切换动画
		overridePendingTransition(R.anim.trans__prev_in, R.anim.trans__prev_out);
	}

	@Override
	public void showNextPage() {
		String phone = etPhone.getText().toString().trim();
		
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "安全号码不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		mpref.edit().putString("safe_phone", phone).commit();
		
		startActivity(new Intent(this, Setup4Activity.class));
		finish();
		// 界面切换动画
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}
	
	public void selectPhone(View v) {
		startActivityForResult(new Intent(this, ContactActivity.class), 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//resultCode为RESULT_OK才会操作
		if (resultCode == Activity.RESULT_OK) {
			String phone = data.getStringExtra("phone");
			phone = phone.replaceAll("-", "").replaceAll(" ", "");//替换-和空格
			
			etPhone.setText(phone);//把电话号码设置给输入框
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
