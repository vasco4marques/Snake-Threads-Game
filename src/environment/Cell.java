package environment;

import java.io.Serializable;
import game.AutomaticSnake;
import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.Snake;

/** Main class for game representation. 
 * 
 * @author luismota
 *
 */
public class Cell implements Serializable {
	private BoardPosition position;
	private Snake ocuppyingSnake = null;
	private GameElement gameElement=null;
	
	public Cell(BoardPosition position) {
		
		super();
		this.position = position;
		
	}
	
	//Returns the cell Game Element
	public GameElement getGameElement() {
		return gameElement;
	}

	@Override
	public String toString() {
		return "(" + position.x + ","	 + position.y + ")";
	}

	//Returns the BoardPosition of a cell
	public BoardPosition getPosition() {
		return position;
	}

	//Connects a snake to a cell -> If cell is occupied by obstacle or another snake, the caller thread enters the wait state until notified
	// If cell is not occupied, connects itself to a snake
	public synchronized void request(Snake snake)
			throws InterruptedException {
		while(isOcupied() && snake.getSnakeState()) {
			System.out.println("ID: "+snake.getIdentification() + " Stopped");
			wait();
		}
		ocuppyingSnake=snake;	
	}

	//Becomes an empty cell -> loses its connection to a snake and notifies every snake that are in wait
	public synchronized void release() {
		//		System.out.println("Snake notified " + ocuppyingSnake.getId());
		ocuppyingSnake=null;	
		notifyAll();
	}

	//Returns true if cell is part of a snake
	public boolean isOcupiedBySnake() {
		return ocuppyingSnake!=null;
	}

	//Defines the cell gameElement
	public  void setGameElement(GameElement element) {
		// TODO coordination and mutual exclusion
		gameElement=element;
	}



	//returns if cell is ocupied (By snake or by obstacle)
	public synchronized boolean isOcupied() {
		return isOcupiedBySnake() || (gameElement!=null && gameElement instanceof Obstacle);
	}



	//returns the snake that is ocuppying the cell
	public Snake getOcuppyingSnake() {
		return ocuppyingSnake;
	}



	//removes the goal
	public  Goal removeGoal() {
		gameElement=null;
		return null;
	}

	//removes an obstacle
	public synchronized void removeObstacle() {
		gameElement=null;
		notifyAll();
	}

	//Returns the goal
	public Goal getGoal() {
		return (Goal)gameElement;
	}

	//Returns if cell is ocupied by goal
	public boolean isOcupiedByGoal() {
		return (gameElement!=null && gameElement instanceof Goal);
	}



}
