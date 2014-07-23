package com.dl2974.whatsaround;

import java.util.ArrayList;
import java.util.HashMap;

import com.dl2974.whatsaround.LocationsListFragment.OnLocationTypeSelectedListener;

import android.app.Activity;
import android.content.res.Configuration;
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
	
	final static private String[] PLACES_TYPES = {"accounting","airport","amusement_park","aquarium","art_gallery","atm","bakery","bank","bar","beauty_salon","bicycle_store","book_store","bowling_alley","bus_station","cafe","campground","car_dealer","car_rental","car_repair","car_wash","casino","cemetery","church","city_hall","clothing_store","convenience_store","courthouse","dentist","department_store","doctor","electrician","electronics_store","embassy","establishment","finance","fire_station","florist","food","funeral_home","furniture_store","gas_station","general_contractor","grocery_or_supermarket","gym","hair_care","hardware_store","health","hindu_temple","home_goods_store","hospital","insurance_agency","jewelry_store","laundry","lawyer","library","liquor_store","local_government_office","locksmith","lodging","meal_delivery","meal_takeaway","mosque","movie_rental","movie_theater","moving_company","museum","night_club","painter","park","parking","pet_store","pharmacy","physiotherapist","place_of_worship","plumber","police","post_office","real_estate_agency","restaurant","roofing_contractor","rv_park","school","shoe_store","shopping_mall","spa","stadium","storage","store","subway_station","synagogue","taxi_stand","train_station","travel_agency","university","veterinary_care","zoo"};
    //final static private String[] PLACES_TYPES = {"accounting","airport","amusement_park","aquarium","art_gallery","atm","bakery","bank","bar","beauty_salon","bicycle_store","book_store","bowling_alley","bus_station","cafe","campground","car_dealer","car_rental","car_repair","car_wash","casino","cemetery"};
	final static private Integer[] GRID_IMG_IDS = {
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.bars2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.bars2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.bars2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.bars2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.shopping2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.bars2_grid,
            R.drawable.bars2_grid, R.drawable.shopping2_grid,
            R.drawable.restaurant2_grid, R.drawable.bars2_grid            
    };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    

	    View gridLayout = getActivity().getLayoutInflater().inflate(R.layout.home_grid, container, false);
	    gridLayout.setBackgroundColor(0xFFF1F1F0);
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
