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

	public void placeItemForAuction() throws Exception{
		try{
			Scanner scanner = new Scanner(System.in);
			AuctionItem auctionItem = new AuctionItem();

				System.out.println("Specify item name: ");
				auctionItem.setName(scanner.nextLine().trim());
				System.out.println("Specify item description: ");
				auctionItem.setDescription(scanner.nextLine().trim());
				System.out.println("Specify item starting price: ");
				auctionItem.setStartingPrice(Double.valueOf(scanner.nextLine().trim()));
			do{
				System.out.println("Would you like to set a timer for your auction? (Y/N): ");
				auctionItem.setName(scanner.nextLine().toLowerCase().trim());
				switch(scanner.nextLine()){
					case "y":
						break;

				}
			}while (!scanner.hasNextLine() || !scanner.hasNext("[yn]"));


		} catch (Exception e){

		}

	}



	public static void main(String[] args) throws Exception {

		AuctionClient ac = new AuctionClient();

		ac.connectToAuctionServer();
		ac.placeItemForAuction();

		//		Scanner scan = new Scanner(System.in);
		//		Socket s = new Socket("localhost", 10000);
		//		DataOutputStream dout = new DataOutputStream(s.getOutputStream());
		//		DataInputStream dis = new DataInputStream(s.getInputStream());
		//		while (true) {
		//			System.out.println("Write Your message");
		//			String str = scan.nextLine();
		//			dout.writeUTF(str);
		//			dout.flush();
		//			if (str.equals("bye")) {
		//				dout.close();
		//				s.close();
		//				break;
		//			}
		//
		//		}
	}
}
