package environment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.io.Serializable;
import game.GameElement;
import game.Goal;
import game.HumanSnake;
import game.Obstacle;
import game.Snake;

public abstract class Board extends Observable implements Serializable {
	protected Cell[][] cells;
	private transient BoardPosition goalPosition;
	public transient static final long PLAYER_PLAY_INTERVAL = 200;
	public transient static final long REMOTE_REFRESH_INTERVAL = 200;
	public transient static final int NUM_COLUMNS = 30;
	public transient static final int NUM_ROWS = 30;
	protected LinkedList<Snake> snakes = new LinkedList<Snake>();
	private LinkedList<Obstacle> obstacles= new LinkedList<Obstacle>();
	protected boolean isFinished;
	private boolean endGame = false;
	
	public Board() {
		cells = new Cell[NUM_COLUMNS][NUM_ROWS];
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				cells[x][y] = new Cell(new BoardPosition(x, y));
			}
		}
	}

	//GETTERS 
	public LinkedList<Snake> getSnakes(){
		return snakes;
	}

	public LinkedList<Obstacle> getObstacles(){
		return obstacles;
	}

	public Cell[][] getCells() {
		return cells;
	}

	public BoardPosition getGoalPosition() {
		return goalPosition;
	}

	public Boolean getIsFinished(){
		return isFinished;
	}

	public Boolean getEndGame(){
		return endGame;
	}

	//SETTERS

	public void setSnakes(LinkedList<Snake> snakes){
		this.snakes = snakes;
	}

	public void setObstacles(LinkedList<Obstacle> obstacles){
		this.obstacles = obstacles;
	}
	
	public void setCells(Cell[][] cells){
		this.cells = cells;
	}

	public void setGoalPosition(BoardPosition goalPosition) {
		this.goalPosition = goalPosition;
	}
	

	//Removes human snake by ID -> releases its cells and removes from snake list
	public void removeHumanSnake(int snakeID){
		Snake toBeRemoved = null;
		for(Snake snake : snakes){
			if(snake instanceof HumanSnake && snake.getIdentification()==snakeID){
				for(Cell cell : snake.getCells()){
					getCell(cell.getPosition()).release();
				}
				toBeRemoved = snake;
			}
		}
		snakes.remove(toBeRemoved);
	}

	//Updates the stats of this from the argument board
	public void updateBoard(Board board){
		this.cells=board.getCells();
		this.snakes=board.getSnakes();
		this.obstacles=board.getObstacles();
		this.goalPosition = board.getGoalPosition();
		this.isFinished = board.getIsFinished();
		this.endGame = board.getEndGame();
		setChanged();
	}
	
	//Returns the cell at BoardPosition x,y
	public  Cell getCell(BoardPosition cellCoord) {
		return cells[cellCoord.x][cellCoord.y];
	}

	
	
	//Returns random BoardPosition
	protected BoardPosition getRandomPosition() {
		return new BoardPosition((int) (Math.random() *NUM_ROWS),(int) (Math.random() * NUM_COLUMNS));
	}

	//Defines endGame variable -> endGame defines if the game is over or not
	public void setEndGame(Boolean bool){endGame = bool;}
	

	//returns the goal gameElement from the goal cell
	public Goal getGoalGameElement() {
		return (Goal) getCell(getGoalPosition()).getGameElement();
	}
	
	
	//removes the goal gameElement from the cell and sets the board goal position to null	
	public void removeActualGoal(){
		getCell(goalPosition).setGameElement(null);
		this.goalPosition=null;
	}
	

	
	
	//adds the goal (as an gameElement) to a random BoardPosition
	public void addGameElement(GameElement gameElement) {
		boolean placed=false;
		while(!placed) {
			BoardPosition pos=getRandomPosition();
			if(!getCell(pos).isOcupied() && !getCell(pos).isOcupiedByGoal()) {
				getCell(pos).setGameElement(gameElement);
				if(gameElement instanceof Goal) {
					setGoalPosition(pos);
//					System.out.println("Goal placed at:"+pos);
				}else if (gameElement instanceof Obstacle) {
					((Obstacle)gameElement).setBoardPosition(pos);
				}
				placed=true;
			}
		}
	}

	
	
	//Returns a list of Neighboring Positions of cell (up,down,left and right)
	public List<BoardPosition> getNeighboringPositions(Cell cell) {
		ArrayList<BoardPosition> possibleCells=new ArrayList<BoardPosition>();
		BoardPosition pos=cell.getPosition();
		if(pos.x>0)
			possibleCells.add(pos.getCellLeft());
		if(pos.x<NUM_COLUMNS-1)
			possibleCells.add(pos.getCellRight());
		if(pos.y>0)
			possibleCells.add(pos.getCellAbove());
		if(pos.y<NUM_ROWS-1)
			possibleCells.add(pos.getCellBelow());
		return possibleCells;

	}

	//returns true if cell is inside the board boarders
	public Boolean insideBoard(Cell cell){
		BoardPosition pos = cell.getPosition();
		return pos.x>=0 && pos.x<=NUM_COLUMNS-1 && pos.y>=0 && pos.y<=NUM_ROWS-1;
	}

	//Interrupts all snakes -> If bool = true, the snake will move to a random position (its only true when the button is pressed)
	// If bool = false the snakes will just interrupt and stop (when the game ends)
	public void interruptSnakes(Boolean bool) {
		for(Snake s : snakes) {
			s.setDifMove(bool);
			s.interrupt();
		}
	}
	
	//Adds a new goal
	protected Goal addGoal() {
		Goal goal=new Goal(this);
		addGameElement(goal);
		return goal;
	}
	
	//Used to add new snakes to the board (Used in the server to add the new Human Snakes)
	public void addSnake(Snake snake) {
		snakes.add(snake);
	}
	
	
	//Adds obstacles
	protected void addObstacles(int numberObstacles) {
		// clear obstacle list , necessary when resetting obstacles.
		getObstacles().clear();
		while(numberObstacles>0) {
			Obstacle obs=new Obstacle(this);
			addGameElement( obs);
			getObstacles().add(obs);
			numberObstacles--;
		}
	}
	
	//Removes obstacle, decrements the value and places it again
	public synchronized void renewObstacle (GameElement obstacle) {
		Obstacle obs = ((Obstacle)obstacle);
		obs.decrementRemainingMoves();
		getCell(obs.getBoardPosition()).removeObstacle();
		addGameElement(obstacle);
	
	}
	

	//DEBUG func
	//From all snakes prints the head position in console
	public void printHeads(){
		for(Snake a : snakes){
			System.out.println("Snake :" + a.getIdentification() + " head: " + a.getCells().getLast());
		}
	}

	//Ends the game by interrupting all snake threads
	public void endGame() {
		interruptSnakes(false);
	}
	

	@Override
	public void setChanged() {
		super.setChanged();
		notifyObservers();
	}

	
	public abstract void init(); 
	
	public abstract void handleKeyPress(int keyCode);

	public abstract void handleKeyRelease();

}
