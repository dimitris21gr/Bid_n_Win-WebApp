package Auction_Project.AuctionProject.dao;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

import Auction_Project.AuctionProject.ws.auction.Auction;
import Auction_Project.AuctionProject.ws.image.AuctionImage;

@Transactional
public interface ImageDAO extends CrudRepository<AuctionImage, Long>{
	
	public AuctionImage findById(long id);
	public List<AuctionImage> findByAuctionId(Auction auction);
}
