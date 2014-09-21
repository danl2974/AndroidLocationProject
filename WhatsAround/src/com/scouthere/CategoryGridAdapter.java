package com.scouthere;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.scouthere.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;


@SuppressLint("NewApi")
public class CategoryGridAdapter extends BaseAdapter {
	
    public Context mContext;
    private Location location;
    public String[] placeTypes;
    private Integer[] categoryImages;
    private Integer[] backgroundImages;
    GridViewCacheSingleton gridViewCache;
    /*
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
     */

    public CategoryGridAdapter(Context c, String[] categories, Integer[] imgResourceIds, Location loc) {
        mContext = c;
        placeTypes = categories;
        categoryImages = imgResourceIds;
        location = new Location(loc);//New object for dissociating Grid from Main Activity 
        gridViewCache = GridViewCacheSingleton.getInstance();
    }
    
    public CategoryGridAdapter(Context c, String[] categories, Integer[] imgResourceIds, Integer[] imgBgIds, Location loc) {
        mContext = c;
        placeTypes = categories;
        categoryImages = imgResourceIds;
        backgroundImages = imgBgIds;
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

        ImageView imageView;
    	
        if (convertView == null) {	

                imageView = new ImageView(mContext);
                GridView.LayoutParams layoutParams = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(1, 1, 1, 1);

       }
       else {
            imageView = (ImageView) convertView;
        }
        
        
        //if(gridViewCache.get(placeTypes[position]) != null){ 
        	  
        //}
        //else{	
         
        	Resources r = mContext.getResources();
        	Drawable[] layers = new Drawable[2];
        	layers[0] = r.getDrawable(backgroundImages[position]);
        	layers[1] = r.getDrawable(categoryImages[position]);
        	LayerDrawable layerDrawable = new LayerDrawable(layers);
        	imageView.setImageDrawable(layerDrawable);
        	
        //}
  
       imageView.setTag(position);
       imageView.setOnDragListener(new GridBlockDragListener(mContext));
       
       return imageView;

    }

    
    private void updateGridState(Integer dragged, Integer dropped){
    	
    	  String dragValue = placeTypes[dragged];
	      String dropValue = placeTypes[dropped];
	      Integer dragImg = categoryImages[dragged];
	      Integer dropImg = categoryImages[dropped];
	      Integer dragBg = backgroundImages[dragged];
	      Integer dropBg = backgroundImages[dropped];
	      
	      placeTypes[dragged] = dropValue;
	      placeTypes[dropped] = dragValue;
	      categoryImages[dragged] = dropImg;
	      categoryImages[dropped] = dragImg;
	      backgroundImages[dragged] = dropBg;
	      backgroundImages[dropped] = dragBg;
	      
    	
    }
    
    class GridBlockDragListener implements OnDragListener {
    	
    	  Activity activity;
    	  
    	  GridBlockDragListener(Context c){
    		  activity = (Activity) c;
    	  }
 	  
    	  @Override
    	  public boolean onDrag(View v, DragEvent event) {
    	    int action = event.getAction();
    	    switch (event.getAction()) {
    	    case DragEvent.ACTION_DRAG_STARTED:

    	      break;
    	    case DragEvent.ACTION_DRAG_ENTERED:
    	     
    	      break;
    	    case DragEvent.ACTION_DRAG_EXITED:       
    	      
    	      break;
    	    case DragEvent.ACTION_DROP:
    	      View view = (View) event.getLocalState();
    	      LinearLayout gridContainer = (LinearLayout) activity.findViewById(R.id.homegrid_container);
    	      ((TextView) gridContainer.getChildAt(0)).setText("Done! New Position Set");
    	      
    	      new Handler().postDelayed(new Runnable() {
    	            public void run() {
    	                ((LinearLayout) activity.findViewById(R.id.homegrid_container)).removeViewAt(0);
    	            }
    	           }, 3000);
    	      
    	     
    	      Drawable draggedDrawable = ((ImageView) view).getDrawable();
    	      Drawable droppedDrawable = ((ImageView) v).getDrawable();
    	      ((ImageView) v).setImageDrawable(draggedDrawable);
    	      ((ImageView) view).setImageDrawable(droppedDrawable);
    	      Integer draggedPosition = Integer.valueOf(event.getClipData().getItemAt(0).getText().toString());
    	      Integer droppedPosition = (Integer) v.getTag();
    	      updateGridState(draggedPosition, droppedPosition);
    	      //String dragValue = placeTypes[draggedPosition];
    	      //String dropValue = placeTypes[droppedPosition];
    	      //placeTypes[draggedPosition] = dropValue;
    	      //placeTypes[droppedPosition] = dragValue;
    	      view.setVisibility(View.VISIBLE);
    	      String draggedStr = HomeGridFragment.sharedPref.getString(String.valueOf(draggedPosition), "");
    	      String droppedStr = HomeGridFragment.sharedPref.getString(String.valueOf(droppedPosition), "");
    	      SharedPreferences.Editor sp = HomeGridFragment.sharedPref.edit();
    	      sp.putString(String.valueOf(droppedPosition), draggedStr);
    	      sp.putString(String.valueOf(draggedPosition), droppedStr);
    	      sp.commit();
    	      break;
    	    case DragEvent.ACTION_DRAG_ENDED:
    	      
    	    default:
    	      break;
    	    }
    	    return true;
    	  }
    	} 
    

}
