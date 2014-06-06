package com.dl2974.whatsaround;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements 
LocationsListFragment.OnLocationTypeSelectedListener, 
FactualFragment.OnLocationSelectedListener,
LocationFragment.MapListener,
GoogleMap.InfoWindowAdapter {

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
		
		LocationFragment lFragment = new LocationFragment();
		lFragment.setLocationData(locationMap);
		
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, lFragment);
        transaction.addToBackStack(null);

        transaction.commit();
		
	}
	
	@SuppressLint("NewApi")
	public void onSingleLocationView(HashMap<String,String> locationData){
		
        GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap)).getMap();
        //GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();
        LatLng locationLongLat = new LatLng( Double.valueOf(locationData.get("latitude")), Double.valueOf(locationData.get("longitude")) );

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,String> entry : locationData.entrySet()){
        	sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        String markerSnippet = sb.toString();
        
        //map.setMyLocationEnabled(true);
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLongLat, 13));
         Marker marker = map.addMarker(new MarkerOptions().position(locationLongLat).title(locationData.get("name")).snippet(markerSnippet));
         map.setInfoWindowAdapter(this);
         marker.showInfoWindow();
         map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLongLat, 15));
         map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
         
	}
	
	
	public View getInfoWindow(Marker marker){
		
		return null;
	}	
	
	public View getInfoContents(Marker marker){
		
		View infoWindowView =  getLayoutInflater().inflate(R.layout.info_window, null);
		TextView textData = (TextView) infoWindowView.findViewById(R.id.location_info);
		textData.setText(marker.getSnippet());
		
		return infoWindowView;
		
	}
	
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	*/

}
