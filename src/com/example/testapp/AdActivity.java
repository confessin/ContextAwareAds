package com.example.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AdActivity extends Activity{
	
	   private String url;
	   private WebView browser;

	   @Override		
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
		  setContentView(R.layout.ad_activity);
		  //url = savedInstanceState.getString("html_data");
	      browser = (WebView)findViewById(R.id.webview1);
	      browser.setWebViewClient(new WebViewBrowser());
	   }


	   public void open(View view){
	      browser.getSettings().setLoadsImagesAutomatically(true);
	      browser.getSettings().setJavaScriptEnabled(true);
	      browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	      browser.loadUrl("http://www.google.com");
	   }
	  
	   private class WebViewBrowser extends WebViewClient {
	      @Override
	      public boolean shouldOverrideUrlLoading(WebView view, String url) {
	         view.loadUrl(url);
	         return true;
	      }
	   }

	   @Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	      // Inflate the menu; this adds items to the action bar if it is present.
	      getMenuInflater().inflate(R.menu.main, menu);
	      return true;
	   }
}
