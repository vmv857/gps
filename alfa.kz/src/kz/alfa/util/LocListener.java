package kz.alfa.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocListener implements LocationListener {
	public static final String LOG_TAG = "LocListener";
	public static Location imHere;

	@Override
	public void onLocationChanged(Location location) {
		//Log.v(LOG_TAG, "onLocationChanged " + location.toString());
		saveLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		//Log.v(LOG_TAG, "onProviderDisabled " + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		//Log.v(LOG_TAG, "onProviderEnabled " + provider);
		//saveLocation(locationManager.getLastKnownLocation(provider));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		/*
		Log.v(LOG_TAG,
				"onStatusChanged " + provider + " Status: "
						+ String.valueOf(status) + " Bundle = "
						+ extras.toString());*/
	}

	public static void saveLocation(Location location) {
		
		if (location != null){
			//Log.d(LOG_TAG, "saveLocation location = "+location.toString());
			(new LocDbHlp(Cnt.get())).ins_loc(location);
			imHere = location;
		} //else
			//Log.v(LOG_TAG, "saveLocation location = NULL !!!");
	}
	
	// это нужно запустить в самом начале работы программы
	public static void SetUpLocationListener() 
	{
		//Log.v(LOG_TAG, "SetUpLocationListener() start");
		LocationManager locationManager = (LocationManager) Cnt.get()
				.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocListener();
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 10, locationListener); 
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				10000, 100, locationListener); 
		
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
				10000, 150, locationListener); 
		saveLocation(locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER));
		saveLocation(locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		saveLocation(locationManager
				.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
		//Log.v(LOG_TAG, "SetUpLocationListener() finish");
	}
}