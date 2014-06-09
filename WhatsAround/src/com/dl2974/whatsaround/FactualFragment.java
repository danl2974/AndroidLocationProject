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
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.dl2974.whatsaround.LocationsListFragment.OnLocationTypeSelectedListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class FactualFragment extends ListFragment implements
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener  {

    public interface OnLocationSelectedListener {
        
        public void onLocationSelected(HashMap<String,String> locationMap);
    }
    
    public interface OnUserLocationChange {
    	
    	public void updateUserLocation(Location location);
    	
    }

    OnLocationSelectedListener selectionCallback;
    OnUserLocationChange iUserLocation;
	
    private static final String DEBUG_TAG = "WhatsAround";
    private EditText urlText;
    private TextView textView;
    private ImageView imgView;

    private String key;
    private String secret;
    private String nonce;
    private String timestamp;
    private String endpoint = "http://api.v3.factual.com/t/places";
    private static final int RESTAURANT_CAT = 347;
    private static final SecureRandom RANDOM = new SecureRandom();

    private ArrayList<String> locations;
    
    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    
    private TextView mLatLng;
    private TextView mAddress;
    private ProgressBar mActivityIndicator;
    private TextView mConnectionState;
    private TextView mConnectionStatus;

    SharedPreferences mPrefs;

    SharedPreferences.Editor mEditor;
    boolean mUpdatesRequested = false;
    
	private double latitude;
	private double longitude;
	
	private ProgressDialog dialog;
	
	private int factualLocationCategory;
	private ArrayList<HashMap<String,String>> foundLocationsList;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.http_test);   
        //urlText = (EditText) findViewById(R.id.myUrl);
        //textView = (TextView) findViewById(R.id.myText);
        
        //setContentView(R.layout.listview);
        
        this.dialog = new ProgressDialog(getActivity());
        this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
        
        
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

        this.key = "iaDJkLcnsCp7uvSe025F7vYR39eTzAk7uMFalBaq";
	    this.secret = "gkvlBEWzFWYJBnE1I9ZfAkcQWkAaAmCGWVGU7ojo";
	    this.nonce = computeNonce();
	    this.timestamp = computeTimestamp();
	    
	    this.mLocationRequest = LocationRequest.create();
	    this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	    this.mLocationClient = new LocationClient(getActivity(), this, this);
	    //this.mLocationClient.connect();
	    
        //new FactualClientTask().execute("347");
        }
        

    }
    
    @Override
    public void onStart() {

        super.onStart();
	    this.mLocationClient.connect();
        


    }


    public void myClickHandler(View view) {
        
        String categoryId = urlText.getText().toString();
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

        this.key = "iaDJkLcnsCp7uvSe025F7vYR39eTzAk7uMFalBaq";
	    this.secret = "gkvlBEWzFWYJBnE1I9ZfAkcQWkAaAmCGWVGU7ojo";
	    this.nonce = computeNonce();
	    this.timestamp = computeTimestamp();
            new FactualClientTask().execute(categoryId);

        } else {
            textView.setText("No network connection available.");
        }
    }


     public class FactualClientTask extends AsyncTask<String, Void, String> {
    	 
        @Override
        protected String doInBackground(String... urls) {
              
            try {
                return callFactual(urls[0]);
            } catch (IOException e) {
                
                return null;
            }
        }
        
        @Override
        protected void onPreExecute() {
        	FactualFragment.this.dialog.setMessage("Getting Location Information");
        	FactualFragment.this.dialog.show();
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if (FactualFragment.this.dialog.isShowing()) {
            	FactualFragment.this.dialog.dismiss();
            }   
        	ArrayList<HashMap<String,String>> dlist = FactualQueryParser.parseJsonResponse(result);
        	FactualFragment.this.foundLocationsList = dlist;
        	/*
        	ArrayList<String> locationsList = new ArrayList<String>();
        	for (HashMap<String,String> dhm: dlist){ 
        		StringBuilder sb = new StringBuilder();
        		for (Map.Entry<String,String> entry: dhm.entrySet()){  
        			
        		    sb.append(entry.getKey() + ": " + entry.getValue());
        		    sb.append("\n");
        		}
        		locationsList.add(sb.toString());
        	}
        	
            setListAdapter(new ArrayAdapter<String>(WhatsAround.this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    locationsList));
            */
            setListAdapter(new FactualArrayAdapter(getActivity(), dlist));       	
            //textView.setText(sb.toString());
            
        	//imgView.setImageBitmap(result);
        	//File png = getFileStreamPath(result);
        	//Uri uriToImage = Uri.fromFile(png);
        	//Log.i(DEBUG_TAG, "Uri Image: " + String.valueOf(uriToImage) );
        	//imgView.setImageURI(uriToImage);
        	
        	
        	
       }
    }

     

  private String callFactual(String categoryId) throws IOException {
	  /*
	  Location currentLocation = null;
	  try{
	  if (servicesConnected()) {
		  Log.i("WHATSAROUND", "inside if block");
          currentLocation = mLocationClient.getLastLocation();
	  }
	 Log.i("WHATSAROUND", "before lang lat assign");
	 double latitude = currentLocation.getLatitude();
	 double longitude = currentLocation.getLongitude();
	 Log.i("WHATSAROUND", String.valueOf(latitude));
	  }catch(Exception e){Log.d("WHATSAROUND", "exception " + e.getMessage());}
     //double latitude = 26.303359;
     //double longitude = -80.122905;
      */
     int meters = 20000;
     String requestParams = String.format("filters={\"$and\":[{\"category_ids\":%d}]}&geo={\"$circle\":{\"$center\":[%f,%f],\"$meters\":%d}}", Integer.valueOf(categoryId), latitude, longitude, meters);
     String normalizedParams = requestParams + String.format("&oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=HMAC-SHA1&oauth_timestamp=%s&oauth_version=1.0", this.key, this.nonce, this.timestamp);
     String json = doConnection(normalizedParams, requestParams);
     //String json = doConnection(requestParams);
     return json;

  }
  



		private String doConnection(String requestpath, String requestParams){

			String factual = "";
			HttpURLConnection conn = null;
			InputStream is = null;
			try{
			 String urlString = this.endpoint + "?" + requestParams;
			 //String urlString = "http://c.sweepstakes-web.com" + "?" + requestParams;
		     URL url = new URL(urlString);
		     Log.i("FactualClient", urlString);
	         conn = (HttpURLConnection) url.openConnection();
	         //conn.setReadTimeout(10000);
	         //conn.setConnectTimeout(15000);
	         conn.setRequestMethod("GET");
	         conn.setDoInput(true);
	         
	         
	         conn.setRequestProperty("Authorization",  createAuthHeader("GET&" + "&"+URLEncoder.encode(this.endpoint, "UTF-8") + "&"+URLEncoder.encode(requestpath, "UTF-8") ) );
	         //conn.setRequestProperty("Host", "api.v3.factual.com");
	         //conn.setRequestProperty("X-Target-URI", "http://api.v3.factual.com");
	         //conn.setRequestProperty("Connection", "Keep-Alive");
	         
		     conn.connect();
	         int response = conn.getResponseCode();
	         Log.i("FactualClient", "The response is: " + String.valueOf(response));
	         if (response == 200){
	              is = conn.getInputStream();
	         }
	         else{
	        	 is = conn.getErrorStream();
	         }
	         //Log.i("FactualClient", "Input Stream bytes available: " + String.valueOf(is.available()) );
	         BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	         StringBuilder sb =  new StringBuilder();
	         String sLine = "";
	         int i;
	         char c;

	     	 while ((sLine = reader.readLine()) != null) {
	     		sb.append(sLine);
	     	 }
	     	 /*
	         while((i = reader.read()) != -1){
	        	   c = (char) i;
		    	   sb.append(c);
		    	   Log.i("FactualClient", sb.toString());
	             }
	         */
	         factual = sb.toString();
	         Log.i("FactualClient",  sb.toString() );
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


		@Override
		public void onConnected(Bundle bundle) {
		        //mConnectionStatus.setText(R.string.connected);
			    //startPeriodicUpdates();
			    Log.i("whatsaround", "inside onConnected");
			    mLocationClient.requestLocationUpdates(mLocationRequest, this);
			    /*
			    try{
			    Location currentLocation = this.mLocationClient.getLastLocation();
				this.latitude = currentLocation.getLatitude();
				this.longitude = currentLocation.getLongitude();
			    new FactualClientTask().execute("347");
			    }catch(Exception e){Log.i("whatsaround", " " + e.getMessage() );}
                */
			    
			    /*
		        if (mUpdatesRequested) {
		            startPeriodicUpdates();
		        }
		        */
		    }

		    /*
		     * Called by Location Services if the connection to the
		     * location client drops because of an error.
		     */
		    @Override
		    public void onDisconnected() {
		        //mConnectionStatus.setText(R.string.disconnected);
		    }



		 @Override
		    public void onConnectionFailed(ConnectionResult connectionResult) {

		        /*
		         * Google Play services can resolve some errors it detects.
		         * If the error has a resolution, try sending an Intent to
		         * start a Google Play services activity that can resolve
		         * error.
		         */
		        if (connectionResult.hasResolution()) {
		            try {

		                // Start an Activity that tries to resolve the error
		                connectionResult.startResolutionForResult(
		                		getActivity(),
		                        9000);

		                /*
		                * Thrown if Google Play services canceled the original
		                * PendingIntent
		                */

		            } catch (IntentSender.SendIntentException e) {

		                // Log the error
		                e.printStackTrace();
		            }
		        } else {

		            // If no resolution is available, display a dialog to the user with the error.
		            showErrorDialog(connectionResult.getErrorCode());
		        }
		    }




		    @Override
		    public void onLocationChanged(Location location) {
		    	
		    	Log.i("WHATSAROUND", "inside onLocationChanged");
		    	longitude = location.getLongitude();
		        latitude = location.getLatitude();
		        iUserLocation.updateUserLocation(location);
		        //String factualLocationCategory = "347"; // Restaurants
		        Log.i("WHATSAROUND long lat", String.format("%f %f", longitude, latitude));
		   	    //int min = 2;
			    //int max = 440;
			    Random rand = new Random();
			    int[] category_arr = {2, 62, 123, 149, 177, 308, 312, 372, 415, 430};
			    //String factualLocationCategory = String.valueOf(rand.nextInt(max - min) + min);
			    //String factualLocationCategory = String.valueOf(category_arr[rand.nextInt(category_arr.length - 1) + 0]);
			    //Log.i("WHATSAROUND Factual Category", factualLocationCategory);
		        new FactualClientTask().execute(String.valueOf(factualLocationCategory));
		        
		        // Report to the UI that the location was updated
		        //mConnectionStatus.setText(R.string.location_updated);

		        // In the UI, set the latitude and longitude to the value received
		        //mLatLng.setText(LocationUtils.getLatLng(this, location));
		    }


		    @Override
		    public void onStop() {

		        // If the client is connected
		        if (mLocationClient.isConnected()) {
		            stopPeriodicUpdates();
		        }

		        // After disconnect() is called, the client is considered "dead".
		        mLocationClient.disconnect();

		        super.onStop();
		    }
		    /*
		     * Called when the Activity is going into the background.
		     * Parts of the UI may be visible, but the Activity is inactive.
		     */
		    @Override
		    public void onPause() {

		        // Save the current setting for updates
		        //mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
		        //mEditor.commit();

		        super.onPause();
		    }
		    
		    @Override
		    public void onResume() {
		        super.onResume();
                /*
		        // If the app already has a setting for getting location updates, get it
		        if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
		            mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

		        // Otherwise, turn off location updates until requested
		        } else {
		            mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
		            mEditor.commit();
		        }
		        */

		    }

		    private void startPeriodicUpdates() {
          
		        mLocationClient.requestLocationUpdates(mLocationRequest, this);
		        Log.i("WHATSAROUND", "startPeriodicUpdates()");
		        //mConnectionState.setText(R.string.location_requested);
		    }
		    
		    private void stopPeriodicUpdates() {
		        //mLocationClient.removeLocationUpdates(this);
		        //mConnectionState.setText(R.string.location_updates_stopped);
		    }
		    
		    private void showErrorDialog(int errorCode) {

		        // Get the error dialog from Google Play services
		        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
		            errorCode,
		            getActivity(),
		            9000);

		        // If Google Play services can provide an error dialog
		        if (errorDialog != null) {
                    /*
		            // Create a new DialogFragment in which to show the error dialog
		            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

		            // Set the dialog in the DialogFragment
		            errorFragment.setDialog(errorDialog);

		            // Show the error dialog in the DialogFragment
		            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
		            */
		        }
		    }
		    
		    
		    private boolean servicesConnected() {

		        // Check that Google Play services is available
		        int resultCode =
		                GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		        Log.i("WHATSAROUND", "result code: " + String.valueOf(resultCode));
		        // If Google Play services is available
		        if (ConnectionResult.SUCCESS == resultCode) {
		            // In debug mode, log the status
		            Log.d("WhatsAroundApp", "Google Play services is available");

		            // Continue
		            return true;
		        // Google Play services was not available for some reason
		        } else {
		            // Display an error dialog
		            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 0);
		            if (dialog != null) {
		                //ErrorDialogFragment errorFragment = new ErrorDialogFragment();
		                //errorFragment.setDialog(dialog);
		                //errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
		            }
		            return false;
		        }
		    }



		    public void getLocation(View v) {

		        // If Google Play Services is available
		        if (servicesConnected()) {

		            // Get the current location
		            Location currentLocation = mLocationClient.getLastLocation();

		            // Display the current location in the UI
		            //mLatLng.setText(LocationUtils.getLatLng(this, currentLocation));
		        }
		    }
		    /*
		    @Override
		    public void onListItemClick(ListView l, View v, int position, long id) {
		      HashMap<String,String> item = (HashMap<String,String>) getListAdapter().getItem(position);
		      Toast.makeText(getActivity(), item.get("name"), Toast.LENGTH_LONG).show();
		    } 
		    */
		    @Override
		    public void onAttach(Activity activity) {
		        super.onAttach(activity);

		        // This makes sure that the container activity has implemented
		        // the callback interface. If not, it throws an exception.
		        try {
		        	selectionCallback = (OnLocationSelectedListener) activity;
		        } catch (ClassCastException e) {
		            throw new ClassCastException(activity.toString()
		                    + " must implement OnLocationSelectedListener");
		        }
		        
		        try{
		        	iUserLocation = (OnUserLocationChange) activity;
		        }
		        catch(ClassCastException cce){
		        	 throw new ClassCastException(activity.toString()
			                    + " must implement OnUserLocationChange");
		        }
		        
		        
		    }
		    
		    @Override
		    public void onListItemClick(ListView l, View v, int position, long id) {
		        // Notify the parent activity of selected item
		    	Log.i("WHATSAROUND", "onListItemClick HM size check " + String.valueOf(foundLocationsList.size()));
		    	Log.i("WHATSAROUND", "onListItemClick position " + String.valueOf(position));
		    	HashMap<String,String> selectedLocationMap = foundLocationsList.get(position);
		    	selectionCallback.onLocationSelected(selectedLocationMap);
		        getListView().setItemChecked(position, true);
		        
		    }  
		
		    
		    public void setCategoryId(int categoryId){
		    	this.factualLocationCategory = categoryId;
		    }
}