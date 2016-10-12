package Auction_Project.AuctionProject.ws.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import Auction_Project.AuctionProject.dao.MessageDAO;
import Auction_Project.AuctionProject.dao.UserDAO;
import Auction_Project.AuctionProject.dto.message.InboxResponse;
import Auction_Project.AuctionProject.dto.message.MessageListResponse;
import Auction_Project.AuctionProject.dto.message.SendMessageResponse;
import Auction_Project.AuctionProject.dto.message.SentResponse;
import Auction_Project.AuctionProject.dto.message.ViewMessageResponse;
import Auction_Project.AuctionProject.ws.user.User;

@RestController
@RequestMapping("/ws/message")
public class MessageController {

	@Autowired
	private MessageDAO messageDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@RequestMapping(value = "/send", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean sendMessage(@RequestBody SendMessageResponse input) {
		User sentUser = new User();
		User receiveUser = new User();		
		try {
			sentUser = userDAO.findById(input.getFrom());
			receiveUser = userDAO.findById(input.getTo());
			Message msg = new Message(input.getTitle(), input.getText(), input.getDate(), false, false, false, sentUser, receiveUser);
			messageDAO.save(msg);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/inbox/{user_id}", method = RequestMethod.GET)
	public List<InboxResponse> inbox(@PathVariable long user_id) {
		List<InboxResponse> responseList = new ArrayList<InboxResponse>();
		List<Message> messageList = new ArrayList<Message>();
		try {
			User user = userDAO.findById(user_id);
			messageList = messageDAO.findByReceiveUserAndInboxDeleteOrderByDateDesc(user, false);
			for (Iterator<Message> iterator = messageList.iterator(); iterator.hasNext();) {
				Message msg = iterator.next();
				InboxResponse response = new InboxResponse(msg.getId(), msg.getSentUser().getUsername(), msg.getTitle(), msg.getDate(), msg.getIsRead());
				responseList.add(response);
			}
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return responseList;
	}
	
	@RequestMapping(value = "/sent/{user_id}", method = RequestMethod.GET)
	public List<SentResponse> sent(@PathVariable long user_id) {
		List<SentResponse> responseList = new ArrayList<SentResponse>();
		List<Message> messageList = new ArrayList<Message>();
		try {
			User user = userDAO.findById(user_id);
			messageList = messageDAO.findBySentUserAndSentDeleteOrderByDateDesc(user, false);
			for (Iterator<Message> iterator = messageList.iterator(); iterator.hasNext();) {
				Message msg = iterator.next();
				SentResponse response = new SentResponse(msg.getId(), msg.getReceiveUser().getUsername(), msg.getTitle(), msg.getDate());
				responseList.add(response);
			}
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return responseList;
	}
	
	@RequestMapping(value = "inbox/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean inboxDelete(@RequestBody MessageListResponse messages) {
		Long[] ids = messages.getIds();
		try {
			for (int i = 0; i < ids.length; i++) {
				Message msg = messageDAO.findById(ids[i]);
				if (msg.getSentDelete() == true)
					messageDAO.delete(msg);
				else {
					msg.setInboxDelete(true);
					messageDAO.save(msg);
				}
			}
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "sent/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean SentDelete(@RequestBody MessageListResponse messages) {
		Long[] ids = messages.getIds();
		try {
			for (int i = 0; i < ids.length; i++) {
				Message msg = messageDAO.findById(ids[i]);
				if (msg.getInboxDelete() == true)
					messageDAO.delete(msg);
				else {
					msg.setSentDelete(true);
					messageDAO.save(msg);
				}
			}
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "markRead", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean markRead(@RequestBody MessageListResponse messages) {
		Long[] ids = messages.getIds();
		try {
			for (int i = 0; i < ids.length; i++) {
				Message msg = messageDAO.findById(ids[i]);
				msg.setIsRead(true);
				messageDAO.save(msg);
			}
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "markUnRead", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean markUnRead(@RequestBody MessageListResponse messages) {
		Long[] ids = messages.getIds();
		try {
			for (int i = 0; i < ids.length; i++) {
				Message msg = messageDAO.findById(ids[i]);
				msg.setIsRead(false);
				messageDAO.save(msg);
			}
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "inbox/count/{user_id}", method = RequestMethod.GET)
	public Long inboxCount(@PathVariable long user_id) {
		Long c = new Long(0);
		try {
			User user = userDAO.findById(user_id);
			c = messageDAO.countByReceiveUserAndInboxDelete(user, false);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return c;
	}
	
	@RequestMapping(value = "sent/count/{user_id}", method = RequestMethod.GET)
	public Long sentCount(@PathVariable long user_id) {
		Long c = new Long(0);
		try {
			User user = userDAO.findById(user_id);
			c = messageDAO.countBySentUserAndSentDelete(user, false);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return c;
	}
	
	@RequestMapping(value = "view/{msg_id}", method = RequestMethod.GET)
	public ViewMessageResponse viewMessage(@PathVariable long msg_id) {
		ViewMessageResponse responseMessage = new ViewMessageResponse();
		try {
			Message msg = messageDAO.findById(msg_id);
			responseMessage = new ViewMessageResponse(msg.getTitle(), msg.getReceiveUser().getUsername(), msg.getSentUser().getUsername(), msg.getText());
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return responseMessage;
	}
	
	@RequestMapping(value = "notify/{user_id}", method = RequestMethod.GET)
	public Long Notify(@PathVariable long user_id) {
		Long count = new Long(0);
		try {
			User user = userDAO.findById(user_id);
			count = messageDAO.countByReceiveUserAndInboxDeleteAndIsRead(user, false, false);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		System.out.println("notify " + count);
		return count;
	}
}
