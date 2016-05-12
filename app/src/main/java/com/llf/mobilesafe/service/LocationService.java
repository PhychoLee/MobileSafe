package com.llf.mobilesafe.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

	private SharedPreferences mpref;
	private LocationManager lm;
	private MyLoacationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mpref = getSharedPreferences("config", MODE_PRIVATE);

		lm = (LocationManager) getSystemService(this.LOCATION_SERVICE);
		// 获取所有位置提供者
		// List<String> allProviders = lm.getAllProviders();
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = lm.getBestProvider(criteria, true);

		listener = new MyLoacationListener();

		lm.requestLocationUpdates(bestProvider, 0, 0, listener);
		
		stopSelf();//service自杀
	}

	class MyLoacationListener implements LocationListener {

		//位置改变时调用
		@Override
		public void onLocationChanged(Location location) {
			mpref.edit().putString(
					"location",
					"j:" + location.getLongitude() + 
					";w:" + location.getLatitude()).commit();
			
			System.out.println("location==j:" + location.getLongitude() + 
					";w:" + location.getLatitude());
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//关掉位置更新
		lm.removeUpdates(listener);
	}
}
