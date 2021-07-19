package voluntier.util.eventdata;

import voluntier.exceptions.InexistentRatingException;
import voluntier.util.rating.DB_Rating;

public class MessageDataReturn {
	
	public String email;
	public String username;
	public String comment;
	public String timestamp;
	public int comment_id;
	public int likes;
	public boolean like_status;
	
	public MessageDataReturn() {
	}
	
	public MessageDataReturn (MessageData data, String user_email) {
		try { 
			this.likes = (int) DB_Rating.getSumRating(data.rating_id);
			this.like_status = DB_Rating.hasRated(data.rating_id, user_email);
		} catch (InexistentRatingException e) {	}
		
		this.email = data.email;
		this.username = data.username;
		this.comment = data.comment;
		this.timestamp = data.timestamp;
		this.comment_id = data.comment_id;
		
		
	}
}
