package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.domain.AppInfo;
import com.llf.mobilesafe.domain.TaskInfo;
import com.llf.mobilesafe.engine.TaskInfos;
import com.llf.mobilesafe.utils.ServiceStateUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends Activity {

    private TextView tv_task_count;
    private TextView tv_memory;
    private ListView list_task;
    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;
    private CheckBox cb_task_all;
    private TaskInfoAdapter taskInfoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        initUI();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                taskInfos = TaskInfos.getTaskInfos(TaskManagerActivity.this);
                userTaskInfos = new ArrayList<>();
                systemTaskInfos = new ArrayList<>();

                for (TaskInfo taskInfo : taskInfos) {
                    if (taskInfo.isUserApp()) {
                        //用户app
                        userTaskInfos.add(taskInfo);
                    } else {
                        //系统app
                        systemTaskInfos.add(taskInfo);
                    }
                }

                //在UI进程运行
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        taskInfoAdapter = new TaskInfoAdapter();
                        list_task.setAdapter(taskInfoAdapter);
                    }
                });
            }
        }.start();
    }

    private class TaskInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return taskInfos.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userTaskInfos.size() + 1) {
                return null;
            }
            TaskInfo taskInfo;
            if (position < userTaskInfos.size() + 1) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = 2 + userTaskInfos.size();
                taskInfo = systemTaskInfos.get(position - location);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                //添加用户应用条
                TextView tv_user = new TextView(TaskManagerActivity.this);
                tv_user.setText("用户程序(" + userTaskInfos.size() + ")");
                tv_user.setTextColor(Color.WHITE);
                tv_user.setBackgroundColor(Color.GRAY);

                return tv_user;
            } else if (position == userTaskInfos.size() + 1) {
                //添加系统应用条
                TextView tv_sys = new TextView(TaskManagerActivity.this);
                tv_sys.setText("系统程序(" + systemTaskInfos.size() + ")");
                tv_sys.setTextColor(Color.WHITE);
                tv_sys.setBackgroundColor(Color.GRAY);

                return tv_sys;
            }

            //跳过两个提示TextView
            TaskInfo taskInfo;
            if (position < userTaskInfos.size() + 1) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = 2 + userTaskInfos.size();
                taskInfo = systemTaskInfos.get(position - location);
            }


            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                holder = new ViewHolder();
                holder.iv_task_icon = (ImageView) convertView.findViewById(R.id.iv_task_icon);
                holder.tv_task_name = (TextView) convertView.findViewById(R.id.tv_task_name);
                holder.tv_task_mem = (TextView) convertView.findViewById(R.id.tv_task_mem);
                holder.cb_task = (CheckBox) convertView.findViewById(R.id.cb_task);

                convertView.setTag(holder);
            }


            holder.iv_task_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_task_name.setText(taskInfo.getAppName());
            holder.tv_task_mem.setText(Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemory()));
            if (taskInfo.isChecked()) {
                holder.cb_task.setChecked(true);
            } else {
                holder.cb_task.setChecked(false);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_task_icon;
        TextView tv_task_name;
        TextView tv_task_mem;
        CheckBox cb_task;
    }

    private void initUI() {
        tv_task_count = (TextView) findViewById(R.id.tv_task_count);
        tv_memory = (TextView) findViewById(R.id.tv_memory);
        list_task = (ListView) findViewById(R.id.list_task);
        cb_task_all = (CheckBox) findViewById(R.id.cb_task_all);

        //进程数
        int taskCount = ServiceStateUtils.getTaskCount(this);
        tv_task_count.setText("运行中进程" + taskCount + "个");

        //剩余内存
        long availMem = ServiceStateUtils.getAvailMen(this);
        //总内存
        long totalMem = ServiceStateUtils.getTotalMen(this);
        tv_memory.setText("剩余/总内存：" + Formatter.formatFileSize(this, availMem) + "/" + Formatter.formatFileSize(this, totalMem));

        //给ListView设置Item监听
        list_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = list_task.getItemAtPosition(position);

                if (item != null && item instanceof TaskInfo) {

                    ViewHolder holder = (ViewHolder) view.getTag();
                    TaskInfo taskInfo = (TaskInfo) item;

                    if (holder.cb_task.isChecked()) {
                        taskInfo.setChecked(false);
                        holder.cb_task.setChecked(false);
                    }else {
                        taskInfo.setChecked(true);
                        holder.cb_task.setChecked(true);
                    }
                }
            }
        });
    }

    /**
     * 选择所有
     *
     * @param view
     */
    public void checkAll(View view) {
        System.out.println("CheckAll==============");
        if (cb_task_all.isChecked()) {
            for (TaskInfo taskInfo : userTaskInfos) {
                taskInfo.setChecked(true);
            }
            for (TaskInfo taskInfo : systemTaskInfos) {
                taskInfo.setChecked(true);
            }
        }else {
            for (TaskInfo taskInfo : userTaskInfos) {
                taskInfo.setChecked(false);
            }
            for (TaskInfo taskInfo : systemTaskInfos) {
                taskInfo.setChecked(false);
            }
        }

        //刷新
        taskInfoAdapter.notifyDataSetChanged();
    }
}
