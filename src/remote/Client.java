package remote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import environment.Board;
import game.Server;
import gui.SnakeGui;

public class Client extends Thread {
	// PRECISA DE 2 THREADS - 1 envia teclas e 1 recebe a board

	private boolean gameRunning = false;
	private long ID;
	private ObjectInputStream in;
	private PrintWriter out;
	private Socket socket;
	private RemoteBoard remote;

	public Client(RemoteBoard remote) {
		this.remote = remote;
	}

	//Realiza as conexões ao servidor
	void connectToServer() throws IOException {
		InetAddress endereco = InetAddress.getByName(null);
		System.out.println("Endereco:" + endereco);
		socket = new Socket(endereco, Server.PORTO);
		System.out.println("Socket:" + socket);
		
		
		in = new ObjectInputStream(socket.getInputStream());
		out = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(socket.getOutputStream())),
				true);
	}

	//Função que dá handle ao servidor
	private void handleServer() {
		
		//Cria uma thread que trate de receber o estado do jogo vindo do servidor
		new Thread(() -> {
			try {
				handleIn();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		//A thread main do cliente trata de enviar para o servidor um representante dos inputs do cliente
		try {
			handleOut();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}


	//Função que trata do envio
	// Vai buscar à remote board a direção e envia-a ao servidor a cada 200ms (REMOTE_REFRESH_INTERVAL)
	private void handleOut() throws InterruptedException {
		while (true) {
			Thread.sleep(remote.REMOTE_REFRESH_INTERVAL);
			String toBeSent = remote.getDirection();
			if (toBeSent != "none") {
				sendString(out, toBeSent);
				System.out.println("Client sent direction");
			}

		}
	}

	//Escreve a string no PrintWriter e força o envio
	private void sendString(PrintWriter out2, String direction) {
		out2.println(direction);
		out2.flush();
	}



	//Trata de recever o estado do jogo do servidor
	private void handleIn() throws ClassNotFoundException, IOException {
		while (true) {
			Board recieve = (Board) in.readObject();
			if (recieve != null) {
				remote.updateBoard(recieve); //Atualiza a sua board remota com o estado do jogo vindo do cliente
				if (!gameRunning) {
					//Caso o jogo ainda não esteja a correr inicializa a gui
					SnakeGui gui = new SnakeGui(remote, 600, 0);
					gui.init();
					gameRunning = true;
				}
			}
		}
	}
	
	
	@Override
	//Cliente conecta-se ao servidor e trata tanto do recebimento como do envio de informação para o mesmo
	public void run() {
		System.out.println("Cliente Ligado");
		try {
			System.out.println("Cliente tenta ligar...");
			connectToServer(); //Trata das ligações ao servidor
			System.out.println("Ligado");
			handleServer(); // Trata do tráfego entre si e o servidor
		} catch (IOException e) {
			e.printStackTrace();
		} finally {// a fechar...
			try {
				socket.close();
			} catch (IOException e) {// ...
			}
		}
	}

	//Inicializa o cliente e dá start à sua thread
	public static void main(String[] args) throws IOException {
		new Client(new RemoteBoard()).start();
	}
}
