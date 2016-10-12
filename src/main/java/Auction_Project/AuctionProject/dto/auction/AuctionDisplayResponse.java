package Auction_Project.AuctionProject.dto.auction;

import java.util.Date;
import java.util.List;

import Auction_Project.AuctionProject.dto.category.CategoryResponse;

public class AuctionDisplayResponse {
	
	private long id;
	
	private String name, description;
	
	private String latitude, longitude, location, country;
	
	private float currently, first_bid;
	
	private float buy_price;
	
	private Date started, ends;
	
	private String creator; //for display
	private long user_id; //for display
	
	private List<CategoryResponse> categories;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getCurrently() {
		return currently;
	}
	public void setCurrently(float currently) {
		this.currently = currently;
	}
	public float getFirst_bid() {
		return first_bid;
	}
	public void setFirst_bid(float first_bid) {
		this.first_bid = first_bid;
	}
	public float getBuy_price() {
		return buy_price;
	}
	public void setBuy_price(float buy_price) {
		this.buy_price = buy_price;
	}
	public Date getStarted() {
		return started;
	}
	public void setStarted(Date started) {
		this.started = started;
	}
	public Date getEnds() {
		return ends;
	}
	public void setEnds(Date ends) {
		this.ends = ends;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	
	public List<CategoryResponse> getCategories() {
		return categories;
	}
	
	public void setCategories(List<CategoryResponse> categories) {
		this.categories = categories;
	}
	
	public AuctionDisplayResponse() {
		
	}
	
	public AuctionDisplayResponse(long id) {
		this.id = id;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	
}
