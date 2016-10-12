package Auction_Project.AuctionProject.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import Auction_Project.AuctionProject.ws.auction.Auction;
import Auction_Project.AuctionProject.ws.category.Category;

@Transactional
public interface CategoryDAO extends CrudRepository<Category, Long>{

	public Long countByNameAndParent(String name, Category parent);
	public Category findById(long id);
	public List<Category>findByParent(Category parent);
	public Category findByNameAndParent(String name, Category parent);
	
	@Query("SELECT a FROM Category c INNER JOIN c.auctions a WHERE c.id = ?1")
	public List<Auction> findAuctions(long id, Pageable page);
	
	@Query("SELECT a FROM Category c INNER JOIN c.auctions a WHERE c.id = ?1")
	public List<Auction> findAuctionsNoPage(long id);
	
}
