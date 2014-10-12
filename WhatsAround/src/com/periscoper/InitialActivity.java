package com.periscoper;

import com.periscoper.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class InitialActivity extends Activity

implements
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener
{
	
	
	private LocationClient mLocationClient;
	private Location userLocation;
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
    	ImageView imgview = (ImageView) this.findViewById(R.id.spinlogo);
    	Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_logo);
        imgview.startAnimation(animation);
        
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
	      this.mLocationClient = new LocationClient(this, this, this);
	      this.mLocationClient.connect();
	      
		}
		else{
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(availableCode, this, 0);
			dialog.show();
		}
    	
    }
    
    
    
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
        mHandler.postDelayed(new Runnable() {
            public void run() {
            	startMain();
            }
           }, 3000);
		
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
