package com.dl2974.whatsaround;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONObject;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.factual.driver.Circle;
import com.factual.driver.Factual;
import com.factual.driver.Query;
import com.factual.driver.ReadResponse;
import com.factual.driver.Response;
import com.google.api.client.util.escape.PercentEscaper;

public class FactualClient {
	
      private String key;
      private String secret;
      private String nonce;
      private String timestamp;
  	  private double latitude;
  	  private double longitude;
  	  private int meterPerimeter = 20000;
      private String endpoint = "http://api.v3.factual.com/t/places";
      private static final SecureRandom RANDOM = new SecureRandom();
      private static final PercentEscaper ESCAPER = new PercentEscaper("-_.~", false);
	
	
	  public FactualClient(double latitude, double longitude, int meterPerimeter){
		  
		    this.latitude = latitude;
		    this.longitude = longitude;
		    this.meterPerimeter = meterPerimeter;
	        this.key = "ycoxfYkfS1kcS42ALb3qYPjGAip3Ui3BHodh5VQo";
		    this.secret = "lADnpfEdLNuq2eEFoSpfxszuG04wDlPUNRwxSQ9I";
		    this.nonce = computeNonce();
		    this.timestamp = computeTimestamp();
	  }
	  
	  public ArrayList<HashMap<String,String>> getLocationsByCategory(int factualLocationCategory){
		  
		  ArrayList<HashMap<String,String>> result = null;
		  AsyncTask<String, Void, ArrayList<HashMap<String,String>>> task = new FactualClientTask().execute(String.valueOf(factualLocationCategory));
		   
		  try {
			 result = task.get();
		} catch (InterruptedException e) {
			Log.e(this.getClass().getName(), e.getMessage());
		} catch (ExecutionException e) {
			Log.e(this.getClass().getName(), e.getMessage());
		}
		  
		  return result;
	  }
	
	
	  private String callFactual(String categoryId) throws IOException {

	     int meters = 20000;
	     String requestParams = escape( String.format("filters={\"$and\":[{\"category_ids\":%d}]}&geo={\"$circle\":{\"$center\":[%f,%f],\"$meters\":%d}}", Integer.valueOf(categoryId), this.latitude, this.longitude, this.meterPerimeter) );
	     String normalizedParams = requestParams + escape( String.format("&oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=HMAC-SHA1&oauth_timestamp=%s&oauth_version=1.0", this.key, this.nonce, this.timestamp) );
	     String json = doConnection(normalizedParams, requestParams);
	     return json;

	  }
	  
	  private String doConnection(String requestpath, String requestParams){

			String factual = "";
			HttpURLConnection conn = null;
			InputStream is = null;
			try{
			 String urlString = this.endpoint + "?" + requestParams;
		     URL url = new URL(urlString);
	         conn = (HttpURLConnection) url.openConnection();
	         //conn.setReadTimeout(10000);
	         //conn.setConnectTimeout(15000);
	         conn.setRequestMethod("GET");
	         conn.setDoInput(true);
	         conn.setRequestProperty("Authorization",  createAuthHeader("GET&" + "&"+URLEncoder.encode(this.endpoint, "UTF-8") + "&"+URLEncoder.encode(requestpath, "UTF-8") ) );
	         conn.setRequestProperty("X-Target-URI", "http://api.v3.factual.com");
	         conn.setRequestProperty("Connection", "Keep-Alive");
             for (Map.Entry<String,List<String>> hf : conn.getRequestProperties().entrySet()){
           	  Log.i("FactualClientRequestHeader", String.format("%s %s", hf.getKey(), hf.getValue().get(0) ) );
             }
		     conn.connect();
	         int response = conn.getResponseCode();
	         if (response == 200){
	              is = conn.getInputStream();
	              for (Map.Entry<String,List<String>> hf : conn.getHeaderFields().entrySet()){
	            	  Log.i("FactualClientResponseHeader", String.format("%s %s", hf.getKey(), hf.getValue().get(0) ) );
	              }
	         }
	         else{
	        	 is = conn.getErrorStream();
	              for (Map.Entry<String,List<String>> hf : conn.getHeaderFields().entrySet()){
	            	  Log.i("FactualClientResponseHeader", String.format("%s %s", hf.getKey(), hf.getValue().get(0) ) );
	              }	        	 
	         }
	         BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	         StringBuilder sb =  new StringBuilder();
	         String sLine = "";

	     	 while ((sLine = reader.readLine()) != null) {
	     		sb.append(sLine);
	     	 }
	         factual = sb.toString();
	         
			}
			catch(Exception e){
				Log.e("FactualClientException", "Exception doConnection " + e.getMessage());
			}
			finally {
			    conn.disconnect();
			   }
			
			return factual;
	  
	   } 
		
		/*
		private String createAuthHeader(String baseString){
			

			StringBuilder auth = new StringBuilder("OAuth ");
			auth.append("oauth_version=\"1.0\",");
			auth.append( String.format("oauth_consumer_key=\"%s\",", this.key) );
			auth.append( String.format("oauth_timestamp=\"%s\",", this.timestamp) );
			auth.append( String.format("oauth_nonce=\"%s\",", this.nonce) );
			auth.append("oauth_signature_method=\"HMAC-SHA1\",");
			auth.append( String.format("oauth_signature=\"%s", computeSignature(baseString)) );
			
			Log.i("FactualClient", auth.toString());
			return auth.toString();
		}
		*/
		private String createAuthHeader(String baseString){
			

			StringBuilder auth = new StringBuilder("OAuth ");
			auth.append( String.format("oauth_version=\"%s\",", "1.0" ));
			auth.append( String.format("oauth_consumer_key=\"%s\",", this.key ) );
			auth.append( String.format("oauth_nonce=\"%s\",", this.nonce ) );
			auth.append( String.format("oauth_signature_method=\"%s\",",  "HMAC-SHA1" ) );
			auth.append( String.format("oauth_signature=\"%s\",", escape(computeSignature(baseString))  ) );
			auth.append( String.format("oauth_timestamp=\"%s\"", this.timestamp ) );
			
			return auth.toString();
		}		
		
		
		private String computeSignature(String signatureBaseString){
			
			String signature = "";
			try{
		      SecretKey secretKey = new SecretKeySpec(this.secret.getBytes("UTF-8"), "HmacSHA1");
		      Mac mac = Mac.getInstance("HmacSHA1");
		      mac.init(secretKey);
		      signature = Base64.encodeToString(mac.doFinal(signatureBaseString.getBytes("UTF-8")), Base64.DEFAULT);
			}
			catch(Exception e){Log.d("FactualClientException", e.getMessage());}
			
			return signature;
		}
		
		
		public String computeNonce() {
			    return Long.toHexString(Math.abs(RANDOM.nextLong()));
			  }


		public String computeTimestamp() {
			    return Long.toString(System.currentTimeMillis() / 1000);
			  } 
	
	
	  public class FactualClientTask extends AsyncTask<String, Void, ArrayList<HashMap<String,String>> > {
	    	 
	        @Override
	        protected ArrayList<HashMap<String,String>> doInBackground(String... categoryId) {
	              
	            try {
	            	
	            	//USING FACTUAL DRIVER LIB
	            	Factual factual = new Factual(FactualClient.this.key, FactualClient.this.secret, true);
	            	Query q = new Query().within(new Circle(FactualClient.this.latitude, FactualClient.this.longitude, FactualClient.this.meterPerimeter));
	            	ReadResponse rr = factual.fetch("places", q.field("category_ids").isEqual(categoryId[0]));
	            	String factualResult = rr.getJson();
	            	
	            	try{
	      		    YelpClient yc = new YelpClient();
	      		    String searchResults = yc.search(FactualClient.this.latitude, FactualClient.this.longitude, FactualClient.this.meterPerimeter);
	      		    Log.i("YELP", searchResults );
	      		    String businessId = YelpClient.matchLocation(searchResults, "The Stanford Theatre");
	      		    if (businessId != null){
	      		      String businessJson = yc.business(businessId);
	      		      Log.i("YELP", businessId + businessJson );
	      		      ArrayList<HashMap<String,String>> reviews = YelpClient.getLocationReviews(businessJson);
	      		      for (HashMap<String,String> r : reviews){
	      		    	     Log.i("YELP", String.format("%s %s %s", r.get("rating"), r.get("user"), r.get("excerpt") ) );
	      		    	  }
	      		     }
	      		   
	            	}catch(Exception ex){Log.i("YELP", "exception");}
	            	
	            	//TURNED OFF own Implementation
	                //String factualResult = callFactual(categoryId[0]);
	                return FactualQueryParser.parseJsonResponse(factualResult);
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

	
	  public static String escape(String value) {
		    return ESCAPER.escape(value);
		  }

}
