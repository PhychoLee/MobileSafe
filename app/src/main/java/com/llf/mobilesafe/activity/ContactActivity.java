package com.llf.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.llf.mobilesafe.R;

import android.app.Activity;
import android.app.Notification.Action;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContactActivity extends Activity {
	private List<Map<String, String>> readContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		
		ListView lvContact = (ListView) findViewById(R.id.lv_contact);
		
		readContact = readContact();
		
		lvContact.setAdapter(new SimpleAdapter(this, readContact,
				R.layout.contact_list_item, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));
		//点击item后，将此item的号码返回给上个界面
		lvContact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//获取点击item的号码
				String phone = readContact.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	/**
	 * 读取联系人信息
	 * 
	 * @return
	 */
	private List<Map<String, String>> readContact() {
		List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();

		Cursor rawContactCursor = getContentResolver().query(
				Uri.parse("content://com.android.contacts/raw_contacts"),
				new String[] { "contact_id" }, null, null, null);
		while (rawContactCursor.moveToNext()) {
			String contactId = rawContactCursor.getString(rawContactCursor
					.getColumnIndex("contact_id"));

			Cursor dataCursor = getContentResolver().query(
					Uri.parse("content://com.android.contacts/data"),
					new String[] { "data1", "mimetype" }, "raw_contact_id=?",
					new String[] { contactId }, null);

			Map<String, String> contactMap = new HashMap<String, String>();
			while (dataCursor.moveToNext()) {
				String data1 = dataCursor.getString(dataCursor
						.getColumnIndex("data1"));
				String mimetype = dataCursor.getString(dataCursor
						.getColumnIndex("mimetype"));

				if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
					contactMap.put("phone", data1);
				} else if (mimetype.equals("vnd.android.cursor.item/name")) {
					contactMap.put("name", data1);
				}
			}
			contactList.add(contactMap);
			System.out.println(contactId.toString());
		}

		return contactList;
	}
}
