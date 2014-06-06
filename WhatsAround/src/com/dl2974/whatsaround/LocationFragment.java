package com.dl2974.whatsaround;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dl2974.whatsaround.FactualFragment.OnLocationSelectedListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class LocationFragment extends Fragment {
	
    public interface MapListener {
        
        public void onSingleLocationView(HashMap<String,String> locationData);
    }

    MapListener mapListenerCallback;
	
	HashMap<String,String> locationData;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        
        View locationView = inflater.inflate(R.layout.location_view, container, false);
        
        TextView textData = (TextView) locationView.findViewById(R.id.location_data);
        StringBuilder sb = new StringBuilder();
        
        for (Map.Entry<String,String> entry : locationData.entrySet()){
        	sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        
        textData.setText(sb.toString());
        
        //mapListenerCallback.onSingleLocationView(Double.valueOf(locationData.get("latitude")), Double.valueOf(locationData.get("longitude")) );
        mapListenerCallback.onSingleLocationView(locationData);        

        return locationView;
         
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
