package com.llf.mobilesafe.receiver;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.service.LocationService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		
		for (Object object : objects) {
			SmsMessage message = SmsMessage.createFromPdu((byte[])object);
			//获取来源号码
			String address = message.getOriginatingAddress();
			//获取短信内容
			String body = message.getMessageBody();
			
			SharedPreferences mpref = context.getSharedPreferences("config", context.MODE_PRIVATE);
			String phone = mpref.getString("sefe_phone", "");
			
			if (address.equals(phone) && body.equals("#*alarm*#")) {
				//播放报警音乐
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1f, 1f);
				player.setLooping(true);
				player.start();
				
				//中断短信传递
				abortBroadcast();
			}else if (address.equals(phone) && body.equals("#*location*#")) {
				//调用位置service
				context.startService(new Intent(context, LocationService.class));
				
				//获取位置信息
				String location = mpref.getString("location", "");
				System.out.println(location);
				
				//中断短信传递
				abortBroadcast();
			}else if (address.equals(phone) && body.equals("#*lockscreen*#")) {
				//获得设备管理服务
				DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				//设备管理组件
				ComponentName mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
				
				
				//首先判断是否激活设备管理器
				if (mDPM.isAdminActive(mDeviceAdminSample)) {
					//直接锁屏
					mDPM.lockNow();
					//
					//设置pin
					mDPM.resetPassword("123", 0);
				}else {
					//激活设备管理器
					Intent intent1 = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                    intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            "开启设备管理器");
                    context.startActivity(intent1);
				}
				
				//中断短信传递
				abortBroadcast();
			}else if (address.equals(phone) && body.equals("#*wipedata*#")) {
				//获得设备管理服务
				DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				//设备管理组件
				ComponentName mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
				
				
				//首先判断是否激活设备管理器
				if (mDPM.isAdminActive(mDeviceAdminSample)) {
					//清除数据
					mDPM.wipeData(0);
				}else {
					//激活设备管理器
					Intent intent1 = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                    intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            "开启设备管理器");
                    context.startActivity(intent1);
				}
				
				//中断短信传递
				abortBroadcast();
			}
		}
	}

}
