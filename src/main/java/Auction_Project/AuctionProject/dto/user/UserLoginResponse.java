package Auction_Project.AuctionProject.dto.user;

public class UserLoginResponse {
	
	private long id;
	private String username;
	private boolean activation, superuser;
	
	
	public UserLoginResponse(long id, String username, boolean activation, boolean superuser) {
		this.id = id;
		this.username = username;
		this.activation = activation;
		this.superuser = superuser;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isActivation() {
		return activation;
	}

	public void setActivation(boolean activation) {
		this.activation = activation;
	}

	public boolean isSuperuser() {
		return superuser;
	}

	public void setSuperuser(boolean superuser) {
		this.superuser = superuser;
	}
	
	
}
