package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.db.dao.AddressDao;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

public class AddressActivity extends Activity {
	private EditText etNumber;
	private TextView tvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);

		etNumber = (EditText) findViewById(R.id.et_number);
		tvResult = (TextView) findViewById(R.id.tv_result);

		etNumber.addTextChangedListener(new TextWatcher() {

			// 文字改变时调用
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String result = AddressDao.getAddress(s.toString());
				tvResult.setText(result);
			}

			// 文字改变前调用
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			// 文字改变后调用
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	public void query(View v) {
		String number = etNumber.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			String result = AddressDao.getAddress(number);
			tvResult.setText(result);
		} else {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			etNumber.startAnimation(shake);
			vibrate();
		}
	}
	
	private void vibrate(){
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{50,500,50,200}, -1);
		//long数组表示{暂停,震动,暂停,震动}，参数2表示：-1不循环，0从long数组下标0开始循环
	}
}
