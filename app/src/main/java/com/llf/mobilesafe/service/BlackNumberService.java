package com.llf.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.llf.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

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

        //获取系统电话服务
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        //动态注册广播接受者
        SmsReceiver receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter);
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话铃响起
                    String mode = dao.find(incomingNumber);
                    if (mode.equals("1") || mode.equals("2")) {
                        //拦截
                        Uri uri = Uri.parse("content://call_log/calls");

                        getContentResolver().registerContentObserver(uri, true, new MyContentObserver(new Handler(), incomingNumber));

                        //挂断电话
                        endCall();
                    }

                    break;
            }
        }
    }

    class MyContentObserver extends ContentObserver {
        String incomingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler        The handler to run {@link #onChange} on, or null if none.
         * @param incomingNumber
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
        }
    }

    /**
     * 删除来电记录
     *
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            //通过类加载器得到ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射拿到方法
            Method getService = clazz.getDeclaredMethod("getService", String.class);
            //通过AIDL远程访问
            IBinder binder = (IBinder) getService.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
            //挂断电话
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SmsReceiver extends BroadcastReceiver {

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
                if (mode.equals("1")) {
                    System.out.println("已拦截" + body);
                    abortBroadcast();
                } else if (mode.equals("3")) {
                    System.out.println("已拦截" + body);
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
