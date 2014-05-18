package com.wmadcc.five_chess.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

import com.wmadcc.five_chess.game_appiication.GameApplication;

public class Chess extends View{
	
	GameApplication.GameConfig.ChessConfig config;
	
	private boolean isChosen = false; 
	private int chessValue = 0;
	
	private Paint paint, chosenPaint;
	private int indexX, indexY;
	private float centerX, centerY;
	private float radius;
	
	public Chess(Context context) {
		super(context);
		config = ((GameApplication) context
				 .getApplicationContext())
				 .new GameConfig()
				 .new ChessConfig();
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public float getRadius() {
		return radius;
	}
		
	public void setValue(int chessValue) {
		this.chessValue = chessValue;	

		int startColor, endColor;
		if(chessValue > 0) {
			startColor = config.YELLOW_START_COLOR;
			endColor = config.YELLOW_END_COLOR;
		}
		else {
			startColor = config.BLACK_START_COLOR;
			endColor = config.BLACK_END_COLOR;
		}
		RadialGradient gradient = 
				new RadialGradient(0, 0, 
						radius, startColor, 
						endColor, Shader.TileMode.CLAMP);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(gradient);
		
		chosenPaint = new Paint();
		chosenPaint.setAntiAlias(true);
		chosenPaint.setColor(config.CHOSEN_STROKE_COLOR);
		chosenPaint.setStyle(Style.STROKE);
		chosenPaint.setStrokeWidth(config.CHOSEN_STROKE_WIDTH);	
	}
	
	public int getValue() {
		return chessValue;
	}
	
	public float getCenterX()
	{
		return centerX;
	}
	
	public void setCenterX(float centerX)
	{
		this.centerX = centerX;
	}
	
	public float getCenterY()
	{
		return centerY;
	}
	
	public void setCenterY(float centerY)
	{
		this.centerY = centerY;
	}
	
	public void setIndexX(int indexX) {
		this.indexX = indexX;
	}
	
	public int getIndexX() {
		return indexX;
	}
	
	public void setIndexY(int indexY) {
		this.indexY = indexY;
	}
	
	public int getIndexY() {
		return indexY;
	}

	public boolean getChosenState() {
		return isChosen;
	}

	public void setChosen(boolean isChosen) {
		this.isChosen = isChosen;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(centerX, centerY);
		if(isChosen)
			canvas.drawCircle(0, 0,	radius + config
					.CHOSEN_STROKE_WIDTH, chosenPaint);
		canvas.drawCircle(0, 0, radius, paint);
		canvas.restore();
	}
		
}
