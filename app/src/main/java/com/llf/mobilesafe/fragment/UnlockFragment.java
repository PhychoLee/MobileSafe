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
public class UnlockFragment extends Fragment {

    private ListView list_unlock;
    private List<AppInfo> appInfos;
    private List<AppInfo> unlockList;
    private TextView tv_unlock;
    private AppLockDao appLockDao;
    private UnlockAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unlock, null);
        list_unlock = (ListView) view.findViewById(R.id.list_unlock);
        tv_unlock = (TextView) view.findViewById(R.id.tv_unlock);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //初始化数据
        appLockDao = new AppLockDao(getActivity());
        appInfos = AppInfos.getAppInfos(getActivity());
        unlockList = new ArrayList<>();
        for (AppInfo appInfo: this.appInfos) {
            if (!appLockDao.query(appInfo.getAppPackageName())){
                //添加未上锁的App
                unlockList.add(appInfo);
            }
        }

        adapter = new UnlockAdapter();
        list_unlock.setAdapter(adapter);

    }

    class UnlockAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            tv_unlock.setText("未加锁应用("+unlockList.size()+")");
            return unlockList.size();
        }

        @Override
        public Object getItem(int position) {
            return unlockList.get(position);
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
                convertView = View.inflate(getActivity(), R.layout.item_unlock, null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
                convertView.setTag(holder);
            }
            final AppInfo appInfo = unlockList.get(position);
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_app_name.setText(appInfo.getAppName());
            //点击给应用加锁
            final View finalConvertView = convertView;
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //加锁移动动画
                    TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0);
                    ta.setDuration(700);
                    finalConvertView.startAnimation(ta);

                    new Thread(){
                        @Override
                        public void run() {
                            //等待动画完成后再做更新操作
                            SystemClock.sleep(700);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //将包名加入数据库保存
                                    appLockDao.add(appInfo.getAppPackageName());
                                    unlockList.remove(appInfo);
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
        ImageView iv_lock;
    }

}
