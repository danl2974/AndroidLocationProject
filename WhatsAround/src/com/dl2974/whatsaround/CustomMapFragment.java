package com.dl2974.whatsaround;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dl2974.whatsaround.SingleFragment.SingleLocationMapListener;
import com.google.android.gms.maps.SupportMapFragment;

public class CustomMapFragment extends SupportMapFragment {
	
    public interface MapListener {
        
        public void onSingleLocationView(HashMap<String,Object> locationData);
        public void onUserCenteredLocationsView();
    }
    
    MapListener mapListenerCallback;
    SingleFragment.SingleLocationMapListener mMapListenerCallback;
	
	HashMap<String,Object> locationData;
	
	public CustomMapFragment(){
		super();
	}
	
	public static CustomMapFragment newInstance(){
		return new CustomMapFragment();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View view = super.onCreateView(inflater, container, savedInstanceState);
    	 //mapListenerCallback.onSingleLocationView(locationData);
    	
    	if ( getTag().equals(MainActivity.MAP_FRAGMENT) ){
    		
    	    mapListenerCallback.onUserCenteredLocationsView();
    	    
    	}
    	 
    	return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	
    	super.onActivityCreated(savedInstanceState);
    	if ( getTag().equals(MainActivity.SINGLE_MAP_FRAGMENT) ){
    	   mMapListenerCallback.onSingleMapViewCreated(this.locationData);
    	   
    	}
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
        	 mapListenerCallback = (MapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MapListener");
        }
        
        try {
        	mMapListenerCallback = (SingleLocationMapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SingleLocationMapListener");
        }
        
        
    }

    public void setSingleLocationData(HashMap<String,Object> singleLocationData){
    	
    	this.locationData = singleLocationData;
    	
    }
    
    public void setLocationData(HashMap<String,Object> locationMap){
    	
    	this.locationData = locationMap;
    	
    }

}
