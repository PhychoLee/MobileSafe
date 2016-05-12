package com.llf.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取本地存储的sim卡序列号
		SharedPreferences mpref = context.getSharedPreferences("config",
				context.MODE_PRIVATE);
		boolean protect = mpref.getBoolean("protect", false);
		if (protect) {
			String sim = mpref.getString("sim", null);
			// 获取当前sim卡序列号
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			String currentSim = tm.getSimSerialNumber();
			if (!TextUtils.isEmpty(sim)) {
				if (sim.equals(currentSim)) {
					System.out.println("手机安全");
				} else {
					System.out.println("手机sim卡已更改，发送报警短信!");
					String phone = mpref.getString("phone", "");
					// 发送sim卡改变短信
					SmsManager manager = SmsManager.getDefault();
					manager.sendTextMessage(phone, null, "sim card changed",
							null, null);
				}
			}
		}
	}
}
