package com.dl2974.whatsaround;

import java.util.ArrayList;
import java.util.HashMap;

import com.dl2974.whatsaround.PlacesClient.PlacesCallType;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
    private HashMap<String,Object> typephotoMap;

    public CategoryGridAdapter(Context c, String[] categories, Integer[] imgResourceIds, HashMap<String,Object> hm) {
        mContext = c;
        placeTypes = categories;
        categoryImages = imgResourceIds;
        this.typephotoMap = (hm != null) ? hm : new HashMap<String,Object>();
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

    @SuppressLint("NewApi")
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
    	
    	/////
    	/*
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setAdjustViewBounds(true);
            //imageView.setBackgroundResource(R.drawable.customborder);
            //imageView.setBackgroundColor(0xFFF1F1F0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(categoryImages[position]);  
        //imageView.setImageDrawable(categoryImages[position].getDrawable());
        return imageView;
        */
        
    	
        TextView textView;
        if (convertView == null) {  
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            //textView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            textView.setPadding(2, 2, 2, 2);
            //textView.setAdjustViewBounds(true);
            //imageView.setBackgroundResource(R.drawable.customborder);
            //imageView.setBackgroundColor(0xFFF1F1F0);
        } else {
            textView = (TextView) convertView;
        }
        
        if(this.typephotoMap.containsKey(placeTypes[position])){

        	textView.setBackground(new BitmapDrawable(mContext.getResources(), (Bitmap) this.typephotoMap.get(placeTypes[position]) ));
        }
        else{
        	textView.setBackgroundResource(categoryImages[position]);
        }
        
        //textView.setBackgroundResource(categoryImages[position]);
        textView.setText(placeTypes[position]);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        
        return textView;
    	
    	/*
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder;
        ImageView imageView;
        if (convertView == null) { 
        	convertView = inflater.inflate(R.layout.home_grid, parent, true);                       
            holder = new ViewHolder();
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setAdjustViewBounds(true);
            holder.image = imageView;
            holder.text = new TextView(mContext);
            convertView.setTag(holder);

        } else {    
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageResource(categoryImages[position]);
        holder.text.setText(placeTypes[position]);

        return convertView;
    	*/
    	
    }



}
