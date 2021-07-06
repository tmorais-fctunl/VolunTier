package voluntier.util.eventdata;

import com.google.cloud.Timestamp;

public class CommentData {
	
	public String email;
	public String comment;
	public Timestamp timestamp;
	public String comment_id;
	
	public CommentData() {
	}
	
	public CommentData (String email, String comment, Timestamp timestamp, String comment_id) {
		this.email = email;
		this.comment = comment;
		this.timestamp = timestamp;
		this.comment_id = comment_id;
	}
}
