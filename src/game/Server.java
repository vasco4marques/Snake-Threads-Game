package game;

import remote.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.lang.Math;

import environment.Cell;
import environment.LocalBoard;

public class Server extends Thread {

	private HashMap<Integer,ObjectOutputStream> clientes = new HashMap(); //Utilizado para guardar o ID do cliente e o ObjectOutputStream
	// de cada um
	private LocalBoard localBoard;
	public static final int PORTO = 8080;
	int humanSnakeID = 0;
	private Boolean firstClient = true;

	public Server(LocalBoard local) {
		localBoard = local;
	}

	//Função chamada após a receção de um cliente
	private void handleClient(Socket socket) throws IOException, InterruptedException {
		System.out.println("Handling Client");
		
		//Faz as conexões de saída e entrada em relação ao cliente
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		
		//Criaçao da cobra humana, atualização do HashMap e incremento do ID
		// A Human Snake é uma thread e a mesma é responsável por tratar do input vindo do cliente
		HumanSnake client = new HumanSnake(humanSnakeID, localBoard, in);
		localBoard.addSnake(client);
		client.start();
		clientes.put(humanSnakeID,out);
		humanSnakeID++;
		
		//Caso seja o primeiro cliente, é criada uma thread que controla o output do servidor
		if (firstClient) {
			new Thread(() -> {
				try {
					handleOut();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
			firstClient = false; //Variavel de controlo posta a false após a criação do primeiro cliente
		}
	}

	//Trata de enviar para todos os clientes (para os seus respetivos ObjectOutputStream) o estado do jogo
	private void handleOut() throws InterruptedException {
		while (true) {
			Thread.sleep(localBoard.REMOTE_REFRESH_INTERVAL);
			for (ObjectOutputStream out : clientes.values())
				try{
					sendObject(out, localBoard);
				}catch(IOException  e){
					//CASO UM CLIENTE SE DESLIGUE
					cancelClient(out);		
				}
			localBoard.setChanged();
		}
	}

	//Caso um cliente se desconecte, o servidor vai receber uma IOException visto que está a tentar enviar para algo que não existe
	// e assim tratamos deste cliente que saiu
	private void cancelClient(ObjectOutputStream out) {
		int snakeID = -1;
		for(int i : clientes.keySet()){
			if(clientes.get(i).equals(out)){
				snakeID=i;
			}
		}
		//Ao encontrar o ID da cobra referente ao cliente que se desligou vamos removê-la da board
		if(snakeID != -1)
			localBoard.removeHumanSnake(snakeID);			
	}



	//Escreve o objeto no ObjectOutputStream, dá reset e força o envio
	private void sendObject(ObjectOutputStream out, LocalBoard board) throws IOException {
		out.writeObject(board);
		out.reset(); // Atualizar referências, sem isto o remote recebe sempre os dados referentes ao
						// primeiro envio
		out.flush();
	}

	@Override
	//A thread main do servidor faz a aceitação dos clientes e trata do seu handle
	public void run() {
		System.out.println("Servido*r Ligado");
		ServerSocket ss;
		try { //Apanha as exceções
			ss = new ServerSocket(PORTO);
			try {
				while (true) {
					Socket socket = ss.accept();
					System.out.println("Cliente ligado ao servidor");
					handleClient(socket);
				}
			} finally {
				ss.close();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	
	//GETTERS e SETTERS 
	
	public LocalBoard getLocalBoard() {
		return localBoard;
	}

	public void setLocalBoard(LocalBoard localBoard) {
		this.localBoard = localBoard;
	}

	public static int getPorto() {
		return PORTO;
	}

	public int getHumanSnakeID() {
		return humanSnakeID;
	}

	public void setHumanSnakeID(int humanSnakeID) {
		this.humanSnakeID = humanSnakeID;
	}

}
