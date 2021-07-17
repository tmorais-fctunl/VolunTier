package voluntier.util.eventdata;

public class MessageData {
	
	public String email;
	public String username;
	public String comment;
	public String timestamp;
	public int comment_id;
	public String rating_id;
	
	public MessageData() {
	}
	
	public MessageData (String email, String username, String comment, String timestamp, int comment_id, String rating_id) {
		this.email = email;
		this.username = username;
		this.comment = comment;
		this.timestamp = timestamp;
		this.comment_id = comment_id;
		this.rating_id = rating_id;
	}
}
