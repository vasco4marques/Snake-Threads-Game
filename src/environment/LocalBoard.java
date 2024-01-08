package environment;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.ObstacleMover;
import game.Server;
import game.Snake;
import game.AutomaticSnake;

/** Class representing the state of a game running locally
 * 
 * @author luismota
 *
 */
public class LocalBoard extends Board implements Serializable{
	
	private static final int NUM_SNAKES = 2;
	private static final int NUM_OBSTACLES = 20;
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 3;


	
	
	public LocalBoard() {
		
		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
		}

		addObstacles(NUM_OBSTACLES);
		
		
		Goal goal=addGoal();

	}

	
	//Initializes the localBoard -> Starts every snake threads and the obstacle threadpool
	public void init() {
		for(Snake s:snakes)
			s.start();
		ExecutorService pool = Executors.newFixedThreadPool(NUM_SIMULTANEOUS_MOVING_OBSTACLES);
		for(Obstacle obs : super.getObstacles()) {
			ObstacleMover obsMover = new ObstacleMover(obs,this);
			pool.submit(obsMover);
		}
		setChanged();
	}

	

	@Override
	public void handleKeyPress(int keyCode) {
		// do nothing... No keys relevant in local game
	}

	@Override
	public void handleKeyRelease() {
		// do nothing... No keys relevant in local game
	}





}
