package com.dl2974.whatsaround;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryGridAdapter extends BaseAdapter {
	
    private Context mContext;
    private String[] placeTypes;
    private Integer[] categoryImages;

    public CategoryGridAdapter(Context c, String[] categories, Integer[] imgResourceIds) {
        mContext = c;
        placeTypes = categories;
        categoryImages = imgResourceIds;
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
    	/*
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
        */
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //imageView.setPadding(3, 3, 3, 3);
            imageView.setAdjustViewBounds(true);
            imageView.setBackgroundResource(R.drawable.customborder);
            //imageView.setBackgroundColor(0xFFF1F1F0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(categoryImages[position]);
        return imageView;
    }



}
