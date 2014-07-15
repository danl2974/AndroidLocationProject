package com.dl2974.whatsaround;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class CategoryGridAdapter extends BaseAdapter {
	
    private Context mContext;
    private String[] placeTypes; 

    public CategoryGridAdapter(Context c, String[] categories) {
        mContext = c;
        placeTypes = categories;
    }

    public int getCount() {
        return placeTypes.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            tView = new TextView(mContext);
            tView.setLayoutParams(new GridView.LayoutParams(200, 200));
            tView.setPadding(8, 8, 8, 8);
        } else {
            tView = (TextView) convertView;
        }

        tView.setText(placeTypes[position]);
        return tView;
    }



}
