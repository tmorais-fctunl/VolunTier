package voluntier.util.eventdata;

public class MessageData {
	
	public String email;
	public String comment;
	public String timestamp;
	public int message_id;
	public int likes;
	
	public MessageData() {
	}
	
	public MessageData (String email, String comment, String timestamp, int comment_id, int likes) {
		this.email = email;
		this.comment = comment;
		this.timestamp = timestamp;
		this.message_id = comment_id;
		this.likes = likes;
	}
}
