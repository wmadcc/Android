package com.wmadcc.five_chess.game_control;

import java.util.HashMap;

import android.os.Handler;

public abstract class StateMachine extends Handler{
	protected HashMap<String, State> states;
	protected State activeState;
	
	public StateMachine() {
		states = new HashMap<String, State>();
		activeState = null;
	}
		
	protected void addState(State state) {
		states.put(state.name, state);
	}
	
	protected void operateAction() {
		if(activeState == null)
			return;
		activeState.doActions();
	}
	
	protected void setNextState() {
        String newStateName = activeState.checkConditions();
        if(newStateName != null 
        		&& !newStateName.equals(""))
        	setState(newStateName);
	}
		
	protected void setState(String newStateName) {
		if(activeState != null) 
			activeState.exitActions();
		
		activeState = states.get(newStateName);
		activeState.entryActions();
	}
	
	
	protected abstract class State{
		protected String name;
		protected State(String name) {
			this.name = name;
		}
		
		protected  abstract void entryActions();
		
		protected  abstract void doActions();
		
		protected  abstract String checkConditions();
		
		protected  abstract void exitActions();
		
	}
}