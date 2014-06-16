package com.dl2974.whatsaround;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleFragment extends Fragment {
	
	private HashMap<String,String> locationData;
	private LinearLayout dataContainer;
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
    	killOldMap();
        View singleView = inflater.inflate(R.layout.single_location_information, container, false);
        LinearLayout dataContainer = (LinearLayout) singleView.findViewById(R.id.location_information_overlay);
        dataContainer.setBackgroundColor(0xFFFFFFDB);
        
        View textPortion = getActivity().getLayoutInflater().inflate(R.layout.single_location_information_text, container, false);
        
		TextView single_name = (TextView) textPortion.findViewById(R.id.single_name);
		single_name.setText(locationData.get("name"));
		TextView single_address = (TextView) textPortion.findViewById(R.id.single_address);
		single_address.setText(locationData.get("address") + "\n" +  locationData.get("locality") + " " + locationData.get("region") + " " + locationData.get("postcode"));
		TextView single_hours = (TextView) textPortion.findViewById(R.id.single_hours);
		single_hours.setText(locationData.get("hours_display"));
		TextView single_telephone = (TextView) textPortion.findViewById(R.id.single_telephone);
		single_telephone.setText(locationData.get("tel"));
		
		TextView single_website = (TextView) textPortion.findViewById(R.id.single_website);
		single_website.setClickable(true);
		String websiteUrl = locationData.get("website");
		String link = String.format("<a href='%s'>%s</a>", websiteUrl, websiteUrl );
		single_website.setText(Html.fromHtml(link));
		
		dataContainer.addView(textPortion);
		
        /*
    	for (Map.Entry<String,String> entry : locationData.entrySet() ){
   	     TextView tv = new TextView(getActivity());
   	     tv.setText(entry.getValue());
   	     //tv.setTextSize(8.0f);
   	     dataContainer.addView(tv);
    	}
    	*/
    	GoogleMap gmap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();
    	gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    	LatLng locationLongLat = new LatLng( Double.valueOf(locationData.get("latitude")), Double.valueOf(locationData.get("longitude")) );
         Marker singleMarker = gmap.addMarker(new MarkerOptions().position(locationLongLat).title(locationData.get("name")));
         singleMarker.showInfoWindow();
         gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLongLat, 17));
         gmap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        
        return singleView;
    }
    
    public void onPause()
    {
    	super.onPause();	
    	
    }
    
    public void setSingleLocationData(HashMap<String,String> singleLocationData){
    	
    	this.locationData = singleLocationData;
    	
    }
    
    private void killOldMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.gmap);

        if(mapFragment != null) {
            FragmentManager fM = getFragmentManager();
            int commit = fM.beginTransaction().remove(mapFragment).commit();
            Log.i("SingleFragment", String.format("inside killOldMap not null %d", commit));
        }

    }


}
