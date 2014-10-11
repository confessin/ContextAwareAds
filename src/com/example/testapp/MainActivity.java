package com.example.testapp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

	String content = new String();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.button1);
        final EditText mEdit   = (EditText)findViewById(R.id.editText1);
        final TextView view = (TextView) findViewById(R.id.text);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
			public void onClick(View v) {
            	String response = new String();
            	String tags = mEdit.getText().toString();
            	mEdit.setText("");
            	InputMethodManager imm = (InputMethodManager)getSystemService(
            			      Context.INPUT_METHOD_SERVICE);
            			imm.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);
            	
                Intent intent = new Intent(MainActivity.this, AdActivity.class);
                intent.putExtra("html_data", getResources().getString(R.string.server_url) + tags);
                startActivity(intent);		
            	//if(tags != null && !tags.isEmpty())
            	//{
            	//	  new LongOperation().execute(getResources().getString(R.string.server_url) + tags );
            	//}
            	//else
            	//{
            	//   view.setText("Don't fuck around. Enter some text");
            	//}
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	  
    // Class with extends AsyncTask class
    private class LongOperation  extends AsyncTask<String, Void, Void> {
         
        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
         
        TextView uiUpdate = (TextView) findViewById(R.id.text);
         
        protected void onPreExecute() {
            Dialog.setMessage("Connecting Server ....");
            Dialog.show();
        }
 
        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                
                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.
                 
                // Server url call by GET method
                HttpGet httpget = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Content = Client.execute(httpget, responseHandler);
                 
            } catch (ClientProtocolException e) {
                Error = e.getMessage();
                cancel(true);
            } catch (IOException e) {
                Error = e.getMessage();
                cancel(true);
            }
//             
            return null;
        }
         
        protected void onPostExecute(Void unused) {
             
        	Dialog.dismiss();
            if (Error != null) 
            {
                uiUpdate.setText(Error);
            }  else 
            {
                uiUpdate.setText(Content);
                Intent intent = new Intent(MainActivity.this, AdActivity.class);
                intent.putExtra("html_data", content);
                startActivity(intent);
            }
        }
    }
}
