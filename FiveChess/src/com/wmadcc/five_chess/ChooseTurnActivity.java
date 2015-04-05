package com.wmadcc.five_chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wmadcc.five_chess.game_appiication.GameApplication;

public class ChooseTurnActivity extends Activity
		implements OnClickListener {

	Button chooseBlack, chooseYellow, returnButton;
	GameApplication.GameConfig.ChessConfig config;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_turn);
		chooseBlack = (Button) findViewById(R.id.chooseBlack);
		chooseYellow = (Button) findViewById(R.id.chooseYellow);
		returnButton = (Button) findViewById(R.id.returnButton);
		config = ((GameApplication) 
				getApplicationContext())
				 .new GameConfig()
				 .new ChessConfig();
		
		chooseBlack.setBackgroundColor(config.BLACK_END_COLOR);
		chooseYellow.setBackgroundColor(config.YELLOW_END_COLOR);
		chooseBlack.setOnClickListener(this);
		chooseYellow.setOnClickListener(this);
		returnButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View source) {
		Intent gameIntent = 
				new Intent(ChooseTurnActivity.this,
						GameActivity.class);
		switch(source.getId()) {
		case R.id.chooseBlack:
			gameIntent.putExtra("chessTurn", false);
			startActivity(gameIntent);
			break;
		case R.id.chooseYellow:
			gameIntent.putExtra("chessTurn", true);
			startActivity(gameIntent);
			break;
		case R.id.returnButton:
			finish();
			break;
		default:
			break;
		}
		
	}

}
