package Auction_Project.AuctionProject.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import Auction_Project.AuctionProject.ws.message.Message;
import Auction_Project.AuctionProject.ws.user.User;

@Transactional
public interface MessageDAO extends CrudRepository<Message, Long>{

	public List<Message> findByReceiveUserAndInboxDeleteOrderByDateDesc(User receiveUser, boolean inboxDelete);
	public Long countByReceiveUserAndInboxDelete(User receiveUser, boolean inboxDelete);
	
	public List<Message> findBySentUserAndSentDeleteOrderByDateDesc(User sentUser, boolean sentDelete);
	public Long countBySentUserAndSentDelete(User sentUser, boolean sentDelete);
	
	public Long countByReceiveUserAndInboxDeleteAndIsRead(User receiveUser, boolean inboxDelete, boolean isRead);
	
	public Message findById(long id);
}
