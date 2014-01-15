package com.seniorproject.caltraindashmobileapp;

import com.seniorproject.caltraindashmobileapp.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

/**
 * Class to display the DASH route map
 * @author Clark Stonehocker
 */
public class DashRouteMapActivity extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dash_route_map);
		
		//load the map in a web view because it has built-in zooming and panning
		WebView view = (WebView) findViewById(R.id.dash_route);
		view.getSettings().setBuiltInZoomControls(true);
		view.setHorizontalScrollBarEnabled(false);
		view.setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
		view.loadUrl("file:///android_asset/dashmap.GIF");
		
		setupActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dash_route_map, menu);
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