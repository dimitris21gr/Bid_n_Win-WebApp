package Auction_Project.AuctionProject.ws.auction;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import Auction_Project.AuctionProject.dao.AuctionDAO;
import Auction_Project.AuctionProject.dao.BidDAO;
import Auction_Project.AuctionProject.dao.CategoryDAO;
import Auction_Project.AuctionProject.dao.UserDAO;
import Auction_Project.AuctionProject.dto.auction.AuctionDisplayResponse;
import Auction_Project.AuctionProject.dto.auction.AuctionSaveResponse;
import Auction_Project.AuctionProject.dto.auction.UserAuctionsListResponse;
import Auction_Project.AuctionProject.dto.bid.BidResponse;
import Auction_Project.AuctionProject.dto.category.CategoryResponse;
import Auction_Project.AuctionProject.ws.bid.Bid;
import Auction_Project.AuctionProject.ws.category.Category;
import Auction_Project.AuctionProject.ws.user.User;


@RestController
@RequestMapping("/ws/auction")
public class AuctionController {
	
	@Autowired
	private AuctionDAO auctionDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private CategoryDAO categoryDAO;

	@Autowired
	private BidDAO bidDAO;
	
	@RequestMapping(value = "/begin/{auctionID}", method = RequestMethod.GET)
	public Date begin(@PathVariable long auctionID) {
		Date date = new Date();
		try {
			Auction auction = auctionDAO.findById(auctionID);
			auction.setStarted(date);
			auctionDAO.save(auction);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return date;
	}

	@RequestMapping(value = "/{auctionID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public AuctionDisplayResponse getByID(@PathVariable long auctionID) {
		AuctionDisplayResponse auctionResponse = new AuctionDisplayResponse(0);
		try {
			Auction auction = auctionDAO.findById(auctionID);
			User user = auction.getUser_seller_id();
			auctionResponse.setId(auction.getId());
			auctionResponse.setName(auction.getName());
			auctionResponse.setDescription(auction.getDescription());
			auctionResponse.setCurrently(auction.getCurrently());
			auctionResponse.setFirst_bid(auction.getFirst_bid());
			auctionResponse.setBuy_price(auction.getBuy_price());
			auctionResponse.setStarted(auction.getStarted());
			auctionResponse.setEnds(auction.getEnds());
			auctionResponse.setCreator(user.getUsername());
			auctionResponse.setUser_id(user.getId());
			auctionResponse.setCountry(auction.getCountry());
			auctionResponse.setLocation(auction.getLocation());
			auctionResponse.setLongitude(auction.getLongitude());
			auctionResponse.setLatitude(auction.getLatitude());
			
			List<Category> cat = auction.getCategories();
			List<CategoryResponse> reCat = new ArrayList<CategoryResponse>();
			for (int i = 0; i < cat.size(); i++) {
				CategoryResponse newCat = new CategoryResponse();
				newCat.setId(cat.get(i).getId());
				newCat.setName(cat.get(i).getName());
				reCat.add(newCat);
			}
			auctionResponse.setCategories(reCat);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return auctionResponse;
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public long createAuction(@RequestBody AuctionSaveResponse new_auction) {	
		Auction auction = new Auction();
		Auction returned = new Auction();
		try {
			auction.setName(new_auction.getName());
			auction.setDescription(new_auction.getDescription());
			auction.setEnds(new_auction.getEnds());
			auction.setCurrently(new_auction.getFirst_bid());
			auction.setFirst_bid(new_auction.getFirst_bid());
			auction.setBuy_price(new_auction.getBuy_price());
			auction.setCreated(new Date());
			auction.setLocation(new_auction.getLocation());
			auction.setLatitude(new_auction.getLatitude());
			auction.setLongitude(new_auction.getLongitude());
			auction.setCountry(new_auction.getCountry());
			User user = userDAO.findById(new_auction.getUser_id());
			auction.setUser_seller_id(user);
			
			List<Category> catList = new ArrayList<Category>();
			for (int i = 0; i < new_auction.getCategoryList().size(); i++) {
				Category cat = categoryDAO.findById(new_auction.getCategoryList().get(i).getId());
				catList.add(cat);
			}
			auction.setCategories(catList);
			returned = auctionDAO.save(auction);
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			return -1;
		}
		return returned.getId();
	}
	
	@RequestMapping(value = "/getUserAuctions/{user_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserAuctionsListResponse> getUserAuctions(@PathVariable long user_id) {
		List<Auction> auctionList = new ArrayList<Auction>();
		List<UserAuctionsListResponse> auctionResponseList = new ArrayList<UserAuctionsListResponse>();
		User seller = userDAO.findById(user_id);
		try {
			auctionList = auctionDAO.findBySellerOrderByCreatedDesc(seller);
			for (Iterator<Auction> iterator = auctionList.iterator(); iterator.hasNext();) {
				Auction auction = iterator.next();
				
				boolean status = false;
				if (auction.getStarted() != null && auction.getEnds().after(new Date()) && auction.getCurrently() != auction.getBuy_price()) {
					status = true;
				}

				boolean allowChanges = true;
				if (bidDAO.countByAuctionId(auction) == 0)
					allowChanges = false;
				
				UserAuctionsListResponse auctionResponse = new UserAuctionsListResponse(auction.getId(), auction.getName(), status, auction.getCurrently(), allowChanges);
				auctionResponseList.add(auctionResponse);
			}
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return auctionResponseList;
	}
	
	@RequestMapping(value = "/history/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<BidResponse> historyAuction(@PathVariable long id) {
		List<BidResponse> returnList = new ArrayList<BidResponse>();
		try {
			List<Bid> bidList = bidDAO.findByAuctionIdOrderByAmountDesc(auctionDAO.findById(id));
			for (int i = 0; i < bidList.size(); i++) {
				Bid bid = bidList.get(i);
				BidResponse resBid = new BidResponse();
				resBid.setBid_id(bid.getId());
				resBid.setAmount(bid.getAmount());
				resBid.setBid_time(bid.getBid_time());
				resBid.setBidder_id(bid.getBidder().getId());
				resBid.setBidder_username(bid.getBidder().getUsername());
				returnList.add(resBid);
			}
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return returnList;
	}
	
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean editAuction(@RequestBody AuctionSaveResponse new_auction) {
		try {
			Auction auction = auctionDAO.findById(new_auction.getId());
			auction.setName(new_auction.getName());
			auction.setDescription(new_auction.getDescription());
			auction.setEnds(new_auction.getEnds());
			auction.setCurrently(new_auction.getFirst_bid());
			auction.setFirst_bid(new_auction.getFirst_bid());
			auction.setBuy_price(new_auction.getBuy_price());
			auction.setLocation(new_auction.getLocation());
			auction.setLatitude(new_auction.getLatitude());
			auction.setLongitude(new_auction.getLongitude());
			auction.setCountry(new_auction.getCountry());
			
			List<Category> catList = auction.getCategories();
			catList.clear();
			for (int i = 0; i < new_auction.getCategoryList().size(); i++) {
				Category cat = categoryDAO.findById(new_auction.getCategoryList().get(i).getId());
				catList.add(cat);
			}
			auction.setCategories(catList);
			
			auctionDAO.save(auction);
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/delete/{auctionID}", method = RequestMethod.GET)
	public boolean deleteAuction(@PathVariable long auctionID) {
		try {
			Auction auction = auctionDAO.findById(auctionID);
			
			List<Category> catList = auction.getCategories(); //categories
			catList.clear();
			
			auctionDAO.delete(auction);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
}
