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
import android.content.Context;
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
import android.widget.RelativeLayout;
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
	private View singleView;
	private TextView photoControl;
	private TextView reviewControl;
	private LinearLayout dataContainer;
	private RelativeLayout pagerContainer;
	SingleLocationPagerAdapter slPagerAdapter;
	ViewPager mViewPager;
	View reviewView;
	LinearLayout reviewsSection;
	//private RelativeLayout reviewsPagerContainer;
	//ViewPager reviewsViewPager;
	public FragmentActivity mContext;
	final static String SINGLE_MAP_FRAGMENT = "singlemapfragment";
	final static String STREETVIEW_TEXT = "Street View";
	final static String AERIALVIEW_TEXT = "Aerial View";
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        //View singleView = inflater.inflate(R.layout.single_location_information2, container, false);
        singleView = inflater.inflate(R.layout.single_location_pager, container, false);
        LinearLayout dataContainer = (LinearLayout) singleView.findViewById(R.id.location_information_overlay);
        
        LinearLayout controlsBar = (LinearLayout) singleView.findViewById(R.id.single_location_controls);
        pagerContainer = new RelativeLayout(this.mContext);
        
        ArrayList<HashMap<String, Object>> placePhotosList = (ArrayList<HashMap<String, Object>>) this.placeDetailsData.get("photos");
        if(placePhotosList != null){
          //pagerContainer = new RelativeLayout(this.mContext);
          SingleLocationPagerAdapter slPagerAdapter = new SingleLocationPagerAdapter(mContext.getSupportFragmentManager(), this.locationData, this.placeDetailsData);
          //mViewPager = (ViewPager) singleView.findViewById(R.id.pager);
          mViewPager = new ViewPager(mContext);
          mViewPager.setId(R.id.location_view_pager);
          mViewPager.setAdapter(slPagerAdapter);
        }
        else{
    	   //((LinearLayout) singleView).removeView(singleView.findViewById(R.id.pager_photo_container));
    	}	
        //RelativeLayout photoPagerContainer = (RelativeLayout) singleView.findViewById(R.id.pager_photo_container);
        //photoPagerContainer.setVisibility(View.INVISIBLE);
        
        //LinearLayout controlsBar = (LinearLayout) singleView.findViewById(R.id.single_location_controls);
        
        if(mViewPager != null && pagerContainer != null){
        photoControl = (TextView) controlsBar.findViewById(R.id.single_location_photo_control);
        photoControl.setText("Open Photos");
        photoControl.setOnClickListener(new View.OnClickListener() { 	
        	 @Override
             public void onClick(View v) {
        		 TextView cv = (TextView) v;
        		 //RelativeLayout pagerContainer = (RelativeLayout) singleView.findViewById(R.id.pager_photo_container);
        		 LinearLayout locationLayout = (LinearLayout) singleView.findViewById(R.id.location_layout);
        		 
        		 if(((String) cv.getText()).equals("Open Photos")){
        			 
          			 if(pagerContainer.getChildCount() > 0 && locationLayout.getChildCount() > 3){
            			   pagerContainer.removeAllViews();
            			   locationLayout.removeViewAt(2);
            		  }
        			 pagerContainer.addView(mViewPager);
        			 LinearLayout.LayoutParams pLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        			 locationLayout.addView(pagerContainer, 2, pLayoutParams);
        			 photoControl.setText("Close Photos");
        		 }
        		 else if(((String) cv.getText()).equals("Close Photos")){
        			 pagerContainer.removeView(mViewPager);
        			 locationLayout.removeView(pagerContainer);
        			 photoControl.setText("Open Photos");
        		 }
        	 }
        });
        
        }
        else{
        	Log.i("ReviewsFrag controlBar remove Photo", "call");
        	controlsBar.removeView(controlsBar.findViewById(R.id.single_location_photo_control));
        }
       
        //REVIEWS
        /*
        ArrayList<HashMap<String, Object>> placeReviewsList = (ArrayList<HashMap<String, Object>>) this.placeDetailsData.get("reviews");
        if(placeReviewsList != null){
        	Log.i("ReviewsFrag", "not null" + String.valueOf(placeReviewsList.size()));
        	if(placeReviewsList.size() > 0){
              reviewsPagerContainer = new RelativeLayout(this.mContext);
              SingleLocationReviewsAdapter rPagerAdapter = new SingleLocationReviewsAdapter(mContext.getSupportFragmentManager(), this.locationData, this.placeDetailsData);
              reviewsViewPager = new ViewPager(mContext);
              reviewsViewPager.setId(R.id.reviews_view_pager);
              reviewsViewPager.setAdapter(rPagerAdapter);
        	}
          }
        if(reviewsViewPager != null && reviewsPagerContainer != null){
        	Log.i("ReviewsFrag", "pager and container not null");
        	 reviewControl = (TextView) controlsBar.findViewById(R.id.single_location_review_control);
        	 reviewControl.setText("See Reviews");
        	 reviewControl.setOnClickListener(new View.OnClickListener() { 	
             	 @Override
                  public void onClick(View v) {
             		 TextView cv = (TextView) v;
             		 //RelativeLayout pagerContainer = (RelativeLayout) singleView.findViewById(R.id.pager_photo_container);
             		 LinearLayout locationLayout = (LinearLayout) singleView.findViewById(R.id.location_layout);
             		 
             		 if(((String) cv.getText()).equals("See Reviews")){
             			 
             			 reviewsPagerContainer.addView(reviewsViewPager);
             			 LinearLayout.LayoutParams rLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
             			 locationLayout.addView(reviewsPagerContainer, 2, rLayoutParams);
             			 reviewControl.setText("Close Reviews");
             		 }
             		 else if(((String) cv.getText()).equals("Close Reviews")){
             			 reviewsPagerContainer.removeView(reviewsViewPager);
             			 locationLayout.removeView(reviewsPagerContainer);
             			 reviewControl.setText("See Reviews");
             		 }
             	 }
             });
        	
        }
        */
        
        //REVIEWS
        ArrayList<HashMap<String, Object>> placeReviewsList = (ArrayList<HashMap<String, Object>>) this.placeDetailsData.get("reviews");
        if(placeReviewsList != null){
        	Log.i("ReviewsFrag", "not null " + String.valueOf(placeReviewsList.size()));
        	if(placeReviewsList.size() > 0){

        	  this.reviewView = inflater.inflate(R.layout.single_location_information_reviews, container, false);	
              this.reviewsSection = (LinearLayout) reviewView.findViewById(R.id.single_review_container);
      		
      		  for (int i = 0; i < placeReviewsList.size(); i++){
      			
      			TextView tv =	new TextView(this.mContext);
      			LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      			lParams.setMargins(0, 0, 0, 10);
      			tv.setText(String.format("%s -- from %s on %s\n\n", (String) placeReviewsList.get(i).get("text"),  (String) placeReviewsList.get(i).get("author_name"), (String) placeReviewsList.get(i).get("time") ));
      			tv.setBackgroundResource(R.drawable.review_bg);
      			this.reviewsSection.addView(tv,lParams);
      			
      		   }  
              
         	 reviewControl = (TextView) controlsBar.findViewById(R.id.single_location_review_control);
         	 reviewControl.setText("See Reviews");
         	 reviewControl.setOnClickListener(new View.OnClickListener() { 	
              	 @Override
                   public void onClick(View v) {
              		 TextView cv = (TextView) v;
              		 //RelativeLayout pagerContainer = (RelativeLayout) singleView.findViewById(R.id.pager_photo_container);
              		 LinearLayout locationLayout = (LinearLayout) singleView.findViewById(R.id.location_layout);
              		 
              		 if(((String) cv.getText()).equals("See Reviews")){
              			 
              			 if(pagerContainer.getChildCount() > 0 && locationLayout.getChildCount() > 3){
              				locationLayout.removeViewAt(2);
              			    pagerContainer.removeAllViews();
              		     }
              			 pagerContainer.addView(reviewView);
              			 LinearLayout.LayoutParams rLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
              			 locationLayout.addView(pagerContainer, 2, rLayoutParams);
              			 reviewControl.setText("Close Reviews");
              		 }
              		 else if(((String) cv.getText()).equals("Close Reviews")){
              			 pagerContainer.removeView(reviewView);
              			 locationLayout.removeView(pagerContainer);
              			 reviewControl.setText("See Reviews");
              		 }
              	 }
              });
              
                            
        	}
        	else{
        		Log.i("ReviewsFrag controlBar remove Reviews", "call");
        		controlsBar.removeView(controlsBar.findViewById(R.id.single_location_review_control));
        	 }
        	
        }
        
        else{
        	controlsBar.removeView(controlsBar.findViewById(R.id.single_location_review_control));
        }
        //END REVIEWS
        
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
    
    
  /*
    private enum PagerType{
    	photos,
    	reviews
    }
    
    public static class SingleLocationReviewsAdapter extends FragmentStatePagerAdapter {

    	private HashMap<String,Object> singleLocationData;
    	private HashMap<String,Object> detailsData;
    	
        public SingleLocationReviewsAdapter(FragmentManager fm, HashMap<String,Object> locationData, HashMap<String,Object> placeDetails) {
            super(fm);
            this.singleLocationData = locationData;
            this.detailsData = placeDetails;
            Log.i("ReviewsFrag constructor", String.valueOf(placeDetails.size()) + " " +  String.valueOf(this.detailsData.size())  );
        }

        @Override
        public Fragment getItem(int i) {
        	
        	
            switch(i) {
            
                    
            default:
            	    Log.i("ReviewsFrag","getItem switch called");
                    Fragment rFragment = new SingleLocationReviewFragment();
                    Bundle rArgs = new Bundle();
                    rArgs.putSerializable("details", this.detailsData);
                    rArgs.putInt("index", i);
                    rFragment.setArguments(rArgs);
                    return rFragment;

            }
            
            
            
            
        }

        @Override
        public int getCount() {

        
        	     if(detailsData.get("reviews") != null){
        	    	
       		      int cnt = ((ArrayList<HashMap<String, Object>>) this.detailsData.get("reviews")).size();
       		      Log.i("ReviewsFrag","getCount switch called " + String.valueOf(cnt));
       		      return cnt;
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
    
    
    */
    
    
    
    
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
    
    
    
    
    /*
    public static class SingleLocationReviewFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";
        HashMap<String,Object> placeDetailsData;
        Context cxt;
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	Log.i("ReviewsFrag","onCreateView called");
            View reviewView = inflater.inflate(R.layout.single_location_information_reviews, container, false);
            Bundle args = getArguments();
            placeDetailsData = (HashMap<String, Object>) args.getSerializable("details");
            //int reviewindex = args.getInt("index");
            LinearLayout reviewsLayout = (LinearLayout) reviewView.findViewById(R.id.single_review_container);
    		ArrayList<HashMap<String, Object>> placeReviews = (ArrayList<HashMap<String, Object>>) placeDetailsData.get("reviews");
    		
    		for (int i = 0; i < placeReviews.size(); i++){
    			
    			TextView tv =	new TextView(cxt);
    			LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    			tv.setText(String.format("%s -- from %s on %s\n\n", (String) placeReviews.get(i).get("text"),  (String) placeReviews.get(i).get("author_name"), (String) placeReviews.get(i).get("time") ));
    			reviewsLayout.addView(tv,lParams);
    			
    			Log.i("ReviewsFrag", String.format("%s -- from %s on %s\n\n", (String) placeReviews.get(i).get("text"),  (String) placeReviews.get(i).get("author_name"), (String) placeReviews.get(i).get("time") ));
    		}
            
            return reviewView;
        }
        
        
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.cxt = activity;
        }
        
        
    }     
    */
    


}

