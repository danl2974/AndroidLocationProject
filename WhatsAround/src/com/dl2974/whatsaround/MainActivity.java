package com.dl2974.whatsaround;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
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
GoogleMap.InfoWindowAdapter {
	
	Location userLocation;
	HashMap<String,String> activityLocationData;

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
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fFragment);
        transaction.addToBackStack(null);

        transaction.commit();
		
	}
	
	public void onLocationSelected(HashMap<String,String> locationMap) {
		
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
		
	}
	
	@SuppressLint("NewApi")
	public void onSingleLocationView(HashMap<String,String> locationData){
		
		this.activityLocationData = locationData;
		/*
		Log.i("MainActivity", "inside onSingleLocationView");
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentByTag("mapfragment");

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
        GoogleMap map = mapFragment.getMap();
        */
        GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap)).getMap();
       // GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();

        
        LatLng locationLongLat = new LatLng( Double.valueOf(locationData.get("latitude")), Double.valueOf(locationData.get("longitude")) );
        LatLng userLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        /*
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,String> entry : locationData.entrySet()){
        	sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        String markerSnippet = sb.toString();
        */
        /*
        String markerSnippet = String.format("%s\n%s\n%s\n%s\n%s", 
        		locationData.get("name"),
        		locationData.get("address") + "\n" +  locationData.get("locality") + " " + locationData.get("region") + " " + locationData.get("postcode"),
        		locationData.get("hours_display"),
        		locationData.get("tel"),
        		locationData.get("website")
        		); 
        */
        
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
         
  
         
	}
	
	
	public View getInfoWindow(Marker marker){
		
		return null;
	}	
	
	public View getInfoContents(Marker marker){
		
		View infoWindowView =  getLayoutInflater().inflate(R.layout.info_window, null);
		/*
		TextView textData = (TextView) infoWindowView.findViewById(R.id.location_info);
		textData.setText(marker.getSnippet());
		*/
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
	
    private void killOldMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap));

        if(mapFragment != null) {
            FragmentManager fM = getSupportFragmentManager();
            int commit = fM.beginTransaction().remove(mapFragment).commit();
            Log.i("MainActivity", String.format("inside killOldMap not null %d", commit));
        }

    }

}
