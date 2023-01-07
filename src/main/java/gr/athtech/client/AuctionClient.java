package gr.athtech.client;

import gr.athtech.auction.AuctionItem;
import gr.athtech.auction.Bidder;
import gr.athtech.server.AuctionServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class AuctionClient {

	private Socket socket;
	private ObjectInputStream inputStreamFromServer;
	private ObjectOutputStream outputStreamToServer;
	private Object serverReply;
	private Bidder bidder;

	private boolean connectToAuctionServer() {
		try {
			socket = new Socket(InetAddress.getLocalHost(), AuctionServer.SERVER_PORT);
			outputStreamToServer = new ObjectOutputStream(socket.getOutputStream());
			inputStreamFromServer = new ObjectInputStream(socket.getInputStream());
			return true;
		} catch (IOException e) {
			System.out.println("CLIENT: Error Connecting to Server" + e.getMessage());
			return false;
		}
	}

	private boolean disconnectFromAuctionServer() {
		try {
			socket.close();
			return true;
		} catch (IOException e) {
			System.out.println("CLIENT: Error Disconnecting from Server" + e.getMessage());
			return false;
		}
	}

	public void displayMenu() throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.println("What would you like to do?");
		System.out.println("1) Advertise an item for auction");
		System.out.println("2) List active auctions");
		System.out.println("3) Register in an auction");
		System.out.println("4) Place a bid");
		System.out.println("5) Check highest bid");
		System.out.println("6) Withdraw from an auction");
		System.out.println("7) Disconnect from server");
		int choice = scanner.nextInt();

		switch (choice) {
			case 1:
				this.placeItemForAuction();
				break;
			case 2:

				break;
			case 3:

				break;
			case 4:

				break;
			case 5:

				break;
			case 6:

				break;
			case 7:
				socket.close();
				break;
			default:
				System.out.println("Invalid command");
		}

	}

	public void placeItemForAuction() throws Exception {
		try {
			Scanner scanner = new Scanner(System.in);
			AuctionItem auctionItem = new AuctionItem();

			System.out.println("Specify item name: ");
			auctionItem.setName(scanner.nextLine().trim());
			System.out.println("Specify item description: ");
			auctionItem.setDescription(scanner.nextLine().trim());
			System.out.println("Specify item starting price: ");
			auctionItem.setStartingPrice(Double.valueOf(scanner.nextLine().trim()));
			do {
				System.out.println("Would you like to set a timer for your auction? (Y/N): ");
				auctionItem.setName(scanner.nextLine().toLowerCase().trim());
				if ("y".equals(scanner.nextLine())) {
				}
			} while (!scanner.hasNextLine() || !scanner.hasNext("[yn]"));

		} catch (Exception e) {

		}

	}

	public static void main(String[] args) throws Exception {

		AuctionClient ac = new AuctionClient();

		ac.connectToAuctionServer();
		ac.displayMenu();
	}
}
