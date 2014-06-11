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
import java.util.concurrent.ExecutionException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

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
	
	
	  public FactualClient(double latitude, double longitude, int meterPerimeter){
		  
		    this.latitude = latitude;
		    this.longitude = longitude;
		    this.meterPerimeter = meterPerimeter;
	        this.key = "iaDJkLcnsCp7uvSe025F7vYR39eTzAk7uMFalBaq";
		    this.secret = "gkvlBEWzFWYJBnE1I9ZfAkcQWkAaAmCGWVGU7ojo";
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
	     String requestParams = String.format("filters={\"$and\":[{\"category_ids\":%d}]}&geo={\"$circle\":{\"$center\":[%f,%f],\"$meters\":%d}}", Integer.valueOf(categoryId), this.latitude, this.longitude, this.meterPerimeter);
	     String normalizedParams = requestParams + String.format("&oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=HMAC-SHA1&oauth_timestamp=%s&oauth_version=1.0", this.key, this.nonce, this.timestamp);
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
	         
		     conn.connect();
	         int response = conn.getResponseCode();
	         if (response == 200){
	              is = conn.getInputStream();
	         }
	         else{
	        	 is = conn.getErrorStream();
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
	        protected ArrayList<HashMap<String,String>> doInBackground(String... urls) {
	              
	            try {
	                String factualResult = callFactual(urls[0]);
	                return FactualQueryParser.parseJsonResponse(factualResult);
	            } catch (IOException e) {
	                
	                return null;
	            }
	        }
	        	        
	        // onPostExecute displays the results of the AsyncTask.
	        @Override
	        protected void onPostExecute(ArrayList<HashMap<String,String>> result) {
                  //No UI to update      	
	        	
	       }
	    }

	

}
