package com.dl2974.whatsaround;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

import android.util.Log;

public class YelpClient {
	
	
	  private static final String API_HOST = "api.yelp.com";
	  private static final String SEARCH_PATH = "/v2/search";
	  private static final String BUSINESS_PATH = "/v2/business";
	  private static final String CONSUMER_KEY = "ZnBaqz0hhds9bN5TDQdgPA";
	  private static final String CONSUMER_SECRET = "rzoub7Yc8qyz_rgiaAExpZ7MAFw";
	  private static final String TOKEN = "hMFVvWuj1xv7xFgIDo6jwulGOwnwPsJ-";
	  private static final String TOKEN_SECRET = "AXKJAGszEXlLvsJT-C_CvZ-LMu4";
	
	  OAuthService service;
	  Token accessToken;
	

	  public YelpClient(){
		  
		  this.service = new ServiceBuilder().provider(OAuthServiceYelp.class).apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET).build();
		  this.accessToken = new Token(TOKEN, TOKEN_SECRET);
		  
	  }
	  
	  
	  public String search(double latitude, double longitude, int meterPerimeter){
		  
		  OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + SEARCH_PATH);
		  request.addQuerystringParameter("radius_filter", String.valueOf(meterPerimeter));
		  request.addQuerystringParameter("ll", String.format("%f,%f", latitude, longitude));
		  this.service.signRequest(this.accessToken, request);
		  Response response = request.send();
		  
		  return response.getBody();
	  }
	  
	  
	  public String business(String businessId){
		  
		  OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + BUSINESS_PATH + "/" + businessId);
		  this.service.signRequest(this.accessToken, request);
		  Response response = request.send();
		  
		  return response.getBody();
	  }
	  
	  //Pass Yelp Search JSON response to match Factual name and return business ID
	  public static String matchLocation(String jsonStr, String locationName){
		  
		    String businessId = null;
		    JSONParser parser = new JSONParser();
		    JSONObject obj = null;
			try {
				obj = (JSONObject) parser.parse(jsonStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		    JSONArray businesses = (JSONArray) obj.get("businesses");
		    Iterator i = businesses.iterator();
		    while (i.hasNext()) {
		    	JSONObject bObj = (JSONObject) i.next();
		    	Log.i("YELP", "matchLocation " + bObj.get("name").toString() );
		    	if (bObj.get("name").equals(locationName)){
		    		businessId = bObj.get("id").toString();
		    		return businessId;
		    	}
		    }
		    
		    return businessId;
		  
	  }
	  
	  public static ArrayList<HashMap<String,String>> getLocationReviews(String jsonStr){
		  
		    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();	  
		    JSONParser parser = new JSONParser();
		    JSONObject obj = null;
			try {
				obj = (JSONObject) parser.parse(jsonStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}		  
		    JSONArray reviews = (JSONArray) obj.get("reviews");
		    Iterator i = reviews.iterator();
		    while (i.hasNext()) {
		    	HashMap<String,String> hm = new HashMap<String,String>();
		    	JSONObject reviewObj = (JSONObject) i.next();
		    	hm.put("rating", reviewObj.get("rating").toString() );
		    	hm.put("excerpt", reviewObj.get("excerpt").toString() );
		    	hm.put("user", ((JSONObject)  reviewObj.get("user")).get("name").toString() );
		    	list.add(hm);
		    }
		    
		    return list;
	  }
	  
	  
}
