package com.llf.mobilesafe.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.llf.mobilesafe.R;
import com.llf.mobilesafe.db.dao.AppLockDao;
import com.llf.mobilesafe.domain.AppInfo;
import com.llf.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class LockFragment extends Fragment {

    private TextView tv_lock;
    private ListView list_lock;
    private List<AppInfo> lockList;
    private LockAdapter adapter;
    private AppLockDao appLockDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock, container, false);
        tv_lock = (TextView) view.findViewById(R.id.tv_lock);
        list_lock = (ListView) view.findViewById(R.id.list_lock);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<AppInfo> appInfos = AppInfos.getAppInfos(getActivity());
        lockList = new ArrayList<>();
        appLockDao = new AppLockDao(getActivity());
        for (AppInfo appInfo: appInfos) {
            if (appLockDao.query(appInfo.getAppPackageName())){
                //应用加锁列表
                lockList.add(appInfo);
            }
        }

        adapter = new LockAdapter();
        list_lock.setAdapter(adapter);
    }

    class LockAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            tv_lock.setText("已加锁应用("+lockList.size()+")");
            return lockList.size();
        }

        @Override
        public Object getItem(int position) {
            return lockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView != null){
                holder = (ViewHolder) convertView.getTag();
            }else {
                convertView = View.inflate(getActivity(), R.layout.item_lock, null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.iv_unlock = (ImageView) convertView.findViewById(R.id.iv_unlock);
                convertView.setTag(holder);
            }
            final AppInfo appInfo = lockList.get(position);
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_app_name.setText(appInfo.getAppName());
            final View finalConvertView = convertView;
            //点击给应用解锁
            holder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //加锁移动动画
                    TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0);
                    ta.setDuration(300);
                    finalConvertView.startAnimation(ta);

                    new Thread(){
                        @Override
                        public void run() {
                            //等待动画完成后再做更新操作
                            SystemClock.sleep(300);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //将包名从数据库删除
                                    appLockDao.delete(appInfo.getAppPackageName());
                                    lockList.remove(appInfo);
                                    //更新ListView
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();
                }
            });
            return convertView;
        }
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_app_name;
        ImageView iv_unlock;
    }
}
