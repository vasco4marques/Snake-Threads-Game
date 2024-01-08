package game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;


import environment.Board;
import environment.BoardPosition;
import environment.Cell;
/** Base class for representing Snakes.
 * Will be extended by HumanSnake and AutomaticSnake.
 * Common methods will be defined here.
 * @author luismota
 *
 */
public abstract class Snake extends Thread implements Serializable{
	private static final int DELTA_SIZE = 10;
	protected LinkedList<Cell> cells = new LinkedList<Cell>();
	protected int size = 5;
	private int id;
	private Board board;
	private Boolean difMove = false;
	private Boolean snakeState = false;

	public void setSnakeState(Boolean bool){
		snakeState = bool;
	}

	public Boolean getSnakeState(){return snakeState;}
	

	public Snake(int id,Board board) {
		this.id = id;
		this.board=board;
	}

	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "Snake " + getName();
	}



	public int getIdentification() {
		return id;
	}
	
	
	
	
	public Boolean getDifMove(){
		return difMove;
	}

	public void setDifMove(Boolean bool){
		difMove = bool; 
	}

	public int getLength() {
		return cells.size();
	}

	public LinkedList<Cell> getCells() {
		return cells;
	}

	//Função que move a cobra
	protected synchronized void move(Cell cell) throws InterruptedException {

		Cell realCell = board.getCell(cell.getPosition());
		
		//Caso o move seja para cima do goal, o jogo acaba e é escrito na consola a cobra vencedora e o seu tamanhos
		//Caso não seja, a cobra mexe-se normalmente
		if(board.getGoalPosition().equals(realCell.getPosition())) {
			updateSnake(realCell);
			Goal goal = board.getGoalGameElement();
			goal.captureGoal();
			if(goal.getValue()==goal.MAX_VALUE){
				System.out.println("Ganhou " + this.getIdentification() + " com comprimento: " + this.getSize());
				board.setEndGame(true);
			}
			size+=goal.getValue();
			
		}else {
			updateSnake(realCell);	
		}
		board.setChanged();	
	}


	//Função que atualiza a cobra -> Dá request da nova cabeça e dá release da cauda caso seja suposto perder a mesma
	public void updateSnake(Cell cell) throws InterruptedException {
		Cell realCell = board.getCell(cell.getPosition());
		realCell.request(this);
		cells.add(realCell);
		//Caso após adicionar a nova cabeça a cobra tenha um número de cells superior ao seu tamanho, é removida a ponta da cauda
		if(cells.size()== size +1) {
			cells.poll().release();	
		}	
	}

	//returns the list of boardPositions from the snake path
	public LinkedList<BoardPosition> getPath() {
		LinkedList<BoardPosition> coordinates = new LinkedList<BoardPosition>();
		for (Cell cell : cells) {
			coordinates.add(cell.getPosition());
		}

		return coordinates;
	}	
	protected void doInitialPositioning() {
		// Random position on the first column. 
		// At startup, snake occupies a single cell
		int posX = 0;
		int posY = (int) (Math.random() * Board.NUM_ROWS);		
		BoardPosition at = new BoardPosition(posX, posY);

		try {
			board.getCell(at).request(this);
			cells.add(board.getCell(at));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.err.println("Snake "+getIdentification()+" starting at:"+getCells().getLast());		
	}

	//returns the snake board
	public Board getBoard() {
		return board;
	}


}
