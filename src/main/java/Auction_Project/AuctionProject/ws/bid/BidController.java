package Auction_Project.AuctionProject.ws.bid;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import Auction_Project.AuctionProject.dao.AuctionDAO;
import Auction_Project.AuctionProject.dao.BidDAO;
import Auction_Project.AuctionProject.dao.UserDAO;
import Auction_Project.AuctionProject.dto.bid.BidResponse;
import Auction_Project.AuctionProject.dto.bid.NewBidResponse;
import Auction_Project.AuctionProject.ws.auction.Auction;
import Auction_Project.AuctionProject.ws.user.User;

@RestController
@RequestMapping("/ws/bid")
public class BidController {
	
	@Autowired
	private BidDAO bidDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private AuctionDAO auctionDAO;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean addBid(@RequestBody NewBidResponse input_bid) {
		Bid bid = new Bid();
		bid.setAmount(input_bid.getAmount());
		bid.setBid_time(new Date());
		try {
			User user = userDAO.findById(input_bid.getBidder());
			Auction auction = auctionDAO.findById(input_bid.getAuction());
			auction.setCurrently(bid.getAmount());
			auctionDAO.save(auction);
			bid.setBidder(user);
			bid.setAuctionId(auction);
			bidDAO.save(bid);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/forAuction/{auctionID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public BidResponse getBids(@PathVariable long auctionID) {
		BidResponse responseBid = new BidResponse();
		try {
			Auction auction = auctionDAO.findById(auctionID);
			Bid bid = bidDAO.findTopBidForAuction(auction);
			responseBid.setAmount(bid.getAmount());
			responseBid.setBid_id(bid.getId());
			responseBid.setBid_time(bid.getBid_time());
			responseBid.setBidder_id(bid.getBidder().getId());
			responseBid.setBidder_username(bid.getBidder().getUsername());
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return responseBid;
	}

}
