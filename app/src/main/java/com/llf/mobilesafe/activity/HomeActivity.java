package com.llf.mobilesafe.activity;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

    private GridView gv_home;

    private String[] mItems = new String[]{"手机防盗", "通信卫士", "软件管理", "进程管理",
            "流量管理", "手机杀毒", "缓存管理", "高级工具", "设置中心"};

    private int[] mPics = {R.drawable.home_safe, R.drawable.home_callmsgsafe,
            R.drawable.home_apps, R.drawable.home_taskmanager,
            R.drawable.home_netmanager, R.drawable.home_trojan,
            R.drawable.home_sysoptimize, R.drawable.home_tools,
            R.drawable.home_settings};

    private SharedPreferences mpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mpref = getSharedPreferences("config", MODE_PRIVATE);

        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapter());
        gv_home.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        //手机防盗，首先弹出密码对话框
                        showPasswordDialog();
                        break;
                    case 1:
                        //手机黑名单
                        startActivity(new Intent(HomeActivity.this,
                               CallSafeActivity.class));
                        break;
                    case 2:
                        //软件管理
                        startActivity(new Intent(HomeActivity.this,
                               AppManagerActivity.class));
                        break;
                    case 7:
                        //高级工具
                        startActivity(new Intent(HomeActivity.this,
                                AToolsActivity.class));
                        break;
                    case 8:
                        //设置中心
                        startActivity(new Intent(HomeActivity.this,
                                SettingActivity.class));
                        break;
                }
            }
        });
    }

    /**
     * 弹出设置密码或输入密码对话框
     */
    protected void showPasswordDialog() {
        // 判断是否设置过密码
        String savedPassword = mpref.getString("password", null);
        if (!TextUtils.isEmpty(savedPassword)) {
            //输入密码
            showInputPasswordDialog();
        } else {
            // 设置密码
            showSetPasswordDialog();
        }
    }

    /**
     * 输入密码对话框
     */
    private void showInputPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_input_password, null);
        dialog.setView(view, 0, 0, 0, 0);// 将自定义布局设置给dialog

        final EditText et_password = (EditText) view.findViewById(R.id.et_password);

        Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        //确定按键监听
        bt_confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //校验两次密码
                String password = et_password.getText().toString();

                if (!TextUtils.isEmpty(password)) {
                    String savedPassword = mpref.getString("password", null);
                    if (MD5Utils.MD5Digest(password).equals(savedPassword)) {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
//						Toast.makeText(HomeActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //取消按键监听
        bt_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * 弹出设置密码对话框
     */
    private void showSetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_set_password, null);
        dialog.setView(view, 0, 0, 0, 0);// 将自定义布局设置给dialog

        final EditText et_password = (EditText) view.findViewById(R.id.et_password);
        final EditText et_password_confirm = (EditText) view.findViewById(R.id.et_password_confirm);

        Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        //确定按键监听
        bt_confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //校验两次密码
                String password = et_password.getText().toString();
                String passwordConfirm = et_password_confirm.getText().toString();

                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordConfirm)) {
                    if (password.equals(passwordConfirm)) {
                        mpref.edit().putString("password", MD5Utils.MD5Digest(password)).commit();
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
//						Toast.makeText(HomeActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HomeActivity.this, "密码不一致！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //取消按键监听
        bt_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    //主界面功能列表适配器
    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this,
                    R.layout.home_list_item, null);

            ImageView iv_home_item = (ImageView) view
                    .findViewById(R.id.iv_home_item);
            TextView tv_home_item = (TextView) view
                    .findViewById(R.id.tv_home_item);

            iv_home_item.setImageResource(mPics[position]);
            tv_home_item.setText(mItems[position]);

            return view;
        }

    }
}
