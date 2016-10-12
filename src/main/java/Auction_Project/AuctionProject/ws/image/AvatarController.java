package Auction_Project.AuctionProject.ws.image;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import Auction_Project.AuctionProject.dao.UserDAO;
import Auction_Project.AuctionProject.dto.avatar.saveAvatarResponse;
import Auction_Project.AuctionProject.ws.user.User;

@RestController
@RequestMapping("/ws/avatar")
public class AvatarController {
	
	@Autowired
	private UserDAO userDAO;	
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean upload(@RequestBody saveAvatarResponse image) {
		
		try {
			String[] parts;
			String fileType, data;
			byte[] decoded;
			FileOutputStream fos;
			long user_id = image.getId();
			User user = userDAO.findById(user_id);
			
			if (!user.getAvatar().getImgPath().equals("./img/avatars/avatar0.png")) {	//changing avatar
				String oldPath = user.getAvatar().getImgPath().substring(1);
				new File("./src/main/resources/static" + oldPath).delete();
			}
			
			parts = image.getImgA().split(";base64,");
			fileType = parts[0];
			data = parts[1];
			fileType = fileType.replace("data:image/", "");
			decoded = Base64.getDecoder().decode(data);
			fos = new FileOutputStream("./src/main/resources/static/img/avatars/avatar" + user_id + "." + fileType);
			fos.write(decoded);
			fos.close();
			
			user.getAvatar().setImgPath("./img/avatars/avatar" + user_id + "." + fileType);
			userDAO.save(user);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
		}
		return true;
	}

}
