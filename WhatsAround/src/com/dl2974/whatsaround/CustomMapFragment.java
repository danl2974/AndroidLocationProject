package com.dl2974.whatsaround;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class CustomMapFragment extends SupportMapFragment {
	
    public interface MapListener {
        
        public void onSingleLocationView(HashMap<String,String> locationData);
    }
    
    MapListener mapListenerCallback;
	
	HashMap<String,String> locationData;
	
	public CustomMapFragment(){
		super();
	}
	
	public static CustomMapFragment newInstance(){
		return new CustomMapFragment();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View view = super.onCreateView(inflater, container, savedInstanceState);
    	 mapListenerCallback.onSingleLocationView(locationData);
    	return view;
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
    }    
    
    public void setLocationData(HashMap<String,String> locationMap){
    	
    	this.locationData = locationMap;
    	
    }

}
