package com.wmadcc.five_chess.game_control;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Point;
import android.os.Message;
import android.widget.Toast;

import com.wmadcc.five_chess.game_appiication.GameApplication;
import com.wmadcc.five_chess.view.Chess;
import com.wmadcc.five_chess.view.Chessboard;

public class GameControl extends StateMachine {
	
	final int CLICK_CHESSBOARD_ID = 0x1111;
	final int OPERATE_ACTION_ID = 0x1112;
	final int SET_NEXT_STATE_ID = 0x1113;
	final int YELLOW_GRAB_ID = 0x1114;
	final int BLACK_GRAB_ID = 0x1115;
	final int YELLOW_GIVEUP_ID = 0x1200;
	final int BLACK_GIVEUP_ID = 0x1201;	
	
	GameApplication.GameConfig config;
	Chessboard chessboard;
	protected Context context;

	String gameWinner;
	Point clickPoint, removePoint,
			moveStart,	moveEnd;
	boolean isYellowTurn, yellowGrabed, blackGrabed;
	ArrayList<Point> yellowToGrab,
	 	blackToGrab;
	int yellowGrabedNum, blackGrabedNum;
	
	public GameControl(Context context) {
		super();		
		 config = ((GameApplication) context
				 .getApplicationContext())
				 .new GameConfig();			 
		 this.context = context;
	}
	
	public void bindChessboard(Chessboard chessboard) {
		this.chessboard = chessboard;
		chessboard.setGameControl(this);
	} 
		
	public void init(boolean isYellowTurn) {
		this.isYellowTurn = isYellowTurn;
		
		gameWinner = null;
		clickPoint = removePoint = 
				moveStart = moveEnd = null;
		yellowGrabed = blackGrabed = false;
		yellowGrabedNum = blackGrabedNum = 0;
		yellowToGrab = 
				blackToGrab =
				new ArrayList<Point>();
		activeState = null;
		
		states.clear();
		addState(new WaitYellowGrab());
		addState(new WaitBlackGrab());
		addState(new WaitYellowMove());
		addState(new WaitBlackMove());
		addState(new YellowGrabChess());
		addState(new BlackGrabChess());
		addState(new MoveChess());
		addState(new RemoveChess());
		addState(new JudgeGame());
		addState(new GameOver());
		
		if(isYellowTurn)
			setState(config.WAIT_YELLOW_GRAB_NAME);
		else
			setState(config.WAIT_BLACK_GRAB_NAME);
	}	
	
	public boolean hasYellowGrabed() {
		return yellowGrabed;
	}
	
	public boolean hasBlackGrabed() {
		return blackGrabed;
	}
	
	public boolean getTurnState() {
		return isYellowTurn;
	}
	
	public String getWinner() {
		return gameWinner;
	}
		
	public void clickChessboard(Point clickPoint) {
		if(!goOnGame())
			return;
		this.clickPoint = clickPoint;
		sendEmptyMessage(CLICK_CHESSBOARD_ID);
	}
	
	public void clickYellowGrab() {
		if(!goOnGame())
			return;
		if(!yellowGrabed
				&& isYellowTurn)
			sendEmptyMessage(YELLOW_GRAB_ID);
	}
	
	public void clickBlackGrab() {
		if(!goOnGame())
			return;
		if(!blackGrabed
				&& !isYellowTurn)
			sendEmptyMessage(BLACK_GRAB_ID);
	}
	
	public void clickYellowGiveup() {
		if(!goOnGame())
			return;
		sendEmptyMessage(YELLOW_GIVEUP_ID);
	}
	
	public void clickBlackGiveup() {
		if(!goOnGame())
			return;
		sendEmptyMessage(BLACK_GIVEUP_ID);
	}
	
	public void getNextState() {
		sendEmptyMessage(SET_NEXT_STATE_ID);
	}
	
	public boolean goOnGame() {
		if(getWinner() != null) {
			sendEmptyMessage(OPERATE_ACTION_ID);
			return false;
		}
		return true;
	}
	
	private void doStateAction() {
		sendEmptyMessage(OPERATE_ACTION_ID);
	}
		
	public void gameOverAction() {
		Toast.makeText(context, 
				"»ñÊ¤ÕßÊÇ" + gameWinner,
				Toast.LENGTH_LONG).show();
	}
		
	public void handleMessage(Message msg) {
		
		switch(msg.what) {
		case CLICK_CHESSBOARD_ID:
			operateAction();
			break;
			
		case OPERATE_ACTION_ID:
			operateAction();
			break;
		
		case SET_NEXT_STATE_ID:
			setNextState();
			break;
			
		case YELLOW_GRAB_ID:
			setState(config.YELLLOW_GRAB_CHESS_NAME);
			operateAction();
			break;
			
		case BLACK_GRAB_ID:
			setState(config.BLACK_GRAB_CHESS_NAME);
			operateAction();
			break;
			
		case YELLOW_GIVEUP_ID:
			gameWinner = config.BLACK_NAME;
			setState(config.GAME_OVER_NAME);
			operateAction();
			break;
			
		case BLACK_GIVEUP_ID:
			gameWinner = config.YELLOW_NAME;
			setState(config.GAME_OVER_NAME);
			operateAction();
			break;
		}
	}
	
	
	class WaitYellowGrab extends State {
		
		WaitYellowGrab() {
			super(config.WAIT_YELLOW_GRAB_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
			chessboard.postInvalidate();
		}
		
		@Override
		public void doActions() {
			if(yellowGrabedNum 
					>= config.GRAB_NUMBER
					|| clickPoint == null) 
				return;
			Chess chess = chessboard
					.getChess(clickPoint.x, 
							clickPoint.y);
			if(chess != null && 
					chess.getValue() == 
					config.BLACK_VALUE) {
				if(yellowToGrab.contains(clickPoint))
					yellowToGrab.remove(clickPoint);
				else {
					if(yellowGrabedNum + 
							yellowToGrab.size() 
							>= config.GRAB_NUMBER) {
						Point abandonPoint = yellowToGrab.get(0);
						chessboard.getChess(abandonPoint.x, 
								abandonPoint.y).setChosen(false);
						yellowToGrab.remove(0);
					}	
					yellowToGrab.add(clickPoint);
				}
				chess.setChosen(!chess.getChosenState());
				chessboard.postInvalidate();
			}
			getNextState();
		}
		
		@Override
		public String checkConditions() {
			return name;
		}
		
		@Override
		public void exitActions() {
			clickPoint = null;
		}
		
	}

	class WaitBlackGrab extends State {

		WaitBlackGrab() {
			super(config.WAIT_BLACK_GRAB_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
			chessboard.postInvalidate();
		}
		
		@Override
		public void doActions() {
			if(blackGrabedNum 
					>= config.GRAB_NUMBER
					|| clickPoint == null) 
				return;
			Chess chess = chessboard
					.getChess(clickPoint.x, 
							clickPoint.y);
			if(chess != null && 
					chess.getValue() == 
					config.YELLOW_VALUE) {
				if(blackToGrab.contains(clickPoint))
					blackToGrab.remove(clickPoint);
				else {
					if(blackGrabedNum + 
							blackToGrab.size() 
							>= config.GRAB_NUMBER) {
						Point abandonPoint = blackToGrab.get(0);
						chessboard.getChess(abandonPoint.x, 
								abandonPoint.y).setChosen(false);
						blackToGrab.remove(0);
					}
					blackToGrab.add(clickPoint);
				}
				chess.setChosen(!chess.getChosenState());
				chessboard.postInvalidate();
			}
			getNextState();
		}
		
		@Override
		public String checkConditions() {
			return name;
		}
		
		@Override
		public void exitActions() {
			clickPoint = null;
		}
		
	}

	class WaitYellowMove extends State {

		WaitYellowMove() {
			super(config.WAIT_YELLOW_MOVE_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
			chessboard.postInvalidate();
		}
		
		@Override
		public void doActions() {
			if(clickPoint == null)
				return;
			
			Chess chess = chessboard
					.getChess(clickPoint.x, 
							clickPoint.y);
			if(chess == null) {
				if(moveStart != null
						&& Math.abs(clickPoint.x 
								- moveStart.x)
						+ Math.abs(clickPoint.y 
								- moveStart.y)
						== 1)
					moveEnd = clickPoint;
			}
			else if(chess.getValue() ==
					config.YELLOW_VALUE) {
				if(chess.getChosenState()) {
					chess.setChosen(false);
					moveStart = null;
				}
				else {
					if(moveStart != null)
						chessboard.getChess(moveStart.x,
								moveStart.y).setChosen(false);
					moveStart = clickPoint;
					chess.setChosen(true);
				}
			}
			chessboard.postInvalidate();
			getNextState();
		}
		
		@Override
		public String checkConditions() {
			if(moveEnd == null)
				return name;
			doStateAction();
			return config.MOVE_CHESS_NAME;		
		}
		
		@Override
		public void exitActions() {
			clickPoint = null;
		}
		
	}
	
	class WaitBlackMove extends State {

		WaitBlackMove() {
			super(config.WAIT_BLACK_MOVE_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
			chessboard.postInvalidate();
		}
		
		@Override
		public void doActions() {
			if(clickPoint == null)
				return;
			
			Chess chess = chessboard
					.getChess(clickPoint.x, 
							clickPoint.y);
			if(chess == null) {
				if(moveStart != null
						&& Math.abs(clickPoint.x 
								- moveStart.x)
						+ Math.abs(clickPoint.y 
								- moveStart.y)
						== 1)
					moveEnd = clickPoint;
			}
			else if(chess.getValue() ==
					config.BLACK_VALUE) {
				if(chess.getChosenState()) {
					chess.setChosen(false);
					moveStart = null;
				}
				else {
					if(moveStart != null)
						chessboard.getChess(moveStart.x,
								moveStart.y).setChosen(false);
					moveStart = clickPoint;
					chess.setChosen(true);
				}
			}
			chessboard.postInvalidate();
			getNextState();
		}
		
		@Override
		public String checkConditions() {
			if(moveEnd == null)
				return name;
			doStateAction();
			return config.MOVE_CHESS_NAME;	
		}
		
		@Override
		public void exitActions() {
			clickPoint = null;
		}
		
	}
	
	class YellowGrabChess extends State {

		YellowGrabChess() {
			super(config.YELLLOW_GRAB_CHESS_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
			chessboard.postInvalidate();
		}
		
		@Override
		public void doActions() {
			if(yellowToGrab.size() == 0) {
				getNextState();
				return;
			}
			Point point = yellowToGrab.get(0);
			if(chessboard
					.hasChess(point.x, 
							point.y) != 0) {
				chessboard.removeChess(
						point.x, point.y);
				yellowGrabedNum += 1;	
				yellowToGrab.remove(0);
				}
		}
		
		@Override
		public String checkConditions() {
			if(yellowToGrab.size() > 0) {
				doStateAction();
				return name;
			}
			if(yellowGrabedNum < 2)
				return config.WAIT_YELLOW_GRAB_NAME;
			yellowGrabed = true;
			isYellowTurn = false;
			if(blackGrabed)
				return config.WAIT_BLACK_MOVE_NAME;
			else
				return config.WAIT_BLACK_GRAB_NAME;
		}
		
		@Override
		public void exitActions() {
			clickPoint = null;
		}
		
	}
	
	class BlackGrabChess extends State {

		BlackGrabChess() {
			super(config.BLACK_GRAB_CHESS_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
		}
		
		@Override
		public void doActions() {
			if(blackToGrab.size() == 0) {
				getNextState();
				return;
			}
			Point point = blackToGrab.get(0);
			if(chessboard
					.hasChess(point.x, 
							point.y) != 0) {
				chessboard.removeChess(
						point.x, point.y);
				blackGrabedNum += 1;	
				blackToGrab.remove(0);
				}
		}
		
		@Override
		public String checkConditions() {	
			if(blackToGrab.size() > 0) {
				doStateAction();
				return name;
			}
			if(blackGrabedNum < 2)
				return config.WAIT_BLACK_GRAB_NAME;
			blackGrabed = true;
			isYellowTurn = true;
			if(yellowGrabed)
				return config.WAIT_YELLOW_MOVE_NAME;
			else
				return config.WAIT_YELLOW_GRAB_NAME;			
		}
		
		@Override
		public void exitActions() {
			clickPoint = null;
		}
		
	}
	
	class MoveChess extends State {

		MoveChess() {
			super(config.MOVE_CHESS_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
			chessboard.getChess(moveStart.x, 
					moveStart.y).setChosen(false);
			chessboard.postInvalidate();
		}
		
		@Override
		public void doActions() {
			chessboard.moveChess(moveStart.x, moveStart.y, 
					moveEnd.x, moveEnd.y);				
		}
		
		@Override
		public String checkConditions() {
			removePoint = chessboard
					.judgeMove(moveEnd.x, 
							moveEnd.y);
			if(removePoint != null) {
				doStateAction();
				return config.REMOVE_CHESS_NAME;
				}
			if(isYellowTurn) { 	
				isYellowTurn = false;
				return config.WAIT_BLACK_MOVE_NAME;
			}
			else { 
				isYellowTurn = true;
				return config.WAIT_YELLOW_MOVE_NAME;
			}
		}
		
		@Override
		public void exitActions() {
			moveEnd = moveStart = null;
		}
		
	}
	
	class RemoveChess extends State {

		RemoveChess() {
			super(config.REMOVE_CHESS_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
			chessboard.postInvalidate();
		}
		
		@Override
		public void doActions() {
			if(removePoint == null)
				return;
			chessboard.removeChess(
					removePoint.x, removePoint.y);
		}
		
		@Override
		public String checkConditions() {
			doStateAction();
			return config.JUDGE_GAME_NAME;
		}
		
		@Override
		public void exitActions() {
			removePoint = null;
		}
		
	}
	
	class JudgeGame extends State {

		JudgeGame() {
			super(config.JUDGE_GAME_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
			chessboard.postInvalidate();
		}
		
		@Override
		public void doActions() {
			gameWinner = chessboard.getWinner();
			getNextState();
		}
		
		@Override
		public String checkConditions() {
			if(gameWinner != null) {
				doStateAction();
				return config.GAME_OVER_NAME;
			}
			if(isYellowTurn) {
				isYellowTurn = false;
				return config.WAIT_BLACK_MOVE_NAME;
			}
			else {
				isYellowTurn = true;
				return config.WAIT_YELLOW_MOVE_NAME;
			}
		}
		
		@Override
		public void exitActions() {
			
		}
		
	}
	
	class GameOver extends State {

		GameOver() {
			super(config.GAME_OVER_NAME);
		}
		
		@Override
		public void entryActions() {
			System.out.print("entry " + name +"\n");
			chessboard.postInvalidate();
		}
		
		@Override
		public void doActions() {
			gameOverAction();
			getNextState();
		}
		
		@Override
		public String checkConditions() {
			return null;
		}
		
		@Override
		public void exitActions() {
			
		}
		
	}
	
}
