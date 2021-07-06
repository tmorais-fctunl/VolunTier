package voluntier.util.eventdata;

public class CommentData {
	
	public String email;
	public String comment;
	public String timestamp;
	public String comment_id;
	
	public CommentData() {
	}
	
	public CommentData (String email, String comment, String timestamp, String comment_id) {
		this.email = email;
		this.comment = comment;
		this.timestamp = timestamp;
		this.comment_id = comment_id;
	}
}
