package com.periscoper;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

public class SingleLocationPhotoCacheSingleton {
	
	LruCache<String, Bitmap> mMemoryCache;
	
	private static SingleLocationPhotoCacheSingleton instance = new SingleLocationPhotoCacheSingleton();
	
	private SingleLocationPhotoCacheSingleton(){
		
		mMemoryCache = new LruCache<String, Bitmap>(10);
		
	}
	
	public static SingleLocationPhotoCacheSingleton getInstance(){
		
	      return instance;
	     
	}
	
	public Bitmap get(String key){
		
		Bitmap bmp = null;
		bmp = mMemoryCache.get(key);
		return bmp;
		
	}
	
	public void put(String key, Bitmap bmp){
		
		Log.i("SingleLocationPhotoCacheSingleton", "size: " + String.valueOf(mMemoryCache.size()));
		mMemoryCache.trimToSize(3);
		mMemoryCache.put(key, bmp);
		
	}
	
	public void clearCache(){
		 mMemoryCache.evictAll();
		 Log.i("SingleLocationPhotoCacheSingleton", "mMemoryCache.evictAll() called");
	}
	
}

