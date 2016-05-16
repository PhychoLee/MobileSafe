package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.llf.mobilesafe.R;
import com.llf.mobilesafe.domain.AppInfo;
import com.llf.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity {
    //使用XUtils的注解
    @ViewInject(R.id.tv_rom)
    private TextView tv_rom;
    @ViewInject(R.id.tv_sd)
    private TextView tv_sd;
    @ViewInject(R.id.list_app)
    private ListView list_app;

    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initUI();
        initData();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AppManagerAdapter appManagerAdapter = new AppManagerAdapter();
            list_app.setAdapter(appManagerAdapter);
        }
    };

    class AppManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return appInfos.size() +2 ;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0){
                return  null;
            }else if (position == userAppInfos.size() +1){
                return null;
            }
            AppInfo appInfo;
            if (position < userAppInfos.size() +1){
                appInfo = userAppInfos.get(position - 1);
            }else {
                int location = 2 + userAppInfos.size();
                appInfo = systemAppInfos.get(position - location);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            if (position < userAppInfos.size() +1){
                return position - 1;
            }else {
                int location = 2 + userAppInfos.size();
                return position - location;

            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0){
                //添加用户应用条
                TextView tv_user = new TextView(AppManagerActivity.this);
                tv_user.setText("用户程序("+userAppInfos.size()+")");
                tv_user.setTextColor(Color.WHITE);
                tv_user.setBackgroundColor(Color.GRAY);

                return tv_user;
            }else if (position == userAppInfos.size() + 1){
                //添加系统应用条
                TextView tv_sys = new TextView(AppManagerActivity.this);
                tv_sys.setText("系统程序("+systemAppInfos.size()+")");
                tv_sys.setTextColor(Color.WHITE);
                tv_sys.setBackgroundColor(Color.GRAY);

                return tv_sys;
            }

            //除去两个提示TextView
            AppInfo appInfo;
            if (position < userAppInfos.size() +1){
                appInfo = userAppInfos.get(position - 1);
            }else {
                int location = 2 + userAppInfos.size();
                appInfo = systemAppInfos.get(position - location);
            }


            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout){
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);
                holder = new ViewHolder();
                holder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tv_app_position = (TextView) convertView.findViewById(R.id.tv_app_position);
                holder.tv_app_size = (TextView) convertView.findViewById(R.id.tv_app_size);
                convertView.setTag(holder);
            }

            holder.iv_app_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_app_name.setText(appInfo.getAppName());
            if(appInfo.isRom()){
                holder.tv_app_position.setText("手机内存");
            }else {
                holder.tv_app_position.setText("SD卡");
            }

            holder.tv_app_size.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getAppSize()));

            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_app_position;
        TextView tv_app_size;
    }


    private void initData() {
        new Thread(){
            @Override
            public void run() {
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);
                userAppInfos = new ArrayList<>();
                systemAppInfos = new ArrayList<>();

                for (AppInfo appInfo: appInfos) {
                    if (appInfo.isUserApp()){
                        //用户app
                        userAppInfos.add(appInfo);
                    }else {
                        //系统app
                        systemAppInfos.add(appInfo);
                    }
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        ViewUtils.inject(this);
        long romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        long sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        tv_rom.setText("内存可用: "+Formatter.formatFileSize(this, romFreeSpace));
        tv_sd.setText("SD卡可用: "+Formatter.formatFileSize(this, sdFreeSpace));

    }
}
