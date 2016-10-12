package Auction_Project.AuctionProject.dto.message;

import java.util.Date;

public class InboxResponse {
	
	private long id;
	private String from, title;
	private Boolean isRead;
	private Date date;
	
	public InboxResponse() {
	}
	
	public InboxResponse(long id, String from, String title, Date date, Boolean isRead) {
		this.id = id;
		this.from = from;
		this.title = title;
		this.date = date;
		this.isRead = isRead;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	
	
}
