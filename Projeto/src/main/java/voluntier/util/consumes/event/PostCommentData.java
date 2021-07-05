package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;

public class PostCommentData extends RequestData {
	
	private static final int DEFALUT_COMMENT_SIZE = 500;

	public String username;
	public String comment;
	public String event_name;
	public String timestamp;
	
	public PostCommentData() {
	}
	
	public PostCommentData (String email, String token, String username, String comment, String event_name, String timestamp) {
		super(email, token);
		this.username = username;
		this.comment = comment;
		this.event_name = event_name;
		this.timestamp = timestamp;
	}
	
	public boolean isValid () {
		return super.isValid() && username != null && comment != null && Integer.parseInt(comment) < DEFALUT_COMMENT_SIZE;
	}
}
