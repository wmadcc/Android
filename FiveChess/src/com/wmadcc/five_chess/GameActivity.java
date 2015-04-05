package com.wmadcc.five_chess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.wmadcc.five_chess.game_appiication.GameApplication;
import com.wmadcc.five_chess.game_control.GameControl;
import com.wmadcc.five_chess.view.Chessboard;

public class GameActivity extends Activity 
		implements View.OnClickListener {

	GameApplication.GameConfig config;
	
	GameControl gameControl;
	Chessboard chessboard;
		
	LinearLayout gameBoard, 
			blackLayout, yellowLayout;
	Button blackGiveup, blackGrab,
			 yellowGrab, yellowGiveup;
	Intent startIntent;
	
	float displayWidth, displayHeight;
	float boardWidth, boardHeight;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		 config = ((GameApplication) 
				 getApplicationContext())
				 .new GameConfig();
		
		startIntent = getIntent();
		boolean chessTurn = startIntent
				.getBooleanExtra("chessTurn", true);
		
		blackGiveup = (Button) 
				findViewById(R.id.blackGiveup);
		blackGrab = (Button) 
				findViewById(R.id.blackGrab);
		yellowGrab = (Button) 
				findViewById(R.id.yellowGrab);
		yellowGiveup = (Button) 
				findViewById(R.id.yellowGiveup);
		
		blackLayout = (LinearLayout) 
				findViewById(R.id.blackLayout);
		yellowLayout = (LinearLayout) 
				findViewById(R.id.yellowLayout);
		gameBoard = (LinearLayout) 
				findViewById(R.id.gameBoard);
				
		setLayoutParams();
		
		blackGiveup.setOnClickListener(this);
		blackGrab.setOnClickListener(this);
		yellowGrab.setOnClickListener(this);
		yellowGiveup.setOnClickListener(this);
		
		chessboard = new Chessboard(this, boardWidth, boardHeight);
		gameControl = new GameControl(this) {
			@Override
			public void gameOverAction()  {
				super.gameOverAction();
				AlertDialog.Builder builder = 
						new AlertDialog.Builder(this.context);
				builder.setMessage("获胜者是" + gameControl.getWinner()
						+ "！请确定");
				builder.setPositiveButton("确定", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {	
						finish();
					}
				});
				builder.create().show();
			}
		};
		gameControl.bindChessboard(chessboard);
		gameControl.init(chessTurn);
		
		gameBoard.setBackgroundColor(config.GAME_BACKGROUND_COLOR);
		gameBoard.addView(chessboard);
	}
	
	public void inintGame() {
		
	}
	
	public void setLayoutParams() {
		WindowManager windowManager = 
				getWindowManager();
		Display display = windowManager
				.getDefaultDisplay();
		DisplayMetrics metrics = 
				new DisplayMetrics();
		display.getMetrics(metrics);
		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;
		
		LayoutParams blackLayoutParams = 
				blackLayout.getLayoutParams();
		blackLayoutParams.height = 
				(int) (displayHeight * config.MENU_HEIGHT_RATE);
		blackLayout.setLayoutParams(blackLayoutParams);
		
		LayoutParams yellowLayoutParams = 
				yellowLayout.getLayoutParams();
		yellowLayoutParams.height = 
				(int) (displayHeight * config.MENU_HEIGHT_RATE);
		yellowLayout.setLayoutParams(yellowLayoutParams);
		
		LayoutParams gameBoardLayoutParams = 
				gameBoard.getLayoutParams();
		gameBoardLayoutParams.height 
				= (int) (displayHeight * (1 - config.MENU_HEIGHT_RATE * 2));
		gameBoard.setLayoutParams(gameBoardLayoutParams);
		
		boardWidth = displayWidth;
		boardHeight = 
				displayHeight * (1 - config.MENU_HEIGHT_RATE * 2);
	}
	
	@Override
	public void onClick(View source) {		
		switch(source.getId()) {
		case R.id.blackGiveup:
			if(gameControl.goOnGame())
				confirmGiveup(false);
			break;
			
		case R.id.blackGrab:
			gameControl.clickBlackGrab();
			break;
			
		case R.id.yellowGiveup:
			if(gameControl.goOnGame())
				confirmGiveup(true);
			break;
			
		case R.id.yellowGrab:
			gameControl.clickYellowGrab();
			break;
			
		default:
			break;
		}
	}
	
	public void confirmGiveup(final boolean isYellow) {
		AlertDialog.Builder builder = 
				new AlertDialog.Builder(this);
		builder.setMessage("确认认输？");
		builder.setPositiveButton("确定", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {	
				if(isYellow) 
					gameControl.clickYellowGiveup();
				else
					gameControl.clickBlackGiveup();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {	
			}
		});
		builder.create().show();
	}
	
}
