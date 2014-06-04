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
    }

    OnLocationTypeSelectedListener selectionCallback;
    
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        setListAdapter(new ArrayAdapter<String>(getActivity(), layout, LOCATION_TYPE_NAMES));
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
    	int locationId = FACTUAL_IDS[position];
    	selectionCallback.onLocationTypeSelected(locationId);
        getListView().setItemChecked(position, true);
        
    }    
	
}
