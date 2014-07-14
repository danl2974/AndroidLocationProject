package com.dl2974.whatsaround;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocationsListFragment extends ListFragment {
	
    public interface OnLocationTypeSelectedListener {
        
        public void onLocationTypeSelected(int categoryId);
        public void onLocationTypeFilter(String filter);
    }

    OnLocationTypeSelectedListener selectionCallback;
    
    final static private String[] PLACES_TYPES = {"accounting","airport","amusement_park","aquarium","art_gallery","atm","bakery","bank","bar","beauty_salon","bicycle_store","book_store","bowling_alley","bus_station","cafe","campground","car_dealer","car_rental","car_repair","car_wash","casino","cemetery","church","city_hall","clothing_store","convenience_store","courthouse","dentist","department_store","doctor","electrician","electronics_store","embassy","establishment","finance","fire_station","florist","food","funeral_home","furniture_store","gas_station","general_contractor","grocery_or_supermarket","gym","hair_care","hardware_store","health","hindu_temple","home_goods_store","hospital","insurance_agency","jewelry_store","laundry","lawyer","library","liquor_store","local_government_office","locksmith","lodging","meal_delivery","meal_takeaway","mosque","movie_rental","movie_theater","moving_company","museum","night_club","painter","park","parking","pet_store","pharmacy","physiotherapist","place_of_worship","plumber","police","post_office","real_estate_agency","restaurant","roofing_contractor","rv_park","school","shoe_store","shopping_mall","spa","stadium","storage","store","subway_station","synagogue","taxi_stand","train_station","travel_agency","university","veterinary_care","zoo"};
	final static private int[] FACTUAL_IDS = {2, 62, 123, 149, 177, 308, 312, 372, 415, 430};
	final static private String[] LOCATION_TYPE_NAMES = {
		"Automotive",
		"Healthcare", 
		"Retail",
		"Food and Beverage",
		"Services and Supplies",
		"Social",
		"Bars",
		"Sports and Recreation",
		"Transportation",
		"Travel"
	};
	final static private String[] YELP_FILTERS = {
		"auto",
		"health",
		"shopping",
		"food,restaurants",
		"localservices",
		"arts",
		"nightlife",
		"active",
		"transport",
		"hotelstravel"
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        //setListAdapter(new ArrayAdapter<String>(getActivity(), layout, LOCATION_TYPE_NAMES));
        setListAdapter(new ArrayAdapter<String>(getActivity(), layout, PLACES_TYPES));
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
        	selectionCallback = (OnLocationTypeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
    	//int locationId = FACTUAL_IDS[position];
    	//selectionCallback.onLocationTypeSelected(locationId);
    	//String yelpfilter = YELP_FILTERS[position];
    	//selectionCallback.onLocationTypeFilter(yelpfilter);
    	String placesType = PLACES_TYPES[position];
    	selectionCallback.onLocationTypeFilter(placesType);    	
        getListView().setItemChecked(position, true);
        
    }    
	
}
