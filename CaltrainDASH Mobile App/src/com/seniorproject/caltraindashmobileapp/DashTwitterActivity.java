package com.seniorproject.caltraindashmobileapp;

import com.seniorproject.caltraindashmobileapp.R;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Class for the DASH Twitter handle (@VTA)
 * @author Clark Stonehocker
 */
public class DashTwitterActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dash_twitter);
		
		//use an app-contained web view for launching the twitter activity in
		WebView webView = (WebView) findViewById(R.id.dash_twitter_web_view);
		
		//Use a progress bar and text to indicate page is loading
		final ProgressBar pb = (ProgressBar) findViewById(R.id.web_view_loading);
		final TextView text = (TextView) findViewById(R.id.web_view_loading_text);
		pb.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		
		webView.setWebViewClient(new WebViewClient() 
		{
			boolean loadingFinished = true;
			boolean redirect = false; 
			
		   @Override
		   public void onPageFinished(WebView view, String url) 
		   {
		       if(!redirect)
		       {
		          loadingFinished = true;
		       }
		       //hide the loading indicators if page is done loading
			   if(loadingFinished && !redirect)
		       {
		    	   	pb.setVisibility(View.GONE);
		   			text.setVisibility(View.GONE);
		       } 
		       else
		       {
		          redirect = false; 
		       }
		    }
		});
		
		//set settings for the web view and load the URL
		WebSettings settings = webView.getSettings();
		webView.getSettings().setDomStorageEnabled(true);
		settings.setJavaScriptEnabled(true);
		webView.loadUrl("https://twitter.com/VTA");
		
		//set up the Action Bar icons and functions
		setupActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dash_twitter, menu);
		return true;
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() 
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
}