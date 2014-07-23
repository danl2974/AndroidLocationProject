package com.dl2974.whatsaround;

import java.util.HashMap;
import java.util.Set;

import com.dl2974.whatsaround.PlacesClient.PlacesCallType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;


public class InitialActivity extends Activity
implements
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
PlacesClient.IPlacesClientTaskCompleted{
	
	
	private LocationRequest mLocationRequest;
	private LocationClient mLocationClient;
	private Location userLocation;
	private HashMap<String,Object> gridPhotoState;
	boolean googlePlayServicesConnected;
	public final static String LOCATION_EXTRA = "Location";
	public final static String PHOTO_TYPE_MAP_EXTRA = "GridPhotoTypeMap";
	
	
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.initial_progress);
    	getActionBar().setDisplayHomeAsUpEnabled(true);
    	
		int availableCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (availableCode == ConnectionResult.SUCCESS)
		{
	      //this.mLocationRequest = LocationRequest.create();
	      //this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	      this.mLocationClient = new LocationClient(this, this, this);
	      this.mLocationClient.connect();
	      
		}
		else{
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(availableCode, this, 0);
			//do Toast here
		}
    	
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
		googlePlayServicesConnected = true;
		this.userLocation = mLocationClient.getLastLocation();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LOCATION_EXTRA, this.userLocation);
        startActivity(intent);
		/*
        HashMap<String,Object> searchParams = new HashMap<String,Object>();
        searchParams.put("location", String.format("%s,%s", userLocation.getLatitude(), userLocation.getLongitude() ));
        searchParams.put("radius", "5000");
        PlacesClient homeGridPC = new PlacesClient(this, searchParams, PlacesCallType.search);
        homeGridPC.getTypePhotoMap(this);
        */
		//mLocationClient.requestLocationUpdates(mLocationRequest, MainActivity);
		
	}
	
	public void startMain(Bundle locationTypePhotoMap){
		
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LOCATION_EXTRA, this.userLocation);
        startActivity(intent);
       
	}
	

	@Override
	public void onDisconnected() {
		
		googlePlayServicesConnected = false;
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
