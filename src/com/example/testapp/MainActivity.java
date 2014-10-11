package com.example.testapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

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
}