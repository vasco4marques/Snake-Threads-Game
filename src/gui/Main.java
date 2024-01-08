package gui;

import java.io.IOException;
import environment.LocalBoard;
import game.Server;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		//Inicialização do jogo local
		LocalBoard board=new LocalBoard();
		SnakeGui game = new SnakeGui(board,600,0);
		game.init();

		//Inicialização do servidor que permite jogo remoto
		Server server = new Server(board);
		server.start();
	}
}

