package com.dl2974.whatsaround;

import java.util.HashMap;
import java.util.Map;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class LocationFragment extends Fragment {
	
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
        
        
        GoogleMap map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();
        LatLng locationLongLat = new LatLng(Double.valueOf(locationData.get("latitude")), Double.valueOf(locationData.get("longitude")));
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLongLat, 18));
        
        return locationView;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    
    public void setLocationData(HashMap<String,String> locationMap){
    	
    	this.locationData = locationMap;
    	
    }

}
