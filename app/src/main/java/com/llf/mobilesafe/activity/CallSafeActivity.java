package com.llf.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.llf.mobilesafe.R;

public class CallSafeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initUI();
    }

    private void initUI() {
        ListView listView = (ListView) findViewById(R.id.list_view);
    }
}
