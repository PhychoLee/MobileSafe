package com.llf.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

import com.llf.mobilesafe.db.dao.BlackNumberDao;

public class BlackNumberService extends Service {

    private BlackNumberDao dao;

    public BlackNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dao = new BlackNumberDao(this);

        //动态注册广播接受者
        SmsReceiver receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver,filter);
    }

    class SmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //拦截短信
            Object[] objects = (Object[]) intent.getExtras().get("pdus");

            for (Object object : objects) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                //获取来源号码
                String address = message.getOriginatingAddress();
                //获取短信内容
                String body = message.getMessageBody();

                String mode = dao.find(address);

                //如果找到此号码设置有短信拦截，将拦截此短信,
                // Android4.4后，要默认短信app才能够拦截，此功能在Android4.4以上版本失效
                if (mode.equals("1")){
                    System.out.println("已拦截"+body);
                    abortBroadcast();
                }else if (mode.equals("3")){
                    System.out.println("已拦截"+body);
                    abortBroadcast();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
