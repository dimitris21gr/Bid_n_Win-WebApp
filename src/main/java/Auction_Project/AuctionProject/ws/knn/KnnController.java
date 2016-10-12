package Auction_Project.AuctionProject.ws.knn;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import Auction_Project.AuctionProject.dao.UserDAO;
import Auction_Project.AuctionProject.ws.auction.Auction;
import Auction_Project.AuctionProject.ws.category.Category;
import Auction_Project.AuctionProject.ws.image.AuctionImage;
import Auction_Project.AuctionProject.ws.user.User;
import Auction_Project.AuctionProject.dao.AuctionDAO;
import Auction_Project.AuctionProject.dao.ImageDAO;
import Auction_Project.AuctionProject.dto.auction.AuctionSearchResponse;
import Auction_Project.AuctionProject.dto.category.CategoryResponse;

@EnableScheduling
@RestController
@RequestMapping("/ws/knn")
public class KnnController {
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private AuctionDAO auctionDAO;
	
	@Autowired
	private ImageDAO imageDAO;
	
	private Map<BigInteger, List<BigInteger>> userArray; //<user, <List of auctions user had bidden>>

	@Scheduled(fixedRate = 3600000) //update every hour
	public void updateArray() {
		
		List<User> userList = userDAO.findAll();
		Map<BigInteger, List<BigInteger>> newUserArray = new HashMap<BigInteger, List<BigInteger>>();
		
		Iterator<User> it = userList.iterator();
		while (it.hasNext())
		{
			BigInteger user_id = BigInteger.valueOf(it.next().getId());
			List<BigInteger> auctionList = auctionDAO.userAuctions(user_id); //get the auctions this user_id user had bidden
			newUserArray.put(user_id, auctionList);//store <user_id(long), auctionList(list of long)>
		}
		setUserArray(newUserArray);
		System.out.println("array completed");
	}
	
	@RequestMapping(value = "/getSuggestions/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AuctionSearchResponse> getSuggestions(@PathVariable long id) {
		
		List<AuctionSearchResponse> returnList = new ArrayList<AuctionSearchResponse>();
		BigInteger[] recommendedItems =  {BigInteger.valueOf(10),BigInteger.valueOf(11),BigInteger.valueOf(12), BigInteger.valueOf(13), BigInteger.valueOf(14)};
		BigInteger[] neighbor =  new BigInteger[20];
		Double[] neighborCos =  new Double[20];
		Arrays.fill(neighbor, BigInteger.ZERO);
		Arrays.fill(neighborCos, 0.0);
		
		BigInteger userA = BigInteger.valueOf(id);  
		if (getUserArray().get(userA).size() == 0) //if the user hasn't bids to any auctions find 5 random to recommend
		{
			Integer totalAuctions = auctionDAO.countAuctions();
			for (int x = 0; x < 5; x++) {
				Random rand = new Random();
				int  n = rand.nextInt(totalAuctions) + 1;
				recommendedItems[x] = BigInteger.valueOf(n);
			}
			
			List<Auction> auctionList = auctionDAO.suggestions(recommendedItems[0], recommendedItems[1], recommendedItems[2], recommendedItems[3], recommendedItems[4]);
			for (int i = 0; i < 5; i++) {
				Auction tempAuction = auctionList.get(i);
				AuctionSearchResponse temp2Auction = new AuctionSearchResponse();
				temp2Auction.setId(tempAuction.getId());
				temp2Auction.setName(tempAuction.getName());
				temp2Auction.setLocation(tempAuction.getLocation());
				temp2Auction.setCurrently(tempAuction.getCurrently());
				temp2Auction.setBuy_price(tempAuction.getBuy_price());
				temp2Auction.setSeller_id(tempAuction.getUser_seller_id().getId());
				temp2Auction.setRating(tempAuction.getUser_seller_id().getSellerRating());
				temp2Auction.setSeller_username(tempAuction.getUser_seller_id().getUsername());
				temp2Auction.setStatus(false);
				
				List<Category> catList = tempAuction.getCategories();
				List<CategoryResponse> catRes = new ArrayList<CategoryResponse>();
				for (int j = 0; j < catList.size(); j++) {
					CategoryResponse cat = new CategoryResponse();
					cat.setId(catList.get(j).getId());
					cat.setName(catList.get(j).getName());
					catRes.add(cat);
				}
				temp2Auction.setCategories(catRes);
				
				List<AuctionImage> imgs = imageDAO.findByAuctionId(tempAuction);
				if (imgs.get(0).getImgPath().equals(""))
					temp2Auction.setImg(imgs.get(1).getImgPath());
				else
					temp2Auction.setImg(imgs.get(0).getImgPath());
				
				returnList.add(temp2Auction);
			}
			return returnList;
		}
		
		try { //calculate top 20 neighbors of user
			int position = 0;
			for(BigInteger i: getUserArray().keySet()) { //for every user in the dataset
				List<BigInteger> userBAuctions = new LinkedList<BigInteger>();
				
				for (int j = 0; j < getUserArray().get(i).size(); j++) 
					userBAuctions.add(getUserArray().get(i).get(j)); //get the auctions the possible neighbor had bidden
				
				int c_userA = getUserArray().get(userA).size(); //amount of auctions loggedin user ad bidden
				int c_userB = userBAuctions.size(); //amount of auctions possible neighbor had bidden
				List<BigInteger> commonAuctionList = userBAuctions;
				commonAuctionList.retainAll(getUserArray().get(userA)); //get the common auctions of the two users

				double cos_similarity = ((commonAuctionList.size()) / (Math.pow((c_userA * c_userB), 0.5)));
				
				if ((position == 20) && (commonAuctionList.size() != 0) && (neighborCos[19] < cos_similarity)) { //update the top 20 neighbors
					neighborCos[19] = cos_similarity;
					neighbor[19] = i;
					
					int cur_pos = 19;
					for (int pos_i = 18; pos_i >= 0; pos_i--) { //sort list with cosine similarity
						if (neighborCos[pos_i] < neighborCos[cur_pos]) {
							double temp = neighborCos[pos_i];
							BigInteger btemp = neighbor[pos_i];
							neighborCos[pos_i] = neighborCos[cur_pos];
							neighbor[pos_i] = neighbor[cur_pos];
							neighborCos[cur_pos] = temp;
							neighbor[cur_pos] = btemp;
							cur_pos = pos_i;
						}
					}
				}
				else if ((commonAuctionList.size() != 0) && (position != 20)) { //if the neighbors array is not full insert the new neighbor
					neighborCos[position] = cos_similarity;
					neighbor[position] = i;
					position++;
					if (position == 20) {
						for (int pos_i = 0; pos_i < 20; pos_i++) { //sort list with cosine similarity
							for (int pos_j = 0; pos_j < 20; pos_j++) {
								if (neighborCos[pos_i] > neighborCos[pos_j]) {
									double temp = neighborCos[pos_i];
									BigInteger btemp = neighbor[pos_i];
									neighborCos[pos_i] = neighborCos[pos_j];
									neighbor[pos_i] = neighbor[pos_j];
									neighborCos[pos_j] = temp;
									neighbor[pos_j] = btemp;
								}
							}
						}
					}
				}
			}
			/*
			 * find top 5 items to suggest
			 * Heuristic for choosing the top 5 recommended items:
			 *  > Get all the items each neighbor had bidden
			 *  > Sort the items by the amount of neighbors that had bid on them
			 */
			
			Map<BigInteger, List<BigInteger>> itemHaveBidders = new HashMap<BigInteger, List<BigInteger>>();
			for (BigInteger nearNeighbor: neighbor)  { //get all the items of the neighbors
				if (nearNeighbor == BigInteger.ZERO)
					break;
				for (BigInteger item: userArray.get(nearNeighbor)) {
					if (!itemHaveBidders.containsKey(item)) {
						List<BigInteger> curNeighborList = new LinkedList<BigInteger>();
						curNeighborList.add(nearNeighbor);
						itemHaveBidders.put(item, curNeighborList);
					}
					else
					{
						List<BigInteger> curNeighborList = new LinkedList<BigInteger>();
						curNeighborList.addAll(itemHaveBidders.get(item)); //get the previous neighbors for this item
						curNeighborList.add(nearNeighbor);
						itemHaveBidders.put(item, curNeighborList);
					}
				}
			}
			
			for (BigInteger userAitem : getUserArray().get(userA)) { //remove all the userA items
				if (itemHaveBidders.containsKey(userAitem))
					itemHaveBidders.remove(userAitem);
			}

			int total = itemHaveBidders.keySet().size();
			if (total < 5)
				total = 5;
			BigInteger[] sortedItems =  new BigInteger[total];
			Integer[] popularItems = new Integer[total];
			Arrays.fill(sortedItems, BigInteger.ZERO);
			Arrays.fill(popularItems, 0);
			int posA = 0;
			for (BigInteger key : itemHaveBidders.keySet()) {
				int size = itemHaveBidders.get(key).size();
				popularItems[posA] = size;
				sortedItems[posA]= key;
				posA++;
			}
			
			//sort items according to the amount of nearest neighbors that have bids on the item
			if (sortedItems[0] != BigInteger.ZERO)  //in case the only common item with the neighbors is already recommended
			{
				for (int pos_i = 0; pos_i < sortedItems.length; pos_i++) { //sort list 
					for (int pos_j = 0; pos_j < sortedItems.length; pos_j++) {
						if (popularItems[pos_i] > popularItems[pos_j]) {
							int temp = popularItems[pos_i];
							BigInteger btemp = sortedItems[pos_i];
							popularItems[pos_i] = popularItems[pos_j];
							sortedItems[pos_i] = sortedItems[pos_j];
							popularItems[pos_j] = temp;
							sortedItems[pos_j] = btemp;
						}
					}
				}
			}
			
			for (int x = 0; x < 5; x++)
			{
				if (sortedItems[x] != BigInteger.ZERO)
					recommendedItems[x] = sortedItems[x];
				else
				{
					Integer totalAuctions = auctionDAO.countAuctions();
					while (true) {
						Random rand = new Random();
						int  n = rand.nextInt(totalAuctions) + 1;
						if (!Arrays.asList(sortedItems).contains(BigInteger.valueOf(n)) && !userArray.get(userA).contains(BigInteger.valueOf(n)))
						{
							recommendedItems[x] = BigInteger.valueOf(n);
							break;
						}
					}
				}
			}
			
			List<Auction> auctionList = auctionDAO.suggestions(recommendedItems[0], recommendedItems[1], recommendedItems[2], recommendedItems[3], recommendedItems[4]);
			for (int i = 0; i < 5; i++) {
				Auction tempAuction = auctionList.get(i);
				AuctionSearchResponse temp2Auction = new AuctionSearchResponse();
				temp2Auction.setId(tempAuction.getId());
				temp2Auction.setName(tempAuction.getName());
				temp2Auction.setLocation(tempAuction.getLocation());
				temp2Auction.setCurrently(tempAuction.getCurrently());
				temp2Auction.setBuy_price(tempAuction.getBuy_price());
				temp2Auction.setSeller_id(tempAuction.getUser_seller_id().getId());
				temp2Auction.setRating(tempAuction.getUser_seller_id().getSellerRating());
				temp2Auction.setSeller_username(tempAuction.getUser_seller_id().getUsername());
				temp2Auction.setStatus(false);
				
				List<Category> catList = tempAuction.getCategories();
				List<CategoryResponse> catRes = new ArrayList<CategoryResponse>();
				for (int j = 0; j < catList.size(); j++) {
					CategoryResponse cat = new CategoryResponse();
					cat.setId(catList.get(j).getId());
					cat.setName(catList.get(j).getName());
					catRes.add(cat);
				}
				temp2Auction.setCategories(catRes);
				
				List<AuctionImage> imgs = imageDAO.findByAuctionId(tempAuction);
				if (imgs.get(0).getImgPath().equals(""))
					temp2Auction.setImg(imgs.get(1).getImgPath());
				else
					temp2Auction.setImg(imgs.get(0).getImgPath());
				
				returnList.add(temp2Auction);
			}
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return returnList;
	}

	
	
	public Map<BigInteger, List<BigInteger>> getUserArray() {
		return userArray;
	}

	public void setUserArray(Map<BigInteger, List<BigInteger>> userArray) {
		this.userArray = userArray;
	}
}
