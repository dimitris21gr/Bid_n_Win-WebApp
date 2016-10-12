package Auction_Project.AuctionProject.dto.auction;

public class UserAuctionsListResponse {
	
	private String name;
	private boolean status, allowChanges;
	private float currently;
	private long id;
	
	public UserAuctionsListResponse(long id, String name, boolean status, float currently, boolean allowChanges) {
		this.name = name;
		this.status = status;
		this.currently = currently;
		this.id = id;
		this.allowChanges = allowChanges;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public float getCurrently() {
		return currently;
	}

	public void setCurrently(float currently) {
		this.currently = currently;
	}

	public boolean isAllowChanges() {
		return allowChanges;
	}

	public void setAllowChanges(boolean allowChanges) {
		this.allowChanges = allowChanges;
	}
	
}
