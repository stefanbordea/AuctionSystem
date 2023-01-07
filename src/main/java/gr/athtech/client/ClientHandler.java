package gr.athtech.client;

import gr.athtech.auction.Auction;
import gr.athtech.server.AuctionServer;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ClientHandler implements Runnable {
	private static final String INVALID_ID = "Invalid auction ID";
	// Socket for communication with client
	private Socket socket;
	// List of auctions the client is registered in
	private List<Integer> registeredAuctions;

	public ClientHandler(Socket socket) {
		this.socket = socket;
		registeredAuctions = new ArrayList<>();
	}

	public ClientHandler() {
	}

	// Send message to client
	public void sendMessage(String message) {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Check if client is registered in an auction
	public boolean isRegistered(int auctionId) {
		return registeredAuctions.contains(auctionId);
	}

	public Socket getSocket() {
		return socket;
	}

	public boolean checkForValidID(PrintWriter out, String[] args, int pos) {
		if (!AuctionServer.getAuctions().containsKey(Integer.parseInt(args[pos]))) {
			out.println(INVALID_ID);
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

			// Send welcome message to client
			out.println("Welcome to the auction server " + socket.getInetAddress().getHostAddress() + "!");

			// Process requests from client
			while (true) {
				if (in.ready()) {
					// Read request from client
					String request = in.readLine();

					if (request == null) {
						// Client has disconnected
						break;
					}

					// Split request into command and arguments
					String[] parts = request.split(" ");
					String command = parts[0];
					String[] args = new String[parts.length - 1];
					System.arraycopy(parts, 1, args, 0, parts.length - 1);

					System.out.println("command: " + command + " " + LocalDateTime.now());

					// Process request
					switch (command) {
						case "advertise":
							// Create new auction and add it to active auctions
							Auction auction = new Auction(AuctionServer.getNextId(), args[0], args[1],
														  Double.parseDouble(args[2]), args[3]);
							if (args[3].equals("time")) {
								auction.setClosingTime(Integer.parseInt(args[4]));
							} else if (!args[3].equals("bid")) {
								out.println("Invalid closing type");
								break;
							}
							AuctionServer.addAuction(auction);

							// Set seller IP for auction
							auction.setSellerIp(socket.getInetAddress().getHostAddress());

							// Send auction ID to client
							out.println(auction.getId());
							break;
						case "list":
							// Get list of active auctions
							StringBuilder sb = new StringBuilder();
							for (Auction activeAuction : AuctionServer.getAuctions().values()) {
								sb.append("Auction ID: ").append(activeAuction.getId()).append(" ").append(
										  "Item name: ").append(activeAuction.getItemName()).append(" ").append(
										  "Item description: ").append(activeAuction.getItemDescription()).append(" ")
								  .append("Starting price: ").append(activeAuction.getStartingPrice()).append(" ")
								  .append("Highest bid: ").append(activeAuction.getHighestBid()).append(" ").append(
										  "Seller ID: ").append(activeAuction.getSellerIp()).append("\n");
							}
							out.println(sb);
							break;
						case "register":
							// Read auction ID from client
							int auctionId = Integer.parseInt(args[0]);

							// Check if auction ID is valid
							if (checkForValidID(out, args, 0)) {
								break;
							}

							// Get auction
							auction = AuctionServer.getAuctions().get(auctionId);

							// Check if auction exists
							if (auction == null) {
								out.println("Auction does not exist");
								break;
							}

							// Check if client is the seller for specified auction
							if (auction.getSellerIp().equals(socket.getInetAddress().getHostAddress())) {
								out.println("You cannot register in your own auction");
								break;
							}

							// Check if client is already registered in auction
							if (isRegistered(auctionId)) {
								out.println("You are already registered in this auction");
								break;
							}

							// Register client in auction
							auction.addClient(this);
							registeredAuctions.add(auctionId);
							out.println("You have been registered in the auction " + auctionId);
							break;
						case "bid":
							// Check if auction ID is valid
							if (checkForValidID(out, args, 0)) {
								break;
							}

							// Check if client is registered in the auction
							if (!isRegistered(Integer.parseInt(args[0]))) {
								out.println("You are not registered in this auction");
								break;
							}

							// Get auction and current highest bid
							auction = AuctionServer.getAuctions().get(Integer.parseInt(args[0]));
							double highestBid = auction.getHighestBid();

							// Check if bid is higher than current highest bid
							if (Double.parseDouble(args[1]) <= highestBid) {
								out.println("Bid must be higher than the current highest bid");
								break;
							}

							// Update highest bid and bidder IP
							auction.setHighestBid(Double.parseDouble(args[1]));
							auction.setBidderIp(socket.getInetAddress().getHostAddress());
							// Notify all clients about new bid
							for (ClientHandler handler : AuctionServer.getHandlers()) {
								handler.sendMessage("NEW BID for auction with ID: " + auction.getId() + " for ITEM " +
															auction.getItemName() + " BID: " + args[1] + " BIDDER: " +
															socket.getInetAddress().getHostAddress());
							}

							// Reset timer for "going once" and "going twice" messages
							if (auction.getClosingType().equals("bid")) {
								auction.setTimer(System.currentTimeMillis() + 300000);
							}
							break;
						case "check":
							// Get auction
							auction = AuctionServer.getAuctions().get(Integer.parseInt(args[0]));

							// Check if auction exists
							if (auction == null) {
								out.println("Auction does not exist");
								break;
							}

							// Get highest bid
							highestBid = auction.getHighestBid();

							// Send highest bid and server time to client
							out.println("Highest Bid: " + highestBid + " timestamp: " + auction.getTime());
							break;
						case "withdraw":
							// Check if client is registered in the auction
							if (!isRegistered(Integer.parseInt(args[0]))) {
								out.println("You are not registered in this auction");
								break;
							}

							// Check if client is the highest bidder
							auction = AuctionServer.getAuctions().get(Integer.parseInt(args[0]));
							if (auction.getBidderIp().equals(socket.getInetAddress().getHostAddress())) {
								out.println("You are the highest bidder and cannot withdraw from the auction");
								break;
							}

							// Remove client from auction
							registeredAuctions.remove(Integer.parseInt(args[0]));
							out.println("You have been withdrawn from the auction");
							break;
						case "disconnect":
							// Check if client is the highest bidder in any auction
							for (Integer id : registeredAuctions) {
								auction = AuctionServer.getAuctions().get(id);
								if (auction.getBidderIp().equals(socket.getInetAddress().getHostAddress())) {
									out.println("You are the highest bidder in auction " + id +
														" and cannot disconnect from the server");
									return;
								}
							}

							// Disconnect client
							out.println("Goodbye!");
							socket.close();
							break;
						default:
							out.println("Invalid command");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
