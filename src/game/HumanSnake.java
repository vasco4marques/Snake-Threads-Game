package game;

import java.io.BufferedReader;
import java.io.IOException;

import environment.Board;
import environment.Cell;
 /** Class for a remote snake, controlled by a human 
  * 
  * @author luismota
  *
  */
public class HumanSnake extends Snake {
	
  
  private transient BufferedReader buffer;



	public HumanSnake(int id,Board board,BufferedReader buffer) {
		super(id,board);
    this.buffer = buffer;
    super.setSnakeState(false);
	}

  @Override
  //Does the initial positioning and then while the game its not over listens every 200ms to the input from the client to determine
  // its next position
	public void run() {
    doInitialPositioning();
    while(!super.getBoard().getEndGame()){
      try {
        sleep(200);
        String s = buffer.readLine();
        switch (s) {
          case "LEFT":
            Cell moveLeft = new Cell(this.getCells().getLast().getPosition().getCellLeft());
            this.move(moveLeft);
            break;
          case "UP":
            Cell moveUp = new Cell(this.getCells().getLast().getPosition().getCellAbove());
            this.move(moveUp);
            break;
          case "RIGHT":
            Cell moveRight = new Cell(this.getCells().getLast().getPosition().getCellRight());
            this.move(moveRight);
            break;
          case "DOWN":
            Cell moveDown = new Cell(this.getCells().getLast().getPosition().getCellBelow());
            this.move(moveDown);
            break;
        }
      } catch (InterruptedException |IOException e) {
        if(e instanceof IOException) {
          System.err.println("Server down");
          break;
        }else {
          System.err.println("Interrupted");
        }
      }
    }
  }
  

  @Override
  //Checks if the position is inside the board or if it is occupied and if everything is good it will move to that position
  public void move(Cell cell){ 
    if(super.getBoard().insideBoard(cell)&&!super.getBoard().getCell(cell.getPosition()).isOcupied()){
      try {
        super.move(cell);
      } catch (InterruptedException e) {
        System.out.println("Got interrupted while moving");
      }
    }
  }




}
