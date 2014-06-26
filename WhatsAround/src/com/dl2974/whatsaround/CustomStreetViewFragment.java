package com.dl2974.whatsaround;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;

public class CustomStreetViewFragment extends SupportStreetViewPanoramaFragment {
	
    public interface StreetMapListener {
        
        public void onStreetMapLocationView(HashMap<String,String> singleLocationData);
        
    }
    
    private StreetMapListener mStreetMapCallback;
    private HashMap<String,String> locationData;
	
	public CustomStreetViewFragment(){
		super();
	}
	
	public static CustomStreetViewFragment newInstance(){
		return new CustomStreetViewFragment();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View view = super.onCreateView(inflater, container, savedInstanceState);
    	return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	
    	super.onActivityCreated(savedInstanceState);

    	mStreetMapCallback.onStreetMapLocationView(this.locationData);
    	   
    	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
   
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
        	mStreetMapCallback = (StreetMapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement StreetMapListener");
        }
                
    }	
    
    
    public void setSingleLocationData(HashMap<String,String> singleLocationData){
    	
    	this.locationData = singleLocationData;
    	
    }

}
