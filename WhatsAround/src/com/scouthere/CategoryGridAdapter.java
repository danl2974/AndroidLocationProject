package com.scouthere;

import com.scouthere.R;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
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
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;


@SuppressLint("NewApi")
public class CategoryGridAdapter extends BaseAdapter {
	
    private Context mContext;
    private Location location;
    public String[] placeTypes;
    private Integer[] categoryImages;
    GridViewCacheSingleton gridViewCache;

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
        
        
        if(gridViewCache.get(placeTypes[position]) != null){ 
        	  
        }
        else{	
        	Resources r = mContext.getResources();
        	Drawable[] layers = new Drawable[2];
        	layers[0] = r.getDrawable(BG_IMG_IDS[position]);
        	layers[1] = r.getDrawable(categoryImages[position]);
        	LayerDrawable layerDrawable = new LayerDrawable(layers);
        	imageView.setImageDrawable(layerDrawable);

        }
       //imageView.setOnTouchListener(new GridBlockTouchListener());
       //imageView.setOnLongClickListener(new GridBlockLongClickListener());
       imageView.setTag(position);
       imageView.setOnDragListener(new GridBlockDragListener());
       return imageView;

    	
    }


    /*
    @SuppressLint("NewApi")
	private final class GridBlockTouchListener implements OnTouchListener {
    	  @SuppressLint("NewApi")
		public boolean onTouch(View view, MotionEvent motionEvent) {
    	    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
    	      ClipData data = ClipData.newPlainText("", "");
    	      DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
    	      view.startDrag(data, shadowBuilder, view, 0);
    	      view.setVisibility(View.INVISIBLE);
    	      return true;
    	    } else {
    	    return false;
    	    }
    	  }
    	}
    
    
    
	private final class GridBlockLongClickListener implements OnLongClickListener {
    	
		public boolean onLongClick(View view) {
    	   
    	      ClipData data = ClipData.newPlainText("", "");
    	      DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
    	      view.startDrag(data, shadowBuilder, view, 0);
    	      view.setVisibility(View.INVISIBLE);
    	      return true;
    	    
    	  }
    	}
    */
    
    class GridBlockDragListener implements OnDragListener {
    	  //Drawable enterShape = getActivity().getResources().getDrawable(R.drawable.shape_droptarget);
    	  //Drawable normalShape = getResources().getDrawable(R.drawable.shape);
    	  
    	  @Override
    	  public boolean onDrag(View v, DragEvent event) {
    	    int action = event.getAction();
    	    switch (event.getAction()) {
    	    case DragEvent.ACTION_DRAG_STARTED:
    	    // do nothing
    	      break;
    	    case DragEvent.ACTION_DRAG_ENTERED:
    	      //v.setBackgroundDrawable(enterShape);
    	      break;
    	    case DragEvent.ACTION_DRAG_EXITED:        
    	      //v.setBackgroundDrawable(normalShape);
    	      break;
    	    case DragEvent.ACTION_DROP:
    	      // Dropped, reassign View to ViewGroup
    	      View view = (View) event.getLocalState();
    	      ((ImageView) v).setImageDrawable(((ImageView) view).getDrawable());
    	      ((ImageView) view).setImageDrawable(((ImageView) v).getDrawable());
    	      Integer draggedPosition = Integer.valueOf(event.getClipData().getItemAt(0).getText().toString());
    	      Integer droppedPosition = (Integer) v.getTag();
    	      String dragValue = placeTypes[draggedPosition];
    	      String dropValue = placeTypes[droppedPosition];
    	      placeTypes[draggedPosition] = dropValue;
    	      placeTypes[droppedPosition] = dragValue;
    	      
    	      ViewGroup owner = (ViewGroup) view.getParent();
    	      ListAdapter lad = ((GridView) owner).getAdapter();
    	      
    	      Log.i("DragDrop", "view " + String.valueOf(view.getClass()));
    	      Log.i("DragDrop", "owner " + String.valueOf(owner.getClass()));
    	      Log.i("DragDrop", "v " + String.valueOf(v.getClass()));
    	      Log.i("DragDrop", "v tag " + String.valueOf(v.getTag()));
    	      Log.i("DragDrop", "lad " + String.valueOf(lad.getClass()));
    	      //owner.removeView(view);
    	      //GridLayout container = (GridLayout) v;
    	      //ImageView container = (ImageView) v;
    	      //container.addView(view);
    	      
    	      
    	      view.setVisibility(View.VISIBLE);
    	      break;
    	    case DragEvent.ACTION_DRAG_ENDED:
    	      //v.setBackgroundDrawable(normalShape);
    	      default:
    	      break;
    	    }
    	    return true;
    	  }
    	} 
    

}
