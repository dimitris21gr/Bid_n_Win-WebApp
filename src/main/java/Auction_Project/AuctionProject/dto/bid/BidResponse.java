package Auction_Project.AuctionProject.dto.bid;

import java.util.Date;

public class BidResponse {
	
	private long bidder_id, bid_id;
	
	private String bidder_username;
	
	private Date bid_time;
	
	private float amount;

	public long getBidder_id() {
		return bidder_id;
	}

	public void setBidder_id(long bidder_id) {
		this.bidder_id = bidder_id;
	}

	public String getBidder_username() {
		return bidder_username;
	}

	public void setBidder_username(String bidder_username) {
		this.bidder_username = bidder_username;
	}

	public Date getBid_time() {
		return bid_time;
	}

	public void setBid_time(Date bid_time) {
		this.bid_time = bid_time;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public long getBid_id() {
		return bid_id;
	}

	public void setBid_id(long bid_id) {
		this.bid_id = bid_id;
	}

}

