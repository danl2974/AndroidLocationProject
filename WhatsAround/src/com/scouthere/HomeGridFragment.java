package com.scouthere;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.scouthere.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.widget.AdapterView;
import android.widget.GridView;

@SuppressLint("NewApi")
public class HomeGridFragment extends Fragment {
	
    public interface OnPlaceTypeSelectedListener {
        
        public void onPlaceTypeFilter(String filter, String resourceName);
        
    }
    
    OnPlaceTypeSelectedListener selectionCallback;
    private HashMap<String,Object> typephotoMap;
    private Location location;
    static public SharedPreferences sharedPref;
    static public String[] mPlaceTypes = new String[25];
    static public String[] mPlaceNames = new String[25];
    static public Integer[] mTypeGridImages = new Integer[25];
	
    final static public String[] PLACES_TYPES = {
    	"bakery|food|restaurant|meal_delivery|meal_takeaway", //Restaurants
    	"clothing_store|department_store|shopping_mall|shoe_store",//Shopping
    	"lodging", //Hotel
    	"gas_station", //Gas
    	"beauty_salon|hair_care",//Hair
    	"doctor|hospital|dentist", //Medical
    	"bar",//Bar
    	"cafe",//Cafe
    	"gym|health", //Fitness
    	"convenience_store|electronics_store|furniture_store|hardware_store|grocery_or_supermarket|liquor_store|home_goods_store|store|laundry", //Store
    	"amusement_park|bowling_alley|casino|movie_rental|movie_theater|night_club|zoo", //Entertainment
    	"atm|bank|finance", //Financial
    	"art_gallery|book_store|museum|park|aquarium|stadium", //Culture
    	"establishment", //Establishment
    	"accounting|electrician|general_contractor|roofing_contractor|painter|locksmith|lawyer|plumber|physiotherapist|insurance_agency|real_estate_agency|travel_agency", //Professional
    	"city_hall|courthouse|embassy|local_government_office|fire_station|police|post_office|library|parking", //Government/Public Services
    	"pharmacy", //Pharmacy
    	"airport|bus_station|subway_station|taxi_stand|train_station", //Transport
    	"bicycle_store|funeral_home|jewelry_store|florist|storage|cemetery|spa|moving_company", //Specialty
    	"school|university", //Education
    	"car_dealer|car_rental|car_repair|car_wash", //Automotive
    	"church|place_of_worship|mosque|synagogue|hindu_temple", //Worship
    	"pet_store|veterinary_care", // Pets
    	"campground|rv_park", //Outdoors
    	};
    
	final static private String[] INFOWINDOW_RESOURCE_NAMES = {

		"restaurants_iw", "shopping_iw", "hotel_iw",
		"gas_iw", "hair_iw", "medical_iw",
		"bar_iw", "cafe_iw", "fitness_iw",
		"store_iw", "entertainment_iw", "financial_iw",
		"culture_iw", "establishment_iw", "professional_iw",
		"publicgovt_iw", "pharmacy_iw", "transport_iw",
		"specialty_iw", "education_iw", "automotive_iw",
		"worship_iw", "pets_iw", "outdoors_iw"
		
    };
    	
	final static public Integer[] GRID_IMG_IDS = {
		R.drawable.restaurants, R.drawable.shopping, R.drawable.hotel,
		R.drawable.gas, R.drawable.hair, R.drawable.medical,
		R.drawable.bar, R.drawable.cafe, R.drawable.fitness,
		R.drawable.store, R.drawable.entertainment, R.drawable.financial,
		R.drawable.culture, R.drawable.establishment, R.drawable.professional,
		R.drawable.publicgovt, R.drawable.pharmacy, R.drawable.transport,
		R.drawable.specialty, R.drawable.education, R.drawable.automotive,
		R.drawable.worship, R.drawable.pets, R.drawable.outdoors,          
    };


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    
		sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		//sharedPref.edit().clear().commit();
		
		if(sharedPref.contains(getResources().getString(R.string.loaded))){
			Log.i("HomeGridPrefStatus", "Prefs loaded " + String.valueOf(sharedPref.getAll().size()));
			for (Entry<String, ?> pref : sharedPref.getAll().entrySet()){
				
			  if(!pref.getKey().equals(getResources().getString(R.string.loaded))){	
				String[] vals = ((String) pref.getValue()).split("::");
				String key = pref.getKey();
				 Log.i("HomeGridPref"+key, String.valueOf(vals[0]));
				 mPlaceTypes[Integer.valueOf(key)] = vals[0];

				 Log.i("HomeGridPref"+key, String.valueOf(vals[1]));
				 mTypeGridImages[Integer.valueOf(key)] = Integer.valueOf(vals[1]);

				 Log.i("HomeGridPref"+key, String.valueOf(vals[2]));
				 mPlaceNames[Integer.valueOf(key)] = vals[2];
			    }
			}
		}
		else{
			Log.i("HomeGridPrefStatus", "No Prefs loaded");
			mPlaceTypes = PLACES_TYPES;
			mTypeGridImages = GRID_IMG_IDS;
			mPlaceNames = INFOWINDOW_RESOURCE_NAMES;
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean(getResources().getString(R.string.loaded), true);
			for(int i = 0; i < GRID_IMG_IDS.length; i++){
				StringBuilder sb = new StringBuilder();
				sb.append(PLACES_TYPES[i]);
				sb.append("::");
				sb.append(String.valueOf(GRID_IMG_IDS[i]));
				sb.append("::");
				sb.append(INFOWINDOW_RESOURCE_NAMES[i]);
				editor.putString(String.valueOf(i), sb.toString());
			}
			editor.commit();
		}
		
	    View gridLayout = getActivity().getLayoutInflater().inflate(R.layout.home_grid, container, false);
	    gridLayout.setBackgroundColor(0xFFFFFFFF);
	    GridView gridview = (GridView) gridLayout.findViewById(R.id.gridview);
	    gridview.setAdapter(new CategoryGridAdapter(getActivity(), mPlaceTypes, mTypeGridImages, this.location));

	    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            
	        	String placesType = mPlaceTypes[position];
	        	String resourceName = mPlaceNames[position];
	        	selectionCallback.onPlaceTypeFilter(placesType, resourceName);
	        	
	        }
	    });
	    gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
	    	
			@SuppressLint("NewApi")
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
	    		  
	    		  ClipData data = ClipData.newPlainText("position", String.valueOf(position));
	    	      DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
	    	      view.startDrag(data, shadowBuilder, view, 0);
	    	      view.setVisibility(View.INVISIBLE);
	    	      return true;
	    		  
	    	  }
	    });
	    
	    
	    return gridLayout;
	}
	
	
	public void setTypePhotoMap(HashMap<String,Object> hm){
		
		this.typephotoMap = hm;
	}
	
	public void setLocation(Location loc){
		
		this.location = loc;
	}	
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
        	selectionCallback = (OnPlaceTypeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPlaceTypeSelectedListener");
        }
    }
	

}
