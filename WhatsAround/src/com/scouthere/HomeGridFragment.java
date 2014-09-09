package com.scouthere;

import java.util.HashMap;

import com.scouthere.R;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class HomeGridFragment extends Fragment {
	
    public interface OnPlaceTypeSelectedListener {
        
        public void onPlaceTypeFilter(String filter);
        
    }
    
    OnPlaceTypeSelectedListener selectionCallback;
    private HashMap<String,Object> typephotoMap;
    private Location location;
	
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
	
	final static private Integer[] GRID_IMG_IDS = {
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
	    

	    View gridLayout = getActivity().getLayoutInflater().inflate(R.layout.home_grid, container, false);
	    gridLayout.setBackgroundColor(0xFFFFFFFF);
	    GridView gridview = (GridView) gridLayout.findViewById(R.id.gridview);
	    gridview.setAdapter(new CategoryGridAdapter(getActivity(), PLACES_TYPES, GRID_IMG_IDS, this.location));

	    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            
	        	String placesType = PLACES_TYPES[position];
	        	selectionCallback.onPlaceTypeFilter(placesType);
	        	
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
