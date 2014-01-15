package com.seniorproject.caltraindashmobileapp;

import com.seniorproject.caltraindashmobileapp.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.support.v4.app.NavUtils;

/**
 * Class for the Caltrain Route Map
 * @author Clark Stonehocker
 *
 */
public class CaltrainRouteMap extends Activity 
{
	@Override
	/**
	 * Create the activity.
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caltrain_route_map);
		
		//Use a web view for the map(an image file) because it has built-in zooming and panning
		WebView view = (WebView) findViewById(R.id.caltrain_route);
		view.getSettings().setBuiltInZoomControls(true);
		view.setVerticalScrollBarEnabled(false);
		view.setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
		view.loadUrl("file:///android_asset/caltrainmap.png");//load the image file
		
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() 
	{
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.caltrain_route_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}