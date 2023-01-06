package gr.athtech.auction;

import lombok.Data;

@Data
public class AuctionItem {

	private String name;
	private String description;
	private Double startingPrice;
	private Boolean auctionTimeSetBySeller;
}
