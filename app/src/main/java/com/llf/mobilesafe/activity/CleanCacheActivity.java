package com.llf.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.llf.mobilesafe.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CleanCacheActivity extends Activity {

    private ListView list_cache;
    private PackageManager packageManager;
    private List<CacheInfo> cacheInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);

        initUI();
    }

    private void initUI() {
        list_cache = (ListView) findViewById(R.id.list_cache);

        cacheInfos = new ArrayList<>();

        new Thread(){
            @Override
            public void run() {
                packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                for (PackageInfo packageInfo: installedPackages) {
                    getCacheSize(packageInfo);
                }
                //更新界面
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CacheAdapter adapter = new CacheAdapter();
            list_cache.setAdapter(adapter);
        }
    };

    private class CacheAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return cacheInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = View.inflate(CleanCacheActivity.this, R.layout.item_clean_cache,null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tv_cache_size = (TextView) convertView.findViewById(R.id.tv_cache_size);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.iv_icon.setImageDrawable(cacheInfos.get(position).icon);
            holder.tv_app_name.setText(cacheInfos.get(position).appName);
            holder.tv_cache_size.setText(Formatter.formatFileSize(CleanCacheActivity.this, cacheInfos.get(position).cacheSize));
            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_app_name;
        TextView tv_cache_size;
    }

    private void getCacheSize(PackageInfo packageInfo) {
        try {
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(packageManager, packageInfo.applicationInfo.packageName,new MyIPackageStatsObserver(packageInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyIPackageStatsObserver implements IPackageStatsObserver{
        private PackageInfo packageInfo;
        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize;
            if (cacheSize > 0){
                //有缓存
                CacheInfo info = new CacheInfo();
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
                info.cacheSize = cacheSize;
                info.appName = appName;
                info.icon = drawable;
                cacheInfos.add(info);
            }
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    static class CacheInfo{
        long cacheSize;
        String appName;
        Drawable icon;
    }
}
