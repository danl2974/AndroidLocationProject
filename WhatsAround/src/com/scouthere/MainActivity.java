package com.scouthere;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.scouthere.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scouthere.PlacesClient.PlacesCallType;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements 

HomeGridFragment.OnPlaceTypeSelectedListener,
CustomMapFragment.MapListener,
GoogleMap.InfoWindowAdapter,
SingleLocationFragment.SingleLocationMapListener,
CustomStreetViewFragment.StreetMapListener,
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	
    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
	Location userLocation;
	HashMap<String,Object> gridPhotoState = new HashMap<String,Object>();
	HashMap<String,Object> activityLocationData;
	ArrayList<HashMap<String,Object>> placesLocations;
	HashMap<String,Object> placesLocationDetailsData;
	ArrayList<HashMap<String,String>> localLocations;
	ArrayList<HashMap<String,Object>> yelpLocations;
	GoogleMap map;
	public Marker activeMarker;
	GroundOverlay closeButton;
	Projection projection;
	int factualCategoryId;
	boolean googlePlayServicesConnected;
	public static HomeGridFragment hgFragment = new HomeGridFragment();
	CustomMapFragment placeTypeMapFragment;
	SingleLocationFragment mSingleLocationFragment;
	CustomMapFragment mSingleCustomMapFragment;
	final static String MAP_FRAGMENT = "mapfragment";
	final static String SINGLE_MAP_FRAGMENT = "singlemapfragment";
	final static String STREET_MAP_FRAGMENT = "streetmapfragment";
	ArrayList<String> yelpMarkers = new ArrayList<String>();
	private String yelpFilter;
	private String placesFilter;
	private String infoWindowResourceName;
	private String placesKey = null;
	private boolean connectionRetry = false;
	long mainTimestamp = 0;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//if (findViewById(R.id.fragment_container) != null) {
	    if (savedInstanceState != null) {
				    Log.i("Main savedInstanceState", String.valueOf(savedInstanceState.size()));
	                return;
	           }
		//}

		Intent mIntent = this.getIntent();
		if (mIntent.getExtras() != null){
		    this.userLocation = mIntent.getParcelableExtra(InitialActivity.LOCATION_EXTRA);
	    }
		else{Log.i("startmain","no location in intent");}
		
		
		int availableCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (availableCode == ConnectionResult.SUCCESS)
		{
	          if(this.mLocationRequest == null){		
	             this.mLocationRequest = LocationRequest.create();
	          }
	          this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	          
	          if(this.mLocationClient == null){
	             this.mLocationClient = new LocationClient(this, this, this); 
	          }
	          if(!this.mLocationClient.isConnected()){
	            this.mLocationClient.connect();
	          }
	          
		}
		else{
			Dialog gpErrorDialog = GooglePlayServicesUtil.getErrorDialog(availableCode, this, 0);
			gpErrorDialog.show();
			
		}
		
		
	}
	
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("Main onSaveInstanceState called", "bundle size: " + String.valueOf(outState.size()));
    }	
	
	
    @Override
    protected void onStart() {
        super.onStart();
        verifyConnectivity();
        if(mainTimestamp != 0){
        	Log.i("main onStart", String.valueOf(mainTimestamp));
        	Date date = new Date();
 	        long startTime = date.getTime();
        	if ((startTime - mainTimestamp) >  60000){
        		 if(!this.mLocationClient.isConnected()){
        			Log.i("main onStart NOT connected", String.valueOf(mainTimestamp));
     	            //this.mLocationClient.connect();
        			finish();
     	            startActivity(new Intent(this, InitialActivity.class));
     	          }
        		 else{
        			Log.i("main onStart connected", String.valueOf(mainTimestamp));
        			this.userLocation = mLocationClient.getLastLocation();
        			initGridHome();
        		 }
        	}
        }
    }
	

    
    private void verifyConnectivity(){
    	
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
  	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 

  	    if (networkInfo != null && networkInfo.isConnected()) {
  	    	 
  	 	     return;
  		 
  	     }
  	     else{
  	       	  AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
  	       	  alertBuilder
  	       	  .setTitle("Where Are You?")
  	       	  .setMessage("Your current location isn't available currently from your device. Are we allowed to find you?")
  	             .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
  	           	  @Override
  	                 public void onClick(DialogInterface dialog, int id) {
  	           		  dialog.cancel();
  	           		  Toast.makeText(MainActivity.this, "Trying To Find Your Location", Toast.LENGTH_LONG).show(); 
  	           		  MainActivity.this.connectionRetry = true;
  	               	  MainActivity.this.mLocationClient.connect();
  	                 }
  	             })
  	             .setNegativeButton("No", new DialogInterface.OnClickListener() {
  	           	  @Override
  	                 public void onClick(DialogInterface dialog, int id) {
  	                      dialog.cancel();
  	                 }
  	             });
  	       	  AlertDialog alertDialog = alertBuilder.create();
  	       	  alertDialog.show();
  	       	  
  	         } //end ELSE
    	
    	
    }
	
    
	private void initGridHome(){
		
	      //HomeGridFragment hgFragment = new HomeGridFragment();
	      hgFragment = new HomeGridFragment();
		  hgFragment.setLocation(this.userLocation);
		  FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
		  mFragmentTransaction.add(R.id.fragment_container, hgFragment);
		  mFragmentTransaction.addToBackStack(null);
		  mFragmentTransaction.commit();
	 
	}
	
	public void onPlaceTypeFilter(String filter, String resourceName){
		
		this.placesFilter = filter;
		this.infoWindowResourceName = resourceName;
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		if (placeTypeMapFragment != null){
			//fragmentManager.popBackStack();
			FragmentTransaction removetransaction = fragmentManager.beginTransaction();
			removetransaction.remove(placeTypeMapFragment).commit();
		}
		
        CustomMapFragment mapFragment = CustomMapFragment.newInstance();
        placeTypeMapFragment = mapFragment;
        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, mapFragment, MAP_FRAGMENT);
        transaction.addToBackStack(null);

        transaction.commit();
	}
    
	
	public void onUserCenteredLocationsView(){
		
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null && networkInfo.isConnected() && this.userLocation != null) {
        	
        	HashMap<String,Object> searchParams = new HashMap<String,Object>();
        	searchParams.put("location", String.format("%s,%s", userLocation.getLatitude(), userLocation.getLongitude() ));
        	searchParams.put("radius", "5000");
        	searchParams.put("types", this.placesFilter);
        	
        	PlacesClient pc = new PlacesClient(searchParams, PlacesCallType.search);
        	this.placesLocations = pc.getPlacesData();
		
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentByTag(MAP_FRAGMENT);
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);
		//map.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Satellite Map
        projection = map.getProjection();
        LatLng userLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        CircleOptions circleOptions = new CircleOptions().center(userLocationLatLng).radius(500).fillColor(0x880099ff).strokeColor(0xaa0099ff).strokeWidth(1.0f);
        Circle circle = map.addCircle(circleOptions);
        
        
        for (HashMap<String,Object> pl : placesLocations){
        	
        	LatLng locationLongLat = new LatLng( Double.valueOf( (String) pl.get("latitude")), Double.valueOf( (String) pl.get("longitude")) );
        	int mapDrawable = getResources().getIdentifier(resolveCategoryName(0), "drawable", this.getPackageName());
        	Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).title((String) pl.get("name")).icon(BitmapDescriptorFactory.fromResource(mapDrawable)));
        	
        }        
        
        map.setInfoWindowAdapter(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationLatLng, 12));
        map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
        
        	@Override
            public boolean onMarkerClick(Marker marker){
        		
        		if(marker.equals(MainActivity.this.activeMarker)){ // Close marker if active one is re-clicked
        			resetMapMarker();
        			return true;
        		}
        		else{
        		  if (MainActivity.this.activeMarker != null){
        			  resetMapMarker();
        		  }	
        		  
        		  MainActivity.this.setActiveMarker(marker);
        		  marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_active));
        		  
        		}

        		return false;
        	}
        	
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng point) {
                  
            	if(MainActivity.this.activeMarker != null){
            		//Restore original marker
            		Log.i("Test MapClick", MainActivity.this.activeMarker.getTitle());
            		resetMapMarker();
	
            	}

            }

        });
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
       	 
       	 @Override
            public void onInfoWindowClick(Marker marker) {

       		if(yelpMarkers.contains(marker.getId())){
       		 
          		 String websiteUrl = (String) MainActivity.this.yelpLocations.get(resolveYelpLocationIndex(marker.getTitle())).get("link");
           		 Uri webpage = Uri.parse(websiteUrl);
           		 Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
           		 startActivity(webIntent);
       			
       		}
       		else{
       			
       		     startSingleFragment(marker);
       		     
       		}
       		
       	 }
        });
		
		
        
	   }//end IF
        
       //Present Dialog Box if Location not known
       else{
      	  AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
      	  alertBuilder
      	  .setTitle("Where Are You?")
      	  .setMessage("Your current location isn't available from your device. Are we allowed to find you?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          	  @Override
                public void onClick(DialogInterface dialog, int id) {
          		  dialog.cancel();
          		  Toast.makeText(MainActivity.this, "Trying To Find Your Location", Toast.LENGTH_LONG).show(); 
          		  MainActivity.this.connectionRetry = true;
              	  MainActivity.this.mLocationClient.connect(); 
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
          	  @Override
                public void onClick(DialogInterface dialog, int id) {
                     dialog.cancel();
                }
            });
      	  AlertDialog alertDialog = alertBuilder.create();
      	  alertDialog.show();
      	  
        } //end ELSE       
        
        
        
	}
	
	
	
	
	public View getInfoWindow(Marker marker){
		
		LinearLayout infoWindowImageView =  (LinearLayout) getLayoutInflater().inflate(R.layout.info_window_image, null);

		if(yelpMarkers.contains(marker.getId())){
			int markerIndex = resolveYelpLocationIndex(marker.getTitle());
			TextView iw_name = (TextView) infoWindowImageView.findViewById(R.id.iw_name);
			iw_name.setText((String) this.yelpLocations.get(markerIndex).get("name"));
			
			TextView iw_website = (TextView) infoWindowImageView.findViewById(R.id.iw_website);
			iw_website.setClickable(true);
			String websiteUrl = (String) this.yelpLocations.get(markerIndex).get("link");
			String link = String.format("<a href='%s'>%s</a>", websiteUrl, websiteUrl );
			iw_website.setText(Html.fromHtml(link));
			
			ImageView iv = (ImageView) infoWindowImageView.findViewById(R.id.info_window_imageview);
			iv.setImageBitmap((Bitmap) this.yelpLocations.get(markerIndex).get("image"));
			iv.setVisibility(View.VISIBLE);

			return infoWindowImageView;		
		}
		else{
			int markerIndex = resolvePlacesIndex(marker.getTitle());
			HashMap<String,Object> detailsParams = new HashMap<String,Object>();
			detailsParams.put("placeid", (String) this.placesLocations.get(markerIndex).get("place_id"));
			PlacesClient dpc = new PlacesClient(detailsParams, PlacesCallType.details);
			this.placesLocationDetailsData = dpc.getPlacesData().get(0);
			
			View infoWindowView = getLayoutInflater().inflate(R.layout.info_window, null);
			ImageView iw_type_image = (ImageView) infoWindowView.findViewById(R.id.iw_type_image);
			iw_type_image.setImageResource(getResources().getIdentifier(this.infoWindowResourceName, "drawable", this.getPackageName()));
			TextView iw_name = (TextView) infoWindowView.findViewById(R.id.iw_name);
			iw_name.setText((String) this.placesLocations.get(markerIndex).get("name"));
			TextView iw_address = (TextView) infoWindowView.findViewById(R.id.iw_address);
			iw_address.setText((String) this.placesLocationDetailsData.get("formatted_address"));
			TextView iw_hours = (TextView) infoWindowView.findViewById(R.id.iw_hours);
			iw_hours.setText((String) this.placesLocationDetailsData.get("hours"));
			
			
			TextView iw_telephone = (TextView) infoWindowView.findViewById(R.id.iw_telephone);
			iw_telephone.setClickable(true);
			iw_telephone.setText((String) this.placesLocationDetailsData.get("formatted_phone_number"));
			iw_telephone.setOnClickListener(new View.OnClickListener() {
				  @Override
				  public void onClick(View v) {
					  Intent callIntent = new Intent(Intent.ACTION_CALL);
					  callIntent.setData(Uri.parse((String) MainActivity.this.placesLocationDetailsData.get("formatted_phone_number")));
					  startActivity(callIntent);
				  }
				});
			
			if(this.placesLocationDetailsData.get("website") != null){
			  TextView iw_website = (TextView) infoWindowView.findViewById(R.id.iw_website);
			  iw_website.setClickable(true);
			  String websiteUrl = (String) this.placesLocationDetailsData.get("website");
			  String link = String.format("<a href='%s'>%s</a>", websiteUrl, websiteUrl );
			  iw_website.setText(Html.fromHtml(link));
			}
			

		return infoWindowView;
		}
		
		
	}
	
	
	
	
	public void startSingleFragment(Marker marker) {
		
		int markerIndex = resolvePlacesIndex(marker.getTitle());
		HashMap<String,Object> singleLocationData = this.placesLocations.get(markerIndex);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		//fragmentManager.popBackStack();
		
		if(mSingleLocationFragment != null){
			FragmentTransaction rsingletransaction = fragmentManager.beginTransaction();
			rsingletransaction.remove(mSingleLocationFragment).commit();
		}
		
		SingleLocationFragment sFragment = new SingleLocationFragment();
		mSingleLocationFragment = sFragment;
		sFragment.setSingleLocationData(singleLocationData);
		sFragment.setSingleLocationDetailsData(this.placesLocationDetailsData);
		
		if(mSingleCustomMapFragment != null){
			FragmentTransaction rsinglemaptransaction = fragmentManager.beginTransaction();
			rsinglemaptransaction.remove(mSingleCustomMapFragment).commit();			
		}
		
		CustomMapFragment gmapFragment = CustomMapFragment.newInstance();
		mSingleCustomMapFragment = gmapFragment;
		gmapFragment.setSingleLocationData(singleLocationData);
		
        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		
        transaction.replace(R.id.fragment_container, sFragment);
        transaction.add(R.id.single_map, gmapFragment, SINGLE_MAP_FRAGMENT);
        transaction.addToBackStack(null);

        transaction.commit();
		
	}
	
	
	
	public void onSingleMapViewCreated(HashMap<String,Object> singleLocationData){
		
		try{
		  SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag(SINGLE_MAP_FRAGMENT);
		  GoogleMap gmap = mapFragment.getMap();
    	  gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    	  LatLng locationLongLat = new LatLng( Double.valueOf((String) singleLocationData.get("latitude")), Double.valueOf((String) singleLocationData.get("longitude")) );
          Marker singleMarker = gmap.addMarker(new MarkerOptions().position(locationLongLat).title((String) singleLocationData.get("name")));
          singleMarker.showInfoWindow();
          gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLongLat, 17));
          gmap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
		 }
		 catch(Exception e){Log.i("SingleLocationCustomMap Main", e.getMessage() );}
	    
	}
	
	
	public void onSingleMapStreetViewRequest(HashMap<String,Object> singleLocationData){
		
		CustomStreetViewFragment streetFragment = CustomStreetViewFragment.newInstance();
		streetFragment.setSingleLocationData(singleLocationData);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.single_map, streetFragment, STREET_MAP_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
		
	}
	
	public void onStreetMapLocationView(HashMap<String,Object> singleLocationData){
	
     StreetViewPanorama svPanorama = ((SupportStreetViewPanoramaFragment)
		        getSupportFragmentManager().findFragmentByTag(STREET_MAP_FRAGMENT)).getStreetViewPanorama();
     LatLng locationLongLat = new LatLng( Double.valueOf((String) singleLocationData.get("latitude")), Double.valueOf((String) singleLocationData.get("longitude")) );
     svPanorama.setPosition(locationLongLat);
		
	}
	
	public void onSingleMapAerialViewRequest(HashMap<String,Object> singleLocationData){
		
		CustomMapFragment gmapFragment = CustomMapFragment.newInstance();
		gmapFragment.setSingleLocationData(singleLocationData);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.single_map, gmapFragment, SINGLE_MAP_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
		
	}
	
	
	
    @Override
    public void onLocationChanged(Location location) {
    	
    	this.userLocation = location;
    	Log.i("MainActivityOnLocationChanged", String.format("location changed: lat %f long %f", location.getLatitude(), location.getLongitude()) );
    	
    }
	
	@Override
	public void onConnected(Bundle bundle) {
	
		googlePlayServicesConnected = true;
		this.userLocation = mLocationClient.getLastLocation();
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		
		if(this.connectionRetry){
			initGridHome();
			this.connectionRetry = false;
		}
		else{
			initGridHome();
		}
		
	}
	
    @Override
    public void onDisconnected() {
        
    	googlePlayServicesConnected = false;
    	
    }
    
	 @Override
	 public void onConnectionFailed(ConnectionResult connectionResult) {
		  
		 //FILL
		 
	 }
	 
	 @Override
	 public void onStop() {

	        if (mLocationClient.isConnected()) {
	        	mLocationClient.removeLocationUpdates(this);
	        }
	        mLocationClient.disconnect();
	        super.onStop();
	        Date date = new Date();
	        mainTimestamp = date.getTime();
	        Log.i("main onStop", "called");
	    }
	 
	 
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.main, menu);
	     return super.onCreateOptionsMenu(menu);
	 }
	 
	 
	 @SuppressLint("NewApi")
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 
	    int itemId = item.getItemId();
		if (itemId == android.R.id.home) {

		    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
		        startActivity(new Intent(this, MainActivity.class));
		    } else {
		    	getSupportFragmentManager().popBackStack();
		    }
			return true;
		} else if (itemId == R.id.home_icon) {
			finish();
			startActivity(new Intent(this, MainActivity.class));
			return true;
		}
	     return super.onOptionsItemSelected(item);
	 }
	 
	 
	 
	 @Override
	 public void onConfigurationChanged(Configuration newConfig) {
	     super.onConfigurationChanged(newConfig);
	     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	 } 
	 
	

    private void resetMapMarker(){
    	
    	
    	if (this.activeMarker != null){
    	  int mapDrawable = getResources().getIdentifier(resolveCategoryName(0), "drawable", MainActivity.this.getPackageName());
		  this.activeMarker.setIcon(BitmapDescriptorFactory.fromResource(mapDrawable));
		  this.activeMarker.hideInfoWindow();
    	}

    	
    }
    
    public void setActiveMarker(Marker marker){
    	
    	this.activeMarker = marker;
        
    }
	
	
	private String resolveCategoryName(int id){
		String name = "place";
		int[] FACTUAL_IDS = {2, 62, 123, 149, 177, 308, 312, 372, 415, 430};
		String[] names = {"car","restaurant","mall","restaurant","departmentstore","group","bar","bar","bus","airport"};
		for(int i = 0;i<FACTUAL_IDS.length;i++){
			if(FACTUAL_IDS[i] == id){
				name = names[i];
				return name;
			}
		}
		return name;
	}
	
	private int resolveLocationIndex(String name){
		int index = -1;
		for(int i=0; i < localLocations.size(); i++){
			if( name.equals(localLocations.get(i).get("name")) ){
				index = i;
				return index;
			}
		}
		return index;
		
	}
	
	private int resolvePlacesIndex(String name){
		int index = -1;
		for(int i=0; i < placesLocations.size(); i++){
			if( name.equals(placesLocations.get(i).get("name")) ){
				index = i;
				return index;
			}
		}
		return index;
		
	}	
	
	private int resolveYelpLocationIndex(String name){
		int index = -1;
		for(int i=0; i < yelpLocations.size(); i++){
			if( name.equals(yelpLocations.get(i).get("name")) ){
				index = i;
				return index;
			}
		}
		return index;
		
	}


	@Override
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
    
}
