package gr.athtech.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AuctionServer extends Thread {

	public static int SERVER_PORT = 10000;
	private ServerSocket serverSocket;
	private ObjectInputStream inputFromClient;
	private ObjectOutputStream outputToClient;
	private boolean serverRunning;

	public static void main(String[] args) {
		AuctionServer s = new AuctionServer();
		s.startServer();
	}

	public boolean startServer() {
		serverRunning = false;
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			serverRunning = true;
			System.out.println("Auction server online");
			start();
		} catch (IOException e) {
			System.out.println("Error starting server: " + e.getMessage());
		}
		return serverRunning;
	}

	public boolean stopServer() {
		try {
			if (serverRunning) {
				serverSocket.close();
				serverRunning = false;
				System.out.println("Auction server offline");
			}
		} catch (IOException e) {
			System.out.println("Error stopping server: " + e.getMessage());
		}
		return serverRunning;
	}

	public void run() {
		while (serverRunning) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				System.out.println("Welcome " + socket.getInetAddress() + " !");
			} catch (IOException e) {
				System.out.println("Cannot establish connection to client: " + e.getMessage());
			}

			try {
				if (socket != null) {
					inputFromClient = new ObjectInputStream(socket.getInputStream());
					outputToClient = new ObjectOutputStream(socket.getOutputStream());
				}
				try {
					String clientResponse;
					clientResponse = (String) inputFromClient.readObject();
					System.out.println("Received from client: " + clientResponse);

					if (clientResponse != null) {
						char choice = clientResponse.charAt(0);
						switch (choice) {
							// put a menu here?
						}
					}
				} catch (IOException | ClassNotFoundException e) {
					System.out.println(e.getMessage());
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
