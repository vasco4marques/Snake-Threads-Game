package game;

import java.io.Serializable;

import environment.Board;
import environment.BoardPosition;
import environment.LocalBoard;

public class Obstacle extends GameElement implements Serializable {
	
	
	private static final int NUM_MOVES=3;
	public static final int OBSTACLE_MOVE_INTERVAL = 1000;
	private int remainingMoves=NUM_MOVES;
	private Board board;
	private BoardPosition position;
	
	
	
	public Obstacle(Board board) {
		super();
		this.board = board;
	}
	
	//Defines this obstacle board position
	public void setBoardPosition(BoardPosition position) {
		this.position = position;
	}
	
	//Returns this obstacle board position
	public BoardPosition getBoardPosition() {
		return position;
	}
	

	//Returns the remaining moves
	public int getRemainingMoves() {
		return remainingMoves;
	}
	
	//Decrement the remaining moves
	public void decrementRemainingMoves() {
		remainingMoves--;
	}
	

}
