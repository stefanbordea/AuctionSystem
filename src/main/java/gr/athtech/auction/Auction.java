package gr.athtech.auction;

import lombok.Data;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Data
public class Auction {

	private String id;
	private AuctionItem auctionItem;
	private Bid highestBid;
	private InetAddress sellerAddress;

	//Generate a random ID for the auctioned item
	private String generateID(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i <= length; i++) {
			int index = (int) (characters.length() * Math.random());
			sb.append(characters.charAt(index));
		}
		return sb.toString();
	}

	public Auction(final AuctionItem auctionItem, final Bid highestBid) throws UnknownHostException {
		this.id = generateID(10);
		this.auctionItem = auctionItem;
		this.highestBid = highestBid;
		this.sellerAddress = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
	}
}
