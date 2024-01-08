package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.LinkedList;

import environment.LocalBoard;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import game.Goal;
import game.Obstacle;
import game.Snake;

/**
 * Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Srver.
 * Only for part II of the project.
 * 
 * @author luismota
 *
*/

enum directions { //Enum para as direções possíveis
	UP,DOWN,LEFT,RIGHT
}

public class RemoteBoard extends Board {
	private directions direction = null;

	@Override
	//Quando uma tecla é premida a variavle global da classe assume essa direção
	public void handleKeyPress(int keyCode) {
		switch(keyCode){
			case 37: direction = directions.LEFT;break;
			case 38: direction = directions.UP;	break;
			case 39: direction = directions.RIGHT;	break;
			case 40: direction = directions.DOWN;	break;
		}
	}

	//Utilizado pelo cliente para enviar a string correspondente à direção correta
	public String getDirection(){
		if(direction!=null)
			return direction.name();
		else
			return "none";
		
	}


	@Override
	//Ao largar a tecla a mesma passa a null visto que não está a ser selecionada nenhuma direção
	public void handleKeyRelease() {
		direction = null;
	}

	@Override
	public void init() {
	}

}
