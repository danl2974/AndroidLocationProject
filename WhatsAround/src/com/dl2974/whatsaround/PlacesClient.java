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
	
	public ArrayList<HashMap<String,String>> getPlacesData(){
		
		ArrayList<HashMap<String,String>> result = null;
		AsyncTask<String, Void, ArrayList<HashMap<String,String>>> task = new PlacesClientTask().execute(this.requestParameters);
		   
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
	
	
	
    public class PlacesClientTask extends AsyncTask<String, Void, ArrayList<HashMap<String,String>> > {
	    	 
	        @Override
	        protected ArrayList<HashMap<String,String>> doInBackground(String... urlParams) {
	              
	        try {
	        	
                    String placesResult = call(PlacesClient.this.endpoint, urlParams[0]);
                    ArrayList<HashMap<String,String>> response = null;
                    
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
	        protected void onPostExecute(ArrayList<HashMap<String,String>> result) {
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
    
    
    ArrayList<HashMap<String,String>>  parseJsonSearchResponse(String jsonString){
    	
		ArrayList<HashMap<String,String>> hmlist = new ArrayList<HashMap<String,String>>();
		JSONParser parser= new JSONParser();
		JSONObject obj = null;
        if(jsonString != null){
		  try {
			obj = (JSONObject) parser.parse(jsonString);
		  } catch (ParseException e) {
			Log.e("parseJsonResponse", e.getMessage());
		  }
        
		  JSONObject responseObj = (JSONObject) obj.get("response");
		  //JSONObject dataObj = (JSONObject) responseObj.get("data");
		  //Set<String> keys = dataObj.keySet();
		  JSONArray dataArray = (JSONArray) responseObj.get("data");
		  Iterator i = dataArray.iterator();

		  while (i.hasNext()) {
			  HashMap<String,String> hm = new HashMap<String,String>();
			  JSONObject dataitem = (JSONObject) i.next();
			  for(int j = 0; j < FactualLocation.StringFields.length; j++){
				if (dataitem.get(FactualLocation.StringFields[j]) != null){ 
				  Log.i("Parser",  FactualLocation.StringFields[j] + (String) dataitem.get(FactualLocation.StringFields[j]) );
			      hm.put(FactualLocation.StringFields[j], (String) dataitem.get(FactualLocation.StringFields[j]));
				}else{Log.i("NullParser",  FactualLocation.StringFields[j]);}
			  }
			  
			  for(int j = 0; j < FactualLocation.DoubleFields.length; j++){
				if (dataitem.get(FactualLocation.DoubleFields[j]) != null){ 
				  //Log.i("Parser",  FactualLocation.DoubleFields[j] + (String) dataitem.get(FactualLocation.DoubleFields[j]) );
			      hm.put(FactualLocation.DoubleFields[j], String.valueOf(dataitem.get(FactualLocation.DoubleFields[j]))  );
				}else{Log.i("NullParser",  FactualLocation.DoubleFields[j]);}
			  }			  
			  
			  hmlist.add(hm);
		  }
        
        }
		return hmlist;    	
    	
    	
    } 
    
    
    
    ArrayList<HashMap<String,String>>  parseJsonDetailsResponse(String jsonString){
    	
  		ArrayList<HashMap<String,String>> hmlist = new ArrayList<HashMap<String,String>>();
  		JSONParser parser= new JSONParser();
  		JSONObject obj = null;
          if(jsonString != null){
  		  try {
  			obj = (JSONObject) parser.parse(jsonString);
  		  } catch (ParseException e) {
  			Log.e("parseJsonResponse", e.getMessage());
  		  }
          
  		  JSONObject responseObj = (JSONObject) obj.get("response");
  		  //JSONObject dataObj = (JSONObject) responseObj.get("data");
  		  //Set<String> keys = dataObj.keySet();
  		  JSONArray dataArray = (JSONArray) responseObj.get("data");
  		  Iterator i = dataArray.iterator();

  		  while (i.hasNext()) {
  			  HashMap<String,String> hm = new HashMap<String,String>();
  			  JSONObject dataitem = (JSONObject) i.next();
  			  for(int j = 0; j < FactualLocation.StringFields.length; j++){
  				if (dataitem.get(FactualLocation.StringFields[j]) != null){ 
  				  Log.i("Parser",  FactualLocation.StringFields[j] + (String) dataitem.get(FactualLocation.StringFields[j]) );
  			      hm.put(FactualLocation.StringFields[j], (String) dataitem.get(FactualLocation.StringFields[j]));
  				}else{Log.i("NullParser",  FactualLocation.StringFields[j]);}
  			  }
  			  
  			  for(int j = 0; j < FactualLocation.DoubleFields.length; j++){
  				if (dataitem.get(FactualLocation.DoubleFields[j]) != null){ 
  				  //Log.i("Parser",  FactualLocation.DoubleFields[j] + (String) dataitem.get(FactualLocation.DoubleFields[j]) );
  			      hm.put(FactualLocation.DoubleFields[j], String.valueOf(dataitem.get(FactualLocation.DoubleFields[j]))  );
  				}else{Log.i("NullParser",  FactualLocation.DoubleFields[j]);}
  			  }			  
  			  
  			  hmlist.add(hm);
  		  }
          
          }
  		return hmlist;    	
      	
      	
      } 
    
    
    
    
    ArrayList<HashMap<String,String>>  parseJsonPhotosResponse(String jsonString){
    	
  		ArrayList<HashMap<String,String>> hmlist = new ArrayList<HashMap<String,String>>();
  		JSONParser parser= new JSONParser();
  		JSONObject obj = null;
          if(jsonString != null){
  		  try {
  			obj = (JSONObject) parser.parse(jsonString);
  		  } catch (ParseException e) {
  			Log.e("parseJsonResponse", e.getMessage());
  		  }
          
  		  JSONObject responseObj = (JSONObject) obj.get("response");
  		  //JSONObject dataObj = (JSONObject) responseObj.get("data");
  		  //Set<String> keys = dataObj.keySet();
  		  JSONArray dataArray = (JSONArray) responseObj.get("data");
  		  Iterator i = dataArray.iterator();

  		  while (i.hasNext()) {
  			  HashMap<String,String> hm = new HashMap<String,String>();
  			  JSONObject dataitem = (JSONObject) i.next();
  			  for(int j = 0; j < FactualLocation.StringFields.length; j++){
  				if (dataitem.get(FactualLocation.StringFields[j]) != null){ 
  				  Log.i("Parser",  FactualLocation.StringFields[j] + (String) dataitem.get(FactualLocation.StringFields[j]) );
  			      hm.put(FactualLocation.StringFields[j], (String) dataitem.get(FactualLocation.StringFields[j]));
  				}else{Log.i("NullParser",  FactualLocation.StringFields[j]);}
  			  }
  			  
  			  for(int j = 0; j < FactualLocation.DoubleFields.length; j++){
  				if (dataitem.get(FactualLocation.DoubleFields[j]) != null){ 
  				  //Log.i("Parser",  FactualLocation.DoubleFields[j] + (String) dataitem.get(FactualLocation.DoubleFields[j]) );
  			      hm.put(FactualLocation.DoubleFields[j], String.valueOf(dataitem.get(FactualLocation.DoubleFields[j]))  );
  				}else{Log.i("NullParser",  FactualLocation.DoubleFields[j]);}
  			  }			  
  			  
  			  hmlist.add(hm);
  		  }
          
          }
  		return hmlist;    	
      	
      	
      }     
    
    
    
}
