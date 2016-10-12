package Auction_Project.AuctionProject.dao;


import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import Auction_Project.AuctionProject.ws.auction.Auction;
import Auction_Project.AuctionProject.ws.user.User;

@Transactional
public interface AuctionDAO extends CrudRepository<Auction, Long>{
	
	public Auction findById(long id);
	
	@Query(value = "SELECT *, MATCH(a.name) AGAINST(?3 IN BOOLEAN MODE) AS rev1, MATCH(a.location) AGAINST (?5 IN BOOLEAN MODE) AS rev2 " 
			+ "FROM Auction a INNER JOIN auction_category c "
			+ "WHERE a.id = c.auction_id AND c.category_id = ?4 AND (a.currently BETWEEN ?1 AND ?2 OR a.buy_price BETWEEN ?1 AND ?2) "
			+ "AND (?3 IS NULL OR  MATCH(a.name) AGAINST(?3 IN BOOLEAN MODE)) "
			+ "AND (?5 IS NULL OR MATCH(a.location) AGAINST (?5 IN BOOLEAN MODE)) "
			+ "ORDER BY rev1+rev2 DESC "
			+ "LIMIT ?6 OFFSET ?7",
	nativeQuery = true)
	public List<Auction> advancedSearchAuctions(float min, float max, String keywords, long category, String location, long page_size, long offset);
	
	@Query(value = "SELECT COUNT(*) FROM Auction a INNER JOIN auction_category c WHERE a.id = c.auction_id AND c.category_id = ?4 AND (a.currently BETWEEN ?1 AND ?2 OR a.buy_price BETWEEN ?1 AND ?2) "
			+ "AND (MATCH(a.name) AGAINST(?3 IN BOOLEAN MODE) OR ?3 IS NULL) "
			+ "AND (MATCH(a.location) AGAINST (?5 IN BOOLEAN MODE) OR ?5 IS NULL)",
	nativeQuery = true)
	public long countAdvancedSearchAuctions(float min, float max, String keywords, long category, String location);
	
	@Query(value = "SELECT DISTINCT (b.auction_id)"
			+ "FROM Bid b "
			+ "WHERE b.bidder = ?1", nativeQuery = true)
	public List<BigInteger> userAuctions(BigInteger user_id);
	
	public List<Auction> findBySellerOrderByCreatedDesc(User user_seller_id);
	public Long countByIdAndStarted(long id, Date started);
	public List<Auction> findAll();
	
	@Query(value = "SELECT COUNT(*)"
			+ "FROM AUCTION a", nativeQuery = true)
	public Integer countAuctions();
	
	
	@Query(value = "SELECT * "
			+ "FROM AUCTION a "
			+ "WHERE a.id = ?1 OR a.id = ?2 OR a.id = ?3 OR a.id = ?4 OR a.id = ?5", nativeQuery = true)
	public List<Auction> suggestions(BigInteger id1, BigInteger id2, BigInteger id3, BigInteger id4, BigInteger id5);
}
