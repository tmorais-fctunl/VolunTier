package voluntier.util.eventdata;

public class MessageData {
	
	public String email;
	public String comment;
	public String timestamp;
	public int comment_id;
	public int likes;
	
	public MessageData() {
	}
	
	public MessageData (String email, String comment, String timestamp, int comment_id, int likes) {
		this.email = email;
		this.comment = comment;
		this.timestamp = timestamp;
		this.comment_id = comment_id;
		this.likes = likes;
	}
}
