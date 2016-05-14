package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.db.dao.BlackNumberDao;
import com.llf.mobilesafe.domain.BlackNumberInfo;

import java.util.List;

public class CallSafeActivity extends Activity {

    private ListView list_view;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout ll_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_pb.setVisibility(View.INVISIBLE);
            ListViewAdapter listViewAdapter = new ListViewAdapter();
            list_view.setAdapter(listViewAdapter);
        }
    };


    private void initData() {
        new Thread(){
            @Override
            public void run() {
                BlackNumberDao dao = new BlackNumberDao(CallSafeActivity.this);
                blackNumberInfos = dao.findAll();
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    class ListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return blackNumberInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return blackNumberInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(CallSafeActivity.this,R.layout.item_call_safe,null);
                holder = new ViewHolder();
                holder.tv_number =  (TextView) convertView.findViewById(R.id.tv_number);
                holder.tv_mode =  (TextView) convertView.findViewById(R.id.tv_mode);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_number.setText(blackNumberInfos.get(position).getNumber());
            String mode = blackNumberInfos.get(position).getMode();
            if (mode.equals("1")){
                holder.tv_mode.setText("电话+短信拦截");
            }else if (mode.equals("2")){
                holder.tv_mode.setText("电话拦截");
            }else if (mode.equals("3")){
                holder.tv_mode.setText("短信拦截");
            }
//            holder.tv_mode.setText(blackNumberInfos.get(position).getMode());
            return convertView;
        }
    }

    static class ViewHolder{
        TextView tv_number;
        TextView tv_mode;
    }

    private void initUI() {
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        ll_pb.setVisibility(View.VISIBLE);
        list_view = (ListView) findViewById(R.id.list_view);
    }
}
