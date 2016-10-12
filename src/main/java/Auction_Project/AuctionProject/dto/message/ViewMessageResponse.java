package Auction_Project.AuctionProject.dto.message;

public class ViewMessageResponse {

	private String title, to, from, text;
	
	public ViewMessageResponse() {
	
	}

	public ViewMessageResponse(String title, String to, String from, String text) {
		this.title = title;
		this.to = to;
		this.from = from;
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
