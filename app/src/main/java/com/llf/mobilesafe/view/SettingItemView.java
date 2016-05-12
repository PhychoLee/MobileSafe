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
 * @author Lee
 *
 */
public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.llf.mobilesafe";
	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_status;
	private String mTitle;
	private String mDesc_on;
	private String mDesc_off;

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//自动设置标题和状态
		mTitle = attrs.getAttributeValue(NAMESPACE, "sivtitle");
		mDesc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
		mDesc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");
		
		initView();
	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}
	
	/**
	 * 初始化View
	 */
	private void initView() {
		//将自定义的布局设置给SettingItemView
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		
		//设置标题
		tv_title.setText(mTitle);
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		tv_title.setText(title);
	}
	/**
	 * 设置详情
	 * @param desc
	 */
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}
	/**
	 * 返回CheckBox的状态
	 * @return
	 */
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	/**
	 * 设置CheckBox的状态
	 * @param checked
	 */
	public void setChecked(Boolean checked){
		cb_status.setChecked(checked);
		if (checked) {
			setDesc(mDesc_on);
		}else {
			setDesc(mDesc_off);
		}
	}
}
