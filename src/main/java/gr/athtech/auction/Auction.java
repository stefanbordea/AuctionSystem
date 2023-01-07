package gr.athtech.auction;

import gr.athtech.client.ClientHandler;
import gr.athtech.server.AuctionServer;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Data
public class Auction {
	// Auction ID
	private final int id;

	// Item details
	private final String itemName;
	private final String itemDescription;
	private final double startingPrice;
	private final Map<String, ClientHandler> handlers;
	// Auction details
	private double highestBid;
	private String sellerIp;
	private String bidderIp;
	private String highestBidderIp;
	private Date time;
	private Long timer;
	private String closingType;
	private int closingTime;

	public Auction(int id, String itemName, String itemDescription, double startingPrice, String closingType) {
		this.id = id;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		this.startingPrice = startingPrice;
		this.closingType = closingType;
		this.highestBid = startingPrice;
		this.sellerIp = null;
		this.bidderIp = null;
		this.time = new Date();
		this.handlers = new HashMap<>();
	}

	public void addClient(ClientHandler handler) {
		handlers.put(handler.getSocket().getLocalAddress().getHostAddress(), handler);
	}

	// Method for closing the auction after a specified amount of time
	public void closeOnTimeExpiry() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Send message to all registered clients that the auction has closed
				for (ClientHandler handler : getHandlers().values()) {
					handler.sendMessage("Auction " + id + " has closed. Item " + itemName + " was sold for " + highestBid + " to participant " + highestBidderIp);
				}
				// Remove auction from active auctions
				AuctionServer.getAuctions().remove(id);
			}
		}, closingTime * 60000L);
	}
}
