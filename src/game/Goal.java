package game;

//Made by Vasco and Prof Luis.

import environment.Board;
import environment.LocalBoard;



public class Goal extends GameElement  {
	private int value=1;
	private Board board;
	public static final int MAX_VALUE=10;
	public Goal( Board board2) {
		this.board = board2;
	}
	

	//Returns the goal value
	public int getValue() {
		return value;
	}

	//Increments the goal value
	public void incrementValue() throws InterruptedException {
		if(value<MAX_VALUE)
			value++;
	}

	//Everything that envolves capturing the goal -> Incrementing its value, removing itself from the board and inserting himself again 
	// if it doesn't reach the max value
	public synchronized int captureGoal() throws InterruptedException {
		incrementValue();
		board.removeActualGoal();
		if(value != MAX_VALUE)
			board.addGameElement(this);
		return -1;
	}
}
