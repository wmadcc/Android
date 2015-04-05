package com.wmadcc.five_chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wmadcc.five_chess.game_appiication.GameApplication;

public class StartActivity extends Activity
			implements OnClickListener {
	
	GameApplication.StartConfig config;
	protected Button startBn, helpBn, exitBn;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		config = ((GameApplication) 
					getApplicationContext())
				.new StartConfig();
		
		// init Buttons to be used
		startBn = (Button) findViewById(R.id.startButton);
		helpBn = (Button) findViewById(R.id.helpButton);
		exitBn = (Button) findViewById(R.id.exitButton);
				
		//set listeners for the buttons
		startBn.setOnClickListener(this);
		helpBn.setOnClickListener(this);
		exitBn.setOnClickListener(this);		
		
	}
	
	@Override
	public void onClick(View source) {
		switch(source.getId()) {
		case R.id.startButton:
			Intent chooseTurnIntent = 
			new Intent(StartActivity.this,
					ChooseTurnActivity.class);
			startActivity(chooseTurnIntent);			
			break;
			
		case R.id.helpButton:
			Intent helpIntent = 
			new Intent(StartActivity.this,
					HelpActivity.class);
			startActivity(helpIntent);
			break;
			
		case R.id.exitButton:
			finish();
			
		default:
			break;
		}
	}

}
