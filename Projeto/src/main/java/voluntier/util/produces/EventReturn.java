package voluntier.util.produces;

public class EventReturn {
	
	public String username;
	public String comment;
	public String timestamp;
	public String comment_id;
	
	public EventReturn () {
	}
	
	public EventReturn (String username, String comment, String timestamp) {
		this.username = username;
		this.comment = comment;
		this.timestamp = timestamp;
	}
	
	public void setCommentID (String comment_id) {
		this.comment_id = comment_id;
	}

}
