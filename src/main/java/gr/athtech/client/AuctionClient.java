package gr.athtech.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class AuctionClient {
	private static final String INPUT_AUCTION_ID = "Enter auction ID:";

	public static void main(String[] args) {
		// Connect to the server
		try (Socket socket = new Socket(InetAddress.getLocalHost().getHostAddress(), 3000);
			 // Set up input and output streams for sending and receiving data
			 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

			// Receive welcome message from server
			System.out.println(in.readLine());

			// Set up scanner for reading user input
			Scanner scanner = new Scanner(System.in);

			while (true) {
				// Prompt user for action
				System.out.println("Enter command (advertise, list, register, bid, check, withdraw, disconnect):");
				String command = scanner.nextLine().trim();

				// Process response from server
				switch (command) {
					case "advertise":
						// Read item name, description, starting price, and closing type from user
						System.out.println("Enter item name:");
						String itemName = scanner.nextLine().replace(" ", "_");
						System.out.println("Enter item description:");
						String itemDescription = scanner.nextLine().replace(" ", "_");
						System.out.println("Enter starting price:");
						double startingPrice = scanner.nextDouble();
						scanner.nextLine(); // consume newline character
						System.out.println("Enter closing type (time or bid):");
						String closingType = scanner.nextLine().toLowerCase().trim();
						if (closingType.equals("time") || closingType.equals("bid")) {
							System.out.println("Enter closing time in minutes:");
							int closingTime = scanner.nextInt();
							scanner.nextLine(); // consume newline character
							// Send command and item details to server
							out.println(command + " " + itemName + " " + itemDescription + " " + startingPrice + " " +
												closingType + " " + closingTime);
						}
						// Receive auction ID from server
						int auctionId = Integer.parseInt(in.readLine());
						System.out.println("Auction ID: " + auctionId);
						break;
					case "list":
						out.println(command);
						// Receive list of active auctions from server
						String line;
						while ((line = in.readLine()) != null && !line.isEmpty()) {
							String auction = line;
							System.out.println(auction);
						}
						break;
					case "register":
						// Read auction ID from user
						System.out.println(INPUT_AUCTION_ID);
						int id = scanner.nextInt();
						scanner.nextLine(); // consume newline character

						// Send auction ID to server
						out.println(command + " " + id);

						// Receive registration confirmation from server
						String confirmation = in.readLine();
						System.out.println(confirmation);
						break;
					case "bid":
						// Read auction ID and bid amount from user
						System.out.println(INPUT_AUCTION_ID);
						id = scanner.nextInt();
						scanner.nextLine(); // consume newline character
						System.out.println("Enter bid amount:");
						double bidAmount = scanner.nextDouble();
						scanner.nextLine(); // consume newline character
						out.println(command + " " + id + " " + bidAmount);
						// Read response from server
						String response = in.readLine();
						System.out.println(response);
						break;
					case "check":
						// Read auction ID from user
						System.out.println(INPUT_AUCTION_ID);
						id = scanner.nextInt();
						scanner.nextLine(); // consume newline character

						// Send auction ID to server
						out.println(command + " " + id);

						// Receive highest bid and server time from server
						String highestBid = in.readLine();
						System.out.println(highestBid);
						break;
					case "withdraw":
						// Read auction ID from user
						System.out.println(INPUT_AUCTION_ID);
						id = scanner.nextInt();
						scanner.nextLine(); // consume newline character

						// Send command and auction ID to server
						out.println(command + " " + id);

						// Receive response from server
						response = in.readLine();
						System.out.println(response);
						break;
					case "disconnect":
						out.println(command);
						// Receive goodbye message from server
						System.out.println(in.readLine());
						return;
					default:
						out.println(command);
						break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
