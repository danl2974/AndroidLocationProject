package com.dl2974.whatsaround;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class GridViewCacheSingleton {
	
	LruCache<String, Bitmap> mMemoryCache;
	
	private static GridViewCacheSingleton instance = new GridViewCacheSingleton();
	
	private GridViewCacheSingleton(){
		
		mMemoryCache = new LruCache<String, Bitmap>(HomeGridFragment.PLACES_TYPES.length);
		
	}
	
	public static GridViewCacheSingleton getInstance(){
		
	      return instance;
	     
	}
	
	public Bitmap get(String key){
		
		Bitmap bmp = null;
		bmp = mMemoryCache.get(key);
		return bmp;
		
	}
	
	public void put(String key, Bitmap bmp){
		
		mMemoryCache.put(key, bmp);
		
	}
	
}
