package Auction_Project.AuctionProject.dto.bid;

public class NewBidResponse {
	
	private float amount;
	
	private long bidder;
	private long auction;
	
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public long getBidder() {
		return bidder;
	}
	public void setBidder(long bidder) {
		this.bidder = bidder;
	}
	public long getAuction() {
		return auction;
	}
	public void setAuction(long auction) {
		this.auction = auction;
	}
	public NewBidResponse() {
	}

}
