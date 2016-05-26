package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.db.dao.AntivirusDao;
import com.llf.mobilesafe.utils.MD5Utils;

import java.util.List;

public class AntivirusActivity extends Activity {

    private static final int BEGIN = 1;
    private static final int SCANNING = 2;
    private static final int FINISH = 3;
    private ImageView iv_scanning;
    private TextView tv_init_virus;
    private ProgressBar pb_antivirus;
    private LinearLayout ll_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);

        initUI();

        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BEGIN:
                    tv_init_virus.setText("初始化双核引擎");
                    break;
                case SCANNING:
                    // 病毒扫描中：
                    TextView child = new TextView(AntivirusActivity.this);

                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    // 如果为true表示有病毒
                    if (scanInfo.desc) {
                        child.setTextColor(Color.RED);
                        child.setText(scanInfo.appName + "有病毒");
                    } else {
                        child.setTextColor(Color.BLACK);
					// 为false表示没有病毒
                        child.setText(scanInfo.appName + "扫描安全");
                    }

                    ll_content.addView(child,0);
                    break;
                case FINISH:
                    // 当扫描结束的时候。停止动画
                    iv_scanning.clearAnimation();
                    break;
            }
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = BEGIN;

                //获取所有安装软件
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                int size = installedPackages.size();
                pb_antivirus.setMax(size);
                int process = 0;

                for (PackageInfo packageInfo: installedPackages) {
                    ScanInfo scanInfo = new ScanInfo();
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    String packageName = packageInfo.packageName;
                    scanInfo.appName = appName;
                    scanInfo.packageName = packageName;

                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    String md5 = MD5Utils.getFileMD5(sourceDir);
                    String desc = AntivirusDao.getDesc(md5);
                    //如果desc == null则没有病毒
                    if (desc == null){
                        scanInfo.desc = false;
                    }else {
                        scanInfo.desc = true;
                    }
                    SystemClock.sleep(100);
                    process++;
                    pb_antivirus.setProgress(process);

                    message = handler.obtainMessage();
                    message.what = SCANNING;
                    message.obj = scanInfo;
                    handler.sendMessage(message);
                }
                message = handler.obtainMessage();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    static class ScanInfo{
        String appName;
        String packageName;
        boolean desc;
    }


    /**
     * 初始化UI
     */
    private void initUI() {
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_init_virus = (TextView) findViewById(R.id.tv_init_virus);
        pb_antivirus = (ProgressBar) findViewById(R.id.pb_antivirus);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);

        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1000);
        ra.setRepeatMode(Animation.RESTART);
        ra.setRepeatCount(Animation.INFINITE);

        iv_scanning.startAnimation(ra);
    }
}
