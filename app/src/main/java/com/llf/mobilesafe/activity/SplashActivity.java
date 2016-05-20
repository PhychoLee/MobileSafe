package com.llf.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.llf.mobilesafe.R;
import com.llf.mobilesafe.utils.StreamUtils;


public class SplashActivity extends Activity {
	
	protected static final int CODE_SHOW_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROR = 3;
	protected static final int CODE_ENTERT_HOME = 4;
	//解析Json的属性
	private String mVersionName;
	private int mVersionCode;
	private String mDescription;
	private String mDownloadUrl;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CODE_SHOW_DIALOG:
				showUpdateDialog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_ENTERT_HOME:
				enterHome();
				break;
			}
		}
	};
	private TextView tv_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本名:"+getVersionName());
        
        tv_download = (TextView) findViewById(R.id.tv_download);

//		//生成快捷图标
//		Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//		//不重复生成快捷图标
//		shortcutIntent.putExtra("duplicate", false);
//		//名字
//		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机卫士");
//		//图标
//		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
//		//意图
//		//注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
//		ComponentName comp = new ComponentName(this.getPackageName(), this.getPackageName() + "." +this.getLocalClassName());
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.setComponent(comp);
//		intent.addCategory(Intent.CATEGORY_LAUNCHER);
////		intent.addCategory("android.intent.category.LAUNCHER");
//		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
//		sendBroadcast(shortcutIntent);

        
        //复制数据库到本地文件夹
        copyDB("address.db");
        
        SharedPreferences mpref = getSharedPreferences("config", MODE_PRIVATE);
        boolean auto_update = mpref.getBoolean("auto_update", true);
        if (auto_update) {
        	checkVersion();
		}else {
			//发送延迟信息，进入主界面
			handler.sendEmptyMessageDelayed(CODE_ENTERT_HOME, 2000);
		}
        
        RelativeLayout rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1);
        animation.setDuration(2000);
        rl_splash.startAnimation(animation);
    }

    /**
     * 获取版本名
     * @return
     */
    public String getVersionName(){
    	PackageManager packageManager = getPackageManager();
    	try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
    }
    /**
     * 获取版本号
     * @return
     */
    public int getVersionCode(){
    	PackageManager packageManager = getPackageManager();
    	try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
    }
    
    /**
     * 获取更新
     */
    public void checkVersion(){
    	//获取当前时间
    	final long startTime = System.currentTimeMillis();
    	new Thread(){
			public void run() {
				//获得message对象
				Message msg = handler.obtainMessage();
				HttpURLConnection conn = null;
    			try {
    				//用模拟器访问本机地址，可用10.0.2.2
					URL url = new URL("http://192.168.14.185:8080/update.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(5000);
					conn.setConnectTimeout(5000);
					int responseCode = conn.getResponseCode();
					
					if (responseCode == 200) {
						
						//将Stream转换为String
						String result = StreamUtils.readStream(conn.getInputStream());
//						System.out.println(result);
						
						//解析Json
						JSONObject jo = new JSONObject(result);
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDescription = jo.getString("description");
						mDownloadUrl = jo.getString("downloadUrl");
						
//						System.out.println("版本名"+mVersionName);
//						System.out.println("版本号"+mVersionCode);
//						System.out.println("详情"+mDescription);
//						System.out.println("下载地址"+mDownloadUrl);
						
						if (mVersionCode > getVersionCode()) {
							//服务器获取版本号与当前版本号进行对比，如果大于，则发送信息给handler
							msg.what = CODE_SHOW_DIALOG;
						}else {
							//如没有新版本，则进入主界面
							msg.what = CODE_ENTERT_HOME;
						}
					}
					
				} catch (MalformedURLException e) {
					//url错误
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					//网络错误异常
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// Json解析错误\
					msg.what = CODE_JSON_ERROR;
					e.printStackTrace();
				}finally{
					long endTime = System.currentTimeMillis();
					long useTime = endTime - startTime;
					if (useTime < 2000) {
						try {
							Thread.sleep(2000 - useTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					handler.sendMessage(msg);
					if (conn!=null) {
						conn.disconnect();
					}
				}
    		}
    	}.start();
    }
    
    /**
     * 弹出更新对话框
     */
    private void showUpdateDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("更新版本号:"+mVersionName);
    	builder.setMessage("更新详情:"+mDescription);
    	builder.setPositiveButton("立即更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				System.out.println("立即更新");
				downloadAPK();
			}
		});
    	builder.setNegativeButton("以后再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//取消更新，进入主界面
				enterHome();
			}
		});
    	
    	/**
    	 * 按返回键后，进入主界面
    	 */
    	builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
    	builder.show();
	}
    
    /**
     * 下载新版本的APK
     */
    private void downloadAPK() {
    	//文件存放路径
    	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		String target = Environment.getExternalStorageDirectory() + "/MobileSafe_2.0.apk";
        	HttpUtils utils = new HttpUtils();
        	utils.download(mDownloadUrl, target, new RequestCallBack<File>(){

        		@Override
        		public void onLoading(long total, long current, boolean isUploading) {
        			super.onLoading(total, current, isUploading);
        			tv_download.setVisibility(TextView.VISIBLE);
        			System.out.println("下载进度:"+ current + "/" + total);
        			tv_download.setText("下载进度："+ current*100/total + "%");
        		}
        		
    			@Override
    			public void onSuccess(ResponseInfo<File> arg0) {
    				//下载成功后跳转到安装界面
    				Intent intent = new Intent(Intent.ACTION_VIEW);
    				intent.addCategory("android.intent.category.DEFAULT");
    				intent.setDataAndType(Uri.fromFile(arg0.result), "application/vnd.android.package-archive");
//    				startActivity(intent);
    				//返回数据，用户选择取消安装后进入主界面
    				startActivityForResult(intent, 0);
    			}
    			
    			@Override
    			public void onFailure(HttpException arg0, String arg1) {
    				Toast.makeText(SplashActivity.this, "下载失败！", Toast.LENGTH_SHORT).show();
    			}

        	});
		}else{
			Toast.makeText(this, "未找到sdcard", Toast.LENGTH_SHORT).show();
		}
	}
    
    //用户选择取消安装后,调用此方法进入主界面,
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	enterHome();
    }
    
    /**
     * 进入主界面
     */
    private void enterHome() {
    	Intent intent = new Intent(this, HomeActivity.class);
    	startActivity(intent);
    	finish();
	}
    
    private void copyDB(String dbName){
    	File file = new File(getFilesDir(), dbName);
    	if (file.exists()) {
			System.out.println("数据库"+dbName+"已存在！");
			return;
		}
    	FileOutputStream fos =null;
    	InputStream open = null;
    	try {
    		fos = new FileOutputStream(file);
			open = getAssets().open(dbName);
			
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = open.read(buffer))!=-1) {
				fos.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
