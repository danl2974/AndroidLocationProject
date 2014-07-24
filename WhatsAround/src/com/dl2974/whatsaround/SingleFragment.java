package com.dl2974.whatsaround;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;

import com.dl2974.whatsaround.LocationFragment.MapListener;
import com.dl2974.whatsaround.PlacesClient.PlacesCallType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleFragment extends Fragment {
	
    public interface SingleLocationMapListener {

        public void onSingleMapViewCreated(HashMap<String,Object> singleLocationData);
        public void onSingleMapStreetViewRequest(HashMap<String,Object> singleLocationData);
        public void onSingleMapAerialViewRequest(HashMap<String,Object> singleLocationData);
        
    }
	
    SingleLocationMapListener mMapListenerCallback;
	private HashMap<String,Object> locationData;
	private HashMap<String,Object> placeDetailsData;
	private LinearLayout dataContainer;
	final static String SINGLE_MAP_FRAGMENT = "singlemapfragment";
	final static String STREETVIEW_TEXT = "Street View";
	final static String AERIALVIEW_TEXT = "Aerial View";
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
        //View singleView = inflater.inflate(R.layout.single_location_information, container, false);
        View singleView = inflater.inflate(R.layout.single_location_information2, container, false);
        LinearLayout dataContainer = (LinearLayout) singleView.findViewById(R.id.location_information_overlay);
        //dataContainer.setBackgroundColor(0xFFFFFFDB);
        
        TextView steetViewOverlay = (TextView) singleView.findViewById(R.id.street_view_request);
        String currentOverlayText = (String) steetViewOverlay.getText();
        Log.i("SingleFragment currentOverlayText",currentOverlayText);
        String overlayText;
        if (currentOverlayText.equals(STREETVIEW_TEXT)){
        	overlayText = AERIALVIEW_TEXT;
        	 Log.i("SingleFragment", "inside IF");
        }
        else{
        	overlayText = STREETVIEW_TEXT;
        }
        steetViewOverlay.setText(overlayText);
        steetViewOverlay.setOnClickListener(new View.OnClickListener() {         
            @Override
            public void onClick(View v) {
            	TextView tv = (TextView) v;
            	
            	if (((String) tv.getText()).equals(STREETVIEW_TEXT)){
            		
            		mMapListenerCallback.onSingleMapStreetViewRequest(SingleFragment.this.locationData);
            		tv.setText(AERIALVIEW_TEXT);
            	}
            	else{
            		
            		mMapListenerCallback.onSingleMapAerialViewRequest(SingleFragment.this.locationData);
            		tv.setText(STREETVIEW_TEXT);
            	}
            }
        });
        
        //View textPortion = getActivity().getLayoutInflater().inflate(R.layout.single_location_information_text, container, false);
        View textPortion = getActivity().getLayoutInflater().inflate(R.layout.single_location_information_text2, container, false);
        
		TextView single_name = (TextView) textPortion.findViewById(R.id.single_name);
		single_name.setText((String) locationData.get("name"));
		TextView single_address = (TextView) textPortion.findViewById(R.id.single_address);
		//single_address.setText(locationData.get("address") + "\n" +  locationData.get("locality") + " " + locationData.get("region") + " " + locationData.get("postcode"));
		single_address.setText((String) placeDetailsData.get("formatted_address"));
		TextView single_hours = (TextView) textPortion.findViewById(R.id.single_hours);
		//single_hours.setText(locationData.get("hours_display"));
		single_hours.setText((String) placeDetailsData.get("hours"));
		TextView single_telephone = (TextView) textPortion.findViewById(R.id.single_telephone);
		//single_telephone.setText(locationData.get("tel"));
		single_telephone.setClickable(true);
		single_telephone.setText((String) placeDetailsData.get("formatted_phone_number"));
		single_telephone.setTextColor(0xFF59c2a3);
		single_telephone.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		single_telephone.setOnClickListener(new View.OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  Intent callIntent = new Intent(Intent.ACTION_CALL);
				  callIntent.setData(Uri.parse(String.format("tel:%s", ((String) SingleFragment.this.placeDetailsData.get("formatted_phone_number")).replaceAll( "[^\\d]", "" )) ));
				  startActivity(callIntent);
			  }
			});
		
		TextView single_website = (TextView) textPortion.findViewById(R.id.single_website);
		single_website.setClickable(true);
		//String websiteUrl = (String) locationData.get("website");
		String websiteUrl = (String) placeDetailsData.get("website");
		String link = String.format("<a href='%s'>%s</a>", websiteUrl, websiteUrl );
		single_website.setText(Html.fromHtml(link));
		single_website.setMovementMethod(LinkMovementMethod.getInstance());
		
		dataContainer.addView(textPortion);
		
		View photoPortion = getActivity().getLayoutInflater().inflate(R.layout.single_location_information_photos, container, false);
		ImageView imageView = (ImageView) photoPortion.findViewById(R.id.single_photo);
		if(placeDetailsData.get("photos") != null){
			ArrayList<HashMap<String,Object>> placePhotos = (ArrayList<HashMap<String, Object>>) placeDetailsData.get("photos");
			Log.i("PhotoSingle", String.valueOf(placePhotos.size()));
			HashMap<String,Object> photoParams = new HashMap<String,Object>();
			photoParams.put("photoreference", placePhotos.get(0).get("photo_reference"));
			photoParams.put("maxwidth", 1600);
			PlacesClient ppc = new PlacesClient(getActivity(), photoParams, PlacesCallType.photos);
			ppc.getSingleLocationPhoto(imageView);
		}
		
		dataContainer.addView(photoPortion);
		
        /*
    	for (Map.Entry<String,String> entry : locationData.entrySet() ){
   	     TextView tv = new TextView(getActivity());
   	     tv.setText(entry.getValue());
   	     //tv.setTextSize(8.0f);
   	     dataContainer.addView(tv);
    	}
    	*/
		
    	//GoogleMap gmap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.gmap)).getMap();
		/*
		//This was added to callback implementation in MainActivity
		try{
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.executePendingTransactions();
			
		GoogleMap gmap = ((SupportMapFragment) fragmentManager.findFragmentByTag(SINGLE_MAP_FRAGMENT)).getMap();
        
    	gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    	LatLng locationLongLat = new LatLng( Double.valueOf(locationData.get("latitude")), Double.valueOf(locationData.get("longitude")) );
         Marker singleMarker = gmap.addMarker(new MarkerOptions().position(locationLongLat).title(locationData.get("name")));
         singleMarker.showInfoWindow();
         gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLongLat, 17));
         gmap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
		}
		catch(Exception e){}
		*/
		 
        return singleView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	
    	super.onActivityCreated(savedInstanceState);
    	//mMapListenerCallback.onSingleMapViewCreated(this.locationData);
    	
    }
    
    
    @Override
    public void onPause()
    {
    	super.onPause();	
    	
    }
    
    public void setSingleLocationData(HashMap<String,Object> singleLocationData){
    	
    	this.locationData = singleLocationData;
    	
    }
    
    public void setSingleLocationDetailsData(HashMap<String,Object> singleLocationDetailsData){
    	
    	this.placeDetailsData = singleLocationDetailsData;
    	
    }
    
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
        	mMapListenerCallback = (SingleLocationMapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SingleLocationMapListener");
        }
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
