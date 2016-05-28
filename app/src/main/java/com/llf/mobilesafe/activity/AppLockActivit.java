package com.llf.mobilesafe.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.fragment.LockFragment;
import com.llf.mobilesafe.fragment.UnlockFragment;

public class AppLockActivit extends FragmentActivity implements View.OnClickListener{

    private TextView tv_lock;
    private TextView tv_unlock;
    private UnlockFragment unlockFragment;
    private LockFragment lockFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        
        initUI();
    }

    private void initUI() {
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        FrameLayout fl_content = (FrameLayout) findViewById(R.id.fl_content);

        tv_lock.setOnClickListener(this);
        tv_unlock.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = fragmentManager.beginTransaction();

        unlockFragment = new UnlockFragment();
        lockFragment = new LockFragment();
        /**
         * 替换界面
         * 1 需要替换的界面的id
         * 2具体指某一个fragment的对象
         */
        mTransaction.replace(R.id.fl_content, unlockFragment).commit();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction mTransaction = fragmentManager.beginTransaction();
        switch (v.getId()){
            case R.id.tv_unlock:
                tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tv_lock.setBackgroundResource(R.drawable.tab_right_default);

                mTransaction.replace(R.id.fl_content, unlockFragment);
                break;
            case R.id.tv_lock:
                tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
                tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);

                mTransaction.replace(R.id.fl_content, lockFragment);
                break;
        }
        mTransaction.commit();
    }
}
