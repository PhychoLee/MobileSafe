package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.llf.mobilesafe.R;

public class AntivirusActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);

        initUI();
    }


    /**
     * 初始化UI
     */
    private void initUI() {
        ImageView iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        ProgressBar pb_annvirus = (ProgressBar) findViewById(R.id.pb_antivirus);
        LinearLayout ll_content = (LinearLayout) findViewById(R.id.ll_content);

        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(300);
        ra.setRepeatMode(Animation.RESTART);
        ra.setRepeatCount(Animation.INFINITE);

        iv_scanning.startAnimation(ra);
    }
}
