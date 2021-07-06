package voluntier.util.eventdata;

import com.google.cloud.Timestamp;

public class CommentData {
	
	public String username;
	public String comment;
	public Timestamp timestamp;
	public String comment_id;
	
	public CommentData() {
	}
	
	public CommentData (String username, String comment, Timestamp timestamp, String comment_id) {
		this.username = username;
		this.comment = comment;
		this.timestamp = timestamp;
		this.comment_id = comment_id;
	}
}
