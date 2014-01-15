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
 * Class to load @GoCaltrain Twitter feed
 * @author Clark Stonehocker
 */
public class GoCaltrainTwitterActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_go_caltrain_twitter);
		
		//initialize web view and progress bar with loading text
		WebView webView = (WebView) findViewById(R.id.go_caltrain_twitter_web_view);
		final ProgressBar pb = (ProgressBar) findViewById(R.id.web_view_loading);
		final TextView text = (TextView) findViewById(R.id.web_view_loading_text);
		pb.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		
		//use a web client to find when page is done loading
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
		       //hide progress bar and loading text when page is finished loading
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
	
		//set up web view settings and load URL
		WebSettings settings = webView.getSettings();
		webView.getSettings().setDomStorageEnabled(true);
		settings.setJavaScriptEnabled(true);
		webView.loadUrl("https://twitter.com/GoCaltrain");
		
		setupActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.go_caltrain_twitter, menu);
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