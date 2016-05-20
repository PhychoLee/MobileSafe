package com.llf.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Lee on 2016/5/18.
 * 短信工具
 */
public class SMSUtils {


    /**
     * 接口类，给ProgressDialog和ProgressBar提供接口方法
     */
    public interface SMSBackUpCallBack{
        void getCount(int count);

        void getProcess(int process);
    }

    /**
     * 短信备份工具
     * @param context
     * @return
     */
    public static boolean smsBackup(Context context, SMSBackUpCallBack backup) {
        //先检测Sd卡状态
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");
                FileOutputStream fos = new FileOutputStream(file);

                //获得内容提供者
                ContentResolver cr = context.getContentResolver();
                Cursor cursor = cr.query(Uri.parse("content://sms/"), new String[]{"address", "date", "body", "type"}, null, null, null);

                int count = cursor.getCount();
                int process = 0;
                backup.getCount(count);

                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(fos, "UTF-8");
                serializer.startDocument("UTF-8", true);
                serializer.startTag(null, "message");
                while (cursor.moveToNext()) {
                    serializer.startTag(null, "sms");

                    serializer.startTag(null, "address");
                    serializer.text(cursor.getString(cursor.getColumnIndex("address")));
                    serializer.endTag(null, "address");

                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(cursor.getColumnIndex("type")));
                    serializer.endTag(null, "type");

                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(cursor.getColumnIndex("date")));
                    serializer.endTag(null, "date");

                    serializer.startTag(null, "body");
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    //加密
                    String encryptBody = CryptoUtils.encrypt("messageEncrypt", body);
                    serializer.text(encryptBody);
                    serializer.endTag(null, "body");

                    serializer.endTag(null, "sms");

                    process++;
                    backup.getProcess(process);

                    SystemClock.sleep(200);
                }
                cursor.close();
                serializer.endTag(null, "message");
                serializer.endDocument();

                fos.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
