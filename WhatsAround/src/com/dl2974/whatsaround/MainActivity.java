package com.dl2974.whatsaround;

import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements 
LocationsListFragment.OnLocationTypeSelectedListener, 
FactualFragment.OnLocationSelectedListener,
FactualFragment.OnUserLocationChange,
LocationFragment.MapListener,
CustomMapFragment.MapListener,
GoogleMap.InfoWindowAdapter,
SingleFragment.SingleLocationMapListener,
CustomStreetViewFragment.StreetMapListener,
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	
    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
	Location userLocation;
	HashMap<String,String> activityLocationData;
	ArrayList<HashMap<String,String>> localLocations;
	ArrayList<HashMap<String,Object>> yelpLocations;
	GoogleMap map;
	Projection projection;
	int factualCategoryId;
	boolean googlePlayServicesConnected;
	final static String MAP_FRAGMENT = "mapfragment";
	final static String SINGLE_MAP_FRAGMENT = "singlemapfragment";
	final static String STREET_MAP_FRAGMENT = "streetmapfragment";
	ArrayList<String> yelpMarkers = new ArrayList<String>();
	private String yelpFilter;
	private boolean connectionRetry = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		int availableCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (availableCode == ConnectionResult.SUCCESS)
		{
	      this.mLocationRequest = LocationRequest.create();
	      this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	      this.mLocationClient = new LocationClient(this, this, this);
	      this.mLocationClient.connect();
	      
		}
		else{
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(availableCode, this, 0);
			//do Toast here
		}
		
		if (findViewById(R.id.fragment_container) != null) {
			 if (savedInstanceState != null) {
	                return;
	            }
			 
			 LocationsListFragment llFragment = new LocationsListFragment();

			 llFragment.setArguments(getIntent().getExtras());

	         getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, llFragment).commit();
			 
		}
	}

	
	public void onLocationTypeSelected(int categoryId) {
		//RESTORE BELOW COMMENTED BLOCK WITH FRAGMENT USING FactualClient
		/*
		FactualFragment fFragment = new FactualFragment();
		fFragment.setCategoryId(categoryId);
		this.factualCategoryId = categoryId;
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fFragment);
        transaction.addToBackStack(null);

        transaction.commit();
        */
        //BYPASS FACTUAL FRAG with LIST...GO DIRECT TO MAP..
        this.factualCategoryId = categoryId;
        CustomMapFragment mapFragment = CustomMapFragment.newInstance();
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mapFragment, MAP_FRAGMENT);
        transaction.addToBackStack(null);

        transaction.commit();
		
	}
	
	
	public void onLocationTypeFilter(String filter){
		this.yelpFilter = filter;
	}
	
	
	public void onLocationSelected(HashMap<String,String> locationMap) {
		
        //GoogleMapOptions gmo = (new GoogleMapOptions()).zoomControlsEnabled(false).rotateGesturesEnabled(false);
        CustomMapFragment mapFragment = CustomMapFragment.newInstance();
        mapFragment.setLocationData(locationMap);
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mapFragment, MAP_FRAGMENT);
        transaction.addToBackStack(null);

        transaction.commit();
		
		/*
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap);
		
		if (mapFragment != null){
			onSingleLocationView(locationMap);
		}
		else{
		LocationFragment lFragment = new LocationFragment();
		lFragment.setLocationData(locationMap);
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, lFragment);
        transaction.addToBackStack(null);

        transaction.commit();
		}
		*/
		
	}
	
	
	
	@SuppressLint("NewApi")
	public void onSingleLocationView(HashMap<String,String> locationData){
		
		this.activityLocationData = locationData;
		
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentByTag(MAP_FRAGMENT);
         /*
        // We only create a fragment if it doesn't already exist.
        if (mapFragment == null) {
            // To programmatically add the map, we first create a SupportMapFragment.
            mapFragment = SupportMapFragment.newInstance();

            // Then we add it using a FragmentTransaction.
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(android.R.id.content, mapFragment, "mapfragment");
            int commit = fragmentTransaction.commit();
            Log.i("MainActivity", String.format("commit %d", commit));
        }
        */
        GoogleMap map = mapFragment.getMap();

        
        //GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap)).getMap();
       // GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();

        //SINGLE LOCATION AND USER POSITION
        LatLng locationLongLat = new LatLng( Double.valueOf(locationData.get("latitude")), Double.valueOf(locationData.get("longitude")) );
        LatLng userLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());        
        //map.setMyLocationEnabled(true);
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLongLat, 13));
         //Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).title(locationData.get("name")).snippet(markerSnippet));
         Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).title(locationData.get("name")));
         Marker userLocationMarker = map.addMarker(new MarkerOptions().position(userLocationLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action)));
         map.setInfoWindowAdapter(this);
         marker.showInfoWindow();
         map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLongLat, 12));
         map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
         
         map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
        	 
        	 @Override
             public void onInfoWindowClick(Marker marker) {
        		 
        		 String websiteUrl = MainActivity.this.activityLocationData.get("website");
        		 Uri webpage = Uri.parse(websiteUrl);
        		 Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        		 startActivity(webIntent);
        	 }
         });
         //SINGLE LOCATION AND USER POSITION
         
         
	}
	
	
	public void onUserCenteredLocationsView(){
		
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        
        if (networkInfo != null && networkInfo.isConnected() && this.userLocation != null) {
        	
		    FactualClient fc = new FactualClient(userLocation.getLatitude(), userLocation.getLongitude(), 20000);
		    this.localLocations = fc.getLocationsByCategory(this.factualCategoryId);
		  
		    YelpClient yc = new YelpClient(userLocation.getLatitude(), userLocation.getLongitude(), 20000);
		    yc.setLocationTypeFilter(this.yelpFilter);
		    this.yelpLocations = yc.formatLocations();	
		
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentByTag(MAP_FRAGMENT);
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);
		//map.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Satellite Map
        projection = map.getProjection();
        LatLng userLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        //Marker userLocationMarker = map.addMarker(new MarkerOptions().position(userLocationLatLng).title("YOU").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action)));
        //userLocationMarker.showInfoWindow();
        CircleOptions circleOptions = new CircleOptions().center(userLocationLatLng).radius(500).fillColor(0x880099ff).strokeColor(0xaa0099ff).strokeWidth(1.0f);
        Circle circle = map.addCircle(circleOptions);
        
        for (HashMap<String,String> ll : localLocations){
        	LatLng locationLongLat = new LatLng( Double.valueOf(ll.get("latitude")), Double.valueOf(ll.get("longitude")) );
        	int mapDrawable = getResources().getIdentifier(resolveCategoryName(this.factualCategoryId), "drawable", this.getPackageName());
        	Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).title(ll.get("name")).icon(BitmapDescriptorFactory.fromResource(mapDrawable)));
        	//Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).title(ll.get("name")).icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant)));
        	//Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).alpha(0.7f).title(ll.get("name")));
        	//marker.showInfoWindow();
        }
        
        //YELP markers
        for (HashMap<String,Object> yl : yelpLocations){
        	LatLng locationLongLat = new LatLng( Double.valueOf((String) yl.get("latitude")), Double.valueOf( (String) yl.get("longitude")) );
        	Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).title( (String) yl.get("name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        	yelpMarkers.add(marker.getId());
        	
        }        
        
        
        map.setInfoWindowAdapter(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationLatLng, 12));
        map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        
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
		//infoWindowView.setBackgroundResource(getResources().getIdentifier("info_window_bg", "drawable", this.getPackageName()););
		//infoWindowView.setBackgroundResource(R.drawable.custom_info_bubble);
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
		View infoWindowView = getLayoutInflater().inflate(R.layout.info_window, null);	
		int markerIndex = resolveLocationIndex(marker.getTitle());
		TextView iw_name = (TextView) infoWindowView.findViewById(R.id.iw_name);
		iw_name.setText(this.localLocations.get(markerIndex).get("name"));
		TextView iw_address = (TextView) infoWindowView.findViewById(R.id.iw_address);
		iw_address.setText(this.localLocations.get(markerIndex).get("address") + "\n" +  this.localLocations.get(markerIndex).get("locality") + " " + this.localLocations.get(markerIndex).get("region") + " " + this.localLocations.get(markerIndex).get("postcode"));
		TextView iw_hours = (TextView) infoWindowView.findViewById(R.id.iw_hours);
		iw_hours.setText(this.localLocations.get(markerIndex).get("hours_display"));
		TextView iw_telephone = (TextView) infoWindowView.findViewById(R.id.iw_telephone);
		iw_telephone.setText(this.localLocations.get(markerIndex).get("tel"));
		
		TextView iw_website = (TextView) infoWindowView.findViewById(R.id.iw_website);
		iw_website.setClickable(true);
		String websiteUrl = this.localLocations.get(markerIndex).get("website");
		String link = String.format("<a href='%s'>%s</a>", websiteUrl, websiteUrl );
		iw_website.setText(Html.fromHtml(link));
		
		//CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.valueOf(this.localLocations.get(markerIndex).get("latitude")),  Double.valueOf(this.localLocations.get(markerIndex).get("longitude")) )).zoom(15).bearing(90).tilt(65).build();
		//map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		return infoWindowView;
		}
		
		
	}	
	
	public View getInfoContents(Marker marker){

		View infoWindowView =  getLayoutInflater().inflate(R.layout.info_window, null);
		
		/*
		TextView textData = (TextView) infoWindowView.findViewById(R.id.location_info);
		textData.setText(marker.getSnippet());
		*/
			
		//SINGLE LOCATION AND USER POSITION
		TextView iw_name = (TextView) infoWindowView.findViewById(R.id.iw_name);
		iw_name.setText(this.activityLocationData.get("name"));
		TextView iw_address = (TextView) infoWindowView.findViewById(R.id.iw_address);
		iw_address.setText(this.activityLocationData.get("address") + "\n" +  this.activityLocationData.get("locality") + " " + this.activityLocationData.get("region") + " " + this.activityLocationData.get("postcode"));
		TextView iw_hours = (TextView) infoWindowView.findViewById(R.id.iw_hours);
		iw_hours.setText(this.activityLocationData.get("hours_display"));
		TextView iw_telephone = (TextView) infoWindowView.findViewById(R.id.iw_telephone);
		iw_telephone.setText(this.activityLocationData.get("tel"));
		
		TextView iw_website = (TextView) infoWindowView.findViewById(R.id.iw_website);
		iw_website.setClickable(true);
		String websiteUrl = this.activityLocationData.get("website");
		String link = String.format("<a href='%s'>%s</a>", websiteUrl, websiteUrl );
		iw_website.setText(Html.fromHtml(link));
		//SINGLE LOCATION AND USER POSITION
        
		
		return infoWindowView;
		
	}
	
	public void updateUserLocation(Location location){
		
		this.userLocation = location;
		
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	*/
	
	private String resolveCategoryName(int id){
		String name = "";
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
	
	public void startSingleFragment(Marker marker) {
		
		int markerIndex = resolveLocationIndex(marker.getTitle());
		HashMap<String,String> singleLocationData = this.localLocations.get(markerIndex);

		SingleFragment sFragment = new SingleFragment();
		sFragment.setSingleLocationData(singleLocationData);
		
		CustomMapFragment gmapFragment = CustomMapFragment.newInstance();
		gmapFragment.setSingleLocationData(singleLocationData);
		//getLayoutInflater().inflate(R.layout.single_location_information, (ViewGroup) findViewById(R.id.single_map), false);
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, sFragment);
        //transaction.replace(R.id.single_map, gmapFragment, SINGLE_MAP_FRAGMENT);
        transaction.add(R.id.single_map, gmapFragment, SINGLE_MAP_FRAGMENT);
        transaction.addToBackStack(null);

        transaction.commit();
		
	}
	
	public void onSingleMapViewCreated(HashMap<String,String> singleLocationData){
		
		//getSupportFragmentManager().dump("", null, new PrintWriter(System.out, true), null);
		try{
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag(SINGLE_MAP_FRAGMENT);
		GoogleMap gmap = mapFragment.getMap();
		//FragmentManager fragmentManager = getSupportFragmentManager();
		//GoogleMap gmap = ((SupportMapFragment) fragmentManager.findFragmentByTag(SINGLE_MAP_FRAGMENT)).getMap();
    	gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    	LatLng locationLongLat = new LatLng( Double.valueOf(singleLocationData.get("latitude")), Double.valueOf(singleLocationData.get("longitude")) );
        Marker singleMarker = gmap.addMarker(new MarkerOptions().position(locationLongLat).title(singleLocationData.get("name")));
        singleMarker.showInfoWindow();
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLongLat, 17));
        gmap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
		}catch(Exception e){}
	    
	}
	
	
	public void onSingleMapStreetViewRequest(HashMap<String,String> singleLocationData){
		
		Log.i("MainActivity onSingleMapStreetViewRequest", "inside");
		CustomStreetViewFragment streetFragment = CustomStreetViewFragment.newInstance();
		streetFragment.setSingleLocationData(singleLocationData);
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.single_map, streetFragment, STREET_MAP_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();
		
	}
	
	public void onStreetMapLocationView(HashMap<String,String> singleLocationData){
	
	 Log.i("MainActivity onStreetMapLocationView", "inside");
     StreetViewPanorama svPanorama = ((SupportStreetViewPanoramaFragment)
		        getSupportFragmentManager().findFragmentByTag(STREET_MAP_FRAGMENT)).getStreetViewPanorama();
     
     LatLng locationLongLat = new LatLng( Double.valueOf(singleLocationData.get("latitude")), Double.valueOf(singleLocationData.get("longitude")) );
     svPanorama.setPosition(locationLongLat);
		
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
			onUserCenteredLocationsView();
			this.connectionRetry = false;
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
	    }
	 
	 
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.main, menu);
	     return super.onCreateOptionsMenu(menu);
	 }
	 
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 
	     switch (item.getItemId()) {
	     
	     case android.R.id.home:
	    	 Log.i("onOptionsItemSelected", String.format("%d", item.getItemId()) );
	    	 //NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
	         //NavUtils.navigateUpFromSameTask(this);
	    	 startActivity(new Intent(this, MainActivity.class));
	         return true;
	         
	     case R.id.home_icon:
	    	 Log.i("onOptionsItemSelected home icon", String.format("%d", item.getItemId()) );
	    	 startActivity(new Intent(this, MainActivity.class));
	    	 return true;
	     }
	     return super.onOptionsItemSelected(item);
	 }
	 
	 
	
    private void killOldMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap));

        if(mapFragment != null) {
            FragmentManager fM = getSupportFragmentManager();
            int commit = fM.beginTransaction().remove(mapFragment).commit();
            Log.i("MainActivity", String.format("inside killOldMap not null %d", commit));
        }

    }
    
    
    

    
}
