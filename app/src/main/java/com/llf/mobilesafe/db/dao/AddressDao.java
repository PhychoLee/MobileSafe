package com.llf.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {

	private static final String PATH = "data/data/com.llf.mobilesafe/files/address.db";

	public static String getAddress(String number) {
		String address = "未知号码";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		
		if (number.matches("^1[3-8]\\d{9}$")) {// 匹配手机号码
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });

			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
		
			cursor.close();
		}else if (number.matches("^\\d+$")) {
			switch (number.length()) {
			//这里仍需要去查询数据库，但此练习版本将省略
			case 3:
				address="报警电话";
				break;
			case 4:
				address="虚拟机";
				break;
			case 5:
				address="客服电话";
				break;
			case 7:
			case 8:
				address="本地电话";
				break;

			default:
				//固话区号查询
				if (number.startsWith("0") && number.length()>10) {
					//先查询4位区号
					Cursor cursor = database
							.rawQuery(
									"select location from data2 where area = ?",
									new String[] { number.substring(1, 4) });
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}else {
						cursor.close();
						//查询3位区号
						cursor = database
								.rawQuery(
										"select location from data2 where area = ?",
										new String[] { number.substring(1, 3) });
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
					}
				}
				break;
			}
		}
		//关闭数据库
		database.close();
		return address;
	}
}
