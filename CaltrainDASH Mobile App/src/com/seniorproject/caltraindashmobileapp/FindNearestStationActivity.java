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
import android.graphics.Paint;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class for handling logic of finding the nearest Caltrain station
 * @author Clark Stonehocker
 */
public class FindNearestStationActivity extends Activity implements LocationListener 
{
	private TextView stationName;
	private TextView stationAddress;
	private TextView stationDistance;
	private LinearLayout nearestLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_nearest_station);
		
		stationName = (TextView) findViewById(R.id.nearest_station1_title);
		stationAddress = (TextView) findViewById(R.id.nearest_station1_address);
		stationDistance = (TextView) findViewById(R.id.nearest_station1_distance);
		
		nearestLocation = (LinearLayout) findViewById(R.id.nearest_station1);
		
		//get the device's current location
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
			if(gps_enabled)
			{
				locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
				location = locationService.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				gps_working = (location == null);
			}
			else //if GPS is not enabled, use WiFi network instead
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
					requestChangeOfLocationSettings("Network Location Disabled");
				}
			}
			else
			{
				nearestLocation.setVisibility(View.VISIBLE);
				//get latitude and longitude of the user's location
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
			
				//calculate distances from current location to all Caltrain stations
				ArrayList<Double> distances = getDistances(latitude, longitude);
			
				//find the shortest distance
				double shortestDistance = Collections.min(distances);
				int closestStationIndex = distances.indexOf(shortestDistance);
				
				//set station name, address, and approximate distance
				stationName.setText(MainActivity.stationNames.get(closestStationIndex));
				String address = MainActivity.stationAddresses.get(closestStationIndex);
				/*format the address to standard format
				 *Street
				 *City, State
				 */
				String[] addressParts = address.split(",");
				address = addressParts[0] + "\n" + addressParts[1].trim() + ", CA";
				//make a final variable to use in the listener for when the address is clicked
				final String addressHelp = address;
				//make the address underlined to indicate it's clickable
				stationAddress.setPaintFlags(stationAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				stationAddress.setText(address);
				//set approximate distance
				stationDistance.setText("Approximate distance: " + String.format("%.2f", distances.get(closestStationIndex)) + " miles");
				
				//create listener for address, so when address is clicked, location is opened in Google Maps app
				stationAddress.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						String uri = "geo:0,0?q=" + addressHelp;
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
					
						//only load station and user's location in Google Maps if it is installed
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
	 * Function to create and display pop-up window for location services action
	 */
	private void requestChangeOfLocationSettings(String title)
	{
		final Dialog dialog=new Dialog(this);
		dialog.setTitle(title);
    	dialog.setContentView(R.layout.prompt_location_settings_change);
    	dialog.setCancelable(false);
    	dialog.show();
    	
    	//button and click event handler to take user to edit their device's location settings
    	Button enable = (Button) dialog.findViewById(R.id.location_confirm);
    	enable.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
				dialog.dismiss();
				startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
			}
		});
		
    	//button and click event handler to cancel and hide the popup window
    	Button cancel = (Button) dialog.findViewById(R.id.location_cancel);
    	cancel.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
				dialog.dismiss();
			}
		});
	}
	
	@Override
	/**
	 * Method to check if user's location has changed so that it is automatically updated,
	 * and if it is, calculate distances and load nearest station
	 */
	public void onLocationChanged(Location location) 
	{
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		ArrayList<Double> distances = getDistances(latitude, longitude);
		double shortestDistance = Collections.min(distances);
		int closestStationIndex = distances.indexOf(shortestDistance);
		
		stationName.setText(MainActivity.stationNames.get(closestStationIndex));
		String address = MainActivity.stationAddresses.get(closestStationIndex);
		String[] addressParts = address.split(",");
		address = addressParts[0] + "\n" + addressParts[1].trim() + ", CA";
		final String addressHelp = address;
		stationAddress.setPaintFlags(stationAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		stationAddress.setText(address);
		stationDistance.setText("Approximate distance: " + String.format("%.2f", distances.get(closestStationIndex)) + " miles");
		
		stationAddress.setOnClickListener(new OnClickListener()
		{
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
	 * Method to calculate distance between user's location and Caltrain stations
	 * @param lat the latitude value of the user's location
	 * @param lon the longitude value of the user's location
	 * @return an ArrayList containing all distances between the user's location and all Caltrain stations
	 * Note: distances are in miles
	 */
	private ArrayList<Double> getDistances(double lat, double lon)
	{
		//an ArrayList to store distances
		ArrayList<Double> distances = new ArrayList<Double>();
		
		//use the smaller of latitude and longitude ArrayList sizes for loop termination
		//They should never be different sizes, but this is for added precaution
		int size = Math.min(MainActivity.stationLatitude.size(),MainActivity.stationLongitude.size());
		
		int r = 6378; // average radius of the earth in km
		
		//get first point
		double x1 = lat;
		double y1 = lon;
		
		//calculate distances between user location point and all stations
		for(int i = 0; i < size; i++)
		{
			//get second point
			double x2 = MainActivity.stationLatitude.get(i);
			double y2 = MainActivity.stationLongitude.get(i);
			
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
		getMenuInflater().inflate(R.menu.find_nearest_station, menu);
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