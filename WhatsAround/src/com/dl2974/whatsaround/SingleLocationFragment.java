package com.dl2974.whatsaround;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
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

public class SingleLocationFragment extends Fragment {
	
    public interface SingleLocationMapListener {

        public void onSingleMapViewCreated(HashMap<String,Object> singleLocationData);
        public void onSingleMapStreetViewRequest(HashMap<String,Object> singleLocationData);
        public void onSingleMapAerialViewRequest(HashMap<String,Object> singleLocationData);
        
    }
	
    SingleLocationMapListener mMapListenerCallback;
	private HashMap<String,Object> locationData;
	private HashMap<String,Object> placeDetailsData;
	private LinearLayout dataContainer;
	SingleLocationPagerAdapter slPagerAdapter;
	ViewPager mViewPager;
	private FragmentActivity mContext;
	final static String SINGLE_MAP_FRAGMENT = "singlemapfragment";
	final static String STREETVIEW_TEXT = "Street View";
	final static String AERIALVIEW_TEXT = "Aerial View";
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        //View singleView = inflater.inflate(R.layout.single_location_information2, container, false);
        View singleView = inflater.inflate(R.layout.single_location_pager, container, false);
        LinearLayout dataContainer = (LinearLayout) singleView.findViewById(R.id.location_information_overlay);
        
        ArrayList<HashMap<String, Object>> placePhotosList = (ArrayList<HashMap<String, Object>>) this.placeDetailsData.get("photos");
        if(placePhotosList != null){
          SingleLocationPagerAdapter slPagerAdapter = new SingleLocationPagerAdapter(mContext.getSupportFragmentManager(), this.locationData, this.placeDetailsData);
          mViewPager = (ViewPager) singleView.findViewById(R.id.pager);
          mViewPager.setAdapter(slPagerAdapter);
        }
        else{
    	   ((LinearLayout) singleView).removeView(singleView.findViewById(R.id.pager_photo_container));
    	}	
        
       
        TextView steetViewOverlay = (TextView) singleView.findViewById(R.id.street_view_request);
        String currentOverlayText = (String) steetViewOverlay.getText();
        
        String overlayText;
        if (currentOverlayText.equals(STREETVIEW_TEXT)){
        	overlayText = AERIALVIEW_TEXT;
        	 
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
            		
            		mMapListenerCallback.onSingleMapStreetViewRequest(SingleLocationFragment.this.locationData);
            		tv.setText(AERIALVIEW_TEXT);
            	}
            	else{
            		
            		mMapListenerCallback.onSingleMapAerialViewRequest(SingleLocationFragment.this.locationData);
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
		if(((String) placeDetailsData.get("hours")) != null){
		  single_hours.setText((String) placeDetailsData.get("hours"));
		}
		else{
		  single_hours.setText(getResources().getString(R.string.hoursunavailable));
		}
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
				  callIntent.setData(Uri.parse(String.format("tel:%s", ((String) SingleLocationFragment.this.placeDetailsData.get("formatted_phone_number")).replaceAll( "[^\\d]", "" )) ));
				  startActivity(callIntent);
			  }
			});
		

		//String websiteUrl = (String) locationData.get("website");
		
		if (placeDetailsData.get("website") != null){
		   TextView single_website = (TextView) textPortion.findViewById(R.id.single_website);
		   single_website.setClickable(true);
		   String websiteUrl = (String) placeDetailsData.get("website");
		   String link = String.format("<a href='%s'>%s</a>", websiteUrl, websiteUrl );
		   single_website.setText(Html.fromHtml(link));
		   single_website.setMovementMethod(LinkMovementMethod.getInstance());
		}
		
		dataContainer.addView(textPortion);
		
		/*
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
        mContext = (FragmentActivity) activity;
        
        try {
        	mMapListenerCallback = (SingleLocationMapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SingleLocationMapListener");
        }
    }
    

    
    
    public static class SingleLocationPagerAdapter extends FragmentStatePagerAdapter {

    	private HashMap<String,Object> singleLocationData;
    	private HashMap<String,Object> placeDetailsData;
    	
        public SingleLocationPagerAdapter(FragmentManager fm, HashMap<String,Object> locationData, HashMap<String,Object> placeDetails) {
            super(fm);
            this.singleLocationData = locationData;
            this.placeDetailsData = placeDetails;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                /*
                case 0:
                	CustomMapFragment gmapFragment = CustomMapFragment.newInstance();
            		gmapFragment.setSingleLocationData(this.singleLocationData);
            		Log.i("SingleLocationPagerAdapter", String.valueOf(this.singleLocationData.size()) );
            		Log.i("SingleLocationPagerAdapter Id", String.valueOf(gmapFragment.getId()) );
            		Bundle gmargs = new Bundle();
                    gmargs.putString("origin", SINGLE_MAP_FRAGMENT);
                    gmapFragment.setArguments(gmargs);
                    
                    return gmapFragment;
                */
                default:
                	Log.i("SingleLocationPagerAdapter", String.valueOf(i) );
                    Fragment fragment = new SingleLocationPhotoFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("details", this.placeDetailsData);
                    args.putInt("index", i);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
        	if(placeDetailsData.get("photos") != null){
        		return ((ArrayList<HashMap<String, Object>>) placeDetailsData.get("photos")).size();
        	}
        	else
        	{
        		return 0;
        	}
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }    
    
    
    
    
    
    public static class SingleLocationPhotoFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";
        HashMap<String,Object> placeDetailsData;
        SingleLocationPhotoCacheSingleton pCache = SingleLocationPhotoCacheSingleton.getInstance();
 
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View photoView = inflater.inflate(R.layout.single_location_information_photos, container, false);
            Bundle args = getArguments();
            placeDetailsData = (HashMap<String, Object>) args.getSerializable("details");
            int photoindex = args.getInt("index");
    		ImageView imageView = (ImageView) photoView.findViewById(R.id.single_photo);
    		ArrayList<HashMap<String, Object>> placePhotos = (ArrayList<HashMap<String, Object>>) placeDetailsData.get("photos");
    		
    		if(placePhotos.size() > 0){
    		  
    			//ArrayList<HashMap<String,Object>> placePhotos = (ArrayList<HashMap<String, Object>>) placeDetailsData.get("photos");
    			Bitmap bmp = pCache.get((String) placePhotos.get(photoindex).get("photo_reference"));
    			
    			if(bmp != null){ //photo exists in cache
    			    imageView.setImageDrawable(new BitmapDrawable(getActivity().getResources(), bmp ));
    			}
    			else{
    			   imageView.setBackgroundResource(R.drawable.empty_photo);
    			   //((AnimationDrawable) imageView.getBackground()).start();
    			   HashMap<String,Object> photoParams = new HashMap<String,Object>();
    			   photoParams.put("photoreference", placePhotos.get(photoindex).get("photo_reference"));
    			   photoParams.put("maxwidth", 1600);
    			   PlacesClient ppc = new PlacesClient(getActivity(), photoParams, PlacesCallType.photos);
    			   imageView.setTag(placePhotos.get(photoindex).get("photo_reference"));
    			   ppc.getSingleLocationPhoto(imageView);
    			}
    			
    		}

            
            return photoView;
        }
    }    
    
    


}

