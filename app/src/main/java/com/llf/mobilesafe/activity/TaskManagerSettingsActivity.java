package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.view.SettingItemView;

public class TaskManagerSettingsActivity extends Activity {

    private SettingItemView siv_sys_task;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_settings);

        mPrefs = getSharedPreferences("config", MODE_PRIVATE);

        initUI();
    }

    private void initUI() {
        siv_sys_task = (SettingItemView) findViewById(R.id.siv_sys_task);
        boolean show_sys_task = mPrefs.getBoolean("show_sys_task", false);
        if (show_sys_task){
            siv_sys_task.setChecked(true);
        }else {
            siv_sys_task.setChecked(false);
        }

        siv_sys_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show_sys_task = mPrefs.getBoolean("show_sys_task", false);
                if (show_sys_task){
                    mPrefs.edit().putBoolean("show_sys_task", false).commit();
                    siv_sys_task.setChecked(false);
                }else {
                    mPrefs.edit().putBoolean("show_sys_task", true).commit();
                    siv_sys_task.setChecked(true);
                }
            }
        });

    }
}
