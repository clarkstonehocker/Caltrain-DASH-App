package com.seniorproject.caltraindashmobileapp;

import java.util.ArrayList;
import java.util.Collections;

import com.seniorproject.caltraindashmobileapp.R;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class to find nearest DASH stop
 * @author Clark Stonehocker
 */
public class FindNearestStopActivity extends Activity implements LocationListener
{
	private TextView stopName;
	private TextView stopAddress;
	private TextView stopDistance;
	private LinearLayout nearestLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_nearest_stop);
		
		nearestLocation = (LinearLayout) findViewById(R.id.nearest_stop1);
		
		//get the user's current location
		Location location;
		LocationManager locationService = (LocationManager) getSystemService(LOCATION_SERVICE);
		//check if the user has GPS enabled
		boolean gps_enabled = locationService.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network_enabled = locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		boolean gps_working, network_working;
		gps_working = network_working = false;
		
		if(!gps_enabled && !network_enabled)
		{
			nearestLocation.setVisibility(View.GONE);
			requestChangeOfLocationSettings("Location Settings Disabled");
		}
		else
		{	
			if(gps_enabled) //if GPS is not enabled, use the network to get the location
			{
				locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
				location = locationService.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				gps_working = (location == null);
			}
			else
			{
				if(network_enabled)
				{
					locationService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
				}
				location = locationService.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				network_working = (location == null);
			}
			
			if(location == null)
			{
				if(!gps_working)
				{
					nearestLocation.setVisibility(View.GONE);
					requestChangeOfLocationSettings("Network Location Requested");
				}
				else if(!network_working)
				{
					nearestLocation.setVisibility(View.GONE);
					requestChangeOfLocationSettings("Network Location disabled");
				}
			}
			else
			{
				nearestLocation.setVisibility(View.VISIBLE);
				stopName = (TextView) findViewById(R.id.nearest_stop1_title);
				stopAddress = (TextView) findViewById(R.id.nearest_stop1_address);
				stopDistance = (TextView) findViewById(R.id.nearest_stop1_distance);
				
				//get the latitude and longitude values of the user's location
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				//get the distances from the user's location to all DASH stops
				ArrayList<Double> distances = getDistances(latitude, longitude);
				//get the shortest distance
				double shortestDistance = Collections.min(distances);
				int closestStationIndex = distances.indexOf(shortestDistance);
				stopName.setText(MainActivity.stopNames.get(closestStationIndex));
				//format the address 
				String address = MainActivity.stopNames.get(closestStationIndex);
				address = address + "\n" + "San Jose, CA";
				final String addressHelp = address;
				//make the address underlined
				String htmlString = "<u>" + address + "</u>";
				stopAddress.setText(Html.fromHtml(htmlString));
				//display the approximate distance
				stopDistance.setText("Approximate distance: " + String.format("%.2f", distances.get(closestStationIndex)) + " miles");
				
				//set listener for address field to perform action when clicked
				stopAddress.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						String uri = "geo:0,0?q=" + addressHelp;
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
						//open the address in Google Maps if it is installed
						if(isAppInstalled("com.google.android.apps.maps"))
						{
							intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
							startActivity(intent);
						}
					}
				});
				
			}
		}
		setupActionBar();
	}
	
	/**
	 * Method to create and show pop-up window prompting user to edit locaiton settings
	 * @param title the title text of the popup window
	 */
	private void requestChangeOfLocationSettings(String title)
	{
		final Dialog dialog=new Dialog(this);
		dialog.setTitle(title);
    	dialog.setContentView(R.layout.prompt_location_settings_change);
    	dialog.setCancelable(false);
    	dialog.show();
    	
    	//button and click event handler for confirmation
    	Button enable = (Button) dialog.findViewById(R.id.location_confirm);
    	enable.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				dialog.dismiss();
				startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
			}
		});
		
    	
    	//button and click event handler for cancel aciton
    	Button cancel = (Button) dialog.findViewById(R.id.location_cancel);
    	cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				dialog.dismiss();
			}
		});
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{	
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		ArrayList<Double> distances = getDistances(latitude, longitude);
		double shortestDistance = Collections.min(distances);
		int closestStationIndex = distances.indexOf(shortestDistance);
		
		stopName.setText(MainActivity.stopNames.get(closestStationIndex));
		String address = MainActivity.stopNames.get(closestStationIndex);
		address = address + "\n" + "San Jose, CA";
		final String addressHelp = address;
		String htmlString = "<u>" + address + "</u>";
		stopAddress.setText(Html.fromHtml(htmlString));
		stopDistance.setText("Approximate distance: " + String.format("%.2f", distances.get(closestStationIndex)) + " miles");
		
		stopAddress.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				String uri = "geo:0,0?q=" + addressHelp;
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
				if(isAppInstalled("com.google.android.apps.maps"))
				{
					intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
					startActivity(intent);
				}
			}
		});
	}
	 
	@Override
	public void onProviderDisabled(String provider) 
	{
		Log.d("Latitude","disable");
	}
	 
	@Override
	public void onProviderEnabled(String provider) 
	{
		Log.d("Latitude","enable");
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		Log.d("Latitude","status");
	}
	
	/**
	 * Method to calculate distances from user's current location and all DASH stops
	 * @param lat the latitude value of the user's location
	 * @param lon the longitude value of the user's location
	 * @return an ArrayList containing all distances (in miles) between user's location and all DASH stops
	 */
	private ArrayList<Double> getDistances(double lat, double lon)
	{
		//an ArrayList to store distances
		ArrayList<Double> distances = new ArrayList<Double>();
		
		//use the smaller of latitude and longitude ArrayList sizes for loop termination
		//They should never be different sizes, but this is for added precaution
		int size = Math.min(MainActivity.stopLatitudes.size(),MainActivity.stopLongitudes.size());
		
		int r = 6378; // average radius of the earth in km
		
		//get first point
		double x1 = lat;
		double y1 = lon;
		
		//calculate distances between user location point and all stations
		for(int i = 0; i < size; i++)
		{
			//get second point
			double x2 = MainActivity.stopLatitudes.get(i);
			double y2 = MainActivity.stopLongitudes.get(i);
			
			//calculate distance between points 
		    double dLat = Math.toRadians(x2 - x1);
		    double dLon = Math.toRadians(y2 - y1);
		    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
		       Math.cos(Math.toRadians(x1)) * Math.cos(Math.toRadians(x2)) 
		      * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		    double d = r * c;
		    double miles = d * 0.621371;
		    distances.add(miles);
		}
				
		return distances;
	}
	
	// helper function to check if Google Maps is installed
	private boolean isAppInstalled(String uri) 
	{
	    PackageManager pm = getApplicationContext().getPackageManager();
	    boolean installed = false;
	    try 
	    {
	        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
	        installed = true;
	        
	    } 
	    catch (PackageManager.NameNotFoundException e) 
	    {
	        installed = false;
	    }
	    return installed;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_nearest_stop, menu);
		return true;
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() 
	{
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
}