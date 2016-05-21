package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.llf.mobilesafe.R;
import com.llf.mobilesafe.domain.AppInfo;
import com.llf.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener {
    //使用XUtils的注解
    @ViewInject(R.id.tv_rom)
    private TextView tv_rom;
    @ViewInject(R.id.tv_sd)
    private TextView tv_sd;
    @ViewInject(R.id.list_app)
    private ListView list_app;
    @ViewInject(R.id.tv_appbar)
    private TextView tv_appbar;

    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;

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

            //跳过两个提示TextView
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

        //设置滚动监听
        list_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopup();

                if (userAppInfos != null && systemAppInfos != null){
                    if (firstVisibleItem > userAppInfos.size()){
                        //系统应用
                        tv_appbar.setText("系统程序("+systemAppInfos.size()+")");
                    }else {
                        //用户应用
                        tv_appbar.setText("用户程序("+userAppInfos.size()+")");
                    }
                }
            }
        });

        list_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = list_app.getItemAtPosition(position);
                if (item != null && item instanceof AppInfo){
                    clickAppInfo =   (AppInfo) item;

                    View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup_app, null);

                    LinearLayout  ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                    LinearLayout  ll_run = (LinearLayout) contentView.findViewById(R.id.ll_run);
                    LinearLayout  ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
                    LinearLayout  ll_detail = (LinearLayout) contentView.findViewById(R.id.ll_detail);

                    //将此Activity实现监听方法
                    ll_uninstall.setOnClickListener(AppManagerActivity.this);
                    ll_run.setOnClickListener(AppManagerActivity.this);
                    ll_share.setOnClickListener(AppManagerActivity.this);
                    ll_detail.setOnClickListener(AppManagerActivity.this);

                    if (popupWindow != null && popupWindow.isShowing()){
                        popupWindow.dismiss();
                        popupWindow = null;
                    }else {
                        //-2表示wrap_content
                        popupWindow = new PopupWindow(contentView,-2,-2);
                        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        int[] location = new int[2];
                        //获取此view在window上的位置
                        view.getLocationInWindow(location);
                        popupWindow.showAtLocation(parent, Gravity.TOP+Gravity.LEFT,70,location[1]);

                        //设置动画
                        ScaleAnimation sa = new ScaleAnimation(0.5f, 1f, 0.5f, 1f,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        sa.setDuration(500);
                        contentView.startAnimation(sa);
                    }
                }
            }
        });
    }

    /**
     * 监听
     * @param v
     */
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ll_uninstall:
                //卸载
                Intent uninstallIntent = new Intent("android.intent.action.DELETE");
                uninstallIntent.setData(Uri.parse("package:" + clickAppInfo.getAppPackageName()));
                startActivity(uninstallIntent);
                dismissPopup();
                break;

            case R.id.ll_run:
                //运行
                Intent runIntent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getAppPackageName());
//                runIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                this.startActivity(runIntent);
                dismissPopup();
                break;
            case R.id.ll_share:
                //分享（打开短信编辑页面）
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                shareIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickAppInfo.getAppName()+"下载地址:"+
                                "https://play.google.com/store/apps/details?id="+clickAppInfo.getAppPackageName());
                this.startActivity(Intent.createChooser(shareIntent, "分享"));
                dismissPopup();
                break;
            case R.id.ll_detail:
                //应用详情
                Intent detailIntent = new Intent();
                detailIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                detailIntent.addCategory(Intent.CATEGORY_DEFAULT);
                detailIntent.setData(Uri.parse("package:" + clickAppInfo.getAppPackageName()));
                startActivity(detailIntent);
                dismissPopup();
                break;
        }
    }

    /**
     * 取消显示Popup
     */
    private void dismissPopup() {
        if (popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPopup();
    }
}
