package com.scouthere;


import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class CustomMapFragment extends SupportMapFragment {
	
    public interface MapListener {
        public void onUserCenteredLocationsView();
    }
    
    MapListener mapListenerCallback;
    SingleLocationFragment.SingleLocationMapListener mapSingleLocationCallback;
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
    	
    	if (getTag() != null){
    	  if ( getTag().equals(MainActivity.MAP_FRAGMENT) ){
    		
    	      mapListenerCallback.onUserCenteredLocationsView();
    	    
    	  }
    	}
    	 	
    	 
    	return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	
    	super.onActivityCreated(savedInstanceState);
    	
    	if ( getTag().equals(MainActivity.SINGLE_MAP_FRAGMENT) ){
    		
    	   mapSingleLocationCallback.onSingleMapViewCreated(this.locationData);
    	   
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
        	mapSingleLocationCallback = (SingleLocationFragment.SingleLocationMapListener) activity;
       } catch (ClassCastException e) {
           throw new ClassCastException(activity.toString()
                   + " must implement SingleLocationFragment.SingleLocationMapListener");
       }        
        
        try {
        	 mapListenerCallback = (MapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MapListener");
        }
        
        
        
    }

    public void setSingleLocationData(HashMap<String,Object> singleLocationData){
    	
    	this.locationData = singleLocationData;
    	
    }
    
    public void setLocationData(HashMap<String,Object> locationMap){
    	
    	this.locationData = locationMap;
    	
    }

}
