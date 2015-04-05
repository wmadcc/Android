package com.wmadcc.five_chess.game_appiication;

import android.app.Application;

import com.wmadcc.five_chess.R;

public class GameApplication extends Application {
	public class StartConfig {
		
	}
	
	public class GameConfig {
		
		public final float MENU_HEIGHT_RATE = 0.1f;
		public final int GAME_BACKGROUND_COLOR =
				getResources()
				.getColor(R.color.ivory); 
		
		public final int TURN_STATE_BAR_HEIGHT = 8;
		
		public class ChessConfig {
			public final int YELLOW_START_COLOR = 
					getResources().getColor(R.color.lightyellow);
			public final int YELLOW_END_COLOR = 
					getResources().getColor(R.color.darkkhaki);
			public final int BLACK_START_COLOR = 
					getResources().getColor(R.color.beige);
			public final int BLACK_END_COLOR = 
					getResources().getColor(R.color.mediumpurple);
			
			public final int CHOSEN_STROKE_WIDTH = 4;
			public final int CHOSEN_STROKE_COLOR = 
					getResources().getColor(R.color.green);
		}
		
		public final float EFFECTIVE_RANGE = 1.2f;
		
		public final String YELLOW_NAME = "黄色一方";
		public final String BLACK_NAME = "紫色一方";
		public final int YELLOW_VALUE = 1;
		public final int BLACK_VALUE = -1;
		
		public final int CHESS_MAP_COLOR = 
				getResources()
				.getColor(R.color.mediumseagreen);
		public final int CHESS_MAP_STROKE_WIDTH = 4;
		public final int CHESS_MOVE_DURATION = 400;
		public final int CHESS_REMOVE_EXPAND_DURATION = 200;
		public final int CHESS_REMOVE_MINIFY_DURATION = 600;
		
		public final String WAIT_YELLOW_GRAB_NAME = "waitYellowGrab";
		public final String WAIT_BLACK_GRAB_NAME = "waitBlackGrab";
		public final String WAIT_YELLOW_MOVE_NAME = "waitYellowMove";
		public final String WAIT_BLACK_MOVE_NAME = "waitBlackMove";
		public final String YELLLOW_GRAB_CHESS_NAME = "YellowgrabChess";
		public final String BLACK_GRAB_CHESS_NAME = "BlackgrabChess";
		public final String MOVE_CHESS_NAME = "moveChess";
		public final String REMOVE_CHESS_NAME = "removeChess";
		public final String JUDGE_GAME_NAME = "judgeGame";
		public final String GAME_OVER_NAME = "gameOver";
		
		public final int GRAB_NUMBER = 2;
			
	}
}
