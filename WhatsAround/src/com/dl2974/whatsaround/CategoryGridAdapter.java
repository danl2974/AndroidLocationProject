package com.dl2974.whatsaround;

import java.util.ArrayList;
import java.util.HashMap;

import com.dl2974.whatsaround.PlacesClient.PlacesCallType;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.support.v4.util.LruCache;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
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
    private Location location;
    private String[] placeTypes;
    private Integer[] categoryImages;
    GridViewCacheSingleton gridViewCache;
    private ArrayList<String> typePhotosLedger = new ArrayList<String>();
    private HashMap<String,TextView> views;
    private int counter = 0;
    private int countercache = 0;
    final static private Integer[] BG_IMG_IDS = {
    	R.drawable.restaurants_bg, R.drawable.shopping_bg, R.drawable.hotel_bg,
		R.drawable.gas_bg, R.drawable.hair_bg, R.drawable.medical_bg,
		R.drawable.bar_bg, R.drawable.cafe_bg, R.drawable.fitness_bg,
		R.drawable.store_bg, R.drawable.entertainment_bg, R.drawable.financial_bg,
		R.drawable.culture_bg, R.drawable.default_bg, R.drawable.default_bg,
		R.drawable.publicgovt_bg, R.drawable.pharmacy_bg, R.drawable.transport_bg,
		R.drawable.default_bg, R.drawable.education_bg, R.drawable.automotive_bg,
		R.drawable.worship_bg, R.drawable.pets_bg, R.drawable.default_bg,
		};


    public CategoryGridAdapter(Context c, String[] categories, Integer[] imgResourceIds, Location loc) {
        mContext = c;
        placeTypes = categories;
        categoryImages = imgResourceIds;
        location = new Location(loc);//New object for dissociating Grid from Main Activity 
        gridViewCache = GridViewCacheSingleton.getInstance();
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
        ImageView imageView;
    	
        if (convertView == null) {	

                imageView = new ImageView(mContext);
                GridView.LayoutParams layoutParams = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(layoutParams);
                //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(1, 1, 1, 1);
                //textView.setAdjustViewBounds(true);
                //imageView.setBackgroundResource(R.drawable.customborder);
                //imageView.setBackgroundColor(0xFFF1F1F0);

       }
       else {
            imageView = (ImageView) convertView;
        }
        
        
        if(gridViewCache.get(placeTypes[position]) != null){ 
        	  //textView.setBackground(new BitmapDrawable(mContext.getResources(), gridViewCache.get(placeTypes[position])));
        	  //textView.setBackgroundResource(R.drawable.grid_bg);
        }
        else{	
        	Resources r = mContext.getResources();
        	Drawable[] layers = new Drawable[2];
        	//layers[0] = r.getDrawable(R.drawable.grid_bg);
        	layers[0] = r.getDrawable(BG_IMG_IDS[position]);
        	layers[1] = r.getDrawable(categoryImages[position]);
        	LayerDrawable layerDrawable = new LayerDrawable(layers);
        	imageView.setImageDrawable(layerDrawable);
        	//imageView.setBackgroundResource(categoryImages[position]);
            //textView.setBackgroundResource(categoryImages[position]);
            //textView.setBackgroundResource(R.drawable.grid_bg);

            // DO GRID PHOTO ASYNC FETCH
            /*
            if(!typePhotosLedger.contains(placeTypes[position]) && this.location != null){
 	           HashMap<String,Object> searchParams = new HashMap<String,Object>();
               searchParams.put("location", String.format("%s,%s", location.getLatitude(), location.getLongitude() ));
               searchParams.put("radius", "5000");
               searchParams.put("types", placeTypes[position]);
               PlacesClient homeGridPC = new PlacesClient(mContext, searchParams, PlacesCallType.search);
               TypePhotoGridParams tpgParams = new TypePhotoGridParams();
               tpgParams.type = placeTypes[position];
               tpgParams.textView = textView;
               tpgParams.width = 250;
               tpgParams.height = 350;
               
               homeGridPC.startGridPhotoTask(tpgParams);
               typePhotosLedger.add(placeTypes[position]);
               counter++;
               Log.i("GridAdapterOnce", String.valueOf(counter) + " " + String.valueOf(textView.getText()));
             } 
             */
        }
        /*
        Spannable spanStr = new SpannableString(placeTypes[position]);        
        //spanStr.setSpan(new BackgroundColorSpan(0xFF59c2a3), 0, placeTypes[position].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanStr);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextScaleX(1.5f);
        */
        //imageView.setMaxHeight(160);
        //imageView.setWidth(100);
    	
       return imageView;
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
