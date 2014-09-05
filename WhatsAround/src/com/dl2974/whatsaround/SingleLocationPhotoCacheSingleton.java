package com.dl2974.whatsaround;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

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
		
		mMemoryCache.put(key, bmp);
		
	}
	
	public void clearCache(){
		 mMemoryCache.evictAll();
	}
	
}

