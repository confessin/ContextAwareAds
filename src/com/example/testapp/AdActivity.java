package com.example.testapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.view.View;

public class AdActivity extends Activity{
	
	
	private float screenWidth;
	private float screenHeight;
	private float scalefactor;
	private float paddingScalefactor;
	private ScrollView scrollView;
	private	LinearLayout baseContainer;
	private WebView webView;

    private String content;
	@Override 
	public void onCreate(Bundle savedInstanceState) 
	{ 
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(getIntent() != null)
        {
            content = getIntent().getExtras().getString("tags");
        }
	    createBaseContainer();
	    setContentView(scrollView);
	}
	
	
	private void createBaseContainer() 
	{ 
		
		scrollView = new ScrollView(getApplicationContext());
	    scrollView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    webView = new WebView(getApplicationContext());
	    webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		webView.setBackgroundColor(Color.DKGRAY);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });
	    baseContainer = new LinearLayout(this); 
		baseContainer.addView(webView);
		Display display = getWindowManager().getDefaultDisplay(); 
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		if(screenHeight < screenWidth)
			screenWidth = screenHeight;
		baseContainer.setMinimumWidth((int) screenWidth);
		paddingScalefactor = (float)(screenWidth/12.0f);
		scalefactor = (float)((screenWidth-paddingScalefactor)/600.0f);
		
		baseContainer.setLayoutParams(new LayoutParams((int)(screenWidth-paddingScalefactor),(int)(360*scalefactor)));
        baseContainer.setOrientation(LinearLayout.VERTICAL);
		baseContainer.setBackgroundColor(Color.DKGRAY);
		scrollView.setBackgroundColor(Color.DKGRAY);
		scrollView.addView(baseContainer);
	}

    @Override
    protected void onResume() {
        super.onResume();
        String url = content == null ? "http://10.14.124.101:5000/get_ad?tags=" : "http://10.14.124" +
                ".101:5000/get_ad?tags=" + content;
        Log.d("activity2",url);
        webView.loadUrl(url);
        webView.setVisibility(View.VISIBLE);
//        TranslateAnimation animation = new TranslateAnimation(0, 0, -500,0);
//        animation.setDuration(500); // duartion in ms
//        animation.setFillAfter(false);
//        scrollView.startAnimation(animation);
    }
}
