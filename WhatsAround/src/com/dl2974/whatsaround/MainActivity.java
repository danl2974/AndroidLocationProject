package com.dl2974.whatsaround;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements 
LocationsListFragment.OnLocationTypeSelectedListener, 
FactualFragment.OnLocationSelectedListener,
FactualFragment.OnUserLocationChange,
LocationFragment.MapListener,
CustomMapFragment.MapListener,
GoogleMap.InfoWindowAdapter {
	
	Location userLocation;
	HashMap<String,String> activityLocationData;
	ArrayList<HashMap<String,String>> localLocations;
	Projection projection;
	int factualCategoryId;
	final static String MAP_FRAGMENT = "mapfragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container);
		
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
		
		FactualFragment fFragment = new FactualFragment();
		fFragment.setCategoryId(categoryId);
		this.factualCategoryId = categoryId;
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fFragment);
        transaction.addToBackStack(null);

        transaction.commit();
		
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
		
		FactualClient fc = new FactualClient(userLocation.getLatitude(), userLocation.getLongitude(), 20000);
		this.localLocations = fc.getLocationsByCategory(this.factualCategoryId);
		
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentByTag(MAP_FRAGMENT);
		GoogleMap map = mapFragment.getMap();
        projection = map.getProjection();
        LatLng userLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        Marker userLocationMarker = map.addMarker(new MarkerOptions().position(userLocationLatLng).title("YOU").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action)));
        userLocationMarker.showInfoWindow();
        
        for (HashMap<String,String> ll : localLocations){
        	LatLng locationLongLat = new LatLng( Double.valueOf(ll.get("latitude")), Double.valueOf(ll.get("longitude")) );
        	int mapDrawable = getResources().getIdentifier(resolveCategoryName(this.factualCategoryId), "drawable", this.getPackageName());
        	Log.i("MainActivityMapDrawable", String.format("%d", mapDrawable));
        	Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).title(ll.get("name")).icon(BitmapDescriptorFactory.fromResource(mapDrawable)));
        	//Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).title(ll.get("name")).icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant)));
        	//Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).alpha(0.7f).title(ll.get("name")));
        	marker.showInfoWindow();
        }
        
        map.setInfoWindowAdapter(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationLatLng, 12));
        map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
       	 
       	 @Override
            public void onInfoWindowClick(Marker marker) {
       		 
       		 String websiteUrl = MainActivity.this.localLocations.get(resolveLocationIndex(marker.getTitle())).get("website");
       		 Uri webpage = Uri.parse(websiteUrl);
       		 Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
       		 startActivity(webIntent);
       	 }
        });
		
		
	}
	
	
	public View getInfoWindow(Marker marker){
		
		View infoWindowView =  getLayoutInflater().inflate(R.layout.info_window, null);
		//infoWindowView.setBackgroundResource(getResources().getIdentifier("info_window_bg", "drawable", this.getPackageName()););
		//infoWindowView.setBackgroundResource(R.drawable.custom_info_bubble);
		
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
		
		return infoWindowView;
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
	
    private void killOldMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap));

        if(mapFragment != null) {
            FragmentManager fM = getSupportFragmentManager();
            int commit = fM.beginTransaction().remove(mapFragment).commit();
            Log.i("MainActivity", String.format("inside killOldMap not null %d", commit));
        }

    }

}
