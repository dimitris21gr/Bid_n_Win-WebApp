package Auction_Project.AuctionProject.dto.user;

public class UserProfileResponse {

	private String username, email, name, surname, address, country, telephone, trn, location, avatar;
	private Boolean superuser, activation;
	private Integer bidderRating, sellerRating;

	
	public UserProfileResponse(String username, String email, String name, String surname, String address, String country, 
			String telephone, String trn, Boolean superuser, Boolean activation, String location, Integer bidderRating, Integer sellerRating, String avatar) {
		this.username = username;
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.address = address;
		this.country = country;
		this.telephone = telephone;
		this.trn = trn;
		this.superuser = superuser;
		this.activation = activation;
		this.location = location;
		this.bidderRating = bidderRating;
		this.sellerRating = sellerRating;
		this.avatar = avatar;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getTrn() {
		return trn;
	}
	public void setTrn(String trn) {
		this.trn = trn;
	}
	public Boolean getSuperuser() {
		return superuser;
	}
	public void setSuperuser(Boolean superuser) {
		this.superuser = superuser;
	}
	public Boolean getActivation() {
		return activation;
	}
	public void setActivation(Boolean activation) {
		this.activation = activation;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getBidderRating() {
		return bidderRating;
	}

	public void setBidderRating(Integer bidderRating) {
		this.bidderRating = bidderRating;
	}

	public Integer getSellerRating() {
		return sellerRating;
	}

	public void setSellerRating(Integer sellerRating) {
		this.sellerRating = sellerRating;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
}
