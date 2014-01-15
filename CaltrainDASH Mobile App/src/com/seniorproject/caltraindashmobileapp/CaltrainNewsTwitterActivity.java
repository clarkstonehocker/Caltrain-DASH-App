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
 * A class to load the @Caltrain_News Twitter feed.
 * @author Clark Stonehocker
 */
public class CaltrainNewsTwitterActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//set the layout for the activity
		setContentView(R.layout.activity_caltrain_news_twitter);
		
		//use a web view to load the Twitter feed
		WebView webView = (WebView) findViewById(R.id.caltrain_news_twitter_web_view);
		
		//use a progress bar and loading text while page is loading
		final ProgressBar pb = (ProgressBar) findViewById(R.id.web_view_loading);
		final TextView text = (TextView) findViewById(R.id.web_view_loading_text);
		pb.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		
		//set a client for the webView to handle if page has loaded yet
		webView.setWebViewClient(new WebViewClient() 
		{
			boolean loadingFinished = true;
			boolean redirect = false; 
			
			@Override
			/**
			 * Take action when page is finished loading.
			 * @param view the view in question
			 * @param url the webAddress to load
			*/
			public void onPageFinished(WebView view, String url) 
			{
				if(!redirect)
			    {
			       loadingFinished = true;
			    }
			    //hide the loading indicators when page is done loading
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
		
		WebSettings settings = webView.getSettings();
		webView.getSettings().setDomStorageEnabled(true);
		//enable javascript on the page
		settings.setJavaScriptEnabled(true);
		//load the Twitter page URL
		webView.loadUrl("https://twitter.com/Caltrain_News");
		
		setupActionBar();
	}

	@Override
	/**
	 * Create the ActionBar menu
	 * @param menu the menu to initialize
	 * @return true that the menu was set up
	 */
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.caltrain_news_twitter, menu);
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
