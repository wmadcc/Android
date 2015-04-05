package com.wmadcc.five_chess.view;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.wmadcc.five_chess.game_appiication.GameApplication;
import com.wmadcc.five_chess.game_control.GameControl;

public class Chessboard extends View 
		implements AnimatorUpdateListener{
	
	Bitmap chessMapPic;
	public ArrayList<Chess> chesses 
						= new ArrayList<Chess>();
	float zeroPointX, zeroPointY;
	float xStep, yStep;
	float boardWidth, boardHeight;
	float chessRadius;
	int[][] chessMap  = new int[4][4];
	boolean inAction = true;
	GameApplication.GameConfig config;
	GameApplication.GameConfig
		.ChessConfig chessConfig;
	GameControl gameControl;
	Paint turnStatePaint = new Paint();
	
	public Chessboard(Context context) {
		super(context);
		config = ((GameApplication) context
				 .getApplicationContext())
				 .new GameConfig();	
		chessConfig = config.new ChessConfig();
		 
		inAction = false;
		this.boardWidth = this.getWidth();
		this.boardHeight = this.getHeight();

		initChessMap();
		initChesses();
	}
	
	public Chessboard(Context context, float boardWidth,
			float boardHeight) {
		super(context);
		config = ((GameApplication) context
				 .getApplicationContext())
				 .new GameConfig();	
		chessConfig = config.new ChessConfig();
		inAction = false;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;

		initChessMap();
		initChesses();
	}
	
	public void setGameControl(GameControl gameControl) {
		this.gameControl = gameControl;
	}
	
	public void initChessMap() {
		xStep = boardWidth / 4;
		yStep = boardHeight / 4;
					
		chessRadius = xStep < yStep ? xStep / 3 : yStep / 3;
		
		zeroPointX = xStep / 2;
		zeroPointY = yStep / 2;

		chessMapPic = getChessMapPic();
	}
	
	public Bitmap getChessMapPic() {
		Bitmap bgBitmap = Bitmap.createBitmap((int) boardWidth,
				(int) boardHeight, Config.ARGB_8888);
		Canvas bgCanvas = new Canvas();
		Paint bgPaint = new Paint();
		
		bgCanvas.setBitmap(bgBitmap);
		bgCanvas.drawColor(config.GAME_BACKGROUND_COLOR);
		
		bgPaint.setColor(config
				.CHESS_MAP_COLOR);
		bgPaint.setAntiAlias(true);
		bgPaint.setStyle(Style.STROKE);
		bgPaint.setStrokeWidth(config.CHESS_MAP_STROKE_WIDTH);
		
		for(int i=0; i <4; i++) {
			bgCanvas.drawLine(zeroPointX, zeroPointY + yStep * i, 
					zeroPointX + xStep * 3, zeroPointY+ yStep * i, bgPaint);
		}
		
		for(int i=0; i <4; i++) {
			bgCanvas.drawLine(zeroPointX + xStep * i, zeroPointY, 
					zeroPointX + xStep * i, zeroPointY+ yStep * 3, bgPaint);
		}
		
		bgPaint.setStyle(Style.FILL);
		bgPaint.setStrokeWidth(0);
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++) 
				bgCanvas.drawCircle(zeroPointX + xStep * i, 
						zeroPointY + yStep * j, chessRadius, bgPaint);
		
		return bgBitmap;
		
	}
	
	public void initChesses() {
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++) {
				Chess chess = new Chess(getContext());
				chess.setIndexX(i);
				chess.setIndexY(j);
				chess.setCenterX(zeroPointX + xStep * i);
				chess.setCenterY(zeroPointY + yStep * j);
				chess.setRadius(chessRadius);
				if(j < 2) {
					chessMap[i][j] = config.BLACK_VALUE;
					chess.setValue(config.BLACK_VALUE);
				}
				else {
					chessMap[i][j] = config.YELLOW_VALUE;
					chess.setValue(config.YELLOW_VALUE);	
				}
				chesses.add(chess);
			}
	}
	
	public void initChesses(int[][] oldChessMap) {
		int chessValue;
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++) {
				chessValue = oldChessMap[i][j];
				if(chessValue != 0){
					Chess chess = new Chess(getContext());
					chess.setIndexX(i);
					chess.setIndexY(j);
					chess.setCenterX(zeroPointX + xStep * i);
					chess.setCenterY(zeroPointY + yStep * j);
					chess.setRadius(chessRadius);
					chessMap[i][j] = oldChessMap[i][j];
					chess.setValue(oldChessMap[i][j]);
					chesses.add(chess);
					}
				else
					chessMap[i][j] = 0;
				}
	}
	
	public Chess getChess(int indexX, int indexY) {
		for (Chess chess : chesses) {
			if(chess.getIndexX() == indexX &&
					chess.getIndexY() == indexY)
				return chess;
		}
		return null;
	}
	
	public int hasChess(int indexX, int indexY) {
		if(indexX < 0 || indexX > 3
				|| indexY < 0 || indexY > 3) 
			return 0;
		return chessMap[indexX][indexY];
	}
	
	public void moveChess(int indexX, int indexY,
			int dx, int dy) {
		if(inAction)
			return;
		
		inAction = true;
		final Chess chess = getChess(indexX, indexY);
		if(chess == null) {
			inAction = false;
			return;
		}
		
		final int startX, startY,targetX, targetY;
		startX = indexX;
		startY = indexY;
		targetX = dx;
		targetY = dy;
		
		float endX, endY;
		endX = zeroPointX + xStep * targetX;
		endY = zeroPointY + yStep * targetY;
		
		ValueAnimator animX = ObjectAnimator
				.ofFloat(chess, "centerX", endX);
		animX.setInterpolator(new LinearInterpolator());
		animX.setDuration(config
				.CHESS_MOVE_DURATION);
		animX.addUpdateListener(this);
		
		ValueAnimator animY = ObjectAnimator
				.ofFloat(chess, "centerY", endY);
		animY.setInterpolator(new LinearInterpolator());
		animY.setDuration(config
				.CHESS_MOVE_DURATION);
		animY.addUpdateListener(this);
		
		animX.addListener(new AnimatorListenerAdapter(){
			@Override
			public void onAnimationEnd(Animator anim) {
				chessMap[startX][startY] = 0;
				chess.setIndexX(targetX);
				chess.setIndexY(targetY);
				chessMap[targetX][targetY] = chess.getValue();
				inAction = false;
				gameControl.getNextState();
			}
		});
		
		AnimatorSet animMove = new AnimatorSet();
		animMove.play(animX).with(animY);
		animMove.start();
	}
	
	public Point judgeMove(int indexX, int indexY) {
		Point selectPoint = null; 
		
		int resultValue = chessMap[indexX][indexY];
		int xResult = 0, yResult = 0;
		int xChessNum = 0, yChessNum = 0;
		
		for(int i = 0; i < 4; i++) {
			if(chessMap[i][indexY] != 0) {
				xChessNum += 1;
				xResult += chessMap[i][indexY];
			}
			if(chessMap[indexX][i] != 0) {
				yChessNum += 1;
				yResult += chessMap[indexX][i];
			}
		}
		
		if(xResult == resultValue
				&& xChessNum == 3) {
			for(int i = 0; i < 4; i++) {
				if(chessMap[i][indexY] 
						== 0 &&	(i < 1 || i > 2)) {
					if(hasChess((i + 5) % 4, indexY) ==
							-resultValue)
						return new Point((i + 5) % 4, indexY);
					if(hasChess((i + 3) % 4, indexY)  ==
							-resultValue)
						return new Point((i + 3) % 4, indexY);
				}
			}
			
		}
		
		if(yResult == resultValue
				&& yChessNum == 3) {			
			for(int i = 0; i < 4; i++) {
				if(chessMap[indexX][i] 
						== 0 &&	(i < 1 || i > 2)) {
					if(hasChess(indexX, (i + 5) % 4) ==
							-resultValue)
						return new Point(indexX, (i + 5) % 4);
					if(hasChess(indexX, (i + 3) % 4)  ==
							-resultValue)
						return new Point(indexX, (i + 3) % 4);
				}
			}
		}
		return selectPoint;
	}
	
	public Point getSelectPoint(float positionX,
			float positionY) {
		int indexX, indexY;
		indexX = (int) (positionX / xStep);
		indexY = (int) (positionY / yStep);
		
		if(indexX >= 0 && indexX < 4
				&& indexY >= 0 && indexY < 4
				&& (Math.pow(positionX - zeroPointX - xStep * indexX, 2)
						+ Math.pow(positionY - zeroPointY - yStep * indexY, 2)
						< Math.pow(chessRadius * config.EFFECTIVE_RANGE, 2)))
			return new Point(indexX, indexY);
		else
			return null;
	}
	
	public void removeChess(int indexX, int indexY) {
		if(inAction)
			return;

		inAction = true;
		final Chess chess = getChess(indexX, indexY);
		if(chess == null) {
			inAction = false;
			return;
		}
		
		final int targetX, targetY;
		targetX = indexX;
		targetY = indexY;
		
		ValueAnimator expand = ObjectAnimator
				.ofFloat(chess, "radius", 1.3F * chessRadius);
		expand.setInterpolator(new AccelerateInterpolator());
		expand.setDuration(config
				.CHESS_REMOVE_EXPAND_DURATION);
		expand.addUpdateListener(this);
		
		ValueAnimator minify = ObjectAnimator
				.ofFloat(chess, "radius", 0);
		minify.setInterpolator(new AccelerateInterpolator());
		minify.setDuration(config
				.CHESS_REMOVE_MINIFY_DURATION);
		minify.addUpdateListener(this);
		
		minify.addListener(new AnimatorListenerAdapter(){
			@Override
			public void onAnimationEnd(Animator anim) {
				chesses.remove(chess);
				chessMap[targetX][targetY] = 0;
				inAction = false;
				gameControl.getNextState();
			}
		});

		AnimatorSet disappear = new AnimatorSet();
		disappear.play(expand).before(minify);
		disappear.start();
	}
	
	public int[] getChessNum() {
		int yellowNum = 0, blackNum = 0;
		for(Chess chess : chesses) {
			if(chess.getValue() == config.YELLOW_VALUE)
				yellowNum += 1;
			else if(chess.getValue() == config.BLACK_VALUE)
				blackNum += 1;
		}
		return new int[] {yellowNum, blackNum};
	}
	
	public String getWinner() {
		int [] chessNum = getChessNum();
		if(chessNum[0] < 2)
			return config.BLACK_NAME;
		if(chessNum[1] < 2)
			return config.YELLOW_NAME;
		return null;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(chessMapPic, 0, 0, null);
		for (Chess chess : chesses) {
			chess.draw(canvas);
		}
		
		if(gameControl.getTurnState()) {
			if(gameControl.hasYellowGrabed())
				turnStatePaint.setColor(chessConfig.YELLOW_END_COLOR);
			else
				turnStatePaint.setColor(chessConfig.BLACK_END_COLOR);
			canvas.drawRect(0f, boardHeight - config.TURN_STATE_BAR_HEIGHT,
				boardWidth, boardHeight, turnStatePaint);
		}
		else {
			if(gameControl.hasBlackGrabed())
				turnStatePaint.setColor(chessConfig.BLACK_END_COLOR);
			else
				turnStatePaint.setColor(chessConfig.YELLOW_END_COLOR);
			canvas.drawRect(0f, 0f, boardWidth, 
					config.TURN_STATE_BAR_HEIGHT, turnStatePaint);
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(inAction)
			return true;
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			Point touchPoint = getSelectPoint(event.getX(), event.getY());
			if(touchPoint != null)
				gameControl.clickChessboard(touchPoint);
		}
		return true;
	}
			
	public boolean checkChessboard() {
		for(int i = 0; i< 4; i++) {
			for(int j = 0; j < 4; j++) {
				int mapChessValue = hasChess(j, i);
				System.out.print(" " + mapChessValue);
				Chess chess = getChess(j, i);
				if(chess == null
					&& mapChessValue != 0)
						return false;
				if(chess != null
					&& chess.getValue() != mapChessValue)
						return false;
			}
			System.out.print("\n");
		}
		return true;
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		postInvalidate();			
	}		
}

