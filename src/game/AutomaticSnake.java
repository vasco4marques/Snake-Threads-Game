package game;

import java.util.Collections;
import java.util.List;

import environment.LocalBoard;
import environment.Cell;
import environment.Board;
import environment.BoardPosition;

public class AutomaticSnake extends Snake {

	public AutomaticSnake(int id, LocalBoard board) {
		super(id, board);
		super.setSnakeState(true);
	}

	@Override
	//Does the initial positioning and then unitl the game is over selects a new position based on the smallest distance to the goal
	//If interrupted by the button will choose a random cell to go to
	public void run() {

		doInitialPositioning();
		// TODO: automatic movement

		while (!super.getBoard().getEndGame()) {
			Cell next = nextMove();
			// System.out.println("ID: " + super.getIdentification() + " pos: " + next + " gEl "
					// + super.getBoard().getCell(next.getPosition()).getGameElement() + " OccSnake: "
					// + super.getBoard().getCell(next.getPosition()).getOcuppyingSnake());
			try {
				super.move(next);
				super.sleep(Board.PLAYER_PLAY_INTERVAL);
			} catch (InterruptedException e) {
				System.out.println("Tried to stop me");
			}
		}
	}




	//Returns the next snake move -> If difMove is true, it will return a random position
	// If difMove is false it will calculate the next move based on the smallest distance to the goal
	public Cell nextMove() {
		Board board = super.getBoard();
		BoardPosition goal = board.getGoalPosition();
		System.out.println("Goal " + goal);
		
		while (goal == null) // Garante que não é null
			goal = board.getGoalPosition();
		Cell head = this.cells.getLast();
		List<BoardPosition> positions = board.getNeighboringPositions(head);
		double[] valores = new double[positions.size()];
		int i = 0;
		
		//Caso seja clicado no botão entra neste loop e retorna uma cell random das vizinhas à cabeça que não esteja ocupdada
		if (super.getDifMove()) {
			super.setDifMove(false);
			List<BoardPosition> lista = board.getNeighboringPositions(head);
			Collections.shuffle(lista);
			for (BoardPosition a : lista) {
				Cell newCell = board.getCell(a);
				if (!newCell.isOcupied())
					return newCell;
			}

		}
		
		//Preenche o array com as distâncias das posições até ao golo
		for (BoardPosition pos : positions) {
			if (board.getCell(pos).getOcuppyingSnake() == this)
				valores[i] = Double.MAX_VALUE;
			else
				valores[i] = pos.distanceTo(goal);
			i++;
		}

		//Escolha da menor distância do array
		double min = Double.MAX_VALUE;
		int index = 0;
		for (int j = 0; j < valores.length; j++) {
			if (valores[j] <= min) {
				min = valores[j];
				index = j;
			}

		}

		return new Cell(positions.get(index));

	}

}
