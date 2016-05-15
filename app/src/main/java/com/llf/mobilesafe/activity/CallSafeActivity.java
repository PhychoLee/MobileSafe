package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.db.dao.BlackNumberDao;
import com.llf.mobilesafe.domain.BlackNumberInfo;

import java.util.List;

public class CallSafeActivity extends Activity {

    private ListView list_view;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout ll_pb;
    private BlackNumberDao dao;

    private int pageSize = 20;
    private int pageNum = 1;
    private ListViewAdapter listViewAdapter;

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
            if (listViewAdapter == null){
                listViewAdapter = new ListViewAdapter();
                list_view.setAdapter(listViewAdapter);
            }else {
                //上拉更新，只更新新加载数据
                listViewAdapter.notifyDataSetChanged();
            }
        }
    };


    private void initData() {
        dao = new BlackNumberDao(this);
        new Thread() {
            @Override
            public void run() {

                if (blackNumberInfos == null) {
                    blackNumberInfos = dao.findByPage(pageSize, pageNum);
                } else {
                    //如果blackNumberInfos已经有数据了，则将新查找的数据添加进去
                    blackNumberInfos.addAll(dao.findByPage(pageSize, pageNum));
                }

                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    class ListViewAdapter extends BaseAdapter {


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
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
                holder = new ViewHolder();
                holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String number = blackNumberInfos.get(position).getNumber();
            holder.tv_number.setText(number);
            String mode = blackNumberInfos.get(position).getMode();
            if (mode.equals("1")) {
                holder.tv_mode.setText("电话+短信拦截");
            } else if (mode.equals("2")) {
                holder.tv_mode.setText("电话拦截");
            } else if (mode.equals("3")) {
                holder.tv_mode.setText("短信拦截");
            }
            //点击删除
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean delete = dao.delete(number);
                    if (delete) {
                        //刷新
                        listViewAdapter.notifyDataSetChanged();
                        Toast.makeText(CallSafeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CallSafeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }
    }
    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        ll_pb.setVisibility(View.VISIBLE);
        list_view = (ListView) findViewById(R.id.list_view);

        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * 滑动状态改变时调用
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //上拉更新数据，不完善：1、底下没有刷新界面
                    //闲置时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        int lastVisiblePosition = list_view.getLastVisiblePosition();

                        if (lastVisiblePosition == blackNumberInfos.size() - 1) {
//                            System.out.println("到底啦" + lastVisiblePosition);
                            //最后一条到底时加载数据
                            pageNum++;
                            initData();
                        } else if (lastVisiblePosition > dao.getTotalSize()) {
                            Toast.makeText(CallSafeActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }
            }
            /**
             * 滑动时调用
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    /**
     * 添加黑名单按钮
     * @param v
     */
    public void addBlackNumber(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_add_black_number, null);
        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_phone);
        final CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cb_sms);
        Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString().trim();
                String mode = "";
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(CallSafeActivity.this, "请输入号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cb_phone.isChecked() && cb_sms.isChecked()) {
                    mode = "1";
                } else if (cb_phone.isChecked()) {
                    mode = "2";
                } else if (cb_sms.isChecked()) {
                    mode = "3";
                } else {
                    Toast.makeText(CallSafeActivity.this, "请勾选拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                BlackNumberInfo info = new BlackNumberInfo();
                info.setNumber(phone);
                info.setMode(mode);
                //加进列表头
                blackNumberInfos.add(0,info);
                dao.add(phone, mode);
                if (listViewAdapter == null) {
                    listViewAdapter = new ListViewAdapter();
                    list_view.setAdapter(listViewAdapter);
                } else {
                    listViewAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(view);

        dialog.show();
    }
}
