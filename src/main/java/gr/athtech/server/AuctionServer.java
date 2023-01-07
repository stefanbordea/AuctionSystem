package gr.athtech.server;

import gr.athtech.auction.Auction;
import gr.athtech.client.ClientHandler;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuctionServer {
	// Map to store auctions
	@Getter
	private static final Map<Integer, Auction> auctions = new HashMap<>();
	// Next available ID for auction
	private static int nextId = 0;

	// Get next available ID for auction
	public static int getNextId() {
		return nextId++;
	}

	// Add auction to list of active auctions
	public static void addAuction(Auction auction) {
		auctions.put(auction.getId(), auction);
	}

	// Get list of active client handlers
	public static List<ClientHandler> getHandlers() {
		List<ClientHandler> handlers = new ArrayList<>();
		for (Map.Entry<Integer, Auction> entry : auctions.entrySet()) {
			Auction auction = entry.getValue();
			for (Map.Entry<String, ClientHandler> handlerEntry : auction.getHandlers().entrySet()) {
				ClientHandler handler = handlerEntry.getValue();
				handlers.add(handler);
			}
		}
		return handlers;
	}

	public static void main(String[] args) {
		// Create server socket for accepting incoming connections
		try (ServerSocket serverSocket = new ServerSocket(3000)) {

			// Create thread pool for handling client requests concurrently
			ExecutorService pool = Executors.newFixedThreadPool(10);

			while (true) {
				// Listen for incoming connection and create a new thread for each incoming connection
				Socket socket = serverSocket.accept();
				ClientHandler handler = new ClientHandler(socket);
				pool.execute(handler);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
