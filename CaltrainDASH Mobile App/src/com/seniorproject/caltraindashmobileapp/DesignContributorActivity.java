package com.seniorproject.caltraindashmobileapp;

import com.seniorproject.caltraindashmobileapp.R;

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
 * Class for loading the application's design contributor's website in a web view
 * @author Clark Stonehocker
 */
public class DesignContributorActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_design_contributor);
		
		//set up Web View for launching the web site
		WebView webView = (WebView) findViewById(R.id.design_contributor_web_view);
		
		//use a progress bar and loading indicator text
		final ProgressBar pb = (ProgressBar) findViewById(R.id.web_view_loading);
		final TextView text = (TextView) findViewById(R.id.web_view_loading_text);
		pb.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		
		//use web client checking if page has loaded or not
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
			   if(loadingFinished && !redirect)
			   {
			      	pb.setVisibility(View.GONE);
			   		text.setVisibility(View.GONE);
			        //HIDE LOADING IT HAS FINISHED
			   } 
			   else
			   {
				   redirect = false; 
			   }
			}
		});
		
		//set settings for web view and load URL 
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadWithOverviewMode(true);
	    webView.getSettings().setUseWideViewPort(true);
		webView.loadUrl("http://losmontoya.com");
		
		setupActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.design_contributor, menu);
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