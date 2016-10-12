package Auction_Project.AuctionProject.dao;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import Auction_Project.AuctionProject.ws.auction.Auction;
import Auction_Project.AuctionProject.ws.bid.Bid;

@Transactional
public interface BidDAO extends CrudRepository<Bid, Long>{
	
	@Query("SELECT b FROM Bid b WHERE b.auctionId = ?1 AND b.amount = (SELECT MAX(b.amount) FROM Bid b WHERE b.auctionId = ?1 ORDER BY b.amount)")
	public Bid findTopBidForAuction(Auction auction_id);
	public Long countByAuctionId(Auction auction_id);
	public List<Bid> findByAuctionIdOrderByAmountAsc(Auction auction_id);
	public List<Bid> findByAuctionIdOrderByAmountDesc(Auction auction_id);
}
