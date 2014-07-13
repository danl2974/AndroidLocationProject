package com.dl2974.whatsaround;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.os.AsyncTask;
import android.util.Log;

import com.dl2974.whatsaround.FactualClient.FactualClientTask;
import com.factual.driver.Circle;
import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.factual.driver.ReadResponse;


public class PlacesClient {
	
    private String apiKey;
	private double latitude;
	private double longitude;
	private int meterPerimeter = 20000;
    private String endpoint;
    private String requestParameters;
    private PlacesCallType placesCallType;
    public static String[] searchDataFields = {"icon","id","name","place_id","reference","scope","vicinity"};
    public static String[] detailsDataFields = {"formatted_address","formatted_phone_number","icon","id","scope","url","user_ratings_total","utc_offset","vicinity","website","place_id","price_level","reference"};
    public static String[] reviewsDataFields = {"author_name","author_url","language","rating","text","time"};
    public static String[] photosDataFields = {};
    public static String[] days = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    
	public enum PlacesCallType {
		search,
		details,
		photos
	}
	
	public PlacesClient(HashMap<String,Object> parameters, String apiKey, PlacesCallType callType){
		  
		this.apiKey = apiKey;
		
		switch(callType){
		
		case search:
			this.placesCallType = PlacesCallType.search;
			this.endpoint = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
			this.requestParameters = createUrlParameters(parameters);
			break;
			
		case details:
			this.placesCallType = PlacesCallType.details;
			this.endpoint = "https://maps.googleapis.com/maps/api/place/details/json";
			this.requestParameters = createUrlParameters(parameters);
			break;
			
		case photos:
			this.placesCallType = PlacesCallType.photos;
			this.endpoint = "https://maps.googleapis.com/maps/api/place/photo";
			this.requestParameters = createUrlParameters(parameters);
			break;
		
		}
		  
	}
	
	public ArrayList<HashMap<String,Object>> getPlacesData(){
		
		ArrayList<HashMap<String,Object>> result = null;
		AsyncTask<String, Void, ArrayList<HashMap<String,Object>>> task = new PlacesClientTask().execute(this.requestParameters);
		   
		try {
			 result = task.get();
		} catch (InterruptedException e) {
			Log.e(this.getClass().getName(), e.getMessage());
		} catch (ExecutionException e) {
			Log.e(this.getClass().getName(), e.getMessage());
		}
		  
		  return result;
		
	}
	
	
	
	private String call(String requestpath, String requestParams){

			String callResponse = "";
			HttpURLConnection conn = null;
			InputStream is = null;
			try{
			    String urlString = this.endpoint + "?" + requestParams;
		        URL url = new URL(urlString);
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            conn.setDoInput(true);
	            //conn.setRequestProperty("Connection", "Keep-Alive");
		        conn.connect();
	            int response = conn.getResponseCode();
	            if (response == 200){
	              is = conn.getInputStream();
	            }
	            else{
	        	 is = conn.getErrorStream();
	              for (Map.Entry<String,List<String>> hf : conn.getHeaderFields().entrySet()){
	            	  Log.i("PlacesClientResponseHeader", String.format("%s %s", hf.getKey(), hf.getValue().get(0) ) );
	              }	        	 
	             }
	             BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	             StringBuilder sb =  new StringBuilder();
	             String sLine = "";

	     	     while ((sLine = reader.readLine()) != null) {
	     		    sb.append(sLine);
	     	     }
	     	     
	     	     callResponse = sb.toString();
	         
			}
			catch(Exception e){
				Log.e("PlacesClientException", "Exception call " + e.getMessage());
			}
			finally {
			    conn.disconnect();
			   }
			
			return callResponse;
	  
	   } 
	
	
	
    public class PlacesClientTask extends AsyncTask<String, Void, ArrayList<HashMap<String,Object>> > {
	    	 
	        @Override
	        protected ArrayList<HashMap<String,Object>> doInBackground(String... urlParams) {
	              
	        try {
	        	
                    String placesResult = call(PlacesClient.this.endpoint, urlParams[0]);
                    ArrayList<HashMap<String,Object>> response = null;
                    
            		switch(PlacesClient.this.placesCallType){
            		
            		case search:
            			response = parseJsonSearchResponse(placesResult);
            			break;
            			
            		case details:
            			response = parseJsonDetailsResponse(placesResult);
            			break;
            			
            		case photos:
            			response = parseJsonPhotosResponse(placesResult);
            			break;
            		
            		}
                    
	                return response;
	                
	            } catch (Exception e) {
	                
	                return null;
	            }
	        }
	        	        
	        // onPostExecute displays the results of the AsyncTask.
	        @Override
	        protected void onPostExecute(ArrayList<HashMap<String,Object>> result) {
                //No UI to update      	
	        	
	       }
	    }
	  

    
    
    String createUrlParameters(HashMap<String,Object> keyValues){
    	
    	StringBuilder sb = new StringBuilder();
    	for(Map.Entry<String,Object> kv : keyValues.entrySet()){
    		
    	     sb.append(String.format("%s=%s", kv.getKey(), String.valueOf(kv.getValue()) ));
    		 sb.append("&");
    	}
    	
    	return sb.deleteCharAt(sb.length() - 1).toString();
    	
    }
    
    
    ArrayList<HashMap<String,Object>>  parseJsonSearchResponse(String jsonString){
    	
		ArrayList<HashMap<String,Object>> hmlist = new ArrayList<HashMap<String,Object>>();
		JSONParser parser= new JSONParser();
		JSONObject obj = null;
        if(jsonString != null){
		  try {
			obj = (JSONObject) parser.parse(jsonString);
		  } catch (ParseException e) {
			Log.e("parseJsonSearchResponse", e.getMessage());
		  }
        
		  if (obj.get("status").equals("OK")){
		  
			  JSONArray resultsArray = (JSONArray) obj.get("results");
			  Iterator i = resultsArray.iterator();
			  while (i.hasNext()) {
				  
				  HashMap<String,Object> hm = new HashMap<String,Object>();
				  JSONObject resultItem = (JSONObject) i.next();
				  JSONObject location = (JSONObject) ((JSONObject) resultItem.get("geometry")).get("location");
				  hm.put("latitude", String.valueOf(location.get("lat")));
				  hm.put("longitude", String.valueOf(location.get("lng")));
				  JSONArray types = (JSONArray) resultItem.get("types");
				  ArrayList typesList = new ArrayList();
				  for(int t = 0; t < types.size(); t++){
					  typesList.add(types.get(t));
				  }
				  hm.put("types",typesList);
				  for(int f = 0; f < PlacesClient.searchDataFields.length; f++){
					  String field = PlacesClient.searchDataFields[f];
					  hm.put(field, (String) resultItem.get(field));
				  }	
				  
			   hmlist.add(hm);
			  }
		  
		  }
        }
		return hmlist;    	
    	
    } 
    
    
    
    ArrayList<HashMap<String,Object>>  parseJsonDetailsResponse(String jsonString){
    	 	
		ArrayList<HashMap<String,Object>> hmlist = new ArrayList<HashMap<String,Object>>();
		JSONParser parser= new JSONParser();
		JSONObject obj = null;
        if(jsonString != null){
		  try {
			obj = (JSONObject) parser.parse(jsonString);
		  } catch (ParseException e) {
			Log.e("parseJsonDetailsResponse", e.getMessage());
		  }
		  
		  if (obj.get("status").equals("OK")){
			  
			  JSONObject resultObj = (JSONObject) obj.get("result");
			  HashMap<String,Object> hm = new HashMap<String,Object>();
			  
			  for(int f = 0; f < PlacesClient.detailsDataFields.length; f++){
				  String field = PlacesClient.detailsDataFields[f];
				  hm.put(field, (String) resultObj.get(field));
			  }
			  
			  hm.put("hours", formatDetailsHours((JSONObject) resultObj.get("opening_hours")));
			  hm.put("photos", formatDetailsPhotos((JSONArray) resultObj.get("photos")));
			  hm.put("reviews", formatDetailsReviews((JSONArray) resultObj.get("reviews")));
			  
		  }
		  
        }
        
        return hmlist;
        
      } 
    
    
    
    
    ArrayList<HashMap<String,Object>>  parseJsonPhotosResponse(String jsonString){

		ArrayList<HashMap<String,Object>> hmlist = new ArrayList<HashMap<String,Object>>();
		JSONParser parser= new JSONParser();
		JSONObject obj = null;
        if(jsonString != null){
		  try {
			obj = (JSONObject) parser.parse(jsonString);
		  } catch (ParseException e) {
			Log.e("parseJsonSearchResponse", e.getMessage());
		  }
		  
		  
		  
		  
        }
        
        return hmlist;
      	
      	
      }     
    
    
    
    private String formatDetailsHours(JSONObject jsonObj){
    	
    	StringBuilder sb = new StringBuilder();
    	
    	Boolean opened = (Boolean) jsonObj.get("open_now");
    	String businessState = opened ? "Opened Now": "Closed Now";
    	sb.append(businessState + "\n");
    	
    	JSONArray periods = (JSONArray) jsonObj.get("periods");
    	Iterator iter = periods.iterator();
    	while(iter.hasNext()){
    		JSONObject dayOfWeek = (JSONObject) iter.next();
    		int dayIndex = (Integer) ((JSONObject) dayOfWeek.get("open")).get("day");
    		String open = (String) ((JSONObject) dayOfWeek.get("open")).get("time");
    		String openStr = Integer.valueOf(open.substring(0,2)) > 12 ? (String.valueOf(Integer.valueOf(open.substring(0,2)) - 12) + ":" + open.substring(2,2)) : (open.substring(0,2) + ":" + open.substring(2,2));
    		String close = (String) ((JSONObject) dayOfWeek.get("close")).get("time");
    		String closeStr = Integer.valueOf(close.substring(0,2)) > 12 ? (String.valueOf(Integer.valueOf(close.substring(0,2)) - 12) + ":" + close.substring(2,2)) : (close.substring(0,2) + ":" + close.substring(2,2));
    		sb.append(String.format("%s: %s - %s\n", PlacesClient.days[dayIndex], openStr, closeStr));
    	}
    	
    	return sb.toString();
    }
    
    
    private ArrayList<HashMap<String,Object>> formatDetailsPhotos(JSONArray jsonArr){
    	
    	ArrayList<HashMap<String,Object>> hmlist = new ArrayList<HashMap<String,Object>>();
    	Iterator iter = jsonArr.iterator();
    	while(iter.hasNext()){
    		JSONObject photos = (JSONObject) iter.next();
    		HashMap<String,Object> hm = new HashMap<String,Object>();
    		hm.put("photo_reference", (String) photos.get("photo_reference"));
    		hm.put("width", (Integer) photos.get("width"));
    		hm.put("height", (Integer) photos.get("height"));
    		hmlist.add(hm);
    	}
    	return hmlist;
    }
    
    private ArrayList<HashMap<String,Object>> formatDetailsReviews(JSONArray jsonArr){
    	
    	ArrayList<HashMap<String,Object>> hmlist = new ArrayList<HashMap<String,Object>>();
    	Iterator iter = jsonArr.iterator();
    	while(iter.hasNext()){
    		JSONObject reviews = (JSONObject) iter.next();
    		if ( ((String) ((JSONObject) ((JSONArray) reviews.get("aspects")).get(0)).get("type")).equals("quality") ){
    		   HashMap<String,Object> hm = new HashMap<String,Object>();
    		   for(int i = 0; i < PlacesClient.reviewsDataFields.length; i++){
    			   hm.put(PlacesClient.reviewsDataFields[i], (String) reviews.get(PlacesClient.reviewsDataFields[i]));
    		   }
    		   hmlist.add(hm);
    		}
    	}
    	return hmlist;    	
    	
    }
    
    
}
