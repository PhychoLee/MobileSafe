package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;

import android.content.Intent;
import android.os.Bundle;
/**
 * 第1个页面
 * @author Lee
 *
 */
public class Setup1Activity extends BaseSetupActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.avtivity_setup1);
	}
	
	@Override
	public void showPreviousPage() {
		
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		
		//界面切换动画
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}
}
