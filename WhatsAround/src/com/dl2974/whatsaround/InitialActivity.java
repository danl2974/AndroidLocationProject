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
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;


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
	ProgressBar bar;
	private Handler mHandler = new Handler();
	
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        
    	setContentView(R.layout.initial_progress);
    	//bar = (ProgressBar) this.findViewById(R.id.progressBar);
    	//bar.setVisibility(View.VISIBLE);
    	
    	//Drawable logod = getResources().getDrawable(R.drawable.logo_spin);
    	//ImageView imgview = new ImageView(this);
    	//imgview.setImageDrawable(logod);
    	ImageView imgview = (ImageView) this.findViewById(R.id.spinlogo);
    	Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_logo);
        imgview.startAnimation(animation);
        
    	//getActionBar().setDisplayHomeAsUpEnabled(true);
    	
    	 ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	     NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
	     
	     if (networkInfo != null && networkInfo.isConnected()) {
	    	 
	    	 initGooglePlayClient();
	     }
	     else{
	    	 Intent intent = new Intent(this, NotConnectedActivity.class);
	         startActivity(intent);
	     }
    	
    	

    	
    }

    
    private void initGooglePlayClient(){
    	
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
		
		//googlePlayServicesConnected = true;
		//this.userLocation = mLocationClient.getLastLocation();
        //Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(LOCATION_EXTRA, this.userLocation);
        //Log.i("InitialActivity", "start main");
        //startActivity(intent);
        mHandler.postDelayed(new Runnable() {
            public void run() {
            	startMain();
            }
           }, 3000);
        
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
	
	public void startMain(){
		
		googlePlayServicesConnected = true;
		this.userLocation = mLocationClient.getLastLocation();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LOCATION_EXTRA, this.userLocation);
        startActivity(intent);
        
	}
	

	@Override
	public void onDisconnected() {
		
		googlePlayServicesConnected = false;
		
	}

	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		
	}
	

}
