package gr.athtech.auction;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Bid {

	private AuctionItem bidItem;
	private LocalDateTime timeOfBid;
	private Double highestBid;

	public Bid(final AuctionItem item, final Double highestBid) {
		this.bidItem = item;
		this.timeOfBid = LocalDateTime.now();
		this.highestBid = highestBid;
	}
}
