package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.view.SettingItemView;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * 第2个页面
 * 
 * @author Lee
 * 
 */
public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avtivity_setup2);

		// 保存sim卡序列号到本地
		siv = (SettingItemView) findViewById(R.id.siv_sim);

		String sim = mpref.getString("sim", null);
		if (!TextUtils.isEmpty(sim)) {
			siv.setChecked(true);
		} else {
			siv.setChecked(false);
		}

		siv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv.isChecked()) {
					siv.setChecked(false);
					// 删除本地的sim卡序列号
					mpref.edit().remove("sim").commit();
				} else {
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tm.getSimSerialNumber();
					// System.out.println(simSerialNumber);
					if (TextUtils.isEmpty(simSerialNumber)) {
						Toast.makeText(Setup2Activity.this, "本机没有SIM卡，此功能无法使用，请退出此界面！", Toast.LENGTH_SHORT)
						.show();
						return;
					}
					siv.setChecked(true);
					// 获取sim卡序列号，保存到本地
					mpref.edit().putString("sim", simSerialNumber).commit();
				}
			}
		});

	}

	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();

		// 界面切换动画
		overridePendingTransition(R.anim.trans__prev_in, R.anim.trans__prev_out);
	}

	public void showNextPage() {
		String sim = mpref.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(this, "SIM卡必须要绑定", Toast.LENGTH_SHORT).show();
			return;
		}

		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		// 界面切换动画
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}

}
