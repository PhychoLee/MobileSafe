package com.llf.mobilesafe.view;

import com.llf.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 设置中心的自定义控件
 * 
 * @author Lee
 * 
 */
public class SettingClickView extends RelativeLayout {

	private TextView tv_title;
	private TextView tv_desc;
	private String mTitle;

	public SettingClickView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView();
	}

	public SettingClickView(Context context) {
		super(context);
		initView();
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		// 将自定义的布局设置给SettingItemView
		View.inflate(getContext(), R.layout.view_setting_click, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);

		// 设置标题
		tv_title.setText(mTitle);
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}

	/**
	 * 设置详情
	 * 
	 * @param desc
	 */
	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}
}
