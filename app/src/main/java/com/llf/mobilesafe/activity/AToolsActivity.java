package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.utils.SMSUtils;
import com.llf.mobilesafe.utils.UIUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AToolsActivity extends Activity {

	private ProgressDialog pd;

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
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("提示");
		pd.setMessage("备份中，请稍后...");
		pd.show();
		new Thread(){
			@Override
			public void run() {
				boolean backup = SMSUtils.smsBackup(AToolsActivity.this, new SMSUtils.SMSBackUpCallBack() {
					@Override
					public void getCount(int count) {
						pd.setMax(count);
					}

					@Override
					public void getProcess(int process) {
						pd.setProgress(process);
					}
				});
				if (backup){
					UIUtils.showToast(AToolsActivity.this, "备份成功");
				}else {
					UIUtils.showToast(AToolsActivity.this, "备份失败");
				}
				pd.dismiss();
			}
		}.start();

	}

	/**
	 * 程序锁
	 * @param view
     */
	public void appLock(View view){
		Intent intent = new Intent(this, AppLockActivit.class);
		startActivity(intent);
	}
}